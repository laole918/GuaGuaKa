//package com.laole918.guaguaka.view;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.drawable.Drawable;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.laole918.guaguaka.R;
//
///**
// * Created by laole918 on 2016/2/25 0025.
// */
//public class GuaGuaKaView extends View {
//
//    private Paint mPaint;
//    private Path mPath;
//    private Canvas mCanvas;
//    private Bitmap mBitmap;
//
//    private int mLastX;
//    private int mLastY;
//    private int mLastAciton;
//
//    private boolean mGgkForegroundSizeChanged;
//    private Drawable mGgkForeground;
//    private float mStrokeWidth;
//
//    // 判断遮盖层区域是否消除达到阈值
//    private volatile boolean mComplete = false;
//
//    public GuaGuaKaView(Context context) {
//        this(context, null);
//    }
//
//    public GuaGuaKaView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public GuaGuaKaView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GuaGuaKaView, defStyleAttr, 0);
//        Drawable foreground = ta.getDrawable(R.styleable.GuaGuaKaView_ggk_foreground);
//        setGgkForeground(foreground);
//        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.GuaGuaKaView_ggk_strokeWidth, 20);
//        ta.recycle();
//        init();
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
//        // 初始化我们的bitmap
//        if(mBitmap != null) {
//            mGgkForegroundSizeChanged = true;
//            if(mBitmap.getWidth() == width && mBitmap.getHeight() == height) {
//                onDrawGgkForeground(mCanvas);
//                return;
//            }
//            mBitmap.recycle();
//            mBitmap = null;
//        }
//        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        mCanvas = new Canvas(mBitmap);
//        onDrawGgkForeground(mCanvas);
//    }
//
//    private void init() {
//        mPath = new Path();
//        mPaint = new Paint();
//        // 设置绘制path画笔的一些属性
//        mPaint.setColor(Color.parseColor("#c0c0c0"));
//        mPaint.setAntiAlias(true);
//        mPaint.setDither(true);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setStrokeWidth(mStrokeWidth);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mLastX = x;
//                mLastY = y;
//                mPath.moveTo(mLastX, mLastY);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int dx = Math.abs(x - mLastX);
//                int dy = Math.abs(y - mLastY);
//                if (dx > 3 || dy > 3) {
//                    mPath.lineTo(x, y);
//                }
//                mLastX = x;
//                mLastY = y;
//                break;
//            case MotionEvent.ACTION_UP:
//                if (!mComplete && mLastAciton == MotionEvent.ACTION_MOVE) {
//                    new Thread(mRunnable).start();
//                }
//                break;
//        }
//        if (!mComplete) {
//            invalidate();
//        }
//        mLastAciton = action;
//        return true;
//    }
//
//    private Runnable mRunnable = new Runnable() {
//        @Override
//        public void run() {
//            int w = getWidth();
//            int h = getHeight();
//
//            float wipeArea = 0;
//            float totalArea = w * h;
//            Bitmap bitmap = mBitmap;
//            int[] mPixels = new int[w * h];
//
//            // 获得Bitmap上所有的像素信息
//            bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);
//
//            for (int i = 0; i < w; i++) {
//                for (int j = 0; j < h; j++) {
//                    int index = i + j * w;
//                    if (mPixels[index] == 0) {
//                        wipeArea++;
//                    }
//                }
//            }
//
//            if (wipeArea > 0 && totalArea > 0) {
//                int percent = (int) (wipeArea * 100 / totalArea);
//
//                Log.e("TAG", percent + "");
//
//                if (percent > 60) {
//                    // 清除掉图层区域
//                    mComplete = true;
//                    post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(getParent() != null) {
//                                ViewGroup parent = (ViewGroup) getParent();
//                                parent.removeView(GuaGuaKaView.this);
//                            }
//                        }
//                    });
//                }
//            }
//
//        }
//    };
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        drawPath();
//        canvas.drawBitmap(mBitmap, 0, 0, null);
//    }
//
//    private void drawPath() {
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        mCanvas.drawPath(mPath, mPaint);
//    }
//
//    public void setGgkForeground(Drawable foreground) {
//        if (mGgkForeground == null) {
//            if (foreground == null) {
//                // Nothing to do.
//                return;
//            }
//        }
//        if (foreground == mGgkForeground) {
//            // Nothing to do
//            return;
//        }
//        mGgkForeground = foreground;
//        mGgkForegroundSizeChanged = true;
////        invalidate();
//    }
//
//    public void onDrawGgkForeground(Canvas canvas) {
//        if (mGgkForeground != null) {
//            setGgkForegroundBounds();
//            mGgkForeground.draw(canvas);
//        }
//    }
//
//    private void setGgkForegroundBounds() {
////        if (mGgkForegroundSizeChanged && mGgkForeground != null) {
//            mGgkForeground.setBounds(0, 0,  getRight() - getLeft(), getBottom() - getTop());
//            mGgkForegroundSizeChanged = false;
////        }
//    }
//
//}
