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

  /** 質問のインテントパラメータ名です。 */
  public static final String INTENT_QUESTION = "question"; //$NON-NLS-1$
  /** 回答選択肢のインテントパラメータ名です。 */
  public static final String INTENT_ANSWERS = "answers"; //$NON-NLS-1$
  private final Communicator communicator;

  /**
   * {@link AnswererActivity}オブジェクトを構築します。
   */
  public AnswererActivity() {
    this.communicator = new DummyCommunicator(this);
  }

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
      layout.addView(answerView);

      final int answerIndex = i++;
      answerView.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(@SuppressWarnings("unused") View v) {
          sendAnswer(answerIndex);
        }
      });
    }
  }

  void sendAnswer(int i) {
    this.communicator.sendAnswer(i);
  }
}
