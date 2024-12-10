package com.example.barcodeapplictaion.AdapterClasses;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.utils.DownloadsSaveUtils;
import com.example.barcodeapplictaion.database.DatabaseUtil;
import com.example.barcodeapplictaion.database.MyEntity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Collections;
import java.util.List;


public class MyFavAdapter extends RecyclerView.Adapter<MyFavAdapter.Holder> {
    private Context context;
    private List<MyEntity> myEntities;
    FavouriteListener listener;

    public MyFavAdapter(Context context, FavouriteListener listener, List<MyEntity> myEntities) {
        this.context = context;
        this.myEntities = myEntities;
        Collections.reverse(myEntities);
        this.listener = listener;
    }

    public interface FavouriteListener{
        void onDelete(String content);
    }

    public void setScannedEntityList(List<MyEntity> myEntities) {
        Collections.reverse(myEntities);
        this.myEntities = myEntities;
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
        holder.txtContentType.setText(myEntities.get(position).getContent_type());
        holder.txtContent.setText(myEntities.get(position).getContent());
        holder.txtTimeStamp.setText(myEntities.get(position).getTimestamp());

        holder.imageView.setVisibility(View.GONE);
        holder.btn_delete.setImageResource(R.drawable.ic_favorite);
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(myEntities.get(position).getContent());
            }
        });

        final String barcodeType = myEntities.get(position).getBarcodeType();
        final String contentType = myEntities.get(position).getContent_type();
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap mBitmap = null;
                if (barcodeType.equals("QR_CODE")){
                    //QR
                    QRCodeWriter writer = new QRCodeWriter();

                    try {
                        BitMatrix bitMatrix = writer.encode(myEntities.get(position).getContent(), BarcodeFormat.QR_CODE, 512, 512);
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

                } else if (barcodeType.equals("BAR_CODE")) {
                    //bar
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    Bitmap bitmapImage = null;
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(myEntities.get(position).getContent(), BarcodeFormat.CODE_128, 256, 256);
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

                final Dialog dialog = new Dialog(context);
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
                        Toast.makeText(view.getContext(), "Download to your PhoneStorage", Toast.LENGTH_SHORT).show();
                        DownloadsSaveUtils.saveBitmapToImageAndGetURI(DatabaseUtil.getImageFromBytes(myEntities.get(position).getImage_bytes()), context);
                    }
                });

                btn_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Share", Toast.LENGTH_SHORT).show();
                        DownloadsSaveUtils.shareImageIntent(context, DownloadsSaveUtils.saveBitmapToCacheAndGetURI(DatabaseUtil.getImageFromBytes(myEntities.get(position).getImage_bytes()), context));
                    }
                });

                btn_visit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (myEntities.get(position).getContent_type().equals("Link")) {
                            DownloadsSaveUtils.openLinkInBrowser(context, myEntities.get(position).getUrl());
                        } else  {
                            Toast.makeText(context, "No Link", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                favImageView.setVisibility(View.GONE);
                txt_content.setText(myEntities.get(position).getContent() + "\nType: " + myEntities.get(position).getBarcodeType());
                imageViewDailog.setImageBitmap(mBitmap);
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return myEntities != null ? myEntities.size() : 0;
    }

    public class Holder extends RecyclerView.ViewHolder {
        LinearLayout root;
        private ImageView imageView, btn_delete;
        private TextView txtContentType, txtContent, txtTimeStamp;
        public Holder(@NonNull View itemView) {
            super(itemView);
            txtContentType = itemView.findViewById(R.id.txt_content_type);
            txtContent = itemView.findViewById(R.id.txt_content);
            txtTimeStamp = itemView.findViewById(R.id.txt_timestamp);
            imageView = itemView.findViewById(R.id.img_bitmap);
            root = itemView.findViewById(R.id.root);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
