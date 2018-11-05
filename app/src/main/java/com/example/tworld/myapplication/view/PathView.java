package com.example.tworld.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by wing on 16/3/30.
 */
public class PathView extends CardiographView {

    // x单位长度
    private final int X_UNIT = 22;

    // 每次取几个队列元素
    private final int NUM_PER_ROUND = 1;

    private int baseX = 0;

    private int baseY = 0;

    private int intervalMs = 600;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        baseY = mHeight / 2;
    }


    private Queue<Integer> yq;

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPath = new Path();

        //设置画笔style
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(5);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        yq = new ArrayDeque<>();

        //用path模拟一个心电图样式
        mPath.moveTo(0, mHeight / 2);

        // 首先生成一条跨屏的直线
        mPath.lineTo(mWidth, mHeight / 2);
        baseX = mWidth;
    }

    /**
     * 接受新脉搏
     */
    public void receiveNewLocation(int y) {
        yq.add(y);
    }

    private void drawPath(Canvas canvas) {
        int baseY = mHeight / 2;

        // 取特定个数队列元素

        for (int i = 0; i < NUM_PER_ROUND; i++) {
            if (!yq.isEmpty()) {
                Integer y = yq.poll();
                assert y != null;
                mPath.lineTo(baseX, baseY - (y - 300));
            } else {
                mPath.lineTo(baseX, baseY);
            }
            baseX += X_UNIT;
        }

        //设置画笔style
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(5);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPath(canvas);
        if (baseX >= mWidth) {
            // 强制刷新
            baseX = 0;
            mPath.reset();
            mPath.moveTo(0, baseY);
        }
        // 每x毫秒画一次
        postInvalidateDelayed(intervalMs);

    }
}

