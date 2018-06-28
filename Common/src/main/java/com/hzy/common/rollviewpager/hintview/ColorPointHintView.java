package com.hzy.common.rollviewpager.hintview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import com.hzy.common.rollviewpager.Util;


/**
 * Created by Mr.Jude on 2016/1/10.
 */
public class ColorPointHintView extends ShapeHintView {
    private int focusColor;
    private int normalColor;

    private int pointSize = 6;

    private int space = 10;


    public void setSpace(int space) {
        this.space = space;
    }

    public ColorPointHintView(Context context,int focusColor,int normalColor) {
        super(context);
        this.focusColor = focusColor;
        this.normalColor = normalColor;
    }

    public ColorPointHintView(Context context,int focusColor,int normalColor,int pointSize) {
        super(context);
        this.focusColor = focusColor;
        this.normalColor = normalColor;
        this.pointSize = pointSize;
    }


    @Override
    public int makeSpace() {
        return space;
    }

    @Override
    public Drawable makeFocusDrawable() {
        GradientDrawable dot_focus = new GradientDrawable();
        dot_focus.setColor(focusColor);
        dot_focus.setCornerRadius(Util.dip2px(getContext(), pointSize/2));
        dot_focus.setSize(Util.dip2px(getContext(), pointSize), Util.dip2px(getContext(), pointSize));
        return dot_focus;
    }

    @Override
    public Drawable makeNormalDrawable() {
        GradientDrawable dot_normal = new GradientDrawable();
        dot_normal.setColor(normalColor);
        dot_normal.setCornerRadius(Util.dip2px(getContext(), pointSize/2));
        dot_normal.setSize(Util.dip2px(getContext(), pointSize), Util.dip2px(getContext(), pointSize));
        return dot_normal;
    }
}
