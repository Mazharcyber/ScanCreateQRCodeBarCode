package com.example.barcodeapplictaion.uiApplication.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.barcodeapplictaion.AdapterClasses.ViewPagerAdapter;
import com.example.barcodeapplictaion.utils.AppExecutors;
import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.database.MyDatabase;
import com.example.barcodeapplictaion.database.ScannedEntity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView shareImage;

    private List<ScannedEntity> scannedEntityList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_history, container, false);
        toolbar = view.findViewById(R.id.myToolbar);
        tabLayout = view.findViewById(R.id.tablayout);
        viewPager= view.findViewById(R.id.myViewPager);
         shareImage = view.findViewById(R.id.shareIcon);
        scanHistoryFragment = new ScanHistoryFragment();
        scannedEntityList = new ArrayList<>();

        final MyDatabase mDB = MyDatabase.getDatabase(getContext());
        AppExecutors appExecutors = AppExecutors.getInstance();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                scannedEntityList = mDB.myDao().getAllScannedList();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupViewpager(viewPager);
                    }
                });
            }
        });


        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setupViewpager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             shareApp();
            }
        });
        return view;
    }

    ScanHistoryFragment scanHistoryFragment;
    CreatedQRHistoryFragment createdQRHistoryFragment;

    private void setupViewpager(ViewPager viewPager){

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPagerAdapter.addFragment(scanHistoryFragment,"Scanned");
        viewPagerAdapter.addFragment(new CreatedQRHistoryFragment(),"Created");
        viewPagerAdapter.addFragment(new FavoriteFragment(),"Favorite");
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
            @Override
            public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

            }
        });


    }
    private void shareApp()
    {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String shareSub = "I am recommending you to install this prank video call app it is awesome app\n";
        shareSub += "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
        myIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
        startActivity(Intent.createChooser(myIntent, "Share using"));
    }
}