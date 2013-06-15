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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;


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
  private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

  private Socket socket;
  private Writer writer;
  private BufferedReader reader;
  private String clientId;
  private Receiver receiver;
  private Thread readerThread;

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
    this.socket = new Socket(host, port);
    this.writer = new OutputStreamWriter(this.socket.getOutputStream(), ENCODING);
    this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), ENCODING));

    this.receiver = receiver;
  }

  /**
   * チェックインします。
   * 
   * @param clientId ID
   * @throws IOException 通信に失敗した場合
   * @throws IdAlreadyUsedException IDがすでに利用されている場合
   */
  public void checkin(@SuppressWarnings("hiding") String clientId) throws IOException, IdAlreadyUsedException {
    this.readerThread = new ReaderThread(this.reader);
    this.readerThread.start();

    writeCommand(CHECKIN_COMMAND_ID, clientId);
    this.clientId = clientId;
  }

  /**
   * チェックアウトします。
   * 
   * @throws IOException 通信に失敗した場合
   */
  public void checkout() throws IOException {
    writeCommand(CHECKOUT_COMMAND_ID, this.clientId);
  }

  /**
   * 質問を送信します。
   * 
   * @param question 質問
   * @param answers 回答選択肢
   * @throws IOException 通信中に問題が発生した場合
   */
  public void sendQuestion(String question, Answers answers) throws IOException {
    final List<String> params = new ArrayList<String>();
    params.add(question);
    for (String answer : answers) {
      params.add(answer);
    }

    writeCommand(SEND_QUESTION_COMMAND_ID, this.clientId, toCsv(params));
  }

  /**
   * 質問を送信します。
   * 
   * @param answerIndex 回答選択肢のインデックス
   * @throws IOException 通信中に問題が発生した場合
   */
  public void sendAnswer(int answerIndex) throws IOException {
    writeCommand(SEND_ANSWER_COMMAND_ID, this.clientId, String.valueOf(answerIndex));
  }

  private static String toCsv(final List<String> params) throws IOException {
    final StringWriter sw = new StringWriter();
    final CSVWriter<String[]> csvWriter = CSVWriterBuilder.newDefaultWriter(sw);
    csvWriter.write(params.toArray(new String[0]));
    final String csv = sw.toString();
    return csv;
  }

  private void writeCommand(int commandId, @SuppressWarnings("hiding") String clientId) throws IOException {
    writeCommand(commandId, clientId, ""); //$NON-NLS-1$
  }

  private void writeCommand(int commandId, @SuppressWarnings("hiding") String clientId, String data) throws IOException {
    this.writer.write(String.format("%d:%s:%s\n", Integer.valueOf(commandId), clientId, data)); //$NON-NLS-1$
    this.writer.flush();
  }

  static class ReaderThread extends Thread {

    private BufferedReader reader;

    ReaderThread(BufferedReader reader) {
      super();
      this.reader = reader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
      String line;
      try {
        while ((line = this.reader.readLine()) != null) {
          System.out.println("client receiver: "+line);
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
