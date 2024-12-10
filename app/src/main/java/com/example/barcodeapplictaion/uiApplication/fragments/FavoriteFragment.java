package com.example.barcodeapplictaion.uiApplication.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.barcodeapplictaion.AdapterClasses.MyFavAdapter;
import com.example.barcodeapplictaion.utils.AppExecutors;
import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.database.MyDatabase;
import com.example.barcodeapplictaion.database.MyEntity;
import com.example.barcodeapplictaion.database.MyViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment implements MyFavAdapter.FavouriteListener {
    RecyclerView recyclerViewFav;
    TextView textView;

    private List<MyEntity> favList;

    MyViewModel myViewModel;
    MyDatabase mDB;
    MyFavAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerViewFav = rootView.findViewById(R.id.favoriteRecyclerView);

        favList = new ArrayList<>();
        adapter = new MyFavAdapter(getContext(), FavoriteFragment.this,  favList);

        mDB = MyDatabase.getDatabase(getContext());
        AppExecutors appExecutors = AppExecutors.getInstance();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favList = mDB.myDao().getFavItems();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerViewFav.setLayoutManager(layoutManager);
                        recyclerViewFav.setAdapter(adapter);
                        adapter.setScannedEntityList(favList);
//                        recyclerViewFav.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
                    }
                });
            }
        });

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        return rootView;

    }


    @Override
    public void onDelete(String content) {
        myViewModel.deleteFavoriteItem(content);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        favList = mDB.myDao().getFavItems();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setScannedEntityList(favList);
                            }
                        });
                    }
                });
            }
        }, 400);

    }
}