package com.camnter.easyarcloadingdemo;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

/**
 * Description：EasyArcLoadingDialog
 * Created by：CaMnter
 * Time：2016-03-07 17:59
 */
public class EasyArcLoadingDialog extends Dialog {

    private static final double DIALOG_DEVICE_LENGTH_RATIO = 0.87826086956522d;


    public EasyArcLoadingDialog(Context context) {
        super(context, R.style.EasyArcLoadingDialog);
        this.init(context);
    }


    public EasyArcLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.init(context);
    }


    protected EasyArcLoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.init(context);
    }


    private void init(Context context) {
        this.setContentView(R.layout.dialog_easy_arc_loading);
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels *
                DIALOG_DEVICE_LENGTH_RATIO);
    }
}
