package cn.dream.android.opengles20.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lgb on 17-6-28.
 * BezierCurve3View
 */

public class BezierCurve3View extends View {

    private int mWidth = 1800;
    private int mHeight = 1100;

    private PointF point0 = new PointF(100f, 100f);
    private PointF point1 = new PointF(200f, 100f);
    private PointF point2 = new PointF(50f, 40f);
    private PointF point3 = new PointF(360f, 160f);
    private int pointIndex;

    private Path path = new Path();
    private Paint paint = new Paint();
    private Paint textPaint = new Paint();

    public BezierCurve3View(Context context) {
        this(context, null);
    }

    public BezierCurve3View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierCurve3View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(0xff148acf);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);

        path.moveTo(point0.x, point0.y);
        path.lineTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xff148acf);
        canvas.drawPath(path, paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(0xffff3333);
        canvas.drawCircle(point0.x, point0.y, 20, paint);
        canvas.drawText("(" + point0.x + "," + point0.y + ")", point0.x, point0.y, textPaint);

        paint.setColor(0xff33ff33);
        canvas.drawCircle(point1.x, point1.y, 20, paint);
        canvas.drawText("(" + point1.x + "," + point1.y + ")", point1.x, point1.y, textPaint);

        paint.setColor(0xff3333ff);
        canvas.drawCircle(point2.x, point2.y, 20, paint);
        canvas.drawText("(" + point2.x + "," + point2.y + ")", point2.x, point2.y, textPaint);

        paint.setColor(0xffff33ff);
        canvas.drawCircle(point3.x, point3.y, 20, paint);
        canvas.drawText("(" + point3.x + "," + point3.y + ")", point3.x, point3.y, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eX = event.getX();
        float eY = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Math.abs(point0.x - eX) < 50 && Math.abs(point0.y - eY) < 50) {
                pointIndex = 0;
            } else if (Math.abs(point1.x - eX) < 50 && Math.abs(point1.y - eY) < 50) {
                pointIndex = 1;
            } else if (Math.abs(point2.x - eX) < 50 && Math.abs(point2.y - eY) < 50) {
                pointIndex = 2;
            } else if (Math.abs(point3.x - eX) < 50 && Math.abs(point3.y - eY) < 50) {
                pointIndex = 3;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pointIndex == 0) {
                point0.set(eX, eY);
            } else if (pointIndex == 1) {
                point1.set(eX, eY);
            } else if (pointIndex == 2) {
                point2.set(eX, eY);
            } else if (pointIndex == 3) {
                point3.set(eX, eY);
            }
            path.reset();
            path.moveTo(point0.x, point0.y);
            int count = (int) (1 / 0.05f);
            for (int i = 0; i <= count; i++) {
                float x1 = bezierCurveX((float) i / count);
                float y1 = bezierCurveY((float) i / count);
                path.lineTo(x1, y1);
            }
            invalidate();
        }
        return true;
    }

    private float bezierCurveX(float t) {
        float temp = 1f - t;
        return temp * temp * temp * point0.x
                + 3 * t * temp * temp * point1.x
                + 3 * t * t * temp * point2.x
                + t * t * t * point3.x;
    }

    private float bezierCurveY(float t) {
        float temp = 1f - t;
        return temp * temp * temp * point0.y + 3 * t * temp * temp * point1.y + 3 * t * t * temp * point2.y + t * t * t * point3.y;
    }
}
