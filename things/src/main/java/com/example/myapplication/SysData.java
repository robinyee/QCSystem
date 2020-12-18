package com.example.myapplication;

import android.content.SharedPreferences;
import android.util.Log;

import java.security.Key;
import java.text.DecimalFormat;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

public class SysData {

    //仪器的参数
    static int shuiyangStep = 65;               //水样泵旋转圈数
    static double shuiyangVolume = 100.0;       //水样的液体体积
    static int liusuanStep = 3200;              //加硫酸的步数
    static double liusuanVolume = 5.0;          //加硫酸的体积
    static int caosuannaStep = 5200;            //加草酸钠的步数
    static double caosuannaVolume = 10.0;       //加草酸钠的体积
    static int gaomengsuanjiaStep = 5200;       //加高锰酸钾的步数
    static double gaomengsuanjiaVolume = 10.0;  //加高锰酸钾的体积
    static double xiaojieTemp = 92;             //消解温度
    static int xiaojieTime = 1500;              //消解时长
    static int didingStep = 50;                 //每滴滴定步数
    static double didingVolume = 0.1;           //每滴滴定体积
    static int didingNum = 0;                   //滴定的滴数
    static int didingMax = 400;                 //最大滴数,计量标定-100，默认值-400
    static int didingDifference = 20;           //滴定时模拟量下降的值大于这个差值判定为滴定终点
    static double didingSumVolume = 0;          //滴定的总体积
    static double kongbaiValue = 0.25;          //空白实验滴定高锰酸钾的量
    static double biaodingValue = 10.0;         //标定实验滴定高锰酸钾的量
    static double caosuannaCon = 0.01;          //草酸钠的浓度
    static double codValue = 0;                 //测定的cod值
    static int didingDeviation = 720;           //开始滴定到出液体需要的步数
    static double originalValue = 0;            //校准前的cod值
    static double newValue = 0;                 //校准后的cod值
    static double coefficient = 1.0;            //标定系数K值
    static double ccf = 1.0;                    //浓度修正因子，0.01浓度值为1.0，0.025浓度值为0.97


    //仪器运行状态
    static boolean isGetNetTime = false;        //是否已经获取到网络时间
    static boolean isRun = false;               //仪器是否运行
    static int progressRate = 0;                //分析进度
    static String statusMsg = "";               //仪器当前执行动作
    static double tempIn, tempOut;              //温度值，in反应器内温度，out加热器温度
    static int adLight, adBack;                 //adLight光电值，adBack备用模拟量
    static byte[] Pump = new byte[10];          //记录各泵的状态，十六进制数据，0x00-状态正常，0x01-帧错误，0x02-参数错误，0x03-光耦错误，0x04-电机忙，0xfe-任务挂起，0xff-未知错误
    static int startAdLight;                    //存储滴定前的光电值
    static String errorMsg = "";                //记录仪器出错信息
    static String[] errorMsgList = {"无报警", "加水样出错", "加硫酸出错", "加高锰酸钾出错", "加草酸钠出错", "滴定超量", "测定超时", "反应器温度过高", "注射泵故障", "试剂量低", "主板温度过高", "主板无法访问"};
    static int errorId = 0;                     //仪表出错代码 1-加水样出错，2-加硫酸出错，3-加高锰酸钾出错，4-加草酸钠出错，5-滴定超量，6-测定超时，7-反应器温度过高，8-注射泵故障， 9-试剂量低， 10-主板温度过高， 11-访问系统时间出错
    static long startXiaojie;                   //消解开始时间
    static long endXiaoJie;                     //消解结束时间
    static int jiaoBanType = 0;                 //搅拌方式 0-停止搅拌，1-间歇搅拌，2-持续搅拌
    static long startTime;                      //测定开始时间
    static long endTime;                        //测定结束时间
    static String workType = "水样分析";         //仪表工作类型 水样分析、标样测定、仪表校准、仪表复位
    static String workFrom = "未知";            //启动分析命令来自于 触摸屏、串口、Web、定时启动
    static double tempBox;                      //主板温度 DS3231芯片温度
    static int ds3231Error = 0;                 //访问芯片DS3231出错的次数

    //系统参数
    static String httpAddr = "";                //http访问地址
    static String wifiIpAddr = "";              //无线网络ip地址
    static String[] localIpAddr;                //可用网络ip地址
    static String webIPAddr = "0.0.0.0";        //web服务ip地址
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
    static int startType = 0;                   //定时启动的类型：0-空; 1-水质测定; 2-标样测定; 3-仪表校准
    static boolean isUpdateAutoRun = false;     //是否需要更新自动启动信息
    static boolean isUpdatnetwork = false;      //是否需要更新网络信息
    static String adminUsername = "admin";      //管理员用户名
    static String adminPassword = "nsy218";     //管理员密码
    static List<String> deviceList;             //串口通讯名称列表
    static int BAUD_RATE = 9600;                //外部串口通讯波特率
    static int DATA_BITS = 8;                   //外部串口通讯数据位
    static int STOP_BITS = 1;                   //外部串口通讯停止位
    static int MODBUS_ADDR = 3;                 //MODBUS地址位
    static boolean isUpdateCom1 = false;        //是否需要更新串口参数
    static int updateNum = 3;                   //界面更新次数
    static String version = "1.0";              //软件版本

    //数据查询结果
    static List<Result> results = null;             //仪表测定结果数据
    static List<Result> resultChart = null;         //仪表趋势线图测定结果数据
    static List<AlertLog> alertLogs = null;         //仪表报警记录数据
    static List<Calibration> calibrations = null;   //仪表校准记录数据
    static String listDataType = "codmn";           //查询的数据类型，codmn,alert,calibration
    //数据查询结果分页
    static int currentPage = 1;                     //当前浏览的页码
    static int countData = 0;                       //数据的总条数
    static int numPerpage = 7;                      //每页的数据条数
    static int maxPage = 1;                         //最大页数
    static long startDataTime = 0;                  //查询起始时间
    static long endDataTime = 0;                    //查询结束时间

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
    static boolean isEmptyPipeline = false;         //测定前是否清空取样管内的液体
    static boolean isNotice = false;                //试剂量低是否报警
    static boolean liusuanStatus;                   //硫酸试剂量，true-有试剂，false-无试剂
    static boolean gaomengsuanjiaStatus;            //高锰酸钾试剂量，true-有试剂，false-无试剂
    static boolean caosuannaStatus;                 //草酸钠试剂量，true-有试剂，false-无试剂
    static boolean zhengliushuiStatus;              //蒸馏水试剂量，true-有试剂，false-无试剂

    //计算COD的值
    public static double calculationValue() {
        if(caosuannaCon == 0.025) {
            ccf = 0.97;    //0.025浓度的试剂修正值
        } else {
            ccf = 1.00;
        }
        double k = caosuannaVolume / biaodingValue;
        didingSumVolume = didingNum * didingVolume;
        didingSumVolume = (double)Math.round(didingSumVolume*100)/100;  //取小数点后两位
        codValue = ((gaomengsuanjiaVolume + didingSumVolume) * k * ccf - caosuannaVolume) * caosuannaCon * 8 * 1000 / shuiyangVolume;
        codValue = (double)Math.round(codValue*100)/100;  //取小数点后两位
        //数据检查，异常数据修正
        if(codValue < 0) {
            codValue = 0;    //当测定值小于0时，返回0
        } else if (codValue > 25) {
            codValue = 25;    //当测定值大于25时，返回25
        }
        //返回COD值
        return codValue;
    }

    //计算校准后的新值
    public static double calibrationValue() {
        double orgDidingVolume = didingSumVolume;
        didingSumVolume = didingNum * didingVolume;
        didingSumVolume = (double)Math.round(didingSumVolume*100)/100;  //取小数点后两位
        biaodingValue = didingSumVolume;
        coefficient = caosuannaVolume / didingSumVolume;
        originalValue = ((gaomengsuanjiaVolume + orgDidingVolume) * 1 - caosuannaVolume) * caosuannaCon * 8 * 1000 / shuiyangVolume;
        originalValue = (double)Math.round(originalValue*100)/100;  //取小数点后两位
        newValue = ((gaomengsuanjiaVolume + orgDidingVolume) * coefficient - caosuannaVolume) * caosuannaCon * 8 * 1000 / shuiyangVolume;
        newValue = (double)Math.round(newValue*100)/100;  //取小数点后两位
        return newValue;
    }

    //保存测定值数据至数据库
    public static void saveDataToDB() {
        Log.i("数据库", "添加测定结果数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = new Result();
                result.dateTime = startTime;
                result.dataType = "COD";
                result.dataValue = codValue;
                MainActivity.db.resultDao().insert(result);
                MainActivity.db.calibrationDao().deleteById(16); //删除16号校准数据
            }
        }).start();
    }

    //保存校准数据至数据库
    public static void saveCalibrationDataToDB() {
        Log.i("数据库", "添加校准数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calibration calibration = new Calibration();
                calibration.dateTime = startTime;
                calibration.byValue = originalValue;
                calibration.csnValue = caosuannaVolume;
                calibration.gmsjValue = didingSumVolume;
                if(didingSumVolume != 0) {
                    coefficient = (double) Math.round(caosuannaVolume / didingSumVolume * 100) / 100;  //取小数点后两位
                    calibration.coefficient = coefficient;
                } else {
                    calibration.coefficient = 1.0;
                }
                calibration.newValue = newValue;
                MainActivity.db.calibrationDao().insert(calibration);
            }
        }).start();
    }

    //保存报警记录数据至数据库
    public static void saveAlertToDB() {
        Log.i("数据库", "添加报警信息数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlertLog alertLog = new AlertLog();
                alertLog.alertTime = System.currentTimeMillis();
                alertLog.errorId = errorId;
                alertLog.errorMsg = errorMsg;
                alertLog.resetFlag = 0;
                alertLog.resetTime = null;
                MainActivity.db.alertLogDao().insert(alertLog);
            }
        }).start();
    }

    //从数据库读取曲线数据
    public static void readChartData(final int num, final int start) {
        Log.i("数据库", "读取趋势线数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SysData.resultChart = MainActivity.db.resultDao().getNum(num, start);       //从数据库中读取30条数据
            }
        }).start();
    }

    //从数据库读取数据
    public static void readData(final int num, final int start) {
        Log.i("数据库", "读取数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(listDataType.equals("codmn")) {
                    List<Result> rss;
                    countData = MainActivity.db.resultDao().findResultCount();
                    maxPage = countData / numPerpage + 1;
                    rss = MainActivity.db.resultDao().getNum(num, start);
                    results = rss;
                }
                if(listDataType.equals("alert")) {
                    List<AlertLog> rss;
                    countData = MainActivity.db.alertLogDao().findAlertLogCount();
                    maxPage = countData / numPerpage + 1;
                    rss = MainActivity.db.alertLogDao().getNum(num, start);
                    alertLogs = rss;
                }
                if(listDataType.equals("calibration")) {
                    List<Calibration> rss;
                    countData = MainActivity.db.calibrationDao().findCalibrationCount();
                    maxPage = countData / numPerpage + 1;
                    rss = MainActivity.db.calibrationDao().getNum(num, start);
                    calibrations = rss;
                }
            }
        }).start();
    }

    //复位报警记录
    public static void resetAlert() {
        Log.i("数据库", "更新报警信息数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity.db.alertLogDao().updateByFlag(System.currentTimeMillis());
                Log.i("更新数据库", "resetTime:" + System.currentTimeMillis());
            }
        }).start();
    }

    //删除一条校准记录
    public static void delDataFromCalibration(final int id) {
        Log.i("数据库", "删除一条校准记录");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity.db.calibrationDao().deleteById(id); //删除16号校准数据
            }
        }).start();
    }

}
