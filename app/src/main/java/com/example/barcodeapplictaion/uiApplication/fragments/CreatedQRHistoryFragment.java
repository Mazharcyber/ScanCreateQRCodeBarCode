package com.example.barcodeapplictaion.uiApplication.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

import com.example.barcodeapplictaion.AdapterClasses.CreatedAdapter;
import com.example.barcodeapplictaion.utils.AppExecutors;
import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.utils.DownloadsSaveUtils;
import com.example.barcodeapplictaion.database.CreatedEntity;
import com.example.barcodeapplictaion.database.DatabaseUtil;
import com.example.barcodeapplictaion.database.MyDatabase;
import com.example.barcodeapplictaion.database.MyEntity;
import com.example.barcodeapplictaion.database.MyViewModel;
import com.example.barcodeapplictaion.utils.Time;

import java.util.ArrayList;
import java.util.List;


public class CreatedQRHistoryFragment extends Fragment implements CreatedAdapter.CreatedListListener{

    private RecyclerView rv_created;
    private CreatedAdapter adapter;
    private List<CreatedEntity> createdEntityList;

    MyViewModel myViewModel;
    MyDatabase mDB;

    public CreatedQRHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_created_q_r_history, container, false);
        rv_created = rootView.findViewById(R.id.rv_created);

        createdEntityList = new ArrayList<>();
        mDB = MyDatabase.getDatabase(getContext());
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        adapter = new CreatedAdapter(getContext(), this, myViewModel, createdEntityList);
        myViewModel.getCreatedItemsList().observe(getViewLifecycleOwner(), new Observer<List<CreatedEntity>>() {
            @Override
            public void onChanged(List<CreatedEntity> createdEntities) {
                createdEntityList = createdEntities;
                adapter.setCreatedEntityList(createdEntities);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_created.setLayoutManager(layoutManager);
        rv_created.setAdapter(adapter);
//        rv_created.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        return rootView;
    }

    @Override
    public void onItemClicked(final CreatedEntity createdEntity) {
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
                DownloadsSaveUtils.saveBitmapToImageAndGetURI(DatabaseUtil.getImageFromBytes(createdEntity.getImage_bytes()), getContext());
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
                DownloadsSaveUtils.shareImageIntent(getContext(), DownloadsSaveUtils.saveBitmapToCacheAndGetURI(DatabaseUtil.getImageFromBytes(createdEntity.getImage_bytes()), getContext()));
            }
        });

        btn_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createdEntity.getContent_type().equals("Link")) {
                    DownloadsSaveUtils.openLinkInBrowser(getContext(), createdEntity.getUrl());
                } else  {
                    Toast.makeText(getContext(), "No Link", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Remove from favorite", Toast.LENGTH_SHORT).show();

                } else {
                    //ADD
                    myViewModel.insertFavoriteItem(favEntity);
                    Toast.makeText(getContext(), "Add to favorite", Toast.LENGTH_SHORT).show();

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateFavButtonUI(createdEntity, favImageView, Flag);
                    }
                }, 500);

                favImageView.invalidate();
            }

        });

        txt_content.setText(createdEntity.getContent() + "\nType: " + createdEntity.getBarcodeType());
        imageViewDailog.setImageBitmap(DatabaseUtil.getImageFromBytes(createdEntity.getImage_bytes()));
        dialog.show();
    }

    private void updateFavButtonUI(final CreatedEntity scannedEntity, final ImageView favImageView, final boolean[] flag) {
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