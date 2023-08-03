package com.example.nasaimageoftheday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NasaImageAdapter extends ArrayAdapter<NasaImage> {

    private Context context;
    private ArrayList<NasaImage> nasaImages;

    public NasaImageAdapter(Context context, ArrayList<NasaImage> nasaImages) {
        super(context, 0, nasaImages);
        this.context = context;
        this.nasaImages = nasaImages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_nasa_image, parent, false);
        }

        NasaImage nasaImage = nasaImages.get(position);

        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView urlTextView = convertView.findViewById(R.id.urlTextView);

        dateTextView.setText(nasaImage.getDate());
        urlTextView.setText(nasaImage.getUrl());

        return convertView;
    }
}

