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
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * @author Yuhi Ishikura
 */
public class CheckInActivity extends RoboActivity {

  @InjectView(R.id.aged)
  private ImageButton agedButton;
  @InjectView(R.id.young)
  private ImageButton youngButton;
  @InjectView(R.id.checkinId)
  private EditText checkinText;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.checkin);
    // ウィンドウマネージャのインスタンス取得
    WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
    // ディスプレイのインスタンス生成
    Display disp = wm.getDefaultDisplay();
    
    agedButton.setLayoutParams(new LinearLayout.LayoutParams((int)(disp.getWidth()*0.5),(int)(disp.getWidth()*0.5)) );
    youngButton.setLayoutParams(new LinearLayout.LayoutParams((int)(disp.getWidth()*0.5),(int)(disp.getWidth()*0.5)) );
    
    this.agedButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(@SuppressWarnings("unused") View v) {
        startAgedPeople();
      }
    });

    this.youngButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(@SuppressWarnings("unused") View v) {
        startYoungPeople();
      }
    });

    this.checkinText.setText("sample");
  }

  void startYoungPeople() {
    startService();

    final Intent activityIntent = new Intent(this, YoungrFamilyActivity.class);
    startActivity(activityIntent);
  }

  void startAgedPeople() {
    startService();
    finish();
  }

  private void startService() {
    final String id = this.checkinText.getText().toString();
    final Intent serviceIntent = new Intent(this, JijipingService.class);
    serviceIntent.putExtra(JijipingService.INTENT_CHECKID, id);
    startService(serviceIntent);
    Toast.makeText(this, "Jijiping service is started.", Toast.LENGTH_SHORT).show();
  }

}
