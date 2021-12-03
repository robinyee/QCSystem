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
    static boolean statusC1 = false;       //C1状态
    static boolean statusC2 = false;       //C2状态
    static boolean statusC3 = false;       //C3状态
    static boolean statusC4 = false;       //C4状态
    static boolean statusC5 = false;       //C5状态
    static boolean statusC6 = false;       //C6状态

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
    private static final String GPIO_OUT_D1 = "BCM4";   //D1
    private static final String GPIO_OUT_D2 = "BCM17";  //D2
    private static final String GPIO_OUT_D3 = "BCM18";  //D3
    private static final String GPIO_OUT_D4 = "BCM27";  //D4
    private static final String GPIO_OUT_D5 = "BCM22";  //D5
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

    //多通道阀状态
    public static void statusSwtch() {
        statusC1 = false;       //C1状态
        statusC2 = false;       //C2状态
        statusC3 = false;       //C3状态
        statusC4 = false;       //C4状态
        statusC5 = false;       //C5状态
        statusC6 = false;       //C6状态
        switch (SysData.reagentChannel) {
            case 1 : statusC1 = true;
            case 2 : statusC2 = true;
            case 3 : statusC3 = true;
            case 4 : statusC4 = true;
            case 5 : statusC5 = true;
            case 6 : statusC6 = true;
        }
    }

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

    //微量泵启动运行
    public static void microPumpRun(int step){
        //启动微量泵添加试剂
        while (step > 0 || SysData.microPumpOn) {
            try {
                SysGpio.mGpioOutP3.setValue(true);  //微量泵加液
                Thread.sleep(150);
                SysGpio.mGpioOutP3.setValue(true);  //微量泵复位
                Thread.sleep(350);
                step--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //清洗定容器
    public static void cleaning() {
        Log.d(TAG, "run: 清洗、润洗定容器");
        try {
            SysGpio.mGpioOutP1.setValue(true);  //开启水样泵电源
            Thread.sleep(3000);
            Log.d(TAG, "run: P1状态" + SysGpio.mGpioOutP1.getValue());
            Log.d(TAG, "run: 发送串口启动进样泵指令" );

            //清洗试剂管路
            addReagent(2, 20);

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
            SysGpio.mGpioOutDC1.setValue(true);   //开启蠕动泵排出液体
            Thread.sleep(30000);  //等待30秒，排空反应器液体
            SysGpio.mGpioOutDC1.setValue(false);   //关闭蠕动泵
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "run: 清洗、润洗定容器完成");
    }

    //进水样
    public static void inletWater(int waterStep) {
        Log.d(TAG, "run: 开始进水样");
        try {
            SysGpio.mGpioOutP1.setValue(true);  //开启水样泵电源
            Thread.sleep(3000);
            Log.d(TAG, "run: P1状态" + SysGpio.mGpioOutP1.getValue());
            Log.d(TAG, "run: 发送串口启动进样泵指令" );
            Log.d(TAG, "run: 进液步数：" + SysData.inletWaterStep);

            //注射泵状态查询
            pumpStatus(1, 1000);

            //注射泵1状态正常时执行
            if(SysData.Pump[1] == 0x00) {
                //注射泵抽取液体
                MainActivity.com0.pumpCmd(1, "turn", waterStep);
                Thread.sleep(60000);
            }
            //注射泵状态查询
            pumpStatus(1, 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "run: 进水样完成");
    }

    //添加试剂 reagentChannel-试剂通道，addReagentStep-添加试剂步数
    public static void addReagent(int reagentChannel, int addReagentStep) {
        Log.d(TAG, "run: 开始加试剂");
        try {
            SysGpio.mGpioOutP2.setValue(true);  //开启多通道阀电源
            Thread.sleep(3000);
            Log.d(TAG, "run: P2状态" + SysGpio.mGpioOutP2.getValue());
            Log.d(TAG, "run: 启动多通道阀" );

            //多通道阀状态查询
            pumpStatus(2, 1000);

            //多通道阀状态正常时执行
            if(SysData.Pump[2] == 0x00) {
                //切换多通道阀到指定通道
                SysData.reagentChannel = reagentChannel;
                MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                Thread.sleep(3000);
            }
            //注射泵状态查询
            pumpStatus(1, 1000);
            //启动微量泵添加试剂
            microPumpRun(addReagentStep);
            Thread.sleep(1000);
            //多通道阀状态查询
            pumpStatus(2, 1000);

            //多通道阀状态正常时执行
            if(SysData.Pump[2] == 0x00) {
                //切换多通道阀到指定通道
                SysData.reagentChannel = 1;
                MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                Thread.sleep(3000);
            }
            //注射泵状态查询
            pumpStatus(1, 1000);
            //启动微量泵添加试剂
            microPumpRun(20);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "run: 加试剂完成");
    }

    //混合样品、供样、排空样品、清洗容器
    public static void supplySamples() {
        try {
            Log.d(TAG, "run: 混合样品");
            SysGpio.mGpioOutD8.setValue(true);  //开启夹管阀电源
            Thread.sleep(1000);
            SysGpio.mGpioOutD5.setValue(true);  //打开夹管阀5
            Thread.sleep(3000);
            SysGpio.mGpioOutRE1.setValue(true);   //开启蠕动泵反转泵入空气
            Thread.sleep(120000);           //泵入空气混合时间2分钟
            SysGpio.mGpioOutRE1.setValue(false);   //停止蠕动泵
            SysGpio.mGpioOutD5.setValue(false);  //关闭夹管阀5
            Log.d(TAG, "run: 混合结束");

            Log.d(TAG, "run: 开始供样");
            SysGpio.mGpioOutD1.setValue(true);   //打开夹管阀1提供配制的标样
            SysData.startSupplySamples = true;   //开始供样标志
            Thread.sleep(600000);          //仪表抽取标样等待时间10分钟

            Log.d(TAG, "run: 结束供样排出废液");
            SysGpio.mGpioOutD1.setValue(false);   //关闭夹管阀1
            SysData.startSupplySamples = false;   //停止供样标志
            Thread.sleep(1000);          //等待1秒
            SysGpio.mGpioOutDC1.setValue(true);   //启动蠕动泵正转排出废液
            Thread.sleep(60000);          //等待1分钟
            SysGpio.mGpioOutDC1.setValue(false);   //停止蠕动泵

            Log.d(TAG, "run: 清洗容器");
            SysGpio.mGpioOutD4.setValue(true);   //开启夹管阀4
            cleaning();                          //启动清洗流程
            SysGpio.mGpioOutD4.setValue(false);   //关闭夹管阀4
            Thread.sleep(1000);
            SysGpio.mGpioOutD8.setValue(false);  //关闭夹管阀电源

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "run: 供样完成");
    }

    //启动标样配制流程
    // waterType类型：1-氨氮，2-总磷，3-总氮，4-COD；
    // sampleType标样种类：1-原水样，2-空白样，3-标样a，4-标样b，5-标样c，6-加标回收
    public static void preparationWaterSamples(final int waterType, final int sampleType) {
        SysData.waterType = waterType;
        SysData.startTime = System.currentTimeMillis();
        SysData.progressRate = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 开始配制标样");
                updateProgress();  //更新进度条
                isRun = true;
                //原水样
                if(sampleType == 1) {
                    return;
                }

                //空白样
                if(sampleType == 2) {
                    try {
                        SysGpio.mGpioOutD2.setValue(true);   //开启夹管阀2
                        SysData.startSupplySamples = true;   //开始供样标志
                        Thread.sleep(600000);          //仪表抽取标样等待时间10分钟
                        SysData.startSupplySamples = false;   //结束供样标志
                        SysGpio.mGpioOutD2.setValue(false);   //关闭夹管阀2
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //配制标样、加标回收
                if(sampleType >= 3) {
                    int waterStepNow = SysData.waterStep[waterType-1];
                    int sampleStepNow = SysData.sampleStep[waterType-1][sampleType-3];
                    int reagentChannelNow = waterType + 2;
                    Log.d(TAG, "run: waterStepNow=" + waterStepNow);
                    Log.d(TAG, "run: sampleStepNow=" + sampleStepNow);
                    try {
                        SysGpio.mGpioOutD8.setValue(true);   //开启夹管阀电源
                        Thread.sleep(3000);          //仪表抽取标样等待时间3秒
                        if(sampleType == 6) {
                            SysGpio.mGpioOutD3.setValue(true);   //开启夹管阀3
                            Thread.sleep(3000);          //仪表抽取标样等待时间3秒
                        }
                        cleaning(); //润洗管路
                        inletWater(waterStepNow); //进样
                        addReagent(reagentChannelNow, sampleStepNow); //加入母液
                        supplySamples(); //混合、供样、清洗
                        Thread.sleep(3000);
                        powerOff(); //关闭所有电源
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "run: 标样配制运行完成");
                }
                //进度条到达100
                SysData.progressRate = 100;
                isRun = false;
            }
        }).start();
    }
    //关闭所有电源
    public static void powerOff() {
        try {
            Log.d(TAG, "run: 关闭所有部件电源");
            Thread.sleep(3000);
            SysGpio.mGpioOutD1.setValue(false);
            SysGpio.mGpioOutD2.setValue(false);
            SysGpio.mGpioOutD3.setValue(false);
            SysGpio.mGpioOutD4.setValue(false);
            SysGpio.mGpioOutD5.setValue(false);
            SysGpio.mGpioOutD6.setValue(false);
            SysGpio.mGpioOutD7.setValue(false);
            SysGpio.mGpioOutP1.setValue(false);
            SysGpio.mGpioOutP2.setValue(false);
            SysGpio.mGpioOutP3.setValue(false);
            Thread.sleep(3000);
            SysGpio.mGpioOutD8.setValue(false);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //仪表初始化
    public static void initialize() {
        try {
            Log.d(TAG, "run: 仪器初始化");
            SysGpio.mGpioOutP1.setValue(true);
            SysGpio.mGpioOutP2.setValue(true);
            SysGpio.mGpioOutD8.setValue(true);
            Thread.sleep(3000);
            addReagent(3, 20); //加入母液
            addReagent(4, 20); //加入母液
            addReagent(5, 20); //加入母液
            addReagent(6, 20); //加入母液
            addReagent(2, 20); //加入母液
            addReagent(1, 20); //加入母液
            cleaning();//清洗容器
            SysGpio.mGpioOutD4.setValue(true);
            cleaning();//清洗容器
            powerOff();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //更新进度条
    public static void updateProgress() {
        new Thread(new Runnable() {
            public void run() {
                do {
                    if (SysData.progressRate < 95) {
                        SysData.progressRate = (int) ((System.currentTimeMillis() - SysData.startTime) / 1000 / 12);
                    }
                    //暂停30秒
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //时间超过90分钟，可能仪器故障
                    if(isRun && (System.currentTimeMillis() - SysData.startTime) / 1000 > 5400) {
                        SysData.errorMsg = "运行超时";
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

}