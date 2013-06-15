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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import jp.uphy.jijiping.CommunicatorImpl;

/**
 * @author Yuhi Ishikura
 */
public class CheckInActivity extends RoboActivity {
  @InjectView(R.id.aged)
  private Button agedButton;
  @InjectView(R.id.young)
  private Button youngButton;
  @InjectView(R.id.checkinId)
  private EditText checkinText;
  
  private String id;
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.checkin);
    
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
  }
  
  void startYoungPeople(){
    final Intent intent = new Intent(this, YoungrFamilyActivity.class);
    intent.putExtra(IntentParamerter.checkinId.getName(), id);
    intent.putExtra(IntentParamerter.state.getName(), YougerState.send);
    this.startService(intent);
  }
  
  void startAgedPeople(){
    final Intent intent = new Intent(this, AgedFamillyService.class);
    intent.putExtra(IntentParamerter.checkinId.getName(), id);
    intent.putExtra(IntentParamerter.state.getName(), AgedState.wait);
    this.startService(intent);
  }
}
