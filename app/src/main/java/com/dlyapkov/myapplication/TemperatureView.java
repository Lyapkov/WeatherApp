package com.dlyapkov.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class TemperatureView extends View {
    private int temperatureColor = Color.GRAY;
    private int levelColor = Color.RED;
    private RectF temperatureRectangle = new RectF();
    private RectF levelRectangle = new RectF();
    private RectF headRectangle = new RectF();
    private Paint temperaturePaint;
    private Paint levelPaint;
    private int width = 0;
    private int height = 0;
    private int level = 20;

    private final static int padding = 10;
    private final static int round = 150;
    private final static int headWidth = 10;
    private final static int headHeight = 10;

    public TemperatureView(Context context) {
        super(context);
        init();
    }

    public TemperatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public TemperatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    public TemperatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    private void init() {
        temperaturePaint = new Paint();
        temperaturePaint.setColor(temperatureColor);
        temperaturePaint.setStyle(Paint.Style.FILL);
        levelPaint = new Paint();
        levelPaint.setColor(levelColor);
        levelPaint.setStyle(Paint.Style.FILL);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TemperatureView, 0, 0);
        temperatureColor = typedArray.getColor(R.styleable.TemperatureView_temperature_color, Color.GRAY);
        levelColor = typedArray.getColor(R.styleable.TemperatureView_level_color, Color.RED);
        level = typedArray.getInteger(R.styleable.TemperatureView_level, 30);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();
        temperatureRectangle.set(getPaddingLeft(),
                getPaddingTop(),
                getWidth() - getPaddingRight(),
                getHeight()  - getPaddingBottom());
//        headRectangle.set(width - padding,
//                2 * padding - headWidth + padding,
//                width - padding,
//                height - 2 * padding);
        levelRectangle.set(getPaddingLeft() + padding,
                (int) (getPaddingTop() + padding + (getHeight() * (100 - level) / 100)),
                //2 * (int) ((width - 2 * padding - headWidth) * ((double) level / (double) 100)),
                getWidth() - getPaddingRight() - padding,
                getHeight() - getPaddingBottom() - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(temperatureRectangle, round, round, temperaturePaint);
        canvas.drawRoundRect(levelRectangle, round, round, levelPaint);
        canvas.drawRoundRect(headRectangle, round, round, temperaturePaint);

//        canvas.drawRect(levelRectangle, levelPaint);
  //      canvas.drawRect(headRectangle, temperaturePaint);
    }
}
