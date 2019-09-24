package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class SysGpio {
    static PeripheralManager manager;
    static Gpio mGpioOutD1, mGpioOutD2, mGpioOutD3, mGpioOutD4, mGpioOutD5, mGpioOutD6, mGpioOutD7, mGpioOutD8,
            mGpioOutD9, mGpioOutD10, mGpioOutD11, mGpioOutD12, mGpioOutLED, mGpioOut24V;

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
    private static final String GPIO_OUT_D9 = "BCM9";  //D9
    private static final String GPIO_OUT_D10 = "BCM25";  //D10
    private static final String GPIO_OUT_D11 = "BCM11";  //D11
    private static final String GPIO_OUT_D12 = "BCM8";  //D12
    private static final String GPIO_OUT_LED = "BCM21";  //LED 3.5V LED灯开关量输出
    private static final String GPIO_OUT_24V = "BCM6";  //24V 24V供电


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
            mGpioOutD9 = manager.openGpio(GPIO_OUT_D9);
            mGpioOutD10 = manager.openGpio(GPIO_OUT_D10);
            mGpioOutD11 = manager.openGpio(GPIO_OUT_D11);
            mGpioOutD12 = manager.openGpio(GPIO_OUT_D12);
            mGpioOutLED = manager.openGpio(GPIO_OUT_LED);
            mGpioOut24V = manager.openGpio(GPIO_OUT_24V);

            //初始化Gpio端口的状态
            mGpioOutD1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD4.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD5.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD6.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD7.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD8.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD9.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD10.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD11.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutD12.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOutLED.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量
            mGpioOut24V.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);  //初始化为低电平，高电平输出开关量

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

    //S1进水样流程
    public static void s1_JSY(final int num, final int time) {

        new Thread(new Runnable() {

            public void run() {
                Log.d(TAG, "run: 进水样线程开始");
                //handler.sendEmptyMessage(MESSAGE_S1_ON);
                statusS1 = true;
                try {
                    SysGpio.mGpioOutD3.setValue(true);
                    Log.d(TAG, "run: D3状态" + SysGpio.mGpioOutD3.getValue());
                    Log.d(TAG, "run: 发送串口启动进样泵指令" + num);
                    Thread.sleep(time);
                    SysGpio.mGpioOutD3.setValue(false);
                    Log.d(TAG, "run: D3状态" + SysGpio.mGpioOutD3.getValue());
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

    //水质分析流程 Demo
    public static void c1_SZFX() {
        new Thread(new Runnable() {

            public void run() {
                //handler.sendEmptyMessage(MESSAGE_S2_ON);
                statusS2 = true;
                try {
                    SysGpio.mGpioOutD9.setValue(true);
                    Log.d(TAG, "run: D9状态" + SysGpio.mGpioOutD9.getValue());
                    s1_JSY(240, 3000);  //运行s1进水样流程
                    Thread.sleep(3000);
                    Thread.sleep(1000);
                    s1_JSY(250, 3000);  //运行s1进水样流程
                    Thread.sleep(3000);
                    SysGpio.mGpioOutD9.setValue(false);
                    Log.d(TAG, "run: D9状态" + SysGpio.mGpioOutD9.getValue());
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
            mGpioOutD9.close();
            mGpioOutD9 = null;
            mGpioOutD10.close();
            mGpioOutD10 = null;
            mGpioOutD11.close();
            mGpioOutD11 = null;
            mGpioOutD12.close();
            mGpioOutD12 = null;
            mGpioOutLED.close();
            mGpioOutLED = null;
            mGpioOut24V.close();
            mGpioOut24V = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}