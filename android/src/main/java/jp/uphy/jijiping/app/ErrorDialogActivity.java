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
package jp.uphy.jijiping.app;

import jp.uphy.jijiping.R;

import java.io.PrintWriter;
import java.io.StringWriter;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


/**
 * エラーを表示するアクティビティです。
 * 
 * @author Yuhi Ishikura
 */
public class ErrorDialogActivity extends RoboActivity {

  /** メッセージのインテントパラメータです。 */
  public static final String INTENT_MESSAGE = "message"; //$NON-NLS-1$
  /** 例外のインテントパラメータです。 */
  public static final String INTENT_EXCEPTION = "exception"; //$NON-NLS-1$

  @InjectView(R.id.error_message)
  private TextView messageView;
  @InjectView(R.id.error_exception)
  private TextView exceptionView;
  @InjectView(R.id.error_close)
  private Button closeButton;
  @InjectView(R.id.error_divider)
  private View divider;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.error_dialog);

    final String message = getIntent().getStringExtra(INTENT_MESSAGE);
    final Throwable exception = (Throwable)getIntent().getSerializableExtra(INTENT_EXCEPTION);

    this.messageView.setText(message);
    this.exceptionView.setText(exceptionToString(exception));

    if (exception == null || message == null) {
      // 二つなければ区切る必要はない
      this.divider.setVisibility(View.INVISIBLE);
    }

    this.closeButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(@SuppressWarnings("unused") View v) {
        finish();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onBackPressed() {
    // Disable to close by 'back'
  }

  private static String exceptionToString(Throwable e) {
    if (e == null) {
      return ""; //$NON-NLS-1$
    }
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    pw.flush();
    return sw.toString();
  }

}
