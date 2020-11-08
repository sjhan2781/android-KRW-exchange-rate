package com.example.exchange_rate.ui.currency_detail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exchange_rate.model.Currency;
import com.example.exchange_rate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CurrencyDayRecyclerAdapter
        extends RecyclerView.Adapter<CurrencyDayRecyclerAdapter.CurrencyDayViewHolder>
        implements Filterable {
    private ArrayList<Currency> currencyList = new ArrayList<>();
    private ArrayList<Currency> filteredList = new ArrayList<>();

    @NonNull
    @Override
    public CurrencyDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CurrencyDayViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_currency_day, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyDayViewHolder holder, int position) {
        holder.bind(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Currency> filteringList = new ArrayList<>();
                for (Currency currency : currencyList) {
                    if (currency.getReceivingFloat() != 0) {
                        filteringList.add(currency);
                    }
                }
                filteredList = filteringList;
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<Currency>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setCurrency(ArrayList<Currency> currencyList) {
        this.currencyList = currencyList;
        getFilter().filter("");
        notifyDataSetChanged();
    }

    public ArrayList<Currency> getCurrencyList() {
        return currencyList;
    }

    @Override
    public long getItemId(int position) {
        return currencyList.get(position).hashCode();
    }

    class CurrencyDayViewHolder extends RecyclerView.ViewHolder {

        public CurrencyDayViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(final Currency currency) {
            TextView day, buy, sold;
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd");

            day = itemView.findViewById(R.id.day);
            buy = itemView.findViewById(R.id.day_buy);
            sold = itemView.findViewById(R.id.day_sold);

            day.setText(mFormat.format(currency.getDate()));
            buy.setText(currency.getSending());
            sold.setText(currency.getReceiving());

        }
    }
}
