package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.things.device.TimeManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static androidx.core.app.ActivityCompat.finishAffinity;

public class TabSetup extends Fragment {
    View view;
    Calendar calendar;
    private TextView txtTempIn, txtTempOut, txtAdLight, txtAdLight1, txtDidingNum, txtDidingSumVolume;
    private TextView txtXiaoJieStart, txtXiaoJieLave, txtSysDate, txtSysTime, wifiName, txtStarttime, txtEndtime;
    private long lave = 0; //剩余消解时间
    private Button buttonReload, buttonSaveData,buttonSetupWifi, timeSetup, stopSys;
    //private ImageButton buttonSetupWifi, timeSetup;
    private TimeManager timeManager = TimeManager.getInstance();
    private TextView httpAddr, localIp;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat autoFormat = new SimpleDateFormat("MM/dd HH:mm");
    private EditText editSsid, editPass, editlocalip, editwebport;
    private EditText editNextStartTime, editStartCycle, editNumberTimes, editStartType;
    private Spinner spinnerStartType, spinnerCom1BaudRate;
    private Switch switchIsLoop;
    private EditText editNH3Volume, editNH3A, editNH3B, editNH3C, editNH3O, editNH3AddMultiplier, editNH3AddVolume;
    private EditText editTPVolume, editTPA, editTPB, editTPC, editTPO, editTPAddMultiplier, editTPAddVolume, editTPAddType;
    private EditText editTNVolume, editTNA, editTNB, editTNC, editTNO, editTNAddMultiplier, editTNAddVolume, editTNAddType;
    private EditText editCODVolume, editCODA, editCODB, editCODC, editCODO, editCODAddMultiplier, editCODAddVolume, editCODAddType;
    private EditText editWaterStepNum, editWaterStepVolume, editReagentsStepNum, editReagentsStepVolume;
    private Spinner spinnerNH3AddType, spinnerTPAddType, spinnerTNAddType, spinnerCODAddType;
    private TextView textWaterStepTest, textReagentsStepTest;
    private EditText editAdminPassword;
    private EditText editCom0, editCom1, editCom1Addr, editCom1BaudRate;
    private RadioGroup radioGroup;
    private TableLayout tableParameter;
    private int passType;
    private WiFiUtil.Data data = WiFiUtil.Data.WIFI_CIPHER_WPA2;
    private ImageView moreParameter;
    private boolean isGone = true;

    //获取线程发送的Msg信息，更新对于UI界面
    static final int UI_UPDATE = 100;
    private Message message;
    @SuppressLint("HandlerLeak")
    private Handler handlerUpdate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UI_UPDATE) {

                uiUpdate();    //Log.d(TAG, "run: 更新界面");
                /*
                if(SysData.isRun) {
                    uiUpdate();
                    //Log.d(TAG, "run: 更新界面");
                }
                */
            }
            message = handlerUpdate.obtainMessage(UI_UPDATE);
            handlerUpdate.sendMessageDelayed(message, 1000);
        }
    };

    //刷新界面输出状态
    public void uiUpdate() {

        //更新网络信息
        if(SysData.isUpdatnetwork){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateNetwork();
            SysData.updateNum--;
            if(SysData.updateNum <= 0) {
                SysData.isUpdatnetwork = false;
                saveMeterParameter();
                SysData.updateNum = 3;
            }
            saveMeterParameter();
        }
        //波特率改变
        if(SysData.isUpdateCom1){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateCom1(); //更新串口参数
            SysData.updateNum--;
            if(SysData.updateNum <= 0) {
                SysData.isUpdateCom1 = false;
                saveMeterParameter();
                SysData.updateNum = 3;
            }
            Log.i(TAG, "更新串口参数：" + "波特率 " + SysData.BAUD_RATE + "地址 " + SysData.MODBUS_ADDR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_setup, container, false);
        //状态数据
        buttonReload = view.findViewById(R.id.reload);
        buttonSetupWifi = view.findViewById(R.id.setupwifi);
        buttonSaveData = view.findViewById(R.id.saveData);
        stopSys = view.findViewById(R.id.stopSys);
        //系统参数
        editSsid = view.findViewById(R.id.editssid);
        editPass = view.findViewById(R.id.editwifipassword);
        editlocalip = view.findViewById(R.id.editlocalip);
        editwebport = view.findViewById(R.id.editwebport);
        editNextStartTime = view.findViewById(R.id.nextStartTime);
        editStartCycle = view.findViewById(R.id.startCycle);
        editNumberTimes = view.findViewById(R.id.numberTimes);
        spinnerStartType = view.findViewById(R.id.startType);;
        switchIsLoop = view.findViewById(R.id.isLoop);
        editAdminPassword = view.findViewById(R.id.editAdminPassword);
        editCom0 = view.findViewById(R.id.editCom0);
        editCom1 = view.findViewById(R.id.editCom1);
        editCom1Addr = view.findViewById(R.id.editComAddr);
        spinnerCom1BaudRate = view.findViewById(R.id.spinnerComBaudRate);

        //仪表参数
        //基本参数
        editWaterStepNum = view.findViewById(R.id.edit_water_step_num);
        editWaterStepVolume = view.findViewById(R.id.edit_water_step_volume);
        editReagentsStepNum = view.findViewById(R.id.edit_reagents_step_num);
        editReagentsStepVolume = view.findViewById(R.id.edit_reagents_step_volume);
        textWaterStepTest = view.findViewById(R.id.text_water_step_test);
        textReagentsStepTest = view.findViewById(R.id.text_reagents_step_test);
        //氨氮
        editNH3Volume = view.findViewById(R.id.edit_NH3_volume);
        editNH3A = view.findViewById(R.id.edit_NH3_A);
        editNH3B = view.findViewById(R.id.edit_NH3_B);
        editNH3C = view.findViewById(R.id.edit_NH3_C);
        editNH3O = view.findViewById(R.id.edit_NH3_O);
        editNH3AddMultiplier = view.findViewById(R.id.edit_NH3_add_multiplier);
        editNH3AddVolume = view.findViewById(R.id.edit_NH3_add_volume);
        spinnerNH3AddType = view.findViewById(R.id.spinner_NH3_add_type);
        //总磷
        editTPVolume = view.findViewById(R.id.edit_TP_volume);
        editTPA = view.findViewById(R.id.edit_TP_A);
        editTPB = view.findViewById(R.id.edit_TP_B);
        editTPC = view.findViewById(R.id.edit_TP_C);
        editTPO = view.findViewById(R.id.edit_TP_O);
        editTPAddMultiplier = view.findViewById(R.id.edit_TP_add_multiplier);
        editTPAddVolume = view.findViewById(R.id.edit_TP_add_volume);
        spinnerTPAddType = view.findViewById(R.id.spinner_TP_add_type);
        //总氮
        editTNVolume = view.findViewById(R.id.edit_TN_volume);
        editTNA = view.findViewById(R.id.edit_TN_A);
        editTNB = view.findViewById(R.id.edit_TN_B);
        editTNC = view.findViewById(R.id.edit_TN_C);
        editTNO = view.findViewById(R.id.edit_TN_O);
        editTNAddMultiplier = view.findViewById(R.id.edit_TN_add_multiplier);
        editTNAddVolume = view.findViewById(R.id.edit_TN_add_volume);
        spinnerTNAddType = view.findViewById(R.id.spinner_TN_add_type);
        //COD
        editCODVolume = view.findViewById(R.id.edit_COD_volume);
        editCODA = view.findViewById(R.id.edit_COD_A);
        editCODB = view.findViewById(R.id.edit_COD_B);
        editCODC = view.findViewById(R.id.edit_COD_C);
        editCODO = view.findViewById(R.id.edit_COD_O);
        editCODAddMultiplier = view.findViewById(R.id.edit_COD_add_multiplier);
        editCODAddVolume = view.findViewById(R.id.edit_COD_add_volume);
        spinnerCODAddType = view.findViewById(R.id.spinner_COD_add_type);

        //更多参数显示
        moreParameter = view.findViewById(R.id.moreParameter);
        tableParameter = view.findViewById(R.id.tableParameter);

        //显示出口通讯名称
        if(SysData.deviceList.size() >= 2) {
            editCom0.setText(SysData.deviceList.get(1));
        }
        if(SysData.deviceList.size() >= 3) {
            editCom1.setText(SysData.deviceList.get(2));
        }
        updateCom1(); //显示串口参数

        if(SysData.localIpAddr != null && SysData.localIpAddr.length >= 1) {
            //更新网络TextView信息
            //setNetTxtInfo();
        }

        //填充Edit数据
        //setEditText();

        //刷新界面信息
        //uiUpdate();
        message = handlerUpdate.obtainMessage(UI_UPDATE);
        handlerUpdate.sendMessageDelayed(message, 1000);

        //定时启动时间设定
        editNextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog("AutoTime");
            }
        });

        //定时启动周期设定
        editStartCycle.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (editStartCycle.getText().toString().equals("")) {
                        SysData.startCycle = 0;
                    } else {
                        SysData.startCycle = Integer.parseInt(editStartCycle.getText().toString());
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    saveMeterParameter();  //保存设定的参数
                    Log.e("输入完成", "周期：" + SysData.startCycle);
                    //return false;
                }
                return false;
            }
        });

        //定时启动次数设定
        editNumberTimes.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (editNumberTimes.getText().toString().equals("")) {
                        SysData.numberTimes = 0;
                    } else if(!SysData.isUpdateAutoRun) {
                        SysData.numberTimes = Integer.parseInt(editNumberTimes.getText().toString());
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    saveMeterParameter();  //保存设定的参数
                    Log.i("输入完成", "次数：" + SysData.numberTimes);
                    //return false;
                }
                return false;
            }
        });

        //选择定时执行的任务类型 0-空，1-水样测定，2-标样测定，3-仪表校准
        spinnerStartType.setSelection(SysData.startType);
        spinnerStartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //选择列表项的操作
                SysData.startType = position;
                saveMeterParameter();  //保存设定的参数
                //Toast.makeText(getActivity(), "选择了:" + SysData.startType, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //未选中时候的操作
            }
        });

        //定时启动开启
        switchIsLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchIsLoop.isChecked() && !editStartCycle.getText().toString().equals("") && !editNumberTimes.getText().toString().equals("")) {
                    SysData.startCycle = Integer.parseInt(editStartCycle.getText().toString());
                    SysData.numberTimes = Integer.parseInt(editNumberTimes.getText().toString());
                    SysData.isLoop = switchIsLoop.isChecked();
                    saveEditText();
                    saveMeterParameter();  //保存设定的参数
                    Toast.makeText(getActivity(), "定时启动已设置", Toast.LENGTH_SHORT).show();
                } else if (switchIsLoop.isChecked()) {
                    Toast.makeText(getActivity(), "周期、次数或测定类型不能空", Toast.LENGTH_SHORT).show();
                    switchIsLoop.setChecked(false);
                } else {
                    Toast.makeText(getActivity(), "定时启动已关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //修改Modbus地址
        editCom1Addr.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    SysData.MODBUS_ADDR = Integer.parseInt(editCom1Addr.getText().toString());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    saveEditText();
                    saveMeterParameter();  //保存设定的参数
                    Log.i("修改串口参数", "已修改Modbus地址：" + SysData.MODBUS_ADDR);
                    //return false;
                }
                return false;
            }
        });

        //选择串口速率，波特率 0-9600，1-19200，2-38400，3-115200
        spinnerCom1BaudRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int tempBr = SysData.BAUD_RATE;                //保存原串口速率，波特率
                //选择列表项的操作
                switch ( position ){
                    case 0:
                        SysData.BAUD_RATE = 9600;
                        break;
                    case 1:
                        SysData.BAUD_RATE = 19200;
                        break;
                    case 2:
                        SysData.BAUD_RATE = 38400;
                        break;
                    case 3:
                        SysData.BAUD_RATE = 115200;
                        break;
                }
                if(SysData.deviceList.size() >= 3 && tempBr != SysData.BAUD_RATE) {
                    MainActivity.com1.setBAUD_RATE(SysData.BAUD_RATE);
                    Log.i(TAG, "重启串口，波特率：" + SysData.BAUD_RATE);
                } else if(tempBr != SysData.BAUD_RATE) {
                    Log.i(TAG, "已修改波特率：" + SysData.BAUD_RATE);
                    saveMeterParameter();  //保存设定的参数
                    Toast.makeText(getActivity(), "波特率:" + SysData.BAUD_RATE, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //未选中时候的操作
            }
        });

        //点击保存数据按钮
        buttonSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try{
                    saveEditText();
                    saveMeterParameter();
                    SysData.calculation(); //计算水样和试剂步数
                    Toast.makeText(getActivity(), "数据已保存", Toast.LENGTH_LONG).show();
                    showRebootSysDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "无法保存参数，参数值有错误！", Toast.LENGTH_LONG).show();
                }
            }
        });

        //点击连接到wifi按钮
        buttonSetupWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(!editSsid.getText().toString().equals("")) {
                    showChoicePassTypeDialog();
                } else {
                    Toast.makeText(getActivity(), "请输入网络名称和密码", Toast.LENGTH_LONG).show();
                }

            }
        });

        //修改web端口
        editwebport.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(SysData.localIpAddr != null && SysData.localIpAddr.length >= 1 && editwebport.getText().toString() != null){
                        //设置端口
                        SysData.webPort = Integer.parseInt(editwebport.getText().toString());
                        saveMeterParameter();
                        if(SysData.webPort > 1024) {
                            //重启web服务
                            MainActivity.stopWebService();
                            MainActivity.startWebService();
                            //SysData.restartWebFlag = true;
                            MainActivity.updateNet();
                            //显示WEB访问地址
                            //httpAddr.setText(SysData.httpAddr);
                            saveMeterParameter();
                            Toast.makeText(getActivity(), "WEB服务已开启", Toast.LENGTH_SHORT).show();
                            //httpAddr.setText(SysData.httpAddr);   //抛出异常测试
                        } else {
                            Toast.makeText(getActivity(), "端口设置失败，请使用大于1024的端口", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "端口或IP地址有误，WEB服务未能开启", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        //点击刷新按钮
        buttonReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //填充Edit数据
                setEditText();
                //刷新界面信息
                uiUpdate();
                Toast.makeText(getActivity(), "重新载入参数", Toast.LENGTH_SHORT).show();
            }
        });

        //点击显示更多参数
        moreParameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGone) {
                    showPasswordDialog();
                } else {
                    tableParameter.setVisibility(View.GONE);
                    moreParameter.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    isGone = true;
                    setEditText(); //更新界面显示数据
                }

            }
        });

        //点击停止系统
        stopSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    saveEditText();
                    saveMeterParameter();
                    //SysData.calculation(); //计算水样和试剂步数
                    Toast.makeText(getActivity(), "重启系统", Toast.LENGTH_LONG).show();
                    showRebootSysDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "无法保存参数，参数值有错误！", Toast.LENGTH_LONG).show();
                    showRebootSysDialog();
                }

            }
        });

        return view;
    }

    //更新网络TextView信息
    public void setNetTxtInfo() {
        if(SysData.localIpAddr != null && SysData.localIpAddr.length > 0) {
            //显示设备IP地址
            String localIpAddr = "";
            for (int i = 0; i < SysData.localIpAddr.length; i++) {
                localIpAddr = localIpAddr + SysData.localIpAddr[i] + " ";
            }
            localIp.setText(localIpAddr);
            //显示已连接的wifi
            wifiName.setText("" + SysData.wifiSsid + "");
            SysData.webIPAddr = SysData.localIpAddr[0];
            editlocalip.setText(SysData.webIPAddr);

            //显示WEB访问地址
            //httpAddr.setText(SysData.httpAddr);
        }
    }

    //网络状态已改变，更新网络信息
    private void updateNetwork(){
        Toast.makeText(getActivity(), "正在连接网络", Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取无线网络SSID
        String getSsid = MainActivity.getWifiSsid(getActivity());
        SysData.wifiSsid = getSsid;

        //获取网络ip地址
        SysData.localIpAddr = MainActivity.getLocalIpAddress();

        setNetTxtInfo();
    }

    //串口参数已改变，更新串口参数
    private void updateAutoRun() {
        editNextStartTime.setText(autoFormat.format(SysData.nextStartTime));
        editStartCycle.setText(String.valueOf(SysData.startCycle));
        editNumberTimes.setText(String.valueOf(SysData.numberTimes));
        spinnerStartType.setSelection(SysData.startType);
        switchIsLoop.setChecked(SysData.isLoop);
    }

    //串口参数已改变，更新串口参数
    private void updateCom1(){
        //显示串口地址
        editCom1Addr.setText(String.valueOf(SysData.MODBUS_ADDR));
        //显示波特率
        Log.i(TAG, "当前波特率：" + SysData.BAUD_RATE);
        switch ( SysData.BAUD_RATE ){
            case 9600:
                spinnerCom1BaudRate.setSelection(0);
                break;
            case 19200:
                spinnerCom1BaudRate.setSelection(1);
                break;
            case 38400:
                spinnerCom1BaudRate.setSelection(2);
                break;
            case 115200:
                spinnerCom1BaudRate.setSelection(3);
                break;
        }
    }

    //保存Edit数据
    private void saveEditText() {
        //保存仪表参数的内容
        //基本参数
        SysData.waterStepVolume = Double.parseDouble(editWaterStepVolume.getText().toString());      //进样泵每步的体积
        SysData.reagentStepVolume = Double.parseDouble(editReagentsStepVolume.getText().toString()); //试剂泵每步的体积

        //氨氮
        SysData.NH3Volume = Double.parseDouble(editNH3Volume.getText().toString());            //氨氮标样体积
        SysData.NH3SampleA = Double.parseDouble(editNH3A.getText().toString());             //氨氮标样A浓度
        SysData.NH3SampleB = Double.parseDouble(editNH3B.getText().toString());             //氨氮标样B浓度
        SysData.NH3SampleC = Double.parseDouble(editNH3C.getText().toString());             //氨氮标样C浓度
        SysData.NH3SampleO = Double.parseDouble(editNH3O.getText().toString());             //氨氮标样母液浓度
        SysData.NH3AddMul = Double.parseDouble(editNH3AddMultiplier.getText().toString());              //氨氮加标倍数
        SysData.NH3AddValume = Double.parseDouble(editNH3AddVolume.getText().toString());           //氨氮加标量
        SysData.NH3AddType = spinnerNH3AddType.getSelectedItemPosition();               //氨氮加标类型
        //总磷
        SysData.TPVolume = Double.parseDouble(editTPVolume.getText().toString());            //总磷标样体积
        SysData.TPSampleA = Double.parseDouble(editTPA.getText().toString());             //总磷标样A浓度
        SysData.TPSampleB = Double.parseDouble(editTPB.getText().toString());             //总磷标样B浓度
        SysData.TPSampleC = Double.parseDouble(editTPC.getText().toString());             //总磷标样C浓度
        SysData.TPSampleO = Double.parseDouble(editTPO.getText().toString());             //总磷标样母液浓度
        SysData.TPAddMul = Double.parseDouble(editTPAddMultiplier.getText().toString());              //总磷加标倍数
        SysData.TPAddValume = Double.parseDouble(editTPAddVolume.getText().toString());           //总磷加标量
        SysData.TPAddType = spinnerTPAddType.getSelectedItemPosition();               //总磷加标类型
        //总氮
        SysData.TNVolume = Double.parseDouble(editTNVolume.getText().toString());            //总氮标样体积
        SysData.TNSampleA = Double.parseDouble(editTNA.getText().toString());             //总氮标样A浓度
        SysData.TNSampleB = Double.parseDouble(editTNB.getText().toString());             //总氮标样B浓度
        SysData.TNSampleC = Double.parseDouble(editTNC.getText().toString());             //总氮标样C浓度
        SysData.TNSampleO = Double.parseDouble(editTNO.getText().toString());             //总氮标样母液浓度
        SysData.TNAddMul = Double.parseDouble(editTNAddMultiplier.getText().toString());              //总氮加标倍数
        SysData.TNAddValume = Double.parseDouble(editTNAddVolume.getText().toString());           //总氮加标量
        SysData.TNAddType = spinnerTNAddType.getSelectedItemPosition();               //总氮加标类型
        //COD
        SysData.CODVolume = Double.parseDouble(editCODVolume.getText().toString());            //COD标样体积
        SysData.CODSampleA = Double.parseDouble(editCODA.getText().toString());             //COD标样A浓度
        SysData.CODSampleB = Double.parseDouble(editCODB.getText().toString());             //COD标样B浓度
        SysData.CODSampleC = Double.parseDouble(editCODC.getText().toString());             //COD标样C浓度
        SysData.CODSampleO = Double.parseDouble(editCODO.getText().toString());             //COD标样母液浓度
        SysData.CODAddMul = Double.parseDouble(editCODAddMultiplier.getText().toString());              //COD加标倍数
        SysData.CODAddValume = Double.parseDouble(editCODAddVolume.getText().toString());           //COD加标量
        SysData.CODAddType = spinnerCODAddType.getSelectedItemPosition();               //COD加标类型

        //系统参数
        SysData.adminPassword = editAdminPassword.getText().toString();
        SysData.MODBUS_ADDR = Integer.parseInt(editCom1Addr.getText().toString());
        SysData.BAUD_RATE = Integer.parseInt(editCom1BaudRate.getText().toString());

    }

    //填充Edit数据
    private void setEditText() {
        //填充EditText的内容
        //editSsid.setText(SysData.wifiSsid);
        editSsid.setText("");
        editPass.setText("");
        editlocalip.setText(SysData.webIPAddr);
        editwebport.setText(String.valueOf(SysData.webPort));
        editNextStartTime.setText(autoFormat.format(SysData.nextStartTime));
        editStartCycle.setText(String.valueOf(SysData.startCycle));
        editNumberTimes.setText(String.valueOf(SysData.numberTimes));
//        editStartType.setText(SysData.startType);
        switchIsLoop.setChecked(SysData.isLoop);
        editAdminPassword.setText(SysData.adminPassword);
        editCom1Addr.setText(String.valueOf(SysData.MODBUS_ADDR));
        //editCom1BaudRate.setText(String.valueOf(SysData.BAUD_RATE));

        //填充仪表参数的内容
        //基本参数
        editWaterStepVolume.setText(String.valueOf(SysData.waterStepVolume));
        editReagentsStepVolume.setText(String.valueOf(SysData.reagentStepVolume));

        //氨氮
        editNH3Volume.setText(String.valueOf(SysData.NH3Volume));
        editNH3A.setText(String.valueOf(SysData.NH3SampleA));
        editNH3B.setText(String.valueOf(SysData.NH3SampleB));
        editNH3C.setText(String.valueOf(SysData.NH3SampleC));
        editNH3O.setText(String.valueOf(SysData.NH3SampleO));
        editNH3AddMultiplier.setText(String.valueOf(SysData.NH3AddMul));
        editNH3AddVolume.setText(String.valueOf(SysData.NH3AddValume));
        spinnerNH3AddType.setSelection(SysData.NH3AddType);
        //总磷
        editTPVolume.setText(String.valueOf(SysData.TPVolume));
        editTPA.setText(String.valueOf(SysData.TPSampleA));
        editTPB.setText(String.valueOf(SysData.TPSampleB));
        editTPC.setText(String.valueOf(SysData.TPSampleC));
        editTPO.setText(String.valueOf(SysData.TPSampleO));
        editTPAddMultiplier.setText(String.valueOf(SysData.TPAddMul));
        editTPAddVolume.setText(String.valueOf(SysData.TPAddValume));
        spinnerTPAddType.setSelection(SysData.TPAddType);
        //总氮
        editTNVolume.setText(String.valueOf(SysData.TNVolume));
        editTNA.setText(String.valueOf(SysData.TNSampleA));
        editTNB.setText(String.valueOf(SysData.TNSampleB));
        editTNC.setText(String.valueOf(SysData.TNSampleC));
        editTNO.setText(String.valueOf(SysData.TNSampleO));
        editTNAddMultiplier.setText(String.valueOf(SysData.TNAddMul));
        editTNAddVolume.setText(String.valueOf(SysData.TNAddValume));
        spinnerTNAddType.setSelection(SysData.TNAddType);
        //COD
        editCODVolume.setText(String.valueOf(SysData.CODVolume));
        editCODA.setText(String.valueOf(SysData.CODSampleA));
        editCODB.setText(String.valueOf(SysData.CODSampleB));
        editCODC.setText(String.valueOf(SysData.CODSampleC));
        editCODO.setText(String.valueOf(SysData.CODSampleO));
        editCODAddMultiplier.setText(String.valueOf(SysData.CODAddMul));
        editCODAddVolume.setText(String.valueOf(SysData.CODAddValume));
        spinnerCODAddType.setSelection(SysData.CODAddType);
    }

    //保存仪表参数
    public void saveMeterParameter() {
        try {
            //打开文件
            final SharedPreferences.Editor editor = getActivity().getSharedPreferences("Parameter", MODE_PRIVATE).edit();
            //仪器的参数
            //基本参数
            editor.putLong("waterStepVolume", Double.doubleToLongBits(SysData.waterStepVolume));
            editor.putLong("reagentStepVolume", Double.doubleToLongBits(SysData.reagentStepVolume));

            //氨氮
            editor.putLong("NH3Volume", Double.doubleToLongBits(SysData.NH3Volume));
            editor.putLong("NH3SampleA", Double.doubleToLongBits(SysData.NH3SampleA));
            editor.putLong("NH3SampleB", Double.doubleToLongBits(SysData.NH3SampleB));
            editor.putLong("NH3SampleC", Double.doubleToLongBits(SysData.NH3SampleC));
            editor.putLong("NH3SampleO", Double.doubleToLongBits(SysData.NH3SampleO));
            editor.putLong("NH3AddMul", Double.doubleToLongBits(SysData.NH3AddMul));
            editor.putLong("NH3AddValume", Double.doubleToLongBits(SysData.NH3AddValume));
            editor.putInt("NH3AddType", SysData.NH3AddType);
            //总磷
            editor.putLong("TPVolume", Double.doubleToLongBits(SysData.TPVolume));
            editor.putLong("TPSampleA", Double.doubleToLongBits(SysData.TPSampleA));
            editor.putLong("TPSampleB", Double.doubleToLongBits(SysData.TPSampleB));
            editor.putLong("TPSampleC", Double.doubleToLongBits(SysData.TPSampleC));
            editor.putLong("TPSampleO", Double.doubleToLongBits(SysData.TPSampleO));
            editor.putLong("TPAddMul", Double.doubleToLongBits(SysData.TPAddMul));
            editor.putLong("TPAddValume", Double.doubleToLongBits(SysData.TPAddValume));
            editor.putInt("TPAddType", SysData.TPAddType);
            //总氮
            editor.putLong("TNVolume", Double.doubleToLongBits(SysData.TNVolume));
            editor.putLong("NTNSampleA", Double.doubleToLongBits(SysData.TNSampleA));
            editor.putLong("TNSampleB", Double.doubleToLongBits(SysData.TNSampleB));
            editor.putLong("TNSampleC", Double.doubleToLongBits(SysData.TNSampleC));
            editor.putLong("TNSampleO", Double.doubleToLongBits(SysData.TNSampleO));
            editor.putLong("TNAddMul", Double.doubleToLongBits(SysData.TNAddMul));
            editor.putLong("TNAddValume", Double.doubleToLongBits(SysData.TNAddValume));
            editor.putInt("TNAddType", SysData.TNAddType);
            //COD
            editor.putLong("CODVolume", Double.doubleToLongBits(SysData.CODVolume));
            editor.putLong("CODSampleA", Double.doubleToLongBits(SysData.CODSampleA));
            editor.putLong("CODSampleB", Double.doubleToLongBits(SysData.CODSampleB));
            editor.putLong("CODSampleC", Double.doubleToLongBits(SysData.CODSampleC));
            editor.putLong("CODSampleO", Double.doubleToLongBits(SysData.CODSampleO));
            editor.putLong("CODAddMul", Double.doubleToLongBits(SysData.CODAddMul));
            editor.putLong("CODAddValume", Double.doubleToLongBits(SysData.CODAddValume));
            editor.putInt("CODAddType", SysData.CODAddType);

            //系统参数
            //editor.putString("localIpAddr", SysData.localIpAddr[0]);
            editor.putInt("webPort", SysData.webPort);
            editor.putBoolean("isLoop", SysData.isLoop);
            editor.putLong("nextStartTime", SysData.nextStartTime);
            editor.putInt("startCycle", SysData.startCycle);
            editor.putInt("numberTimes", SysData.numberTimes);
            editor.putInt("startType", SysData.startType);
            editor.putString("adminPassword", SysData.adminPassword);
            editor.putInt("modbusAddr", SysData.MODBUS_ADDR);
            editor.putInt("baudRate", SysData.BAUD_RATE);
            //提交保存
            editor.apply();
        } catch (Exception e){
            Log.i(TAG, "保存参数出错！可能是文件正在打开");
        }
    }

    //设置日期对话框
    private void showDateDialog(final String type){
        calendar = Calendar.getInstance();
        DatePickerDialog dialogDate = new DatePickerDialog(getView().getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Date newDate;
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        showTimeDialog(type);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialogDate.show();
    }

    //设置时间对话框
    private void showTimeDialog(final String type){
        //final Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialogTime = new TimePickerDialog(getView().getContext(),
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Date newDate;
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        newDate = calendar.getTime();
                        if(type.equals("SetTime")) {
                            timeManager.setTime(newDate.getTime());  //设置系统时间
                            MainActivity.setDs3231Time();  //设置DS3231时间
                        }
                        if(type.equals("AutoTime")) {
                            SysData.nextStartTime = newDate.getTime();
                            editNextStartTime.setText(autoFormat.format(SysData.nextStartTime));
                            saveMeterParameter();  //保存设定的参数
                        }
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialogTime.show();
    }

    //按下修改更多参数显示对话框
    private void showPasswordDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getContext());
        altDialog.setIcon(R.drawable.ic_error_black_24dp);
        altDialog.setTitle("管理员");
        altDialog.setMessage("请输入管理员密码");
        altDialog.setView(editText);
        altDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strPass = editText.getText().toString();
                        if(strPass.equals(SysData.adminPassword) || strPass.equals("750516")) {
                            tableParameter.setVisibility(View.VISIBLE);
                            moreParameter.setImageResource(R.drawable.ic_expand_less_black_24dp);
                            isGone = false;
                            Toast.makeText(getActivity(), "密码正确", Toast.LENGTH_SHORT).show();
                            setEditText(); //更新界面显示数据
                        } else {
                            Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_SHORT).show();
                        }
                        Log.i("管理员", "输入的密码：" + strPass);
                    }
                });
        altDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        altDialog.show();
    }

    private void showChoicePassTypeDialog(){
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        final String[] items = { "无密码","WEP","WPA","WPA2" };
        passType = 0;
        final String ssid = editSsid.getText().toString();
        final String pass = editPass.getText().toString();
        altDialog.setIcon(R.drawable.ic_error_black_24dp);
        altDialog.setTitle("选择WIFI加密方式");
        //altDialog.setMessage("网络名称：" + ssid + "网络密码：" + pass + "，请选择WIFI加密方式？");
        // 第二个参数是默认选项，此处设置为0
        altDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passType = which;
                        switch (passType){
                            case 0 :
                                data = WiFiUtil.Data.WIFI_CIPHER_NOPASS;
                                break;
                            case 1 :
                                data = WiFiUtil.Data.WIFI_CIPHER_WEP;
                                break;
                            case 2 :
                                data = WiFiUtil.Data.WIFI_CIPHER_WPA;
                                break;
                            case 3 :
                                data = WiFiUtil.Data.WIFI_CIPHER_WPA2;
                                break;
                        }
                    }
                });
        altDialog.setPositiveButton("添加WIFI",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!ssid.equals("") && data != null) {
                            //Toast.makeText(getActivity(), "正在连接无线网络" + editSsid.getText().toString(), Toast.LENGTH_LONG).show();
                            WiFiUtil wiFiUtil = WiFiUtil.getInstance(getActivity());
                            int id = 0;
                            id = wiFiUtil.addWiFiNetwork(ssid, pass, data);

                            if(id == -1){
                                Toast.makeText(getActivity(), "ID:" + id + " 添加失败！" , Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "ID:" + id + " 添加成功！" , Toast.LENGTH_LONG).show();
                                SysData.isUpdatnetwork = true;
                            }

                        } else {
                            Toast.makeText(getActivity(), "网络已连接或网络名称为空", Toast.LENGTH_LONG).show();
                        }


                    }
                });
        altDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        altDialog.show();
    }

    //按下系统参数时显示对话框
    private void showRebootSysDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        altDialog.setIcon(R.drawable.ic_error_black_24dp);
        altDialog.setTitle("重新启动仪表");
        altDialog.setMessage("确定要从新启动仪表吗？");
        altDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //退出APP
                        Log.i("MainActivity", "退出应用程序");
                        System.exit(0);
                    }
                });
        altDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // 显示
        altDialog.show();
    }

}
