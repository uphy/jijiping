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

import android.content.Context;
import android.content.Intent;


/**
 * @author Yuhi Ishikura
 */
class ErrorNotifierImpl implements ErrorNotifier {

  private Context context;

  /**
   * {@link ErrorNotifierImpl}オブジェクトを構築します。
   * 
   * @param context コンテキスト
   */
  ErrorNotifierImpl(Context context) {
    this.context = context;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void notifyError(Throwable e, String message) {
    final Intent intent = new Intent(this.context, ErrorDialogActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(ErrorDialogActivity.INTENT_MESSAGE, message);
    intent.putExtra(ErrorDialogActivity.INTENT_EXCEPTION, e);
    this.context.startActivity(intent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void notifyError(Throwable e) {
    notifyError(e, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void notifyError(String message) {
    notifyError(null, message);
  }

}
