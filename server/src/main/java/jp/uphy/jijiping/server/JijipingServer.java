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

import jp.uphy.jijiping.common.JijipingClient;
import jp.uphy.jijiping.common.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * @author Yuhi Ishikura
 */
public class JijipingServer {

  private Map<String, Set<ClientContext>> clientIdToContext = new HashMap<String, Set<JijipingServer.ClientContext>>();

  /**
   * サーバーを開始します。
   * 
   * @param port 待ち受けポート
   * @throws IOException 開始できなかった場合
   */
  public void start(int port) throws IOException {
    final ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.socket().setReuseAddress(true);
    serverChannel.socket().bind(new InetSocketAddress(port));
    serverChannel.configureBlocking(false);
    final Selector selector = Selector.open();
    final SelectionKey serverKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    while (selector.select() > 0) {
      Set<SelectionKey> keys = selector.selectedKeys();
      for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
        SelectionKey key = it.next();
        it.remove();
        if (key == serverKey && key.isAcceptable()) {
          final SocketChannel channel = serverChannel.accept();
          channel.configureBlocking(false);
          SelectionKey clientKey = channel.register(selector, SelectionKey.OP_READ);
          final ClientContext context = new ClientContext();
          clientKey.attach(context);
        } else {
          if (key.isReadable()) {
            final SocketChannel channel = (SocketChannel)key.channel();
            final String line = Util.read(channel);
            final ClientContext context = (ClientContext)key.attachment();

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
                context.setKey(key);
                checkin(clientId, context);
                continue;
              case JijipingClient.CHECKOUT_COMMAND_ID:
                checkout(clientId, context);
                continue;
            }

            write(context, clientId, String.valueOf(commandId) + ":" + parameter); //$NON-NLS-1$
          }
          if (key.isWritable() && key.isValid()) {
            final ClientContext context = (ClientContext)key.attachment();
            String message;
            final SocketChannel channel = (SocketChannel)key.channel();
            while ((message = context.popMessage()) != null) {
              Util.write(channel, message);
            }
          }
        }
      }
    }
  }

  private void checkout(String clientId, ClientContext context) {
    synchronized (this.clientIdToContext) {
      final Set<ClientContext> contexts = this.clientIdToContext.get(clientId);
      contexts.remove(context);
    }
  }

  private void checkin(String clientId, ClientContext context) {
    synchronized (this.clientIdToContext) {
      Set<ClientContext> contexts = this.clientIdToContext.get(clientId);
      if (contexts == null) {
        contexts = new HashSet<JijipingServer.ClientContext>();
        contexts.add(context);
        this.clientIdToContext.put(clientId, contexts);
      }
      contexts.add(context);
    }
  }

  void write(ClientContext sender, String clientId, String line) {
    for (ClientContext context : getContexts(clientId)) {
      if (context == sender) {
        continue;
      }
      context.pushMessage(line);
    }
  }

  Set<ClientContext> getContexts(String clientId) {
    Set<ClientContext> contexts = this.clientIdToContext.get(clientId);
    if (contexts == null) {
      return Collections.emptySet();
    }
    return contexts;
  }

  static class ClientContext {

    private Queue<String> writeQueue = new LinkedList<String>();
    private SelectionKey key;

    /**
     * keyを設定します。
     * 
     * @param key key
     */
    public void setKey(SelectionKey key) {
      synchronized (this.writeQueue) {
        this.key = key;
      }
    }

    void pushMessage(String message) {
      synchronized (this.writeQueue) {
        if (this.key == null) { // checkin前
          return;
        }
        this.writeQueue.add(message);
        this.key.interestOps(this.key.interestOps() | SelectionKey.OP_WRITE);
      }
    }

    String popMessage() {
      synchronized (this.writeQueue) {
        if (this.writeQueue.size() == 0) {
          return null;
        }
        final String message = this.writeQueue.poll();
        if (this.writeQueue.isEmpty()) {
          this.key.interestOps(this.key.interestOps() & (~SelectionKey.OP_WRITE));
        }
        return message;
      }
    }
  }

}
