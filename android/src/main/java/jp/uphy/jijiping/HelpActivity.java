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

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboListActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;


/**
 * @author ishikura
 */
public class HelpActivity extends RoboListActivity {

  static final Help[] helps = new Help[] {
      new Help("概要", "「ピンピン」はご老人との何気ないやりとりを促進させるアプリケーションです。「今日、何を食べた？」や「体調は大丈夫？」などがそれに当たります。お互い住んでいるところが離れていてなかなか会えない日が続いていても、何気ないやりとりがあれば、不安や寂しさが解消されるのではないかと思い、制作しました。"),
      new Help("トップ画面", "ピンピンの会話は操作性を考え簡易化せれており、質問者と回答者の２つに分類されます。[名]と書いたテキストフィールドに任意の名前を書いて、回答者でしたらお年寄りの画像、質問者でしたら若者の画像をクリックしてください。なお、デモ用アプリケーションとして、\"bot\"という名前を入力し入室すると、ボットがたまに質問し、質問に答えてくれます。"),
      new Help("質問者機能", "質問者は、質問内容の他に、回答項目も入力します。簡単のため、予めよく使うYes/Noは用意して有りますが、「カスタム」をタップすることで任意の回答項目を設定することもできます。入力が完了したら「送信ボタン」をタップしてください。"),
      new Help("回答者機能", "回答者の場合は、ボタンをクリックした直後、アプリは消えます。質問者が質問したタイミングでアクティビティが表示されます。回答者は回答ボタンをタップするだけで、質問者に回答を返すことができます。")};

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final List<String> titles = new ArrayList<String>();
    for (int i = 0; i < helps.length; i++) {
      titles.add(helps[i].title);
    }
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
    setListAdapter(adapter);
    getListView().setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        showHelp(arg2);
      }
    });
  }

  void showHelp(int index) {
    final String content = helps[index].content;
    final String title = helps[index].title;
    new AlertDialog.Builder(this).setMessage(content).setTitle(title).create().show();
  }

  static class Help {

    String title;
    String content;

    /**
     * {@link Help}オブジェクトを構築します。
     * 
     * @param title
     * @param content
     */
    Help(String title, String content) {
      super();
      this.title = title;
      this.content = content;
    }

  }

}
