package com.example.exchange_rate.ui.currency_detail.viewmodel;

import android.icu.util.Calendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.exchange_rate.model.Currency;
import com.example.exchange_rate.ui.currency_detail.adapter.CurrencyDayRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DetailViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Currency>> currencyList = new MutableLiveData<>();
    public SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd");

    public CurrencyDayRecyclerAdapter adapter;

    public MutableLiveData<Calendar> today = new MutableLiveData<>(Calendar.getInstance());
    public MutableLiveData<Calendar> startDate = new MutableLiveData<>(Calendar.getInstance());
    public MutableLiveData<Calendar> endDate = new MutableLiveData<>(Calendar.getInstance());

    public String currencyCode = "";

    public DetailViewModel(){
        adapter = new CurrencyDayRecyclerAdapter();

    }


}
