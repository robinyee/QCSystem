package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.things.device.TimeManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;
import com.instacart.library.truetime.TrueTime;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;



public class MainActivity extends AppCompatActivity {

    private static MainActivity mainApplication;
    public static TimeManager timeManager = TimeManager.getInstance();
    public static UartCom com0;
    public static OutCom com1;
    public static WebServer webServer;
    public static WebSockets webSockets;
    public static AppDatabase db;   //数据库
    public static PeripheralManager manager = PeripheralManager.getInstance();
    public static Ds3231 device;
    public static Typeface typefaceStHeiTi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appFont();  //设置字体
        initData();   //初始化异常捕获
        setContentView(R.layout.activity_main);
        SysData.version = getString(R.string.version);

        //加载Tab页面
        loadTabPager();

        //读取DS3231时间
        getDs3231Time();

        //第一次运行获取网络时间，每天零时网络校时
        if (!SysData.isGetNetTime) {
            setSysTime();
        }

        //打开数据库
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "db_cod").build();  //创建数据库
        SysData.readData(SysData.numPerpage, (SysData.currentPage - 1) * SysData.numPerpage);  //从数据库读取数据
        SysData.readChartData(30, 0);       //从数据库中读取30条数据
        //SysData.delDataFromCalibration(16);         //删除一条校准记录

        //clearPreferences();
        //读取系统参数
        readMeterParameter();
        //读取上次仪表状态
        readMeterStatus();
        //清除保存的数据
        //clearPreferences();

        /*
        //启动后台服务
        Intent intent = new Intent(this, SysService.class);
        startService(intent);
        Log.i("MainActivity", "启动后台服务");

         */


        //打开并初始化Gpio端口
        SysGpio.gpioInit();

        //获取无线网络IP地址
        String ipText = getLocalIpStr(getApplicationContext());
        SysData.wifiIpAddr = ipText;

        //获取无线网络SSID
        String ssid = getWifiSsid(getApplicationContext());
        SysData.wifiSsid = ssid;


        //获取网络ip地址
        SysData.localIpAddr = getLocalIpAddress();

        if (SysData.localIpAddr != null && SysData.localIpAddr.length >= 1) {
            //更新访问网址
            updateNet();
            //启动web服务
            startWebService();
        }

        //前次正在测试中断电，自动复位
        if (SysData.isRun) {
            SysGpio.s8_Reset();
            SysData.progressRate = 0;
        }

        //启动循环进程定时保存仪表状态信息
        saveStatusRun();

        //自动运行测定程序
        autoRun();

        Log.i("外部存储", "是否有权限写外部存储：" + isExternalStorageWritable());

        //试剂状态检测
        try {
            SysGpio.mGpioIn1.registerGpioCallback(gpioCallback);
            SysGpio.mGpioIn2.registerGpioCallback(gpioCallback);
            SysGpio.mGpioIn3.registerGpioCallback(gpioCallback);
            SysGpio.mGpioIn4.registerGpioCallback(gpioCallback);
            Log.i("输入端口", "已经启动侦听");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //打开出口通讯
        openCom();

    }

    //程序退出时关闭资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysGpio.gpioClose(); //关闭GPIO并注销
        com0.closeUart();   //关闭com0串口通信
        if (SysData.deviceList.size() >= 3) {
            com1.closeUart();   //关闭com1串口通信
        }
        db.close();  //关闭数据库连接
    }

    //软件使用字体
    private void appFont() {
        typefaceStHeiTi = Typeface.createFromAsset(getAssets(), "fonts/NotoSansHans-Medium.ttf");

        try {
            Field field = Typeface.class.getDeclaredField("DEFAULT"); //monospace
            field.setAccessible(true);
            field.set(null, typefaceStHeiTi);
            Log.d(TAG, "设置字体成功");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    //获取Ds3231温度
    private void getDs3231Temp() {
        //Ds3231 device;
        try {
            if(device != null) {
                return;
            }
            Log.d(TAG, "开始读取DS3231温度");
            device = new Ds3231(BoardDefaults.getI2CPort());
            SysData.tempBox = device.readTemperature();
            Log.d(TAG, "Ds3231温度 = " + device.readTemperature());
            // Close the device.
            device.close();
            device = null;
            SysData.ds3231Error = 0;
        } catch (IOException e) {
            SysData.ds3231Error ++;
            //连续10次读取温度错误报警
            if(SysData.ds3231Error > 10){
                /*
                SysData.errorMsg = "访问系统时间出错";
                SysData.errorId = 11;

                 */
                Toast.makeText(getApplicationContext(),"读取时间芯片出错", Toast.LENGTH_LONG).show();
                Log.e(TAG, "访问Ds3231出错", e);
            }
            throw new RuntimeException(e);
        } finally {
            return;
        }
    }

    //获取Ds3231时间
    private void getDs3231Time() {
        Date sysDate, ds3231Date, startDate;
        //Ds3231 device;
        try {
            if(device != null) {
                return;
            }
            Log.d(TAG, "开始读取DS3231时间");
            device = new Ds3231(BoardDefaults.getI2CPort());
            Log.d(TAG, "isTimekeepingDataValid = " + device.isTimekeepingDataValid());
            Log.d(TAG, "isOscillatorEnabled = " + device.isOscillatorEnabled());

            //初始时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(2020, Calendar.JANUARY, 1);
            startDate = calendar.getTime();
            Log.d(TAG, "初始时间 = " + startDate.toString());

            //如果时间保存数据有效读取DS3231时间
            if(device.isTimekeepingDataValid()) {
                //DS3231时间
                ds3231Date = new Date(device.getTime().getTime());
            } else {
                ds3231Date = startDate;
            }
            Log.d(TAG, "Ds3231时间 = " + ds3231Date.getTime());

            //读取原系统时间
            sysDate = new Date(System.currentTimeMillis());
            Log.d(TAG, "原系统时间 = " + sysDate.toString());

            //设置系统时间
            if(ds3231Date.getTime() > sysDate.getTime()) {
                timeManager.setTime(ds3231Date.getTime());
            }

            //设置后的时间
            Log.d(TAG, "Ds3231时间 = " + ds3231Date.toString());
            Log.d(TAG, "Ds3231温度 = " + device.readTemperature());
            SysData.tempBox = device.readTemperature();
            sysDate = new Date(System.currentTimeMillis());
            Log.d(TAG, "新系统时间 = " + sysDate.toString());

            // Close the device.
            device.close();
            device = null;
            SysData.ds3231Error = 0;
        } catch (IOException e) {
            SysData.ds3231Error ++;
            //连续10次错误报警
            if(SysData.ds3231Error > 10){
                SysData.errorMsg = "访问系统时间出错";
                SysData.errorId = 11;
                Log.e(TAG, "访问Ds3231出错", e);
            }
            throw new RuntimeException(e);
        } finally {
            return;
        }
    }

    //设置Ds3231时间
    public static void setDs3231Time() {
        Date date;
        //Ds3231 device;
        try {
            if(device != null) {
                return;
            }
            Log.d(TAG, "开始设置DS3231时间");
            device = new Ds3231(BoardDefaults.getI2CPort());
            Log.d(TAG, "isTimekeepingDataValid = " + device.isTimekeepingDataValid());
            Log.d(TAG, "isOscillatorEnabled = " + device.isOscillatorEnabled());

            date = new Date(System.currentTimeMillis());
            device.setTime(date);
            Log.d(TAG, "系统时间 = " + date.toString());
            Log.d(TAG, "DS3231时间 = " + device.getTime().toString());
            Log.d(TAG, "DS3231温度 = " + device.readTemperature());
            SysData.tempBox = device.readTemperature();

            // Close the device.
            device.close();
            device = null;
            SysData.ds3231Error = 0;
        } catch (IOException e) {
            SysData.ds3231Error ++;
            //连续10次读取温度错误报警
            if(SysData.ds3231Error > 10){
                SysData.errorMsg = "访问系统时间出错";
                SysData.errorId = 11;
                Log.e(TAG, "访问Ds3231出错", e);
            }
            throw new RuntimeException(e);
        } finally {
            return;
        }

    }

    //打开串口通讯端口
    public static void openCom() {
        SysData.deviceList = manager.getUartDeviceList();
        if (SysData.deviceList.isEmpty()) {
            Log.i(TAG, "No UART port available on this device.");
        } else {
            if(SysData.deviceList.size() >= 2) {
                //打开串口0通讯
                com0 = new UartCom(SysData.deviceList.get(1), 9600, 8, 1);// 为TLL串口
                com0.openUart();
            }
            if(SysData.deviceList.size() >= 3) {
                //打开串口1通讯
                com1 = new OutCom(SysData.deviceList.get(2), SysData.BAUD_RATE, SysData.DATA_BITS, SysData.STOP_BITS);// 为U转串接口
                com1.openUart();
            }
        }
    }

    //当输入端口状态改变时调用，记录试剂状态
    private GpioCallback gpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            // Read the active low pin state
            try {
                SysData.liusuanStatus = SysGpio.mGpioIn1.getValue();
                SysData.gaomengsuanjiaStatus = SysGpio.mGpioIn2.getValue();
                SysData.caosuannaStatus = SysGpio.mGpioIn3.getValue();
                SysData.zhengliushuiStatus = SysGpio.mGpioIn4.getValue();
                Log.w(TAG, "输入端口状态" + " 1:" + SysGpio.mGpioIn1.getValue());
                Log.w(TAG, "输入端口状态" + " 2:" + SysGpio.mGpioIn2.getValue());
                Log.w(TAG, "输入端口状态" + " 3:" + SysGpio.mGpioIn3.getValue());
                Log.w(TAG, "输入端口状态" + " 4:" + SysGpio.mGpioIn4.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w(TAG, gpio + ": Error event " + error);
        }
    };

    //初始化异常捕获并处理
    private void initData() {
        mainApplication = this;

        //监视应用异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    //开启Web服务
    public static void startWebService() {
        if(SysData.localIpAddr != null && SysData.localIpAddr.length >= 1 && SysData.webPort > 0) {
            //启动web服务
            webServer = new WebServer(SysData.webPort, mainApplication);
            webSockets = new WebSockets(SysData.webPort + 1, mainApplication);
            try {
                webServer.start();
                webSockets.start();
            } catch (IOException e) {
                e.printStackTrace();
                SysData.webServiceFlag = false;
            }
            SysData.webServiceFlag = true;
            Log.i("MainActivity", "WEB服务已启动");
            Log.i("MainActivity", "WebHttp服务 http://" + SysData.webIPAddr + ":" + SysData.webPort);
            Log.i("MainActivity", "WebSocket服务 ws://" + SysData.webIPAddr + ":" + (SysData.webPort + 1));
        }
    }

    //关闭Web服务
    public static void stopWebService() {
        //启动web服务
        if(webServer.wasStarted()) {
            webServer.stop();
        }
        if(webSockets.wasStarted()) {
            webSockets.stop();
        }
        SysData.webServiceFlag = false;
        Log.i("MainActivity", "WEB服务停止：" + SysData.webServiceFlag);
    }

    //重启Web服务
    public void restartService() {
        //启动后台服务
        Intent intent = new Intent(MainActivity.this, SysService.class);
        Bundle bundle = new Bundle();
        stopService(intent);
        startService(intent);
        Log.i("MainActivity", "重新启动Web服务");
    }


    //更新网络地址
    public static void updateNet() {
        //更新访问网址
        for(int i = 0; i < SysData.localIpAddr.length; i++) {
            Log.i("MainActivity", "本机IP地址：" + SysData.localIpAddr[i]);
        }
        if(SysData.webIPAddr.equals("0.0.0.0")) {
            if(SysData.localIpAddr.length > 0) {
                SysData.webIPAddr = SysData.localIpAddr[0];
            }
        }
        String urlString = "http://" + SysData.webIPAddr;
        if(SysData.webPort != 80) {
            urlString = urlString + ":" + SysData.webPort;
        }
        SysData.httpAddr = urlString;
    }

    //获取网络ip地址
    public static String[] getLocalIpAddress() {
        try {
            String[] ipStr = new String[10];
            int i = 0;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        ipStr[i] = inetAddress.getHostAddress().toString();
                        i++;
                    }
                }
            }
            if(i > 0) {
                String[] ipStrFind = new String[i];
                for(int j = 0; j < i; j++) {
                    ipStrFind[j] = ipStr[j];
                }
                return ipStrFind;
            }
        } catch (SocketException ex) {

        }
        return null;
    }

    //获取WIFI SSID
    public static String getWifiSsid(Context context){
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);  //开启无线网络
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String wifiName = wifiInfo.getExtraInfo();

        if(wifiInfo != null && wifiName != null) {
            if (wifiName.startsWith("\"")) {
                wifiName = wifiName.substring(1, wifiName.length());
            }
            if (wifiName.endsWith("\"")) {
                wifiName = wifiName.substring(0, wifiName.length() - 1);
            }
            Log.i("MainActivity", "WIFI SSID：" + wifiName);
            return wifiName;
        }

        return "未知网络";
    }

    //获取IP地址
    public static String getLocalIpStr(Context context){
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        Log.i("MainActivity", "WIFI IP地址：" + intToIpAddr(wifiInfo.getIpAddress()));
        String wifiIp = "";
        if(wifiInfo.getIpAddress() != 0) {
            wifiIp = intToIpAddr(wifiInfo.getIpAddress());
        }
        return wifiIp;
    }
    //转换IP地址格式
    private static String intToIpAddr(int ip){
        return (ip & 0xFF)+"."
                + ((ip>>8)&0xFF) + "."
                + ((ip>>16)&0xFF) + "."
                + ((ip>>24)&0xFF);
    }

    private void loadTabPager() {
        //加载系统界面
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("首页"));
        tabLayout.addTab(tabLayout.newTab().setText("数据查询"));
        tabLayout.addTab(tabLayout.newTab().setText("仪表设置"));
        tabLayout.addTab(tabLayout.newTab().setText("系统维护"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    //系统时间同步为网络时间
    private void setSysTime() {
        Log.i("MainActivity", "准备获取网络时间");
        new Thread(new Runnable() {
            @Override
            public void run() {
                timeManager.setTimeFormat(TimeManager.FORMAT_24); //设置24小时格式
                timeManager.setTimeZone("Asia/Shanghai"); //设置时区
                timeManager.setAutoTimeEnabled(true);
                Date date = null; //new Date(System.currentTimeMillis());  //初始时间从系统获取
                int isOK = 0;
                int max = 10;
                do {
                    try {
                        TrueTime.build().initialize();
                        date = TrueTime.now();
                        Log.i("MainActivity", "获取网络时间成功");
                        Log.i("MainActivity", "网络时间：" + date.toString());
                        Log.i("MainActivity", "网络时间：" + date.getTime());
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                    if (date != null) {
                        timeManager.setTime(date.getTime());
                        isOK = max;
                        SysData.isGetNetTime = true;
                        setDs3231Time(); //设置DS3231时间
                    } else {
                        isOK = isOK + 1;
                    }
                    try {
                        Thread.sleep(10000);  // 线程暂停10秒，单位毫秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isOK < max || SysData.isGetNetTime == false);

            }
        }).start();

    }

    //设置日期对话框
    public void showDateDialog(){

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialogDate = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date newDate;
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        newDate = calendar.getTime();
                        //timeManager.setTime(newDate.getTime());
                        showTimeDialog();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialogDate.show();
    }

    //设置时间对话框
    public void showTimeDialog(){

        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialogTime = new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Date newDate;
                        calendar.set(Calendar.HOUR, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        newDate = calendar.getTime();
                        //timeManager.setTime(newDate.getTime());
                        //setSysTime();
                    }
                },
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                true);
        dialogTime.show();
    }

    //读取仪表参数
    public void readMeterParameter() {
        //打开文件
        final SharedPreferences sp = getSharedPreferences("Parameter", MODE_PRIVATE);
        //仪器的参数
        SysData.shuiyangStep = sp.getInt("shuiyangStep", 65);
        SysData.shuiyangVolume = Double.longBitsToDouble(sp.getLong("shuiyangVolume", 0));
        SysData.liusuanStep = sp.getInt("liusuanStep", 3200);
        SysData.liusuanVolume = Double.longBitsToDouble(sp.getLong("liusuanVolume", 0));
        SysData.caosuannaStep = sp.getInt("caosuannaStep", 5690);
        SysData.caosuannaVolume = Double.longBitsToDouble(sp.getLong("caosuannaVolume", 0));
        SysData.gaomengsuanjiaStep = sp.getInt("gaomengsuanjiaStep", 5200);
        SysData.gaomengsuanjiaVolume = Double.longBitsToDouble(sp.getLong("gaomengsuanjiaVolume", 0));
        SysData.xiaojieTemp = Double.longBitsToDouble(sp.getLong("xiaojieTemp", 0));
        SysData.xiaojieTime = sp.getInt("xiaojieTime", 1500);
        SysData.didingStep = sp.getInt("didingStep", 50);
        SysData.didingVolume = Double.longBitsToDouble(sp.getLong("didingVolume", 0));
        SysData.didingNum = sp.getInt("didingNum", 0);
        SysData.didingSumVolume = Double.longBitsToDouble(sp.getLong("didingSumVolume", 0));
        SysData.kongbaiValue = Double.longBitsToDouble(sp.getLong("kongbaiValue", 0));
        SysData.biaodingValue = Double.longBitsToDouble(sp.getLong("biaodingValue", 0));
        SysData.caosuannaCon = Double.longBitsToDouble(sp.getLong("caosuannaCon", 0));
        SysData.didingDeviation = sp.getInt("didingDeviation", 720);
        SysData.didingDifference = sp.getInt("didingDifference", 20);
        SysData.slopeA = Double.longBitsToDouble(sp.getLong("slopeA", 1));
        SysData.interceptB = Double.longBitsToDouble(sp.getLong("interceptB", 0));
        SysData.trueValue = Double.longBitsToDouble(sp.getLong("trueValue", 0));
        //系统参数
        //SysData.localIpAddr[0] = sp.getString("localIpAddr", "");     //ip地址不需要存储
        SysData.webPort = sp.getInt("webPort", 0);
        SysData.isLoop = sp.getBoolean("isLoop", false);
        SysData.nextStartTime = sp.getLong("nextStartTime", 0);
        SysData.startCycle = sp.getInt("startCycle", 0);
        SysData.numberTimes = sp.getInt("numberTimes", 0);
        SysData.startType = sp.getInt("startType", 0);
        SysData.isNotice = sp.getBoolean("isNotice", false);
        SysData.isEmptyPipeline = sp.getBoolean("isEmptyPipeline", false);
        SysData.adminPassword = sp.getString("adminPassword", "nsy218");
        SysData.MODBUS_ADDR = sp.getInt("modbusAddr", 3);
        SysData.BAUD_RATE = sp.getInt("baudRate", 9600);
        //Log.i("读取参数", "试剂量报警信息" + SysData.isNotice);
    }

    //主程序循环进程，定时保存仪器状态
    private void saveStatusRun() {
        Log.i("MainActivity", "保存仪表状态信息");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int errorid = 0;
                do {
                    errorid = SysData.errorId;
                    //保存运行状态数据
                    if(SysData.isRun){
                        saveMeterStatus();  //仪器运行时定时保存仪器状态数据
                        if(SysData.isSaveLog) {
                            saveLog(); //保存运行日志
                        }
                        try {
                            Thread.sleep(10000);  //10秒后保存数据，能记录isRun的false状态
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        saveMeterStatus();  //仪器运行时定时保存仪器状态数据
                        try {
                            Thread.sleep(60000);  //60秒后保存数据，能记录isRun的false状态
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //获取主板温度
                    getDs3231Temp();  //DS3231芯片温度

                    //检查试剂状态
                    if(SysData.isNotice) {
                        if (SysData.liusuanStatus) {
                            SysData.errorMsg = "硫酸试剂量低";
                            SysData.errorId = 9;
                        }
                        if (SysData.gaomengsuanjiaStatus) {
                            SysData.errorMsg = "高锰酸钾试剂量低";
                            SysData.errorId = 9;
                        }
                        if (SysData.caosuannaStatus) {
                            SysData.errorMsg = "草酸钠试剂量低";
                            SysData.errorId = 9;
                        }
                        if (SysData.zhengliushuiStatus) {
                            SysData.errorMsg = "蒸馏水试剂量低";
                            SysData.errorId = 9;
                        }
                        if(errorid != SysData.errorId || errorid == 9) {
                            SysData.saveAlertToDB();  //保存报警记录
                        }
                    }
                    //加热器温度大于160度，反应器内温度高于110度，停止加热并报警
                    if(SysData.tempOut > 160 || SysData.tempIn > 110) {
                        try {
                            SysGpio.mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                            SysData.errorMsg = "反应器温度过高";
                            SysData.errorId = 7;
                            if(errorid != SysData.errorId || errorid == 7) {
                                SysData.saveAlertToDB();  //保存报警记录
                            }
                            SysGpio.s8_Reset();  //仪表复位，保护反应器
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //主板温度高于60度，停止加热并报警
                    if(SysData.tempBox >= 60) {
                        try {
                            SysGpio.mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: 停止加热");
                            SysData.errorMsg = "主板温度过高";
                            SysData.errorId = 10;
                            if(errorid != SysData.errorId || errorid == 10) {
                                SysData.saveAlertToDB();  //保存报警记录
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } while (true);
            }
        }).start();
    }

    //保存日志文件
    private void saveLog() {
        Log.i("MainActivity", "保存仪表运行日志");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
                String fileName = "log" + dateFormat.format(System.currentTimeMillis()) + ".txt";
                try {
                    FileOutputStream fos = openFileOutput(fileName, Context.MODE_APPEND);
                    fos.write((dateFormat2.format(System.currentTimeMillis()) + ","
                            + "codValue," + SysData.codValue + ","
                            + "progressRate," + SysData.progressRate + ","
                            + "statusMsg," + SysData.statusMsg + ","
                            + "tempIn," + SysData.tempIn + ","
                            + "tempOut," + SysData.tempOut + ","
                            + "adLight," + SysData.adLight + ","
                            + "startAdLight," + SysData.startAdLight + ","
                            + "smaAdLight," + SysData.smaAdLight + ","
                            + "didingNum," + SysData.didingNum + ","
                            + "didingSumVolume," + SysData.didingSumVolume + "\n").getBytes());
                    fos.close();
                } catch (
                        FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //主程序循环进程，定时启动测定程序
    private void autoRun() {
        Log.i("MainActivity", "启动自动运行线程");
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    //定时启动程序，运行次数为999以上时，仪器一直定时运行不会停止。
                    long dtime = System.currentTimeMillis() - SysData.nextStartTime;
                    //定时启动测定程序
                    if(SysData.isLoop && !SysData.isRun && dtime > 0 && dtime < 15000 && SysData.numberTimes > 0) {
                        //启动测定流程
                        startAction(SysData.startType);
                    }
                    //循环运行，周期值设为0，次数设为需要运行的次数
                    if(SysData.isLoop && !SysData.isRun && SysData.startCycle == 0 && SysData.numberTimes > 0) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //启动测定流程
                        startAction(SysData.startType);
                    }
                    //计算下次启动时间
                    if(SysData.nextStartTime < System.currentTimeMillis() && SysData.numberTimes > 0 && SysData.startCycle > 0 && SysData.isLoop) {
                        SysData.nextStartTime = SysData.nextStartTime + SysData.startCycle * 3600 * 1000;
                        Log.i("MainActivity", "当前时间：" + System.currentTimeMillis() + " 下次启动时间：" + SysData.nextStartTime);
                        SysData.numberTimes = (SysData.numberTimes >= 999) ? 999 : SysData.numberTimes - 1;
                        SysData.numberTimes = (SysData.numberTimes > 0) ? SysData.numberTimes : 0;
                        SysData.isUpdateAutoRun = true;
                    }
                    if( SysData.startCycle == 0 && SysData.numberTimes > 0 && SysData.isLoop && !SysData.isRun) {
                        SysData.nextStartTime = System.currentTimeMillis();
                        SysData.numberTimes = (SysData.numberTimes > 0) ? SysData.numberTimes - 1 : 0;
                        SysData.isUpdateAutoRun = true;
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (true);
            }
        }).start();
    }

    //启动不同类型的测定流程
    public void startAction(int actionType) {
        switch (actionType) {
            case 0:
                break;
            case 1:
                //启动水样测定流程
                SysGpio.s7_ShuiZhiCeDing();
                SysData.statusMsg = "启动水样测定程序";
                break;
            case 2:
                //启动标样测定流程
                SysGpio.s10_BiaoYangCeDing();
                SysData.statusMsg = "启动标样测定程序";
                break;
            case 3:
                //启动仪表校准流程
                SysGpio.s11_Calibration();
                SysData.statusMsg = "启动仪表校准程序";
                break;
        }
        if(actionType > 0) {
            SysData.isRun = true;
            SysData.workFrom = "定时启动";           //启动分析命令来自于 触摸屏、串口、Web、定时启动
        }
    }

    //保存运行状态参数
    public void saveMeterStatus() {
        //打开文件
        final SharedPreferences.Editor editor = getSharedPreferences("Parameter", MODE_PRIVATE).edit();
        //仪器的运行状态
        editor.putBoolean("isRun", SysData.isRun);
        editor.putInt("progressRate", SysData.progressRate);
        editor.putString("statusMsg", SysData.statusMsg);
        editor.putString("errorMsg", SysData.errorMsg);
        editor.putLong("startTime", SysData.startTime);
        editor.putLong("endTime", SysData.endTime);
        editor.putLong("codValue", Double.doubleToLongBits(SysData.codValue));
        //仪表的参数，web设置的参数
        editor.putBoolean("isLoop", SysData.isLoop);
        editor.putLong("nextStartTime", SysData.nextStartTime);
        editor.putInt("startCycle", SysData.startCycle);
        editor.putInt("numberTimes", SysData.numberTimes);
        editor.putInt("startType", SysData.startType);
        editor.putLong("xiaojieTemp", Double.doubleToLongBits(SysData.xiaojieTemp));
        editor.putInt("xiaojieTime", SysData.xiaojieTime);
        editor.putLong("biaodingValue", Double.doubleToLongBits(SysData.biaodingValue));
        editor.putLong("slopeA", Double.doubleToLongBits(SysData.slopeA));
        editor.putLong("interceptB", Double.doubleToLongBits(SysData.interceptB));
        editor.putLong("trueValue", Double.doubleToLongBits(SysData.trueValue));
        editor.putInt("BAUD_RATE", SysData.BAUD_RATE);
        editor.putInt("MODBUS_ADDR", SysData.MODBUS_ADDR);
        //提交保存
        editor.apply();
        Log.i("存储", "仪表状态已保存");
    }

    //读取仪表参数
    public void readMeterStatus() {
        //打开文件
        final SharedPreferences sp = getSharedPreferences("Parameter", MODE_PRIVATE);
        //仪器的参数
        SysData.isRun = sp.getBoolean("isRun", false);
        SysData.progressRate = sp.getInt("progressRate", 0);
        SysData.statusMsg = sp.getString("statusMsg", "");
        SysData.errorMsg = sp.getString("errorMsg", "");
        SysData.startTime = sp.getLong("startTime", 0);
        SysData.endTime = sp.getLong("endTime", 0);
        SysData.codValue = Double.longBitsToDouble(sp.getLong("codValue", 0));
    }

    //清空Preferences中的数据
    public void clearPreferences() {
        SharedPreferences preferences = getSharedPreferences("Parameter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    //保存运行日志
    public Boolean saveMeterLog(String msg) {
        if(!msg.equals("") && !msg.equals(null)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            String fileName = "log" + dateFormat.format(System.currentTimeMillis()) + ".txt";
            String line = timeFormat.format(System.currentTimeMillis()) + " " + msg;
            try {
                FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);
                fos.write(line.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /* 检测是否可以写外部文件 */
    public boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
