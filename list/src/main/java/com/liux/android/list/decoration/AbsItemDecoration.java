package com.liux.android.list.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Liux on 2017/8/11.
 */

public abstract class AbsItemDecoration extends RecyclerView.ItemDecoration {

    private SparseArray<Decoration> mDecorations = new SparseArray<Decoration>();

    public AbsItemDecoration() {
        super();
    }

    /**
     * 可以实现类似绘制背景的效果，内容在上面
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);

            int position = parent.getChildAdapterPosition(view);

            Decoration decoration = mDecorations.get(position);

            if (decoration == null) continue;
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();

            // ChildView 的上下左右包括 Margin
            int bottom = view.getBottom() + lp.bottomMargin;
            int left = view.getLeft() - lp.leftMargin;
            int right = view.getRight() + lp.rightMargin;
            int top = view.getTop() - lp.topMargin;

            //下面的
            decoration.drawItemOffsets(c, left - decoration.left, bottom, right + decoration.right, bottom + decoration.bottom);
            //上面的
            decoration.drawItemOffsets(c, left - decoration.left, top - decoration.top, right + decoration.right, top);
            //左边的
            decoration.drawItemOffsets(c, left - decoration.left, top, left, bottom);
            //右边的
            decoration.drawItemOffsets(c, right, top, right + decoration.right, bottom);
        }
    }

    /**
     * 可以绘制在内容的上面，覆盖内容
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    /**
     * 可以实现类似padding的效果
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // 获取条目对应 position
        int position = parent.getChildAdapterPosition(view);

        Decoration decoration = getItemOffsets(position);

        if (decoration != null) {
            outRect.set(decoration.left, decoration.top, decoration.right, decoration.bottom);
        }

        mDecorations.put(position, decoration);
    }

    public abstract Decoration getItemOffsets(int position);

    public abstract static class Decoration {
        public int left, top, right, bottom;

        /**
         * 根据偏移量设定的 当前的线在界面中的坐标
         *
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        public abstract void drawItemOffsets(Canvas canvas, int left, int top, int right, int bottom);
    }

    public static class ColorDecoration extends Decoration {
        public int color = Color.BLACK;

        private Paint mPaint;

        public ColorDecoration() {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void drawItemOffsets(Canvas canvas, int left, int top, int right, int bottom) {
            mPaint.setColor(color);
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
