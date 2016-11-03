package com.sirui.smartcar.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by sr06 on 2016/10/18.
 */

public class DrawCarTrackPath extends View {

    private static final int MAX_COUNT = 2;  // 贝塞尔曲线最大阶数
    private static final int REGION_WIDTH = 30;  // 合法区域宽度
    private static final int FINGER_RECT_SIZE = 60;   // 矩形尺寸
    private static final int BEZIER_WIDTH = 10;   // 贝塞尔曲线线宽
    private static final int RATE = 10; // 移动速率
    private static final int HANDLER_WHAT = 100;
    private static final int FRAME = 1000;  // 1000帧

    private float mPreX, mPreY;
    private float endX, endY;

    private Path mBezierPath = new Path();    // 贝塞尔曲线路径
    private Paint mBezierPaint = new Paint();  // 贝塞尔曲线画笔

    private ArrayList<PointF> mBezierPoints = new ArrayList<PointF>(); // 贝塞尔曲线点集
    private PointF mBezierPoint = null; // 贝塞尔曲线移动点
    private int pointNum = 0;

    private int mR = 0;  // 移动速率
    private int mRate = RATE;   // 速率
    private int mState; // 状态
    private boolean mLoop = false;  // 设置是否循环
    private int mWidth = 0, mHeight = 0;    // 画布宽高

    // flags
    private boolean isDraw = true;
    private boolean isDrawRuler = false;
    private boolean isDrawXYLine = true;



    // ---------------------------------------
    private int mScaleMargin = 16; //刻度间距
    private int mScaleHeight = 12; //刻度线的高度
    private int mScaleMaxHeight = mScaleHeight * 2; //整刻度线高度

    private int rulerTextSize = 18; // 标尺文字

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == HANDLER_WHAT) {
//                mR += mRate;
//                if (mR >= mBezierPoints.size()) {
//                    removeMessages(HANDLER_WHAT);
//                    mR = 0;
//                    mState &= ~STATE_RUNNING;
//                    mState &= ~STATE_STOP;
//                    mState |= STATE_READY | STATE_TOUCH;
//                    if (mLoop) {
//                        start();
//                    }
//                    return;
//                }


//                if (mR != mBezierPoints.size() - 1 && mR + mRate >= mBezierPoints.size()) {
//                    mR = mBezierPoints.size() - 1;
//                }
//                // Bezier点
//                mBezierPoint = new PointF(mBezierPoints.get(mR).x, mBezierPoints.get(mR).y);
//
//                if (mR == mBezierPoints.size() - 1) {
//                    mState |= STATE_STOP;
//                }
//                invalidate();
            }

        }
    };


    public DrawCarTrackPath(Context context) {
        super(context);
        init(context);
    }

    public DrawCarTrackPath(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mWidth == 0 || mHeight == 0) {
            mWidth = getWidth();
            mHeight = getHeight();

            Log.i("onMeasure", "  width=" + mWidth + "  height=" + mHeight);
        }
    }

    public void init(Context context) {

        mBezierPaint.setStyle(Paint.Style.STROKE);
        mBezierPaint.setColor(Color.BLUE);
        mBezierPaint.setStrokeWidth(8);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 判断是否可以画图，是否合法区域
        if (isDraw && isLegalFingerRegion(event.getX(), event.getY())) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    setWhetherDrawXYLine(true);

                    mBezierPath.moveTo(event.getX(), event.getY());
                    mPreX = event.getX();
                    mPreY = event.getY();
                    pointNum += 1;
                    Log.i("ACTIVON_POINT", "移动点坐标：X：" + mPreX + "  Y：" + mPreY + "  count=" + pointNum);

                    mBezierPoints.add(new PointF(mPreX, mPreY));// 添加Bezier点
                    return true;
                }
                case MotionEvent.ACTION_MOVE:
                    endX = (mPreX + event.getX()) / 2;
                    endY = (mPreY + event.getY()) / 2;
                    mBezierPath.quadTo(mPreX, mPreY, endX, endY);

                    mBezierPoints.add(new PointF(endX, endY));// 添加Bezier点
                    pointNum += 1;
                    Log.i("ACTIVON_POINT", "移动点坐标：X：" + endX + "  Y：" + endY + "  count=" + pointNum);

                    // 第一个起始点是Path.moveTo(x,y)定义的，其它部分，一个quadTo的终点，是下一个quadTo的起始点。
                    mPreX = event.getX();
                    mPreY = event.getY();
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    isDraw = false;
                    setWhetherDrawXYLine(false);

//                    int size = mBezierPoints.size();
//                    for (int i = 0; i < size; i++ ) {
//                        mBezierPoint = mBezierPoints.get(i);
//                        Log.i("ACTIVON_POINT", "移动点坐标：X：" + mBezierPoint.x + "  Y：" + mBezierPoint.y + "  count=" + i);
//                    }
                    invalidate();
                    break;
                default:
                    break;
            }
        } else {
            isDraw = false;
//            ToastUtils.showToast("请在有效范围内画图");
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画布边缘线
        Paint lPaint = new Paint();
        lPaint.setColor(Color.GRAY);
        canvas.drawLine(0, 0, mWidth, 0, lPaint);// X轴实线
        canvas.drawLine(0, 0, 0, mHeight, lPaint);// Y轴实线

        // 画贝塞尔曲线
        canvas.drawPath(mBezierPath, mBezierPaint);

        if (isDrawRuler) {
            onDrawRuler(canvas); //画刻度

            if (isDrawXYLine) {
                onDrawXYLine(canvas); // XY线条
            }
        }


    }

    /**
     * 画刻度
     */
    private void onDrawRuler(Canvas canvas) {
        if (canvas == null) return;

        Paint mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setTextAlign(Paint.Align.CENTER); //文字居中
        mPaint.setTextSize(rulerTextSize);

        for (int i = 0; i < mHeight; i++) {
            if (i != 0 && i != mHeight) {
                if (i % 10 == 0) { //整值
                    // X轴刻度
                    canvas.drawLine(i * mScaleMargin, 0, i * mScaleMargin, mScaleMaxHeight, mPaint);
                    canvas.drawText(String.valueOf(i), i * mScaleMargin, mScaleMaxHeight + rulerTextSize, mPaint);
                    // Y轴刻度
                    canvas.drawLine(0, i * mScaleMargin, mScaleMaxHeight, i * mScaleMargin, mPaint);
                    canvas.drawText(String.valueOf(i), mScaleMaxHeight + rulerTextSize, i * mScaleMargin + 5, mPaint);
                } else {
                    canvas.drawLine(i * mScaleMargin, 0, i * mScaleMargin, mScaleHeight, mPaint);// X轴刻度
                    canvas.drawLine(0, i * mScaleMargin, mScaleHeight, i * mScaleMargin , mPaint);// Y轴刻度
                }
            }
        }
    }

    /**
     * 画X横线和Y纵线
     */
    private void onDrawXYLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);

        PathEffect effects = new DashPathEffect(new float[]{10, 10, 10, 10}, 10);// 间隔数组和间隔距离
        paint.setPathEffect(effects);

        // 横线
        Path pathHorizontal = new Path();
        pathHorizontal.moveTo(0, mPreY);// 始点
        pathHorizontal.lineTo(mWidth, mPreY);
        canvas.drawPath(pathHorizontal, paint);
        // 竖线
        Path pathVertical = new Path();
        pathVertical.moveTo(mPreX, 0);// 始点
        pathVertical.lineTo(mPreX, mHeight);
        canvas.drawPath(pathVertical, paint);
    }

    /**
     * 判断手指坐标是否在合法区域中
     */
    private boolean isLegalFingerRegion(float x, float y) {
        if (x >= 0 && y >= 0 && x <= mWidth && y <= mHeight) {
            return true;
        }
        return false;
    }

    /**
     * 重新绘图
     */
    public void reset() {
        mBezierPath.reset();
        postInvalidate(); // 重新绘图
    }

    /**
     * 设置是否可以触摸画图
     */
    public void setWhetherDraw(boolean flag) {
        isDraw = flag;
    }

    /**
     * 是否显示标尺
     */
    public void setWhetherDrawRuler(boolean flag) {
        isDrawRuler = flag;
        postInvalidate(); // 重新绘图
    }

    /**
     * 是否显示XY横纵标线
     */
    public void setWhetherDrawXYLine(boolean flag) {
        isDrawXYLine = flag;
        postInvalidate(); // 重新绘图
    }



}
