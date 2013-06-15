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

import jp.uphy.jijiping.app.ErrorNotifier;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.inject.Inject;


/**
 * @author Yuhi Ishikura
 */
public class QuestionerActivity extends RoboActivity {

  @Inject
  private ErrorNotifier errorNotifier;
  @InjectView(R.id.question)
  private EditText question;
  @InjectView(R.id.questionType)
  private Spinner questionType;
  @InjectView(R.id.sendQuestion)
  private Button send;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.questioner);

    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"Yes/No"});
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    this.questionType.setAdapter(adapter);
    this.questionType.setSelection(0);

    this.send.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        send();
      }
    });
  }

  void send() {
    final String question = this.question.getText().toString();
    final Answers answers = new Answers();
    if (this.questionType.getSelectedItemPosition() == 0) {
      answers.add("はい");
      answers.add("いいえ");
    }
    final Intent intent = new Intent(this, AnswererActivity.class);
    intent.putExtra(AnswererActivity.INTENT_QUESTION, question);
    intent.putExtra(AnswererActivity.INTENT_ANSWERS, answers);
    startActivity(intent);
  }

}
