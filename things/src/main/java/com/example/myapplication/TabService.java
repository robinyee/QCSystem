package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.IOException;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class TabService extends Fragment {
    private Switch aSwitchD1, aSwitchD2, aSwitchD3, aSwitchD4, aSwitchD5, aSwitchD6, aSwitchD7, aSwitchD8, aSwitchP1,
            aSwitchP2, aSwitchP3, aSwitchH1, aSwitchLED, aSwitchV24, aSwitchDC1, aSwitchRE1, aSwitchDC2, aSwitchRE2;
    private Switch aSwitchS1, aSwitchS2, aSwitchS3, aSwitchS4, aSwitchS5, aSwitchS6, aSwitchS7, aSwitchS8,
            aSwitchS9, aSwitchS10, aSwitchS11, aSwitchS12;
    private Switch aSwitchIn1, aSwitchIn2, aSwitchIn3, aSwitchIn4, aSwitchIsNotice, aSwitchIsEmptyPipeline;

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
            handlerUpdate.sendMessageDelayed(message, 100);
        }
    };

    //刷新界面输出状态
    public void uiUpdate() {
        try {

            //基本输出
            aSwitchD1.setChecked(SysGpio.mGpioOutD1.getValue());
            aSwitchD2.setChecked(SysGpio.mGpioOutD2.getValue());
            aSwitchD3.setChecked(SysGpio.mGpioOutD3.getValue());
            aSwitchD4.setChecked(SysGpio.mGpioOutD4.getValue());
            aSwitchD5.setChecked(SysGpio.mGpioOutD5.getValue());
            aSwitchD6.setChecked(SysGpio.mGpioOutD6.getValue());
            aSwitchD7.setChecked(SysGpio.mGpioOutD7.getValue());
            aSwitchD8.setChecked(SysGpio.mGpioOutD8.getValue());
            aSwitchP1.setChecked(SysGpio.mGpioOutP1.getValue());
            aSwitchP2.setChecked(SysGpio.mGpioOutP2.getValue());
            aSwitchP3.setChecked(SysGpio.mGpioOutP3.getValue());
            aSwitchH1.setChecked(SysGpio.mGpioOutH1.getValue());
            aSwitchLED.setChecked(SysGpio.mGpioOutLED.getValue());
            aSwitchV24.setChecked(SysGpio.mGpioOut24V.getValue());
            aSwitchDC1.setChecked(SysGpio.mGpioOutDC1.getValue());
            aSwitchRE1.setChecked(SysGpio.mGpioOutRE1.getValue());
            aSwitchDC2.setChecked(SysGpio.mGpioOutDC2.getValue());
            aSwitchRE2.setChecked(SysGpio.mGpioOutRE2.getValue());

            //流程控制
            aSwitchS1.setChecked(SysGpio.statusS1);
            aSwitchS2.setChecked(SysGpio.statusS2);
            aSwitchS3.setChecked(SysGpio.statusS3);
            aSwitchS4.setChecked(SysGpio.statusS4);
            aSwitchS5.setChecked(SysGpio.statusS5);
            aSwitchS6.setChecked(SysGpio.statusS6);
            aSwitchS7.setChecked(SysGpio.statusS7);
            aSwitchS8.setChecked(SysGpio.statusS8);
            aSwitchS9.setChecked(SysGpio.statusS9);
            aSwitchS10.setChecked(SysGpio.statusS10);
            aSwitchS11.setChecked(SysGpio.statusS11);
            aSwitchS12.setChecked(SysGpio.statusS12);

            //端子输入信息
            aSwitchIn1.setChecked(SysGpio.mGpioIn1.getValue());
            aSwitchIn2.setChecked(SysGpio.mGpioIn2.getValue());
            aSwitchIn3.setChecked(SysGpio.mGpioIn3.getValue());
            aSwitchIn4.setChecked(SysGpio.mGpioIn4.getValue());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_service, container, false);
        //基本输出Switch按钮赋值
        aSwitchD1 = (Switch) view.findViewById(R.id.d1);
        aSwitchD2 = (Switch) view.findViewById(R.id.d2);
        aSwitchD3 = (Switch) view.findViewById(R.id.d3);
        aSwitchD4 = (Switch) view.findViewById(R.id.d4);
        aSwitchD5 = (Switch) view.findViewById(R.id.d5);
        aSwitchD6 = (Switch) view.findViewById(R.id.d6);
        aSwitchD7 = (Switch) view.findViewById(R.id.d7);
        aSwitchD8 = (Switch) view.findViewById(R.id.d8);
        aSwitchP1 = (Switch) view.findViewById(R.id.p1);
        aSwitchP2 = (Switch) view.findViewById(R.id.p2);
        aSwitchP3 = (Switch) view.findViewById(R.id.p3);
        aSwitchH1 = (Switch) view.findViewById(R.id.h1);
        aSwitchLED = (Switch) view.findViewById(R.id.led);
        aSwitchV24 = (Switch) view.findViewById(R.id.v24);
        aSwitchDC1 = (Switch) view.findViewById(R.id.dc1);
        aSwitchRE1 = (Switch) view.findViewById(R.id.dc1_r);
        aSwitchDC2 = (Switch) view.findViewById(R.id.dc2);
        aSwitchRE2 = (Switch) view.findViewById(R.id.dc2_r);

        //流程控制Switch按钮赋值
        aSwitchS1 = (Switch) view.findViewById(R.id.s1);
        aSwitchS2 = (Switch) view.findViewById(R.id.s2);
        aSwitchS3 = (Switch) view.findViewById(R.id.s3);
        aSwitchS4 = (Switch) view.findViewById(R.id.s4);
        aSwitchS5 = (Switch) view.findViewById(R.id.s5);
        aSwitchS6 = (Switch) view.findViewById(R.id.s6);
        aSwitchS7 = (Switch) view.findViewById(R.id.s7);
        aSwitchS8 = (Switch) view.findViewById(R.id.s8);
        aSwitchS9 = (Switch) view.findViewById(R.id.s9);
        aSwitchS10 = (Switch) view.findViewById(R.id.s10);
        aSwitchS11 = (Switch) view.findViewById(R.id.s11);
        aSwitchS12 = (Switch) view.findViewById(R.id.s12);

        //输入状态Switch
        aSwitchIn1 = (Switch) view.findViewById(R.id.switchIn1);
        aSwitchIn2 = (Switch) view.findViewById(R.id.switchIn2);
        aSwitchIn3 = (Switch) view.findViewById(R.id.switchIn3);
        aSwitchIn4 = (Switch) view.findViewById(R.id.switchIn4);
        aSwitchIsNotice = (Switch) view.findViewById(R.id.switchIsNotice);
        aSwitchIsEmptyPipeline = (Switch) view.findViewById(R.id.switchIsEmptyPipeline);
        //当前状态显示
        aSwitchIsNotice.setChecked(SysData.isNotice);
        aSwitchIsEmptyPipeline.setChecked(SysData.isEmptyPipeline);

        //刷新界面信息
        message = handlerUpdate.obtainMessage(UI_UPDATE);
        handlerUpdate.sendMessageDelayed(message, 1000);

        //打开串口通讯
        //UartCom com1 = new UartCom("UART0", 115200, 8, 1);
        //com1.openUart();
        //MainActivity.com1.sendMsg("hello");  //测试串口通信，发送数据


        //SwitchD1按钮点击
        aSwitchD1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD1.setValue(aSwitchD1.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD2按钮点击
        aSwitchD2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD2.setValue(aSwitchD2.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD3按钮点击
        aSwitchD3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD3.setValue(aSwitchD3.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD4按钮点击
        aSwitchD4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD4.setValue(aSwitchD4.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD5按钮点击
        aSwitchD5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD5.setValue(aSwitchD5.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD6按钮点击
        aSwitchD6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD6.setValue(aSwitchD6.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD7按钮点击
        aSwitchD7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD7.setValue(aSwitchD7.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD8按钮点击
        aSwitchD8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD8.setValue(aSwitchD8.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchP1按钮点击
        aSwitchP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutP1.setValue(aSwitchP1.isChecked());
                    if(aSwitchP1.isChecked()) {
                        Thread.sleep(1000);
                        MainActivity.com0.pumpInit(1);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchP2按钮点击
        aSwitchP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutP2.setValue(aSwitchP2.isChecked());
                    if(aSwitchP2.isChecked()) {
                        Thread.sleep(1000);
                        MainActivity.com0.pumpInit(2);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchP3按钮点击
        aSwitchP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutP3.setValue(aSwitchP3.isChecked());
                    if(aSwitchP3.isChecked()) {
                        Thread.sleep(1000);
                        MainActivity.com0.pumpInit(3);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchH1按钮点击
        aSwitchH1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutH1.setValue(aSwitchH1.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchLED按钮点击
        aSwitchLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutLED.setValue(aSwitchLED.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchV24按钮点击
        aSwitchV24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOut24V.setValue(aSwitchV24.isChecked());
                    if(aSwitchV24.isChecked() && SysData.isRun == false) {
                        //开始读取模拟量
                        SysGpio.readTempFlag = true;
                        SysGpio.readAd();
                    } else {
                        //停止读取模拟量
                        SysGpio.readTempFlag = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchDC1按钮点击
        aSwitchDC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutDC1.setValue(aSwitchDC1.isChecked());
                    if(SysGpio.mGpioOutDC1.getValue()) {
                        SysGpio.mGpioOutRE1.setValue(false);
                        aSwitchRE1.setChecked(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchRE1按钮点击
        aSwitchRE1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutRE1.setValue(aSwitchRE1.isChecked());
                    if(SysGpio.mGpioOutRE1.getValue()) {
                        SysGpio.mGpioOutDC1.setValue(false);
                        aSwitchDC1.setChecked(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchDC2按钮点击
        aSwitchDC2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutDC2.setValue(aSwitchDC2.isChecked());
                    if(SysGpio.mGpioOutDC2.getValue()) {
                        SysGpio.mGpioOutRE2.setValue(false);
                        aSwitchRE2.setChecked(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchRE2按钮点击
        aSwitchRE2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutRE2.setValue(aSwitchRE2.isChecked());
                    if(SysGpio.mGpioOutRE2.getValue()) {
                        SysGpio.mGpioOutDC2.setValue(false);
                        aSwitchDC2.setChecked(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchS1进水样按钮点击
        aSwitchS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS1.isChecked() && !SysGpio.statusS1) {
                    SysGpio.s1_JiaShuiYang();
                }
            }
        });

        //SwitchS2加硫酸按钮点击
        aSwitchS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS2.isChecked() && !SysGpio.statusS2) {
                    SysGpio.s2_JiaLiuSuan();
                }
            }
        });

        //SwitchS3加高锰酸钾按钮点击
        aSwitchS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS3.isChecked() && !SysGpio.statusS3) {
                    SysGpio.s3_JiaGaoMengSuanJIa();
                }
            }
        });

        //SwitchS4加草酸钠按钮点击
        aSwitchS4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS4.isChecked() && !SysGpio.statusS4) {
                    SysGpio.s4_JiaCaoSuanNa();
                }
            }
        });

        //SwitchS5消解按钮点击
        aSwitchS5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS5.isChecked() && !SysGpio.statusS5) {
                    SysGpio.s5_XiaoJie();
                }
            }
        });

        //SwitchS6滴定按钮点击
        aSwitchS6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS6.isChecked() && !SysGpio.statusS6) {
                    SysGpio.s6_DiDing();
                }
            }
        });

        //SwitchS7水质测定按钮点击
        aSwitchS7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS7.isChecked() && !SysGpio.statusS7) {
                    SysGpio.s7_ShuiZhiCeDing();
                    SysData.workFrom = "触摸屏启动";           //启动分析命令来自于 触摸屏、串口、Web、定时启动
                }
            }
        });

        //SwitchS8仪器复位按钮点击
        aSwitchS8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS8.isChecked() && !SysGpio.statusS8) {
                    SysGpio.s8_Reset();
                }
            }
        });

        //SwitchS9空白实验按钮点击
        aSwitchS9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS9.isChecked() && !SysGpio.statusS9) {
                    SysGpio.s9_KongBaiShiYan();
                    SysData.workFrom = "触摸屏启动";           //启动分析命令来自于 触摸屏、串口、Web、定时启动
                }
            }
        });

        //SwitchS10标样测定按钮点击
        aSwitchS10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS10.isChecked() && !SysGpio.statusS10) {
                    SysGpio.s10_BiaoYangCeDing();
                    SysData.workFrom = "触摸屏启动";           //启动分析命令来自于 触摸屏、串口、Web、定时启动
                }
            }
        });

        //SwitchS11仪器校准按钮点击
        aSwitchS11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS11.isChecked() && !SysGpio.statusS11) {
                    SysGpio.s11_Calibration();
                    SysData.workFrom = "触摸屏启动";           //启动分析命令来自于 触摸屏、串口、Web、定时启动
                }
            }
        });

        //SwitchS12紧急停止按钮点击
        aSwitchS12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS12.isChecked() && !SysGpio.statusS12) {
                    SysGpio.s12_Stop();
                }
            }
        });

        //SwitchIsNotice按钮点击
        aSwitchIsNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.isNotice = aSwitchIsNotice.isChecked();
                saveMeterParameter();
            }
        });

        //SwitchIsEmptyPipeline按钮点击
        aSwitchIsEmptyPipeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysData.isEmptyPipeline = aSwitchIsEmptyPipeline.isChecked();
                saveMeterParameter();
            }
        });

        return view;
    }

    //保存仪表参数
    public void saveMeterParameter() {
        //打开文件
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences("Parameter", MODE_PRIVATE).edit();
        editor.putBoolean("isNotice", SysData.isNotice);
        editor.putBoolean("isEmptyPipeline", SysData.isEmptyPipeline);

        Log.i("参数存储", "试剂量报警已存储" + SysData.isNotice);
        Log.i("参数存储", "试剂量报警已存储" + SysData.isEmptyPipeline);
        //提交保存
        editor.apply();
    }


    @Override
    public void onResume() {
        super.onResume();
        uiUpdate();
        Log.d(TAG, "run: 系统维护页面onResume");
    }

}
