package com.hzy.common;

import android.app.Application;
import android.os.Handler;


/**
 * 详述: </br>
 * 开发人员：huangzy</br>
 * 创建时间：2017/10/23 下午1:49 </br>
 * @param
 */
public class CommonAppLication extends Application{
    /**
     * 单例对象
     */
    private static CommonAppLication mInstance = null;
    /**
     * handler
     */
    private Handler mUiHandler = null;



    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


    /**
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：17/4/25 下午9:04 </br>
     *
     * @param
     */
    public static CommonAppLication getInstance() {
        return mInstance;
    }


    /**
     * 方法名: </br>
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：2014-4-30</br>
     *
     * @return
     */
    public Handler getUiHandler() {
        return (mUiHandler != null) ? (mUiHandler) : (mUiHandler = new Handler(getMainLooper()));
    }

    /**
     * 方法名: </br>
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：2014-4-30</br>
     *
     * @param r
     * @param delayMillis
     * @return
     */
    public boolean post2UIDelayed(Runnable r, long delayMillis) {
        return (mInstance != null) ? mInstance.getUiHandler().postDelayed(r, delayMillis) : false;
    }


}
