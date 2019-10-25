package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

import static android.content.ContentValues.TAG;
import static com.example.myapplication.SysData.calculationValue;

public class SysGpio {
    static PeripheralManager manager;
    static Gpio mGpioOutD1, mGpioOutD2, mGpioOutD3, mGpioOutD4, mGpioOutD5, mGpioOutD6, mGpioOutD7, mGpioOutD8, mGpioOutP1,
            mGpioOutP2, mGpioOutH1, mGpioOutB1, mGpioOutLED, mGpioOut24V, mGpioOutDC1, mGpioOutRE1, mGpioOutDC2, mGpioOutRE2;
    static boolean readTempFlag = false; //是否持续读取温度
    static boolean tempControlFlag = false; //是否进行温度控制

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

    //设置输入输出引脚
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
    private static final String GPIO_OUT_H1 = "BCM11";  //H1
    private static final String GPIO_OUT_B1 = "BCM8";   //B1
    private static final String GPIO_OUT_LED = "BCM21";  //LED 3.5V LED灯开关量输出
    private static final String GPIO_OUT_24V = "BCM6";  //24V 24V供电
    private static final String GPIO_OUT_DC1 = "BCM7";  //DC1正转
    private static final String GPIO_OUT_RE1 = "BCM5";  //DC1反转
    private static final String GPIO_OUT_DC2 = "BCM12";  //DC2正转
    private static final String GPIO_OUT_RE2 = "BCM13";  //DC2反转


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
            mGpioOutH1 = manager.openGpio(GPIO_OUT_H1);
            mGpioOutB1 = manager.openGpio(GPIO_OUT_B1);
            mGpioOutLED = manager.openGpio(GPIO_OUT_LED);
            mGpioOut24V = manager.openGpio(GPIO_OUT_24V);
            mGpioOutDC1 = manager.openGpio(GPIO_OUT_DC1);
            mGpioOutRE1 = manager.openGpio(GPIO_OUT_RE1);
            mGpioOutDC2 = manager.openGpio(GPIO_OUT_DC2);
            mGpioOutRE2 = manager.openGpio(GPIO_OUT_RE2);

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
            mGpioOutH1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutB1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutLED.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOut24V.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutDC1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutRE1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutDC2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutRE2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //设置Gpio输出口的开关状态，端口名称，
    public static void setGpioOut(Gpio mGpioOut, boolean v) {
        try {
            mGpioOut.setValue(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        Thread.sleep(1000);
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
                            SysGpio.mGpioOutDC1.setValue(true);   //开始搅拌
                            Log.d(TAG, "run: 搅拌状态" + SysGpio.mGpioOutP2.getValue());
                            Thread.sleep(3000);
                            SysGpio.mGpioOutDC1.setValue(false);   //停止搅拌
                            Log.d(TAG, "run: 搅拌状态" + SysGpio.mGpioOutP2.getValue());
                            Thread.sleep(3000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(SysData.jiaoBanType == 2) {
                        try {
                            SysGpio.mGpioOutDC1.setValue(true);   //开始搅拌
                            Log.d(TAG, "run: 搅拌状态" + SysGpio.mGpioOutP2.getValue());
                            Thread.sleep(3000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(SysData.jiaoBanType == 0) {
                        try {
                            SysGpio.mGpioOutDC1.setValue(false);   //停止搅拌
                            Log.d(TAG, "run: 搅拌状态" + SysGpio.mGpioOutP2.getValue());
                            Thread.sleep(3000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    SysGpio.mGpioOutDC1.setValue(false);   //停止搅拌
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
                    if(SysData.tempIn < (temp)) {
                        try {
                            mGpioOutH1.setValue(true);
                            Log.d(TAG, "run: 开始加热");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //高于设置温度，关闭加热器
                    if(SysData.tempIn > (temp)) {
                        try {
                            mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //等待10S
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    /*
                    //温度高于60度，间断加热
                    if(SysData.tempIn > 60 && SysData.tempOut > 120) {
                        try {
                            mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //等待30S
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                     */

                    //温度高于85度，间断加热
                    if(SysData.tempIn > 85 && SysData.tempOut > 110) {
                        try {
                            mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //等待60S
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
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
                        //等待60S
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //加热器温度大于150度，停止加热
                    if(SysData.tempOut > 150 || SysData.tempIn > 95) {
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

    //S1进水样流程
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
                    SysGpio.mGpioOutP1.setValue(true);
                    Log.d(TAG, "run: P1状态" + SysGpio.mGpioOutP1.getValue());
                    Log.d(TAG, "run: 发送串口启动进样泵指令" );
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(1, "status", 0);
                    Thread.sleep(1000);
                    //注射泵1状态正常时执行
                    if(SysData.Pump[1] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(1, "turn", 65);
                        Thread.sleep(41000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(1, "status", 0);
                    Thread.sleep(1000);
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
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);     //注射泵吸气
                        Thread.sleep(22000);
                    }

                    SysGpio.mGpioOutD3.setValue(true);
                    Log.d(TAG, "run: D3状态" + SysGpio.mGpioOutD3.getValue());

                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //排空容器中的硫酸
                        Thread.sleep(22000);
                    }

                    //Log.d(TAG, "run: 发送串口启动进样泵指令" + num);
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 9600);     //抽取硫酸试剂
                        Thread.sleep(15000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出液体
                        MainActivity.com0.pumpCmd(2, "push", 2400);     //压出硫酸试剂
                        Thread.sleep(4000);
                    }
                    SysGpio.mGpioOutD6.setValue(true);                                       //打开D6电磁阀
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵压出液体
                        MainActivity.com0.pumpCmd(2, "push", SysData.liusuanStep);     //压出硫酸试剂
                        Thread.sleep(7000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 1200);     //吸回管道剩余硫酸试剂
                        Thread.sleep(3000);
                    }
                    SysGpio.mGpioOutD6.setValue(false);                                       //关闭D6电磁阀
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //压出硫酸试剂,返回0位
                        Thread.sleep(10000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
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
                    //Log.d(TAG, "run: 发送串口启动进样泵指令" );
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);     //抽取高锰酸钾试剂
                        Thread.sleep(20000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "push", 6400);     //压出高锰酸钾试剂
                        Thread.sleep(10000);
                    }
                    SysGpio.mGpioOutD2.setValue(true);                                       //打开D2电磁阀
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "push", SysData.gaomengsuanjiaStep);     //压出高锰酸钾试剂，进入反应器的高锰酸钾体积
                        Thread.sleep(10000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 1600);     //注射泵吸气
                        Thread.sleep(3000);
                    }
                    SysGpio.mGpioOutD2.setValue(false);                                       //关闭D2电磁阀
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //压出高锰酸钾试剂,返回0位
                        Thread.sleep(3000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
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

    //S4加草酸钠流程
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
                        //注射泵抽取液体
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
                        //注射泵抽取液体
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
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "push", 2400);     //压出草酸钠试剂
                        Thread.sleep(4000);
                    }
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


    //S5消解程序
    public static void s5_XiaoJie() {
        new Thread(new Runnable() {

            public void run() {

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
            }
        }).start();
    }

    //S6滴定流程
    public static void s6_DiDing() {

        new Thread(new Runnable() {

            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                //读取温度
                SysGpio.readTempFlag = true;
                SysGpio.readAd();
                Log.d(TAG, "run: 滴定线程开始");
                statusS6 = true;
                //等待获取模拟量的值
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //记录初始光电值
                SysData.startAdLight = SysData.adLight;
                //重置滴定量
                SysData.didingNum = 0;
                int ddNum = 0;
                //停止持续读取温度和模拟量
                SysGpio.readTempFlag = false;

                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
                    //Log.d(TAG, "run: 发送串口启动进样泵指令" );
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);     //抽取高锰酸钾试剂
                        Thread.sleep(20000);
                    }

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(2, "status", 0);
                        Thread.sleep(200);
                    } while(SysData.Pump[2] != 0x00);

                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "push", 4000);     //抽取高锰酸钾试剂
                        Thread.sleep(6000);
                    }

                    SysGpio.mGpioOutD2.setValue(true);                                       //打开D2电磁阀

                    //光电值没有降低到原始值-10之前持续滴定
                    do {

                        do {
                            //注射泵状态查询
                            MainActivity.com0.pumpCmd(2, "status", 0);
                            Thread.sleep(200);
                        } while(SysData.Pump[2] != 0x00);

                        if(SysData.Pump[2] == 0x00) {
                            //注射泵抽取液体
                            MainActivity.com0.pumpCmd(2, "push", SysData.didingStep);     //压出高锰酸钾试剂，进入反应器的高锰酸钾体积
                            Thread.sleep(200);
                        }
                        ddNum += 1;
                        SysData.didingNum = (ddNum > 4) ? ddNum - 4 : 0;
                        if(ddNum >= 150){
                            SysData.errorMsg = "COD值超量程";
                            //return;
                        }
                        //读取模拟量值
                        MainActivity.com0.getAd();
                        Thread.sleep(600);
                        //如果光电值降低10以上，等待30S
                        if((SysData.startAdLight - SysData.adLight) >= 5){
                            //读取温度
                            SysGpio.readTempFlag = true;
                            SysGpio.readAd();
                            Thread.sleep(30000);
                        }
                        SysGpio.readTempFlag = false;  //停止循环读取温度
                    } while ((SysData.startAdLight - SysData.adLight) < 5 && SysData.didingNum < 150);

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(2, "status", 0);
                        Thread.sleep(200);
                    } while(SysData.Pump[2] != 0x00);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 1600);     //注射泵吸气
                        Thread.sleep(3000);
                    }
                    SysGpio.mGpioOutD2.setValue(false);   //关闭D2电磁阀

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(2, "status", 0);
                        Thread.sleep(200);
                    } while(SysData.Pump[2] != 0x00);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //压出高锰酸钾试剂,返回0位
                        Thread.sleep(10000);
                    }

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(2, "status", 0);
                        Thread.sleep(200);
                    } while(SysData.Pump[2] != 0x00);

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
                    if((System.currentTimeMillis() - SysData.startTime) / 1000 > 5400) {
                        SysData.errorMsg = "仪器故障";
                        return;
                    }
                } while (SysData.progressRate < 100);
            }
        }).start();
    }

    //水质测定流程
    public static void s7_ShuiZhiCeDing() {
        new Thread(new Runnable() {

            public void run() {

                //紧急停止
                if(SysData.stopFlag) {
                    return;
                }

                //启动水质测定程序
                statusS7 = true;
                SysData.progressRate = 1;
                updateProgress();   //自动更新进度条
                SysData.statusMsg = "启动测定程序";
                SysData.isRun = true;
                SysData.startTime = System.currentTimeMillis();
                SysData.endTime = 0;
                //SysData.codVolue = 0;  //测定过程中显示前次数据
                SysData.didingNum = 0;

                //开启电源
                try {
                    SysGpio.mGpioOut24V.setValue(true);
                    SysGpio.mGpioOutLED.setValue(true);
                } catch (IOException e) {
                    e.printStackTrace();
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
                do {
                    //紧急停止
                    if(SysData.stopFlag) {
                        return;
                    }

                    try {
                        //时间超过5分钟，可能进样泵故障
                        if((System.currentTimeMillis() - SysData.startTime) / 1000 > 300) {
                            SysData.errorMsg = "进样泵故障";
                            return;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(statusS1 == true);

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
                            SysData.errorMsg = "试剂泵故障";
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
                            SysData.errorMsg = "加热器故障";
                            return;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while(SysData.tempIn < SysData.xiaojieTemp);

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

                SysData.statusMsg = "加入草酸钠";

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

                //循环读取温度
                SysGpio.readTempFlag = true;
                SysGpio.readAd();
                //启动温度控制
                SysGpio.tempControlFlag = true;
                SysGpio.tempControl(60);

                //等待60S
                try {
                    Thread.sleep(60000);
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

                //停止温度控制
                SysGpio.tempControlFlag = false;
                SysData.statusMsg = "排放废液";

                //启动排水
                try {
                    SysGpio.mGpioOutD8.setValue(true);
                    Thread.sleep(120000);
                    SysGpio.mGpioOutD8.setValue(false);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //终止搅拌程序
                SysData.jiaoBanType = -1;
                SysData.statusMsg = "完成水质测定";

                //等待2秒钟
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //关闭泵、关闭led灯和电源
                try {
                    SysGpio.mGpioOutP1.setValue(false);
                    SysGpio.mGpioOutP2.setValue(false);
                    SysGpio.mGpioOutLED.setValue(false);
                    SysGpio.mGpioOut24V.setValue(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SysData.statusMsg = "系统待机";
                SysData.progressRate = 100;
                SysData.isRun = false;
                SysData.endTime = System.currentTimeMillis();

                //将数据保存至数据库
                SysData.saveDataToDB();

                //完成水质测定程序
                statusS7 = false;
            }
        }).start();
    }

    //仪器复位流程
    public static void s8_Reset() {
        new Thread(new Runnable() {

            public void run() {
                statusS8 = true;
                statusS7 = false;
                SysData.isRun = false;
                SysData.statusMsg = "正在复位";
                SysData.stopFlag = true;  //启动紧急停止
                try {
                    //等待60S
                    //Thread.sleep(60000);
                    SysGpio.mGpioOutH1.setValue(false);
                    SysGpio.mGpioOut24V.setValue(true);
                    SysGpio.mGpioOutLED.setValue(true);
                    SysGpio.mGpioOutP2.setValue(true);
                    SysGpio.mGpioOutP1.setValue(true);
                    SysGpio.mGpioOutD8.setValue(true);
                    SysGpio.mGpioOutD2.setValue(true);
                    Thread.sleep(2000);

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(2, "status", 0);
                        Thread.sleep(200);
                    } while(SysData.Pump[2] != 0x00);

                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);     //注射泵返回0位
                        Thread.sleep(20000);
                    }

                    do {
                        //注射泵状态查询
                        MainActivity.com0.pumpCmd(2, "status", 0);
                        Thread.sleep(200);
                    } while(SysData.Pump[2] != 0x00);
                    //等待120秒，排废液
                    Thread.sleep(120000);

                    //清洗反应器
                    SysGpio.mGpioOutD1.setValue(true);  //开启阀1准备加蒸馏水
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(1, "status", 0);
                    Thread.sleep(1000);
                    //注射泵1状态正常时执行
                    if(SysData.Pump[1] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(1, "turn", 65);
                        Thread.sleep(41000);
                    }
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(1, "status", 0);
                    Thread.sleep(1000);
                    //等待120秒，排清洗水
                    Thread.sleep(120000);
                    SysGpio.mGpioOutD1.setValue(false);
                    SysGpio.mGpioOutD2.setValue(false);
                    SysGpio.mGpioOutD8.setValue(false);
                    SysGpio.mGpioOutP1.setValue(false);
                    SysGpio.mGpioOutP2.setValue(false);
                    SysGpio.mGpioOutLED.setValue(false);
                    SysGpio.mGpioOut24V.setValue(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SysData.statusMsg = "复位完成";
                SysData.stopFlag = false;
                statusS8 = false;
                SysData.statusMsg = "系统待机";
            }
        }).start();
    }

    //关闭端口
    public static void onClose() {
        try {
            mGpioOutD1.close();
            mGpioOutD1 = null;
            mGpioOutD2.close();
            mGpioOutD2 = null;
            mGpioOutD3.close();
            mGpioOutD3 = null;
            mGpioOutD4.close();
            mGpioOutD4 = null;
            mGpioOutD5.close();
            mGpioOutD5 = null;
            mGpioOutD6.close();
            mGpioOutD6 = null;
            mGpioOutD7.close();
            mGpioOutD7 = null;
            mGpioOutD8.close();
            mGpioOutD8 = null;
            mGpioOutP1.close();
            mGpioOutP1 = null;
            mGpioOutP2.close();
            mGpioOutP2 = null;
            mGpioOutH1.close();
            mGpioOutH1 = null;
            mGpioOutB1.close();
            mGpioOutB1 = null;
            mGpioOutDC1.close();
            mGpioOutDC1 = null;
            mGpioOutRE1.close();
            mGpioOutRE1 = null;
            mGpioOutDC2.close();
            mGpioOutDC2 = null;
            mGpioOutRE2.close();
            mGpioOutRE2 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}