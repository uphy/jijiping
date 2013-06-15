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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 回答選択肢を表すクラスです。
 * 
 * @author Yuhi Ishikura
 */
public class Answers implements Serializable, Iterable<String> {

  /** for serialization. */
  private static final long serialVersionUID = -8655317689995285013L;
  private List<String> answers = new ArrayList<String>();

  /**
   * 回答選択肢を追加します。
   * 
   * @param answer 回答選択肢
   */
  public void add(String answer) {
    this.answers.add(answer);
  }

  /**
   * 回答選択肢の数を取得します。
   * 
   * @return 回答選択肢の数
   */
  public int size() {
    return this.answers.size();
  }

  /**
   * 回答を取得します。
   * 
   * @param i 回答選択肢のインデックス
   * @return 回答選択肢
   */
  public String getAnswer(int i) {
    return this.answers.get(i);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<String> iterator() {
    return this.answers.iterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return this.answers.toString();
  }

}
