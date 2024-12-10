package com.example.barcodeapplictaion.AdapterClasses;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.database.DatabaseUtil;
import com.example.barcodeapplictaion.database.ScannedEntity;

import java.util.Collections;
import java.util.List;
public class ScannedAdapter extends RecyclerView.Adapter<ScannedAdapter.Holder> {
    private Context context;
    private List<ScannedEntity> scannedEntityList;
    ScannedAdapterListener listener;

    public ScannedAdapter(Context context, ScannedAdapterListener listener,  List<ScannedEntity> scannedEntityList) {
        this.context = context;
        this.scannedEntityList = scannedEntityList;
        Collections.reverse(scannedEntityList);
        this.listener = listener;
    }

    public interface ScannedAdapterListener {
        void onItemClicked(ScannedEntity scannedEntity);
        void onDelete(String content);
    }

    public void setScannedEntityList(List<ScannedEntity> scannedEntityList) {
        Collections.reverse(scannedEntityList);
        this.scannedEntityList = scannedEntityList;
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
        holder.txtContentType.setText(scannedEntityList.get(position).getContent_type());
        holder.txtContent.setText(scannedEntityList.get(position).getContent());
        holder.txtTimeStamp.setText(scannedEntityList.get(position).getTimestamp());
        if (scannedEntityList.get(position).getImage_bytes() != null)
            holder.imageView.setImageBitmap(DatabaseUtil.getImageFromBytes(scannedEntityList.get(position).getImage_bytes()));

        holder.imageView.setVisibility(View.GONE);
        final String barcodeType = scannedEntityList.get(position).getBarcodeType();
        final String contentType = scannedEntityList.get(position).getContent_type();
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(scannedEntityList.get(position));
            }
        });

        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(scannedEntityList.get(position).getContent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return scannedEntityList != null ? scannedEntityList.size() : 0;
    }

    public class Holder extends RecyclerView.ViewHolder {
        LinearLayout root;
        private ImageView imageView, btnDel;
        private TextView txtContentType, txtContent, txtTimeStamp;
        private CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            txtContentType = itemView.findViewById(R.id.txt_content_type);
            txtContent = itemView.findViewById(R.id.txt_content);
            txtTimeStamp = itemView.findViewById(R.id.txt_timestamp);
            imageView = itemView.findViewById(R.id.img_bitmap);
            root = itemView.findViewById(R.id.root);
            btnDel = itemView.findViewById(R.id.btn_delete);
        }
    }
}
