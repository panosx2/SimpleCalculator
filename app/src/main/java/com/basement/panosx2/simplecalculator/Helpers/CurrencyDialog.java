package com.basement.panosx2.simplecalculator.Helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basement.panosx2.simplecalculator.MainActivity;
import com.basement.panosx2.simplecalculator.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyDialog {
    private static Context context;
    private static List<CurrencyObject> currencies = new ArrayList<>();
    private static CurrencyAdapter currencyAdapter;
    public static AlertDialog dialog;

    public CurrencyDialog(Context context, LayoutInflater layoutInflater, String origin) {
        this.context = context;

        currencyAdapter = new CurrencyAdapter(currencies, origin);

        if (currencies.isEmpty()) getCurrencies();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setMessage("Select currency");
        View dialogView = layoutInflater.inflate(R.layout.dialog_currency, null);
        dialogBuilder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(currencyAdapter);

        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void getCurrencies() {
        String url = "http://data.fixer.io/api/latest?access_key=a300bb2d56f6b4525975538e2fb711b2";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        if (responseObject.getBoolean("success")) {
                            JSONObject rateObject = responseObject.getJSONObject("rates");
                            JSONArray rates = rateObject.names();

                            for (int i = 0; i < rates.length(); i++) {
                                currencies.add(new CurrencyObject(rates.getString(i), rateObject.getDouble(rates.getString(i))));
                                Log.d("CurrencyDialog", rates.getString(i) + " = " + rateObject.getDouble(rates.getString(i)));

                                currencyAdapter.notifyDataSetChanged();
                            }

                            MainActivity.currencies = currencies;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();

                return map;
            }
        };
        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }
}