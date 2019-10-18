package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.things.device.TimeManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TimePicker;

import com.instacart.library.truetime.TrueTime;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private TimeManager timeManager = TimeManager.getInstance();
    public static UartCom com0, com1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //启动后台服务
        Intent intent = new Intent(this, SysService.class);
        Bundle bundle = new Bundle();
        startService(intent);
        Log.i("MainActivity", "启动后台服务");

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
        Resources res = getResources();
        String urlString = "http://" + ipText + ":8080";
        SysData.wifiIpAdd = urlString;
    }

    //获取IP地址
    public static String getLocalIpStr(Context context){
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        return  intToIpAddr(wifiInfo.getIpAddress());
    }

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
}
