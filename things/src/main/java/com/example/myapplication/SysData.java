package com.example.myapplication;

public class SysData {

    //Home页面状态
    static boolean isGetNetTime = false;    //是否已经获取到网络时间
    static boolean isRun = false;           //仪器是否正在运行

    static double tempIn, tempOut;             //温度值，in反应器内温度，out加热器温度
    static int adLight, adBack;             //adLight光电值，adBack备用模拟量
    static byte[] Pump = new byte[10];      //记录各泵的状态，十六进制数据，0x00-状态正常，
                                            // 0x01-帧错误，0x02-参数错误，0x03-光耦错误，0x04-电机忙，0xfe-任务挂起，0xff-未知错误
    static int startAdLight;                //存储滴定前的光电值
    static int didingNum;                   //滴定的高锰酸钾的数量
    static String errorMsg;                 //记录仪器出错信息
    static double tempSet = 92;             //温度控制目标值
    static long startXiaojie;                //消解开始时间
    static long endXiaoJie;                  //消解结束时间
    static int timeXiaoJie = 1500;        //消解时长，单位：秒
    static int jiaoBanType = 0;             //搅拌方式 0-停止搅拌，1-间歇搅拌，2-持续搅拌
    static int progressRate = 0;            //分析进度

    //仪器控制页面状态
    static boolean statusD1 = false;       //D1状态
    static boolean statusD2 = false;       //D2状态
    static boolean statusD3 = false;       //D3状态
    static boolean statusD4 = false;       //D4状态
    static boolean statusD5 = false;       //D5状态
    static boolean statusD6 = false;       //D6状态
    static boolean statusD7 = false;       //D7状态
    static boolean statusD8 = false;       //D8状态
    static boolean statusD9 = false;       //D9状态
    static boolean statusD10 = false;      //D10状态
    static boolean statusD11 = false;      //D11状态
    static boolean statusD12 = false;      //D12状态
    static boolean statusD35 = false;      //D35状态
    static boolean statusD24EN = false;    //D24EN状态

}
