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
import jp.uphy.jijiping.common.Question;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 * @author ishikura
 */
public class Bot {

  JijipingClient client;

  /**
   * {@link Bot}オブジェクトを構築します。
   * 
   * @throws IOException
   * @throws UnknownHostException
   */
  public Bot() throws UnknownHostException, IOException {
    this.client = new JijipingClient("192.168.0.10", 12542, new JijipingClient.Receiver() {

      @Override
      public void answerReceived(Question question, int answerIndex) {
        System.out.println("AnswerReceived: " + answerIndex);
      }

      @Override
      public void questionReceived(Question question) {
        client.sendAnswer(question, 0);
      }
    });
    this.client.checkin("sample");
  }

  public static void main(String[] args) throws UnknownHostException, IOException {
    new Bot();
  }
}
