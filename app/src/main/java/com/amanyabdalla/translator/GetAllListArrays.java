package com.amanyabdalla.translator;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class GetAllListArrays {
    //этот класс создан, дабы собрать все листы
    //для адаптера
    static List<String>getTranslatedWords = new ArrayList<String>();//вариации синонимов
    static List<String>getUnTranslatedExp = new ArrayList<String>();//непереведенные экстра
    static List<String>getTranslatedExp = new ArrayList<String>();//переведенные экстра
    static List<String> getTransForNonPrevious = new ArrayList<String>();//на случай пустых предыдущих

    public static String translate123(String lang, String input) throws IOException {
        String urlStr = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20170324T082045Z.5089076809ee28a9.ffa2a8f71e0c7c4a629cfa04aa38975a721fdaa6&";
        URL urlObj = new URL(urlStr);
        HttpsURLConnection connection = (HttpsURLConnection)urlObj.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes("lang="+ lang + "&text=" + URLEncoder.encode(input, "UTF-8"));
        InputStream response = connection.getInputStream();
        String json = null;
        Scanner scn = new Scanner(response);
        while(scn.hasNextLine()){
            json = scn.nextLine();
        }
        String translated = json;
        //вот это вот заморочка для русского. Иначе эту шляпу делать не надо
        //хотя она не мешает и с ангельским
        byte ptext[] = translated.getBytes();
        String encodedStr = new String(ptext, "UTF-8");
        return encodedStr;
    }

    public static void handParse(String link) throws IOException{
      List<String>textList = new ArrayList<String>();
        if(link.contains("\"syn\"")){
            String tr = link.substring(link.indexOf("\"syn\""));
            tr = tr.substring(0,tr.indexOf("\"mean\""));
            //получили
            //переводы
            for(int i = 0;i<tr.length()-8;i++){
                if(tr.substring(i, i+6).equals("\"text\"")){
                    String s = tr.substring(i+8);
                    s = s.substring(0, s.indexOf("\""));
                    ;				textList.add(s);
                }
            };
        }else{
            getAllTrans(translate123("en-ru","Hi!"));
        }
        List<String>exList = new ArrayList<String>();
        List<String>exListTrans = new ArrayList<String>();
        List<String>nonTrans = new ArrayList<String>();
        if(link.contains("\"ex\"")){
            String ex = link.substring(link.indexOf("\"ex\""));
            ex = ex.substring(0, ex.indexOf("\"mean\""));
            //получили экстра данные
            for(int i = 0;i<ex.length()-8;i++){
                if(ex.substring(i,i+6).equals("\"text\"")){
                    String s = ex.substring(i+8);
                    s = s.substring(0, s.indexOf("\""));
                    exList.add(s);
                }

            }
            for(int i=0;i<exList.size();i++){
                if(i%2!=0){
                    exListTrans.add(exList.get(i));
                }else{
                    nonTrans.add(exList.get(i));
                }

            };
        };

        //System.out.println(textList);
        //System.out.println(exListTrans);
        //System.out.println(nonTrans);

        getTranslatedWords = textList;
        getUnTranslatedExp = nonTrans;
        getTranslatedExp = exListTrans;

        //и попробовать i-2!=tr
        //поделить ёбаную входящую строку на несколько строк. типа ex или tr или ещё что.
        //оттуда плясать. как же я заебался, блять
    }

    public static void getAllTrans(String link){
        link = link.substring(link.indexOf("\"tr\":[{\""));
        List<String>translatedWords = new ArrayList<String>();
        for(int i = 6;i<link.length()-6;i++){
            //от шестого знака, дабы не было конфликта при минусе,
            //ведь начинаю с тр
            if(link.substring(i, i+6).equals("\"text\"")&&!link.substring(i-6, i).equals("an\":[{")&&!link.substring(i-3, i).equals("},{")&&!link.substring(i-6, i).equals("an\":[{")&&!link.substring(i-6, i).equals("ex\":[{")){
                String s = link.substring(i+8);
                s = s.substring(0, s.indexOf("\""));
                translatedWords.add(s);
            }

        };
        System.out.println(translatedWords);
        getTransForNonPrevious = translatedWords;
        //тут я просто получу все переводы на случай, если всё предыдущее пусто и вообще пиздец
        //например, разбираемся со словом enough и hi
    };
}
