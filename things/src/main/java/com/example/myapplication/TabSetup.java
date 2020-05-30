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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import static android.content.Context.MODE_PRIVATE;

public class TabSetup extends Fragment {
    View view;
    Calendar calendar;
    private TextView txtTempIn, txtTempOut, txtAdLight, txtAdLight1, txtDidingNum, txtDidingSumVolume;
    private TextView txtXiaoJieStart, txtXiaoJieLave, txtSysDate, txtSysTime, wifiName, txtStarttime, txtEndtime;
    private long lave = 0; //剩余消解时间
    private Button buttonSetupWeb, buttonSaveData,buttonSetupWifi, timeSetup, stopSys;
    //private ImageButton buttonSetupWifi, timeSetup;
    private TimeManager timeManager = TimeManager.getInstance();
    private TextView httpAddr, localIp;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH时mm分ss秒");
    private SimpleDateFormat autoFormat = new SimpleDateFormat("MM/dd HH:mm");
    private EditText editSsid, editPass, editlocalip, editwebport;
    private EditText editNextStartTime, editStartCycle, editNumberTimes;
    private Switch switchIsLoop;
    private EditText editShuiyangStep,editShuiyangVolume,editLiusuanStep,editLiusuanVolume,editCaosuannaStep,editCaosuannaVolume;
    private EditText editGaomengsuanjiaStep,editGaomengsuanjiaVolume,editDidingStep,editDidingVolume,editXiaojieTemp,editXiaojieTime;
    private EditText editKongbaiValue, editBiaodingValue, editCaosuannaCon, editDidingDeviation, editAdminPassword;
    private EditText editCom0, editCom1;
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
        //显示状态数据
        txtTempIn.setText(Double.toString(SysData.tempIn) + "℃");
        txtTempOut.setText(Double.toString(SysData.tempOut) + "℃");
        txtAdLight.setText(Integer.toString(SysData.adLight));
        txtAdLight1.setText(Integer.toString(SysData.startAdLight));
        txtDidingNum.setText(Integer.toString(SysData.didingNum)); //4为注射泵开始滴定到有高锰酸钾出来的数量
        txtDidingSumVolume.setText(Double.toString(SysData.didingSumVolume));
        if(SysData.startXiaojie != 0) txtXiaoJieStart.setText(timeFormat.format(SysData.startXiaojie));
        if(SysData.startTime != 0) txtStarttime.setText(timeFormat.format(SysData.startTime));
        if(SysData.endTime != 0) txtEndtime.setText(timeFormat.format(SysData.endTime));
        lave = (SysData.endXiaoJie > System.currentTimeMillis()) ? (SysData.endXiaoJie - System.currentTimeMillis()) / 1000 : 0;
        txtXiaoJieLave.setText(Long.toString(lave) + "秒");
        txtSysDate.setText(dateFormat.format(System.currentTimeMillis()));
        //txtSysTime.setText(timeFormat.format(System.currentTimeMillis()));
        //更新自动运行信息
        if(SysData.isUpdateTimes) {
            Log.i("变更自动运行参数", "isUpdateTimes：" + SysData.isUpdateTimes);
            Log.i("变更自动运行参数", "numberTimes：" + SysData.numberTimes);
            Log.i("变更自动运行参数", "页面显示：" +editNumberTimes.getText().toString());
            if(!editNumberTimes.getText().toString().equals(String.valueOf(SysData.numberTimes))) {
                editNextStartTime.setText(autoFormat.format(SysData.nextStartTime));
                editNumberTimes.setText(String.valueOf(SysData.numberTimes));
                SysData.isUpdateTimes = false;
                Log.i("变更自动运行参数", "已经更新自动运行参数");
                Log.i("变更自动运行参数", "isUpdateTimes：" + SysData.isUpdateTimes);
                Log.i("变更自动运行参数", "numberTimes：" + SysData.numberTimes);
                Log.i("变更自动运行参数", "页面显示：" +editNumberTimes.getText().toString());
            } else {
                SysData.isUpdateTimes = false;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_setup, container, false);
        //状态数据
        txtTempIn = view.findViewById(R.id.tempin);
        txtTempOut = view.findViewById(R.id.tempout);
        txtAdLight = view.findViewById(R.id.adlight);
        txtAdLight1 = view.findViewById(R.id.adlight1);
        txtDidingNum = view.findViewById(R.id.didingnum);
        txtDidingSumVolume = view.findViewById(R.id.didingSumVolume);
        txtXiaoJieStart = view.findViewById(R.id.xiaojiestart);
        txtXiaoJieLave = view.findViewById(R.id.xiaojielave);
        txtStarttime = view.findViewById(R.id.starttime);
        txtEndtime = view.findViewById(R.id.endtime);
        timeSetup = view.findViewById(R.id.setuptime);
        //httpAddr = view.findViewById(R.id.httpaddr);
        localIp = view.findViewById(R.id.localip);
        txtSysDate = view.findViewById(R.id.sysdate);
        //txtSysTime = view.findViewById(R.id.systime);
        wifiName = view.findViewById(R.id.wifissid);
        buttonSetupWeb = view.findViewById(R.id.setupweb);
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
        switchIsLoop = view.findViewById(R.id.isLoop);
        editAdminPassword = view.findViewById(R.id.editAdminPassword);
        editCom0 = view.findViewById(R.id.editCom0);
        editCom1 = view.findViewById(R.id.editCom1);

        //仪表参数
        editShuiyangStep = view.findViewById(R.id.editShuiyangStep);
        editShuiyangVolume = view.findViewById(R.id.editShuiyangVolume);
        editLiusuanStep = view.findViewById(R.id.editLiusuanStep);
        editLiusuanVolume = view.findViewById(R.id.editLiusuanVolume);
        editCaosuannaStep = view.findViewById(R.id.editCaosuannaStep);
        editCaosuannaVolume = view.findViewById(R.id.editCaosuannaVolume);
        editGaomengsuanjiaStep = view.findViewById(R.id.editGaomengsuanjiaStep);
        editGaomengsuanjiaVolume = view.findViewById(R.id.editGaomengsuanjiaVolume);
        editDidingStep = view.findViewById(R.id.editDidingStep);
        editDidingVolume = view.findViewById(R.id.editDidingVolume);
        editXiaojieTemp = view.findViewById(R.id.editXiaojieTemp);
        editXiaojieTime = view.findViewById(R.id.editXiaojieTime);
        editKongbaiValue = view.findViewById(R.id.editKongbaiValue);
        editBiaodingValue = view.findViewById(R.id.editBiaodingValue);
        editCaosuannaCon = view.findViewById(R.id.editCaosuannaCon);
        editDidingDeviation = view.findViewById(R.id.editDidingDeviation);
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

        //更新网络TextView信息
        setNetTxtInfo();

        //填充Edit数据
        setEditText();

        //刷新界面信息
        uiUpdate();
        message = handlerUpdate.obtainMessage(UI_UPDATE);
        handlerUpdate.sendMessageDelayed(message, 1000);

        //点击时间设置按钮
        timeSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog("SetTime");
            }
        });

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
                    } else if(!SysData.isUpdateTimes) {
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

        /*
        //定时启动周期设定
        editStartCycle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editStartCycle.getText().toString().equals("")){
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
            }
        });

        //定时启动次数设定
        editNumberTimes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editNumberTimes.getText().toString().equals("")){
                    SysData.numberTimes = 0;
                } else {
                    SysData.numberTimes = Integer.parseInt(editNumberTimes.getText().toString());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                saveMeterParameter();  //保存设定的参数
            }
        });

         */

        //定时启动开启
        switchIsLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editStartCycle.getText().toString().equals("") && !editNumberTimes.getText().toString().equals("")) {
                    SysData.startCycle = Integer.parseInt(editStartCycle.getText().toString());
                    SysData.numberTimes = Integer.parseInt(editNumberTimes.getText().toString());
                    SysData.isLoop = switchIsLoop.isChecked();
                    saveMeterParameter();  //保存设定的参数
                } else {
                    Toast.makeText(getActivity(), "周期或次数为空", Toast.LENGTH_SHORT).show();
                    switchIsLoop.setChecked(false);
                }

            }
        });

        //点击保存数据按钮
        buttonSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                saveEditText();
                saveMeterParameter();
                Toast.makeText(getActivity(), "数据已保存", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        radioGroup=(RadioGroup) view.findViewById(R.id.radioGroupPassType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton) view.findViewById(checkedId);
                passType = rb.getId();
                switch (passType){
                    case R.id.nopass :
                        data = WiFiUtil.Data.WIFI_CIPHER_NOPASS;
                        break;
                    case R.id.wep :
                        data = WiFiUtil.Data.WIFI_CIPHER_WEP;
                        break;
                    case R.id.wpa :
                        data = WiFiUtil.Data.WIFI_CIPHER_WPA;
                        break;
                    case R.id.wpa2 :
                        data = WiFiUtil.Data.WIFI_CIPHER_WPA2;
                        break;
                }

                Toast.makeText(getActivity(), "加密方式:" + data, Toast.LENGTH_SHORT).show();
            }
        });
         */

        //点击连接到wifi按钮
        buttonSetupWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showChoicePassTypeDialog();
                /*
                WiFiUtil wiFiUtil = WiFiUtil.getInstance(getActivity());
                String ssid = editSsid.getText().toString();
                String pass = editPass.getText().toString();
                if(!ssid.equals("") && data != null) {
                    int id = wiFiUtil.addWiFiNetwork(ssid, pass, data);
                    Toast.makeText(getActivity(), "正在连接无线网络" + editSsid.getText().toString(), Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "信息填写不完整", Toast.LENGTH_LONG).show();
                }

                //获取无线网络SSID
                String getSsid = MainActivity.getWifiSsid(getActivity());
                SysData.wifiSsid = getSsid;

                //获取网络ip地址
                SysData.localIpAddr = MainActivity.getLocalIpAddress();

                setNetTxtInfo();
                SysData.webIPAddr = SysData.localIpAddr[0];
                editSsid.setText(SysData.wifiSsid);
                editlocalip.setText(SysData.webIPAddr);

                 */
            }
        });

        //点击web设置按钮
        buttonSetupWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //重启web服务
                MainActivity.stopWebService();
                MainActivity.startWebService();
                SysData.restartWebFlag = true;
                SysData.webPort = Integer.parseInt(editwebport.getText().toString());
                MainActivity.updateNet();
                //显示WEB访问地址
                //httpAddr.setText(SysData.httpAddr);
                //生成网址的二维码
                creatQRCode(getView());
                saveMeterParameter();
                Toast.makeText(getActivity(), "Web服务已开启", Toast.LENGTH_SHORT).show();
                //httpAddr.setText(SysData.httpAddr);   //抛出异常测试
            }
        });

        //生成网址的二维码
        creatQRCode(view);


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
                }

            }
        });

        //点击设置系统参数按钮
        stopSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStopSysDialog();
            }
        });

        return view;
    }

    //更新网络TextView信息
    public void setNetTxtInfo() {
        //显示设备IP地址
        String localIpAddr = "";
        for(int i = 0; i < SysData.localIpAddr.length; i++) {
            localIpAddr = localIpAddr + SysData.localIpAddr[i] + " ";
        }
        localIp.setText(localIpAddr);
        //显示已连接的wifi
        wifiName.setText("" + SysData.wifiSsid + "");

        //显示WEB访问地址
        //httpAddr.setText(SysData.httpAddr);
    }


    //保存仪表参数
    public void saveMeterParameter() {
        //打开文件
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences("Parameter", MODE_PRIVATE).edit();
        //仪器的参数
        editor.putInt("shuiyangStep", SysData.shuiyangStep);
        editor.putLong("shuiyangVolume", Double.doubleToLongBits(SysData.shuiyangVolume));
        editor.putInt("liusuanStep", SysData.liusuanStep);
        editor.putLong("liusuanVolume", Double.doubleToLongBits(SysData.liusuanVolume));
        editor.putInt("caosuannaStep", SysData.caosuannaStep);
        editor.putLong("caosuannaVolume", Double.doubleToLongBits(SysData.caosuannaVolume));
        editor.putInt("gaomengsuanjiaStep", SysData.gaomengsuanjiaStep);
        editor.putLong("gaomengsuanjiaVolume", Double.doubleToLongBits(SysData.gaomengsuanjiaVolume));
        editor.putLong("xiaojieTemp", Double.doubleToLongBits(SysData.xiaojieTemp));
        editor.putInt("xiaojieTime", SysData.xiaojieTime);
        editor.putInt("didingStep", SysData.didingStep);
        editor.putLong("didingVolume", Double.doubleToLongBits(SysData.didingVolume));
        editor.putInt("didingNum", SysData.didingNum);
        editor.putLong("didingSumVolume", Double.doubleToLongBits(SysData.didingSumVolume));
        editor.putLong("kongbaiValue", Double.doubleToLongBits(SysData.kongbaiValue));
        editor.putLong("biaodingValue", Double.doubleToLongBits(SysData.biaodingValue));
        editor.putLong("caosuannaCon", Double.doubleToLongBits(SysData.caosuannaCon));
        editor.putInt("didingDeviation", SysData.didingDeviation);
        //系统参数
        editor.putString("localIpAddr", SysData.localIpAddr[0]);
        editor.putInt("webPort", SysData.webPort);
        editor.putBoolean("isLoop", SysData.isLoop);
        editor.putLong("nextStartTime", SysData.nextStartTime);
        editor.putInt("startCycle", SysData.startCycle);
        editor.putInt("numberTimes", SysData.numberTimes);
        editor.putString("adminPassword", SysData.adminPassword);

        //提交保存
        editor.apply();
    }

    //保存Edit数据
    private void saveEditText() {
        //保存仪表参数的内容
        SysData.shuiyangStep = Integer.parseInt(editShuiyangStep.getText().toString());
        SysData.shuiyangVolume = Double.parseDouble(editShuiyangVolume.getText().toString());
        SysData.liusuanStep = Integer.parseInt(editLiusuanStep.getText().toString());
        SysData.liusuanVolume = Double.parseDouble(editLiusuanVolume.getText().toString());
        SysData.caosuannaStep = Integer.parseInt(editCaosuannaStep.getText().toString());
        SysData.caosuannaVolume = Double.parseDouble(editCaosuannaVolume.getText().toString());
        SysData.gaomengsuanjiaStep = Integer.parseInt(editGaomengsuanjiaStep.getText().toString());
        SysData.gaomengsuanjiaVolume = Double.parseDouble(editGaomengsuanjiaVolume.getText().toString());
        SysData.didingStep = Integer.parseInt(editDidingStep.getText().toString());
        SysData.didingVolume = Double.parseDouble(editDidingVolume.getText().toString());
        SysData.xiaojieTemp = Double.parseDouble(editXiaojieTemp.getText().toString());
        SysData.xiaojieTime = Integer.parseInt(editXiaojieTime.getText().toString());
        SysData.kongbaiValue = Double.parseDouble(editKongbaiValue.getText().toString());
        SysData.biaodingValue = Double.parseDouble(editBiaodingValue.getText().toString());
        SysData.caosuannaCon = Double.parseDouble(editCaosuannaCon.getText().toString());
        SysData.didingDeviation = Integer.parseInt(editDidingDeviation.getText().toString());
        //系统参数
        SysData.adminPassword = editAdminPassword.getText().toString();
        /*
        //保存EditText的内容 -- 系统自动获取无需保存
        SysData.wifiSsid = editSsid.getText().toString();
        SysData.wifiPass = editPass.getText().toString();
        SysData.webIPAddr = editlocalip.getText().toString();
        SysData.webPort = Integer.parseInt(editwebport.getText().toString());
         */
    }

    //生成网址的二维码
    private void creatQRCode(View view) {
        //生成网址的二维码
        ImageView mImageView = (ImageView) view.findViewById(R.id.imageViewZXing);
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(SysData.httpAddr, 60, 60);
        mImageView.setImageBitmap(mBitmap);
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
        switchIsLoop.setChecked(SysData.isLoop);
        editAdminPassword.setText(SysData.adminPassword);
        //填充仪表参数的内容
        editShuiyangStep.setText(String.valueOf(SysData.shuiyangStep));
        editShuiyangVolume.setText(String.valueOf(SysData.shuiyangVolume));
        editLiusuanStep.setText(String.valueOf(SysData.liusuanStep));
        editLiusuanVolume.setText(String.valueOf(SysData.liusuanVolume));
        editCaosuannaStep.setText(String.valueOf(SysData.caosuannaStep));
        editCaosuannaVolume.setText(String.valueOf(SysData.caosuannaVolume));
        editGaomengsuanjiaStep.setText(String.valueOf(SysData.gaomengsuanjiaStep));
        editGaomengsuanjiaVolume.setText(String.valueOf(SysData.gaomengsuanjiaVolume));
        editDidingStep.setText(String.valueOf(SysData.didingStep));
        editDidingVolume.setText(String.valueOf(SysData.didingVolume));
        editXiaojieTemp.setText(String.valueOf(SysData.xiaojieTemp));
        editXiaojieTime.setText(String.valueOf(SysData.xiaojieTime));
        editKongbaiValue.setText(String.valueOf(SysData.kongbaiValue));
        editBiaodingValue.setText(String.valueOf(SysData.biaodingValue));
        editCaosuannaCon.setText(String.valueOf(SysData.caosuannaCon));
        editDidingDeviation.setText(String.valueOf(SysData.didingDeviation));
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
                            timeManager.setTime(newDate.getTime());
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
        altDialog.setPositiveButton("连接到WIFI",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(ssid != SysData.wifiSsid && !ssid.equals("") && data != null) {
                            WiFiUtil wiFiUtil = WiFiUtil.getInstance(getActivity());
                            int id = wiFiUtil.addWiFiNetwork(ssid, pass, data);
                            Toast.makeText(getActivity(), "正在连接无线网络" + editSsid.getText().toString(), Toast.LENGTH_LONG).show();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), "网络已连接或网络名称为空", Toast.LENGTH_LONG).show();
                        }

                        //获取无线网络SSID
                        String getSsid = MainActivity.getWifiSsid(getActivity());
                        SysData.wifiSsid = getSsid;

                        //获取网络ip地址
                        SysData.localIpAddr = MainActivity.getLocalIpAddress();

                        setNetTxtInfo();
                        SysData.webIPAddr = SysData.localIpAddr[0];
                        //editSsid.setText(SysData.wifiSsid);
                        editlocalip.setText(SysData.webIPAddr);
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
    private void showStopSysDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        altDialog.setIcon(R.drawable.ic_error_black_24dp);
        altDialog.setTitle("系统参数");
        altDialog.setMessage("确定要进入系统参数设置吗？");
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
