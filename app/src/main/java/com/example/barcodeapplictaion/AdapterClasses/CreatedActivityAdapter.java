package com.example.barcodeapplictaion.AdapterClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barcodeapplictaion.R;
import com.example.barcodeapplictaion.model.QrModelAnimated;

import java.util.List;

public class CreatedActivityAdapter extends RecyclerView.Adapter<CreatedActivityAdapter.plateViewHolder> {
    private List<QrModelAnimated> qrModelAnimatedList;
    private Context context;
    private AdapterListener listener;

    int scrollPosition = 0;

    private String contentType;

    public interface AdapterListener {
        void onContentTypeChanged(String content_type);
        void scrollTo(int position);
    }

    public CreatedActivityAdapter(String contentType, List<QrModelAnimated> qrModelAnimatedList, Context context) {
        this.qrModelAnimatedList = qrModelAnimatedList;
        this.context = context;
        this.contentType = contentType;
        listener = (AdapterListener) context;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        notifyDataSetChanged();
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public plateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_plates,viewGroup,false);
        return new plateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull plateViewHolder holder, final int position) {
        final QrModelAnimated qrModelAnimated = qrModelAnimatedList.get(position);
        Glide.with(context.getApplicationContext()).load(qrModelAnimated.getPlate_imag()).into(holder.plateimg);
        holder.textViews.setText(qrModelAnimated.getTitleText());

        if (qrModelAnimated.getTitleText().equals(contentType)) {
            scrollPosition = position;
            //listener.scrollTo(scrollPosition);
            holder.linearLyout.setBackground(context.getResources().getDrawable(R.drawable.card_aniamte));
            holder.textViews.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }
        else
            {
          holder.linearLyout.setBackground(context.getResources().getDrawable(R.drawable.card_all));
                holder.textViews.setTextColor(context.getResources().getColor(R.color.colorBlack));
        }
        //listener.scrollTo(scrollPosition != 0 ? scrollPosition : 1);
        holder.plateimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                listener.onContentTypeChanged(qrModelAnimated.getTitleText());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return qrModelAnimatedList.size();
    }
    public static class plateViewHolder extends RecyclerView.ViewHolder {
        private ImageView plateimg;
        private TextView textViews;
        LinearLayout linearLyout;
        RelativeLayout relativeLayout;
        public plateViewHolder(@NonNull View itemView) {
            super(itemView);
            plateimg = itemView.findViewById(R.id.imageViews);
            textViews = itemView.findViewById(R.id.textViewTittle);
            linearLyout = itemView.findViewById(R.id.linearTouch);
            relativeLayout = itemView.findViewById(R.id.reltiveTouch);
        }
    }
}
