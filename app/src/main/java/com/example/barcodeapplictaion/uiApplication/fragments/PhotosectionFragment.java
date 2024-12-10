package com.example.barcodeapplictaion.uiApplication.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcodeapplictaion.utils.AppExecutors;
import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.utils.DownloadsSaveUtils;
import com.example.barcodeapplictaion.database.DatabaseUtil;
import com.example.barcodeapplictaion.database.MyDatabase;
import com.example.barcodeapplictaion.database.MyEntity;
import com.example.barcodeapplictaion.database.MyViewModel;
import com.example.barcodeapplictaion.database.ScannedEntity;
import com.example.barcodeapplictaion.utils.Pref_Config;
import com.example.barcodeapplictaion.utils.Time;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PhotosectionFragment extends Fragment {
    FrameLayout frameLayout;
    ImageView imageViewPhoto,galleryPhoto;
    MyViewModel myViewModel;

    MyDatabase mDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_photosection, container, false);
        imageViewPhoto = view.findViewById(R.id.selectPhoto);
        galleryPhoto = view.findViewById(R.id.galleryPhoto);

        mDB = MyDatabase.getDatabase(getContext());

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        myViewModel.getScannedItemsList().observe(getViewLifecycleOwner(), new Observer<List<ScannedEntity>>() {
            @Override
            public void onChanged(List<ScannedEntity> words) {
                //adapter.setWordList(words);
            }
        });



        imageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1000);
            }
        });
        return view;
    }

    private static final String TAG = "PhotosectionFragment";
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                BarcodeDetector barcodeDetector =
                        new BarcodeDetector.Builder(getContext())
                                //.setBarcodeFormats(Barcode.QR_CODE)
                                .build();

                Frame myFrame = new Frame.Builder()
                        .setBitmap(selectedImage)
                        .build();

                SparseArray<Barcode> barcodes = barcodeDetector.detect(myFrame);
                String type = "";

                final ScannedEntity scannedEntity = new ScannedEntity();

                // Check if at least one barcode was detected
                if(barcodes.size() > 0) {

                    if (barcodes.valueAt(0).format == Barcode.QR_CODE) {
                        //QR_
                        type = "QR_CODE";
                    } else {
                        //BAR_
                        type = "BAR_CODE";
                    }

                    String content_type = "Text";
                    if (barcodes.valueAt(0).wifi != null) {
                        //WIFI
                        Log.d(TAG, "ContactInfo: " + barcodes.valueAt(0).wifi.ssid);
                        Log.d(TAG, "ContactInfo: " + barcodes.valueAt(0).wifi.password);
                        Log.d(TAG, "ContactInfo: " + barcodes.valueAt(0).wifi.encryptionType);
                        content_type = "Wifi";

                        scannedEntity.setSsid(barcodes.valueAt(0).wifi.ssid);
                        scannedEntity.setPassword(barcodes.valueAt(0).wifi.password);
                        //scannedEntity.setEncryptionType(barcodes.valueAt(0).wifi.encryptionType);

                    } else  if (barcodes.valueAt(0).sms != null) {
                        //SMS
                        Log.d(TAG, "ContactInfo: " + barcodes.valueAt(0).sms.phoneNumber);
                        Log.d(TAG, "ContactInfo: " + barcodes.valueAt(0).sms.message);

                        content_type = "Sms";
                        scannedEntity.setPhoneNumber(barcodes.valueAt(0).sms.phoneNumber);
                        scannedEntity.setMessage(barcodes.valueAt(0).sms.message);

                    } else  if (barcodes.valueAt(0).phone != null) {
                        //PHONE
                        String number =  barcodes.valueAt(0).phone.number;
                        Log.d(TAG, "ContactInfo: " + number);

                        content_type = "Phone";
                        scannedEntity.setMessage(barcodes.valueAt(0).phone.number);
                    } else  if (barcodes.valueAt(0).email != null) {
                        //EMAIL
                        String address =  barcodes.valueAt(0).email.address;
                        String subject =  barcodes.valueAt(0).email.subject;
                        String body =  barcodes.valueAt(0).email.body;
                        Log.d(TAG, "ContactInfo: " + address);
                        Log.d(TAG, "ContactInfo: " + subject);
                        Log.d(TAG, "ContactInfo: " + body);
                        content_type = "Email";
                        scannedEntity.setEmailAddress(barcodes.valueAt(0).email.address);
                        scannedEntity.setSubject(barcodes.valueAt(0).email.subject);
                        scannedEntity.setBody(barcodes.valueAt(0).email.body);
                    } else  if (barcodes.valueAt(0).url != null) {
                        //URL
                        content_type = "Link";
                        String title =  barcodes.valueAt(0).url.title;
                        String url =  barcodes.valueAt(0).url.url;
                        Log.d(TAG, "ContactInfo: " + title);
                        Log.d(TAG, "ContactInfo: " + url);

                        if (url.contains("facebook.com") || url.contains("fb.com")) {
                            content_type = "Facebook";
                        } else if (url.contains("twitter.com")) {
                            content_type = "Twitter";
                        } else if (url.contains("instagram.com")) {
                            content_type = "Instagram";
                        } else if (url.contains("youtube.com")) {
                            content_type = "Youtube";
                        } else if (url.contains("play.google.com/store")) {
                            content_type = "PlayStore";
                        }

                        scannedEntity.setTitle(barcodes.valueAt(0).url.title);
                        scannedEntity.setUrl(barcodes.valueAt(0).url.url);

                    } else  if (barcodes.valueAt(0).contactInfo != null) {
                        //ContactInfo
                        Log.d(TAG, "ContactInfo: " + barcodes.valueAt(0).contactInfo.name.formattedName);
                        Log.d(TAG, "ContactInfo: " + barcodes.valueAt(0).contactInfo.phones[0].number);
                        Log.d(TAG, "ContactInfo: " + barcodes.valueAt(0).contactInfo.emails[0].address);
                        content_type = "ContactInfo";
                    }

                    String content = barcodes.valueAt(0).rawValue;
                    int format = barcodes.valueAt(0).format;

                    scannedEntity.setImage_bytes(DatabaseUtil.getBytesFromBitmap(selectedImage));
                    scannedEntity.setBarcodeType(type);
                    scannedEntity.setContent(content);
                    scannedEntity.setContent_type(content_type);
                    scannedEntity.setFormat(format);
                    scannedEntity.setTimestamp(String.valueOf(System.currentTimeMillis()));

                    if (Pref_Config.isHistory(getContext())) {
                        myViewModel.insertScannedItem(scannedEntity);
                    }

                }

                if (barcodes.size() != 0) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.customdailogphoto);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.dailog_bg);
                    final ImageView imageViewDailog = dialog.findViewById(R.id.resultImage);
                    final TextView txt_content = dialog.findViewById(R.id.txt_content);
                    final ImageView favImageView = dialog.findViewById(R.id.favBtn);

                    RelativeLayout btn_download = dialog.findViewById(R.id.btn_download);
                    RelativeLayout btn_share = dialog.findViewById(R.id.btn_share);
                    RelativeLayout btn_visit = dialog.findViewById(R.id.btn_visit);

                    btn_download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), "Download to your Memory", Toast.LENGTH_SHORT).show();
                            DownloadsSaveUtils.saveBitmapToImageAndGetURI(DatabaseUtil.getImageFromBytes(scannedEntity.getImage_bytes()), getContext());
                        }
                    });

                    btn_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();

                            DownloadsSaveUtils.shareImageIntent(getContext(), DownloadsSaveUtils.saveBitmapToCacheAndGetURI(DatabaseUtil.getImageFromBytes(scannedEntity.getImage_bytes()), getContext()));
                        }
                    });

                    btn_visit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (scannedEntity.getContent_type().equals("Link")) {
                                DownloadsSaveUtils.openLinkInBrowser(getContext(), scannedEntity.getUrl());
                            } else  {
                                Toast.makeText(getContext(), "No Link", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    final MyEntity favEntity = new MyEntity();
                    favEntity.setContent(scannedEntity.getContent());
                    favEntity.setBarcodeType(scannedEntity.getBarcodeType());
                    favEntity.setBody(scannedEntity.getBody());
                    favEntity.setContent_type(scannedEntity.getContent_type());
                    favEntity.setEmailAddress(scannedEntity.getEmailAddress());
                    favEntity.setFormat(scannedEntity.getFormat());
                    favEntity.setFormatted_name(scannedEntity.getFormatted_name());
                    favEntity.setImage_bytes(scannedEntity.getImage_bytes());
                    favEntity.setMessage(scannedEntity.getMessage());
                    favEntity.setPassword(scannedEntity.getPassword());
                    favEntity.setPhoneNumber(scannedEntity.getPhoneNumber());
                    favEntity.setTitle(scannedEntity.getTitle());
                    favEntity.setUrl(scannedEntity.getUrl());
                    favEntity.setTimestamp(Time.time());

                    final boolean[] Flag = {false};
                    updateFavButtonUI(scannedEntity, favImageView, Flag);

                    favImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            if (Flag[0]) {
                                //DELETE
                                myViewModel.deleteFavoriteItem(scannedEntity.getContent());
                                Toast.makeText(getContext(), "Remove from favorite", Toast.LENGTH_SHORT).show();

                            } else {
                                //ADD
                                myViewModel.insertFavoriteItem(favEntity);
                                Toast.makeText(getContext(), "Add to favorite", Toast.LENGTH_SHORT).show();
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateFavButtonUI(scannedEntity, favImageView, Flag);
                                }
                            }, 500);

                            favImageView.invalidate();
                        }

                    });
                    txt_content.setText(barcodes.valueAt(0).displayValue + "\nType: " + type);
                    imageViewDailog.setImageBitmap(selectedImage);
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), "You picked an wrong image", Toast.LENGTH_SHORT).show();
                }


                /*try {
                    Bitmap bMap = selectedImage;
                    String contents = null;
                    int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    galleryPhoto.setImageBitmap(bMap);
                    Reader reader = new Reader() {
                        @Override
                        public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
                            return null;
                        }

                        @Override
                        public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
                            return null;
                        }

                        @Override
                        public void reset() {

                        }

                      };

                    Result result = reader.decode(bitmap);
                    contents = result.getText();
                    if (result != null) {
                        Toast.makeText(getActivity(), contents, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                }*/
                //  image_view.setImageBitmap(selectedImage);

            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();

            }



        }
        else
            {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();

        }

    }

    private void updateFavButtonUI(final ScannedEntity scannedEntity, final ImageView favImageView, final boolean[] flag) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                flag[0] = !mDB.myDao().checkFavByContentAndImage(scannedEntity.getContent()).isEmpty();
                (getActivity()).runOnUiThread(new Runnable() {
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
}