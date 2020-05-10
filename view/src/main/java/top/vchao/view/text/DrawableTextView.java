package top.vchao.view.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import top.vchao.view.R;

/**
 * @ description: 可以自由控制 drawable 大小的 TextView
 * @ author: vchao  blog: https://vchao.blog.csdn.net
 */
public class DrawableTextView extends AppCompatTextView {

    public final static int DRAWABLE_POSITION_LEFT = 0;
    public final static int DRAWABLE_POSITION_TOP = 1;
    public final static int DRAWABLE_POSITION_RIGHT = 2;
    public final static int DRAWABLE_POSITION_BOTTOM = 3;

    public final static int SIZE_WIDTH = 0;
    public final static int SIZE_HEIGHT = 1;

    private Drawable[] drawableList;
    private int[][] drawableSizeList = new int[4][2];

    public DrawableTextView(Context context) {
        this(context, null);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        if (drawableList == null) {
            drawableList = new Drawable[4];
        }
        drawableList[DRAWABLE_POSITION_LEFT] = left;
        drawableList[DRAWABLE_POSITION_TOP] = top;
        drawableList[DRAWABLE_POSITION_RIGHT] = right;
        drawableList[DRAWABLE_POSITION_BOTTOM] = bottom;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);

        int globalWidth = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableWidth, (int) getTextSize());
        int globalHeight = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableHeight, (int) getTextSize());

        drawableSizeList[DRAWABLE_POSITION_LEFT][SIZE_WIDTH] = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableLeftWidth, globalWidth);
        drawableSizeList[DRAWABLE_POSITION_LEFT][SIZE_HEIGHT] = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableLeftHeight, globalHeight);

        drawableSizeList[DRAWABLE_POSITION_RIGHT][SIZE_WIDTH] = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableRightWidth, globalWidth);
        drawableSizeList[DRAWABLE_POSITION_RIGHT][SIZE_HEIGHT] = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableRightHeight, globalHeight);

        drawableSizeList[DRAWABLE_POSITION_TOP][SIZE_WIDTH] = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableTopWidth, globalWidth);
        drawableSizeList[DRAWABLE_POSITION_TOP][SIZE_HEIGHT] = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableTopHeight, globalHeight);

        drawableSizeList[DRAWABLE_POSITION_BOTTOM][SIZE_WIDTH] = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableBottomWidth, globalWidth);
        drawableSizeList[DRAWABLE_POSITION_BOTTOM][SIZE_HEIGHT] = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableBottomHeight, globalHeight);

        ta.recycle();

        setDrawableSize();
    }

    public void setDrawableSize() {
        if (drawableList == null) {
            drawableList = new Drawable[4];
        }

        if (drawableList[DRAWABLE_POSITION_LEFT] != null) {
            drawableList[DRAWABLE_POSITION_LEFT].setBounds(0, 0, drawableSizeList[DRAWABLE_POSITION_LEFT][SIZE_WIDTH], drawableSizeList[DRAWABLE_POSITION_LEFT][SIZE_HEIGHT]);
        }
        if (drawableList[DRAWABLE_POSITION_TOP] != null) {
            drawableList[DRAWABLE_POSITION_TOP].setBounds(0, 0, drawableSizeList[DRAWABLE_POSITION_TOP][SIZE_WIDTH], drawableSizeList[DRAWABLE_POSITION_TOP][SIZE_HEIGHT]);
        }
        if (drawableList[DRAWABLE_POSITION_RIGHT] != null) {
            drawableList[DRAWABLE_POSITION_RIGHT].setBounds(0, 0, drawableSizeList[DRAWABLE_POSITION_LEFT][SIZE_WIDTH], drawableSizeList[DRAWABLE_POSITION_RIGHT][SIZE_HEIGHT]);
        }
        if (drawableList[DRAWABLE_POSITION_BOTTOM] != null) {
            drawableList[DRAWABLE_POSITION_BOTTOM].setBounds(0, 0, drawableSizeList[DRAWABLE_POSITION_BOTTOM][SIZE_WIDTH], drawableSizeList[DRAWABLE_POSITION_BOTTOM][SIZE_HEIGHT]);
        }
        setCompoundDrawables(drawableList[DRAWABLE_POSITION_LEFT], drawableList[DRAWABLE_POSITION_TOP],
                drawableList[DRAWABLE_POSITION_RIGHT], drawableList[DRAWABLE_POSITION_BOTTOM]);
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef({DRAWABLE_POSITION_LEFT, DRAWABLE_POSITION_TOP, DRAWABLE_POSITION_RIGHT, DRAWABLE_POSITION_BOTTOM})
    @interface Position {
    }

    /*
     * 代码中动态设置 drawable 的宽高
     * */
    public void setDrawableSize(@Position int position, int width, int height) {
        // 非法参数
        if (width < 0 || height < 0) {
            return;
        }
        // 如果数值未发生变化，直接返回
        if (drawableSizeList[position][SIZE_WIDTH] == width && drawableSizeList[position][SIZE_HEIGHT] == height) {
            return;
        }
        drawableSizeList[position][SIZE_WIDTH] = width;
        drawableSizeList[position][SIZE_HEIGHT] = height;

        setDrawableSize();
    }

}
