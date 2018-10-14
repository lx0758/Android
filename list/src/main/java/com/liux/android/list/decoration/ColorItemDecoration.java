package com.liux.android.list.decoration;

/**
 * Created by Liux on 2017/9/19.
 */

public abstract class ColorItemDecoration extends AbsItemDecoration {
    @Override
    public Decoration getItemOffsets(int position) {
        ColorDecoration decoration = new ColorDecoration();
        drawColorDecoration(position, decoration);
        return decoration;
    }

    public abstract void drawColorDecoration(int position, ColorDecoration decoration);
}
