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

import jp.uphy.jijiping.common.Answers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * @author Yuhi Ishikura
 */
public class AgedFamilyActivity extends Activity {

  public static final String INTENT_QUESTION = "question"; //$NON-NLS-1$
  public static final String INTENT_ANSWERS = "answers"; //$NON-NLS-1$

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.younger);

    final String question = getIntent().getStringExtra(INTENT_QUESTION);
    final Answers answers = (Answers)getIntent().getSerializableExtra(INTENT_ANSWERS);

    if (question == null || answers == null) {
      finish();
      return;
    }

    final LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);

    // question
    final TextView questionView = new TextView(this);
    questionView.setText(question);
    layout.addView(questionView);
    // answer
    createAnswerViews(answers, layout);

    setContentView(layout);
  }

  private void createAnswerViews(final Answers answers, final LinearLayout layout) {
    int i = 0;
    for (final String answer : answers) {
      final Button answerView = new Button(this);
      answerView.setText(answer);
      answerView.setTextSize(30);
      layout.addView(answerView);

      final int answerIndex = i++;
      answerView.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(@SuppressWarnings("unused") View v) {
          send(answerIndex);
        }
      });
    }
  }

  void send(int i) {
    final SendAnswerServiceConnection connection = new SendAnswerServiceConnection(i);
    bindService(new Intent(this, JijipingService.class), connection, Context.BIND_AUTO_CREATE);
    finish();
  }

  class SendAnswerServiceConnection implements ServiceConnection {

    private int answerIndex;

    SendAnswerServiceConnection(int answerIndex) {
      this.answerIndex = answerIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      final JijipingService jijipingService = ((JijipingService.JijipingLocalBinder)service).getService();
      jijipingService.sendAnswer(this.answerIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceDisconnected(ComponentName className) {
      // do nothing
    }
  }
}
