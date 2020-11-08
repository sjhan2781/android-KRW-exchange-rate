package com.example.exchange_rate.ui.main.adapter;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exchange_rate.model.Currency;
import com.example.exchange_rate.model.HashArrayMap;
import com.example.exchange_rate.R;

import java.util.ArrayList;

public class CurrencyRecyclerAdapter
        extends RecyclerView.Adapter<CurrencyRecyclerAdapter.CurrencyViewHolder>
        implements Filterable {
    private ArrayList<Currency> currencyList = new ArrayList<>();
    private ArrayList<Currency> filteredList = new ArrayList<>();
    private HashArrayMap<String, Currency> currencyHashMap = new HashArrayMap<>();

    public interface OnItemClickListener {
        void onItemClick(ArrayList<Currency> currency);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CurrencyViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_currency, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
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
                String filter = constraint.toString();

                if (filter.isEmpty()) {
                    filteredList = currencyList;
                } else {
                    ArrayList<Currency> filteringList = new ArrayList<>();
                    for (Currency currency : currencyList) {
                        if (currency.getCode().contains(filter.toUpperCase())) {
                            filteringList.add(currency);
                        }
                        else if(currency.getName().contains(constraint)){
                            filteringList.add(currency);
                        }
                    }
                    filteredList = filteringList;
                }
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
        this.filteredList = currencyList;
        notifyDataSetChanged();
    }

    public void setCurrencyHashMap(HashArrayMap<String, Currency> currencyHashMap) {
        this.currencyHashMap = currencyHashMap;
    }

    @Override
    public long getItemId(int position) {
        return currencyList.get(position).hashCode();
    }

    class CurrencyViewHolder extends RecyclerView.ViewHolder {

        public CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(final Currency currency) {
            TextView code, name, buy, sold, sign;
            ImageView currencyImage;

            sign = itemView.findViewById(R.id.currency_sign);
            code = itemView.findViewById(R.id.currency_code);
            name = itemView.findViewById(R.id.currency_name);
            buy = itemView.findViewById(R.id.currency_buy);
            sold = itemView.findViewById(R.id.currency_sold);
            currencyImage = itemView.findViewById(R.id.currency_image);

            code.setText(currency.getCode());
            name.setText(currency.getName());
            buy.setText(currency.getSending());
            sold.setText(currency.getReceiving());

            String lowerCode = currency.getCode().substring(0, 3).toLowerCase();

            int resId = itemView.getResources().getIdentifier(lowerCode, "drawable", itemView.getContext().getPackageName());
            int stringId = itemView.getResources().getIdentifier(lowerCode + "_sign", "string", itemView.getContext().getPackageName());

            currencyImage.setImageResource(resId);
            sign.setText(stringId);

            itemView.setOnClickListener(new View.OnClickListener() {
                long mLastClickTime = 0;

                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    onItemClickListener.onItemClick(currencyHashMap.get(currency.getCode()));
                }
            });
        }
    }
}
