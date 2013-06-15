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
import jp.uphy.jijiping.common.Answers;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.inject.Inject;


/**
 * @author Yuhi Ishikura
 */
public class YoungrFamilyActivity extends RoboActivity {

  @Inject
  private ErrorNotifier errorNotifier;
  @InjectView(R.id.question)
  private EditText questionText;
  @InjectView(R.id.questionType)
  private Spinner questionTypeSpinner;
  @InjectView(R.id.sendQuestion)
  private Button sendButton;
  @InjectView(R.id.customAnswersPane)
  private LinearLayout customAnswersPane;
  @InjectView(R.id.customAnswers)
  private LinearLayout customAnswers;
  @InjectView(R.id.addAnswer)
  private Button addCustomAnswerButton;
  @InjectView(R.id.saveAnswers)
  private Button saveCustomAnswersButton;
  private List<EditText> customAnswerTexts = new ArrayList<EditText>();

  private Communicator communicator;
  
  public static final String CHECKIN_ID = "checkinId"; //$NON-NLS-2$
  /** ユーザ状態のインテントパラメータ名 */
  public static final String STATE = "state"; //$NON-NLS-3$
  
  /**
   * {@link YoungrFamilyActivity}オブジェクトを構築します。
   */
  public YoungrFamilyActivity(Communicator communicator) {
    super();
    this.communicator = new DummyCommunicator(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.younger);

    final List<String> questionTypes = new ArrayList<String>();
    questionTypes.add(getString(R.string.yes_no));
    questionTypes.add(getString(R.string.custom));

    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, questionTypes);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    this.questionTypeSpinner.setAdapter(adapter);
    this.questionTypeSpinner.setSelection(0);

    this.sendButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(@SuppressWarnings("unused") View v) {
        send();
      }
    });
    this.questionTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unused")
      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        updateViewByQuestionTypeSelection();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void onNothingSelected(@SuppressWarnings("unused") AdapterView<?> arg0) {
        updateViewByQuestionTypeSelection();
      }
    });
    this.addCustomAnswerButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(@SuppressWarnings("unused") View v) {
        addNewAnswerEditField();
      }
    });
    this.saveCustomAnswersButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(@SuppressWarnings("unused") View v) {
        saveCustomAnswers();
      }
    });
    updateViewByQuestionTypeSelection();
  }

  void addNewAnswerEditField() {
    final EditText text = new EditText(this);
    this.customAnswers.addView(text);
    this.customAnswerTexts.add(text);
  }

  void updateViewByQuestionTypeSelection() {
    if (isCustomSelected()) {
      this.customAnswersPane.setVisibility(View.VISIBLE);
    } else {
      this.customAnswers.removeAllViews();
      this.customAnswerTexts.clear();
      this.customAnswersPane.setVisibility(View.INVISIBLE);
    }
  }

  void send() {
    final String question = this.questionText.getText().toString();
    final Answers answers = new Answers();
    if (isYesNoSelected()) {
      answers.add(getString(R.string.yes));
      answers.add(getString(R.string.no));
    } else if (isCustomSelected()) {
      for (EditText answer : this.customAnswerTexts) {
        answers.add(answer.getText().toString());
      }
    }
    this.communicator.sendQuestion(question, answers);
  }

  void saveCustomAnswers() {
    final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
    final Editor editor = prefs.edit();
  }

  private boolean isCustomSelected() {
    return isYesNoSelected() == false;
  }

  private boolean isYesNoSelected() {
    return this.questionTypeSpinner.getSelectedItemPosition() == 0;
  }

}