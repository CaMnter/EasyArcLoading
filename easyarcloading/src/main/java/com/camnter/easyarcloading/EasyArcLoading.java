package com.camnter.easyarcloading;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Description：EasyArcLoading
 * Created by：CaMnter
 * Time：2016-03-05 15:19
 */
public class EasyArcLoading extends View {

    private static final int DEFAULT_EXTERNAL_ARC_COLOR = 0xffFFFFFF;
    private static final int DEFAULT_INTERNAL_ARC_COLOR = 0xff8FC4E2;
    private DisplayMetrics mMetrics;

    /**************
     * Default dp *
     **************/
    private static final float DEFAULT_EXTERNAL_ARC_WIDTH = 0.66f;
    private static final float DEFAULT_INTERNAL_ARC_WIDTH = 0.5f;

    private static final float DEFAULT_VIEW_PADDING_DP = 3.0f;

    // 1/6
    private static final float DEFAULT_ARC_PADDING_SIDE_LENGTH_RATIO = 0.16666667f;
    private static final float DEFAULT_VIEW_WRAP_CONTENT_SIDE_LENGTH = 42.0f;

    /**************
     * Default px *
     **************/
    private float defaultViewPadding;
    private float defaultWrapContentSideLength;

    private int viewWidth;
    private int viewHeight;

    /**********
     * Custom *
     **********/
    private float easyArcPadding;
    private int eastArcExternalColor;
    private int eastArcInternalColor;
    private float eastArcExternalWidth;
    private float eastArcInternalWidth;

    /************
     * Spinning *
     ************/
    private static final float EXTERNAL_SPIN_SPEED = 600.0f;
    private static final double EXTERNAL_INCREASE_MAX_TIME = 100.0d;
    private static final double EXTERNAL_ANIMATION_TIME = 600.0d;

    private static final float INTERNAL_SPIN_SPEED = 200.0f;
    private static final double INTERNAL_INCREASE_MAX_TIME = 300.0d;
    private static final double INTERNAL_ANIMATION_TIME = 600.0d;

    private static final int ARC_MAX_LENGTH = 276;
    private static final int ARC_MIN_LENGTH = 2;

    private double externalIncreaseTime = 0.0d;
    private double externalPastTime = 0.0d;
    private boolean externalIncrease = true;
    private float externalLength = 0.0f;
    private float externalProgress = 0.0f;

    private double internalIncreaseTime = 0.0d;
    private double internalPastTime = 0.0d;
    private boolean internalIncrease = true;
    private float internalLength = 0.0f;
    private float internalProgress = 0.0f;

    private Paint externalPaint = new Paint();
    private Paint internalPaint = new Paint();

    private RectF externalRectF = new RectF();
    private RectF internalRectF = new RectF();

    private long lastTimeAnimated = 0;


    public EasyArcLoading(Context context) {
        super(context);
        this.init(context, null);
    }


    public EasyArcLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }


    public EasyArcLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EasyArcLoading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void init(Context context, AttributeSet attrs) {
        this.mMetrics = this.getResources().getDisplayMetrics();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EasyArcLoading);

        this.easyArcPadding = typedArray.getDimension(R.styleable.EasyArcLoading_easyArcPadding, this.easyArcPadding);
        this.eastArcExternalColor = typedArray.getColor(R.styleable.EasyArcLoading_eastArcExternalColor, DEFAULT_EXTERNAL_ARC_COLOR);
        this.eastArcInternalColor = typedArray.getColor(R.styleable.EasyArcLoading_eastArcInternalColor, DEFAULT_INTERNAL_ARC_COLOR);
        this.eastArcExternalWidth = typedArray.getDimension(R.styleable.EasyArcLoading_eastArcExternalWidth, this.dp2px(DEFAULT_EXTERNAL_ARC_WIDTH));
        this.eastArcInternalWidth = typedArray.getDimension(R.styleable.EasyArcLoading_eastArcInternalWidth, this.dp2px(DEFAULT_INTERNAL_ARC_WIDTH));

        this.externalPastTime = 0.0f;
        this.internalPastTime = 0.0f;

        this.lastTimeAnimated = SystemClock.uptimeMillis();
        this.defaultViewPadding = this.dp2px(DEFAULT_VIEW_PADDING_DP);
        this.defaultWrapContentSideLength = this.dp2px(DEFAULT_VIEW_WRAP_CONTENT_SIDE_LENGTH);

        this.externalPaint.setStrokeWidth(this.dp2px(this.eastArcExternalWidth));
        this.externalPaint.setAntiAlias(true);
        this.externalPaint.setStyle(Paint.Style.STROKE);
        this.externalPaint.setColor(this.eastArcExternalColor);

        this.internalPaint.setStrokeWidth(this.dp2px(this.eastArcInternalWidth));
        this.internalPaint.setAntiAlias(true);
        this.internalPaint.setStyle(Paint.Style.STROKE);
        this.internalPaint.setColor(this.eastArcInternalColor);

        typedArray.recycle();

        this.setupRectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.externalPaint.setStrokeCap(Paint.Cap.ROUND);
        this.internalPaint.setStrokeCap(Paint.Cap.ROUND);

        long timeInterval = (SystemClock.uptimeMillis() - this.lastTimeAnimated);
        float externalIncreaseProgress = timeInterval * EXTERNAL_SPIN_SPEED / 1000.0f;
        float internalIncreaseProgress = timeInterval * INTERNAL_SPIN_SPEED / 1000.0f;

        this.updateLength(timeInterval);

        this.externalProgress += externalIncreaseProgress;
        if (this.externalProgress > 360.0f)
            this.externalProgress -= 360f;

        this.internalProgress += internalIncreaseProgress;
        if (this.internalProgress > 360.0f)
            this.internalProgress -= 360f;

        this.lastTimeAnimated = SystemClock.uptimeMillis();

        float externalFrom = this.externalProgress - 120.0f;
        float externalLength = this.externalLength + ARC_MIN_LENGTH;
        float internalFrom = this.internalProgress - 120.0f;
        float internalLength = this.internalLength + ARC_MIN_LENGTH;

        canvas.drawArc(this.externalRectF, externalFrom, externalLength, false, this.externalPaint);
        canvas.drawArc(this.internalRectF, internalFrom, internalLength, false, this.internalPaint);

        this.invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        float resultWidth;
        float resultHeight;

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                resultWidth = this.defaultWrapContentSideLength;
                break;
            case MeasureSpec.EXACTLY:
            default:
                resultWidth = Math.min(this.viewWidth, this.viewHeight);
                break;
        }
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                resultHeight = this.defaultWrapContentSideLength;
                break;
            case MeasureSpec.EXACTLY:
            default:
                resultHeight = Math.min(this.viewWidth, this.viewHeight);
                break;
        }
        this.setMeasuredDimension((int) resultWidth, (int) resultHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
        this.setupRectF();
        this.invalidate();
    }

    private void setupRectF() {
        int paddingLeft = this.getPaddingLeft();
        int paddingTop = this.getPaddingTop();
        int paddingRight = this.getPaddingRight();
        int paddingBottom = this.getPaddingBottom();

        this.easyArcPadding = this.easyArcPadding > 0.0f ? this.easyArcPadding : Math.min(this.viewWidth, this.viewHeight) * DEFAULT_ARC_PADDING_SIDE_LENGTH_RATIO;

        this.externalRectF.left = paddingLeft + this.defaultViewPadding;
        this.externalRectF.top = paddingTop + this.defaultViewPadding;
        this.externalRectF.right = this.viewWidth - paddingRight - this.defaultViewPadding;
        this.externalRectF.bottom = this.viewHeight - paddingBottom - this.defaultViewPadding;

        this.internalRectF.left = this.externalRectF.left + this.easyArcPadding;
        this.internalRectF.top = this.externalRectF.top + this.easyArcPadding;
        this.internalRectF.right = this.externalRectF.right - this.easyArcPadding;
        this.internalRectF.bottom = this.externalRectF.bottom - this.easyArcPadding;

    }

    private void updateLength(long timeInterval) {
        if (this.externalIncreaseTime >= EXTERNAL_INCREASE_MAX_TIME) {
            this.externalPastTime += timeInterval;
            if (this.externalPastTime > EXTERNAL_ANIMATION_TIME) {
                this.externalPastTime -= EXTERNAL_ANIMATION_TIME;
                this.externalIncreaseTime = 0;
                this.externalIncrease = !this.externalIncrease;
            }
            float distance = (float) Math.cos((this.externalPastTime / EXTERNAL_ANIMATION_TIME + 1) * Math.PI) / 2 + 0.5f;
            float lengthRange = (ARC_MAX_LENGTH - ARC_MIN_LENGTH);

            if (this.externalIncrease) {
                this.externalLength = distance * lengthRange;
            } else {
                float decreaseLength = lengthRange * (1 - distance);
                this.externalProgress += (this.externalLength - decreaseLength);
                this.externalLength = decreaseLength;
            }
        } else {
            this.externalIncreaseTime += timeInterval;
        }

        if (this.internalIncreaseTime >= INTERNAL_INCREASE_MAX_TIME) {
            this.internalPastTime += timeInterval;
            if (this.internalPastTime > INTERNAL_ANIMATION_TIME) {
                this.internalPastTime -= INTERNAL_ANIMATION_TIME;
                this.internalIncreaseTime = 0;
                this.internalIncrease = !this.internalIncrease;
            }
            float distance = (float) Math.cos((this.internalPastTime / INTERNAL_ANIMATION_TIME + 1) * Math.PI) / 2 + 0.5f;
            float lengthRange = (ARC_MAX_LENGTH - ARC_MIN_LENGTH);

            if (this.internalIncrease) {
                this.internalLength = distance * lengthRange;
            } else {
                float decreaseLength = lengthRange * (1 - distance);
                this.internalProgress += (this.internalLength - decreaseLength);
                this.internalLength = decreaseLength;
            }
        } else {
            this.internalIncreaseTime += timeInterval;
        }
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.mMetrics);
    }

    public void setEasyArcPadding(float easyArcPadding) {
        this.easyArcPadding = easyArcPadding;
    }

    public void setEastArcExternalColor(int eastArcExternalColor) {
        this.eastArcExternalColor = eastArcExternalColor;
    }

    public void setEastArcInternalColor(int eastArcInternalColor) {
        this.eastArcInternalColor = eastArcInternalColor;
    }

    public void setEastArcExternalWidth(float eastArcExternalWidth) {
        this.eastArcExternalWidth = eastArcExternalWidth;
    }

    public void setEastArcInternalWidth(float eastArcInternalWidth) {
        this.eastArcInternalWidth = eastArcInternalWidth;
    }

}
