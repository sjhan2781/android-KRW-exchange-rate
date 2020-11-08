package com.example.exchange_rate.ui.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exchange_rate.model.Currency;
import com.example.exchange_rate.model.HashArrayMap;
import com.example.exchange_rate.R;
import com.example.exchange_rate.ui.currency_detail.CurrencyDetailActivity;
import com.example.exchange_rate.ui.main.adapter.CurrencyRecyclerAdapter;
import com.example.exchange_rate.ui.main.viewmodel.MainViewModel;
import com.example.exchange_rate.ui.main.viewmodel.MainViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CurrencyRecyclerAdapter adapter;
    AppCompatDialog progressDialog;

    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLoadingDialog();
        progressDialog.show();

        mainViewModel = new ViewModelProvider(this, new MainViewModelFactory()).get(MainViewModel.class);

        recyclerView = findViewById(R.id.currency_recycler);

        adapter = mainViewModel.currencyRecyclerAdapter;
        adapter.setOnItemClickListener(new CurrencyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ArrayList<Currency> currency) {
                Intent intent = new Intent(getApplicationContext(), CurrencyDetailActivity.class);
                intent.putParcelableArrayListExtra("currency", currency);
                startActivity(intent);
            }
        });
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);


        mainViewModel.currencyHashArrayMap.observe(this, new Observer<HashArrayMap<String, Currency>>() {
            @Override
            public void onChanged(HashArrayMap<String, Currency> stringCurrencyHashArrayMap) {
                adapter.setCurrencyHashMap(stringCurrencyHashArrayMap);
            }
        });

        mainViewModel.currencyList.observe(this, new Observer<ArrayList<Currency>>() {
            @Override
            public void onChanged(ArrayList<Currency> currencies) {
                adapter.setCurrency(currencies);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

        loadCurrency();
    }

    public void loadCurrency() {
        Calendar today = Calendar.getInstance();
        Calendar startDay = Calendar.getInstance();
        startDay.add(Calendar.DATE, -7);

        mainViewModel.loadCurrency(startDay, today);
    }

    void setLoadingDialog() {
        progressDialog = new AppCompatDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_loading);
        ((TextView) progressDialog.findViewById(R.id.loading_text)).setText("화폐 정보를 불러오는 중 입니다..");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.byCode:
                item.setChecked(true);
                Currency.order = Currency.filter.CODE;
                break;
            case R.id.byName:
                item.setChecked(true);
                Currency.order = Currency.filter.NAME;
                break;

            case R.id.byBuy:
                item.setChecked(true);
                Currency.order = Currency.filter.BUY;
                adapter.notifyDataSetChanged();
                break;
            case R.id.bySold:
                item.setChecked(true);
                Currency.order = Currency.filter.SOLD;
                break;
        }

        Collections.sort(mainViewModel.currencyList.getValue());
        adapter.notifyDataSetChanged();

        adapter.notifyDataSetChanged();
        recyclerView.invalidate();

        return super.onOptionsItemSelected(item);
    }
}
