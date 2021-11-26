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
    static String workTypeId = "";   //运行工作类型的代码

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

    //注射泵初始化

    public static void pumpStart() {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Log.d(TAG, "run: 注射泵初始化");
                    SysGpio.mGpioOutP2.setValue(true);
                    SysGpio.mGpioOutP3.setValue(true);
                    Thread.sleep(3000);
                    pumpStatus(2, 1000); //注射泵2状态查询
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵返回0位
                        MainActivity.com0.pumpCmd(2, "back", 0);     //注射泵2返回0位
                        Thread.sleep(1000);
                    }
                    pumpStatus(3, 1000); //注射泵3状态查询
                    if(SysData.Pump[3] == 0x00) {
                        //注射泵返回0位
                        MainActivity.com0.pumpCmd(3, "back", 0);     //注射泵3返回0位
                        Thread.sleep(1000);
                    }
                    Thread.sleep(30000);
                    SysGpio.mGpioOutP2.setValue(false);
                    SysGpio.mGpioOutP3.setValue(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}