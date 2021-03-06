package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.google.android.things.device.TimeManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TabHome extends Fragment {

    Calendar calendar;
    private TextView textViewTime, textViewCODValue, textSpecimenType, textViewStatus;
    private ProgressBar progressBar;
    private SimpleDateFormat formater = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    private Date curDate;
    private ImageButton buttonStartup;
    private ImageView imageWarning;

    //更新首页系统时间显示
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    // 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
                    handler.removeMessages(0);
                    // 更新页面信息
                    updateUi();
                    // 再次发出msg，循环更新
                    handler.sendEmptyMessageDelayed(0, 1000);
                    break;

                case 1:
                    // 直接移除，定时器停止
                    handler.removeMessages(0);
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_home, container, false);

        //定时更新系统时间显示
        textViewTime = (TextView) view.findViewById(R.id.textTime);
        imageWarning = (ImageView) view.findViewById(R.id.imageWarning);
        textViewCODValue = (TextView) view.findViewById(R.id.textCodValue);
        textSpecimenType = (TextView) view.findViewById(R.id.text_Specimen_Type);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        textViewStatus = (TextView) view.findViewById(R.id.textStatus);

        //生成网址的二维码
        ImageView mImageView = (ImageView) view.findViewById(R.id.imageViewQRCode);
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(SysData.httpAddr, 70, 70);
        mImageView.setImageBitmap(mBitmap);

        //显示界面数据
        updateUi();

        //发送消息刷新页面
        handler.sendEmptyMessageDelayed(0, 0);

        //点击报警图标显示对话框
        imageWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SysData.errorMsg.equals("")) {
                    showWarningDialog();
                } else {
                    //showStopDialog();
                }
            }
        });

        //点击时间设置按钮
        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        return view;
    }

    private void updateUi() {
        curDate = new Date(System.currentTimeMillis());
        textViewTime.setText(formater.format(curDate)); //显示当前时间
        textViewCODValue.setText("");
        textSpecimenType.setText(SysData.strWaterType + SysData.strSampleType);
        progressBar.setProgress(SysData.progressRate);
        textViewStatus.setText(SysData.statusMsg);
        if(!SysData.errorMsg.equals("")) {
            imageWarning.setVisibility(View.VISIBLE);
            Log.i("报警", "报警信息：" + SysData.errorMsg);
        }
    }


    //按下启动测定时显示对话框
    private void showStartDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        altDialog.setIcon(R.drawable.ic_error_black_24dp);
        altDialog.setTitle("提示");
        altDialog.setMessage("要启动测定程序吗？");
        altDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        altDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        altDialog.show();
    }

    //按报警图片时显示对话框
    private void showWarningDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        altDialog.setIcon(R.drawable.ic_warning_black_24dp);
        altDialog.setTitle("报警");
        altDialog.setMessage("报警信息：" + SysData.errorMsg + "\n是否清除报警？");
        altDialog.setPositiveButton("清除",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        //清除报警信息
                        SysData.errorMsg = "";
                        SysData.errorId = 0;
                        SysData.resetAlert();                       //复位数据库报警记录
                        imageWarning.setVisibility(View.INVISIBLE);
                        Log.i("报警信息", "已清除");
                    }
                });
        altDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        altDialog.show();
    }

    //设置日期对话框
    private void showDateDialog(){
        calendar = Calendar.getInstance();
        DatePickerDialog dialogDate = new DatePickerDialog(getView().getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Date newDate;
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
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
        //final Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialogTime = new TimePickerDialog(getView().getContext(),
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Date newDate;
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        newDate = calendar.getTime();
                        MainActivity.timeManager.setTime(newDate.getTime());  //设置系统时间
                        MainActivity.setDs3231Time();  //设置DS3231时间
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialogTime.show();
    }

}