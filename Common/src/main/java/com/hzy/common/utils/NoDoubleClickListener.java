package com.hzy.common.utils;

import android.view.View;
import java.util.Calendar;

/**
 * 类名: NoDoubleClickListener</br>
 * 包名：com.hzy.common.utils </br>
 * 描述: </br>
 * 发布版本号：</br>
 * 开发人员： huangzy</br>
 * 创建时间： 2018/6/28 上午10:01 </br>
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    protected abstract void onNoDoubleClick(View v);

}