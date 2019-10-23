package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.things.device.TimeManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.instacart.library.truetime.TrueTime;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TimeManager timeManager = TimeManager.getInstance();
    public static UartCom com0, com1;
    public static WebServer webServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //clearPreferences();
        //读取系统参数
        readMeterParameter();
        //读取上次仪表状态
        readMeterStatus();
        //清除保存的数据
        //clearPreferences();

        //SysData.didingNum = 20;
        //SysData.calculationValue();
        //SysData.errorMsg = "加热超时";
        /*
        //启动后台服务
        Intent intent = new Intent(this, SysService.class);
        startService(intent);
        Log.i("MainActivity", "启动后台服务");

         */

        //加载Tab页面
        loadTabPager();

        //第一次运行获取网络时间，每天零时网络校时
        if (!SysData.isGetNetTime) {
            setSysTime();
        }

        //打开串口通讯
        com0 = new UartCom("UART0", 9600, 8, 1);// "UART0"为TLL串口
        com0.openUart();

        /*
        //打开串口通讯
        com1 = new UartCom("USB1-1.4:1.0", 9600, 8, 1);// "USB1-1.4:1.0"为U转串接口
        com1.openUart();
         */

        //打开并初始化Gpio端口
        SysGpio.gpioInit();

        //获取无线网络IP地址
        String ipText = getLocalIpStr(getApplicationContext());
        SysData.wifiIpAddr = ipText;

        //获取无线网络SSID
        String ssid = getWifiSsid(getApplicationContext());
        SysData.wifiSsid = ssid;

        /*
        //连接到无线网络 - 调试未通过
        boolean result = wifiConnection(getApplicationContext(), "HappyWiFi", "13621588977");
        Log.i("MainActivity", "WIFI连接：" + result);

         */

        //获取网络ip地址
        SysData.localIpAddr = getLocalIpAddress();
        //更新访问网址
        updateNet();

        //启动web服务
        startWebService();

        //前次正在测试中断电，自动复位
        if(SysData.isRun) {
            SysGpio.s8_Reset();
            SysData.isRun = false;
            SysData.progressRate = 0;
            //停止搅拌程序
            SysData.jiaoBanType = 0;
        }

        //启动循环进程定时保存仪表状态信息
        saveStatusRun();
    }


    //开启Web服务
    public static void startWebService() {
        //启动web服务
        webServer = new WebServer(SysData.webPort);
        try {
            webServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SysData.webServiceFlag = true;
        Log.i("MainActivity", "WEB服务启动：" + SysData.webServiceFlag);
    }

    //关闭Web服务
    public static void stopWebService() {
        //启动web服务
        webServer.stop();
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
        if(SysData.webIPAddr.equals("")) {
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
    public String[] getLocalIpAddress() {
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

    //连接wifi网络-调试不成功
    public static boolean wifiConnection(Context context, String wifiSSID, String password) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String strQuotationSSID = "\"" + wifiSSID + "\"";
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        if (wifiInfo != null && (wifiSSID.equals(wifiInfo.getSSID()) || strQuotationSSID.equals(wifiInfo.getSSID()))) {
            Log.i("MainActivity", "WIFI连接：1");
            return true;
        }
        wifi.startScan();
        List<ScanResult> scanResults = wifi.getScanResults();
        if (scanResults == null || scanResults.size() == 0) {
            Log.i("MainActivity", "WIFI连接：2");
            return false;
        }
        for (int nAllIndex = scanResults.size() - 1; nAllIndex >= 0; nAllIndex--) {
            String strScanSSID = ((ScanResult) scanResults.get(nAllIndex)).SSID;
            if (wifiSSID.equals(strScanSSID) || strQuotationSSID.equals(strScanSSID)) {
                WifiConfiguration config = new WifiConfiguration();
                config.SSID = strQuotationSSID;
                config.preSharedKey = "\"" + password + "\"";
                config.status = 3;
                Log.i("MainActivity", "WIFI连接：3");
                return wifi.enableNetwork(wifi.addNetwork(config), false);
            }
        }
        Log.i("MainActivity", "WIFI连接：4");
        return false;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysGpio.onClose();  //关闭Gpio输入输出
        com1.closeUart();   //关闭com1串口通信
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
                    } else {
                        isOK = isOK + 1;
                    }
                    try {
                        Thread.sleep(10000);  // 线程暂停10秒，单位毫秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isOK < max || SysData.isGetNetTime == false);

                timeManager.setTimeFormat(TimeManager.FORMAT_24); //设置24小时格式
                timeManager.setTimeZone("Asia/Shanghai"); //设置时区
                timeManager.setAutoTimeEnabled(true);
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

        //系统参数
        //SysData.localIpAddr[0] = sp.getString("localIpAddr", "");     //ip地址不需要存储
        SysData.webPort = sp.getInt("webPort", 0);
    }

    //保存仪表状态信息进程
    private void saveStatusRun() {
        Log.i("MainActivity", "保存仪表状态信息");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //定时保存仪表状态信息
                do {
                    saveMeterStatus();
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (true);
            }
        }).start();
    }

    //保存运行状态参数
    public void saveMeterStatus() {
        //打开文件
        final SharedPreferences.Editor editor = getSharedPreferences("Parameter", MODE_PRIVATE).edit();
        //仪器的参数
        editor.putBoolean("isRun", SysData.isRun);
        editor.putInt("progressRate", SysData.progressRate);
        editor.putString("statusMsg", SysData.statusMsg);
        editor.putString("errorMsg", SysData.errorMsg);
        editor.putLong("startTime", SysData.startTime);
        editor.putLong("endTime", SysData.endTime);
        editor.putLong("codVolue", Double.doubleToLongBits(SysData.codVolue));
        //提交保存
        editor.apply();
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
        SysData.codVolue = Double.longBitsToDouble(sp.getLong("codVolue", 0));
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
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMM HH:mm:ss");
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

}
