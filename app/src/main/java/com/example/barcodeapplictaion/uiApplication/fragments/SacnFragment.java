package com.example.barcodeapplictaion.uiApplication.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

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
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SacnFragment extends Fragment {
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;
    Camera camera;
    boolean hasCameraFlash = false;
    private boolean isFlashOn = false;
    Camera mCamera;
    ZoomControls zoomControls;
    MyViewModel myViewModel;
    boolean isDetected = false;
    MyDatabase mDB;
  private boolean mFlash = false;
    boolean isFlashLight = false;
    private static final String TAG = "ScanFragment";
    ImageView btn_flash_light;

    public SacnFragment() {

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sacn, container, false);

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        mDB = MyDatabase.getDatabase(getContext());
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = view.findViewById(R.id.surface_view);
        barcodeText = view.findViewById(R.id.barcode_text);
        initialiseDetectorsAndSources();
        final CameraManager cameraManager = (CameraManager)getContext().getSystemService(Context.CAMERA_SERVICE);



        btn_flash_light = view.findViewById(R.id.btn_flash_light);
        btn_flash_light.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                findCameraObject();

                Camera.Parameters parameters = mCamera.getParameters();

                if(!isFlashLight)
                {
                    isFlashLight = true;
                } else {
                    isFlashLight = false;
                }

                updateFlashLight(parameters);
            }
        });

        return view;

    }

    private void initialiseDetectorsAndSources() {
        final Context context; // before onCreate in MainActivity
        context = getContext();  // in onCreate in MainActivity
        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        barcodeText.setVisibility(View.INVISIBLE);
        cameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else
                        {
                        ActivityCompat.requestPermissions((Activity) context, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();

            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {


                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                final String[] type = {""};
                if (barcodes.size() != 0) {
                    final MyEntity favEntity = new MyEntity();

                    CameraSource.PictureCallback pictureCB = new CameraSource.PictureCallback() {

                        public void onPictureTaken(byte[] data) {
                            Bitmap srcBmp = DatabaseUtil.getImageFromBytes(data);

                            //CROPING BITMAP
                            if (srcBmp.getWidth() >= srcBmp.getHeight()) {

                                mBitmap = Bitmap.createBitmap(
                                        srcBmp,
                                        srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                                        0,
                                        srcBmp.getHeight(),
                                        srcBmp.getHeight()
                                );

                            } else {

                                mBitmap = Bitmap.createBitmap(
                                        srcBmp,
                                        0,
                                        srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                                        srcBmp.getWidth(),
                                        srcBmp.getWidth()
                                );
                            }

                            //ROTATE
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap, 512, 512, true);
                            mBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


                            cameraSource.stop();

                            if (barcodes.valueAt(0).format == Barcode.QR_CODE) {
                                //QR_
                                type[0] = "QR_CODE";
                            } else {
                                //BAR_
                                type[0] = "BAR_CODE";
                            }


                            final ScannedEntity scannedEntity = new ScannedEntity();

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
                                scannedEntity.setMessage(barcodes.valueAt(0).sms.message != null ?
                                        barcodes.valueAt(0).sms.message : "");

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
                                content_type = "Contact";
                            }

                            String content = barcodes.valueAt(0).rawValue;
                            int format = barcodes.valueAt(0).format;

                            barcodeText.setText(content);
                            //scannedEntity.setImage_bytes(DatabaseUtil.getBytesFromBitmap(mBitmap));
                            scannedEntity.setBarcodeType(type[0]);
                            scannedEntity.setContent(content);
                            scannedEntity.setContent_type(content_type);
                            scannedEntity.setFormat(format);
                            scannedEntity.setTimestamp(Time.time());

                            if (Pref_Config.isHistory(getContext())) {
                                myViewModel.insertScannedItem(scannedEntity);
                            }


                            /*barcodeDetector.release();*/

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
                                    Toast.makeText(getContext(), "Download to your PhoneStorage", Toast.LENGTH_SHORT).show();
                                    DownloadsSaveUtils.saveBitmapToImageAndGetURI(mBitmap, getContext());
                                }
                            });

                            btn_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
                                    DownloadsSaveUtils.shareImageIntent(getContext(), DownloadsSaveUtils.saveBitmapToCacheAndGetURI(mBitmap, getContext()));
                                }
                            });

                            btn_visit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (scannedEntity.getContent_type().equals("Link")) {
                                        DownloadsSaveUtils.openLinkInBrowser(getContext(), scannedEntity.getUrl());
                                    } else  {
                                        Toast.makeText(context, "No Link", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                            dialog.setOnCancelListener(
                                    new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            //When you touch outside of dialog bounds,
                                            //the dialog gets canceled and this method executes.
                                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            try {
                                                cameraSource.start(surfaceView.getHolder());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                            );

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
                            txt_content.setText(barcodes.valueAt(0).displayValue + "\nType: " + type[0]);
                            imageViewDailog.setImageBitmap(mBitmap);
                            dialog.show();

                        }
                    };

                    CameraSource.ShutterCallback shutterCallback = new CameraSource.ShutterCallback() {
                        @Override
                        public void onShutter() {

                        }
                    };

                    cameraSource.takePicture(shutterCallback, pictureCB);

                    //cameraSource.stop();
                   /* barcodeText.post(new Runnable() {
                        @Override
                        public void run()
                        {
*/
                            /*if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                            } else {
                                barcodeData = barcodes.valueAt(0).displayValue;
                            }
                            barcodeText.setText(barcodes.valueAt(0).rawValue);
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);*/



                }

                    //});
                //}
            }
        });
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
    /*private void setPic(byte[] data) {
        // Get the dimensions of the View
        int targetW = 192;
        int targetH = 192;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(data, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.deb(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }*/

    private File getOutputMediaFile(int type)
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getActivity().getPackageName());
        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.UK).format(new Date());
        if (type == MEDIA_TYPE_IMAGE)
        {
            return new File(dir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        }
        else
        {
            return null;
        }
    }


    /*public Bitmap getBitmap() {
        surfaceView.setDrawingCacheEnabled(true);
        surfaceView.buildDrawingCache();
        final Bitmap bitmap = Bitmap.createBitmap( surfaceView.getDrawingCache() );
        surfaceView.setDrawingCacheEnabled(true);
        surfaceView.destroyDrawingCache();
        return bitmap;
    }*/

    private Bitmap mBitmap;
    private static final int CAMERA_REQUEST = 1888; // field

    private void takePicture(){ //you can call this every 5 seconds using a timer or whenever you want
        Intent cameraIntent = new  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }


    @Override
    public void onPause() {
        cameraSource.release();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        barcodeDetector.release();
    }

    @Override
    public void onResume() {
        initialiseDetectorsAndSources();
        super.onResume();
    }
    private boolean findCameraObject(){
        if(cameraSource == null) {
            return false;
        }

        Field[] declaredFields = null;
        try {
            declaredFields = CameraSource.class.getDeclaredFields();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(declaredFields == null) {
            return false;
        }

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(this.cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera.setParameters(params);
                        setCamera(camera);
                        return true;
                    }

                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return false;
    }

    private void setCamera(Camera camera) {
        this.mCamera = camera;
    }

    private void updateFlashLight(Camera.Parameters parameters) {
        if(isFlashLight)
        {
            btn_flash_light.setImageResource(R.drawable.ic_flash_on);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } else {
            btn_flash_light.setImageResource(R.drawable.ic_flash_off);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }
}