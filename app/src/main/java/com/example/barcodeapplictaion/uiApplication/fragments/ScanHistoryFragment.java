package com.example.barcodeapplictaion.uiApplication.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcodeapplictaion.AdapterClasses.ScannedAdapter;
import com.example.barcodeapplictaion.utils.AppExecutors;
import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.utils.DownloadsSaveUtils;
import com.example.barcodeapplictaion.database.MyDatabase;
import com.example.barcodeapplictaion.database.MyEntity;
import com.example.barcodeapplictaion.database.MyViewModel;
import com.example.barcodeapplictaion.database.ScannedEntity;
import com.example.barcodeapplictaion.utils.Time;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;


public class ScanHistoryFragment extends Fragment implements ScannedAdapter.ScannedAdapterListener {

    private RecyclerView rv_scanned;
    private ScannedAdapter adapter;
    private List<ScannedEntity> scannedEntityList;

    MyViewModel myViewModel;
    MyDatabase mDB;

    public ScanHistoryFragment() {
        // Required empty public constructor
    }

    public void setScannedEntityList(List<ScannedEntity> scannedEntityList) {
        this.scannedEntityList = scannedEntityList;
        if (scannedEntityList != null)
            adapter.setScannedEntityList(scannedEntityList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scan_history, container, false);

        rv_scanned = rootView.findViewById(R.id.rv_scanned);

        scannedEntityList = new ArrayList<>();
        adapter = new ScannedAdapter(getContext(), ScanHistoryFragment.this, scannedEntityList);

        mDB = MyDatabase.getDatabase(getContext());
        AppExecutors appExecutors = AppExecutors.getInstance();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                scannedEntityList = mDB.myDao().getAllScannedList();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_scanned.setLayoutManager(layoutManager);
//                        rv_scanned.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

                        rv_scanned.setAdapter(adapter);

                        adapter.setScannedEntityList(scannedEntityList);
                    }
                });
            }
        });

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);



       /* myViewModel.getScannedItemsList().observe(getViewLifecycleOwner(), new Observer<List<ScannedEntity>>() {
            @Override
            public void onChanged(List<ScannedEntity> scannedEntities) {
                //adapter.setWordList(words);
                scannedEntityList = scannedEntities;
                adapter.setScannedEntityList(scannedEntityList);

            }
        });*/

        return rootView;
    }

    @Override
    public void onItemClicked(final ScannedEntity scannedEntity) {

        Bitmap mBitmap = null;
        if (scannedEntity.getBarcodeType().equals("QR_CODE")){
            //QR
            QRCodeWriter writer = new QRCodeWriter();

            try {
                BitMatrix bitMatrix = writer.encode(scannedEntity.getContent(), BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                mBitmap = bmp;

            } catch (WriterException e) {
                e.printStackTrace();
            }

        } else if (scannedEntity.getBarcodeType().equals("BAR_CODE")) {
            //bar
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Bitmap bitmapImage = null;
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(scannedEntity.getContent(), BarcodeFormat.CODE_128, 256, 256);
                bitmapImage = Bitmap.createBitmap(256, 256, Bitmap.Config.RGB_565);
                for (int i = 0; i<256; i++){
                    for (int j = 0; j<256; j++){
                        bitmapImage.setPixel(i,j,bitMatrix.get(i,j)? Color.BLACK:Color.WHITE);
                    }
                }
                mBitmap = bitmapImage;

            }
            catch (WriterException e)
            {
                e.printStackTrace();
            }
        }

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
        final Bitmap finalMBitmap = mBitmap;
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Download to your PhoneStorage", Toast.LENGTH_SHORT).show();
                DownloadsSaveUtils.saveBitmapToImageAndGetURI(finalMBitmap, getContext());
            }
        });


        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
                DownloadsSaveUtils.shareImageIntent(getContext(), DownloadsSaveUtils.saveBitmapToCacheAndGetURI(finalMBitmap, getContext()));
            }
        });

        btn_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scannedEntity.getContent_type().equals("Link")) {
                    DownloadsSaveUtils.openLinkInBrowser(getContext(), scannedEntity.getUrl());
                } else
                    {
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
        txt_content.setText(scannedEntity.getContent() + "\nType: "
                + scannedEntity.getContent_type());
        imageViewDailog.setImageBitmap(mBitmap);
        dialog.show();
    }

    @Override
    public void onDelete(String content) {
        myViewModel.deleteScannedItem(content);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        scannedEntityList = mDB.myDao().getAllScannedList();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setScannedEntityList(scannedEntityList);
                            }
                        });
                    }
                });
            }
        }, 600);
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