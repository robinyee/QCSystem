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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.IOException;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class TabService extends Fragment {
    private Switch aSwitchD1, aSwitchD2, aSwitchD3, aSwitchD4, aSwitchD5, aSwitchD6, aSwitchD7, aSwitchD8, aSwitchP1,
            aSwitchP2, aSwitchP3, aSwitchH1, aSwitchLED, aSwitchV24, aSwitchDC1, aSwitchRE1, aSwitchDC2, aSwitchRE2;
    private Switch aSwitchS1, aSwitchS2, aSwitchS3, aSwitchS4, aSwitchS5, aSwitchS6, aSwitchS7, aSwitchS8,
            aSwitchS9, aSwitchS10, aSwitchS11, aSwitchS12;
    private Switch aSwitchC1, aSwitchC2, aSwitchC3, aSwitchC4, aSwitchC5, aSwitchC6;
    private Switch aSwitchIn1, aSwitchIn2, aSwitchIn3, aSwitchIn4, aSwitchIsNotice, aSwitchIsEmptyPipeline;
    private Button buttonNH3Start, buttonTPStart, buttonTNStart, buttonCODStart;
    private Spinner spinnerWaterType, spinnerSampleType;

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
            //aSwitchD7.setChecked(SysGpio.mGpioOutD7.getValue());
            aSwitchD8.setChecked(SysGpio.mGpioOutD8.getValue());
            aSwitchP1.setChecked(SysGpio.mGpioOutP1.getValue());
            aSwitchP2.setChecked(SysGpio.mGpioOutP2.getValue());
            //aSwitchP3.setChecked(SysGpio.mGpioOutP3.getValue());
            //aSwitchH1.setChecked(SysGpio.mGpioOutH1.getValue());
            //aSwitchLED.setChecked(SysGpio.mGpioOutLED.getValue());
            //aSwitchV24.setChecked(SysGpio.mGpioOut24V.getValue());
            aSwitchDC1.setChecked(SysGpio.mGpioOutDC1.getValue());
            aSwitchRE1.setChecked(SysGpio.mGpioOutRE1.getValue());
            //aSwitchDC2.setChecked(SysGpio.mGpioOutDC2.getValue());
            //aSwitchRE2.setChecked(SysGpio.mGpioOutRE2.getValue());

            //多通道阀切换
            SysGpio.statusSwtch();
            aSwitchC1.setChecked(SysGpio.statusC1);
            aSwitchC2.setChecked(SysGpio.statusC2);
            aSwitchC3.setChecked(SysGpio.statusC3);
            aSwitchC4.setChecked(SysGpio.statusC4);
            aSwitchC5.setChecked(SysGpio.statusC5);
            aSwitchC6.setChecked(SysGpio.statusC6);

            //流程控制
            /*
            aSwitchS1.setChecked(SysGpio.statusS1);
            aSwitchS2.setChecked(SysGpio.statusS2);
            aSwitchS3.setChecked(SysGpio.statusS3);
            aSwitchS4.setChecked(SysGpio.statusS4);
            aSwitchS5.setChecked(SysGpio.statusS5);
            aSwitchS6.setChecked(SysGpio.statusS6);
            aSwitchS7.setChecked(SysGpio.statusS7);
            aSwitchS8.setChecked(SysGpio.statusS8);
            //aSwitchS9.setChecked(SysGpio.statusS9);
            //aSwitchS10.setChecked(SysGpio.statusS10);
            //aSwitchS11.setChecked(SysGpio.statusS11);
            //aSwitchS12.setChecked(SysGpio.statusS12);

             */
            aSwitchS1.setChecked(SysGpio.statusS[1]);
            aSwitchS2.setChecked(SysGpio.statusS[2]);
            aSwitchS3.setChecked(SysGpio.statusS[3]);
            aSwitchS4.setChecked(SysGpio.statusS[4]);
            aSwitchS5.setChecked(SysGpio.statusS[5]);
            aSwitchS6.setChecked(SysGpio.statusS[6]);
            aSwitchS7.setChecked(SysGpio.statusS[7]);
            aSwitchS8.setChecked(SysGpio.statusS[8]);

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
        //aSwitchD7 = (Switch) view.findViewById(R.id.d7);
        aSwitchD8 = (Switch) view.findViewById(R.id.d8);
        aSwitchP1 = (Switch) view.findViewById(R.id.p1);
        aSwitchP2 = (Switch) view.findViewById(R.id.p2);
        aSwitchP3 = (Switch) view.findViewById(R.id.p3);
        //aSwitchH1 = (Switch) view.findViewById(R.id.h1);
        //aSwitchLED = (Switch) view.findViewById(R.id.led);
        //aSwitchV24 = (Switch) view.findViewById(R.id.v24);
        aSwitchDC1 = (Switch) view.findViewById(R.id.dc1);
        aSwitchRE1 = (Switch) view.findViewById(R.id.dc1_r);
        //aSwitchDC2 = (Switch) view.findViewById(R.id.dc2);
        //aSwitchRE2 = (Switch) view.findViewById(R.id.dc2_r);

        //流程控制Switch按钮赋值
        aSwitchC1 = (Switch) view.findViewById(R.id.c1);
        aSwitchC2 = (Switch) view.findViewById(R.id.c2);
        aSwitchC3 = (Switch) view.findViewById(R.id.c3);
        aSwitchC4 = (Switch) view.findViewById(R.id.c4);
        aSwitchC5 = (Switch) view.findViewById(R.id.c5);
        aSwitchC6 = (Switch) view.findViewById(R.id.c6);

        //流程控制Switch按钮赋值
        aSwitchS1 = (Switch) view.findViewById(R.id.s1_inletWater);
        aSwitchS2 = (Switch) view.findViewById(R.id.s2_addReagent);
        aSwitchS3 = (Switch) view.findViewById(R.id.s3_supplySamples);
        aSwitchS4 = (Switch) view.findViewById(R.id.s4_initialize);
        aSwitchS5 = (Switch) view.findViewById(R.id.s5_reset);
        aSwitchS6 = (Switch) view.findViewById(R.id.s6_reboot);
        aSwitchS8 = (Switch) view.findViewById(R.id.s8_clean);

        //选择标样类型
        spinnerWaterType = view.findViewById(R.id.spinner_WaterType);
        spinnerSampleType = view.findViewById(R.id.spinner_SampleType);
        aSwitchS7 = (Switch) view.findViewById(R.id.s7_start);
        SysData.arrWaterType = getResources().getStringArray(R.array.waterTyep);
        SysData.arrSampleType = getResources().getStringArray(R.array.sampleTyep);
        /*
        buttonNH3Start = (Button) view.findViewById(R.id.button_NH3_start);
        buttonTPStart = (Button) view.findViewById(R.id.button_TP_start);
        buttonTNStart = (Button) view.findViewById(R.id.button_TN_start);
        buttonCODStart = (Button) view.findViewById(R.id.button_COD_start);

         */

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
                } catch (IOException e) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //SwitchP3按钮点击
        aSwitchP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(aSwitchP3.isChecked()){
                        SysData.microPumpOn = true;
                        SysGpio.microPumpRun(0);
                        aSwitchP3.setChecked(false);
                    } else {
                        SysData.microPumpOn = false;
                    }
                } catch (Exception e) {
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

        //SwitchC1多通道阀到1通道
        aSwitchC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchC1.isChecked() && !SysGpio.statusC1) {
                        //切换多通道阀到指定通道
                        SysData.reagentChannel = 1;
                        MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                }
            }
        });

        //SwitchC2多通道阀到2通道
        aSwitchC2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchC2.isChecked() && !SysGpio.statusC2) {
                    //切换多通道阀到指定通道
                    SysData.reagentChannel = 2;
                    MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                }
            }
        });

        //SwitchC3多通道阀到3通道
        aSwitchC3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchC3.isChecked() && !SysGpio.statusC3) {
                    //切换多通道阀到指定通道
                    SysData.reagentChannel = 3;
                    MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                }
            }
        });

        //SwitchC4多通道阀到4通道
        aSwitchC4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchC4.isChecked() && !SysGpio.statusC4) {
                    //切换多通道阀到指定通道
                    SysData.reagentChannel = 4;
                    MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                }
            }
        });

        //SwitchC5多通道阀到5通道
        aSwitchC5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchC5.isChecked() && !SysGpio.statusC5) {
                    //切换多通道阀到指定通道
                    SysData.reagentChannel = 5;
                    MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                }
            }
        });

        //SwitchC6多通道阀到6通道
        aSwitchC6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchC6.isChecked() && !SysGpio.statusC6) {
                    //切换多通道阀到指定通道
                    SysData.reagentChannel = 6;
                    MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                }
            }
        });

        //SwitchS1进水样按钮点击
        aSwitchS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS1.isChecked() && !SysGpio.statusS1) {
                    SysGpio.s1_inletWater(SysData.inletWaterStep);
                }
            }
        });

        //SwitchS2添加试剂
        aSwitchS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS2.isChecked() && !SysGpio.statusS2) {
                    SysGpio.s2_addReagent(SysData.reagentChannel, SysData.addReagentStep);
                }
            }
        });

        //SwitchS3混合溶液、供样、清洗
        aSwitchS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS3.isChecked() && !SysGpio.statusS3) {
                    SysGpio.s3_supplySamples();
                }
            }
        });

        //SwitchS4仪表初始化
        aSwitchS4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS4.isChecked() && !SysGpio.statusS4) {
                    SysGpio.s4_initialize();
                }
            }
        });

        //SwitchS5关闭所有电源
        aSwitchS5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS5.isChecked() && !SysGpio.statusS5) {
                    SysGpio.powerOff();
                    SysGpio.statusS5 = false;
                }
            }
        });

        //SwitchS6重新启动
        aSwitchS6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS6.isChecked() && !SysGpio.statusS6) {
                    //提醒系统将重置
                    Toast.makeText(getContext(),"正在重置系统，请稍后...", Toast.LENGTH_LONG).show();
                    //重启软件
                    System.exit(0);
                }
            }
        });

        //SwitchS7启动配制标样流程
        aSwitchS7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS7.isChecked() && !SysGpio.statusS7) {
                    SysData.waterType = spinnerWaterType.getSelectedItemPosition();
                    SysData.sampleType = spinnerSampleType.getSelectedItemPosition();
                    SysGpio.s7_preparationWaterSamples(SysData.waterType, SysData.sampleType);
                    //SysData.strWaterType = arrWaterType[spinnerWaterType.getSelectedItemPosition()];
                    //SysData.strSampleType = arrSampleType[spinnerSampleType.getSelectedItemPosition()];
                    Toast.makeText(getContext(),"启动配制标样流程，请稍后...", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "run: 启动配制水样类型：" + SysData.strWaterType);
                    Log.d(TAG, "run: 启动配制标样名称：" + SysData.strSampleType);
                }
            }
        });

        //SwitchS8清洗
        aSwitchS8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitchS8.isChecked() && !SysGpio.statusS8) {
                    SysGpio.s8_cleaning();
                }
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
