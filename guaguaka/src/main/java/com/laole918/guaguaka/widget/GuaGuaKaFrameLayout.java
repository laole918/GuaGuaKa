package com.laole918.guaguaka.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.laole918.guaguaka.R;

/**
 * Created by laole918 on 2016/2/26 0026.
 */
public class GuaGuaKaFrameLayout extends FrameLayout {

    private final static String TAG = GuaGuaKaFrameLayout.class.getSimpleName();
    private final static Xfermode dst_out = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    private final int DEFAULT_STROKEWIDTH = 20;
    private final float DEFAULT_PERCENTAGE = 0.6f;

    private Paint mPaint;
    private Path mPath;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private int mLastX;
    private int mLastY;

    private Drawable mGgkForeground;
    private int mStrokeWidth;
    private float mPercentage;

    private boolean mHasMoved = false;
    // 判断遮盖层区域是否消除达到阈值
    private boolean mComplete = false;

    private OnWipeListener mOnWipeListener;

    public interface OnWipeListener {
        void onWipeClean();
    }

    public void setOnWipeListener(OnWipeListener listener) {
        this.mOnWipeListener = listener;
    }

    public GuaGuaKaFrameLayout(Context context) {
        this(context, null);
    }

    public GuaGuaKaFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaGuaKaFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GuaGuaKaFrameLayout, defStyleAttr, 0);
        Drawable foreground = a.getDrawable(R.styleable.GuaGuaKaFrameLayout_ggk_foreground);
        setGgkForeground(foreground);
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.GuaGuaKaFrameLayout_ggk_strokeWidth, DEFAULT_STROKEWIDTH);
        mPercentage = a.getFloat(R.styleable.GuaGuaKaFrameLayout_ggk_percentage, DEFAULT_PERCENTAGE);
        a.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 初始化我们的bitmap
        if(mBitmap != null) {
            if(mBitmap.getWidth() == width && mBitmap.getHeight() == height) {
                onDrawGgkForeground(mCanvas);
                return;
            }
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        if(mCanvas == null) {
            mCanvas = new Canvas(mBitmap);
        } else {
            mCanvas.setBitmap(mBitmap);
        }
        onDrawGgkForeground(mCanvas);
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        // 设置绘制path画笔的一些属性
//        mPaint.setColor(Color.parseColor("#c0c0c0"));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(mComplete) {
            return false;
        }
        return true;
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
                if (!mComplete) {
                    mPath.moveTo(mLastX, mLastY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = Math.abs(x - mLastX);
                int dy = Math.abs(y - mLastY);
                if ((dx > 3 || dy > 3) && !mComplete) {
                    mPath.lineTo(x, y);
                    mHasMoved = true;
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (!mComplete && mHasMoved) {
                    mHasMoved = false;
                    new Thread(mRunnable).start();
                }
                break;
        }
        if (!mComplete) {
            invalidate();
        }
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
            int [] mPixels = new int[w * h];
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
                Log.e(TAG, "wipeArea:" + percent);
                if (percent > mPercentage * 100 || percent == 100) {
                    // 清除掉图层区域
                    post(new Runnable() {
                        @Override
                        public void run() {
                            wipeGgkForeground();
                        }
                    });
                }
            }

        }
    };


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (!mComplete) {
            drawPath();
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        if(isInEditMode()) {
            onDrawGgkForeground(canvas);
        }
    }

    private void drawPath() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setXfermode(dst_out);
        mCanvas.drawPath(mPath, mPaint);
    }

    public void setGgkForeground(Drawable foreground) {
        if (mGgkForeground == null) {
            if (foreground == null) {
                // Nothing to do.
                return;
            }
        }
        if (foreground == mGgkForeground) {
            // Nothing to do
            return;
        }
        setWillNotDraw(false);
        mGgkForeground = foreground;
        invalidate();
    }

    private void onDrawGgkForeground(Canvas canvas) {
        if (mGgkForeground != null) {
            setGgkForegroundBounds();
            mGgkForeground.draw(canvas);
        }
    }

    private void setGgkForegroundBounds() {
        if (mGgkForeground != null) {
            mGgkForeground.setBounds(0, 0,  getRight() - getLeft(), getBottom() - getTop());
        }
    }

    public void wipeGgkForeground() {
        mComplete = true;
        if(mOnWipeListener != null) {
            mOnWipeListener.onWipeClean();
        }
        invalidate();
    }
}
