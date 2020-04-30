package top.vchao.view.image;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import top.vchao.view.R;

import static top.vchao.view.util.DpUtils.dp2px;

/**
 * @ Create_time: 2020/4/30 on 16:21.
 * @ description: 圆形/圆角矩形ImageView
 * @ author: vchao  blog: https://vchao.blog.csdn.net/
 */
public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView {

    // 默认圆形图片
    private static final int TYPE_CIRCLE = 0;
    // 圆角图片
    private static final int TYPE_ROUND = 1;
    // 默认圆角大小
    private static final int DEFAULT_ROUND_RADIUS = 8;
    // 默认边框大小
    private static final int DEFAULT_BORDER_WIDTH = 0;
    // 默认边框颜色
    private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
    // 默认按下状态蒙层颜色
    private static final int DEFAULT_COVER_COLOR = Color.parseColor("#40333333");
    // 默认可以按下
    private static final boolean DEFAULT_IS_PRESSED = true;

    // 类型
    private int mImageType;
    // 圆角大小
    private int mRoundRadius;
    // 边框大小
    private int mBorderWidth;
    // 边框颜色
    private int mBorderColor;
    // 按下状态蒙层颜色
    private int mCoverColor;
    // 当前是否被按下
    private boolean mIsPressed;

    // 缩放矩阵
    private Matrix mShaderMatrix;
    // 渲染器
    private BitmapShader mBitmapShader;
    // Rect Bitmap
    private RectF mRcBitmap;
    // Rect Border
    private RectF mRcBorder;

    // Bitmap Paint
    private Paint mBitmapPaint;
    // 边框 Paint
    private Paint mBorderPaint;

    // 圆心坐标
    private int mCenterCoordinates;
    // 边框半径
    private float mBorderRadius;
    // 图片半径
    private float mBitmapRadius;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyle, 0);
        mImageType = typedArray.getInt(R.styleable.RoundImageView_imageType, TYPE_CIRCLE);
        mRoundRadius = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_roundRadius, dp2px(DEFAULT_ROUND_RADIUS));
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_borderWidth, dp2px(DEFAULT_BORDER_WIDTH));
        mBorderColor = typedArray.getColor(R.styleable.RoundImageView_borderColor, DEFAULT_BORDER_COLOR);
        mCoverColor = typedArray.getColor(R.styleable.RoundImageView_coverColor, DEFAULT_COVER_COLOR);
        mIsPressed = typedArray.getBoolean(R.styleable.RoundImageView_isPressed, DEFAULT_IS_PRESSED);

        typedArray.recycle();
        initAttributes();
    }

    private void initAttributes() {
        // 设置缩放
        setScaleType(ScaleType.CENTER_CROP);
        // 初始化画笔等属性
        mShaderMatrix = new Matrix();
        mRcBitmap = new RectF();
        mRcBorder = new RectF();

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setDither(true);
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 如果是圆形，则强制设置宽高一致，以最小的值为准
        if (mImageType == TYPE_CIRCLE) {
            int mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mCenterCoordinates = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (!mIsPressed) {
            return;
        }
        if (pressed) {
            mBitmapPaint.setColorFilter(new PorterDuffColorFilter(mCoverColor, PorterDuff.Mode.SRC_ATOP));
        } else {
            mBitmapPaint.setColorFilter(null);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRcBitmap.contains(event.getX(), event.getY())) {
                    setPressed(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setShader();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setShader();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        setShader();
        if (mImageType == TYPE_ROUND) {
            canvas.drawRoundRect(mRcBorder, mRoundRadius, mRoundRadius, mBorderPaint);
            canvas.drawRoundRect(mRcBitmap, mRoundRadius, mRoundRadius, mBitmapPaint);
        } else {
            canvas.drawCircle(mCenterCoordinates, mCenterCoordinates, mBorderRadius, mBorderPaint);
            canvas.drawCircle(mCenterCoordinates, mCenterCoordinates, mBitmapRadius, mBitmapPaint);
        }
    }

    private void setShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        Bitmap bitmap = getBitmapFromDrawable(drawable);

        mBitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        mBitmapPaint.setShader(mBitmapShader);

        mRcBorder.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mRcBorder.height() - mBorderWidth) / 2, (mRcBorder.width() - mBorderWidth) / 2);

        if (mImageType == TYPE_CIRCLE) {
            mRcBitmap.set(mBorderWidth, mBorderWidth, mRcBorder.width() - mBorderWidth, mRcBorder.height() - mBorderWidth);
        } else if (mImageType == TYPE_ROUND) {
            mRcBitmap.set(mBorderWidth / 2f, mBorderWidth / 2f, mRcBorder.width() - mBorderWidth / 2f, mRcBorder.height() - mBorderWidth / 2f);
        }

        mBitmapRadius = Math.min(mRcBitmap.height() / 2, mRcBitmap.width() / 2);

        updateShaderMatrix(bitmap);
        invalidate();
    }

    /**
     * 矩阵变换
     */
    private void updateShaderMatrix(Bitmap mBitmap) {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmap.getWidth() * mRcBitmap.height() > mRcBitmap.width() * mBitmap.getHeight()) {
            scale = mRcBitmap.height() / (float) mBitmap.getHeight();
            dx = (mRcBitmap.width() - mBitmap.getWidth() * scale) * 0.5f;
        } else {
            scale = mRcBitmap.width() / (float) mBitmap.getWidth();
            dy = (mRcBitmap.height() - mBitmap.getHeight() * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    /**
     * 将Drawable转化为Bitmap
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public int getImageType() {
        return mImageType;
    }

    public void setImageType(int imageType) {
        if (this.mImageType != imageType) {
            this.mImageType = imageType;
            requestLayout();
        }
    }

    public int getRoundRadius() {
        return mRoundRadius;
    }

    public void setRoundRadius(int roundRadius) {
        if (this.mRoundRadius != dp2px(roundRadius)) {
            this.mRoundRadius = dp2px(roundRadius);
            invalidate();
        }
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (this.mBorderWidth != dp2px(borderWidth)) {
            this.mBorderWidth = dp2px(borderWidth);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            invalidate();
        }
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (this.mBorderColor != borderColor) {
            this.mBorderColor = borderColor;
            mBorderPaint.setColor(mBorderColor);
            invalidate();
        }
    }

    public int getCoverColor() {
        return mCoverColor;
    }

    public void setCoverColor(int coverColor) {
        if (this.mCoverColor != coverColor) {
            this.mCoverColor = coverColor;
            invalidate();
        }
    }

    public boolean isIsPressed() {
        return mIsPressed;
    }

    public void setIsPressed(boolean isPressed) {
        if (this.mIsPressed != isPressed) {
            this.mIsPressed = isPressed;
            invalidate();
        }
    }

}