package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.google.android.things.device.TimeManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TabSetup extends Fragment {

    private TextView txtTempIn, txtTempOut, txtAdLight, txtAdLight1, txtDidingNum;
    private TextView txtXiaoJieStart, txtXiaoJieLave, txtSysTime;
    private long lave = 0; //剩余消解时间
    private Button systemSetup;
    private TimeManager timeManager = TimeManager.getInstance();
    private TextView wifiIp;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm:ss");

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
        txtXiaoJieStart.setText(timeFormat.format(SysData.startXiaojie));
        //txtXiaoJieStart.setText(Long.toString(SysData.startXiaojie));
        lave = (SysData.endXiaoJie > System.currentTimeMillis()) ? (SysData.endXiaoJie - System.currentTimeMillis()) / 1000 : 0;
        txtXiaoJieLave.setText(Long.toString(lave) + "秒");
        txtSysTime.setText(dateFormat.format(System.currentTimeMillis()));
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
        txtXiaoJieStart = view.findViewById(R.id.xiaojiestart);
        txtXiaoJieLave = view.findViewById(R.id.xiaojielave);
        systemSetup = view.findViewById(R.id.systemsetup);
        wifiIp = view.findViewById(R.id.wifiip);
        txtSysTime = view.findViewById(R.id.sysTime);

        //显示无线网络ip地址
        wifiIp.setText("WEB访问：" + SysData.wifiIpAdd);
        //刷新界面信息
        message = handlerUpdate.obtainMessage(UI_UPDATE);
        handlerUpdate.sendMessageDelayed(message, 1000);

        //点击系统设置按钮
        systemSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        //生成网址的二维码
        ImageView mImageView = (ImageView) view.findViewById(R.id.imageViewZXing);
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(SysData.wifiIpAdd, 120, 120);
        mImageView.setImageBitmap(mBitmap);

        return view;
    }

    //设置日期对话框
    private void showDateDialog(){

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialogDate = new DatePickerDialog(getView().getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date newDate;
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        newDate = calendar.getTime();
                        timeManager.setTime(newDate.getTime());
                        showTimeDialog();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialogDate.show();
    }


    //设置时间对话框
    private void showTimeDialog(){

        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialogTime = new TimePickerDialog(getView().getContext(),
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Date newDate;
                        calendar.set(Calendar.HOUR, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        newDate = calendar.getTime();
                        timeManager.setTime(newDate.getTime());
                    }
                },
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                true);
        dialogTime.show();
    }

}
