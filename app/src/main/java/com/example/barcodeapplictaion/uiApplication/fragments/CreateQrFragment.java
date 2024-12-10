package com.example.barcodeapplictaion.uiApplication.fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcodeapplictaion.AdapterClasses.SlidingImage_Adapter;
import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.dbhelper.MySqlDBHandler;
import com.example.barcodeapplictaion.model.ImageModel;
import com.example.barcodeapplictaion.uiApplication.activties.SelectionQrBrActivity;
import com.google.android.gms.ads.AdView;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class CreateQrFragment extends Fragment implements View.OnClickListener {
    LinearLayout link,phone,sms,text,email,youtube,whatsApp,facebook,contact,playStore,instagram,twitter;
    int clickedView;

    private TextView txtLoading;
    private static ViewPager mPager;
    private AdView mAdView;
    LinearLayout contentView;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private View view;
    private ScrollView scrollView;
    private ArrayList<ImageModel> imageModelArrayList;
    private int[] myImageList = new int[]{R.drawable.i1,R.drawable.i2,
            R.drawable.i3,  R.drawable.i4,
            R.drawable.i5
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
         view =  inflater.inflate(R.layout.fragment_create_qr, container, false);
        link = view.findViewById(R.id.linearLink);
        phone = view.findViewById(R.id.linearPhone);
        sms = view.findViewById(R.id.linearSms);
        text = view.findViewById(R.id.linearText);
        email = view.findViewById(R.id.linearEmail);
        youtube = view.findViewById(R.id.linearYoutube);
        whatsApp = view.findViewById(R.id.linearWhatsApp);
        facebook = view.findViewById(R.id.linearFacebook);
        contact = view.findViewById(R.id.linearContact);
        playStore = view.findViewById(R.id.linearPlayStore);
        instagram = view.findViewById(R.id.linearInstargarm);
        twitter = view.findViewById(R.id.lineartwitter);

        link.setOnClickListener(this);
        phone.setOnClickListener(this);
        sms.setOnClickListener(this);
        text.setOnClickListener(this);
        email.setOnClickListener(this);
        youtube.setOnClickListener(this);
        whatsApp.setOnClickListener(this);
        facebook.setOnClickListener(this);
        contact.setOnClickListener(this);
        playStore.setOnClickListener(this);
        instagram.setOnClickListener(this);
        twitter.setOnClickListener(this);
        imageModelArrayList = new ArrayList<>(); ///////// USE FOR SLIDE SHOW
        imageModelArrayList = populateList(); //////////USE FOR SLIDE SHOW
        init();
        return view;
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        Intent intent = new Intent(getActivity(), SelectionQrBrActivity.class);
        switch (id) {

            case R.id.linearLink:
                clickedView =1;
                intent.putExtra("view",clickedView);
                intent.putExtra("content_type", "Link");
                break;
            case R.id.linearPhone:
                clickedView = 2;
                intent.putExtra("view",clickedView);
                intent.putExtra("content_type", "Phone");
                break;
            case R.id.linearSms:
                intent.putExtra("content_type", "Sms");
                break;
            case R.id.linearText:
                intent.putExtra("content_type", "Text");
                break;
            case R.id.linearEmail:
                intent.putExtra("content_type", "Email");
                break;
            case R.id.linearContact:
                intent.putExtra("content_type", "Contact");
                break;
            case R.id.linearWhatsApp:
                intent.putExtra("content_type", "WhatsApp");
                break;
            case R.id.linearFacebook:
                intent.putExtra("content_type", "Facebook");
                break;
            case R.id.linearYoutube:
                intent.putExtra("content_type", "Youtube");
                break;
            case R.id.linearTouch:
                intent.putExtra("content_type", "ContactInfo");
                break;
            case R.id.linearInstargarm:
                intent.putExtra("content_type", "Instagram");
                break;
            case R.id.lineartwitter:
                intent.putExtra("content_type", "Twitter");
                break;
            case R.id.linearPlayStore:
                intent.putExtra("content_type", "PlayStore");

                break;
        }

        startActivity(intent);
    }

    //for sliding images the below two methods are used
    private void init() {
        mPager = view.findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(getActivity(),imageModelArrayList));
        CirclePageIndicator indicator = (CirclePageIndicator)
                view.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        //Set circle indicator radius
        indicator.setRadius(5 * density);
        NUM_PAGES =imageModelArrayList.size();
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;

                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }
    private ArrayList<ImageModel> populateList(){
        ArrayList<ImageModel> list = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }
        return list;
    }
}