package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

import static android.content.ContentValues.TAG;
import static com.example.myapplication.SysData.calculationValue;
import static com.example.myapplication.SysData.codValue;
import static com.example.myapplication.SysData.coefficient;
import static com.example.myapplication.SysData.didingDeviation;
import static com.example.myapplication.SysData.isEmptyPipeline;
import static com.example.myapplication.SysData.isRun;

public class SysGpio {
    static PeripheralManager manager;
    static Gpio mGpioOutD1, mGpioOutD2, mGpioOutD3, mGpioOutD4, mGpioOutD5, mGpioOutD6, mGpioOutD7, mGpioOutD8, mGpioOutP1,
            mGpioOutP2, mGpioOutP3, mGpioOutH1, mGpioOutLED, mGpioOut24V, mGpioOutDC1, mGpioOutRE1, mGpioOutDC2, mGpioOutRE2;
    static Gpio mGpioIn1, mGpioIn2, mGpioIn3, mGpioIn4;
    static boolean readTempFlag = false; //是否持续读取温度
    static boolean tempControlFlag = false; //是否进行温度控制
    static int pumpTimeOut = 0;

    //仪器控制页面状态
    static boolean statusS1 = false;       //S1状态
    static boolean statusS2 = false;       //S2状态
    static boolean statusS3 = false;       //S3状态
    static boolean statusS4 = false;       //S4状态
    static boolean statusS5 = false;       //S5状态
    static boolean statusS6 = false;       //S6状态
    static boolean statusS7 = false;       //S7状态
    static boolean statusS8 = false;       //S8状态
    static boolean statusS9 = false;       //S9状态
    static boolean statusS10 = false;      //S10状态
    static boolean statusS11 = false;      //S11状态
    static boolean statusS12 = false;      //S12状态

    //设置输出引脚
    private static final String GPIO_OUT_D1 = "BCM4";  //D1进样阀开关量输出
    private static final String GPIO_OUT_D2 = "BCM17";  //D2进样阀开关量输出
    private static final String GPIO_OUT_D3 = "BCM18";  //D3排空阀开关量输出
    private static final String GPIO_OUT_D4 = "BCM27";  //D4微量泵开关量输出
    private static final String GPIO_OUT_D5 = "BCM22";  //D5加热开关量输出
    private static final String GPIO_OUT_D6 = "BCM23";  //D6
    private static final String GPIO_OUT_D7 = "BCM24";  //D7
    private static final String GPIO_OUT_D8 = "BCM10";  //D8
    private static final String GPIO_OUT_P1 = "BCM9";   //P1
    private static final String GPIO_OUT_P2 = "BCM25";  //P2
    private static final String GPIO_OUT_P3 = "BCM11";  //P3
    private static final String GPIO_OUT_H1 = "BCM8";   //H1
    private static final String GPIO_OUT_LED = "BCM21"; //LED 3.5V LED灯开关量输出
    private static final String GPIO_OUT_24V = "BCM6";  //24V 24V供电
    private static final String GPIO_OUT_DC1 = "BCM7";  //DC1正转
    private static final String GPIO_OUT_RE1 = "BCM5";  //DC1反转
    private static final String GPIO_OUT_DC2 = "BCM12";  //DC2正转
    private static final String GPIO_OUT_RE2 = "BCM13";  //DC2反转

    //设置输入引脚
    private static final String GPIO_IN_1 = "BCM16";
    private static final String GPIO_IN_2 = "BCM19";
    private static final String GPIO_IN_3 = "BCM20";
    private static final String GPIO_IN_4 = "BCM26";


    public static void gpioInit() {
        manager = PeripheralManager.getInstance();
        try {
            //打开Gpio端口
            mGpioOutD1 = manager.openGpio(GPIO_OUT_D1);
            mGpioOutD2 = manager.openGpio(GPIO_OUT_D2);
            mGpioOutD3 = manager.openGpio(GPIO_OUT_D3);
            mGpioOutD4 = manager.openGpio(GPIO_OUT_D4);
            mGpioOutD5 = manager.openGpio(GPIO_OUT_D5);
            mGpioOutD6 = manager.openGpio(GPIO_OUT_D6);
            mGpioOutD7 = manager.openGpio(GPIO_OUT_D7);
            mGpioOutD8 = manager.openGpio(GPIO_OUT_D8);
            mGpioOutP1 = manager.openGpio(GPIO_OUT_P1);
            mGpioOutP2 = manager.openGpio(GPIO_OUT_P2);
            mGpioOutP3 = manager.openGpio(GPIO_OUT_P3);
            mGpioOutH1 = manager.openGpio(GPIO_OUT_H1);
            mGpioOutLED = manager.openGpio(GPIO_OUT_LED);
            mGpioOut24V = manager.openGpio(GPIO_OUT_24V);
            mGpioOutDC1 = manager.openGpio(GPIO_OUT_DC1);
            mGpioOutRE1 = manager.openGpio(GPIO_OUT_RE1);
            mGpioOutDC2 = manager.openGpio(GPIO_OUT_DC2);
            mGpioOutRE2 = manager.openGpio(GPIO_OUT_RE2);

            //端口输入端口
            mGpioIn1 = manager.openGpio(GPIO_IN_1);
            mGpioIn2 = manager.openGpio(GPIO_IN_2);
            mGpioIn3 = manager.openGpio(GPIO_IN_3);
            mGpioIn4 = manager.openGpio(GPIO_IN_4);

            //初始化Gpio端口的状态
            mGpioOutD1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD4.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD5.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD6.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD7.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD8.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutP1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutP2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutP3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutH1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutLED.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOut24V.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutDC1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutRE1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutDC2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutRE2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量

            // 初始化为输入端口
            mGpioIn1.setDirection(Gpio.DIRECTION_IN);
            mGpioIn2.setDirection(Gpio.DIRECTION_IN);
            mGpioIn3.setDirection(Gpio.DIRECTION_IN);
            mGpioIn4.setDirection(Gpio.DIRECTION_IN);
            // 高电平为活动状态
            mGpioIn1.setActiveType(Gpio.ACTIVE_HIGH);
            mGpioIn2.setActiveType(Gpio.ACTIVE_HIGH);
            mGpioIn3.setActiveType(Gpio.ACTIVE_HIGH);
            mGpioIn4.setActiveType(Gpio.ACTIVE_HIGH);
            // 设置状态转换响应
            mGpioIn1.setEdgeTriggerType(Gpio.EDGE_BOTH);
            mGpioIn2.setEdgeTriggerType(Gpio.EDGE_BOTH);
            mGpioIn3.setEdgeTriggerType(Gpio.EDGE_BOTH);
            mGpioIn4.setEdgeTriggerType(Gpio.EDGE_BOTH);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //关闭GPIO
    public static void gpioClose() {
        manager = PeripheralManager.getInstance();
        try {
            //关闭所有Gpio端口
            mGpioOutD1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD4.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD5.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD6.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD7.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD8.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutP1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutP2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutP3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutH1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutLED.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOut24V.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutDC1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutRE1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutDC2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutRE2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            //关闭Gpio端口
            if(mGpioOutD1 != null) mGpioOutD1.close();
            if(mGpioOutD2 != null) mGpioOutD2.close();
            if(mGpioOutD3 != null) mGpioOutD3.close();
            if(mGpioOutD4 != null) mGpioOutD4.close();
            if(mGpioOutD5 != null) mGpioOutD5.close();
            if(mGpioOutD6 != null) mGpioOutD6.close();
            if(mGpioOutD7 != null) mGpioOutD7.close();
            if(mGpioOutD8 != null) mGpioOutD8.close();
            if(mGpioOutP1 != null) mGpioOutP1.close();
            if(mGpioOutP2 != null) mGpioOutP2.close();
            if(mGpioOutP3 != null) mGpioOutP3.close();
            if(mGpioOutH1 != null) mGpioOutH1.close();
            if(mGpioOutLED != null) mGpioOutLED.close();
            if(mGpioOut24V != null) mGpioOut24V.close();
            if(mGpioOutDC1 != null) mGpioOutDC1.close();
            if(mGpioOutRE1 != null) mGpioOutRE1.close();
            if(mGpioOutDC2 != null) mGpioOutDC2.close();
            if(mGpioOutRE2 != null) mGpioOutRE2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(manager != null) manager = null;
        Log.i("关闭GPIO", "GPIO已关闭并注销");
    }

    //设置Gpio输出口的开关状态，端口名称，
    public static void setGpioOut(Gpio mGpioOut, boolean v) {
        try {
            mGpioOut.setValue(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取Gpio输出口的开关状态，端口名称，
    public static boolean getGpioOut(Gpio  mGpioOut) {
        boolean isOn = false;
        try {
            isOn = mGpioOut.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isOn;
    }


    //读取模拟量值
    public static void readAd() {
        //Log.d(TAG, "run: 开始读取模拟量数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 开始读取模拟量数据");
                do {

                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }


                    MainActivity.com0.getAd();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } while (readTempFlag);
            }
        }).start();
    }

    //搅拌控制
    public static void jiaoBanControl() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                Log.d(TAG, "run: 开始搅拌");
                while (SysData.jiaoBanType != -1){

                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    if(SysData.jiaoBanType == 1) {
                        try {
                            SysGpio.mGpioOutRE1.setValue(true);   //开始搅拌
                            //Log.d(TAG, "run: 搅拌状态" + SysGpio.mGpioOutRE1.getValue());
                            Thread.sleep(3000);
                            SysGpio.mGpioOutRE1.setValue(false);   //停止搅拌
                            //Log.d(TAG, "run: 搅拌状态" + SysGpio.mGpioOutRE1.getValue());
                            Thread.sleep(3000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(SysData.jiaoBanType == 2) {
                        try {
                            SysGpio.mGpioOutRE1.setValue(true);   //开始搅拌
                            //Log.d(TAG, "run: 搅拌状态" + SysGpio.mGpioOutRE1.getValue());
                            Thread.sleep(3000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(SysData.jiaoBanType == 0) {
                        try {
                            SysGpio.mGpioOutRE1.setValue(false);   //停止搅拌
                            //Log.d(TAG, "run: 搅拌状态" + SysGpio.mGpioOutRE1.getValue());
                            Thread.sleep(3000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    SysGpio.mGpioOutRE1.setValue(false);   //停止搅拌
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    //温度控制
    public static void tempControl(final double temp){
        //Log.d(TAG, "run: 开始温度控制");
        new Thread(new Runnable() {
            @Override
            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                Log.d(TAG, "run: 开始温度控制");
                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    //低于设置温度，开启加热器
                    if((SysData.tempIn < temp &&  SysData.tempOut < (temp + 3)) || SysData.tempOut < temp) {
                        try {
                            mGpioOutH1.setValue(true);
                            Log.d(TAG, "run: 开始加热");
                            //等待5S
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //高于设置温度，关闭加热器
                    if(SysData.tempIn > temp || (SysData.tempIn > (temp - 1) && SysData.tempOut > (temp + 3))) {
                        try {
                            mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                            //等待5S
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
/*
                    //温度高于75度，间断加热
                    if(SysData.tempIn > 75 && SysData.tempOut > 120) {
                        try {
                            mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //等待20S
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
*/
                    //温度高于85度，间断加热
                    if(SysData.tempIn > 85 && SysData.tempOut > 120) {
                        try {
                            mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                            Thread.sleep(10000);
                            mGpioOutH1.setValue(true);
                            Log.d(TAG, "run: 开始加热");
                            Thread.sleep(5000);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //温度高于90度，间断加热
                    if(SysData.tempIn > 90 && SysData.tempOut > 100) {
                        try {
                            mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //等待20S
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //加热器温度大于150度，停止加热
                    if(SysData.tempIn > 98 || SysData.tempOut > 150) {
                        try {
                            mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } while (tempControlFlag);
            }
        }).start();
    }

    //查询注射泵状态 n-泵号 t-查询间隔时间
    public static void pumpStatus(int n, int t) throws InterruptedException {
        pumpTimeOut = 0;
        SysData.Pump[n] = -2;
        do {
            pumpTimeOut ++;
            MainActivity.com0.pumpCmd(n, "status", 0);
            Thread.sleep(t);
            Log.d(TAG, "run: 泵" + n + "状态：" + SysData.Pump[n] );
            if(pumpTimeOut > 100) {
                SysData.errorMsg = "注射泵" + n + "故障";
                SysData.errorId = 8;
                SysData.saveAlertToDB();  //保存报警记录
                return;
            }
        } while(SysData.Pump[n] != 0x00);
    }

    //测定前排空进样管，进水样、进标样、进空白样润洗管路，通过电磁阀D1控制
    public static void paikong() {
        Log.d(TAG, "run: 排空进样管路线程开始");
        //handler.sendEmptyMessage(MESSAGE_S1_ON);
        //statusS13 = true;
        try {
            //SysGpio.mGpioOutD8.setValue(true);  //开启排空阀排空反应器和管路中的液体
            SysGpio.mGpioOutP1.setValue(true);  //开启水样泵电源
            Thread.sleep(3000);
            Log.d(TAG, "run: P1状态" + SysGpio.mGpioOutP1.getValue());
            Log.d(TAG, "run: 发送串口启动进样泵指令" );

            //注射泵状态查询
            pumpStatus(1, 1000);

            //注射泵1状态正常时执行
            if(SysData.Pump[1] == 0x00) {
                //注射泵抽取液体
                MainActivity.com0.pumpCmd(1, "turn", 30);
                Thread.sleep(15000);
            }
            //注射泵状态查询
            pumpStatus(1, 1000);
            SysGpio.mGpioOutD8.setValue(true);  //开启排空阀排空反应器和管路中的液体
            Thread.sleep(30000);  //等待30秒，排空反应器液体
            SysGpio.mGpioOutD8.setValue(false);  //关闭排空阀排开始进水样
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //handler.sendEmptyMessage(MESSAGE_S1_OFF);
        //statusS13 = false;
        Log.d(TAG, "run: 排空进样管线程结束");
    }

    //S1进样流程，进水样、进标样、进空白样，通过电磁阀D1控制
    public static void s1_JiaShuiYang() {

        new Thread(new Runnable() {

            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                Log.d(TAG, "run: 进水样线程开始");
                //handler.sendEmptyMessage(MESSAGE_S1_ON);
                statusS1 = true;
                try {
                    if(isEmptyPipeline) {
                        paikong();   //如果需要排空进样管路，进样前排空进样管内液体
                    }
                    SysGpio.mGpioOutP1.setValue(true);  //开启水样泵电源
                    Thread.sleep(3000);
                    Log.d(TAG, "run: P1电源" + SysGpio.mGpioOutP1.getValue());
                    Log.d(TAG, "run: 发送串口启动进样泵指令" );
                    //注射泵状态查询
                    pumpStatus(1, 1000);
                    //注射泵1状态正常时执行
                    if(SysData.Pump[1] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(1, "turn", SysData.shuiyangStep);
                        Thread.sleep(41000);
                    }
                    //注射泵状态查询
                    pumpStatus(1, 1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //handler.sendEmptyMessage(MESSAGE_S1_OFF);
                statusS1 = false;
                Log.d(TAG, "run: 进水样线程结束");
            }
        }).start();
    }

    //S2加硫酸流程
    public static void s2_JiaLiuSuan() {

        new Thread(new Runnable() {

            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                Log.d(TAG, "run: 加硫酸线程开始");
                statusS2 = true;
                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());

                    //首先排空试剂管中的试剂
                    SysGpio.mGpioOutD2.setValue(true);
                    Log.d(TAG, "run: D2状态" + SysGpio.mGpioOutD2.getValue());

                    //等待3S
                    Thread.sleep(1000);

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);     //注射泵吸气
                        Thread.sleep(22000);
                    }

                    SysGpio.mGpioOutD3.setValue(true);
                    Log.d(TAG, "run: D3状态" + SysGpio.mGpioOutD3.getValue());


                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //排空容器中的硫酸
                        Thread.sleep(22000);
                    }

                    //Log.d(TAG, "run: 发送串口启动进样泵指令" + num);

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 9600);     //抽取硫酸试剂
                        Thread.sleep(15000);
                    }

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出液体
                        MainActivity.com0.pumpCmd(2, "push", 2400);     //压出硫酸试剂
                        Thread.sleep(4000);
                    }
                    SysGpio.mGpioOutD5.setValue(true);                                       //打开D5电磁阀

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出液体
                        MainActivity.com0.pumpCmd(2, "push", SysData.liusuanStep);     //压出硫酸试剂
                        Thread.sleep(8000);
                    }

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 1200);     //吸回管道剩余硫酸试剂
                        Thread.sleep(3000);
                    }
                    SysGpio.mGpioOutD5.setValue(false);                                       //关闭D5电磁阀

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //压出硫酸试剂,返回0位
                        Thread.sleep(10000);
                    }

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    SysGpio.mGpioOutD2.setValue(false);
                    Log.d(TAG, "run: D2状态" + SysGpio.mGpioOutD2.getValue());
                    SysGpio.mGpioOutD3.setValue(false);
                    Log.d(TAG, "run: D3状态" + SysGpio.mGpioOutD3.getValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //handler.sendEmptyMessage(MESSAGE_S1_OFF);
                statusS2 = false;
                Log.d(TAG, "run: 加硫酸线程结束");
            }
        }).start();
    }

    //S3加高锰酸钾流程
    public static void s3_JiaGaoMengSuanJIa() {

        new Thread(new Runnable() {

            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                Log.d(TAG, "run: 加高锰酸钾线程开始");
                //handler.sendEmptyMessage(MESSAGE_S1_ON);
                statusS3 = true;
                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
                    Thread.sleep(3000);
                    //Log.d(TAG, "run: 发送串口启动进样泵指令" );

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);     //抽取高锰酸钾试剂
                        Thread.sleep(20000);
                    }

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出液体
                        MainActivity.com0.pumpCmd(2, "push", 6400);     //压出高锰酸钾试剂
                        Thread.sleep(10000);
                    }
                    SysGpio.mGpioOutD2.setValue(true);                                       //打开D2电磁阀

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "push", SysData.gaomengsuanjiaStep);     //压出高锰酸钾试剂，进入反应器的高锰酸钾体积
                        Thread.sleep(10000);
                    }

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 1600);     //注射泵吸气
                        Thread.sleep(3000);
                    }
                    SysGpio.mGpioOutD2.setValue(false);                                       //关闭D2电磁阀

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //压出高锰酸钾试剂,返回0位
                        Thread.sleep(3000);
                    }

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //handler.sendEmptyMessage(MESSAGE_S1_OFF);
                statusS3 = false;
                Log.d(TAG, "run: 加高锰酸钾线程结束");

            }
        }).start();
    }

    //S4加草酸钠流程，双注射泵
    public static void s4_JiaCaoSuanNa() {

        new Thread(new Runnable() {

            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                Log.d(TAG, "run: 加草酸钠线程开始");
                //handler.sendEmptyMessage(MESSAGE_S1_ON);
                statusS4 = true;
                try {
                    SysGpio.mGpioOutP3.setValue(true);
                    Thread.sleep(3000);
                    //Log.d(TAG, "run: P3电源状态" + SysGpio.mGpioOutP3.getValue());
                    //Log.d(TAG, "run: 发送串口启动泵3指令" );

                    //注射泵状态查询
                    pumpStatus(3, 1000);

                    //注射泵3状态正常时执行
                    if(SysData.Pump[3] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(3, "pull", 12800);     //抽取草酸钠试剂
                        //Log.d(TAG, "run: 泵3抽取液体" );
                        Thread.sleep(20000);
                    }

                    //注射泵状态查询
                    pumpStatus(3, 1000);

                    if(SysData.Pump[3] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(3, "push", 6400);     //压出草酸钠试剂
                        //Log.d(TAG, "run: 泵3压出液体" );
                        Thread.sleep(10000);
                    }
                    SysGpio.mGpioOutD6.setValue(true);                                       //打开D6电磁阀
                    //Log.d(TAG, "run: 打开D6阀" );

                    //注射泵状态查询
                    pumpStatus(3, 1000);

                    if(SysData.Pump[3] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(3, "push", SysData.caosuannaStep);     //压出草酸钠试剂，进入反应器的草酸钠体积
                        //Log.d(TAG, "run: 泵3压出液体到反应器" );
                        Thread.sleep(10000);
                    }

                    //注射泵状态查询
                    pumpStatus(3, 1000);

                    if(SysData.Pump[3] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(3, "pull", 1600);     //注射泵吸气
                        //Log.d(TAG, "run: 泵3抽取气体" );
                        Thread.sleep(3000);
                    }
                    SysGpio.mGpioOutD6.setValue(false);                                       //关闭D2电磁阀

                    //注射泵状态查询
                    pumpStatus(3, 1000);

                    if(SysData.Pump[3] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(3, "back", 0);     //压出草酸钠试剂,返回0位
                        //Log.d(TAG, "run: 泵3返回起始位，排除剩余液体" );
                        Thread.sleep(3000);
                    }

                    //注射泵状态查询
                    pumpStatus(3, 1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //handler.sendEmptyMessage(MESSAGE_S1_OFF);
                statusS4 = false;
                Log.d(TAG, "run: 加草酸钠线程结束");

            }
        }).start();
    }
/*
    //S4加草酸钠流程，单注射泵
    public static void s4_JiaCaoSuanNa() {

        new Thread(new Runnable() {

            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                Log.d(TAG, "run: 加草酸钠线程开始");
                statusS4 = true;
                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
                    SysGpio.mGpioOutD2.setValue(true);
                    Log.d(TAG, "run: D2状态" + SysGpio.mGpioOutD2.getValue());

                    //首先排空试剂管中的试剂
                    SysGpio.mGpioOutD2.setValue(true);
                    Log.d(TAG, "run: D2状态" + SysGpio.mGpioOutD2.getValue());
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取气体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);     //注射泵吸气
                        Thread.sleep(22000);
                    }

                    SysGpio.mGpioOutD3.setValue(true);
                    Log.d(TAG, "run: D3状态" + SysGpio.mGpioOutD3.getValue());
                    SysGpio.mGpioOutD4.setValue(true);
                    Log.d(TAG, "run: D4状态" + SysGpio.mGpioOutD4.getValue());

                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出空气
                        MainActivity.com0.pumpCmd(2, "back", 0);     //排空容器中的草酸钠
                        Thread.sleep(22000);
                    }

                    //Log.d(TAG, "run: 发送串口启动进样泵指令" + num);
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12000);     //抽取草酸钠试剂
                        Thread.sleep(20000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出液体
                        MainActivity.com0.pumpCmd(2, "push", 2400);     //压出草酸钠试剂
                        Thread.sleep(4000);
                    }
                    Thread.sleep(1000);
                    SysGpio.mGpioOutD5.setValue(true);                                       //打开D5电磁阀
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "push", SysData.caosuannaStep);     //压出草酸钠试剂
                        Thread.sleep(14000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 2400);      //吸回管道剩余草酸钠试剂
                        Thread.sleep(5000);
                    }
                    SysGpio.mGpioOutD5.setValue(false);                                       //关闭D5电磁阀
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);          //压出草酸钠试剂,返回0位
                        Thread.sleep(10000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    SysGpio.mGpioOutD2.setValue(false);
                    Log.d(TAG, "run: D2状态" + SysGpio.mGpioOutD2.getValue());
                    SysGpio.mGpioOutD3.setValue(false);
                    Log.d(TAG, "run: D3状态" + SysGpio.mGpioOutD3.getValue());
                    SysGpio.mGpioOutD4.setValue(false);
                    Log.d(TAG, "run: D4状态" + SysGpio.mGpioOutD4.getValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //handler.sendEmptyMessage(MESSAGE_S1_OFF);
                statusS4 = false;
                Log.d(TAG, "run: 加草酸钠线程结束");
            }
        }).start();
    }
*/

    //S5消解程序
    public static void s5_XiaoJie() {
        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: 开始消解");
                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                //消解开始流程
                statusS5 = true;
                //设置消解开始时间和结束时间
                SysData.startXiaojie = System.currentTimeMillis(); //消解开始时间
                SysData.endXiaoJie = System.currentTimeMillis() + (SysData.xiaojieTime * 1000);  //消解结束时间
                //循环读取温度
                SysGpio.readTempFlag = true;
                SysGpio.readAd();
                //启动温度控制
                SysGpio.tempControlFlag = true;
                SysGpio.tempControl(SysData.xiaojieTemp);
                try {
                    while (System.currentTimeMillis() < SysData.endXiaoJie) {
                        //紧急停止
                        if(SysData.stopFlag) {
                            //停止读取温度
                            SysGpio.readTempFlag = false;
                            //停止温度控制
                            SysGpio.tempControlFlag = false;
                            //修改消解结束时间
                            SysData.endXiaoJie = System.currentTimeMillis();
                            //退出消解状态
                            statusS5 = false;
                            return;
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //停止读取温度
                SysGpio.readTempFlag = false;
                //停止温度控制
                SysGpio.tempControlFlag = false;
                //结束消解流程
                SysGpio.statusS5 = false;
                Log.d(TAG, "run: 消解结束");
            }
        }).start();
    }

    //S6滴定流程
    public static void s6_DiDing() {

        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: 开始滴定");
                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                //读取温度
                SysGpio.readTempFlag = true;
                SysGpio.readAd();
                Log.d(TAG, "run: 滴定线程开始");
                statusS6 = true;
                //int Difference = 10;  //滴定时模拟量下降的值大于这个差值判定为滴定终点

                //记录初始光电值，取10次采样的平均值
                int light = 0;
                int endAdLight = 0;   //判断滴定结束时光电平均值
                boolean isEnd = false;  //是否到达滴定终点
                for(int i = 0; i < 10; i++) {
                    //等待获取模拟量的值
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    light = light + SysData.adLight;
                    SysData.startAdLight = light / (i + 1);
                }

                //重置滴定量
                SysData.didingNum = 0;
                int endDidingNum = 0; //记录滴定终端时滴数
                int ddNum = 0;
                //停止持续读取温度和模拟量
                SysGpio.readTempFlag = false;

                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
                    //Log.d(TAG, "run: 发送串口启动进样泵指令" );

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);     //抽取高锰酸钾试剂
                        Thread.sleep(20000);
                    }

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出液体
                        MainActivity.com0.pumpCmd(2, "push", 3200);     //压出高锰酸钾试剂
                        Thread.sleep(6000);
                    }

                    SysGpio.mGpioOutD2.setValue(true);                                       //打开D2电磁阀
                    Thread.sleep(1000);

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出液体
                        MainActivity.com0.pumpCmd(2, "push", didingDeviation);     //压出高锰酸钾试剂,高锰酸钾液体到达滴定嘴
                        Thread.sleep(2000);
                    }

                    //光电值没有降低到原始值-10之前持续滴定
                    do {

                        //注射泵状态查询
                        pumpStatus(2, 200);

                        if(SysData.Pump[2] == 0x00) {
                            //注射泵滴定液体
                            MainActivity.com0.pumpCmd(2, "push", SysData.didingStep);     //压出高锰酸钾试剂，进入反应器的高锰酸钾体积
                            Thread.sleep(200);
                        }
                        ddNum += 1;
                        SysData.didingNum = ddNum;
                        //SysData.didingNum = (ddNum > SysData.didingDeviation) ? ddNum - SysData.didingDeviation : 0;      //从滴定开始到液体到达管口滴定的次数和滴定过量的滴数，空管滴数9滴，过量滴数3滴
                        if(ddNum >= 400){
                            SysData.errorMsg = "滴定超量";
                            SysData.errorId = 5;
                            SysData.saveAlertToDB();  //保存报警记录
                            //return;
                        }
                        //读取模拟量值
                        MainActivity.com0.getAd();
                        Thread.sleep(1600);
                        //如果光电值降低Difference/4以上，等待2S
                        if((SysData.startAdLight - SysData.adLight) >= (SysData.didingDifference / 4) ){
                            //读取温度
                            SysGpio.readTempFlag = true;
                            SysGpio.readAd();
                            Thread.sleep(3000);
                            if(endDidingNum == 0) {
                                endDidingNum = SysData.didingNum;
                            }

                            if((SysData.startAdLight - SysData.adLight) >= SysData.didingDifference ) {
                                Thread.sleep(30000);

                                if((SysData.startAdLight - SysData.adLight) >= SysData.didingDifference) {
                                    Log.d(TAG, "滴定终点光电值：" + SysData.adLight);
                                    isEnd = true;
                                    SysData.didingNum = endDidingNum;
                                } else {
                                    endDidingNum = 0;
                                }
                            }
                        } else {
                            endDidingNum = 0;
                        }
                        SysData.startAdLight = (SysData.adLight > SysData.startAdLight) ? SysData.adLight : SysData.startAdLight;  //初始光电值取最大值
                        SysGpio.readTempFlag = false;  //停止循环读取温度
                    } while (!isEnd && (SysData.startAdLight - SysData.adLight) < SysData.didingDifference && SysData.didingNum < SysData.didingMax);     //最多滴定didingMax滴

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 1600);     //注射泵吸气
                        Thread.sleep(3000);
                    }
                    SysGpio.mGpioOutD2.setValue(false);   //关闭D2电磁阀

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //压出高锰酸钾试剂,返回0位
                        Thread.sleep(10000);
                    }

                    //注射泵状态查询
                    pumpStatus(2, 1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                statusS6 = false;
                Log.d(TAG, "run: 滴定线程结束");

            }
        }).start();
    }

    //更新进度条
    public static void updateProgress() {
        new Thread(new Runnable() {
            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                do {

                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    if (SysData.progressRate < 95) {
                        SysData.progressRate = (int) ((System.currentTimeMillis() - SysData.startTime) / 1000 / 30);
                    }

                    //暂停30秒
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //时间超过90分钟，可能仪器故障
                    if(isRun && (System.currentTimeMillis() - SysData.startTime) / 1000 > 5400) {
                        SysData.errorMsg = "测定超时";
                        SysData.errorId = 6;
                        SysData.saveAlertToDB();  //保存报警记录
                        return;
                    }
                    //待机状态
                    if(!isRun) {
                        SysData.progressRate = 0;
                    }
                } while (isRun && SysData.progressRate < 100);
            }
        }).start();
    }

    //水质测定流程
    public static void s7_ShuiZhiCeDing() {
        SysData.workType = "水质分析";
        statusS7 = true;
        analysis();
    }

    //空白测定流程
    public static void s9_KongBaiShiYan() {
        SysData.workType = "标样测定";
        statusS9 = true;
        analysis();   //启动分析流程
    }

    //标样测定流程
    public static void s10_BiaoYangCeDing() {
        SysData.workType = "标样测定";
        statusS10 = true;
        analysis();   //启动分析流程
    }

    //仪表校准流程
    public static void s11_Calibration() {
        SysData.workType = "仪表校准";
        statusS11 = true;
        analysis();   //启动分析流程
    }

    //水质分析过程，可运行于水质分析、标样分析、仪表校准模式
    public static void analysis() {
        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: " + SysData.workType);
                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                //启动水质分析流程
                //statusS7 = true;
                SysData.progressRate = 1;
                SysData.statusMsg = "启动测定程序";
                SysData.isRun = true;
                SysData.startTime = System.currentTimeMillis();
                updateProgress();   //自动更新进度条
                //仪表校准需要1个小时左右
                if(SysData.workType.equals("仪表校准")){
                    SysData.endTime = System.currentTimeMillis() + 3600000;
                } else {
                    SysData.endTime = System.currentTimeMillis() + 3000000;
                }
                //SysData.codValue = 0;  //测定过程中显示前次数据
                SysData.didingNum = 0;

                //开启电源
                try {
                    SysGpio.mGpioOut24V.setValue(true);
                    SysGpio.mGpioOutLED.setValue(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //水质测定开电磁阀D1，标样测定、仪器校准关电磁阀D1
                if(SysData.workType.equals("水质分析")){
                    try {
                        SysGpio.mGpioOutD1.setValue(true); //打开阀1
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        SysGpio.mGpioOutD1.setValue(false); //关开阀1
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //等待2S
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //SysData.progressRate = 1;
                SysData.statusMsg = "进水样";

                //加水样流程
                s1_JiaShuiYang(); //加水样
                try {
                    SysGpio.mGpioOutH1.setValue(true); //开始加热
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //等待加水样完成

                //循环读取温度
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SysGpio.readTempFlag = true;
                SysGpio.readAd();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int beforeAd = SysData.adLight;    //加液前光电值
                int afterAd = SysData.adLight;     //加液后光电值

                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    try {
                        //时间超过5分钟，可能进样泵故障
                        if((System.currentTimeMillis() - SysData.startTime) / 1000 > 300) {
                            SysData.errorMsg = "加水样出错";
                            SysData.errorId = 1;
                            SysData.saveAlertToDB();  //保存报警记录
                            return;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //获取最大和最小光电值
                    beforeAd = SysData.adLight < beforeAd ? SysData.adLight : beforeAd;
                    afterAd = SysData.adLight > afterAd ? SysData.adLight : afterAd;

                } while(statusS1 == true);

                //判断是否有水样进入
                if((afterAd - beforeAd) < 30) {
                    SysData.errorMsg = "加水样出错";
                    SysData.errorId = 1;
                    SysData.saveAlertToDB();  //保存报警记录
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s8_Reset();
                    return;
                }
                Log.d(TAG, "水质测定:Max=" + afterAd + ",水质测定:Min=" + beforeAd);

                //测水样关闭电磁阀1
                if(SysData.workType.equals("水质分析")){
                    try {
                        SysGpio.mGpioOutD1.setValue(false); //关闭阀1
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //停止读取模拟量
                SysGpio.readTempFlag = false;
                //SysData.progressRate = 5;

                SysData.statusMsg = "加入硫酸";

                //加硫酸流程
                s2_JiaLiuSuan();
                //等待加硫酸完成
                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    try {
                        //时间超过10分钟，可能试剂泵故障
                        if((System.currentTimeMillis() - SysData.startTime) / 1000 > 600) {
                            SysData.errorMsg = "加硫酸出错";
                            SysData.errorId = 2;
                            SysData.saveAlertToDB();  //保存报警记录
                            return;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(statusS2 == true);

                //SysData.progressRate = 8;
                SysData.statusMsg = "反应器加热";

                //循环读取温度
                SysGpio.readTempFlag = true;
                SysGpio.readAd();
                //启动温度控制
                SysGpio.tempControlFlag = true;
                SysGpio.tempControl(SysData.xiaojieTemp);
                //开启搅拌程序
                SysData.jiaoBanType = 2;
                SysGpio.jiaoBanControl();
                //等待温度到达消解温度
                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    try {
                        //时间超过30分钟，可能加热器故障
                        if((System.currentTimeMillis() - SysData.startTime) / 1000 > 1800) {
                            SysData.errorMsg = "温度异常";
                            SysData.errorId = 7;
                            SysData.saveAlertToDB();  //保存报警记录
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //停止温度控制
                            SysGpio.tempControlFlag = false;
                            //开启搅拌程序
                            SysData.jiaoBanType = -1;
                            //启动复位程序
                            s8_Reset();
                            return;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(SysData.tempIn < SysData.xiaojieTemp);

                //记录加高锰酸钾前光电值
                beforeAd = SysData.adLight;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //停止读取温度
                SysGpio.readTempFlag = false;
                //停止温度控制
                SysGpio.tempControlFlag = false;

                //SysData.progressRate = 8;
                SysData.statusMsg = "加入高锰酸钾";

                //加高锰酸钾流程
                s3_JiaGaoMengSuanJIa();
                //等待加高锰酸钾完成
                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(statusS3 == true);

                //循环读取温度
                SysGpio.readTempFlag = true;
                SysGpio.readAd();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //记录加高锰酸钾后光电值
                afterAd = SysData.adLight;
                //判断加高锰酸钾是否正常
                if((beforeAd - afterAd) < 50) {
                    SysData.errorMsg = "加高锰酸钾出错";
                    SysData.errorId = 3;
                    SysData.saveAlertToDB();  //保存报警记录
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //s8_Reset();
                    //return;
                }
                Log.d(TAG, "加高锰酸钾前：" + beforeAd + ",加高锰酸钾后：" + afterAd);

                //间隔搅拌
                SysData.jiaoBanType = 1;

                //SysData.progressRate = 10;
                SysData.statusMsg = "正在消解";

                //启动消解程序
                s5_XiaoJie();
                //等待消解完成
                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(statusS5 == true);

                //记录加草酸钠前光电值
                beforeAd = SysData.adLight;

                SysData.statusMsg = "加入草酸钠";
                //停止读取温度
                SysGpio.readTempFlag = false;
                //停止温度控制
                SysGpio.tempControlFlag = false;
                //持续搅拌
                SysData.jiaoBanType = 2;

                //等待1秒钟
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //启动加草酸钠程序
                s4_JiaCaoSuanNa();
                //等待加草酸钠完成
                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(statusS4 == true);

                //循环读取温度
                SysGpio.readTempFlag = true;
                SysGpio.readAd();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //记录加草酸钠后光电值
                afterAd = SysData.adLight;
                //判断加草酸钠是否正常
                if((afterAd - beforeAd) < 50) {
                    SysData.errorMsg = "加草酸钠出错";
                    SysData.errorId = 4;
                    SysData.saveAlertToDB();  //保存报警记录
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //s8_Reset();
                    //return;
                }
                Log.d(TAG, "加草酸钠前：" + beforeAd + ",加草酸钠后：" + afterAd);

                SysData.statusMsg = "准备滴定";
                //启动温度控制
                SysGpio.tempControlFlag = true;
                SysGpio.tempControl(90);  //滴定前温度控制在90度

                //等待60S
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //启动温度控制
                SysGpio.tempControlFlag = true;
                SysGpio.tempControl(60);  //滴定时温度控制在60度
                //等待10S
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //停止读取温度
                SysGpio.readTempFlag = false;
                SysData.statusMsg = "正在滴定";

                //启动滴定程序
                s6_DiDing();
                //等待滴定完成
                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(statusS6 == true);

                //计算COD的值
                SysData.calculationValue();

                //等待1秒钟
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //将数据保存至数据库
                SysData.saveDataToDB();

                //等待1秒钟
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //停止读取温度
                SysGpio.readTempFlag = false;
                //停止温度控制
                SysGpio.tempControlFlag = false;

                //仪表校准，需要加入草酸钠，再滴定
                if(SysData.workType.equals("仪表校准")){
                    //启动排水2秒钟
                    try {
                        SysGpio.mGpioOutD8.setValue(true);
                        Thread.sleep(3000);
                        SysGpio.mGpioOutD8.setValue(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SysData.statusMsg = "加入草酸钠";
                    //停止读取温度
                    SysGpio.readTempFlag = false;
                    //停止温度控制
                    SysGpio.tempControlFlag = false;
                    //持续搅拌
                    SysData.jiaoBanType = 2;
                    //启动加草酸钠程序
                    s4_JiaCaoSuanNa();
                    //等待加草酸钠完成
                    do {
                        //紧急停止
                        if(SysData.stopFlag) {
                            return;
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while(statusS4 == true);

                    SysData.statusMsg = "准备滴定";
                    //启动温度控制
                    SysGpio.tempControlFlag = true;
                    SysGpio.tempControl(90);  //滴定前温度控制在90度

                    //等待60S
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //启动温度控制
                    SysGpio.tempControlFlag = true;
                    SysGpio.tempControl(60);  //滴定时温度控制在60度
                    //等待10S
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //停止读取温度
                    SysGpio.readTempFlag = false;
                    SysData.statusMsg = "正在滴定";

                    //启动滴定程序
                    s6_DiDing();
                    //等待滴定完成
                    do {
                        //紧急停止
                        if(SysData.stopFlag) {
                            return;
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while(statusS6 == true);
                    //计算标定值
                    SysData.calibrationValue();
                    //将校准数据保存至数据库
                    SysData.saveCalibrationDataToDB();
                    //停止读取温度
                    SysGpio.readTempFlag = false;
                    //停止温度控制
                    SysGpio.tempControlFlag = false;
                }

                //排放废液
                SysData.statusMsg = "排放废液";
                //启动排水
                try {
                    SysGpio.mGpioOutD8.setValue(true);
                    Thread.sleep(60000);
                    SysGpio.mGpioOutD8.setValue(false);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //终止搅拌程序
                SysData.jiaoBanType = -1;
                SysData.statusMsg = "完成分析流程";

                //等待2秒钟
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //关闭加热器、泵、关闭led灯和电源
                try {
                    SysGpio.mGpioOutH1.setValue(false);
                    SysGpio.mGpioOut24V.setValue(false);
                    SysGpio.mGpioOutLED.setValue(false);
                    SysGpio.mGpioOutP1.setValue(false);
                    SysGpio.mGpioOutP2.setValue(false);
                    SysGpio.mGpioOutP3.setValue(false);
                    SysGpio.mGpioOutD1.setValue(false);
                    SysGpio.mGpioOutD2.setValue(false);
                    SysGpio.mGpioOutD3.setValue(false);
                    SysGpio.mGpioOutD4.setValue(false);
                    SysGpio.mGpioOutD5.setValue(false);
                    SysGpio.mGpioOutD6.setValue(false);
                    SysGpio.mGpioOutD7.setValue(false);
                    SysGpio.mGpioOutD8.setValue(false);
                    SysGpio.mGpioOutDC1.setValue(false);
                    SysGpio.mGpioOutRE1.setValue(false);
                    SysGpio.mGpioOutDC2.setValue(false);
                    SysGpio.mGpioOutRE2.setValue(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SysData.statusMsg = "系统待机";
                SysData.progressRate = 100;
                SysData.isRun = false;
                SysData.endTime = System.currentTimeMillis();
                //测定时间大于1.5小时，则提示测定超时
                if((SysData.endTime - SysData.startTime) / 1000 > 5400) {
                    SysData.errorMsg = "测定超时";
                    SysData.errorId = 6;
                    SysData.saveAlertToDB();  //保存报警记录
                }
                //完成水质分析程序
                statusS7 = false;
                statusS9 = false;
                statusS10 = false;
                statusS11 = false;
                Log.d(TAG, "run: 结束分析流程");
                Log.d(TAG, "run: COD值：" + SysData.codValue);
            }
        }).start();
    }

    //仪器复位流程
    public static void s8_Reset() {
        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: 启动仪表复位");
                statusS8 = true;
                statusS7 = false;
                statusS9 = false;
                statusS10 = false;
                statusS11 = false;
                SysData.statusMsg = "正在复位";
                SysData.stopFlag = true;  //启动紧急停止
                try {
                    //等待60S
                    //Thread.sleep(60000);
                    SysGpio.mGpioOutH1.setValue(false);
                    SysGpio.mGpioOut24V.setValue(true);
                    SysGpio.mGpioOutLED.setValue(true);
                    SysGpio.mGpioOutP1.setValue(true);
                    SysGpio.mGpioOutP2.setValue(true);
                    SysGpio.mGpioOutP3.setValue(true);
                    SysGpio.mGpioOutD8.setValue(true);
                    SysGpio.mGpioOutD2.setValue(true);
                    SysGpio.mGpioOutD6.setValue(true);
                    Thread.sleep(2000);

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(2, "status", 0);
                        Thread.sleep(1000);
                    } while(SysData.Pump[2] != 0x00);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //注射泵2返回0位
                        Thread.sleep(1000);
                    }

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(2, "status", 0);
                        Thread.sleep(1000);
                    } while(SysData.Pump[2] != 0x00);

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(3, "status", 0);
                        Thread.sleep(1000);
                    } while(SysData.Pump[3] != 0x00);

                    if(SysData.Pump[3] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(3, "back", 0);     //注射泵3返回0位
                        Thread.sleep(1000);
                    }

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(3, "status", 0);
                        Thread.sleep(1000);
                    } while(SysData.Pump[3] != 0x00);

                    //等待30秒，排废液
                    Thread.sleep(30000);

                    //清洗反应器
                    SysGpio.mGpioOutD1.setValue(true);  //开启阀1准备加蒸馏水

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(1, "status", 0);
                        Thread.sleep(1000);
                    } while(SysData.Pump[1] != 0x00);

                    //注射泵1状态正常时执行
                    if(SysData.Pump[1] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(1, "turn", 65);
                        Thread.sleep(41000);
                    }

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(1, "status", 0);
                        Thread.sleep(1000);
                    } while(SysData.Pump[1] != 0x00);

                    //等待30秒，排清洗水
                    Thread.sleep(30000);
                    SysGpio.mGpioOutD1.setValue(false);
                    SysGpio.mGpioOutD2.setValue(false);
                    SysGpio.mGpioOutD6.setValue(false);
                    SysGpio.mGpioOutD8.setValue(false);
                    SysGpio.mGpioOutP1.setValue(false);
                    SysGpio.mGpioOutP2.setValue(false);
                    SysGpio.mGpioOutP3.setValue(false);
                    SysGpio.mGpioOutLED.setValue(false);
                    SysGpio.mGpioOut24V.setValue(false);
                    SysGpio.mGpioOutRE1.setValue(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SysData.statusMsg = "复位完成";
                SysData.stopFlag = false;
                statusS8 = false;
                SysData.statusMsg = "系统待机";
                SysData.progressRate = 0;
                SysData.isRun = false;
                Log.d(TAG, "run: 结束仪表复位");
            }
        }).start();

    }

    //紧急停止流程
    public static void s12_Stop() {
        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: 启动紧急停止");
                statusS12 = true;
                SysData.statusMsg = "紧急停止";
                SysData.stopFlag = true;  //启动紧急停止

                //停止所有输出
                try {
                    SysGpio.mGpioOutH1.setValue(false);
                    SysGpio.mGpioOut24V.setValue(false);
                    SysGpio.mGpioOutLED.setValue(false);
                    SysGpio.mGpioOutP1.setValue(false);
                    SysGpio.mGpioOutP2.setValue(false);
                    SysGpio.mGpioOutP3.setValue(false);
                    SysGpio.mGpioOutD1.setValue(false);
                    SysGpio.mGpioOutD2.setValue(false);
                    SysGpio.mGpioOutD3.setValue(false);
                    SysGpio.mGpioOutD4.setValue(false);
                    SysGpio.mGpioOutD5.setValue(false);
                    SysGpio.mGpioOutD6.setValue(false);
                    SysGpio.mGpioOutD7.setValue(false);
                    SysGpio.mGpioOutD8.setValue(false);
                    SysGpio.mGpioOutDC1.setValue(false);
                    SysGpio.mGpioOutRE1.setValue(false);
                    SysGpio.mGpioOutDC2.setValue(false);
                    SysGpio.mGpioOutRE2.setValue(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SysData.stopFlag = false; //紧急停止结束
                SysData.statusMsg = "系统待机";
                SysData.progressRate = 0;
                SysData.isRun = false;
                statusS12 = false;
                Log.d(TAG, "run: 结束紧急停止");
                //重启软件
                System.exit(0);
            }
        }).start();
    }

}