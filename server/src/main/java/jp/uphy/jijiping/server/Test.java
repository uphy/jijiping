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
import jp.uphy.jijiping.common.JijipingClient;
import jp.uphy.jijiping.common.JijipingClient.Receiver;

import java.io.IOException;


/**
 * @author ishikura
 */
public class Test {

  public static void main(String[] args) throws IOException {
    final int port = 12542;
    //final String host = "192.168.0.10";
    final String host = "localhost";
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
    final JijipingClient client1 = createClient(host, port, "client1");
    final JijipingClient client2 = createClient(host, port, "client2");
    client1.checkin("aaa");
    client2.checkin("aaa");
    
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }

    final Answers answers = new Answers();
    answers.add("Yes");
    answers.add("No");
    client1.sendQuestion("How do you do?", answers);
    System.out.println("Test.enclosing_method()");
  }

  private static JijipingClient createClient(String host, int port, String name) throws IOException {
    TestReceiver receiver = new TestReceiver(name);
    final JijipingClient client = new JijipingClient(host, port, receiver);
    receiver.setClient(client);
    return client;
  }

  static class TestReceiver implements Receiver {

    private String name;
    private JijipingClient client;

    TestReceiver(String name) {
      super();
      this.name = name;
    }

    void setClient(JijipingClient client) {
      this.client = client;
    }

    @Override
    public void questionReceived(String question, Answers answer) {
      log("QuestionReceived: " + question + "," + answer);
      this.client.sendAnswer(1);
    }

    @Override
    public void answerReceived(int answerIndex) {
      log("AnswerReceived: " + answerIndex);
    }

    private void log(String text) {
      System.out.printf("[%s]%s%n", this.name, text);
    }

  }

}
