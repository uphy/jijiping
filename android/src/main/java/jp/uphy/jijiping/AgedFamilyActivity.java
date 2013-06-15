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
import jp.uphy.jijiping.common.Question;

import java.util.ArrayList;
import java.util.List;

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
  private Question question;
  private List<SendAnswerServiceConnection> connections = new ArrayList<SendAnswerServiceConnection>();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.younger);

    this.question = (Question)getIntent().getSerializableExtra(INTENT_QUESTION);
    if (this.question == null) {
      finish();
      return;
    }

    final LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);

    // question
    final TextView questionView = new TextView(this);
    questionView.setText(this.question.getQuestion());
    questionView.setTextSize(30);
    layout.addView(questionView);
    // answer
    createAnswerViews(this.question.getAnswers(), layout);

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
    this.connections.add(connection);
    bindService(new Intent(this, JijipingService.class), connection, Context.BIND_AUTO_CREATE);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    finish();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onDestroy() {
    super.onDestroy();
    for (SendAnswerServiceConnection con : this.connections) {
      unbindService(con);
    }
    this.connections.clear();
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
      jijipingService.sendAnswer(question, this.answerIndex);
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
