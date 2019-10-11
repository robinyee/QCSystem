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

public class SysGpio {
    static PeripheralManager manager;
    static Gpio mGpioOutD1, mGpioOutD2, mGpioOutD3, mGpioOutD4, mGpioOutD5, mGpioOutD6, mGpioOutD7, mGpioOutD8, mGpioOutP1,
            mGpioOutP2, mGpioOutH1, mGpioOutB1, mGpioOutLED, mGpioOut24V, mGpioOutDC1, mGpioOutRE1, mGpioOutDC2, mGpioOutRE2;
    static boolean readTempFlag = false; //是否持续读取温度

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

    //S1进水样流程
    public static void s1_JSY(final int num, final int time) {

        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: 进水样线程开始");
                //handler.sendEmptyMessage(MESSAGE_S1_ON);
                statusS1 = true;
                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
                    Log.d(TAG, "run: 发送串口启动进样泵指令" );
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);
                        Thread.sleep(20000);
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
                statusS1 = false;
                Log.d(TAG, "run: 进水样线程结束");
            }
        }).start();
    }

    //S2加硫酸流程
    public static void s2_JLS(final int num, final int time) {

        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: 加硫酸线程开始");
                //handler.sendEmptyMessage(MESSAGE_S1_ON);
                statusS1 = true;
                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
                    Log.d(TAG, "run: 发送串口启动进样泵指令" + num);
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "back", 0);
                        Thread.sleep(20000);
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
                statusS1 = false;
                Log.d(TAG, "run: 加硫酸线程结束");
            }
        }).start();
    }

    //S3加高锰酸钾流程
    public static void s3_JGMSJ(final int num, final int time) {

        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: 加高锰酸钾线程开始");
                //handler.sendEmptyMessage(MESSAGE_S1_ON);
                statusS1 = true;
                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
                    Log.d(TAG, "run: 发送串口启动进样泵指令" );
                    //注射泵状态查询
                    MainActivity.com0.pumpCmd(2, "status", 0);
                    Thread.sleep(1000);
                    //注射泵2状态正常时执行
                    if(SysData.Pump[2] == 0x00) {
                        //注射泵抽取液体
                        MainActivity.com0.pumpCmd(2, "pull", 12800);
                        Thread.sleep(20000);
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
                statusS1 = false;
                Log.d(TAG, "run: 加高锰酸钾线程结束");

            }
        }).start();
    }

    //水质分析流程 Demo
    public static void c1_SZFX() {
        new Thread(new Runnable() {

            public void run() {
                //handler.sendEmptyMessage(MESSAGE_S2_ON);
                statusS2 = true;
                try {
                    SysGpio.mGpioOutP2.setValue(true);
                    Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
                    //注射泵状态查询
                    byte bytes1[] = new byte[]{(byte) 0xCC, (byte) 0x02, (byte) 0x4A, (byte) 0x00, (byte) 0x00, (byte) 0xDD};
                    MainActivity.com0.sendBytes(bytes1, true, 2);
                    Thread.sleep(1000);
                    //注射泵复位
                    byte bytes2[] = new byte[]{(byte) 0xCC, (byte) 0x02, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0xDD};
                    MainActivity.com0.sendBytes(bytes2, true, 2);
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //handler.sendEmptyMessage(MESSAGE_S2_OFF);
                statusS2 = false;
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