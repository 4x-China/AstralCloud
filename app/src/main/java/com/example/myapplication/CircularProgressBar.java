package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CircularProgressBar extends ProgressBar {

    private Paint paint;
    private RectF oval;

    public CircularProgressBar(Context context) {
        super(context);
        init();
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFF00FF00); // 设置颜色
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f); // 设置边框宽度
        paint.setAntiAlias(true);

        oval = new RectF(0, 0, getWidth(), getHeight());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float sweepAngle = 360 * getProgress() / getMax(); // 计算扫过的角度
        canvas.drawArc(oval, -90, sweepAngle, false, paint); // 绘制弧形
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        oval.set(0, 0, w, h);
    }
}
