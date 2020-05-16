package com.example.ripay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BusinessCardAdapter extends ArrayAdapter<Business> {
    private Context mContext;
    private List<Business> businessList = new ArrayList<>();

    public BusinessCardAdapter(@NonNull Context context, @LayoutRes ArrayList<Business> list) {
        super(context, 0, list);
        mContext = context;
        businessList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.swiper, parent, false);
        }

        Business curBusiness = businessList.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.image);
        image.setImageResource(curBusiness.getImageSrc());

        TextView name = (TextView) listItem.findViewById(R.id.businessName);
        name.setText(curBusiness.toString());

        ProgressBar progressBar = (ProgressBar) listItem.findViewById(R.id.fundProgressBar);
        progressBar.setProgress(curBusiness.getProg());

        return listItem;
    }
}
