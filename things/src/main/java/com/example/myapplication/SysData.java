package com.example.myapplication;

import android.content.SharedPreferences;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.List;

public class SysData {

    //仪器的参数
    static int shuiyangStep = 65;               //水样泵旋转圈数
    static double shuiyangVolume = 100.0;       //水样的液体体积
    static int liusuanStep = 3200;              //加硫酸的步数
    static double liusuanVolume = 5.0;          //加硫酸的体积
    static int caosuannaStep = 5690;            //加草酸钠的步数
    static double caosuannaVolume = 10.0;       //加草酸钠的体积
    static int gaomengsuanjiaStep = 5200;       //加高锰酸钾的步数
    static double gaomengsuanjiaVolume = 10.0;  //加高锰酸钾的体积
    static double xiaojieTemp = 92;             //消解温度
    static int xiaojieTime = 1500;              //消解时长
    static int didingStep = 50;                 //每滴滴定步数
    static double didingVolume = 0.1;           //每滴滴定体积
    static int didingNum = 0;                   //滴定的滴数
    static double didingSumVolume = 0;          //滴定的总体积
    static double kongbaiValue = 0.25;          //空白实验滴定高锰酸钾的量
    static double biaodingValue = 10.0;         //标定实验滴定高锰酸钾的量
    static double caosuannaCon = 0.01;          //草酸钠的浓度
    static double codVolue = 0;                 //测定的cod值
    static int didingDeviation = 12;            //滴定误差，开始滴定到出液体加上滴定过量的值

    //仪器运行状态
    static boolean isGetNetTime = false;        //是否已经获取到网络时间
    static boolean isRun = false;               //仪器是否运行
    static int progressRate = 0;                //分析进度
    static String statusMsg = "";               //仪器当前执行动作
    static double tempIn, tempOut;              //温度值，in反应器内温度，out加热器温度
    static int adLight, adBack;                 //adLight光电值，adBack备用模拟量
    static byte[] Pump = new byte[10];          //记录各泵的状态，十六进制数据，0x00-状态正常，0x01-帧错误，0x02-参数错误，0x03-光耦错误，0x04-电机忙，0xfe-任务挂起，0xff-未知错误
    static int startAdLight;                    //存储滴定前的光电值
    static String errorMsg;                     //记录仪器出错信息
    static long startXiaojie;                   //消解开始时间
    static long endXiaoJie;                     //消解结束时间
    static int jiaoBanType = 0;                 //搅拌方式 0-停止搅拌，1-间歇搅拌，2-持续搅拌
    static long startTime;                      //测定开始时间
    static long endTime;                        //测定结束时间

    //系统参数
    static String httpAddr = "";                //http访问地址
    static String wifiIpAddr = "";              //无线网络ip地址
    static String[] localIpAddr;                //可用网络ip地址
    static String webIPAddr = "";               //web服务ip地址
    static int webPort = 8080;                  //web服务端口
    static String wifiSsid = "";                //无线网络ssid
    static String wifiPass = "";                //无线网络密码
    static boolean restartWebFlag = false;      //是否需要重启web服务
    static boolean webServiceFlag = false;      //web服务是否启动
    static boolean stopFlag = false;            //紧急停止
    static boolean isLoop = false;              //是否循环运行
    static long nextStartTime = 0;              //下次启动时间
    static int startCycle = 1;                  //启动周期
    static int numberTimes = 0;                 //启动次数
    static boolean isUpdateTimes = false;       //是否需要更新自动启动信息
    static String adminPassword = "nsy218";     //管理员密码

    //仪器数据
    static List<Result> results = null;         //仪表测定结果数据
    static int currentPage = 1;                 //当前浏览的页码
    static int countData = 0;                   //数据的总条数
    static int numPerpage = 50;                 //每页的数据条数
    static int maxPage = 1;                     //最大页数
    static long startDataTime = 0;               //查询起始时间
    static long endDataTime = 0;                 //查询结束时间

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

    //仪器的试剂状态
    static boolean isNotice = false;          //试剂量低是否报警
    static boolean liusuanStatus;                   //硫酸试剂量，true-有试剂，false-无试剂
    static boolean gaomengsuanjiaStatus;            //高锰酸钾试剂量，true-有试剂，false-无试剂
    static boolean caosuannaStatus;                 //草酸钠试剂量，true-有试剂，false-无试剂
    static boolean zhengliushuiStatus;              //蒸馏水试剂量，true-有试剂，false-无试剂

    public static double calculationValue() {
        double k = 10.0 / biaodingValue;
        didingSumVolume = didingNum * didingVolume;
        didingSumVolume = (double)Math.round(didingSumVolume*100)/100;  //取小数点后两位
        codVolue = ((gaomengsuanjiaVolume + didingSumVolume) * k - caosuannaVolume) * caosuannaCon * 8 * 1000 /100;
        //codVolue = (didingSumVolume - kongbaiValue) * k * caosuannaCon * 16 * 1000 / shuiyangVolume;  //公式错误
        codVolue = (double)Math.round(codVolue*100)/100;  //取小数点后两位
        return codVolue;
    }

    //保存测定值数据至数据库
    public static void saveDataToDB() {
        Log.i("数据库", "添加数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = new Result();
                result.dateTime = startTime;
                result.dataType = "COD";
                result.dataValue = codVolue;
                MainActivity.db.resultDao().insert(result);
            }
        }).start();
    }

    //从数据库读取数据
    public static void readData(final int num, final int start) {
        Log.i("数据库", "读取数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Result> rss;
                //results = MainActivity.db.resultDao().getAll();
                countData = MainActivity.db.resultDao().findResultCount();
                maxPage = countData / numPerpage + 1;
                rss = MainActivity.db.resultDao().getNum(num, start);
                for(Result result:rss) {
                    //result = results.get(0);
                    Log.i("数据记录", "id:" + result.rid);
                    Log.i("数据记录", "time:" + result.dateTime);
                    Log.i("数据记录", "type:" + result.dataType);
                    Log.i("数据记录", "value:" + result.dataValue);
                }
                results = rss;
            }
        }).start();
    }


}
