package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.things.device.TimeManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import com.instacart.library.truetime.TrueTime;
import java.io.IOException;
import java.util.Date;



public class MainActivity extends AppCompatActivity {

    private TimeManager timeManager = TimeManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //加载Tab页面
        loadTabPager();

        //第一次运行获取网络时间，每天零时网络校时
        if(!SysData.isGetNetTime){
            setSysTime();
        }

        //打开并初始化Gpio端口
        SysGpio.gpioInit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysGpio.onClose();
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
    private void setSysTime()
    {
        Log.i("MainActivity", "准备获取网络时间");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = null; //new Date(System.currentTimeMillis());  //初始时间从系统获取
                int isOK = 0;
                int max = 10;
                do{
                    try {
                        TrueTime.build().initialize();
                        date = TrueTime.now();
                        Log.i("MainActivity", "获取网络时间成功");
                        Log.i("MainActivity", "网络时间："+date.toString());
                        Log.i("MainActivity", "网络时间："+date.getTime());
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                    if(date != null){
                        timeManager.setTime(date.getTime());
                        isOK = max;
                        SysData.isGetNetTime = true;
                    }else {
                        isOK = isOK + 1;
                    }
                    try {
                        Thread.sleep(10000);  // 线程暂停10秒，单位毫秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while(isOK < max || SysData.isGetNetTime == false);

                timeManager.setTimeFormat(TimeManager.FORMAT_24); //设置24小时格式
                timeManager.setTimeZone("Asia/Shanghai"); //设置时区
            }
        }).start();

    }
}
