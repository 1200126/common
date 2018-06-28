package com.hzy.common.popMenus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzy.common.R;
import com.hzy.common.adapter.AdapterForAbsListView;
import com.hzy.common.adapter.OnItemClickListener;
import com.hzy.common.adapter.ViewHolder;
import com.hzy.common.adapter.ViewHolderForAbsListView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.textSize;

/**
 * Created by huangzy on 2017/10/23.
 */

public class PopMenuDialog {

    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int height = ViewGroup.LayoutParams.WRAP_CONTENT;

    /**
     * 回调函数
     */
    private CallBack callBack;
    /**
     * 底部设置menu
     */
    private PopupWindow settingPop;//
    /**
     * 上下文
     */
    private Context context;
    /**
     * 菜单
     */
    private List<String> groups;
    /** */
    private View view;

    /**  */
    private float textSize = 0;


    public PopMenuDialog(Context context,CallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    /**
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：2017/10/23 下午1:44 </br>
     * @param
     */
    public void setMenuList(List<String> items){
        groups.clear();
        groups.addAll(items);
    }

    /**
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：2017/10/23 下午1:45 </br>
     * @param
     */
    public void setTextSize(float textSize){
        this.textSize = textSize;
    }



    /**
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：17/3/7 下午4:34 </br>
     * @param
     */
    public void setLayoutParams(int width,int height){
        this.width = width;
        this.height = height;
        initMenuPopWindowsList();
    }


    /**
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：17/3/7 下午3:13 </br>
     *
     * @param
     */
    private void initMenuPopWindowsList() {
        view = ((Activity) context).getLayoutInflater().inflate(R.layout.popmenu_group_list, null);
        view.setFocusableInTouchMode(true);
        ListView lv_group = (ListView) view.findViewById(R.id.lvGroup);

        AdapterForAbsListView<String> adapter = new AdapterForAbsListView<String>(context,R.layout.popmenu_list_item_view) {
            @Override
            public void convert(ViewHolderForAbsListView helper, String item, int position) {
                TextView menuItem = helper.getView(R.id.groupItem);
                menuItem.setTextColor(Color.BLACK);
                menuItem.setText(item);
                if(textSize != 0){
                    menuItem.setTextSize(textSize);
                }
            }
        };

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewHolder helper, ViewGroup parent, View itemView, int position) {
                if (callBack != null) {
                    callBack.onClick(position);
                }
            }
        });
        lv_group.setAdapter(adapter);
        adapter.setData(groups);
        settingPop = new PopupWindow(view, width, height, true);
        // 设置背景
        settingPop.setBackgroundDrawable(new BitmapDrawable());
        // 设置获得焦点
        settingPop.setFocusable(true);
        settingPop.setTouchable(true);// 设置支持触屏事件
        settingPop.setOutsideTouchable(true);// 允许outside的触屏事件
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU) {
                    closeWindows();
                    return true;
                }

                return false;
            }
        });
    }

    /**
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：17/1/20 下午5:18 </br>
     *
     * @param
     */
    public String getMenusString(int index) {
        try {
            return groups.get(index);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 方法名: isShowing</br>
     * 详述: 是否显示中</br>
     * 开发人员：huangzy</br>
     * 创建时间：2014-7-7</br>
     *
     * @return
     */
    public boolean isShowing() {
        return settingPop.isShowing();
    }


    /**
     * 方法名: dismiss</br>
     * 详述: 关闭</br>
     * 开发人员：huangzy</br>
     * 创建时间：2014-7-7</br>
     */
    public void dismiss() {
        settingPop.dismiss();
    }

    /**
     * 方法名: getWidth</br>
     * 详述: </br>
     * 开发人员：huangzy</br>
     * 创建时间：2015年6月26日</br>
     *
     * @return
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * 方法名: closeWindows</br> 详述: 关闭对话框</br> 开发人员：huangzy</br> 创建时间：2014-6-16</br>
     */
    public void closeWindows() {
        if (null != settingPop) {
            if (settingPop != null && settingPop.isShowing()) {
                settingPop.dismiss();
            }
        }


    }


    /**
     * 方法名: showAsDropDown</br>
     * 详述: 设置显示位置</br>
     * 开发人员：huangzy</br>
     * 创建时间：2014-7-7</br>
     *
     * @param anchor
     */
    public void showAsDropDown(View anchor) {
        settingPop.showAsDropDown(anchor);
    }

    /**
     * 方法名: showAsDropDown</br>
     * 详述: 设置显示位置</br>
     * 开发人员：huangzy</br>
     * 创建时间：2014-7-7</br>
     *
     * @param anchor
     */
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        settingPop.showAsDropDown(anchor, xoff, yoff);
    }


    /**
     * 方法名: showAsDropDown</br>
     * 详述: 设置显示位置</br>
     * 开发人员：huangzy</br>
     * 创建时间：2014-7-7</br>
     *
     * @param anchor
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        settingPop.showAtLocation(parent, gravity, x, y);
    }

    /**
     * 类名: CallBack</br> 包名：com.cndatacom.cloudcontact.view.viewflow </br> 描述:
     * 回调</br> 发布版本号：</br> 开发人员： huangzy</br> 创建时间： 2014-6-16
     */
    public interface CallBack {
        public void onClick(int menu);
    }
}
