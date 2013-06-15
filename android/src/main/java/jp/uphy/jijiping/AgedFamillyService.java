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

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.style.LineHeightSpan.WithDensity;


/**
 * @author Yuhi Ishikura
 */
public class AgedFamillyService extends Service {

  class IncommingHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
      //メッセージを受信したときの処理
    }
  }

  Messenger mServiceMessenger = new Messenger(new IncommingHandler());

  private String checkinId;

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    checkinId = intent.getStringExtra(IntentParamerter.checkinId.getName());
    AgedState agedState = (AgedState)intent.getSerializableExtra(IntentParamerter.checkinId.getName());
    switch(agedState){
      case wait:
        break;
      case receive:
        break;
      case send:
        break;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IBinder onBind(Intent intent) {
    return mServiceMessenger.getBinder();
  }
}
