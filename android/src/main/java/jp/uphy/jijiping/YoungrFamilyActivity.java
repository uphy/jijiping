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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
  private List<EditText> customAnswerTexts = new ArrayList<EditText>();

  private List<SendQuestionServiceConnection> connections = new ArrayList<YoungrFamilyActivity.SendQuestionServiceConnection>();

  /** チェックイン対象の部屋のIDのインテントパラメータです。 */
  public static final String CHECKIN_ID = "checkinId"; //$NON-NLS-1$
  /** ユーザ状態のインテントパラメータ名 */
  public static final String STATE = "state"; //$NON-NLS-1$

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
    updateViewByQuestionTypeSelection();
  }

  void addNewAnswerEditField() {
    final LayoutInflater layoutInflator = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    final LinearLayout layout = (LinearLayout)layoutInflator.inflate(R.layout.custom_editor_row, null);
    final EditText text = (EditText)layout.findViewById(R.id.text);
    final ImageButton imageButton = (ImageButton)layout.findViewById(R.id.deleteCustomText);
    imageButton.setImageResource(R.drawable.delete);
    imageButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        customAnswers.removeView(layout);
      }
    });

    this.customAnswers.addView(layout);
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
    final SendQuestionServiceConnection connection = new SendQuestionServiceConnection(question, answers);
    this.connections.add(connection);
    bindService(new Intent(this, JijipingService.class), connection, Context.BIND_AUTO_CREATE);

    //    try {
    //      Thread.sleep(10 * 1000);
    //    } catch (InterruptedException e) {
    //      throw new RuntimeException(e);
    //    }
    //    unbindService(connection);
    //    finish();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onDestroy() {
    super.onDestroy();
    for (SendQuestionServiceConnection con : this.connections) {
      unbindService(con);
    }
    this.connections.clear();
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

  class SendQuestionServiceConnection implements ServiceConnection {

    private String question;
    private Answers answers;

    SendQuestionServiceConnection(String question, Answers answers) {
      this.question = question;
      this.answers = answers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      final JijipingService jijipingService = ((JijipingService.JijipingLocalBinder)service).getService();
      jijipingService.sendQuestion(this.question, this.answers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceDisconnected(ComponentName className) {
      unbindService(this);
    }
  };

}
