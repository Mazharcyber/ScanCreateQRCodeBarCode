package com.example.barcodeapplictaion.uiApplication.activties;
import android.app.Dialog;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.os.Handler;
    import android.util.Patterns;
    import android.view.View;
    import android.webkit.URLUtil;
    import android.widget.AdapterView;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.RelativeLayout;
    import android.widget.Spinner;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.lifecycle.ViewModelProviders;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.LinearSmoothScroller;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.barcodeapplictaion.AdapterClasses.CreatedActivityAdapter;
    import com.example.barcodeapplictaion.utils.AppExecutors;
    import com.example.barcodeapplictaion.R;
    import com.example.barcodeapplictaion.utils.DownloadsSaveUtils;
    import com.example.barcodeapplictaion.database.CreatedEntity;
    import com.example.barcodeapplictaion.database.DatabaseUtil;
    import com.example.barcodeapplictaion.database.MyDatabase;
    import com.example.barcodeapplictaion.database.MyEntity;
    import com.example.barcodeapplictaion.database.MyViewModel;
    import com.example.barcodeapplictaion.model.QrModelAnimated;
    import com.example.barcodeapplictaion.utils.Pref_Config;
    import com.example.barcodeapplictaion.utils.Time;
    import com.google.zxing.BarcodeFormat;
    import com.google.zxing.MultiFormatWriter;
    import com.google.zxing.WriterException;
    import com.google.zxing.common.BitMatrix;
    import com.google.zxing.qrcode.QRCodeWriter;
    import java.util.ArrayList;
    import java.util.List;
    import androidmads.library.qrgenearator.QRGEncoder;

    public class SelectionQrBrActivity extends AppCompatActivity implements CreatedActivityAdapter.AdapterListener {
         EditText et_content, et_title, et_url, et_contact_name, et_phone,
                 et_message, et_email_address, et_email_subject, et_email_body;
         ImageView imageView,btnCreateCode;
        RecyclerView recyclerView;
         List<QrModelAnimated> qrModelAnimatedList;
         CreatedActivityAdapter createdActivityAdapter;
         private Spinner spinnerSelection;
        private static final int VERTICAL_ITEM_SPACE = 48;
          QRGEncoder qrgEncoder;
         Bitmap bitmap;
        MyViewModel myViewModel;

        MyDatabase mDB;
         int POS = 0;
         private String barcodeType = "QR_CODE";
         private int barcodePosition = 0;
         private String contentType = "Text";

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_selection_qr_br);
            et_content = findViewById(R.id.editText);
            et_title = findViewById(R.id.et_title);
            et_url = findViewById(R.id.et_url);
            et_contact_name = findViewById(R.id.et_contact_name);
            et_phone = findViewById(R.id.et_phone);
            et_message = findViewById(R.id.et_message);
            et_email_address = findViewById(R.id.et_email_address);
            et_email_subject = findViewById(R.id.et_email_subject);
            et_email_body = findViewById(R.id.et_email_body);


            imageView = findViewById(R.id.imageView);
            btnCreateCode = findViewById(R.id.buttonCreate);
            et_content = findViewById(R.id.editText);
            spinnerSelection = findViewById(R.id.selectType);
            recyclerView = findViewById(R.id.recyclerViewImages);

            mDB = MyDatabase.getDatabase(this);
            myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

            Intent intent = getIntent();
            if (intent != null)
            {
                contentType = intent.getStringExtra("content_type");
            }
            updateEditTextUI();
            //adding items to the recyclerview with help of arrayList
            qrModelAnimatedList = new ArrayList<>();
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.email,"Email"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.twitter,"Twitter"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.phone,"Phone"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.youtube,"Youtube"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.play_store,"PlayStore"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.instagram,"Instagram"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.cantact,"Contact"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.facebook,"Facebook"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.whatsapp,"WhatsApp"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.text,"Text"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.link,"Link"));
            qrModelAnimatedList.add(new QrModelAnimated(R.drawable.sms,"Sms"));


            final RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(this) {
                        @Override protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            recyclerView.setLayoutManager(linearLayoutManager);
            createdActivityAdapter = new CreatedActivityAdapter(contentType, qrModelAnimatedList,this);
            recyclerView.setAdapter(createdActivityAdapter);
            for (int x = 0; x < qrModelAnimatedList.size(); x++) {
                if (qrModelAnimatedList.get(x).getTitleText().equals(contentType)) {
                    POS = x;
                    if (x > 4) {
                        POS = POS - 2;
                    }
                    break;
                }
            }
            recyclerView.scrollToPosition(POS);
            barcodePosition = spinnerSelection.getSelectedItemPosition();

            buttonCreate();
            //used spinner for selecting the bar code or qr code
                 spinnerSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                     @Override
                     public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                       barcodePosition = position;
                         updateCreateButtonOnTypeSelection();
                     }

                     @Override
                     public void onNothingSelected(AdapterView<?> adapterView) {

                     }
                 });
        }

        private void updateEditTextUI() {
            et_content.setVisibility(View.GONE);
            et_title.setVisibility(View.GONE);
            et_url.setVisibility(View.GONE);
            et_contact_name.setVisibility(View.GONE);
            et_phone.setVisibility(View.GONE);
            et_message.setVisibility(View.GONE);
            et_email_address.setVisibility(View.GONE);
            et_email_subject.setVisibility(View.GONE);
            et_email_body.setVisibility(View.GONE);

            if (contentType.equals("Sms")) {
                et_phone.setVisibility(View.VISIBLE);
                et_message.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Phone")) {
                et_phone.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Email")) {
                et_email_address.setVisibility(View.VISIBLE);
                et_email_subject.setVisibility(View.VISIBLE);
                et_email_body.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Link")) {
                et_url.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Facebook")) {
                et_url.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Instagram")) {
                et_url.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Twitter")) {
                et_url.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Youtube")) {
                et_url.setVisibility(View.VISIBLE);
            } else if (contentType.equals("PlayStore")) {
                et_url.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Contact")) {
                et_contact_name.setVisibility(View.VISIBLE);
                et_phone.setVisibility(View.VISIBLE);
                et_email_address.setVisibility(View.VISIBLE);
            } else if (contentType.equals("WhatsApp")) {
                et_phone.setVisibility(View.VISIBLE);
            } else if (contentType.equals("Text")) {
                et_content.setVisibility(View.VISIBLE);
            }
        }

        boolean isValid = false;
        private void buttonCreate() {
            btnCreateCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CreatedEntity createdEntity = new CreatedEntity();
                    String contents = "";
                    if (contentType.equals("Sms")) {
                        String ph = et_phone.getText().toString().trim();
                        String msg = et_message.getText().toString().trim();

                        contents = "smsto:" + ph + ":" + msg;
                        createdEntity.setPhoneNumber(ph);
                        createdEntity.setMessage(msg);
                    } else if (contentType.equals("Phone") || contentType.equals("WhatsApp")) {
                        String ph = et_phone.getText().toString().trim();
                        contents = "tel:" + ph;
                        createdEntity.setPhoneNumber(ph);
                    } else if (contentType.equals("Email")) {
                        String email = et_email_address.getText().toString().trim();
                        String subject = et_email_subject.getText().toString().trim();
                        String body = et_email_body.getText().toString().trim();

                        contents = "mailto:" +  email
                                + "?subject=" + subject
                                + "&body=" + body;
                        createdEntity.setEmailAddress(email);
                        createdEntity.setSubject(subject);
                        createdEntity.setBody(body);

                    } else if (contentType.equals("Link")
                            || contentType.equals("Facebook")
                            || contentType.equals("Instagram")
                            || contentType.equals("Twitter")
                            || contentType.equals("Youtube")
                            || contentType.equals("PlayStore")) {


                        String url = et_url.getText().toString().trim();

                        if (!url.startsWith("http")) {
                            url = "https://" + url;
                        }

                         isValid = URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches();
                        if (isValid) {
                            contents = "url:" + url;
                            createdEntity.setUrl(url);
                        } else {
                            Toast.makeText(SelectionQrBrActivity.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } else if (contentType.equals("Contact")) {

                        String name = et_contact_name.getText().toString().trim();
                        String email = et_email_address.getText().toString().trim();
                        String ph = et_phone.getText().toString().trim();


                        contents = "MECARD:N:" + name +",;EMAIL:"
                                + email
                                + ";TEL:" + ph;
                        createdEntity.setFormatted_name(name);
                        createdEntity.setEmailAddress(email);
                        createdEntity.setPhoneNumber(ph);

                    } else if (contentType.equals("Text")) {
                        contents = et_content.getText().toString().trim();
                    }

                    createdEntity.setContent(contents);
                    createdEntity.setContent_type(contentType);
                    createdEntity.setBarcodeType(barcodeType);
                    createdEntity.setTimestamp(Time.time());

                    if (barcodeType.equals("QR_CODE")){
                        //QR
                        QRCodeWriter writer = new QRCodeWriter();
                        /*

                        "tel:" + data; Phone
                        "mailto:test@gmail.com?subject=Hello&body=world" Email
                        "sms:" + data;
                        */

                        try {
                            BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, 512, 512);
                            int width = bitMatrix.getWidth();
                            int height = bitMatrix.getHeight();
                            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            for (int x = 0; x < width; x++) {
                                for (int y = 0; y < height; y++) {
                                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                }
                            }
                            bitmap = bmp;
                            imageView.setImageBitmap(bmp);

                            createdEntity.setImage_bytes(DatabaseUtil.getBytesFromBitmap(bitmap));
                            if (Pref_Config.isHistory(SelectionQrBrActivity.this)) {
                                myViewModel.insertCreatedItem(createdEntity);
                            }



                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                        final Dialog dialog = new Dialog(SelectionQrBrActivity.this);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.customdailog);
                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dailog_bg);
                        final ImageView imageViewDailog = dialog.findViewById(R.id.resultImage);
                        final ImageView favImageView = dialog.findViewById(R.id.favBtn);
                        final RelativeLayout downloadImages = dialog.findViewById(R.id.download);
                        final RelativeLayout shareImages = dialog.findViewById(R.id.share);
                        final RelativeLayout visitLink = dialog.findViewById(R.id.visit);


                        downloadImages.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getApplicationContext(), "Download to your Memory", Toast.LENGTH_SHORT).show();
                                DownloadsSaveUtils.saveBitmapToImageAndGetURI(bitmap, getApplicationContext());
                            }
                        });


                        shareImages.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DownloadsSaveUtils.shareImageIntent(getApplicationContext(), DownloadsSaveUtils.saveBitmapToCacheAndGetURI(bitmap, getApplicationContext()));
                                Toast.makeText(getApplicationContext(), "Share App", Toast.LENGTH_SHORT).show();
                            }
                        });

                        visitLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (createdEntity.getContent_type().equals("Link")) {
                                    DownloadsSaveUtils.openLinkInBrowser(getApplicationContext(), createdEntity.getUrl());
                                } else  {
                                    Toast.makeText(getApplicationContext(), "No Link", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        final MyEntity favEntity = new MyEntity();
                        favEntity.setContent(createdEntity.getContent());
                        favEntity.setBarcodeType(createdEntity.getBarcodeType());
                        favEntity.setBody(createdEntity.getBody());
                        favEntity.setContent_type(createdEntity.getContent_type());
                        favEntity.setEmailAddress(createdEntity.getEmailAddress());
                        favEntity.setFormat(createdEntity.getFormat());
                        favEntity.setFormatted_name(createdEntity.getFormatted_name());
                        favEntity.setImage_bytes(createdEntity.getImage_bytes());
                        favEntity.setMessage(createdEntity.getMessage());
                        favEntity.setPassword(createdEntity.getPassword());
                        favEntity.setPhoneNumber(createdEntity.getPhoneNumber());
                        favEntity.setTitle(createdEntity.getTitle());
                        favEntity.setUrl(createdEntity.getUrl());
                        favEntity.setTimestamp(Time.time());

                        final boolean[] Flag = {false};
                        updateFavButtonUI(createdEntity, favImageView, Flag);

                        favImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                if (Flag[0]) {
                                    //DELETE
                                    myViewModel.deleteFavoriteItem(createdEntity.getContent());
                                    Toast.makeText(SelectionQrBrActivity.this, "Remove from favorite", Toast.LENGTH_SHORT).show();

                                } else {
                                    //ADD
                                    myViewModel.insertFavoriteItem(favEntity);
                                    Toast.makeText(SelectionQrBrActivity.this, "Add to favorite", Toast.LENGTH_SHORT).show();
                                }
                                updateFavButtonUI(createdEntity, favImageView, Flag);
                            }

                        });

                        imageViewDailog.setImageBitmap(bitmap);
                        dialog.show();

                    } else if (barcodeType.equals("BAR_CODE")) {
                        //bar
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        Bitmap bitmapImage = null;
                        try {
                            BitMatrix bitMatrix = multiFormatWriter.encode(contents, BarcodeFormat.CODE_128, imageView.getWidth(), imageView.getHeight());
                            bitmapImage = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.RGB_565);
                            for (int i = 0; i<imageView.getWidth(); i++){
                                for (int j = 0; j<imageView.getHeight(); j++){
                                    bitmapImage.setPixel(i,j,bitMatrix.get(i,j)? Color.BLACK:Color.WHITE);
                                }
                            }
                            bitmap = bitmapImage;
                            imageView.setImageBitmap(bitmapImage);

                            createdEntity.setImage_bytes(DatabaseUtil.getBytesFromBitmap(bitmap));

                            if (Pref_Config.isHistory(SelectionQrBrActivity.this)) {
                                myViewModel.insertCreatedItem(createdEntity);
                            }

                        }
                        catch (WriterException e)
                        {
                            e.printStackTrace();
                        }
                        final Dialog dialog = new Dialog(SelectionQrBrActivity.this);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.customdailog);
                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dailog_bg);
                        final ImageView imageViewDailog = dialog.findViewById(R.id.resultImage);
                        final ImageView favImageView = dialog.findViewById(R.id.favBtn);

                        final MyEntity favEntity = new MyEntity();
                        favEntity.setContent(createdEntity.getContent());
                        favEntity.setBarcodeType(createdEntity.getBarcodeType());
                        favEntity.setBody(createdEntity.getBody());
                        favEntity.setContent_type(createdEntity.getContent_type());
                        favEntity.setEmailAddress(createdEntity.getEmailAddress());
                        favEntity.setFormat(createdEntity.getFormat());
                        favEntity.setFormatted_name(createdEntity.getFormatted_name());
                        favEntity.setImage_bytes(createdEntity.getImage_bytes());
                        favEntity.setMessage(createdEntity.getMessage());
                        favEntity.setPassword(createdEntity.getPassword());
                        favEntity.setPhoneNumber(createdEntity.getPhoneNumber());
                        favEntity.setTitle(createdEntity.getTitle());
                        favEntity.setUrl(createdEntity.getUrl());
                        favEntity.setTimestamp(Time.time());

                        final boolean[] Flag = {false};
                        updateFavButtonUI(createdEntity, favImageView, Flag);

                        favImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                if (Flag[0]) {
                                    //DELETE
                                    myViewModel.deleteFavoriteItem(createdEntity.getContent());
                                } else {
                                    //ADD
                                    myViewModel.insertFavoriteItem(favEntity);
                                }
                                updateFavButtonUI(createdEntity, favImageView, Flag);
                            }

                        });

                        imageViewDailog.setImageBitmap(bitmapImage);
                        dialog.show();
                    } else {
                        Toast.makeText(SelectionQrBrActivity.this, "Select Type", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        private void updateFavButtonUI(final CreatedEntity scannedEntity, final ImageView favImageView, final boolean[] flag) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    flag[0] = !mDB.myDao().checkFavByContentAndImage(scannedEntity.getContent()).isEmpty();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (flag[0]) {
                                        favImageView.setImageResource(R.drawable.ic_favorite);

                                    } else {
                                        favImageView.setImageResource(R.drawable.ic__unfavorite);
                                    }
                                }
                            }, 200);

                        }
                    });
                }
            });
        }


        private void updateCreateButtonOnTypeSelection() {
            if (barcodePosition == 0) {
                btnCreateCode.setEnabled(false);

                barcodeType = "";

            } else if (barcodePosition == 1) {
                btnCreateCode.setEnabled(true);
                barcodeType = "QR_CODE";
            } else if (barcodePosition == 2) {
                barcodeType = "BAR_CODE";
                btnCreateCode.setEnabled(true);

            }
        }

        public void autoscroll()
        { ////auto scroll is used for the recycler view to scroll it self
            final int speedScroll = 1;
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                int count = 0;
                @Override
                public void run()
                {
                    if (count == createdActivityAdapter.getItemCount())
                        count=0;
                    if (count < createdActivityAdapter.getItemCount()){
                        recyclerView.smoothScrollToPosition(++count);
                        handler.postDelayed(this,speedScroll);
                    }
                }
            };
            handler.postDelayed(runnable,speedScroll);
        }

        @Override
        public void onContentTypeChanged(String content_type) {
            contentType = content_type;
            createdActivityAdapter.setContentType(contentType);
            updateEditTextUI();
        }

        @Override
        public void scrollTo(final int position) {
           POS = position;

        }


    }