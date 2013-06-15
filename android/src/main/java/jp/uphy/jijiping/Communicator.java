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
package jp.uphy.jijiping;

/**
 * 質問・回答をやり取りするためのインターフェースです。
 * 
 * @author Yuhi Ishikura
 */
public interface Communicator {
  
  /**
   * 入室します
   */
  void checkIn();
  
  /**
   * 退室します
   */
  void checkout();
  
  /**
   * 質問を送信します。
   * 
   * @param question 質問
   * @param answers 回答の選択肢
   */
  void sendQuestion(String question, Answers answers);

  /**
   * 回答を送信します。
   * 
   * @param answerIndex 回答選択肢のインデックス
   */
  void sendAnswer(int answerIndex);
  
  /**
   * 回答を待ちます。
   */
  void listen();
}
