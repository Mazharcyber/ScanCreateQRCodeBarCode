package com.example.barcodeapplictaion.uiApplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.utils.Pref_Config;

public class SettingFragment extends Fragment implements View.OnClickListener {
    SwitchCompat vibrate,saveHistory,copyClipBoard;
    Vibrator vibrator;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);
        vibrate = view.findViewById(R.id.switch_compat_vibrate);
        saveHistory = view.findViewById(R.id.switch_compat_save_history);
        copyClipBoard = view.findViewById(R.id.switch_compat_copy_to_clipboard);

        vibrate.setOnClickListener(this);
        saveHistory.setOnClickListener(this);
        copyClipBoard.setOnClickListener(this);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.setChecked(Pref_Config.isVibrate(getContext()));
        saveHistory.setChecked(Pref_Config.isHistory(getContext()));
        copyClipBoard.setChecked(Pref_Config.isCopy(getContext()));

        return view;
    }

    @Override
    public void onClick(View view)
    {
       int id = view.getId();
       switch (id){
           case R.id.switch_compat_vibrate:
               if (Pref_Config.isVibrate(getContext())) {
                   Pref_Config.setVibrate(getContext(),false);
                   Toast.makeText(getContext(), "Vibration Mode off", Toast.LENGTH_SHORT).show();

               }
               else {
                  Pref_Config.setVibrate(getContext(),true);
                   vibrator.vibrate(1000);
                   Toast.makeText(getContext(), "Vibration Mode on", Toast.LENGTH_SHORT).show();
               }
               break;

           case R.id.switch_compat_save_history:
               if (Pref_Config.isHistory(getContext())) {
                   Pref_Config.setHistory(getContext(), false);
                   Toast.makeText(getContext(), "History turn off", Toast.LENGTH_SHORT).show();


               } else {
                   Pref_Config.setHistory(getContext(), true);
                   Toast.makeText(getContext(), "History turn on", Toast.LENGTH_SHORT).show();
               }
               break;

           case R.id.switch_compat_copy_to_clipboard:
               if (Pref_Config.isCopy(getContext())) {
                   Pref_Config.setCopy(getContext(), false);
                   Toast.makeText(getContext(), "Copy to clipboard Off", Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(getContext(), "Copy to clipboard On", Toast.LENGTH_SHORT).show();
                   Pref_Config.setCopy(getContext(), true);
               }
               break;
       }
    }
}