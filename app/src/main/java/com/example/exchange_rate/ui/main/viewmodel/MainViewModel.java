package com.example.exchange_rate.ui.main.viewmodel;

import android.icu.util.Calendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.exchange_rate.util.ApiConnector;
import com.example.exchange_rate.model.Currency;
import com.example.exchange_rate.model.HashArrayMap;
import com.example.exchange_rate.ui.main.adapter.CurrencyRecyclerAdapter;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    ApiConnector apiConnector = new ApiConnector();
    public MutableLiveData<ArrayList<Currency>> currencyList = new MutableLiveData<>();
    public MutableLiveData<HashArrayMap<String, Currency>> currencyHashArrayMap = new MutableLiveData<>();
    public CurrencyRecyclerAdapter currencyRecyclerAdapter = new CurrencyRecyclerAdapter();
    private static MainViewModel mainViewModel;

    public MainViewModel() {

    }

    public static synchronized MainViewModel getInstance() {
        if (mainViewModel == null) {
            mainViewModel = new MainViewModel();
            return mainViewModel;
        }
        return mainViewModel;
    }

    public void loadCurrency(Calendar start, Calendar end) {
        final int dayToMillis = 24*60*60*1000;
        final long dateDiff = end.getTimeInMillis() - start.getTimeInMillis() ;

        Thread apiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int term = (int) (dateDiff / dayToMillis) + 1;
                Calendar calendar = Calendar.getInstance();
                ArrayList<Currency> currency = new ArrayList<>();
                ArrayList<Currency> current = new ArrayList<>();

                calendar.add(Calendar.DATE, term * -1);

                apiConnector.currencyHashMap = new HashArrayMap<>();

                for (int i = 0; i < term; i++) {
                    calendar.add(calendar.DATE, 1);
                    currency = apiConnector.loadApi(calendar.getTime());

                    if (!currency.isEmpty()) {
                        current = currency;
                    } else {
                        for (String code : apiConnector.currencyCodeSet) {
                            Currency noData = new Currency();
                            noData.setDate(calendar.getTime());
                            apiConnector.currencyHashMap.put(code, noData);
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for(int i = 0; i < current.size(); i++){
                    if(current.get(i).getCode().equals("KRW")){
                        current.remove(i);
                        current.trimToSize();
                        break;
                    }
                }

                currencyList.postValue(current);
                currencyHashArrayMap.postValue(apiConnector.currencyHashMap);
            }
        });

        apiThread.start();
    }

}


