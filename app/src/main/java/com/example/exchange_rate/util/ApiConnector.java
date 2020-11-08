package com.example.exchange_rate.util;

import com.example.exchange_rate.model.Currency;
import com.example.exchange_rate.model.HashArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class ApiConnector {
    String serviceKey = "4UFZNLaDwWlsCYrFCvV2jAGWTTdJ1j67";
    public HashArrayMap<String, Currency> currencyHashMap = new HashArrayMap<>();
    public HashSet<String> currencyCodeSet = new HashSet<>();

    public ApiConnector() {
    }


    public ArrayList<Currency> loadApi(Date date) {
        ArrayList<Currency> currencyList = new ArrayList<>();
        StringBuilder urlBuilder = new StringBuilder("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON"); /*URL*/
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");

        try {
            urlBuilder.append("?" + URLEncoder.encode("authkey", "UTF-8") + "=" + serviceKey); /*인증 키*/
            urlBuilder.append("&" + URLEncoder.encode("searchdate", "UTF-8") + "=" + URLEncoder.encode(mFormat.format(date), "UTF-8")); /*검색날짜*/
            urlBuilder.append("&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode("AP01", "UTF-8")); /*검색 유형*/


            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            InputStream is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }

            currencyList = jsonParsing(buffer.toString(), date);

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currencyList;
    }

    ArrayList<Currency> jsonParsing(String json, Date date) {
        ArrayList<Currency> currencyList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currencyObject = jsonArray.getJSONObject(i);

                Currency currency = new Currency();

                currency.setCode(currencyObject.getString("cur_unit"));
                currency.setName(currencyObject.getString("cur_nm"));
                currency.setReceiving(currencyObject.getString("ttb"));
                currency.setSending(currencyObject.getString("tts"));
                currency.setDate(date);

                currencyList.add(currency);

                currencyCodeSet.add(currency.getCode());
                currencyHashMap.put(currency.getCode(), currency);
            }
//            for (int i = 0; i < currencyList.size(); i++)
//                Log.d("currency", currencyList.get(i).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currencyList;
    }

}
