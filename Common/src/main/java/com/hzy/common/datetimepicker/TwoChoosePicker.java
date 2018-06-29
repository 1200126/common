package com.hzy.common.datetimepicker;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hzy.common.R;

import java.util.ArrayList;

public class TwoChoosePicker {
    /**
     * 定义结果回调接口
     */
    public interface ResultHandler {
        void handle(String start, String end);
    }

    private ResultHandler handler;
    private Context context;
    private boolean canAccess = false;

    private Dialog datePickerDialog;
    private DatePickerView year_pv, month_pv;

    private ArrayList<String> year, month;
    private int startYear, startMonth, endYear, endMonth;
    private TextView tv_cancle, tv_select;

    private String chooseYear, chooseMonth;

    public TwoChoosePicker(Context context, ResultHandler resultHandler) {
        canAccess = true;
        this.context = context;
        this.handler = resultHandler;
        initDialog();
        initView();
    }

    private void initDialog() {
        if (datePickerDialog == null) {
            datePickerDialog = new Dialog(context, R.style.time_dialog);
            datePickerDialog.setCancelable(false);
            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            datePickerDialog.setContentView(R.layout.custom_twochoose_picker);
            Window window = datePickerDialog.getWindow();
            window.setWindowAnimations(R.style.DialogAnimation);
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);
        }
    }

    private void initView() {

        year_pv = (DatePickerView) datePickerDialog.findViewById(R.id.year_pv);
        month_pv = (DatePickerView) datePickerDialog.findViewById(R.id.month_pv);
        tv_cancle = (TextView) datePickerDialog.findViewById(R.id.tv_cancle);
        tv_select = (TextView) datePickerDialog.findViewById(R.id.tv_select);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.dismiss();
            }
        });

        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.handle(chooseYear.replace("岁",""), chooseMonth.replace("岁",""));
                datePickerDialog.dismiss();
            }
        });
    }

    private void initParameter() {
        startYear = 10;
        startMonth = 10;
        endYear = 99;
        endMonth = 99;
        chooseYear = startYear +"岁";
        chooseMonth = startMonth +"岁";
    }

    private void initTimer() {
        initArrayList();
        for (int i = startYear; i <= endYear; i++) {
            year.add(String.valueOf(i)+"岁");
        }
        for (int i = startMonth; i <= endMonth; i++) {
            month.add(String.valueOf(i)+"岁");
        }
        loadComponent();
    }

    /**
     * 将“0-9”转换为“00-09”
     */
    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void initArrayList() {
        if (year == null) year = new ArrayList<>();
        if (month == null) month = new ArrayList<>();
        year.clear();
        month.clear();
    }

    private void loadComponent() {
        year_pv.setData(year);
        month_pv.setData(month);
        year_pv.setSelected(0);
        month_pv.setSelected(0);
        executeScroll();
    }

    private void addListener() {
        year_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                chooseYear = text;
                monthChange();
            }
        });

        month_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                chooseMonth = text;
            }
        });
    }

    private void monthChange() {
        month.clear();

        int year = Integer.parseInt(chooseYear.replace("岁",""));
        int selected = 0;
        for (int i = year; i <= endMonth; i++) {
            month.add(String.valueOf(i)+"岁");
        }
        if(Integer.parseInt(chooseMonth.replace("岁","")) > year){
            for (int i = 0; i < month.size(); i++) {
                if(chooseMonth.equals(month.get(i))){
                    selected = i;
                    break;
                }
            }
        }
        chooseMonth = month.get(selected);
        month_pv.setData(month);
        month_pv.setSelected(selected);
        executeAnimator(month_pv);

    }


    private void executeAnimator(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(200).start();
    }

    private void executeScroll() {
        year_pv.setCanScroll(year.size() > 1);
        month_pv.setCanScroll(month.size() > 1);
    }

    public void show() {
        canAccess = true;
        initParameter();
        initTimer();
        addListener();
        datePickerDialog.show();
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    public void setIsLoop(boolean isLoop) {
        if (canAccess) {
            this.year_pv.setIsLoop(isLoop);
            this.month_pv.setIsLoop(isLoop);
        }
    }

}
