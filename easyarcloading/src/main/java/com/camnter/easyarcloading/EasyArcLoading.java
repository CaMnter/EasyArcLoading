package com.camnter.easyarcloading;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

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

    private static final float[] DEFAULT_EXTERNAL_DECREASE_START_ANGLE = {
            0.0f, 246.0f, 360.0f
    };
    private static final float[] DEFAULT_EXTERNAL_DECREASE_SWEEP_ANGLE = {
            1.0f, 240.0f, 340.0f
    };
    private static final float[] DEFAULT_INTERNAL_DECREASE_START_ANGLE = {
            0.0f, 246.0f, 360.0f
    };
    private static final float[] DEFAULT_INTERNAL_DECREASE_SWEEP_ANGLE = {
            1.0f, 240f, 270f
    };


    private static final int DECREASE_MAX_LENGTH = DEFAULT_EXTERNAL_DECREASE_START_ANGLE.length;
    private static final int INCREASE_MAX_LENGTH = DEFAULT_INTERNAL_DECREASE_START_ANGLE.length;
    private Object[] decreaseArc = new ArcProperty[DECREASE_MAX_LENGTH];
    private Object[] increaseArc = new ArcProperty[INCREASE_MAX_LENGTH];

    private float arcPadding;

    private Paint externalPaint = new Paint();
    private Paint internalPaint = new Paint();

    private RectF externalRectF = new RectF();
    private RectF internalRectF = new RectF();

    private float currentExternalStart;
    private float currentExternalSweep;

    private float currentInternalStart;
    private float currentInternalSweep;


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
        this.defaultViewPadding = this.dp2px(DEFAULT_VIEW_PADDING_DP);
        this.arcPadding = this.dp2px(DEFAULT_ARC_PADDING_DP);

        this.externalPaint.setStrokeWidth(this.dp2px(DEFAULT_EXTERNAL_ARC_WIDTH));
        this.externalPaint.setAntiAlias(true);
        this.externalPaint.setStyle(Paint.Style.STROKE);
        this.externalPaint.setColor(DEFAULT_EXTERNAL_ARC_COLOR);


        this.internalPaint.setStrokeWidth(this.dp2px(DEFAULT_INTERNAL_ARC_WIDTH));
        this.internalPaint.setAntiAlias(true);
        this.internalPaint.setStyle(Paint.Style.STROKE);
        this.internalPaint.setColor(DEFAULT_INTERNAL_ARC_COLOR);

        this.setupRectF();

        for (int i = 0; i < DEFAULT_EXTERNAL_DECREASE_START_ANGLE.length; i++) {
            this.decreaseArc[i] = new ArcProperty(DEFAULT_EXTERNAL_DECREASE_START_ANGLE[i], DEFAULT_EXTERNAL_DECREASE_SWEEP_ANGLE[i]);
        }
        for (int i = 0; i < DEFAULT_INTERNAL_DECREASE_START_ANGLE.length; i++) {
            this.increaseArc[i] = new ArcProperty(DEFAULT_INTERNAL_DECREASE_START_ANGLE[i], DEFAULT_INTERNAL_DECREASE_SWEEP_ANGLE[i]);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.externalPaint.setStrokeCap(Paint.Cap.ROUND);
        this.internalPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawArc(this.externalRectF, this.currentExternalStart, this.currentExternalSweep, false, this.externalPaint);
        canvas.drawArc(this.internalRectF, this.currentInternalStart, this.currentInternalSweep, false, this.internalPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.setupRectF();
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


    private void play() {
        ValueAnimator exAnimator = ValueAnimator.ofObject(new ArcEvaluator(), this.decreaseArc);
        exAnimator.setDuration(800);
        exAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ArcProperty current = (ArcProperty) animation.getAnimatedValue();
                currentExternalStart = current.startAngle;
                currentExternalSweep = current.sweepAngle;
                invalidate();
            }
        });
        exAnimator.setRepeatCount(ValueAnimator.INFINITE);
        exAnimator.setRepeatMode(ValueAnimator.RESTART);
        exAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        exAnimator.start();

        ValueAnimator inAnimator = ValueAnimator.ofObject(new ArcEvaluator(), this.increaseArc);
        inAnimator.setDuration(800);
        inAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ArcProperty current = (ArcProperty) animation.getAnimatedValue();
                currentInternalStart = current.startAngle;
                currentInternalSweep = current.sweepAngle;
                invalidate();
            }
        });
        inAnimator.setRepeatCount(ValueAnimator.INFINITE);
        inAnimator.setRepeatMode(ValueAnimator.RESTART);
        inAnimator.setInterpolator(new AccelerateInterpolator());
        inAnimator.start();
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


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.play();
    }

}
