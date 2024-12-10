package com.example.barcodeapplictaion.uiApplication.mainmenu;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.uiApplication.activties.BackActivity;
import com.example.barcodeapplictaion.uiApplication.fragments.CreateQrFragment;
import com.example.barcodeapplictaion.uiApplication.fragments.HistoryFragment;
import com.example.barcodeapplictaion.uiApplication.fragments.PhotosectionFragment;
import com.example.barcodeapplictaion.uiApplication.fragments.SacnFragment;
import com.example.barcodeapplictaion.uiApplication.fragments.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainMenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    static final float END_SCALE = 0.7f;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuicon;
    LinearLayout contentView;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            Window window = getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        };
        contentView = findViewById(R.id.content);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.scan);
        defaultFragment();
    }

    private static String TAG_HISTORY_FRAG = "TAG_HISTORY_FRAG";
    private boolean isHistoryFragment() {
        HistoryFragment historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(TAG_HISTORY_FRAG);
        if (historyFragment != null && historyFragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {  // id and action listener on navigation drawer items........
            case R.id.photos:
                PhotosectionFragment photosectionFragment = new PhotosectionFragment();
                FragmentTransaction photoTransaction  = getSupportFragmentManager().beginTransaction();
                photoTransaction.replace(R.id.drawer_layout, photosectionFragment);
                photoTransaction.commit();
                return true;
            case R.id.history:
                //f (!isHistoryFragment()) {
                    HistoryFragment fragmentHistory = new HistoryFragment();
                    FragmentTransaction historyTransaction  = getSupportFragmentManager().beginTransaction();
                    historyTransaction.replace(R.id.drawer_layout, fragmentHistory);
                    historyTransaction.commit();
                //}

                return true;
            case R.id.scan:
             defaultFragment();
                return true;
            case R.id.qrCreate:
                CreateQrFragment createQrFragment = new CreateQrFragment();
                FragmentTransaction createTransaction  = getSupportFragmentManager().beginTransaction();
                createTransaction.replace(R.id.drawer_layout, createQrFragment);
                createTransaction.commit();
                return true;
            case R.id.setting:
                SettingFragment settingFragment = new SettingFragment();
                FragmentTransaction setttingTransction  = getSupportFragmentManager().beginTransaction();
                setttingTransction.replace(R.id.drawer_layout, settingFragment);
                setttingTransction.commit();
                return true;

        }
        return false;
    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), BackActivity.class);
        startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void defaultFragment()
    {
        //here it is a scan fragment so i use it in a method cause it is needed in two places;
        SacnFragment sacnFragment = new SacnFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_layout, sacnFragment);
        transaction.commit();
    }


}