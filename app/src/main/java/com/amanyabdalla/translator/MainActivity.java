package com.amanyabdalla.translator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText et;
    TextView txt;
    TryLanguage tr;
    ListView listView;
    Switch sw;
    String language;
    String langForAn;

    String transWordForHistory;
    String wordForHistory;
    SQLDataBase sqlDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.textEditor);

        txt = (TextView) findViewById(R.id.checkThread);

        tr = new TryLanguage();
        listView = (ListView)findViewById(R.id.listView);
        sw = (Switch)findViewById(R.id.switch1);
        sw.setChecked(false);
        language = "en-ar";
        langForAn="ar";
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    language = "ar-en";
                    langForAn = "en";
                }else {
                    language = "en-ar";
                    langForAn="ar";
                }
                //  Log.d("Используемый язык",language);
            }
        });

        sqlDataBase = new SQLDataBase(this);
    }

    public void pushButt(View view) {
        Toast.makeText(getApplicationContext(),"please wait...",Toast.LENGTH_SHORT).show();
        TryThread tr = new TryThread();

        tr.execute(et.getText().toString());
    }



    public void toTheBest(View view) {
        if(!et.getText().toString().isEmpty()&&!txt.getText().toString().isEmpty()){
            placeBest(et.getText().toString(),txt.getText().toString());
        }else{
            Log.d("Нечего добавлять","сударь-с");
        }
    }




    public class TryThread extends AsyncTask<String, Void, String> {
        ArrayAdapter<String> arrar;
        String s = et.getText().toString();
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String a = tr.translatedWord(langForAn, params[0]);


            /**
             * сюда помещаем итоги
             * того, что было переведено в
             * гетОлЭрэйс или метод по установке вида листа
             */


            GetAllListArrays lll = new GetAllListArrays();
            try {
                lll.handParse(lll.translate123(language, s));
                Log.d("BUMP:", lll.getTranslatedWords.toString());

                String [] transl = new String[lll.getTranslatedWords.size()];
                lll.getTranslatedWords.toArray(transl);

                String [] transExtr = new String[lll.getTranslatedExp.size()];
                lll.getTranslatedExp.toArray(transExtr);

                String [] unTrans = new String[lll.getUnTranslatedExp.size()];
                lll.getUnTranslatedExp.toArray(unTrans);

                String [] correct = new String[transExtr.length];
                if(transExtr.length<transl.length) {
                    for (int i = 0; i < transExtr.length; i++) {
                        correct[i] = transl[i];
                    }
                    transl = correct;
                }
                String [] allTrans = new String[0];
                try {
                    lll.getAllTrans(lll.translate123(language, s));
                    allTrans = new String [lll.getTransForNonPrevious.size()];
                    lll.getTransForNonPrevious.toArray(allTrans);
                }catch (RuntimeException e) {

                }


                MyOwnArrayAdapter myOwnArrayAdapter = new MyOwnArrayAdapter(getBaseContext(),transl,transExtr,unTrans);
                AnotherAdapter anotherAdapter = new AnotherAdapter(getBaseContext(),allTrans);
                //нужно создать условие. если будут пусты, то юзать другой адаптер!

                if(transl.length == 0&&allTrans.length!=0){
                    arrar = anotherAdapter;
                }else {
                    arrar = myOwnArrayAdapter;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }






            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(arrar);
            txt.setText(s);
            wordForHistory = et.getText().toString();//это для истории и SQL
            transWordForHistory = s;//это слова для истории
            if(!wordForHistory.equals(transWordForHistory)&&!wordForHistory.isEmpty()&&!transWordForHistory.isEmpty()) {
                placeHistory(wordForHistory, transWordForHistory);
            }else{
                Log.d("Упс!","И зачем два одинаковых в истрию?");
            }
        }
    }//на вход подаю текст, который же получаю снова

    public void placeHistory(String untrans, String trans){
        //это метод, принимающий переводы для истории
        //Log.d("GOVNO",untrans);
        // Log.d("GOVNO",trans);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = sqlDataBase.getWritableDatabase();
        cv.put("word",untrans);
        cv.put("wordtransed",trans);
        long rowID = db.insert("mytable",null,cv);
        Log.d("Что имеем?", "row inserted, ID = " + rowID);
        //добавить в СКУЛЬ ДВЕ КОЛОНКИ ДЛЯ ИЗБРАННОГО, ИНАЧЕ ОДНО И ТО ЖЕ ГОВНО БУДЕТ
    };
    public void placeBest(String untrans, String trans){
        //Log.d("SOS",untrans);
        //Log.d("Имено так",trans);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = sqlDataBase.getWritableDatabase();
        cv.put("word",untrans);
        cv.put("wordtransed", trans);
        long rowID = db.insert("mytable1",null,cv);
        Log.d("Что имеем?", "row inserted, ID = " + rowID);


    };
}
