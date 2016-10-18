package com.sirui.smartcar.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sirui.smartcar.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/12 0012.
 */
public class MyCanvasView extends View {

    private Paint mPaint = null; // 普通画笔
    private static final int REGION_WIDTH = 30;  // 合法区域宽度
    private static final int FINGER_RECT_SIZE = 60;   // 矩形尺寸
    private static final int BEZIER_WIDTH = 10;   // 贝塞尔曲线线宽
    private static final int TEXT_HEIGHT = 60;  // 文本高度
    private static final int RATE = 10; // 移动速率
    private static final int HANDLER_WHAT = 100;
    private static final int FRAME = 1000;  // 1000帧
    private static final int STATE_READY = 0x0001;
    private static final int STATE_RUNNING = 0x0002;
    private static final int STATE_STOP = 0x0004;
    private static final int STATE_TOUCH = 0x0010;

    private Path mBezierPath = null;    // 贝塞尔曲线路径

    private Paint mBezierPaint = null;  // 贝塞尔曲线画笔
    private Paint mMovingPaint = null;  // 移动点画笔
    private Paint mTextPaint = null;    // 文字画笔

    private ArrayList<PointF> mBezierPoints = null; // 贝塞尔曲线点集
    private PointF mBezierPoint = null; // 贝塞尔曲线移动点

    private ArrayList<PointF> mControlPoints = null;    // 控制点集

    private ArrayList<ArrayList<PointF>> mInstantTangentPoints;

    private int mR = 0;  // 移动速率

    private int mRate = RATE;   // 速率

    private int mState; // 状态

    private boolean mLoop = false;  // 设置是否循环

    private int mWidth = 0, mHeight = 0;    // 画布宽高

    private PointF mCurPoint; // 当前移动的控制点

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_WHAT) {
                mR += mRate;
                if (mR >= mBezierPoints.size()) {
                    removeMessages(HANDLER_WHAT);
                    mR = 0;
                    mState &= ~STATE_RUNNING;
                    mState &= ~STATE_STOP;
                    mState |= STATE_READY | STATE_TOUCH;
                    if (mLoop) {
                        start();
                    }
                    return;
                }
                if (mR != mBezierPoints.size() - 1 && mR + mRate >= mBezierPoints.size()) {
                    mR = mBezierPoints.size() - 1;
                }
                // Bezier点
                mBezierPoint = new PointF(mBezierPoints.get(mR).x, mBezierPoints.get(mR).y);

                if (mR == mBezierPoints.size() - 1) {
                    mState |= STATE_STOP;
                }
                invalidate();
            }
        }
    };

    public MyCanvasView(Context context) {
        super(context);
        init();
    }

    public MyCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 创建Bezier点集
     *
     * @return
     */
    private ArrayList<PointF> buildBezierPoints() {
        ArrayList<PointF> points = new ArrayList<>();
        int order = mControlPoints.size() - 1;
        float delta = 1.0f / FRAME;
        for (float t = 0; t <= 1; t += delta) {
            // Bezier点集
            points.add(new PointF(deCasteljauX(order, 0, t), deCasteljauY(order, 0, t)));
        }
        return points;
    }

    /**
     * deCasteljau算法
     *
     * @param i 阶数
     * @param j 点
     * @param t 时间
     * @return
     */
    private float deCasteljauX(int i, int j, float t) {
        if (i == 1) {
            return (1 - t) * mControlPoints.get(j).x + t * mControlPoints.get(j + 1).x;
        }
        return (1 - t) * deCasteljauX(i - 1, j, t) + t * deCasteljauX(i - 1, j + 1, t);
    }

    /**
     * deCasteljau算法
     *
     * @param i 阶数
     * @param j 点
     * @param t 时间
     * @return
     */
    private float deCasteljauY(int i, int j, float t) {
        if (i == 1) {
            return (1 - t) * mControlPoints.get(j).y + t * mControlPoints.get(j + 1).y;
        }
        return (1 - t) * deCasteljauY(i - 1, j, t) + t * deCasteljauY(i - 1, j + 1, t);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mWidth == 0 || mHeight == 0) {
            mWidth = getWidth();
            mHeight = getHeight();
        }
    }

    /**
     * 判断坐标是否在合法区域中
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isLegalTouchRegion(float x, float y) {
        if (x <= REGION_WIDTH || x >= mWidth - REGION_WIDTH || y <= REGION_WIDTH || y >= mHeight - REGION_WIDTH) {
            return false;
        }
        RectF rectF = new RectF();
        for (PointF point : mControlPoints) {
            if (mCurPoint != null && mCurPoint.equals(point)) { // 判断是否是当前控制点
                continue;
            }
            rectF.set(point.x - REGION_WIDTH, point.y - REGION_WIDTH, point.x + REGION_WIDTH, point.y + REGION_WIDTH);
            if (rectF.contains(x, y)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取合法控制点
     *
     * @param x
     * @param y
     * @return
     */
    private PointF getLegalControlPoint(float x, float y) {
        RectF rectF = new RectF();
        for (PointF point : mControlPoints) {
            rectF.set(point.x - REGION_WIDTH, point.y - REGION_WIDTH, point.x + REGION_WIDTH, point.y + REGION_WIDTH);
            if (rectF.contains(x, y)) {
                return point;
            }
        }
        return null;
    }


    /**
     * 判断手指坐标是否在合法区域中
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isLegalFingerRegion(float x, float y) {
        if (mCurPoint != null) {
            RectF rectF = new RectF(mCurPoint.x - FINGER_RECT_SIZE / 2, mCurPoint.y - FINGER_RECT_SIZE / 2, mCurPoint
                    .x +
                    FINGER_RECT_SIZE / 2, mCurPoint.y +
                    FINGER_RECT_SIZE / 2);
            if (rectF.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    private void init() {
        mPaint = new Paint();
        //setARGB(int a, int r, int g, int b) : 设置ARGB颜色。
        mPaint.setColor(getResources().getColor(R.color.black));//setColor(int color) : 设置颜色。
        //setAlpha(int a) : 设置透明度。
        mPaint.setAntiAlias(true);//setAntiAlias(boolean aa) : 设置是否抗锯齿。
        //setShader(Shader shader) : 设置Paint的填充效果。
        mPaint.setStrokeWidth(10);//setStrokeWidth(float width) : 设置Paint的笔触宽度。
        //setStyle(Paint.Style style) : 设置Paint的填充风格。
        //setTextSize(float textSize) : 设置绘制文本时的文字大小。


        // 贝塞尔曲线画笔
        mBezierPaint = new Paint();
        mBezierPaint.setColor(Color.RED);
        mBezierPaint.setStrokeWidth(BEZIER_WIDTH);
        mBezierPaint.setStyle(Paint.Style.STROKE);
        mBezierPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        //  设置背景色
//        canvas.drawColor(Color.WHITE);
//
//        //  绘制一条线
//        mPaint.setColor(Color.BLACK);
//        mPaint.setStrokeWidth(5);
//        canvas.drawLine(0, 0, 100, 100, mPaint);
//
//        //  绘制一个矩形
//        mPaint.setColor(Color.YELLOW);
//        canvas.drawRect(0, 120, 100, 200, mPaint);
//
//        //  绘制一个圆
//        mPaint.setColor(Color.BLUE);
//        canvas.drawCircle(380, 130, 50, mPaint);
//
//        //  绘制一个椭圆
//        mPaint.setColor(Color.CYAN);
//        canvas.drawOval(new RectF(300, 370, 120, 100), mPaint);
//
//        //  绘制一个多边形
//        mPaint.setColor(Color.BLACK);
//        Path path = new Path();
//        path.moveTo(100, 380);
//        path.lineTo(100 + 45, 380);
//        path.lineTo(100 + 30, 380 + 50);
//        path.lineTo(100 + 15, 380 + 50);
//        path.close();
//        canvas.drawPath(path, mPaint);

        if (isRunning() && !isTouchable()) {
            if (mBezierPoint == null) {
                mBezierPath.reset();
                mBezierPoint = mBezierPoints.get(0);
                mBezierPath.moveTo(mBezierPoint.x, mBezierPoint.y);
            }

            // Bezier曲线
            mBezierPath.lineTo(mBezierPoint.x, mBezierPoint.y);
            canvas.drawPath(mBezierPath, mBezierPaint);
            // Bezier曲线起始移动点
//            canvas.drawCircle(mBezierPoint.x, mBezierPoint.y, CONTROL_RADIUS, mMovingPaint);
            // 时间展示
            canvas.drawText("t:" + (new DecimalFormat("##0.000").format((float) mR / FRAME)), mWidth - TEXT_HEIGHT *
                    3, mHeight - TEXT_HEIGHT, mTextPaint);

            mHandler.removeMessages(HANDLER_WHAT);
            mHandler.sendEmptyMessage(HANDLER_WHAT);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();
        if (!isTouchable()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mState &= ~STATE_READY;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                if (mCurPoint == null) {
                    mCurPoint = getLegalControlPoint(x, y);
                }
                if (mCurPoint != null && isLegalTouchRegion(x, y)) {  // 判断手指移动区域是否合法
                    if (isLegalFingerRegion(x, y)) {    // 判断手指触摸区域是否合法
                        mCurPoint.x = x;
                        mCurPoint.y = y;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurPoint = null;
                mState |= STATE_READY;
                break;
        }
        return true;
    }

    private boolean isReady() {
        return (mState & STATE_READY) == STATE_READY;
    }

    private boolean isRunning() {
        return (mState & STATE_RUNNING) == STATE_RUNNING;
    }

    private boolean isTouchable() {
        return (mState & STATE_TOUCH) == STATE_TOUCH;
    }

    private boolean isStop() {
        return (mState & STATE_STOP) == STATE_STOP;
    }

    /**
     * 开始
     */
    public void start() {
        if (isReady()) {
            mBezierPoint = null;
            mInstantTangentPoints = null;
            mBezierPoints = buildBezierPoints();

            mState &= ~STATE_READY;
            mState &= ~STATE_TOUCH;
            mState |= STATE_RUNNING;
            invalidate();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (isRunning()) {
            mHandler.removeMessages(HANDLER_WHAT);
            mR = 0;
            mState &= ~STATE_RUNNING;
            mState &= ~STATE_STOP;
            mState |= STATE_READY | STATE_TOUCH;
            invalidate();
        }
    }

    /**
     * 设置移动速率
     *
     * @param rate
     */
    public void setRate(int rate) {
        mRate = rate;
    }

    /**
     * 设置是否循环
     *
     * @param loop
     */
    public void setLoop(boolean loop) {
        mLoop = loop;
    }

}
