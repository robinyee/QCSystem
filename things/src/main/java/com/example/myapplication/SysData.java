package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

public class SysData {

    //仪器的参数
    static int inletWaterStep = 65;             //水样泵旋转圈数
    static boolean startSupplySamples = false;  //是否开始供样
    static int waterType = 0;                   //水样的类型 0-氨氮 1-总磷 2-总氮 3-COD
    static int sampleType = 0;                  //标样的类型 0-原水 1-空白样 2-标样A 3-标样B 4-标样C 5-加标回收
    static boolean microPumpOn = false;         //启动微量泵
    static boolean microPumpTest = false;       //微量泵计量标定
    static int reagentChannel = 1;              //当前通道号
    static int addReagentStep = 20;             //添加试剂的步数
    static int pureWaterStep = 50;              //添加试剂的步数
    static int supplySamplesTime = 1;          //供样时长（分钟）
    static String arrWaterType[];               //水样类型数组
    static String arrSampleType[];              //标样浓度型号数组
    static String strWaterType = "通用";         //当前选择的水样类型
    static String strSampleType = "原水样";      //当前选择的水样类型
    static double concentration = 2.0;          //当前配制水样的浓度
    static double waterStepVolume = 1.8;        //水样泵每步的体积
    static double reagentStepVolume = 0.050;    //试剂泵每步的体积


    //氨氮
    static double NH3Volume = 150.0;            //氨氮标样体积mL
    static double NH3SampleA = 0.5;             //氨氮标样A浓度mg/L
    static double NH3SampleB = 1.0;             //氨氮标样B浓度mg/L
    static double NH3SampleC = 2.0;             //氨氮标样C浓度mg/L
    static double NH3SampleO = 100;             //氨氮标样母液浓度mg/L
    static double NH3AddMul = 0.5;              //氨氮加标倍数
    static double NH3AddValume = 1.0;           //氨氮加标量
    static int NH3AddType = 0;                  //氨氮加标类型
    static int NH3WaterStep = 150;              //氨氮标样步数
    static int NH3SampleAStep = 10;             //氨氮标样A添加步数
    static int NH3SampleBStep = 20;             //氨氮标样B添加步数
    static int NH3SampleCStep = 30;             //氨氮标样C添加步数
    static int NH3SampleDStep = 10;             //氨氮加标回收添加步数
    //总磷
    static double TPVolume = 150.0;             //总磷标样体积
    static double TPSampleA = 0.5;              //总磷标样A浓度
    static double TPSampleB = 1.0;              //总磷标样B浓度
    static double TPSampleC = 2.0;              //总磷标样C浓度
    static double TPSampleO = 100;              //总磷标样母液浓度
    static double TPAddMul = 0.5;               //总磷加标倍数
    static double TPAddValume = 1.0;            //总磷加标量
    static int TPAddType = 0;                   //总磷加标类型
    static int TPWaterStep = 150;               //总磷标样步数
    static int TPSampleAStep = 10;              //总磷标样A添加步数
    static int TPSampleBStep = 20;              //总磷标样B添加步数
    static int TPSampleCStep = 30;              //总磷标样C添加步数
    static int TPSampleDStep = 10;              //总磷加标回收添加步数
    //总氮
    static double TNVolume = 150.0;             //总氮标样体积
    static double TNSampleA = 0.5;              //总氮标样A浓度
    static double TNSampleB = 1.0;              //总氮标样B浓度
    static double TNSampleC = 2.0;              //总氮标样C浓度
    static double TNSampleO = 100;              //总氮标样母液浓度
    static double TNAddMul = 0.5;               //总氮加标倍数
    static double TNAddValume = 1.0;            //总氮加标量
    static int TNAddType = 0;                   //总氮加标类型
    static int TNWaterStep = 150;               //总氮标样步数
    static int TNSampleAStep = 10;              //总氮标样A添加步数
    static int TNSampleBStep = 20;              //总氮标样B添加步数
    static int TNSampleCStep = 30;              //总氮标样C添加步数
    static int TNSampleDStep = 10;              //总氮加标回收添加步数
    //COD
    static double CODVolume = 250.0;            //COD标样体积
    static double CODSampleA = 2.0;             //COD标样A浓度
    static double CODSampleB = 4.0;             //COD标样B浓度
    static double CODSampleC = 6.0;             //COD标样C浓度
    static double CODSampleO = 1000;            //COD标样母液浓度
    static double CODAddMul = 0.5;              //COD加标倍数
    static double CODAddValume = 2.0;           //COD加标量
    static int CODAddType = 0;                  //COD加标类型
    static int CODWaterStep = 150;              //COD标样步数
    static int CODSampleAStep = 10;             //COD标样A添加步数
    static int CODSampleBStep = 20;             //COD标样B添加步数
    static int CODSampleCStep = 30;             //COD标样C添加步数
    static int CODSampleDStep = 10;             //COD加标回收添加步数

    //生成数组
    static double[] waterVolumes = {NH3Volume,TPVolume,TNVolume,CODVolume};                         //水样体积数组 0-氨氮，1-总磷，2-总氮，3-COD
    static int[] waterStep = {NH3WaterStep,TPWaterStep,TNWaterStep,CODWaterStep};                   //水样步数数组 0-氨氮，1-总磷，2-总氮，3-COD
    static double[] reagentCon = {NH3SampleO,TPSampleO,TNSampleO,CODSampleO};     //母液浓度数组 0-氨氮，1-总磷，2-总氮，3-COD
    static double[][] sampleValue = {{NH3SampleA,NH3SampleB,NH3SampleC,NH3AddValume},     //氨氮标样浓度
                                    {TPSampleA,TPSampleB,TPSampleC,TPAddValume},          //总磷标样浓度
                                    {TNSampleA,TNSampleB,TNSampleC,TNAddValume},          //总氮标样浓度
                                    {CODSampleA,CODSampleB,CODSampleC,CODAddValume}};     //COD标样浓度

    static int[][] sampleStep = {{NH3SampleAStep,NH3SampleBStep,NH3SampleCStep,NH3SampleDStep},     //氨氮加母液步数数组
                                 {TPSampleAStep,TPSampleBStep,TPSampleCStep,TPSampleDStep},         //总磷加母液步数数组
                                 {TNSampleAStep,TNSampleBStep,TNSampleCStep,TNSampleDStep},         //总氮加母液步数数组
                                 {CODSampleAStep,CODSampleBStep,CODSampleCStep,CODSampleDStep}};    //COD加母液步数数组

    static double reagentVolume[][] = new double[4][4];            //所需母液的体积

    //仪器运行状态
    static boolean isGetNetTime = false;        //是否已经获取到网络时间
    static boolean isRun = false;               //仪器是否运行
    static int progressRate = 0;                //分析进度
    static String statusMsg = "仪器待机";        //仪器当前执行动作
    static double tempIn, tempOut;              //温度值，in反应器内温度，out加热器温度
    static int adLight, adBack;                 //adLight光电值，adBack备用模拟量
    static double smaAdLight;                   //adLight光电值的滑动平均值
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
    static boolean isSaveLog = false;            //是否保存运行日志

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
    static List<Task> tasks = null;                 //仪表测定结果数据
    //static List<Result> resultChart = null;       //仪表趋势线图测定结果数据
    static List<Record> records = null;             //仪表校准记录数据
    static List<AlertLog> alertLogs = null;         //仪表报警记录数据
    static String listDataType = "task";            //查询的数据类型，task,record,alert
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

    //计算水样步数，计算试剂步数
    public static void calculation(){
        calculationWaterStep();
        calculationReagentStep();
    }

    //计算水样步数
    public static void calculationWaterStep() {
        double volumes[] = {NH3Volume,TPVolume,TNVolume,CODVolume};                         //水样体积数组 0-氨氮，1-总磷，2-总氮，3-COD
        waterVolumes = volumes;
        if(waterStepVolume > 0) {
            for (int i = 0; i < waterVolumes.length; i++) {
                waterStep[i] = (int) Math.round((waterVolumes[i] - pureWaterStep * reagentStepVolume) / waterStepVolume);
            }
        }else {
            Log.i("参数设置", "设置错误：水样单步体积小于等于0");
        }
    }

    //计算母液步数
    public static void calculationReagentStep() {
        double volumes[] = {NH3Volume,TPVolume,TNVolume,CODVolume};  //水样体积数组 0-氨氮，1-总磷，2-总氮，3-COD
        double[] con = {NH3SampleO,TPSampleO,TNSampleO,CODSampleO};     //母液浓度数组 0-氨氮，1-总磷，2-总氮，3-COD
        double[][] value = {{NH3SampleA,NH3SampleB,NH3SampleC,NH3AddValume*(1+NH3AddMul*NH3AddType)},      //氨氮标样浓度
                            {TPSampleA,TPSampleB,TPSampleC,TPAddValume*(1+TPAddMul*TPAddType)},          //总磷标样浓度
                            {TNSampleA,TNSampleB,TNSampleC,TNAddValume*(1+TNAddMul*TNAddType)},          //总氮标样浓度
                            {CODSampleA,CODSampleB,CODSampleC,CODAddValume*(1+CODAddMul*CODAddType)}};     //COD标样浓度

        waterVolumes = volumes;
        reagentCon = con;
        sampleValue = value;
        if(reagentStepVolume > 0) {
            for (int i = 0; i < waterVolumes.length; i++) {
                for (int j=0; j< sampleValue[i].length; j++)
                {
                    //Log.i("参数设置", "reagentCon[i]=" + reagentCon[i] + " sampleValue[i][j]=" + sampleValue[i][j] + " waterVolumes[i]=" + waterVolumes[i]);
                    //Log.i("参数设置", "reagentVolume[i][j]=" + reagentVolume[i][j]);
                    reagentVolume[i][j] = calculationReagentVolume(reagentCon[i], sampleValue[i][j], waterVolumes[i]);
                    if(reagentVolume[i][j]>2){
                        Log.i("参数设置", "设置错误：加入母液的量大于2ml" + " i=" + i + " j=" + j + " 试剂量=" + reagentVolume[i][j]);
                    }
                    sampleStep[i][j] = (int) Math.round(reagentVolume[i][j] / reagentStepVolume);
                    Log.i("参数设置", "加试剂步数：" + " i=" + i + " j=" + j + " 试剂量=" + reagentVolume[i][j]);
                    Log.i("参数设置", "加试剂步数：" + " i=" + i + " j=" + j + " 步数=" + sampleStep[i][j]);
                }
            }
        }else {
            Log.i("参数设置", "设置错误：试剂单步量小于等于0");
        }
    }

    //计算所需母液的量mL o-母液浓度，x-目标浓度，v-水样体积，rv-所需试剂的体积
    public static double calculationReagentVolume(double o, double x, double v) {
        double rv = 0;
        rv = x/o*v;
        return rv;
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

    //保存配制标样记录至数据库
    public static void saveRecord() {
        Log.i("数据库", "添加质控数据记录");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Record record = new Record();
                record.dataType = strWaterType + strSampleType;
                record.dateTime = startTime;
                record.preValue = concentration;
                record.meaValue = 0.0;
                MainActivity.db.recordDao().insert(record);
            }
        }).start();
    }

    //从数据库读取配制记录
    public static void readData(final int num, final int start) {
        Log.i("数据库", "读取数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(listDataType.equals("task")) {
                    List<Task> rss;
                    countData = MainActivity.db.taskDao().findTaskCount();
                    maxPage = countData / numPerpage + 1;
                    rss = MainActivity.db.taskDao().getNum(num, start);
                    tasks = rss;
                }
                if(listDataType.equals("record")) {
                    List<Record> rss;
                    countData = MainActivity.db.recordDao().findRecordCount();
                    maxPage = countData / numPerpage + 1;
                    rss = MainActivity.db.recordDao().getNum(num, start);
                    records = rss;
                }
                if(listDataType.equals("alert")) {
                    List<AlertLog> rss;
                    countData = MainActivity.db.alertLogDao().findAlertLogCount();
                    maxPage = countData / numPerpage + 1;
                    rss = MainActivity.db.alertLogDao().getNum(num, start);
                    alertLogs = rss;
                }
            }
        }).start();
    }

/*
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
*/
}
