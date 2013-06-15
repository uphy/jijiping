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

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Yuhi Ishikura
 */
public class Question implements Serializable {

  private String question;
  private Answers answers;
  private String id;

  public static Question fromCsv(String csv) {
    final List<String> qaData = Util.csvToList(csv);
    final String questionId = qaData.get(0);
    final String question = qaData.get(1);
    final Answers answers = new Answers();
    for (int i = 2; i < qaData.size(); i++) {
      answers.add(qaData.get(i));
    }
    return new Question(questionId, question, answers);
  }

  /**
   * {@link Question}オブジェクトを構築します。
   * 
   * @param id ID
   * @param question 質問
   * @param answers 回答選択肢
   */
  public Question(String id, String question, Answers answers) {
    this.id = id;
    this.question = question;
    this.answers = answers;
  }

  public String toCsv() {
    final List<String> params = new ArrayList<String>();
    params.add(this.id);
    params.add(this.question);
    for (String answer : this.answers) {
      params.add(answer);
    }
    return Util.listToCsv(params);
  }

  /**
   * idを取得します。
   * 
   * @return id
   */
  public String getId() {
    return this.id;
  }

  /**
   * questionを取得します。
   * 
   * @return question
   */
  public String getQuestion() {
    return this.question;
  }

  /**
   * answersを取得します。
   * 
   * @return answers
   */
  public Answers getAnswers() {
    return this.answers;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return MessageFormat.format("Question [id={0}, question={1}, answers={2}]", this.id, this.question, this.answers); //$NON-NLS-1$
  }

}