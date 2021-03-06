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
    public static AppDatabase db;   //?????????
    public static PeripheralManager manager = PeripheralManager.getInstance();
    public static Ds3231 device;
    public static Typeface typefaceStHeiTi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appFont();  //????????????
        initData();   //?????????????????????
        setContentView(R.layout.activity_main);
        SysData.version = getString(R.string.version);

        //??????Tab??????
        loadTabPager();

        //??????DS3231??????
        getDs3231Time();

        //????????????????????????????????????????????????????????????
        if (!SysData.isGetNetTime) {
            setSysTime();
        }

        //???????????????
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "db_qc").build();  //???????????????
        //????????????
        SysData.currentPage = 1;
        SysData.readData(SysData.numPerpage, (SysData.currentPage-1)*SysData.numPerpage);  //????????????????????????
        //db.recordDao().deleteByTime(System.currentTimeMillis()); //??????????????????
        //SysData.readData(SysData.numPerpage, (SysData.currentPage - 1) * SysData.numPerpage);  //????????????????????????
        //SysData.readChartData(30, 0);       //?????????????????????30?????????
        //SysData.delDataFromCalibration(16);         //????????????????????????

        //?????????????????????????????????
        //clearPreferences();
        //??????????????????
        readMeterParameter();
        //????????????????????????
        //readMeterStatus();

        /*
        //??????????????????
        Intent intent = new Intent(this, SysService.class);
        startService(intent);
        Log.i("MainActivity", "??????????????????");

         */


        //??????????????????Gpio??????
        SysGpio.gpioInit();

        //??????????????????IP??????
        String ipText = getLocalIpStr(getApplicationContext());
        SysData.wifiIpAddr = ipText;

        //??????????????????SSID
        String ssid = getWifiSsid(getApplicationContext());
        SysData.wifiSsid = ssid;


        //????????????ip??????
        SysData.localIpAddr = getLocalIpAddress();

        if (SysData.localIpAddr != null && SysData.localIpAddr.length >= 1) {
            //??????????????????
            updateNet();
            //??????web??????
            startWebService();
        }

        //??????????????????????????????????????????
        if (SysData.isRun) {
            //SysGpio.s8_Reset();
            SysData.progressRate = 0;
        }

        //????????????????????????????????????????????????
        //saveStatusRun();

        //????????????????????????
        //autoRun();

        Log.i("????????????", "?????????????????????????????????" + isExternalStorageWritable());

        //??????????????????
        try {
            SysGpio.mGpioIn1.registerGpioCallback(gpioCallback);
            SysGpio.mGpioIn2.registerGpioCallback(gpioCallback);
            SysGpio.mGpioIn3.registerGpioCallback(gpioCallback);
            SysGpio.mGpioIn4.registerGpioCallback(gpioCallback);
            Log.i("????????????", "??????????????????");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //??????????????????
        //SysGpio.pumpStart();

        //??????????????????
        openCom();

    }

    //???????????????????????????
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysGpio.gpioClose(); //??????GPIO?????????
        com0.closeUart();   //??????com0????????????
        if (SysData.deviceList.size() >= 3) {
            com1.closeUart();   //??????com1????????????
        }
        db.close();  //?????????????????????
    }

    //??????????????????
    private void appFont() {
        typefaceStHeiTi = Typeface.createFromAsset(getAssets(), "fonts/NotoSansHans-Medium.ttf");

        try {
            Field field = Typeface.class.getDeclaredField("DEFAULT"); //monospace
            field.setAccessible(true);
            field.set(null, typefaceStHeiTi);
            Log.d(TAG, "??????????????????");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    //??????Ds3231??????
    private void getDs3231Temp() {
        //Ds3231 device;
        try {
            if(device != null) {
                return;
            }
            Log.d(TAG, "????????????DS3231??????");
            device = new Ds3231(BoardDefaults.getI2CPort());
            SysData.tempBox = device.readTemperature();
            Log.d(TAG, "Ds3231?????? = " + device.readTemperature());
            // Close the device.
            device.close();
            device = null;
            SysData.ds3231Error = 0;
        } catch (IOException e) {
            SysData.ds3231Error ++;
            //??????10???????????????????????????
            if(SysData.ds3231Error > 10){
                /*
                SysData.errorMsg = "????????????????????????";
                SysData.errorId = 11;

                 */
                Toast.makeText(getApplicationContext(),"????????????????????????", Toast.LENGTH_LONG).show();
                Log.e(TAG, "??????Ds3231??????", e);
            }
            throw new RuntimeException(e);
        } finally {
            return;
        }
    }

    //??????Ds3231??????
    private void getDs3231Time() {
        Date sysDate, ds3231Date, startDate;
        //Ds3231 device;
        try {
            if(device != null) {
                return;
            }
            Log.d(TAG, "????????????DS3231??????");
            device = new Ds3231(BoardDefaults.getI2CPort());
            Log.d(TAG, "isTimekeepingDataValid = " + device.isTimekeepingDataValid());
            Log.d(TAG, "isOscillatorEnabled = " + device.isOscillatorEnabled());

            //????????????
            Calendar calendar = Calendar.getInstance();
            calendar.set(2020, Calendar.JANUARY, 1);
            startDate = calendar.getTime();
            Log.d(TAG, "???????????? = " + startDate.toString());

            //????????????????????????????????????DS3231??????
            if(device.isTimekeepingDataValid()) {
                //DS3231??????
                ds3231Date = new Date(device.getTime().getTime());
            } else {
                ds3231Date = startDate;
            }
            Log.d(TAG, "Ds3231?????? = " + ds3231Date.getTime());

            //?????????????????????
            sysDate = new Date(System.currentTimeMillis());
            Log.d(TAG, "??????????????? = " + sysDate.toString());

            //??????????????????
            if(ds3231Date.getTime() > sysDate.getTime()) {
                timeManager.setTime(ds3231Date.getTime());
            }

            //??????????????????
            Log.d(TAG, "Ds3231?????? = " + ds3231Date.toString());
            Log.d(TAG, "Ds3231?????? = " + device.readTemperature());
            SysData.tempBox = device.readTemperature();
            sysDate = new Date(System.currentTimeMillis());
            Log.d(TAG, "??????????????? = " + sysDate.toString());

            // Close the device.
            device.close();
            device = null;
            SysData.ds3231Error = 0;
        } catch (IOException e) {
            SysData.ds3231Error ++;
            //??????10???????????????
            if(SysData.ds3231Error > 10){
                SysData.errorMsg = "????????????????????????";
                SysData.errorId = 11;
                Log.e(TAG, "??????Ds3231??????", e);
            }
            throw new RuntimeException(e);
        } finally {
            return;
        }
    }

    //??????Ds3231??????
    public static void setDs3231Time() {
        Date date;
        //Ds3231 device;
        try {
            if(device != null) {
                return;
            }
            Log.d(TAG, "????????????DS3231??????");
            device = new Ds3231(BoardDefaults.getI2CPort());
            Log.d(TAG, "isTimekeepingDataValid = " + device.isTimekeepingDataValid());
            Log.d(TAG, "isOscillatorEnabled = " + device.isOscillatorEnabled());

            date = new Date(System.currentTimeMillis());
            device.setTime(date);
            Log.d(TAG, "???????????? = " + date.toString());
            Log.d(TAG, "DS3231?????? = " + device.getTime().toString());
            Log.d(TAG, "DS3231?????? = " + device.readTemperature());
            SysData.tempBox = device.readTemperature();

            // Close the device.
            device.close();
            device = null;
            SysData.ds3231Error = 0;
        } catch (IOException e) {
            SysData.ds3231Error ++;
            //??????10???????????????????????????
            if(SysData.ds3231Error > 10){
                SysData.errorMsg = "????????????????????????";
                SysData.errorId = 11;
                Log.e(TAG, "??????Ds3231??????", e);
            }
            throw new RuntimeException(e);
        } finally {
            return;
        }

    }

    //????????????????????????
    public static void openCom() {
        SysData.deviceList = manager.getUartDeviceList();
        if (SysData.deviceList.isEmpty()) {
            Log.i(TAG, "No UART port available on this device.");
        } else {
            if(SysData.deviceList.size() >= 2) {
                //????????????0??????
                com0 = new UartCom(SysData.deviceList.get(1), 9600, 8, 1);// ???TLL??????
                com0.openUart();
            }
            if(SysData.deviceList.size() >= 3) {
                //????????????1??????
                com1 = new OutCom(SysData.deviceList.get(2), SysData.BAUD_RATE, SysData.DATA_BITS, SysData.STOP_BITS);// ???U????????????
                com1.openUart();
            }
        }
    }

    //?????????????????????????????????????????????????????????
    private GpioCallback gpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            // Read the active low pin state
            try {
                SysData.liusuanStatus = SysGpio.mGpioIn1.getValue();
                SysData.gaomengsuanjiaStatus = SysGpio.mGpioIn2.getValue();
                SysData.caosuannaStatus = SysGpio.mGpioIn3.getValue();
                SysData.zhengliushuiStatus = SysGpio.mGpioIn4.getValue();
                Log.w(TAG, "??????????????????" + " 1:" + SysGpio.mGpioIn1.getValue());
                Log.w(TAG, "??????????????????" + " 2:" + SysGpio.mGpioIn2.getValue());
                Log.w(TAG, "??????????????????" + " 3:" + SysGpio.mGpioIn3.getValue());
                Log.w(TAG, "??????????????????" + " 4:" + SysGpio.mGpioIn4.getValue());
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

    //??????????????????????????????
    private void initData() {
        mainApplication = this;

        //??????????????????
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    //??????Web??????
    public static void startWebService() {
        if(SysData.localIpAddr != null && SysData.localIpAddr.length >= 1 && SysData.webPort > 0) {
            //??????web??????
            webServer = new WebServer(SysData.webPort, mainApplication);
            webSockets = new WebSockets(SysData.webPort + 1, mainApplication);
            Log.i("MainActivity", "??????WEB??????...");
            try {
                webServer.start();
                webSockets.start();
            } catch (IOException e) {
                e.printStackTrace();
                SysData.webServiceFlag = false;
                //Log.i("MainActivity", "WEB????????????");
            }
            SysData.webServiceFlag = true;
            Log.i("MainActivity", "WEB???????????????");
            Log.i("MainActivity", "WebHttp?????? http://" + SysData.webIPAddr + ":" + SysData.webPort);
            Log.i("MainActivity", "WebSocket?????? ws://" + SysData.webIPAddr + ":" + (SysData.webPort + 1));
        }
    }

    //??????Web??????
    public static void stopWebService() {
        //??????web??????
        if(webServer.wasStarted()) {
            webServer.stop();
        }
        if(webSockets.wasStarted()) {
            webSockets.stop();
        }
        SysData.webServiceFlag = false;
        Log.i("MainActivity", "WEB???????????????" + SysData.webServiceFlag);
    }

    //??????Web??????
    public void restartService() {
        //??????????????????
        Intent intent = new Intent(MainActivity.this, SysService.class);
        Bundle bundle = new Bundle();
        stopService(intent);
        startService(intent);
        Log.i("MainActivity", "????????????Web??????");
    }


    //??????????????????
    public static void updateNet() {
        //??????????????????
        for(int i = 0; i < SysData.localIpAddr.length; i++) {
            Log.i("MainActivity", "??????IP?????????" + SysData.localIpAddr[i]);
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

    //????????????ip??????
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

    //??????WIFI SSID
    public static String getWifiSsid(Context context){
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);  //??????????????????
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
            Log.i("MainActivity", "WIFI SSID???" + wifiName);
            return wifiName;
        }

        return "????????????";
    }

    //??????IP??????
    public static String getLocalIpStr(Context context){
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        Log.i("MainActivity", "WIFI IP?????????" + intToIpAddr(wifiInfo.getIpAddress()));
        String wifiIp = "";
        if(wifiInfo.getIpAddress() != 0) {
            wifiIp = intToIpAddr(wifiInfo.getIpAddress());
        }
        return wifiIp;
    }
    //??????IP????????????
    private static String intToIpAddr(int ip){
        return (ip & 0xFF)+"."
                + ((ip>>8)&0xFF) + "."
                + ((ip>>16)&0xFF) + "."
                + ((ip>>24)&0xFF);
    }

    private void loadTabPager() {
        //??????????????????
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("??????"));
        tabLayout.addTab(tabLayout.newTab().setText("????????????"));
        tabLayout.addTab(tabLayout.newTab().setText("????????????"));
        tabLayout.addTab(tabLayout.newTab().setText("????????????"));
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

    //?????????????????????????????????
    private void setSysTime() {
        Log.i("MainActivity", "????????????????????????");
        new Thread(new Runnable() {
            @Override
            public void run() {
                timeManager.setTimeFormat(TimeManager.FORMAT_24); //??????24????????????
                timeManager.setTimeZone("Asia/Shanghai"); //????????????
                timeManager.setAutoTimeEnabled(true);
                Date date = null; //new Date(System.currentTimeMillis());  //???????????????????????????
                int isOK = 0;
                int max = 10;
                do {
                    try {
                        TrueTime.build().initialize();
                        date = TrueTime.now();
                        Log.i("MainActivity", "????????????????????????");
                        Log.i("MainActivity", "???????????????" + date.toString());
                        Log.i("MainActivity", "???????????????" + date.getTime());
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                    if (date != null) {
                        timeManager.setTime(date.getTime());
                        isOK = max;
                        SysData.isGetNetTime = true;
                        setDs3231Time(); //??????DS3231??????
                    } else {
                        isOK = isOK + 1;
                    }
                    try {
                        Thread.sleep(10000);  // ????????????10??????????????????
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isOK < max || SysData.isGetNetTime == false);

            }
        }).start();

    }

    //?????????????????????
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

    //?????????????????????
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

    //??????????????????
    public void readMeterParameter() {
        //????????????
        final SharedPreferences sp = getSharedPreferences("Parameter", MODE_PRIVATE);
        //???????????????
        //????????????
        SysData.waterStepVolume = Double.longBitsToDouble(sp.getLong("waterStepVolume", 0));
        SysData.reagentStepVolume = Double.longBitsToDouble(sp.getLong("reagentStepVolume", 0));
        SysData.supplySamplesTime = sp.getInt("supplySamplesTime", 10);

        //??????
        SysData.NH3Volume = Double.longBitsToDouble(sp.getLong("NH3Volume", 0));
        SysData.NH3SampleA = Double.longBitsToDouble(sp.getLong("NH3SampleA", 0));
        SysData.NH3SampleB = Double.longBitsToDouble(sp.getLong("NH3SampleB", 0));
        SysData.NH3SampleC = Double.longBitsToDouble(sp.getLong("NH3SampleC", 0));
        SysData.NH3SampleO = Double.longBitsToDouble(sp.getLong("NH3SampleO", 0));
        SysData.NH3AddMul = Double.longBitsToDouble(sp.getLong("NH3AddMul", 0));
        SysData.NH3AddValume = Double.longBitsToDouble(sp.getLong("NH3AddValume", 0));
        SysData.NH3AddType = sp.getInt("NH3AddType", 0);
        //??????
        SysData.TPVolume = Double.longBitsToDouble(sp.getLong("TPVolume", 0));
        SysData.TPSampleA = Double.longBitsToDouble(sp.getLong("TPSampleA", 0));
        SysData.TPSampleB = Double.longBitsToDouble(sp.getLong("TPSampleB", 0));
        SysData.TPSampleC = Double.longBitsToDouble(sp.getLong("TPSampleC", 0));
        SysData.TPSampleO = Double.longBitsToDouble(sp.getLong("TPSampleO", 0));
        SysData.TPAddMul = Double.longBitsToDouble(sp.getLong("TPAddMul", 0));
        SysData.TPAddValume = Double.longBitsToDouble(sp.getLong("TPAddValume", 0));
        SysData.TPAddType = sp.getInt("TPAddType", 0);
        //??????
        SysData.TNVolume = Double.longBitsToDouble(sp.getLong("TNVolume", 0));
        SysData.TNSampleA = Double.longBitsToDouble(sp.getLong("TNSampleA", 0));
        SysData.TNSampleB = Double.longBitsToDouble(sp.getLong("TNSampleB", 0));
        SysData.TNSampleC = Double.longBitsToDouble(sp.getLong("TNSampleC", 0));
        SysData.TNSampleO = Double.longBitsToDouble(sp.getLong("TNSampleO", 0));
        SysData.TNAddMul = Double.longBitsToDouble(sp.getLong("TNAddMul", 0));
        SysData.TNAddValume = Double.longBitsToDouble(sp.getLong("TNAddValume", 0));
        SysData.TNAddType = sp.getInt("TNAddType", 0);
        //COD
        SysData.CODVolume = Double.longBitsToDouble(sp.getLong("CODVolume", 0));
        SysData.CODSampleA = Double.longBitsToDouble(sp.getLong("CODSampleA", 0));
        SysData.CODSampleB = Double.longBitsToDouble(sp.getLong("CODSampleB", 0));
        SysData.CODSampleC = Double.longBitsToDouble(sp.getLong("CODSampleC", 0));
        SysData.CODSampleO = Double.longBitsToDouble(sp.getLong("CODSampleO", 0));
        SysData.CODAddMul = Double.longBitsToDouble(sp.getLong("CODAddMul", 0));
        SysData.CODAddValume = Double.longBitsToDouble(sp.getLong("CODAddValume", 0));
        SysData.CODAddType = sp.getInt("CODAddType", 0);
        //??????
        SysData.MIXVolume = Double.longBitsToDouble(sp.getLong("MIXVolume", 0));
        SysData.MIXSampleA = Double.longBitsToDouble(sp.getLong("MIXSampleA", 0));
        SysData.MIXSampleB = Double.longBitsToDouble(sp.getLong("MIXSampleB", 0));
        SysData.MIXSampleC = Double.longBitsToDouble(sp.getLong("MIXSampleC", 0));
        SysData.MIXSampleO = Double.longBitsToDouble(sp.getLong("MIXSampleO", 0));
        SysData.MIXAddMul = Double.longBitsToDouble(sp.getLong("MIXAddMul", 0));
        SysData.MIXAddValume = Double.longBitsToDouble(sp.getLong("MIXAddValume", 0));
        SysData.MIXAddType = sp.getInt("MIXAddType", 0);

        //????????????
        //SysData.localIpAddr[0] = sp.getString("localIpAddr", "");     //ip?????????????????????
        SysData.webPort = sp.getInt("webPort", 8080);
        SysData.isLoop = sp.getBoolean("isLoop", false);
        SysData.nextStartTime = sp.getLong("nextStartTime", 0);
        SysData.startCycle = sp.getInt("startCycle", 0);
        SysData.numberTimes = sp.getInt("numberTimes", 0);
        SysData.startType = sp.getInt("startType", 0);
        SysData.isNotice = sp.getBoolean("isNotice", false);
        SysData.isEmptyPipeline = sp.getBoolean("isEmptyPipeline", false);
        SysData.adminPassword = sp.getString("adminPassword", "nsy218");
        SysData.MODBUS_ADDR = sp.getInt("modbusAddr", 6);
        SysData.BAUD_RATE = sp.getInt("baudRate", 9600);
        //Log.i("????????????", "?????????????????????" + SysData.isNotice);
    }

    /*
    //????????????????????????????????????????????????
    private void saveStatusRun() {
        Log.i("MainActivity", "????????????????????????");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int errorid = 0;
                do {
                    errorid = SysData.errorId;
                    //????????????????????????
                    if(SysData.isRun){
                        saveMeterStatus();  //?????????????????????????????????????????????
                        if(SysData.isSaveLog) {
                            saveLog(); //??????????????????
                        }
                        try {
                            Thread.sleep(10000);  //10??????????????????????????????isRun???false??????
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        saveMeterStatus();  //?????????????????????????????????????????????
                        try {
                            Thread.sleep(60000);  //60??????????????????????????????isRun???false??????
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //??????????????????
                    getDs3231Temp();  //DS3231????????????

                    //??????????????????
                    if(SysData.isNotice) {
                        if (SysData.liusuanStatus) {
                            SysData.errorMsg = "??????????????????";
                            SysData.errorId = 9;
                        }
                        if (SysData.gaomengsuanjiaStatus) {
                            SysData.errorMsg = "????????????????????????";
                            SysData.errorId = 9;
                        }
                        if (SysData.caosuannaStatus) {
                            SysData.errorMsg = "?????????????????????";
                            SysData.errorId = 9;
                        }
                        if (SysData.zhengliushuiStatus) {
                            SysData.errorMsg = "?????????????????????";
                            SysData.errorId = 9;
                        }
                        if(errorid != SysData.errorId || errorid == 9) {
                            SysData.saveAlertToDB();  //??????????????????
                        }
                    }
                    //?????????????????????160??????????????????????????????110???????????????????????????
                    if(SysData.tempOut > 160 || SysData.tempIn > 110) {
                        try {
                            SysGpio.mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: ????????????");
                            SysData.errorMsg = "?????????????????????";
                            SysData.errorId = 7;
                            if(errorid != SysData.errorId || errorid == 7) {
                                SysData.saveAlertToDB();  //??????????????????
                            }
                            if(SysGpio.statusS8 != true) {
                                //SysGpio.s8_Reset();  //??????????????????????????????
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //??????????????????60???????????????????????????
                    if(SysData.tempBox >= 60) {
                        try {
                            SysGpio.mGpioOutH1.setValue(false);
                            Log.d(TAG, "run: ????????????");
                            SysData.errorMsg = "??????????????????";
                            SysData.errorId = 10;
                            if(errorid != SysData.errorId || errorid == 10) {
                                SysData.saveAlertToDB();  //??????????????????
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } while (true);
            }
        }).start();
    }

     */
/*
    //??????????????????
    private void saveLog() {
        Log.i("MainActivity", "????????????????????????");
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
*/
    /*
    //????????????????????????????????????????????????
    private void autoRun() {
        Log.i("MainActivity", "????????????????????????");
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    //????????????????????????????????????999???????????????????????????????????????????????????
                    long dtime = System.currentTimeMillis() - SysData.nextStartTime;
                    //????????????????????????
                    if(SysData.isLoop && !SysData.isRun && dtime > 0 && dtime < 15000 && SysData.numberTimes > 0) {
                        //??????????????????
                        startAction(SysData.startType);
                    }
                    //??????????????????????????????0????????????????????????????????????
                    if(SysData.isLoop && !SysData.isRun && SysData.startCycle == 0 && SysData.numberTimes > 0) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //??????????????????
                        startAction(SysData.startType);
                    }
                    //????????????????????????
                    if(SysData.nextStartTime < System.currentTimeMillis() && SysData.numberTimes > 0 && SysData.startCycle > 0 && SysData.isLoop) {
                        SysData.nextStartTime = SysData.nextStartTime + SysData.startCycle * 3600 * 1000;
                        Log.i("MainActivity", "???????????????" + System.currentTimeMillis() + " ?????????????????????" + SysData.nextStartTime);
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

     */
/*
    //?????????????????????????????????
    public void startAction(int actionType) {
        switch (actionType) {
            case 0:
                break;
            case 1:
                //????????????????????????
                SysGpio.s7_ShuiZhiCeDing();
                SysData.statusMsg = "????????????????????????";
                break;
            case 2:
                //????????????????????????
                SysGpio.s10_BiaoYangCeDing();
                SysData.statusMsg = "????????????????????????";
                break;
            case 3:
                //????????????????????????
                SysGpio.s11_Calibration();
                SysData.statusMsg = "????????????????????????";
                break;
        }
        if(actionType > 0) {
            SysData.isRun = true;
            SysData.workFrom = "????????????";           //??????????????????????????? ?????????????????????Web???????????????
        }
    }

    //????????????????????????
    public void saveMeterStatus() {
        //????????????
        final SharedPreferences.Editor editor = getSharedPreferences("Parameter", MODE_PRIVATE).edit();
        //?????????????????????
        editor.putBoolean("isRun", SysData.isRun);
        editor.putInt("progressRate", SysData.progressRate);
        editor.putString("statusMsg", SysData.statusMsg);
        editor.putString("errorMsg", SysData.errorMsg);
        editor.putLong("startTime", SysData.startTime);
        editor.putLong("endTime", SysData.endTime);
        editor.putLong("codValue", Double.doubleToLongBits(SysData.codValue));
        //??????????????????web???????????????
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
        //????????????
        editor.apply();
        Log.i("??????", "?????????????????????");
    }

    //??????????????????
    public void readMeterStatus() {
        //????????????
        final SharedPreferences sp = getSharedPreferences("Parameter", MODE_PRIVATE);
        //???????????????
        SysData.isRun = sp.getBoolean("isRun", false);
        SysData.progressRate = sp.getInt("progressRate", 0);
        SysData.statusMsg = sp.getString("statusMsg", "");
        SysData.errorMsg = sp.getString("errorMsg", "");
        SysData.startTime = sp.getLong("startTime", 0);
        SysData.endTime = sp.getLong("endTime", 0);
        SysData.codValue = Double.longBitsToDouble(sp.getLong("codValue", 0));
    }

    //??????????????????
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
*/


    //??????Preferences????????????
    public void clearPreferences() {
        SharedPreferences preferences = getSharedPreferences("Parameter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }


    /* ????????????????????????????????? */
    public boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
