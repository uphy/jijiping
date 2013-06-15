package jp.uphy.jijiping;

import jp.uphy.jijiping.common.Answers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;


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

/**
 * @author Yuhi Ishikura
 */
class DummyCommunicator implements Communicator {

  /**
   * {@inheritDoc}
   */
  @Override
  public void checkIn() {
    // TODO Auto-generated method stub
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void checkout() {
    // TODO Auto-generated method stub
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void listen() {
    // TODO Auto-generated method stub
    
  }

  private Context context;

  DummyCommunicator(Context context) {
    this.context = context;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendQuestion(String question, Answers answers) {
    final Intent intent = new Intent(this.context, AgedFamilyActivity.class);
//    intent.putExtra(AgedFamilyActivity.INTENT_QUESTION, question);
//    intent.putExtra(AgedFamilyActivity.INTENT_ANSWERS, answers);
    this.context.startActivity(intent);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("boxing")
  @Override
  public void sendAnswer(int answerIndex) {
    new AlertDialog.Builder(this.context).setMessage(String.format("SEND answer : %d", answerIndex)).create().show(); //$NON-NLS-1$
  }

}
