package com.androidso.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;
import com.androidso.app.R;

public class RoundProgressBar extends ProgressBar {

    public boolean play = false;

    /**
     * mRadius of view
     */
    private int mRadius = dp2px(30);
    private int mMaxPaintWidth;
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;
    private static final int DEFAULT_PROGRESS_BAR_ALPHA = 255;

    /**
     * painter of all drawing things
     */
    protected Paint mPaint = new Paint();
    /**
     * color of progress number
     */
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    /**
     * size of text (sp)
     */
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

    /**
     * offset of draw progress
     */
    protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

    /**
     * height of reached progress bar
     */
    protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

    /**
     * color of reached bar
     */
    protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
    /**
     * color of unreached bar
     */
    protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
    /**
     * height of unreached progress bar
     */
    protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    /**
     * view width except padding
     */
    protected int mRealWidth;
    /**
     * default color alpha
     */
    protected int mAlpha = DEFAULT_PROGRESS_BAR_ALPHA;

    protected int mInnerColor;

    protected boolean mIfDrawText = true;

    protected static final int VISIBLE = 0;

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
        obtainStyledAttributes(attrs);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);


        //   mReachedProgressBarHeight = (int) (mUnReachedProgressBarHeight * 2.5f);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }


    /**
     * get the styled attributes
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        // init values from custom attributes
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.RoundProgressBar);

        mTextColor = attributes
                .getColor(
                        R.styleable.RoundProgressBar_progress_text_color,
                        DEFAULT_TEXT_COLOR);
        mTextSize = (int) attributes.getDimension(
                R.styleable.RoundProgressBar_progress_text_size,
                mTextSize);

        mReachedBarColor = attributes
                .getColor(
                        R.styleable.RoundProgressBar_progress_reached_color,
                        mTextColor);
        mUnReachedBarColor = attributes
                .getColor(
                        R.styleable.RoundProgressBar_progress_unreached_color,
                        DEFAULT_COLOR_UNREACHED_COLOR);
        mReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable.RoundProgressBar_progress_reached_bar_height,
                        mReachedProgressBarHeight);
        mUnReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable.RoundProgressBar_progress_unreached_bar_height,
                        mUnReachedProgressBarHeight);
        mTextOffset = (int) attributes
                .getDimension(
                        R.styleable.RoundProgressBar_progress_text_offset,
                        mTextOffset);
        mAlpha = attributes
                .getInteger(
                        R.styleable.RoundProgressBar_progress_bar_alpha,
                        mAlpha);

        int textVisible = attributes
                .getInt(R.styleable.RoundProgressBar_progress_text_visibility,
                        VISIBLE);
        if (textVisible != VISIBLE) {
            mIfDrawText = false;
        }
        mInnerColor = attributes.getColor(R.styleable.RoundProgressBar_progress_inner_color, DEFAULT_COLOR_UNREACHED_COLOR);

        attributes.recycle();
    }


    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMaxPaintWidth = Math.max(mReachedProgressBarHeight,
                mUnReachedProgressBarHeight);
        int expect = mRadius * 2 + mMaxPaintWidth + getPaddingLeft()
                + getPaddingRight();
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);
        int realWidth = Math.min(width, height);

        mRadius = (realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;

        setMeasuredDimension(realWidth, realWidth);
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //  super.onDraw(canvas);
        String text = getProgress() + "%";
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

        canvas.save();
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop()
                + mMaxPaintWidth / 2);
        mPaint.setStyle(Paint.Style.STROKE);
        // draw unreaded bar
        mPaint.setColor(mUnReachedBarColor);
        mPaint.setAlpha(mAlpha);
        mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        // draw reached bar
        mPaint.setColor(mReachedBarColor);
        mPaint.setStrokeWidth(mReachedProgressBarHeight);
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), 0,
                sweepAngle, false, mPaint);
        if (mIfDrawText) {
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight,
                    mPaint);
        }
        canvas.restore();
    }


}