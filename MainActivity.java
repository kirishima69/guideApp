package com.example.kiris.kaisetsuapp3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {


    // GlobalValue gv = new GlobalValue();

    /* url */
    private String urlIndex = "http://10.0.2.2:8080/request/topPage";

    /* テキストビュー＆テーブルレイアウトのオブジェクトを定義 */
    private TextView textView_Title;
    private TextView textView_Sentence;
    private TextView textView_TitleListName;
    private CheckBox checkBox_Knowledge;
    private ViewGroup vgtl;
    // Neo4jから獲得したJSONデータを格納するためのハッシュ変数
    private HashMap<String, Integer> textNeo4jHashMap = new HashMap<>();
    // 解説データに含まれた知識ワードを格納するためのリスト変数
    private ArrayList<String> elmArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Layout部品のインスタンスを作成 */
        // テキストビュー（解説タイトル・本文）のインスタンスを作成
        textView_Title = (TextView)findViewById(R.id.textView_Title);
        textView_Sentence = (TextView)findViewById(R.id.textView_Sentence);
        textView_TitleListName = (TextView)findViewById(R.id.textView_TitleListName);
        // テーブルレイアウトのインスタンスのインスタンスを作成
        vgtl = (ViewGroup)findViewById(R.id.TableLayout_Elements);

        /* 非同期通信(@TopPage) */
        // AsyncNetworkTask インスタンスを作成
        AsyncNetworkTask httpTask = new AsyncNetworkTask(new AsyncNetworkTask.AsyncTaskCallback() {
            @Override
            public void preExecute() {
                System.out.println("preExecute: ");
            }

            @Override
            public void cancel() {
                System.out.println("cancel: " );
            }

            @Override
            public void postExecute(String result) {
                System.out.println("postExecute: " + result);   // 2017/06/29  1:11 　result の中身はDBから取得したデータ
                // gv.result = result;
                // System.out.println("Global: " + gv.result);
//                getResultHashMap(result);
                textNeo4jHashMap = getResultHashMap(result);

                /* レイアウトへ出力（解説タイトル・解説本文・解説の構成知識） */
                for(String key : textNeo4jHashMap.keySet()){
                    switch(key){
                        case "title":
                            // 解説タイトル
                            textView_Title.setText("解説タイトル) " + String.valueOf(textNeo4jHashMap.get("title")));
                            break;
                        case "sentence":
                            // 解説本文
                            textView_Sentence.setText("本文) " + String.valueOf(textNeo4jHashMap.get("sentence")));
                            break;
                        default:
                            // 解説の構成知識
                            if(textNeo4jHashMap.get(key) != null){
                                elmArray.add(String.valueOf(textNeo4jHashMap.get(key)));
                            }
                    }
                }
                // 構成知識をテーブルに展開
                int i = 0;
                for (String elm : elmArray) {
                    // 行を追加
                    getLayoutInflater().inflate(R.layout.row_knowledge, vgtl);
                    // 文字設定
                    TableRow tr = (TableRow) vgtl.getChildAt(i);
                    String str = String.format(Locale.getDefault(), "%2d: " + (String)elm, i);
                    ((TextView)(tr.getChildAt(0))).setText(str);

                    /*  */
                    System.out.println("child: " + tr.getChildCount());
                    ((CheckBox)(tr.getChildAt(0))).setId(R.id.checkBox_01);

                    i=i+1;
                }
                // リストタイトルを表示

            }

            @Override
            public void progressUpdate(int progress) {
                System.out.println("progressUpadate: ");
            }
        });
        // 非同期通信(@TopPage)の実行
        httpTask.execute(urlIndex);
        /* 非同期通信終了 */

        /* 知りたい知識の選択 */
        // チェックボックスのインスタンスを作成
//        checkBox_Knowledge = (CheckBox)findViewById(R.id.checkBoxKnowledge);
/*        // チェックボックスに
        checkBox_Knowledge.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked){
                        Toast.makeText(MainActivity.this,
                                isChecked ? "オン" : "オフ",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        */
    }

    /* 知りたい知識の選択＆解説要求フェーズ */



    // JsonデータをHashMapに変換する．．
    private HashMap<String, Integer> getResultHashMap(String result){
        System.out.println("result: " +  result);
        Gson gson = new Gson();
        return gson.fromJson(result, HashMap.class);
    }

}
