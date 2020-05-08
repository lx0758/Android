package com.liux.android.mediaer.action.preview;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.liux.android.mediaer.R;

import java.util.List;

public class PreviewAdapter extends PagerAdapter {

    private List<Uri> medias;
    private View.OnClickListener onClickListener;

    public PreviewAdapter(List<Uri> medias, View.OnClickListener onClickListener) {
        this.medias = medias;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_preview, container, false);
        container.addView(view);

        PhotoView photoView = view.findViewById(R.id.pv_photo);
        photoView.setOnClickListener(onClickListener);
        Glide.with(photoView).load(medias.get(position)).fitCenter().into(photoView);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return medias.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
