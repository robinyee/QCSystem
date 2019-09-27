package com.example.myapplication;

import android.annotation.SuppressLint;
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

public class TabService extends Fragment {
    private Switch aSwitchD1, aSwitchD2, aSwitchD3, aSwitchD4, aSwitchD5, aSwitchD6, aSwitchD7, aSwitchD8, aSwitchD9,
            aSwitchD10, aSwitchD11, aSwitchD12, aSwitchLED, aSwitchV24, aSwitchDC1, aSwitchRE1, aSwitchDC2, aSwitchRE2;
    private Switch aSwitchS1, aSwitchS2, aSwitchS3, aSwitchS4, aSwitchS5, aSwitchS6, aSwitchS7, aSwitchS8,
            aSwitchS9, aSwitchS10, aSwitchS11, aSwitchS12;


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
        aSwitchD9 = (Switch) view.findViewById(R.id.d9);
        aSwitchD10 = (Switch) view.findViewById(R.id.d10);
        aSwitchD11 = (Switch) view.findViewById(R.id.d11);
        aSwitchD12 = (Switch) view.findViewById(R.id.d12);
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

        //刷新界面信息
        message = handlerUpdate.obtainMessage(UI_UPDATE);
        handlerUpdate.sendMessageDelayed(message, 1000);

        //打开串口通讯
        //UartCom com1 = new UartCom("UART0", 115200, 8, 1);
        //com1.openUart();
        MainActivity.com1.sendMsg("hello");  //测试串口通信，发送数据


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

        //SwitchD9按钮点击
        aSwitchD9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD9.setValue(aSwitchD9.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD10按钮点击
        aSwitchD10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD10.setValue(aSwitchD10.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD11按钮点击
        aSwitchD11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD11.setValue(aSwitchD11.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchD12按钮点击
        aSwitchD12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutD12.setValue(aSwitchD12.isChecked());
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchRE1按钮点击
        aSwitchRE2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SysGpio.mGpioOutRE2.setValue(aSwitchRE2.isChecked());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchS1按钮点击
        aSwitchS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS1.isChecked() && !SysGpio.statusS1) {
                    SysGpio.s1_JSY(200, 3000);
                }
            }
        });

        //SwitchS2按钮点击
        aSwitchS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS2.isChecked() && !SysGpio.statusS2) {
                    SysGpio.c1_SZFX();
                }
            }
        });

        return view;
    }


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
            aSwitchD9.setChecked(SysGpio.mGpioOutD9.getValue());
            aSwitchD10.setChecked(SysGpio.mGpioOutD10.getValue());
            aSwitchD11.setChecked(SysGpio.mGpioOutD11.getValue());
            aSwitchD12.setChecked(SysGpio.mGpioOutD12.getValue());
            aSwitchLED.setChecked(SysGpio.mGpioOutLED.getValue());
            aSwitchV24.setChecked(SysGpio.mGpioOut24V.getValue());

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiUpdate();
        Log.d(TAG, "run: 系统维护页面onResume");
    }

}
