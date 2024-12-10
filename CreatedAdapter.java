package com.example.barcodeapplictaion.AdapterClasses;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.database.CreatedEntity;
import com.example.barcodeapplictaion.database.DatabaseUtil;
import com.example.barcodeapplictaion.database.MyViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class CreatedAdapter extends RecyclerView.Adapter<CreatedAdapter.Holder> {
    private Context context;
    private List<CreatedEntity> createdEntities;
    MyViewModel myViewModel;
    CreatedListListener listListener;

    public interface CreatedListListener{
        void onItemClicked(CreatedEntity createdEntity);
    }

    public CreatedAdapter(Context context, CreatedListListener listListener, MyViewModel myViewModel, List<CreatedEntity> createdEntities) {
        this.context = context;
        this.createdEntities = createdEntities;
        Collections.reverse(createdEntities);
        this.myViewModel = myViewModel;
        this.listListener = listListener;
    }

    public void setCreatedEntityList(List<CreatedEntity> createdEntities) {
        Collections.reverse(createdEntities);
        this.createdEntities = createdEntities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
                .inflate(R.layout.item_scanned, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.txtContentType.setText(createdEntities.get(position).getContent_type());
        holder.txtContent.setText(createdEntities.get(position).getContent());
        holder.txtTimeStamp.setText(createdEntities.get(position).getTimestamp());

        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewModel.deleteCreatedItem(createdEntities.get(position).getContent());
            }
        });

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listListener.onItemClicked(createdEntities.get(position));
            }
        });

        holder.img_bitmap.setImageBitmap(DatabaseUtil.getImageFromBytes(createdEntities.get(position).getImage_bytes()));
    }

    @Override
    public int getItemCount() {
        return createdEntities != null ? createdEntities.size() : 0;
    }

    public class Holder extends RecyclerView.ViewHolder {
        LinearLayout root;
        private ImageView img_bitmap, btnDel;
        private TextView txtContentType, txtContent, txtTimeStamp;
        public Holder(@NonNull View itemView)
        {
            super(itemView);
            img_bitmap = itemView.findViewById(R.id.img_bitmap);
            txtContentType = itemView.findViewById(R.id.txt_content_type);
            txtContent = itemView.findViewById(R.id.txt_content);
            txtTimeStamp = itemView.findViewById(R.id.txt_timestamp);
            root = itemView.findViewById(R.id.root);
            btnDel = itemView.findViewById(R.id.btn_delete);
        }
    }

}
