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

import roboguice.activity.RoboActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * @author Yuhi Ishikura
 */
public class AnswererActivity extends RoboActivity {

  public static final String INTENT_QUESTION = "question";
  public static final String INTENT_ANSWERS = "answers";

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.questioner);

    final String question = getIntent().getStringExtra(INTENT_QUESTION);
    final Answers answers = (Answers)getIntent().getSerializableExtra(INTENT_ANSWERS);

    if (question == null || answers == null) {
      finish();
      return;
    }

    final LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);

    final TextView questionView = new TextView(this);
    questionView.setText(question);
    layout.addView(questionView);

    int i = 0;
    for (final String answer : answers) {
      final Button answerView = new Button(this);
      answerView.setText(answer);
      layout.addView(answerView);

      final int answerIndex = i++;
      answerView.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          sendAnswer(answerIndex);
        }
      });
    }
    setContentView(layout);
  }

  void sendAnswer(int i) {
    new AlertDialog.Builder(AnswererActivity.this).setMessage(String.format("send answer : %d", i)).create().show();
  }
}
