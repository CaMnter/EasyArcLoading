package com.camnter.easyarcloadingdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Description：EasyArcLoading
 * Created by：CaMnter
 * Time：2016-03-05 15:05
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EasyArcLoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findViewById(R.id.view_bt).setOnClickListener(this);
        this.findViewById(R.id.dialog_bt).setOnClickListener(this);
        this.dialog = new EasyArcLoadingDialog(this);
        this.dialog.setCanceledOnTouchOutside(true);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_bt:
                ViewActivity.startActivity(this);
                break;
            case R.id.dialog_bt:
                this.dialog.show();
                break;
        }
    }

}
