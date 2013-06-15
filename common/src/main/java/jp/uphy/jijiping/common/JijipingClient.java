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
package jp.uphy.jijiping.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


/**
 * @author Yuhi Ishikura
 */
public class JijipingClient {

  /** チェックインコマンドのIDです。 */
  public static final int CHECKIN_COMMAND_ID = 0;
  /** チェックアウトコマンドのIDです。 */
  public static final int CHECKOUT_COMMAND_ID = 1;
  private static final int SEND_QUESTION_COMMAND_ID = 2;
  private static final int SEND_ANSWER_COMMAND_ID = 3;

  private String clientId;
  private ClientThread clientThread;

  /**
   * {@link JijipingClient}オブジェクトを構築します。
   * 
   * @param host ホストアドレス
   * @param port ポート
   * @param receiver メッセージ受信時の処理を定義するオブジェクト
   * @throws IOException 通信できなかった場合
   * @throws UnknownHostException 未知のホストの場合
   */
  public JijipingClient(String host, int port, Receiver receiver) throws UnknownHostException, IOException {
    this.clientThread = new ClientThread(host, port, receiver);
    this.clientThread.start();
  }

  /**
   * チェックインします。
   * 
   * @param clientId ID
   */
  public void checkin(@SuppressWarnings("hiding") String clientId) {
    synchronized (this) {
      if (this.clientId != null) {
        throw new IllegalStateException("Already checked in."); //$NON-NLS-1$
      }
      writeCommand(CHECKIN_COMMAND_ID, clientId);
      this.clientId = clientId;
    }
  }

  /**
   * チェックアウトします。
   */
  public void checkout() {
    synchronized (this) {
      if (this.clientId == null) {
        throw new IllegalStateException("Not checked in."); //$NON-NLS-1$
      }
      this.clientThread.requestStop();
      this.clientThread = null;
      writeCommand(CHECKOUT_COMMAND_ID, this.clientId);
    }
  }

  /**
   * 質問を送信します。
   * 
   * @param question 質問
   * @param answers 回答選択肢
   */
  public void sendQuestion(String question, Answers answers) {
    final List<String> params = new ArrayList<String>();
    params.add(question);
    for (String answer : answers) {
      params.add(answer);
    }

    writeCommand(SEND_QUESTION_COMMAND_ID, this.clientId, Util.listToCsv(params));
  }

  /**
   * 質問を送信します。
   * 
   * @param answerIndex 回答選択肢のインデックス
   */
  public void sendAnswer(int answerIndex) {
    writeCommand(SEND_ANSWER_COMMAND_ID, this.clientId, String.valueOf(answerIndex));
  }

  private void writeCommand(int commandId, @SuppressWarnings("hiding") String clientId) {
    writeCommand(commandId, clientId, ""); //$NON-NLS-1$
  }

  private void writeCommand(int commandId, @SuppressWarnings("hiding") String clientId, String data) {
    final String message = String.format("%d:%s:%s", Integer.valueOf(commandId), clientId, data); //$NON-NLS-1$
    this.clientThread.putMessage(message);
  }

  static class ClientThread extends Thread {

    private SocketChannel channel;
    private Queue<String> messageQueue = new LinkedList<String>();
    private Receiver receiver;
    private boolean stopRequested = false;

    /**
     * {@link JijipingClient}オブジェクトを構築します。
     * 
     * @param host ホストアドレス
     * @param port ポート
     * @param receiver メッセージ受信時の処理を定義するオブジェクト
     * @throws IOException 通信できなかった場合
     * @throws UnknownHostException 未知のホストの場合
     */
    public ClientThread(String host, int port, Receiver receiver) throws UnknownHostException, IOException {
      this.channel = SocketChannel.open(new InetSocketAddress(host, port));
      this.channel.configureBlocking(false);
      this.receiver = receiver;
    }

    public void requestStop() {
      synchronized (this.messageQueue) {
        this.stopRequested = true;
      }
    }

    public void putMessage(String message) {
      synchronized (this.messageQueue) {
        this.messageQueue.add(message);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
      try {
        Selector selector = Selector.open();
        this.channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        while (selector.select() > 0) {
          final Set<SelectionKey> keys = selector.selectedKeys();
          for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
            final SelectionKey key = it.next();
            it.remove();
            if (key.isReadable()) {
              final BufferedReader reader = new BufferedReader(new StringReader(Util.read(this.channel)));
              String message;
              while ((message = reader.readLine()) != null) {
                final int delimiterIndex = message.indexOf(':');
                final int commandId = Integer.parseInt(message.substring(0, delimiterIndex));
                final String parameter = message.substring(delimiterIndex + 1);
                switch (commandId) {
                  case SEND_ANSWER_COMMAND_ID:
                    final int answerIndex = Integer.parseInt(parameter);
                    this.receiver.answerReceived(answerIndex);
                    break;
                  case SEND_QUESTION_COMMAND_ID:
                    final List<String> qaData = Util.csvToList(parameter);
                    final String question = qaData.get(0);
                    final Answers answers = new Answers();
                    for (int i = 1; i < qaData.size(); i++) {
                      answers.add(qaData.get(i));
                    }
                    this.receiver.questionReceived(question, answers);
                    break;
                  default:
                    throw new UnsupportedOperationException();
                }
              }
            } else if (key.isWritable()) {
              synchronized (this.messageQueue) {
                while (this.messageQueue.size() > 0) {
                  final String message = this.messageQueue.poll();
                  Util.write(this.channel, message);
                }
                if (this.stopRequested) {
                  this.channel.close();
                  return;
                }
              }
            }
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * 質問・回答を受け取ったときの処理を定義するハンドラです。
   * 
   * @author Yuhi Ishikura
   */
  public static interface Receiver {

    /**
     * 回答を受け取ったときに呼び出されます。
     * 
     * @param answerIndex 回答選択肢のインデックス
     */
    void answerReceived(int answerIndex);

    /**
     * 質問を受け取った時に呼び出されます。
     * 
     * @param question 質問
     * @param answer 回答の選択肢
     */
    void questionReceived(String question, Answers answer);
  }
}
