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

import jp.uphy.jijiping.common.Question;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


/**
 * @author Yuhi Ishikura
 */
public class AnswerActivity extends RoboActivity {

  /** */
  public static final String INTENT_ANSWER = "answer"; //$NON-NLS-1$
  /** */
  public static final String INTENT_QUESTION = "question"; //$NON-NLS-1$
  @InjectView(R.id.answerQuestion)
  private TextView questionView;
  @InjectView(R.id.answerAnswer)
  private TextView answerView;
  @InjectView(R.id.answerClose)
  private Button closeButton;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.answer);

    final Question question = (Question)getIntent().getSerializableExtra(INTENT_QUESTION);
    final int answerIndex = getIntent().getIntExtra(INTENT_ANSWER, 0);
    final String answer = question.getAnswers().getAnswer(answerIndex);

    this.questionView.setText(question.getQuestion());
    this.answerView.setText(answer);
    this.closeButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }
}
