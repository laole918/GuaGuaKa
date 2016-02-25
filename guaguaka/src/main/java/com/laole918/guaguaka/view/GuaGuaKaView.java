package com.laole918.guaguaka.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.laole918.guaguaka.R;

/**
 * Created by laole918 on 2016/2/25 0025.
 */
public class GuaGuaKaView extends View {

    private Paint mOutterPaint;
    private Path mPath;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private int mLastX;
    private int mLastY;

    private Drawable mForeground;
    private float mStrokeWidth;

    // 判断遮盖层区域是否消除达到阈值
    private volatile boolean mComplete = false;

    public GuaGuaKaView(Context context) {
        this(context, null);
    }

    public GuaGuaKaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaGuaKaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GuaGuaKaView, defStyleAttr, 0);
        mForeground = ta.getDrawable(R.styleable.GuaGuaKaView_ggk_foreground);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.GuaGuaKaView_ggk_strokeWidth, 20);
        ta.recycle();

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 初始化我们的bitmap
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        drawForeground(mCanvas);
    }

    /**
     * 设置绘制path画笔的一些属性
     */
    private void setupOutPaint() {
        mOutterPaint.setColor(Color.parseColor("#c0c0c0"));
        mOutterPaint.setAntiAlias(true);
        mOutterPaint.setDither(true);
        mOutterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOutterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutterPaint.setStyle(Paint.Style.FILL);
        mOutterPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = Math.abs(x - mLastX);
                int dy = Math.abs(y - mLastY);

                if (dx > 3 || dy > 3) {
                    mPath.lineTo(x, y);
                }

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                if (!mComplete)
                    new Thread(mRunnable).start();
                break;
        }
        if (!mComplete)
            invalidate();
        return true;

    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int w = getWidth();
            int h = getHeight();

            float wipeArea = 0;
            float totalArea = w * h;
            Bitmap bitmap = mBitmap;
            int[] mPixels = new int[w * h];

            // 获得Bitmap上所有的像素信息
            bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int index = i + j * w;
                    if (mPixels[index] == 0) {
                        wipeArea++;
                    }
                }
            }

            if (wipeArea > 0 && totalArea > 0) {
                int percent = (int) (wipeArea * 100 / totalArea);

                Log.e("TAG", percent + "");

                if (percent > 60) {
                    // 清除掉图层区域
                    mComplete = true;
                    postInvalidate();
                }
            }

        }
    };


    @Override
    protected void onDraw(Canvas canvas) {
        if (!mComplete) {
            drawPath();
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        if(isInEditMode()) {
            drawForeground(canvas);
        }
    }

    private void drawForeground(Canvas canvas) {
        if (mForeground != null) {
            mForeground.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
            mForeground.draw(canvas);
        }
    }

    private void drawPath() {
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawPath(mPath, mOutterPaint);
    }

    /**
     * 进行一些初始化操作
     */
    private void init() {
        mOutterPaint = new Paint();
        mPath = new Path();
        // 设置绘制path画笔的一些属性
        setupOutPaint();
    }

}
