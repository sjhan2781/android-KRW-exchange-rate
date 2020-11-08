package com.example.exchange_rate.ui.currency_detail;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exchange_rate.model.Currency;
import com.example.exchange_rate.model.HashArrayMap;
import com.example.exchange_rate.R;
import com.example.exchange_rate.util.XAxisValueFormatter;
import com.example.exchange_rate.ui.currency_detail.adapter.CurrencyDayRecyclerAdapter;
import com.example.exchange_rate.ui.currency_detail.viewmodel.DetailViewModel;
import com.example.exchange_rate.ui.main.viewmodel.MainViewModel;
import com.example.exchange_rate.ui.main.viewmodel.MainViewModelFactory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CurrencyDetailActivity extends AppCompatActivity {
    LineChart lineChart;
    RecyclerView recyclerView;
    Button search_start, search_end;
    DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    ImageButton refreshButton;

    MainViewModel mainViewModel;
    DetailViewModel detailViewModel;

    AppCompatDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_detail);

        setLoadingDialog();
        progressDialog.show();

        final ArrayList<Currency> currencyList = getIntent().getParcelableArrayListExtra("currency");

        mainViewModel = new ViewModelProvider(this, new MainViewModelFactory()).get(MainViewModel.class);
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        if (currencyList != null) {
            setTitle(currencyList.get(0).getName() + "(" + currencyList.get(0).getCode() + ")");
            initUI(currencyList);
            detailViewModel.currencyCode = currencyList.get(0).getCode();
        }

//        detailViewModel.currencyList.setValue(mainViewModel.currencyHashArrayMap.getValue().get(detailViewModel.currencyCode));

        mainViewModel.currencyHashArrayMap.observe(this, new Observer<HashArrayMap<String, Currency>>() {
            @Override
            public void onChanged(HashArrayMap<String, Currency> stringCurrencyHashArrayMap) {
//                currencyList = stringCurrencyHashArrayMap.get(currencyCode);
                detailViewModel.currencyList.setValue(stringCurrencyHashArrayMap.get(detailViewModel.currencyCode));
            }
        });

        detailViewModel.currencyList.observe(this, new Observer<ArrayList<Currency>>() {
            @Override
            public void onChanged(ArrayList<Currency> currencies) {
                setChart(currencies);
                detailViewModel.adapter.setCurrency(currencies);
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }

    void setLoadingDialog(){
        progressDialog = new AppCompatDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_loading);
        ((TextView) progressDialog.findViewById(R.id.loading_text)).setText("잠시만 기다려주세요..");
    }

    void initUI(ArrayList<Currency> currency) {
        lineChart = findViewById(R.id.chart1);
        recyclerView = findViewById(R.id.currency_detail_recycler);
        search_start = findViewById(R.id.search_start);
        search_end = findViewById(R.id.search_end);
        refreshButton = findViewById(R.id.refresh_button);

        Objects.requireNonNull(detailViewModel.startDate.getValue()).setTime(currency.get(0).getDate());
        Objects.requireNonNull(detailViewModel.endDate.getValue()).setTime(currency.get(currency.size() - 1).getDate());

        search_start.setText(getStringDate(currency.get(0).getDate()));
        search_end.setText(getStringDate(currency.get(currency.size() - 1).getDate()));


        CurrencyDayRecyclerAdapter adapter = detailViewModel.adapter;
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        makeDatePickerDialog();

        setChart(currency);

        setUpListeners();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    void setUpListeners(){
        final RotateAnimation rAnim = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshButton.startAnimation(rAnim);
                String startDayStr = search_start.getText().toString();
                String endDayStr = search_end.getText().toString();

                if (getStringDate(detailViewModel.startDate.getValue()).compareTo(startDayStr)
                        + getStringDate(detailViewModel.endDate.getValue()).compareTo(endDayStr) == 0) {
                    Toast.makeText(getApplicationContext(), "기준일을 변경해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        detailViewModel.startDate.getValue().setTime(parseStringToDate(startDayStr));
                        detailViewModel.endDate.getValue().setTime(parseStringToDate(endDayStr));

                        progressDialog.show();
                        reloadData(detailViewModel.startDate.getValue(), detailViewModel.endDate.getValue());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        search_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });
        search_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });
    }

    void reloadData(Calendar start, Calendar end) {
        mainViewModel.loadCurrency(start, end);
    }

    void makeDatePickerDialog() {
        final Calendar pickDate = Calendar.getInstance();
        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                pickDate.set(year, month, dayOfMonth);
                search_start.setText(getStringDate(pickDate.getTime()));
                endDatePickerDialog.getDatePicker().setMinDate(pickDate.getTimeInMillis());
            }
        },
                detailViewModel.startDate.getValue().get(Calendar.YEAR),
                detailViewModel.startDate.getValue().get(Calendar.MONTH),
                detailViewModel.startDate.getValue().get(Calendar.DATE)
        );

        startDatePickerDialog.getDatePicker().setMaxDate(Objects.requireNonNull(detailViewModel.endDate.getValue()).getTimeInMillis());

        endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                pickDate.set(year, month, dayOfMonth);
                search_end.setText(getStringDate(pickDate.getTime()));
                startDatePickerDialog.getDatePicker().setMaxDate(pickDate.getTimeInMillis());
            }
        },
                detailViewModel.endDate.getValue().get(Calendar.YEAR),
                detailViewModel.endDate.getValue().get(Calendar.MONTH),
                detailViewModel.endDate.getValue().get(Calendar.DATE)
        );

        endDatePickerDialog.getDatePicker().setMinDate(detailViewModel.startDate.getValue().getTimeInMillis());
        endDatePickerDialog.getDatePicker().setMaxDate(Objects.requireNonNull(detailViewModel.today.getValue()).getTimeInMillis());
    }

    String getStringDate(Date date) {
        return detailViewModel.mFormat.format(date);
    }

    String getStringDate(Calendar calendar) {
        return detailViewModel.mFormat.format(calendar.getTime());
    }

    Date parseStringToDate(String str) throws ParseException {
        return detailViewModel.mFormat.parse(str);
    }

    void setChart(ArrayList<Currency> currencyList) {
        List<Entry> buyEntries = new ArrayList<>();
        List<Entry> soldEntries = new ArrayList<>();

        for (int i = 0; i < currencyList.size(); i++) {
            if (currencyList.get(i).getReceivingFloat() != 0) {
                soldEntries.add(new Entry(i, Float.parseFloat(currencyList.get(i).getReceiving())));
                buyEntries.add(new Entry(i, Float.parseFloat(currencyList.get(i).getSending())));
            }
        }

        LineDataSet buyDataSet = new LineDataSet(buyEntries, "사실 때");
        buyDataSet.setLineWidth(2);
        buyDataSet.setCircleRadius(6);
        buyDataSet.setValueTextSize(10f);
        buyDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        buyDataSet.setCircleHoleColor(Color.BLUE);
        buyDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        buyDataSet.setDrawCircleHole(true);
        buyDataSet.setDrawCircles(true);
        buyDataSet.setDrawHorizontalHighlightIndicator(false);
        buyDataSet.setDrawHighlightIndicators(false);
        buyDataSet.setDrawValues(false);

        LineDataSet soldDataSet = new LineDataSet(soldEntries, "파실 때");
        soldDataSet.setLineWidth(2);
        soldDataSet.setCircleRadius(6);
        soldDataSet.setValueTextSize(16f);
        soldDataSet.setCircleColor(Color.parseColor("#FF0000"));
        soldDataSet.setCircleHoleColor(Color.BLUE);
        soldDataSet.setColor(Color.parseColor("#FF0000"));
        soldDataSet.setDrawCircleHole(true);
        soldDataSet.setDrawCircles(true);
        soldDataSet.setDrawHorizontalHighlightIndicator(false);
        soldDataSet.setDrawHighlightIndicators(false);
        soldDataSet.setDrawValues(false);

        LineData lineData = new LineData();
        lineData.addDataSet(buyDataSet);
        lineData.addDataSet(soldDataSet);
        lineChart.setData(lineData);

        lineChart.setScaleMinima(1f, 1f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(10, 24, 0);
        xAxis.setTextSize(10f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(currencyList.size() - 1);
        xAxis.setValueFormatter(new XAxisValueFormatter(currencyList.get(0).getDate()));

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        Legend legend = lineChart.getLegend();
        legend.setTextSize(12f);
        legend.setFormLineWidth(50f);
        legend.setWordWrapEnabled(true);
        legend.setXEntrySpace(13f);

        lineChart.setDoubleTapToZoomEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.fitScreen();
        lineChart.setDescription(description);
//        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.setExtraRightOffset(30f);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
