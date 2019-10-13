package com.basement.panosx2.simplecalculator.Helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.basement.panosx2.simplecalculator.MainActivity;
import com.basement.panosx2.simplecalculator.R;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    private static List<CurrencyObject> currencies;
    private static String origin;

    public CurrencyAdapter(List<CurrencyObject> currencies, String origin) {
        this.currencies = currencies;
        this.origin = origin;
    }

    @Override
    public CurrencyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency, parent, false);
        return new CurrencyAdapter.ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
        }
    }

    @Override
    public void onBindViewHolder(final CurrencyAdapter.ViewHolder holder, final int position) {
        CurrencyObject currencyObject = currencies.get(position);

        holder.name.setText(currencyObject.getCurrency());

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin.equals("from")) {
                    MainActivity.fromView.setText(holder.name.getText());
                    MainActivity.position1 = position;
                }
                else {
                    MainActivity.toView.setText(holder.name.getText());
                    MainActivity.position2 = position;
                }

                CurrencyDialog.dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    public long getItemId(int position) {
        return position;
    }
}
