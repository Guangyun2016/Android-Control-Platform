package com.sirui.iotplatform.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wx.android.common.util.ToastUtils;

import java.util.ArrayList;

/**
 * Created by sr06 on 2016/10/18.
 */

public class DrawCarTrackPath extends View {

    private final String TAG = "DrawCarTrackPath";
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

    private ArrayList<PointF> mBezierControlPoints = new ArrayList<PointF>(); // 控制点
    private ArrayList<PointF> mBezierControlPointsPlus = new ArrayList<PointF>(); // 控制点
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

    // Paint Type
    public static final int PAINT_TYPE_LINE = 100;
    public static final int PAINT_TYPE_TRIANGLE = 101;
    public static final int PAINT_TYPE_CIRCLE = 102;
    public static final int PAINT_TYPE_RECTANGLE = 103;
    private int paintType = 0;

    //darw Circle params
    private float cx = 0;
    private float cy = 0;
    private float radius = 0;
    // draw Triangle params
    private Path pathTriangle = new Path();
    private float point_x_3 = 0;
    private float point_y_3 = 0;
    private float point_x_2 = 0;
    private float point_y_2 = 0;



    // ---------------------------------------
    private int mScaleMargin = 16; // scale spacing
    private int mScaleHeight = 12; // The height of the scale line
    private int mScaleMaxHeight = mScaleHeight * 2; // 整刻度线高度

    private int rulerTextSize = 18; // 标尺文字

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == HANDLER_WHAT) {
//                mR += mRate;
//                if (mR >= mBezierControlPoints.size()) {
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


//                if (mR != mBezierControlPoints.size() - 1 && mR + mRate >= mBezierControlPoints.size()) {
//                    mR = mBezierControlPoints.size() - 1;
//                }
//                // Bezier点
//                mBezierPoint = new PointF(mBezierControlPoints.get(mR).x, mBezierControlPoints.get(mR).y);
//
//                if (mR == mBezierControlPoints.size() - 1) {
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

    public void init(Context context) {
        paintType = PAINT_TYPE_LINE;// 默认画笔类型
        mBezierPaint.setStyle(Paint.Style.STROKE);
        mBezierPaint.setColor(Color.BLUE);
        mBezierPaint.setStrokeWidth(8);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画布边缘线
        Paint lPaint = new Paint();
        lPaint.setColor(Color.GRAY);
        canvas.drawLine(0, 0, mWidth, 0, lPaint);// X轴实线
        canvas.drawLine(0, 0, 0, mHeight, lPaint);// Y轴实线


        // 画贝塞尔曲线类型
        switch (paintType) {
            case PAINT_TYPE_LINE:
                canvas.drawPath(mBezierPath, mBezierPaint);
                break;
            case PAINT_TYPE_CIRCLE:
                canvas.drawCircle(cx, cy, radius / 2, mBezierPaint);
                Log.i(TAG, "-----drawCircle ---cx：" + cx + "---cy：" + cy + "---radius：" + radius / 2);
                break;
            case PAINT_TYPE_TRIANGLE:
                // 绘制这个三角形,你可以绘制任意多边形
                canvas.drawPath(pathTriangle, mBezierPaint);
                break;
            case PAINT_TYPE_RECTANGLE:
//                canvas.drawRect(new Rect((int)mPreX, (int)mPreY, (int)endX, (int)endY), mBezierPaint);
                if (mPreX > endX && mPreY > endY) { // 往左上
                    canvas.drawRect(new RectF(endX, endY, mPreX, mPreY), mBezierPaint);
                } else if (mPreX < endX && mPreY > endY) {// 往右上
                    canvas.drawRect(new RectF(mPreX, endY, endX, mPreY), mBezierPaint);
                } else if (mPreX > endX && mPreY < endY) {// 往左下
                    canvas.drawRect(new RectF(endX, mPreY, mPreX, endY), mBezierPaint);
                } else if (mPreX < endX && mPreY < endY) {// 往右下
                    canvas.drawRect(new RectF(mPreX, mPreY, endX, endY), mBezierPaint);
                }
                break;
        }

        if (isDrawRuler) {
            onDrawRuler(canvas); //画刻度

            if (isDrawXYLine) {
                onDrawXYLine(canvas); // XY线条
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Whether can draw, judge legal area
        if (isDraw && isLegalFingerRegion(event.getX(), event.getY())) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    setWhetherDrawXYLine(true);

                    if (paintType == PAINT_TYPE_LINE) {
                        drawFreeLineActionDown(event);
                    }
                    if (paintType == PAINT_TYPE_CIRCLE) {
                        cx = event.getX();
                        cy = event.getY();
//                        Log.i(TAG, "-----Circle down ---cx：" + cx + "---cy：" + cy);
                    }
                    if (paintType == PAINT_TYPE_TRIANGLE) {
                        mPreX = event.getX();
                        mPreY = event.getY();
                    }
                    if (paintType == PAINT_TYPE_RECTANGLE) {
                        mPreX = event.getX();
                        mPreY = event.getY();
                    }

                    mListener.onDrawPathMotionEventDown();
                    return true;
                }
                case MotionEvent.ACTION_MOVE:

                    if (paintType == PAINT_TYPE_LINE) {
                        drawFreeLineActionMove(event);
                    }
                    if (paintType == PAINT_TYPE_CIRCLE) {
                        endX = event.getX();
                        endY = event.getY();
//                        Log.i(TAG, "-----Circle move ---endX：" + endX + "---endY：" + endY);
                        radius = (float) DistanceOfTwoPoints(mPreX, mPreY, endX, endY);
                    }
                    if (paintType == PAINT_TYPE_TRIANGLE) {
                        endX = event.getX();
                        endY = event.getY();
                        point_x_2 = endX;
                        point_y_2 = endY;

                        float deltaX = endX - mPreX;
                        float deltaY = endY - mPreY;
                        float frac = (float) Math.sqrt(3) / 2;
                        // The third point coordinates
                        point_x_3 = mPreX + (float) (Math.sqrt(1 - Math.pow(frac, 2)) * deltaX - frac * deltaY);
                        point_y_3 = mPreY + (float) (Math.sqrt(1 - Math.pow(frac, 2)) * deltaY + frac * deltaX);

                        // when everytime move and draw
                        pathTriangle.reset();
                        pathTriangle.moveTo(mPreX, mPreY);
                        pathTriangle.lineTo(point_x_2, point_y_2);
                        pathTriangle.lineTo(point_x_3, point_y_3);
                        pathTriangle.close();// Make these points closed polygons
                    }
                    if (paintType == PAINT_TYPE_RECTANGLE) {
                        endX = event.getX();
                        endY = event.getY();
                    }

                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    isDraw = false;
                    setWhetherDrawXYLine(false);
                    if (paintType == PAINT_TYPE_LINE) {
                        drawFreeLineActionUp();
                    }
                    if (paintType == PAINT_TYPE_CIRCLE) {
                    }
                    if (paintType == PAINT_TYPE_TRIANGLE) {
                        // when motionevent is up, clear before and draw the last
                        pathTriangle.reset();
                        pathTriangle.moveTo(mPreX, mPreY);
                        pathTriangle.lineTo(point_x_2, point_y_2);
                        pathTriangle.lineTo(point_x_3, point_y_3);
                        pathTriangle.close();// Make these points closed polygons
                    }
                    if (paintType == PAINT_TYPE_RECTANGLE) {
//                        reset();
                        endX = event.getX();
                        endY = event.getY();
                    }


                    mListener.onDrawPathMotionEventUp();
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

    private void drawFreeLineActionUp() {
        reset();
        mBezierPath.moveTo(mBezierControlPoints.get(0).x, mBezierControlPoints.get(0).y);
        mBezierControlPointsPlus.add(new PointF(mBezierControlPoints.get(0).x, mBezierControlPoints.get(0).y));

        int ctr = 0;
        PointF[] point = new PointF[2];

        for (int i = 1; i < mBezierControlPoints.size(); i++) {
            point[ctr] = mBezierControlPoints.get(i);
            if (ctr == 1) {
                mPreX = point[0].x;
                mPreY = point[0].y;

                endX = (point[0].x + point[1].x) / 2;
                endY = (point[0].y + point[1].y) / 2;
//                            endX = point[1].x;
//                            endY = point[1].y;

                mBezierPath.quadTo(mPreX, mPreY, endX, endY);// 二阶贝塞尔曲线
                mBezierControlPointsPlus.add(new PointF(mPreX, mPreY));
                mBezierControlPointsPlus.add(new PointF(endX, endY));

                point[0] = new PointF(endX, endY);
                ctr = 0;
            }
            ctr++;
        }
    }

    private void drawFreeLineActionMove(MotionEvent event) {
        endX = (mPreX + event.getX()) / 2;
        endY = (mPreY + event.getY()) / 2;
//                    mBezierPath.quadTo(mPreX, mPreY, endX, endY);// 二阶贝塞尔曲线
//                    mBezierControlPoints.add(new PointF(endX, endY));// 添加Bezier控制点

        mBezierControlPoints.add(new PointF(event.getX(), event.getY()));// 添加Bezier控制点
        mBezierPath.lineTo(event.getX(), event.getY());

        pointNum += 1;
        Log.i("ACTIVON_POINT", "移动点坐标：X：" + endX + "  Y：" + endY + "  count=" + pointNum);

        // 第一个起始点是Path.moveTo(x,y)定义的，其它部分，一个quadTo的终点，是下一个quadTo的起始点。
        mPreX = event.getX();
        mPreY = event.getY();
    }

    private void drawFreeLineActionDown(MotionEvent event) {
        mBezierPath.reset();
        mBezierPath.moveTo(event.getX(), event.getY());
        mPreX = event.getX();
        mPreY = event.getY();

        pointNum += 1;
        Log.i("ACTIVON_POINT", "移动点坐标：X：" + mPreX + "  Y：" + mPreY + "  count=" + pointNum);
        mBezierControlPointsPlus.clear();
        mBezierControlPoints.clear();
        mBezierControlPoints.add(new PointF(mPreX, mPreY));// 添加Bezier控制点
    }

    /**
     * 坐标两点间距离
     */
    public double DistanceOfTwoPoints(double x1, double y1, double x2, double y2) {
        double countX = x1 - x2;
        double countY = y1 - y2;
        double sum = Math.pow(countX, 2) + Math.pow(countY, 2);
        return Math.sqrt(sum);
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
        pathHorizontal.reset();
        pathHorizontal.moveTo(0, mPreY);// 始点
        pathHorizontal.lineTo(mWidth, mPreY);
        canvas.drawPath(pathHorizontal, paint);
        // 竖线
        Path pathVertical = new Path();
        pathVertical.reset();
        pathVertical.moveTo(mPreX, 0);// 始点
        pathVertical.lineTo(mPreX, mHeight);
        canvas.drawPath(pathVertical, paint);
    }

    /** 判断手指坐标是否在合法区域中
     */
    private boolean isLegalFingerRegion(float x, float y) {
        if (x >= 0 && y >= 0 && x <= mWidth && y <= mHeight) {
            return true;
        }
        return false;
    }

    /** 重新绘图
     */
    public void reset() {
        this.mBezierPath.reset();
        // clear
        mPreX = 0;
        mPreY = 0;
        endX = 0;
        endY = 0;
        // Circle
        cx = 0;
        cy = 0;
        radius = 0;
        // Triangle
        pathTriangle.reset();

        postInvalidate(); // 重新绘图
    }

    /** 设置是否可以触摸画图
     */
    public void setWhetherDraw(boolean flag) {
        this.isDraw = flag;
    }

    /** 是否显示标尺
     */
    public void setWhetherDrawRuler(boolean flag) {
        this.isDrawRuler = flag;
        postInvalidate(); // 重新绘图
    }

    /** 是否显示XY横纵标线
     */
    public void setWhetherDrawXYLine(boolean flag) {
        this.isDrawXYLine = flag;
        postInvalidate(); // 重新绘图
    }

    /**  设置画笔类型
     */
    public void setPaintType(int paintType) {
        this.paintType = paintType;
//        reset();
        switch(paintType) {
            case PAINT_TYPE_CIRCLE:
                ToastUtils.showToast("圆形画笔");
                break;
            case PAINT_TYPE_TRIANGLE:
                ToastUtils.showToast("三角形画笔");
                break;
            case PAINT_TYPE_RECTANGLE:
                ToastUtils.showToast("矩形画笔");
                break;
            case PAINT_TYPE_LINE:
                ToastUtils.showToast("线画笔");
                break;
        }
    }

    private onDrawCarPathViewClickListener mListener;

    public void setOnCarPathViewClickListener(onDrawCarPathViewClickListener listener) {
        this.mListener = listener;
    }

    public interface onDrawCarPathViewClickListener {
        void onDrawPathViewClick();
        void onDrawPathMotionEventUp();
        void onDrawPathMotionEventDown();
    }



}
