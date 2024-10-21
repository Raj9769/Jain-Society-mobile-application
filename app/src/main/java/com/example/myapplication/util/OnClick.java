package com.example.myapplication.util;

import android.widget.ImageView;

public interface OnClick {
    void saveClick(ImageView imageView,ImageView imageView1, int position);

    void onDownload(int position);
}
