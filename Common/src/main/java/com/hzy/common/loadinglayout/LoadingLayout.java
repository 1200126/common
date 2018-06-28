package com.hzy.common.loadinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzy.common.R;

/** 
 * 类名: LoadingLayout</br> 
 * 包名：com.hzy.common.loadinglayout </br> 
 * 描述: </br>
 * 发布版本号：</br>
 * 开发人员： huangzy</br>
 * 创建时间： 2017/10/26 上午10:18 </br>
 */ 
public class LoadingLayout extends FrameLayout {

    /**
     * 空数据View
     */
    private int mEmptyView;
    /**
     * 状态View
     */
    private int mStateView;
    /**
     * 加载View
     */
    private int mLoadingView;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingLayout, 0, 0);
        try {
            mStateView = a.getResourceId(R.styleable.LoadingLayout_stateView, R.layout.loadstate_layout);
            mLoadingView = a.getResourceId(R.styleable.LoadingLayout_loadingView, R.layout.loading_layout);
            mEmptyView = a.getResourceId(R.styleable.LoadingLayout_emptyView, R.layout.empty_layout);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(mStateView, this, true);
            inflater.inflate(mLoadingView, this, true);
            inflater.inflate(mEmptyView, this, true);
        } finally {
            a.recycle();
        }
    }

    /**
     * 布局加载完成后隐藏所有View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount() - 1; i++) {
            getChildAt(i).setVisibility(GONE);
        }
    }


    /**
     * 设置Empty点击事件
     * @param listener
     */
    public void setEmptyClickListener(final OnClickListener listener) {
        if( listener!=null )
            findViewById(R.id.state_retry2).setOnClickListener(listener);
    }

    /**
     * 设置State点击事件
     * @param listener
     */
    public void setStateClickListener( OnClickListener listener ){
        if(listener!=null)
            findViewById(R.id.state_retry).setOnClickListener(listener);
    }

    /**
     * 设置自定义布局的点击事件
     * @param resoureId
     * @param listener
     */
    public void setViewOncClickListener(int resoureId,OnClickListener listener) {
        findViewById(resoureId).setOnClickListener(listener);
    }

    /**
     * 设置自定义布局的view文本
     * @param resoureId
     * @param text
     */
    public void setViewText(int resoureId,String text){
        ((TextView)findViewById(resoureId)).setText(text);
    }

    /**
     * 设置自定义布局的image
     * @param resoureId
     * @param img
     */
    public void setViewImage(int resoureId,int img ){
        ((ImageView)findViewById(resoureId)).setImageResource(img);
    }

    /**
     * State View
     */
    public void showState() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 0) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    /**
     * Empty view
     */
    public void showEmpty() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 2) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }


    /**
     * Loading view
     */
    public void showLoading() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 1) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }


    /**
     *
     * @param text
     */
    public void showLoading(String text) {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 1) {
                child.setVisibility(VISIBLE);
                ((TextView) child.findViewById(R.id.loading_text)).setText(text + "");
            } else {
                child.setVisibility(GONE);
            }
        }
    }


    /**
     * Empty view
     *
     * @param text
     */
    public void showEmpty(String text) {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 2) {
                child.setVisibility(VISIBLE);
                ((TextView) child.findViewById(R.id.empty_text)).setText(text + "");
            } else {
                child.setVisibility(GONE);
            }
        }
    }


    /**
     *
     * @param tips
     */
    public void showState(String tips) {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 0) {
                child.setVisibility(VISIBLE);
                ((TextView) child.findViewById(R.id.load_state_tv)).setText(tips + "");
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    /**
     * @param stateId
     * @param tips
     */
    public void showState(int stateId, String tips) {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 0) {
                child.setVisibility(VISIBLE);
                ((ImageView) child.findViewById(R.id.load_state_img)).setImageResource(stateId);
                ((TextView) child.findViewById(R.id.load_state_tv)).setText(tips + "");
            } else {
                child.setVisibility(GONE);
            }
        }
    }



    /**
     * 展示内容
     */
    public void showContent() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i > 2 ) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }
}