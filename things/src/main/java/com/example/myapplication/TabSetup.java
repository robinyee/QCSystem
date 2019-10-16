package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.IOException;

public class TabSetup extends Fragment {

    private TextView txtTempIn, txtTempOut, txtAdLight, txtAdLight1, txtDidingNum;
    //获取线程发送的Msg信息，更新对于UI界面
    static final int UI_UPDATE = 100;
    private Message message;
    @SuppressLint("HandlerLeak")
    private Handler handlerUpdate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UI_UPDATE) {
                uiUpdate();
                //Log.d(TAG, "run: 更新界面");
            }
            message = handlerUpdate.obtainMessage(UI_UPDATE);
            handlerUpdate.sendMessageDelayed(message, 1000);
        }
    };

    //刷新界面输出状态
    public void uiUpdate() {
        //显示状态数据
        txtTempIn.setText(Double.toString(SysData.tempIn) + "℃");
        txtTempOut.setText(Double.toString(SysData.tempOut) + "℃");
        txtAdLight.setText(Integer.toString(SysData.adLight));
        txtAdLight1.setText(Integer.toString(SysData.startAdLight));
        txtDidingNum.setText(Integer.toString(SysData.didingNum)); //4为注射泵开始滴定到有高锰酸钾出来的数量
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_setup, container, false);
        //状态数据
        txtTempIn = view.findViewById(R.id.tempin);
        txtTempOut = view.findViewById(R.id.tempout);
        txtAdLight = view.findViewById(R.id.adlight);
        txtAdLight1 = view.findViewById(R.id.adlight1);
        txtDidingNum = view.findViewById(R.id.didingnum);
        //刷新界面信息
        message = handlerUpdate.obtainMessage(UI_UPDATE);
        handlerUpdate.sendMessageDelayed(message, 1000);

        return view;
    }
}
