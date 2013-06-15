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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Yuhi Ishikura
 */
public class JijipingClient {

  /** チェックインコマンドのIDです。 */
  public static final int CHECKIN_COMMAND_ID = 0;
  /** チェックアウトコマンドのIDです。 */
  public static final int CHECKOUT_COMMAND_ID = 1;
  static final Logger logger = Logger.getLogger(JijipingClient.class.getName());
  private static final int SEND_QUESTION_COMMAND_ID = 2;
  private static final int SEND_ANSWER_COMMAND_ID = 3;

  private String clientId;
  private ClientThread clientThread;
  private Map<String, Question> questionMap = new HashMap<String, Question>();

  static {
    logger.setLevel(Level.ALL);
    final ConsoleHandler h = new ConsoleHandler();
    h.setLevel(Level.ALL);
    logger.addHandler(h);
  }

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
    final String questionId = String.valueOf((long)(Math.random() * 100000000000000000L));
    final Question q = new Question(questionId, question, answers);
    this.questionMap.put(questionId, q);
    writeCommand(SEND_QUESTION_COMMAND_ID, this.clientId, q.toCsv());
  }

  /**
   * 質問を送信します。
   * 
   * @param question 質問
   * @param answerIndex 回答選択肢のインデックス
   */
  public void sendAnswer(Question question, int answerIndex) {
    writeCommand(SEND_ANSWER_COMMAND_ID, this.clientId, question.getId() + "," + answerIndex); //$NON-NLS-1$
  }

  private void writeCommand(int commandId, @SuppressWarnings("hiding") String clientId) {
    writeCommand(commandId, clientId, ""); //$NON-NLS-1$
  }

  private void writeCommand(int commandId, @SuppressWarnings("hiding") String clientId, String data) {
    final String message = String.format("%d:%s:%s", Integer.valueOf(commandId), clientId, data); //$NON-NLS-1$
    logger.fine("Write requested => " + message); //$NON-NLS-1$
    this.clientThread.putMessage(message);
  }

  class ClientThread extends Thread {

    private SocketChannel channel;
    private Queue<String> messageQueue = new LinkedList<String>();
    private Receiver receiver;
    private boolean stopRequested = false;
    private SelectionKey key;

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
        //        this.key.interestOps(this.key.interestOps() | SelectionKey.OP_WRITE);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
      try {
        Selector selector = Selector.open();
        this.key = this.channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        while (selector.select() > 0) {
          final Set<SelectionKey> keys = selector.selectedKeys();
          for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
            @SuppressWarnings("hiding")
            final SelectionKey key = it.next();
            it.remove();
            if (key.isReadable()) {
              final BufferedReader reader = new BufferedReader(new StringReader(Util.read(this.channel)));
              String message;
              while ((message = reader.readLine()) != null) {
                logger.fine("Reading => " + message); //$NON-NLS-1$
                final int delimiterIndex = message.indexOf(':');
                final int commandId = Integer.parseInt(message.substring(0, delimiterIndex));
                final String parameter = message.substring(delimiterIndex + 1);
                switch (commandId) {
                  case SEND_ANSWER_COMMAND_ID:
                    final String[] idAnswerIndex = parameter.split(","); //$NON-NLS-1$
                    final String questionId = idAnswerIndex[0];
                    final int answerIndex = Integer.parseInt(idAnswerIndex[1]);
                    final Question question = questionMap.get(questionId);
                    this.receiver.answerReceived(question, answerIndex);
                    break;
                  case SEND_QUESTION_COMMAND_ID:
                    this.receiver.questionReceived(Question.fromCsv(parameter));
                    break;
                  default:
                    throw new UnsupportedOperationException();
                }
              }
            }
            if (key.isWritable()) {
              try {
                Thread.sleep(300);
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
              synchronized (this.messageQueue) {
                while (this.messageQueue.size() > 0) {
                  final String message = this.messageQueue.poll();
                  logger.fine("Writing => " + message); //$NON-NLS-1$
                  Util.write(this.channel, message);
                }
                //                if (this.messageQueue.isEmpty()) {
                //                  this.key.interestOps(this.key.interestOps() & (~SelectionKey.OP_WRITE));
                //                }
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
     * @param question 質問
     * @param answerIndex 回答選択肢のインデックス
     */
    void answerReceived(Question question, int answerIndex);

    /**
     * 質問を受け取った時に呼び出されます。
     * 
     * @param question 質問
     */
    void questionReceived(Question question);
  }
}
