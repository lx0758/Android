package com.liux.android.qrcode.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.liux.android.qrcode.QRCode;
import com.liux.android.qrcode.util.SizeUtil;

public class ResultView extends View {
    private static Bitmap CACHE_BITMAP;

    private QRCode mQRCode;

    public ResultView(Context context) {
        super(context);
    }

    public QRCode getQRCode() {
        return mQRCode;
    }

    public void setQRCde(QRCode qrCode) {
        mQRCode = qrCode;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(getCacheBitmap(), 0, 0, null);
    }

    private Bitmap getCacheBitmap() {
        if (CACHE_BITMAP == null) {
            int size = SizeUtil.getResultSize(getContext());
            int circle = SizeUtil.dp2px(getContext(), 4);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.TRANSPARENT);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(size / 2, size / 2,size / 2, paint);
            paint.setColor(Color.GREEN);
            canvas.drawCircle(size / 2, size / 2,size / 2 - circle, paint);

            CACHE_BITMAP = bitmap;
        }
        return CACHE_BITMAP;
    }
}
