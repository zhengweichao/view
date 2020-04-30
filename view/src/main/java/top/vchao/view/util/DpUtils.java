package top.vchao.view.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * @ Create_time: 2020/4/30 on 23:48.
 * @ description: dp sp 转换工具类
 * @ author: vchao  blog: https://vchao.blog.csdn.net/
 */
public class DpUtils {
    public static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    public static int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }
}
