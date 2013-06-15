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

/**
 * 例外を処理するインターフェースです。
 * 
 * @author Yuhi Ishikura
 */
public interface ErrorNotifier {

  /**
   * エラーを通知します。
   * 
   * @param e 例外
   * @param message メッセージ
   */
  void notifyError(Throwable e, String message);

  /**
   * エラーを通知します。
   * 
   * @param e 例外
   */
  void notifyError(Throwable e);

  /**
   * エラーを通知します。
   * 
   * @param message メッセージ
   */
  void notifyError(String message);

}
