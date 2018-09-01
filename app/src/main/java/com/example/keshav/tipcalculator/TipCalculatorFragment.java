package com.example.keshav.tipcalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class TipCalculatorFragment extends Fragment implements TextView.OnEditorActionListener{
private SharedPreferences prefs;
private EditText billAmountEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getContext(),R.xml.preferences,false);
    prefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
    setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tip_calculator,container,false);

        billAmountEditText=view.findViewById(R.id.billAmountEditText);
        billAmountEditText.setOnEditorActionListener(this);
        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        return false;
    }
}
