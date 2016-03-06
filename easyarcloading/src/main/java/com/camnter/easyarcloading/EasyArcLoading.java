package com.camnter.easyarcloading;

import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
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

    // Default dp
    private static final float DEFAULT_EXTERNAL_ARC_WIDTH = 2.2f;
    private static final float DEFAULT_INTERNAL_ARC_WIDTH = 1.9f;
    private static final float DEFAULT_VIEW_PADDING_DP = 9.0f;
    private static final float DEFAULT_ARC_PADDING_DP = 10.0f;

    // Default px
    private float defaultViewPadding;
    private float arcPadding;

    private Paint externalPaint = new Paint();
    private Paint internalPaint = new Paint();

    private RectF externalRectF = new RectF();
    private RectF internalRectF = new RectF();

    private long lastTimeAnimated = 0;

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
        this.externalPastTime = 0.0f;
        this.internalPastTime = 0.0f;

        this.defaultViewPadding = this.dp2px(DEFAULT_VIEW_PADDING_DP);
        this.arcPadding = this.dp2px(DEFAULT_ARC_PADDING_DP);
        this.lastTimeAnimated = SystemClock.uptimeMillis();

        this.externalPaint.setStrokeWidth(this.dp2px(DEFAULT_EXTERNAL_ARC_WIDTH));
        this.externalPaint.setAntiAlias(true);
        this.externalPaint.setStyle(Paint.Style.STROKE);
        this.externalPaint.setColor(DEFAULT_EXTERNAL_ARC_COLOR);


        this.internalPaint.setStrokeWidth(this.dp2px(DEFAULT_INTERNAL_ARC_WIDTH));
        this.internalPaint.setAntiAlias(true);
        this.internalPaint.setStyle(Paint.Style.STROKE);
        this.internalPaint.setColor(DEFAULT_INTERNAL_ARC_COLOR);

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
        if (this.externalProgress > 360)
            this.externalProgress -= 360f;

        this.internalProgress += internalIncreaseProgress;
        if (this.internalProgress > 360)
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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.setupRectF();
        this.invalidate();
    }

    private void setupRectF() {
        int paddingLeft = this.getPaddingLeft();
        int paddingTop = this.getPaddingTop();
        int paddingRight = this.getPaddingRight();
        int paddingBottom = this.getPaddingBottom();

        int viewWidth = this.getWidth();
        int viewHeight = this.getHeight();

        this.externalRectF.left = paddingLeft + defaultViewPadding;
        this.externalRectF.top = paddingTop + defaultViewPadding;
        this.externalRectF.right = viewWidth - paddingRight - defaultViewPadding;
        this.externalRectF.bottom = viewHeight - paddingBottom - defaultViewPadding;

        this.internalRectF.left = this.externalRectF.left + arcPadding;
        this.internalRectF.top = this.externalRectF.top + arcPadding;
        this.internalRectF.right = this.externalRectF.right - arcPadding;
        this.internalRectF.bottom = this.externalRectF.bottom - arcPadding;
    }


    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
    }

    private class ArcProperty {
        public float startAngle;
        public float sweepAngle;

        public ArcProperty(float startAngle, float sweepAngle) {
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;
        }
    }

    private class ArcEvaluator implements TypeEvaluator<ArcProperty> {
        private ArcProperty result;

        public ArcEvaluator() {
            this.result = new ArcProperty(0.0f, 0.0f);
        }

        @Override
        public ArcProperty evaluate(float fraction, ArcProperty startValue, ArcProperty endValue) {
            this.result.startAngle = startValue.startAngle + fraction * (endValue.startAngle - startValue.startAngle);
            this.result.sweepAngle = startValue.sweepAngle + fraction * (endValue.sweepAngle - startValue.sweepAngle);
            return this.result;
        }

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
            float distance = (float) Math.cos((this.internalPastTime / EXTERNAL_ANIMATION_TIME + 1) * Math.PI) / 2 + 0.5f;
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


}
