package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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
    private Button timeSetup, buttonSetupWeb, buttonSetupWifi, buttonSaveData;
    private TimeManager timeManager = TimeManager.getInstance();
    private TextView httpAddr, localIp;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH时mm分ss秒");
    private SimpleDateFormat autoFormat = new SimpleDateFormat("MM/dd HH:mm");
    private EditText editSsid, editPass, editlocalip, editwebport;
    private EditText editNextStartTime, editStartCycle;
    private Switch switchIsLoop;
    private EditText editShuiyangStep,editShuiyangVolume,editLiusuanStep,editLiusuanVolume,editCaosuannaStep,editCaosuannaVolume;
    private EditText editGaomengsuanjiaStep,editGaomengsuanjiaVolume,editDidingStep,editDidingVolume,editXiaojieTemp,editXiaojieTime;
    private EditText editKongbaiValue, editBiaodingValue, editCaosuannaCon;
    private RadioGroup radioGroup;
    private int passType;
    private WiFiUtil.Data data = WiFiUtil.Data.WIFI_CIPHER_WPA2;

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
        txtSysTime.setText(timeFormat.format(System.currentTimeMillis()));
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
        httpAddr = view.findViewById(R.id.httpaddr);
        localIp = view.findViewById(R.id.localip);
        txtSysDate = view.findViewById(R.id.sysdate);
        txtSysTime = view.findViewById(R.id.systime);
        wifiName = view.findViewById(R.id.wifissid);
        buttonSetupWeb = view.findViewById(R.id.setupweb);
        buttonSetupWifi = view.findViewById(R.id.setupwifi);
        buttonSaveData = view.findViewById(R.id.saveData);
        //系统参数
        editSsid = view.findViewById(R.id.editssid);
        editPass = view.findViewById(R.id.editwifipassword);
        editlocalip = view.findViewById(R.id.editlocalip);
        editwebport = view.findViewById(R.id.editwebport);
        editNextStartTime = view.findViewById(R.id.nextStartTime);
        editStartCycle = view.findViewById(R.id.startCycle);
        switchIsLoop = view.findViewById(R.id.isLoop);
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
        editStartCycle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                SysData.startCycle = Integer.parseInt(editStartCycle.getText().toString());
            }
        });

        //定时启动开启
        switchIsLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editStartCycle.getText().toString().equals("")) {
                    SysData.startCycle = Integer.parseInt(editStartCycle.getText().toString());
                }
                SysData.isLoop = switchIsLoop.isChecked();
                saveMeterParameter();  //保存设定的参数
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

        //点击连接到wifi按钮
        buttonSetupWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
            }
        });

        //点击web设置按钮
        buttonSetupWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //重启web服务
                MainActivity.stopWebService();
                MainActivity.startWebService();
                saveMeterParameter();
                SysData.restartWebFlag = true;
                SysData.webPort = Integer.parseInt(editwebport.getText().toString());
                MainActivity.updateNet();
                //显示WEB访问地址
                httpAddr.setText(SysData.httpAddr);
                //生成网址的二维码
                creatQRCode(getView());
                Toast.makeText(getActivity(), "Web服务已开启", Toast.LENGTH_SHORT).show();
            }
        });

        //生成网址的二维码
        creatQRCode(view);

        return view;
    }

    //更新网络TextView信息
    public void setNetTxtInfo() {
        //显示设备IP地址
        String localIpAddr = "";
        for(int i = 0; i < SysData.localIpAddr.length; i++) {
            localIpAddr = localIpAddr + "[" + (i+1) + "] " + SysData.localIpAddr[i] + "  ";
        }
        localIp.setText(localIpAddr);
        //显示已连接的wifi
        wifiName.setText("已连接到\"" + SysData.wifiSsid + "\"");

        //显示WEB访问地址
        httpAddr.setText(SysData.httpAddr);
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
        //系统参数
        editor.putString("localIpAddr", SysData.localIpAddr[0]);
        editor.putInt("webPort", SysData.webPort);
        editor.putBoolean("isLoop", SysData.isLoop);
        editor.putLong("nextStartTime", SysData.nextStartTime);
        editor.putInt("startCycle", SysData.startCycle);

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
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(SysData.httpAddr, 80, 80);
        mImageView.setImageBitmap(mBitmap);
    }

    //填充Edit数据
    private void setEditText() {
        //填充EditText的内容
        editSsid.setText(SysData.wifiSsid);
        editPass.setText("");
        editlocalip.setText(SysData.webIPAddr);
        editwebport.setText(String.valueOf(SysData.webPort));
        editNextStartTime.setText(autoFormat.format(SysData.nextStartTime));
        editStartCycle.setText(String.valueOf(SysData.startCycle));
        switchIsLoop.setChecked(SysData.isLoop);

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
                        newDate = calendar.getTime();
                        if(type.equals("SetTime")) {
                            timeManager.setTime(newDate.getTime());
                        }
                        if(type.equals("AutoTime")) {
                            SysData.nextStartTime = newDate.getTime();
                            editNextStartTime.setText(autoFormat.format(SysData.nextStartTime));
                        }
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialogTime.show();
    }

}
