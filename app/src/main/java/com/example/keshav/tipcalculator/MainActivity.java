package com.example.keshav.tipcalculator;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements EditText.OnEditorActionListener, TextWatcher, View.OnClickListener, View.OnKeyListener, SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    private EditText billAmountEditText;
    private TextView percentTextView;
    private SeekBar percentSeekBar;
    private Button percentUpButton;
    private Button percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    private RadioGroup roundingRadioGroup;
    private RadioButton roundingNoneRadioButton;
    private RadioButton roundingTipRadioButton;
    private RadioButton roundingTotalRadioButton;
    private TextView splitBill;
    private Spinner splitSpinner;
    private TextView perPersonLabel;
    private TextView perPersonTextView;
    private float tipPercent = 0.15f;

    private SharedPreferences savedValues;
    private String billAmountString;

    private final int ROUND_NONE = 0;
    private final int ROUND_TIP = 1;
    private final int ROUND_TOTAL = 2;

    private int rounding = ROUND_NONE;
    private int split = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setContentView(R.layout.notification);
        new AppShortCut<MainActivity>().shortcut(getApplicationContext(), MainActivity.this, R.drawable.ic_launcher_foreground);
        savedValues = getSharedPreferences("savedValues", MODE_PRIVATE);
        initialize();
        setListener();
        calculateAndDisplay();
    }

    private void calculateAndDisplay() {
        billAmountString = billAmountEditText.getText().toString();
        float billAmount = 0;
        if (!billAmountString.isEmpty())
            billAmount = Float.parseFloat(billAmountString);

        int progress = percentSeekBar.getProgress();
        tipPercent = (float) progress / 100;

        float tipAmount = 0;
        float totalAmount = 0;

        if (rounding == ROUND_NONE) {
            tipAmount = billAmount * tipPercent;
            totalAmount = billAmount + tipAmount;
        } else if (rounding == ROUND_TIP) {
            tipAmount = StrictMath.round(billAmount * tipPercent);
            totalAmount = tipAmount + billAmount;
            tipPercent = tipAmount / billAmount;
        } else if (rounding == ROUND_TOTAL) {
            float tipNotRounded = billAmount * tipPercent;
            totalAmount = StrictMath.round(billAmount + tipNotRounded);
            tipAmount = totalAmount - billAmount;
            tipPercent = tipAmount / billAmount;
        }

        float splitAmount = 0;
        if (split == 1) {
            perPersonLabel.setVisibility(View.GONE);
            perPersonTextView.setVisibility(View.GONE);
        } else {
            splitAmount = totalAmount / split;
            perPersonLabel.setVisibility(View.VISIBLE);
            perPersonTextView.setVisibility(View.VISIBLE);
        }


        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
        perPersonTextView.setText(currency.format(splitAmount));

        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));


    }

    public void initialize() {
        // references to widgets
        billAmountEditText = findViewById(R.id.billAmountEditText);
        percentTextView = findViewById(R.id.percentTextView);
        percentSeekBar = findViewById(R.id.percentSeekBar);
        /* percentUpButton = findViewById(R.id.percentUpButton);
        percentDownButton = findViewById(R.id.percentDownButton);*/
        tipTextView = findViewById(R.id.tipTextView);
        totalTextView = findViewById(R.id.totalTextView);
        splitBill = findViewById(R.id.splitBill);
        splitSpinner = findViewById(R.id.splitSpinner);

        perPersonLabel = findViewById(R.id.perPersonLabel);
        perPersonTextView = findViewById(R.id.perPersonTextView);

        roundingRadioGroup = findViewById(R.id.roundingRadioGroup);
        roundingNoneRadioButton = findViewById(R.id.roundingNoneRadioButton);
        roundingTipRadioButton = findViewById(R.id.roundingTipRadioButton);
        roundingTotalRadioButton = findViewById(R.id.roundingTotalRadioButton);

        registerForContextMenu(findViewById(R.id.billAmountLabel));


        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.split_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splitSpinner.setAdapter(arrayAdapter);
    }

    public void setListener() {
        billAmountEditText.setOnEditorActionListener(this);
        billAmountEditText.setOnKeyListener(this);
        percentSeekBar.setOnSeekBarChangeListener(this);
        percentSeekBar.setOnKeyListener(this);
       /* percentDownButton.setOnClickListener(this);
        percentUpButton.setOnClickListener(this);*/
        roundingRadioGroup.setOnCheckedChangeListener(this);
        roundingRadioGroup.setOnKeyListener(this);
        splitSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.d("editor action", "starting on Editor Action");
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
          /*  tipTextView.setText("Rs.10.00");
            totalTextView.setText("Rs.110.00");*/
            calculateAndDisplay();
        }
        //calculateAndDisplay();
        Log.d("editor action", "ending on Editor Action");
        return false;
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.putInt("rounding", rounding);
        editor.putInt("split", split);
        editor.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", .15f);
        super.onResume();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        percentTextView.setText(progress + "%");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        calculateAndDisplay();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.roundingNoneRadioButton:
                rounding = ROUND_NONE;
                break;
            case R.id.roundingTipRadioButton:
                rounding = ROUND_TIP;
                break;
            case R.id.roundingTotalRadioButton:
                rounding = ROUND_TOTAL;
                break;
        }
        calculateAndDisplay();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        split = position++;
        calculateAndDisplay();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.menu_about:
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("This is Tip Calculator!");
                alertDialog.setTitle("About app");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("OK", null);
                alertDialog.show();
                return true;
            case R.id.item3:
                Toast.makeText(getApplicationContext(), "Item 3 Selected!", Toast.LENGTH_SHORT).show();
                addNotification();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.menu_about:
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("This is Tip Calculator!");
                alertDialog.setTitle("About app");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("OK", null);
                alertDialog.show();
                return true;
            case R.id.item3:
                Toast.makeText(getApplicationContext(), "Item 3 Selected!", Toast.LENGTH_SHORT).show();
                addNotification();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        calculateAndDisplay();
    }

   /* public void pop(View view){
        PopupMenu popupMenu=new PopupMenu(this.view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_settings:
                        Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        return true;
                    case R.id.menu_about:
                        Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                        alertDialog.setMessage("This is Tip Calculator!");
                        alertDialog.setTitle("About app");
                        alertDialog.setCancelable(true);
                        alertDialog.setPositiveButton("OK", null);
                        alertDialog.show();
                        return true;
                    case R.id.item3:
                        Toast.makeText(getApplicationContext(), "Item 3 Selected!", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }

            }
        });
        getMenuInflater().inflate(R.menu.menu_main,popupMenu.getMenu());
        popupMenu.show();

    }*/

   public void addNotification(){

       // notificationCompat
       NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
       builder.setSmallIcon(R.drawable.ic_settings_black_24dp);
       builder.setContentTitle("NotificationTitle");
       builder.setContentText("This is notification.");

       // Intent
       Intent notificationIntent=new Intent(this,MainActivity.class);
       PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
       builder.setContentIntent(pendingIntent);

       // add as notification
       NotificationManager manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       manager.notify(0,builder.build());

   }


}
