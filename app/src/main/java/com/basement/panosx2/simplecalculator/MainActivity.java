package com.basement.panosx2.simplecalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basement.panosx2.simplecalculator.Helpers.CurrencyDialog;
import com.basement.panosx2.simplecalculator.Helpers.CurrencyObject;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static Context context;

    private TextView info;
    private RelativeLayout converterLayout;

    private Button clear, backspace, convert, dot, result;

    private Button change, ok;
    public static Button fromView, toView;

    private static String first = "", second = "", operator = "";

    public static List<CurrencyObject> currencies;
    public static int position1, position2;

    private int INTERNET_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        info = findViewById(R.id.info);
        converterLayout = findViewById(R.id.converterLayout);

        currencies = new ArrayList<>();

        change = findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromView.getText().length() == 3 || toView.getText().length() == 3) {
                    String temp = fromView.getText().toString();
                    fromView.setText(toView.getText());
                    toView.setText(temp);
                    int tempPos = position1;
                    position1 = position2;
                    position2 = tempPos;
                }
            }
        });

        ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromView.getText().length() == 3 && toView.getText().length() == 3) {
                    if (info.getText().length() > 0) {
                        String lastChar = info.getText().subSequence(info.getText().length()-1, info.getText().length()).toString();
                        if (!lastChar.equals("×") &&
                                !lastChar.equals("÷") &&
                                !lastChar.equals("+") &&
                                !lastChar.equals("-") &&
                                !lastChar.equals(".")) {
                            if (!operator.isEmpty() && second.length() > 0) result.performClick();

                            double d_from = currencies.get(position1).getRate();
                            double d_to = currencies.get(position2).getRate();

                            if (d_from != d_to) {
                                BigDecimal inEuro = BigDecimal.valueOf(Double.parseDouble(info.getText().toString())).divide(BigDecimal.valueOf(d_from), 10, RoundingMode.HALF_UP);
                                BigDecimal result = BigDecimal.valueOf(d_to).multiply(inEuro, MathContext.DECIMAL32);

                                Log.d(TAG, "NumberToConvert = " + info.getText());
                                Log.d(TAG, "FromRate = " + d_from);
                                Log.d(TAG, "ToRate = " + d_to);
                                Log.d(TAG, "inEuro = " + inEuro);

                                info.setText("" + removeUselessZeros(result));
                                first = "" + result;
                            }
                            else {
                                info.setText("" + info.getText());
                                first = "" + info.getText();
                            }
                            second = "";
                            operator = "";
                        }
                    }
                }
            }
        });

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setText("");
                first = "";
                second = "";
                operator = "";
            }
        });

        backspace = findViewById(R.id.backspace);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.getText().length() > 0) {
                    String lastChar = info.getText().subSequence(info.getText().length()-1, info.getText().length()).toString();

                    if (info.getText().length() == 1) {
                        info.setText(info.getText().subSequence(0, info.getText().length()-1));
                        first = "";
                    }
                    else {
                        if (lastChar.equals("×") ||
                                lastChar.equals("÷") ||
                                lastChar.equals("+") ||
                                lastChar.equals("-")) {
                            info.setText(info.getText().subSequence(0, info.getText().length()-1));

                            if (lastChar.equals("-") && !operator.isEmpty()) second = "";
                            else operator = "";
                        }
                        else {
                            info.setText(info.getText().subSequence(0, info.getText().length()-1));
                            if (!operator.isEmpty()) second = second.substring(0, second.length()-1); //!!!
                            else first = first.substring(0, first.length()-1);
                        }
                    }
                }
            }
        });

        dot = findViewById(R.id.dot);
        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.getText().length() > 0) {
                    if ((operator.isEmpty() && !first.contains(".")) || (!operator.isEmpty() && !second.contains("."))) {
                        String lastChar = info.getText().subSequence(info.getText().length() - 1, info.getText().length()).toString();

                        if (!lastChar.equals("×") &&
                                !lastChar.equals("÷") &&
                                !lastChar.equals("+") &&
                                !lastChar.equals("-") &&
                                !lastChar.equals(".")) {
                            info.append(dot.getText());
                            if (second.isEmpty()) first += ".";
                            else {
                                if (!second.contains(".")) second += ".";
                            }
                        } else {
                            if (!lastChar.equals(".")) {
                                info.append("0.");
                                if (second.isEmpty()) first += "0.";
                                else second += "0.";
                            }
                        }
                    }
                }
                else {
                    info.setText("0.");
                    first = "0.";
                }
            }
        });

        result = findViewById(R.id.result);
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!first.isEmpty() && !second.isEmpty()) {
                    BigDecimal result;
                    String strResult;

                    Log.d(TAG, "first = "+first);
                    Log.d(TAG, "operation = "+operator);
                    Log.d(TAG, "second = "+second);

                    if (operator.equals("+")) {
                        result = BigDecimal.valueOf(Double.parseDouble(first)).add(BigDecimal.valueOf(Double.parseDouble(second)), MathContext.DECIMAL32);
                        strResult = removeUselessZeros(result);
                        info.setText("" + strResult);
                        first = "" + strResult;
                    }
                    else if (operator.equals("-")) {
                        result = BigDecimal.valueOf(Double.parseDouble(first)).subtract(BigDecimal.valueOf(Double.parseDouble(second)), MathContext.DECIMAL32);
                        strResult = removeUselessZeros(result);
                        info.setText("" + strResult);
                        first = "" + strResult;
                    }
                    else if (operator.equals("×")) {
                        result = BigDecimal.valueOf(Double.parseDouble(first)).multiply(BigDecimal.valueOf(Double.parseDouble(second)), MathContext.DECIMAL32);
                        strResult = removeUselessZeros(result);
                        info.setText("" + strResult);
                        first = "" + strResult;
                    }
                    else if (operator.equals("÷")) {
                        if (Double.parseDouble(second) != 0) {
                            result = BigDecimal.valueOf(Double.parseDouble(first)).divide(BigDecimal.valueOf(Double.parseDouble(second)), 10, RoundingMode.HALF_UP);
                            strResult = removeUselessZeros(result);
                            info.setText("" + strResult);
                            first = "" + strResult;
                        }
                        else {
                            info.setText("ERROR");
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    info.setText("");
                                }
                            }, 1500);

                            first = "";
                        }
                    }

                    second = "";
                    operator = "";
                }
            }
        });

        convert = findViewById(R.id.convert);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (converterLayout.getVisibility() == View.VISIBLE) converterLayout.setVisibility(View.GONE);
                else {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                        if (isInternetAvailable()) converterLayout.setVisibility(View.VISIBLE);
                        else Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                    }
                    else requestInternetPermission();
                }
            }
        });

        fromView = findViewById(R.id.from);
        toView = findViewById(R.id.to);

        fromView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CurrencyDialog(context, getLayoutInflater(), "from");
            }
        });

        toView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CurrencyDialog(context, getLayoutInflater(), "to");
            }
        });
    }

    public void numberClicked(View view) {
        Button button = (Button)view;

        if (operator.isEmpty()) first += button.getText().toString();
        else second += button.getText().toString();

        info.append(button.getText());
    }

    public void operationClicked(View view) {
        Button button = (Button)view;

        if (info.getText().length() > 0) {
            String lastChar = info.getText().subSequence(info.getText().length()-1, info.getText().length()).toString();

            if (lastChar.equals("+") || lastChar.equals("÷") || lastChar.equals("×")) {
                if (button.getText().toString().equals("-")) {
                    if (lastChar.equals("+")) {
                        info.setText(info.getText().toString().subSequence(0, info.getText().length() - 1) + button.getText().toString());
                        second = button.getText().toString();
                    }
                    else {
                        info.setText(info.getText().toString() + button.getText());
                        second = button.getText().toString();
                    }
                }
                else if (button.getText().toString().equals("+")) {
                    if (lastChar.equals("÷") || lastChar.equals("×")) {
                        info.setText(info.getText().toString() + button.getText());
                        second = button.getText().toString();
                    }
                }
                else { // * 'h /
                    info.setText(info.getText().toString().subSequence(0, info.getText().length() - 1).toString() + button.getText()); //allagh
                    operator = button.getText().toString();
                }
            }
            else if (lastChar.equals("-")) {
                if (button.getText().toString().equals("-")) {
                    if (operator.isEmpty()) {
                        info.setText("+"); // -- = +
                        first = "+"; // -- = +
                    }
                    else {
                        if (!operator.equals("+")) {
                            if (operator.equals("-")) {
                                info.setText(info.getText().toString().subSequence(0, info.getText().length() - 1).toString() + "+"); // -- = +
                                operator = "+";
                            }
                            else { // * 'h /
                                info.setText(info.getText().toString().subSequence(0, info.getText().length() - 1).toString() + "+"); // -- = +
                            }
                            second = "+";
                        }
                        else {
                            info.setText(info.getText().toString().subSequence(0, info.getText().length() - 1)); // +-- = +
                            second = "";
                        }
                    }
                }
                else { //de paizei
                    if (first.length() > 1) {
                        info.setText(info.getText().toString().subSequence(0, info.getText().length() - 1).toString() + button.getText()); //allagh
                        operator = button.getText().toString();
                    }
                }
            }
            else { //an lastChar ari8mos 'h .
                if (lastChar.equals(".")) {
                    if (second.isEmpty()) first += "0";
                    else second += "0";
                }

                if (!first.isEmpty() && !second.isEmpty()) {
                    BigDecimal result;
                    String strResult;

                    Log.d(TAG, "first = "+first);
                    Log.d(TAG, "operation = "+operator);
                    Log.d(TAG, "second = "+second);

                    if (operator.equals("+")) {
                        result = BigDecimal.valueOf(Double.parseDouble(first)).add(BigDecimal.valueOf(Double.parseDouble(second)), MathContext.DECIMAL32);
                        strResult = removeUselessZeros(result);
                        info.setText(strResult + button.getText().toString());
                        operator = button.getText().toString();
                        first = "" + strResult;
                    } else if (operator.equals("-")) {
                        result = BigDecimal.valueOf(Double.parseDouble(first)).subtract(BigDecimal.valueOf(Double.parseDouble(second)), MathContext.DECIMAL32);
                        strResult = removeUselessZeros(result);
                        info.setText(strResult + button.getText().toString());
                        operator = button.getText().toString();
                        first = "" + strResult;
                    } else if (operator.equals("×")) {
                        result = BigDecimal.valueOf(Double.parseDouble(first)).multiply(BigDecimal.valueOf(Double.parseDouble(second)), MathContext.DECIMAL32);
                        strResult = removeUselessZeros(result);
                        info.setText(strResult + button.getText().toString());
                        operator = button.getText().toString();
                        first = "" + strResult;
                    } else if (operator.equals("÷")) {
                        if (Double.parseDouble(second) != 0) {
                            result = BigDecimal.valueOf(Double.parseDouble(first)).divide(BigDecimal.valueOf(Double.parseDouble(second)), 10, RoundingMode.HALF_UP);
                            strResult = removeUselessZeros(result);
                            info.setText(strResult + button.getText().toString());
                            operator = button.getText().toString();
                            first = "" + strResult;
                        } else {
                            info.setText("ERROR");
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    info.setText("");
                                }
                            }, 1500);

                            operator = "";
                            first = "";
                        }
                    }

                    second = "";
                }
                else {
                    info.setText(info.getText().toString() + button.getText());
                    operator = button.getText().toString();
                }
            }
        }
        else {
            if (button.getText().equals("-")) {
                first = button.getText().toString();
                info.setText(button.getText());
            }
        }
    }

    private void requestInternetPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Internet permission is needed for using the currency convertion.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == INTERNET_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isInternetAvailable()) converterLayout.setVisibility(View.VISIBLE);
                else Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String removeUselessZeros(BigDecimal number) {
        String zeroFree = "" + number;

        Log.d(TAG, "stupid Result = " + zeroFree);

        if (zeroFree.contains(".")) {
            String part = zeroFree.substring(zeroFree.indexOf(".")+1);
            char[] characters = part.toCharArray();
            int length = characters.length;
            int counter = 0;

            for (int i = length - 1; i >= 0; i--) {
                if (characters[i] == '0') counter++;
                else break;
            }

            zeroFree = zeroFree.substring(0, zeroFree.length() - counter);
            if (zeroFree.charAt(zeroFree.length()-1) == '.') zeroFree = zeroFree.substring(0, zeroFree.length() - 1);
        }

        Log.d(TAG, "zero-free Result = " + zeroFree);

        return zeroFree;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}