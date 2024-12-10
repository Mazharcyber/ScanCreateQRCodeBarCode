
package com.example.barcodeapplictaion.uiApplication.activties;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.uiApplication.mainmenu.MainMenuActivity;

public class BackActivity extends AppCompatActivity implements View.OnClickListener {
RelativeLayout rateUs, moreApps, privacy, home, exit,share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);


        rateUs = findViewById(R.id.rate);
        moreApps = findViewById(R.id.more);
        privacy = findViewById(R.id.privacy);
        home = findViewById(R.id.home);
        exit = findViewById(R.id.exit);
        share = findViewById(R.id.shares);

        rateUs.setOnClickListener(this);
        moreApps.setOnClickListener(this);
        privacy.setOnClickListener(this);
        share.setOnClickListener(this);
        home.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home:
                goToHome();
                break;
            case R.id.more:
                Intent more = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.more_apps)));
                startActivity(more);
                break;
            case R.id.exit:
                finishAffinity();
                break;
            case R.id.rate:
                try {
                    Uri uri1 = Uri.parse("market://details?id=" + getPackageName());
                    Intent goTo = new Intent(Intent.ACTION_VIEW, uri1);
                    startActivity(goTo);
                } catch (ActivityNotFoundException e)
                {
                    Uri uri1 = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent goTo = new Intent(Intent.ACTION_VIEW, uri1);
                    startActivity(goTo);
                }
                break;
            case R.id.shares:
                shareApp();
                break;

            case R.id.privacy:
                Intent privacy = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.privaci_policylink)));
                startActivity(privacy);
                break;
        }
    }

    public void goToHome()
    {
        Intent intent = new Intent(BackActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }
    private void shareApp()
    {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String shareSub = "I am recommending you to install this Qr(Quick Response)code And Br(Bar Code)Scanner app it is awesome app\n";
        shareSub += "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
        myIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
        startActivity(Intent.createChooser(myIntent, "Share using"));
    }
}