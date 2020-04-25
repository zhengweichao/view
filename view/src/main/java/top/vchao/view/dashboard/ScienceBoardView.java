package top.vchao.view.dashboard;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

import top.vchao.view.R;

/**
 * @ Create_time : 2020/1/2 on 12:48.
 * @ description : 科技感的仪表盘
 * @ author : vchao
 * @ blog：https://vchao.blog.csdn.net/
 */
public class ScienceBoardView extends View {

    //仪表盘刻度线 颜色
    private static final int DEFAULT_COLOR_DIAL_LINE = Color.parseColor("#228fbd");
    // 仪表盘刻度 字体大小
    private static final int DEFAULT_TEXT_SIZE_DIAL = 11;
    // 仪表盘刻度 字体颜色
    private static final int DEFAULT_COLOR_DIAL_TEXT = Color.WHITE;
    // 刻度盘圆弧宽度
    private static final int DEFAULT_BORDER_WIDTH = 2;
    // 仪表盘中心数值 字体大小
    private static final int DEFAULT_TEXT_SIZE_VALUE = 22;
    // 动画时长
    private static final int DEFAULT_ANIM_PLAY_TIME = 2000;
    // 仪表盘中心单位 字体大小
    private static final int DEFAULT_TEXT_SIZE_UNIT = 11;
    // 仪表盘中心数值 字体颜色
    private static final int DEFAULT_COLOR_VALUE_TEXT = Color.WHITE;
    // 仪表盘中心单位 字体颜色
    private static final int DEFAULT_COLOR_UNIT_TEXT = Color.parseColor("#38F9FD");
    // 仪表盘 单位
    private static final String DEFAULT_UNIT_NAME = "℃";
    DecimalFormat decimalFormat = new DecimalFormat("###.#");

    private static final int DEFAULT_RADIUS_DIAL = 128;
    private static final int DEFAULT_border = 5;

    private int mDialLineColor;
    private int mDialTextSize;
    private int mDialTextColor;
    private int mBorderWidth;
    private int mValueTextSize;
    private int mAnimDuration;
    private int mValueTextColor;
    private int mUnitTextSize;
    private int mUnitTextColor;
    private String mUnitName = DEFAULT_UNIT_NAME;

    private int mRadius;// 仪表盘半径
    private int mRealRadius;
    private float currentValue;

    private float openAngle = 120;// 底部开口的角度
    private int clockPointNum = 100;
    private int clockMinValue = 0;

    private Paint arcPaint;
    private RectF mRect;
    private Paint dialLinePaint;
    private Paint.FontMetrics fontMetrics;
    private Paint titlePaint;
    private Path pointerPath;
    private Paint blackCirPaint;
    private Paint blueCirPaint;
    private Paint pointShadowPaint;
    private Paint pointerPaint;
    private Paint dialTextPaint;
    private Paint unitPaint;
    private ValueAnimator animator;
    private AccelerateDecelerateInterpolator interpolator;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;

    public ScienceBoardView(Context context) {
        this(context, null);
    }

    public ScienceBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScienceBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
//        初始化属性
        initAttrs(context, attrs);
        initPaint();
        initAnim();
    }

    private void initAnim() {
        animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (currentValue == (float) (Math.round((float) animation.getAnimatedValue() * 10)) / 10) {
                    return;
                }
                currentValue = (float) (Math.round((float) animation.getAnimatedValue() * 10)) / 10;
                invalidate();
            }
        };
        interpolator = new AccelerateDecelerateInterpolator();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ScienceBoardView);
        mDialLineColor = attributes.getColor(R.styleable.ScienceBoardView_sbDialColor, DEFAULT_COLOR_DIAL_LINE);
        mDialTextSize = (int) attributes.getDimension(R.styleable.ScienceBoardView_sbDialTextSize, dp2px(DEFAULT_TEXT_SIZE_DIAL));
        mDialTextColor = attributes.getColor(R.styleable.ScienceBoardView_sbValueTextColor, DEFAULT_COLOR_DIAL_TEXT);

        mRadius = (int) attributes.getDimension(R.styleable.ScienceBoardView_sbRadius, dp2px(DEFAULT_RADIUS_DIAL));
        mBorderWidth = (int) attributes.getDimension(R.styleable.ScienceBoardView_sbBorderWidth, dp2px(DEFAULT_BORDER_WIDTH));
        mValueTextSize = (int) attributes.getDimension(R.styleable.ScienceBoardView_sbValueTextSize, dp2px(DEFAULT_TEXT_SIZE_VALUE));
        mUnitTextSize = (int) attributes.getDimension(R.styleable.ScienceBoardView_sbUnitTextSize, dp2px(DEFAULT_TEXT_SIZE_UNIT));
        mValueTextColor = attributes.getColor(R.styleable.ScienceBoardView_sbValueTextColor, DEFAULT_COLOR_VALUE_TEXT);
        mUnitTextColor = attributes.getColor(R.styleable.ScienceBoardView_sbValueTextColor, DEFAULT_COLOR_UNIT_TEXT);
        mAnimDuration = attributes.getInt(R.styleable.ScienceBoardView_sbAnimDuration, DEFAULT_ANIM_PLAY_TIME);
        if (attributes.hasValue(R.styleable.ScienceBoardView_sbUnitName))
            mUnitName = attributes.getString(R.styleable.ScienceBoardView_sbUnitName);
        attributes.recycle();
    }

    /**
     * 初始化 paint
     */
    private void initPaint() {
        // 绘制发光圆环边框 paint
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(Color.parseColor("#38F9FD"));
        arcPaint.setStrokeWidth(mBorderWidth);
        arcPaint.setShader(null);
        arcPaint.setAlpha(70);
        arcPaint.setShadowLayer(10, 0, 0, Color.WHITE);

        // 绘制刻度和数字 paint
        dialLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dialLinePaint.setTextSize(mDialTextSize);
        dialLinePaint.setColor(mDialLineColor);
        dialLinePaint.setTextAlign(Paint.Align.CENTER);
        fontMetrics = dialLinePaint.getFontMetrics();

        // 刻度数字 paint
        dialTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dialTextPaint.setColor(mDialTextColor);
        dialTextPaint.setTextSize(mDialTextSize);

        // 绘制中心数值 paint
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(mValueTextColor);
        titlePaint.setTextSize(mValueTextSize);

        // 绘制中心单位 paint
        unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setTextAlign(Paint.Align.CENTER);
        unitPaint.setFakeBoldText(true);
        unitPaint.setColor(mUnitTextColor);
        unitPaint.setTextSize(mUnitTextSize);

        pointerPath = new Path();

        // 绘制黑色背景圆
        blackCirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackCirPaint.setStyle(Paint.Style.FILL);
        blackCirPaint.setColor(Color.parseColor("#05002D"));

        // 绘制蓝色发光圆
        blueCirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blueCirPaint.setStyle(Paint.Style.FILL);
        blueCirPaint.setColor(Color.parseColor("#050D3D"));
        blueCirPaint.setShadowLayer(15, 0, 0, Color.parseColor("#006EC6"));

        // 指针阴影paint
        int[] colorSweep = {0xAAFFE9EC, 0x0028E9EC, 0xAA28E9EC};
        float[] position = {0f, 0.9f, 1f};
        SweepGradient mShader = new SweepGradient(0, 0, colorSweep, position);
        pointShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointShadowPaint.setShader(mShader);
        pointShadowPaint.setStyle(Paint.Style.STROKE);
        pointShadowPaint.setStrokeWidth((float) (mRadius * 0.4));
        pointShadowPaint.clearShadowLayer();

        // 表针 paint
        pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointerPaint.setColor(Color.WHITE);
    }

    public void setClockPointNum(int clockPointNum) {
        this.clockPointNum = clockPointNum;
        postInvalidate();
    }

    public void setUnitName(String mUnitName) {
        this.mUnitName = mUnitName;
    }

    public void setClockValueArea(int clockMinValue, int clockMaxValue, String dataUnit) {
        this.clockMinValue = clockMinValue;
        if (!TextUtils.isEmpty(dataUnit)) {
            this.mUnitName = dataUnit;
        }
        setClockPointNum(clockMaxValue - clockMinValue);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int mWidth, mHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = getPaddingLeft() + mRadius * 2 + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                mWidth = Math.min(mWidth, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = getPaddingTop() + mRadius * 2 + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = Math.min(mHeight, heightSize);
            }
        }

        setMeasuredDimension(mWidth, mHeight);

        mRadius = Math.min((getMeasuredWidth() - getPaddingLeft() - getPaddingRight()),
                (getMeasuredHeight() - getPaddingTop() - getPaddingBottom())) / 2;
        mRealRadius = mRadius - mBorderWidth / 2 - DEFAULT_border * 2;
        mRect = new RectF(-mRealRadius - DEFAULT_border, -mRealRadius - DEFAULT_border,
                mRealRadius + DEFAULT_border, mRealRadius + DEFAULT_border);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        step1 画圆弧
        drawArc(canvas);
//        step2 绘制刻度和数字
        drawPointerLine(canvas);
//        step3 画指针阴影
        drawPointShadow(canvas);
//        step4 绘制中间黑色圆形背景
        drawBlackCircle(canvas);
//        step5 绘制表针
        drawPointer(canvas);
//        step6 绘制深蓝色发光圆形
        drawBlueCircle(canvas);
//        step7 绘制表盘中的数字
        drawCircleText(canvas);
    }

    /**
     * step1 画圆弧
     */
    private void drawArc(Canvas canvas) {
        canvas.translate(getPaddingLeft() + mRadius, getPaddingTop() + mRadius);
        canvas.drawArc(mRect, 150, (360 - openAngle), false, arcPaint);
    }

    /**
     * step2 绘制刻度和数字
     */
    private void drawPointerLine(Canvas canvas) {
//        旋转画布 （坐标系）
        canvas.rotate(150);

        for (int i = 0; i < clockPointNum + 1; i++) {

            if (i % 10 == 0) {     //长表针
                dialLinePaint.setStrokeWidth(3);
                canvas.drawLine(mRadius - DEFAULT_border - mBorderWidth, 0, mRadius - mBorderWidth - dp2px(10), 0, dialLinePaint);
                drawPointerText(canvas, i);
            } else if (i % 5 == 0f) {    //短表针
                dialLinePaint.setStrokeWidth(2);
                canvas.drawLine(mRadius - DEFAULT_border - mBorderWidth, 0, mRadius - mBorderWidth - dp2px(6), 0, dialLinePaint);
            }
            canvas.rotate((360 - openAngle) / clockPointNum);
        }
        canvas.rotate(-((180 - openAngle) / 2 + ((360 - openAngle) / clockPointNum)));
    }

    /**
     * step3 画指针阴影
     */
    private void drawPointShadow(Canvas canvas) {
        int currentDegree = (int) ((currentValue - clockMinValue) * ((360 - openAngle) / clockPointNum) + 150);
        canvas.rotate(currentDegree);

        RectF mRect = new RectF((float) (-mRealRadius - DEFAULT_border + mRadius * 0.2), (float) (-mRealRadius - DEFAULT_border + mRadius * 0.2),
                (float) (mRealRadius + DEFAULT_border - mRadius * 0.2), (float) (mRealRadius + DEFAULT_border - mRadius * 0.2));
        canvas.drawArc(mRect, 360 - (currentDegree - 150), (currentDegree - 150), false, pointShadowPaint);
    }

    /**
     * step4 绘制中间黑色圆形背景
     */
    private void drawBlackCircle(Canvas canvas) {
        canvas.restore();
        canvas.translate(getPaddingLeft() + mRadius, getPaddingTop() + mRadius);
        canvas.drawCircle(0, 0, (float) (mRadius * 0.6), blackCirPaint);
    }

    /**
     * step5 绘制表针
     */
    private void drawPointer(Canvas canvas) {
        canvas.save();
        int currentDegree = (int) ((currentValue - clockMinValue) * ((360 - openAngle) / clockPointNum) + 150);

        canvas.rotate(currentDegree);

        pointerPath.moveTo(mRadius - mBorderWidth, 0);
        pointerPath.lineTo(0, -dp2px(5));
        pointerPath.lineTo(-12, 0);
        pointerPath.lineTo(0, dp2px(5));
        pointerPath.close();
        canvas.drawPath(pointerPath, pointerPaint);

        canvas.save();
        canvas.restore();
    }

    /**
     * step6 绘制深蓝色发光圆形
     */
    private void drawBlueCircle(Canvas canvas) {
        canvas.rotate(0);
        canvas.restore();
        canvas.drawCircle(0, 0, (float) (mRadius * 0.4), blueCirPaint);
    }

    /**
     * step7 绘制表盘中的数字
     */
    private void drawCircleText(Canvas canvas) {
        canvas.drawText(formatData(currentValue), 0, 0, titlePaint);
        canvas.drawText("(" + mUnitName + ")", 0, mUnitTextSize + dp2px(6), unitPaint);
    }

    /**
     * 绘制刻度数字
     */
    private void drawPointerText(Canvas canvas, int i) {
        canvas.save();
        int currentCenterX = (int) (mRadius - mBorderWidth - dp2px(21) - dialTextPaint.measureText(String.valueOf(i)) / 2);
        canvas.translate(currentCenterX, 0);

        canvas.rotate(360 - 150 - ((360 - openAngle) / clockPointNum) * i);        //坐标系总旋转角度为360度

        int textBaseLine = (int) (0 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
        canvas.drawText(String.valueOf(i + clockMinValue), 0, textBaseLine, dialTextPaint);
        canvas.restore();
    }

    public void setCompleteDegree(float degree) {
        if (animator != null) {
            animator.cancel();
            animator.removeAllUpdateListeners();
            animator = null;
            System.gc();
        }
        animator = ValueAnimator.ofFloat(currentValue, degree);
        animator.addUpdateListener(animatorUpdateListener);
        animator.setInterpolator(interpolator);
        animator.setDuration(mAnimDuration);
        animator.start();
    }

    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }

    protected String formatData(float num) {
        return decimalFormat.format(num);
    }

}

