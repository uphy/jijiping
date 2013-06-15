/**
 * Copyright (C) 2013 uphy.jp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.uphy.jijiping.server;

import jp.uphy.jijiping.common.Answers;
import jp.uphy.jijiping.common.IdAlreadyUsedException;
import jp.uphy.jijiping.common.JijipingClient;
import jp.uphy.jijiping.common.JijipingClient.Receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * @author Yuhi Ishikura
 */
public class JijipingServer {

  private Map<String, List<ClientHandler>> clientHandlers = new HashMap<String, List<ClientHandler>>();

  /**
   * サーバーを開始します。
   * 
   * @param port 待ち受けポート
   * @throws IOException 開始できなかった場合
   */
  public void start(int port) throws IOException {
    final ServerSocket serverSocket = new ServerSocket(port);
    while (true) {
      final Socket socket = serverSocket.accept();
      new ClientHandler(socket).start();
    }
  }

  List<ClientHandler> getClientHandlers(String clientId) {
    synchronized (this.clientHandlers) {
      final List<ClientHandler> handlers = this.clientHandlers.get(clientId);
      if (handlers == null) {
        return Collections.emptyList();
      }
      return handlers;
    }
  }

  void checkin(String clientId, ClientHandler h) {
    synchronized (this.clientHandlers) {
      List<ClientHandler> handlers = this.clientHandlers.get(clientId);
      if (handlers == null) {
        handlers = new ArrayList<JijipingServer.ClientHandler>();
        this.clientHandlers.put(clientId, handlers);
      }
      handlers.add(h);
    }
  }

  void checkout(String clientId, ClientHandler h) {
    synchronized (this.clientHandlers) {
      List<ClientHandler> handlers = this.clientHandlers.get(clientId);
      if (handlers == null) {
        return;
      }
      handlers.remove(h);
    }
  }

  class ClientHandler extends Thread {

    /** */
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$
    private Socket socket;
    private Writer writer;

    ClientHandler(Socket socket) throws IOException {
      this.socket = socket;
      this.writer = new OutputStreamWriter(socket.getOutputStream(), ENCODING);
    }

    void sendCommand(int commandId, String params) throws IOException {
      System.out.println("writing");
      this.writer.write(String.valueOf(commandId) + ":" + params); //$NON-NLS-1$
      this.writer.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
      try {
        final BufferedReader br = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), ENCODING));

        l:while (true) {
          String line;
          synchronized (ClientHandler.this) {
            System.out.println("reading");
            line = br.readLine();
          }
          if (line.length() == 0) {
            continue;
          }
          StringTokenizer tokenizer = new StringTokenizer(line, ":"); //$NON-NLS-1$
          final int commandId = Integer.parseInt(tokenizer.nextToken());
          final String clientId = tokenizer.nextToken();
          final String parameter;
          if (tokenizer.hasMoreTokens()) {
            parameter = tokenizer.nextToken();
          } else {
            parameter = ""; //$NON-NLS-1$
          }
          switch (commandId) {
            case JijipingClient.CHECKIN_COMMAND_ID:
              checkin(clientId, this);
              continue l;
            case JijipingClient.CHECKOUT_COMMAND_ID:
              checkout(clientId, this);
              break l;
          }

          for (ClientHandler to : getClientHandlers(clientId)) {
            if (to == this) {
              continue;
            }
            to.sendCommand(commandId, parameter);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

  }

  public static void main(String[] args) throws IOException {
    final int port = 10023;
    new Thread() {

      public void run() {
        final JijipingServer server = new JijipingServer();
        try {
          server.start(port);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }.start();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e1) {
      throw new RuntimeException(e1);
    }
    Receiver receiver = new Receiver() {

      @Override
      public void questionReceived(String question, Answers answer) {
        System.out.println(question);
      }

      @Override
      public void answerReceived(int answerIndex) {
        // TODO Auto-generated method stub

      }
    };
    final JijipingClient client1 = new JijipingClient("localhost", port, receiver);
    final JijipingClient client2 = new JijipingClient("localhost", port, receiver);
    try {
      client1.checkin("aaa");
      client2.checkin("aaa");
    } catch (IdAlreadyUsedException e) {
      throw new RuntimeException(e);
    }
    client1.sendQuestion("aiueo", new Answers());
  }
}
