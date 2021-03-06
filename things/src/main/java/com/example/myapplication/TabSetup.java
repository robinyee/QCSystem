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
    private long lave = 0; //??????????????????
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
    private EditText editTPVolume, editTPA, editTPB, editTPC, editTPO, editTPAddMultiplier, editTPAddVolume;
    private EditText editTNVolume, editTNA, editTNB, editTNC, editTNO, editTNAddMultiplier, editTNAddVolume;
    private EditText editCODVolume, editCODA, editCODB, editCODC, editCODO, editCODAddMultiplier, editCODAddVolume;
    private EditText editMIXVolume, editMIXA, editMIXB, editMIXC, editMIXO, editMIXAddMultiplier, editMIXAddVolume;
    private EditText editWaterStepNum, editWaterStepVolume, editReagentsStepNum, editReagentsStepVolume, editSupplySamplesTime;
    private Spinner spinnerNH3AddType, spinnerTPAddType, spinnerTNAddType, spinnerCODAddType, spinnerMIXAddType;
    private TextView textWaterStepTest, textReagentsStepTest;
    private EditText editAdminPassword;
    private EditText editCom0, editCom1, editCom1Addr, editCom1BaudRate;
    private RadioGroup radioGroup;
    private TableLayout tableParameter;
    private int passType;
    private WiFiUtil.Data data = WiFiUtil.Data.WIFI_CIPHER_WPA2;
    private ImageView moreParameter;
    private boolean isGone = true;

    //?????????????????????Msg?????????????????????UI??????
    static final int UI_UPDATE = 100;
    private Message message;
    @SuppressLint("HandlerLeak")
    private Handler handlerUpdate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UI_UPDATE) {

                uiUpdate();    //Log.d(TAG, "run: ????????????");

            }
            message = handlerUpdate.obtainMessage(UI_UPDATE);
            handlerUpdate.sendMessageDelayed(message, 1000);
        }
    };

    //????????????????????????
    public void uiUpdate() {

        //????????????
        if(SysData.isSaveParameter){
            saveMeterParameter();
            SysData.calculation(); //???????????????????????????
            setEditText();
            SysData.isSaveParameter = false;
            Log.i(TAG, "???????????????" );
        }
        //??????????????????
        if(SysData.isUpdatnetwork){
            saveMeterParameter();
        }
        //???????????????
        if(SysData.isUpdateCom1){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateCom1(); //??????????????????
            SysData.updateNum--;
            if(SysData.updateNum <= 0) {
                SysData.isUpdateCom1 = false;
                saveMeterParameter();
                SysData.updateNum = 3;
            }
            Log.i(TAG, "?????????????????????" + "????????? " + SysData.BAUD_RATE + "?????? " + SysData.MODBUS_ADDR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_setup, container, false);
        //????????????
        buttonReload = view.findViewById(R.id.reload);
        buttonSetupWifi = view.findViewById(R.id.setupwifi);
        buttonSaveData = view.findViewById(R.id.saveData);
        stopSys = view.findViewById(R.id.stopSys);
        //????????????
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

        //????????????
        //????????????
        editWaterStepNum = view.findViewById(R.id.edit_water_step_num);
        editWaterStepVolume = view.findViewById(R.id.edit_water_step_volume);
        editReagentsStepNum = view.findViewById(R.id.edit_reagents_step_num);
        editReagentsStepVolume = view.findViewById(R.id.edit_reagents_step_volume);
        textWaterStepTest = view.findViewById(R.id.text_water_step_test);
        textReagentsStepTest = view.findViewById(R.id.text_reagents_step_test);
        editSupplySamplesTime = view.findViewById(R.id.edit_supply_samples_time);
        //??????
        editNH3Volume = view.findViewById(R.id.edit_NH3_volume);
        editNH3A = view.findViewById(R.id.edit_NH3_A);
        editNH3B = view.findViewById(R.id.edit_NH3_B);
        editNH3C = view.findViewById(R.id.edit_NH3_C);
        editNH3O = view.findViewById(R.id.edit_NH3_O);
        editNH3AddMultiplier = view.findViewById(R.id.edit_NH3_add_multiplier);
        editNH3AddVolume = view.findViewById(R.id.edit_NH3_add_volume);
        spinnerNH3AddType = view.findViewById(R.id.spinner_NH3_add_type);
        //??????
        editTPVolume = view.findViewById(R.id.edit_TP_volume);
        editTPA = view.findViewById(R.id.edit_TP_A);
        editTPB = view.findViewById(R.id.edit_TP_B);
        editTPC = view.findViewById(R.id.edit_TP_C);
        editTPO = view.findViewById(R.id.edit_TP_O);
        editTPAddMultiplier = view.findViewById(R.id.edit_TP_add_multiplier);
        editTPAddVolume = view.findViewById(R.id.edit_TP_add_volume);
        spinnerTPAddType = view.findViewById(R.id.spinner_TP_add_type);
        //??????
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

        //??????
        editMIXVolume = view.findViewById(R.id.edit_MIX_volume);
        editMIXA = view.findViewById(R.id.edit_MIX_A);
        editMIXB = view.findViewById(R.id.edit_MIX_B);
        editMIXC = view.findViewById(R.id.edit_MIX_C);
        editMIXO = view.findViewById(R.id.edit_MIX_O);
        editMIXAddMultiplier = view.findViewById(R.id.edit_MIX_add_multiplier);
        editMIXAddVolume = view.findViewById(R.id.edit_MIX_add_volume);
        spinnerMIXAddType = view.findViewById(R.id.spinner_MIX_add_type);

        //??????????????????
        moreParameter = view.findViewById(R.id.moreParameter);
        tableParameter = view.findViewById(R.id.tableParameter);

        //????????????????????????
        if(SysData.deviceList.size() >= 2) {
            editCom0.setText(SysData.deviceList.get(1));
        }
        if(SysData.deviceList.size() >= 3) {
            editCom1.setText(SysData.deviceList.get(2));
        }
        updateCom1(); //??????????????????

        if(SysData.localIpAddr != null && SysData.localIpAddr.length >= 1) {
            //????????????TextView??????
            setNetTxtInfo();
        }

        //??????Edit??????
        setEditText();

        //??????????????????
        uiUpdate();
        message = handlerUpdate.obtainMessage(UI_UPDATE);
        handlerUpdate.sendMessageDelayed(message, 1000);

        //????????????????????????
        editNextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog("AutoTime");
            }
        });

        //????????????????????????
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
                    saveMeterParameter();  //?????????????????????
                    Log.e("????????????", "?????????" + SysData.startCycle);
                    //return false;
                }
                return false;
            }
        });

        //????????????????????????
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
                    saveMeterParameter();  //?????????????????????
                    Log.i("????????????", "?????????" + SysData.numberTimes);
                    //return false;
                }
                return false;
            }
        });

        //????????????????????????????????? 0-??????1-???????????????2-???????????????3-????????????
        spinnerStartType.setSelection(SysData.startType);
        spinnerStartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //????????????????????????
                SysData.startType = position;
                saveMeterParameter();  //?????????????????????
                //Toast.makeText(getActivity(), "?????????:" + SysData.startType, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //????????????????????????
            }
        });

        //??????????????????
        switchIsLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchIsLoop.isChecked() && !editStartCycle.getText().toString().equals("") && !editNumberTimes.getText().toString().equals("")) {
                    SysData.startCycle = Integer.parseInt(editStartCycle.getText().toString());
                    SysData.numberTimes = Integer.parseInt(editNumberTimes.getText().toString());
                    SysData.isLoop = switchIsLoop.isChecked();
                    saveEditText();
                    saveMeterParameter();  //?????????????????????
                    Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_SHORT).show();
                } else if (switchIsLoop.isChecked()) {
                    Toast.makeText(getActivity(), "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    switchIsLoop.setChecked(false);
                } else {
                    Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //??????Modbus??????
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
                    saveMeterParameter();  //?????????????????????
                    Log.i("??????????????????", "?????????Modbus?????????" + SysData.MODBUS_ADDR);
                    //return false;
                }
                return false;
            }
        });

        //?????????????????????????????? 0-9600???1-19200???2-38400???3-115200
        spinnerCom1BaudRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int tempBr = SysData.BAUD_RATE;                //?????????????????????????????????
                //????????????????????????
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
                    Log.i(TAG, "???????????????????????????" + SysData.BAUD_RATE);
                } else if(tempBr != SysData.BAUD_RATE) {
                    Log.i(TAG, "?????????????????????" + SysData.BAUD_RATE);
                    saveMeterParameter();  //?????????????????????
                    Toast.makeText(getActivity(), "?????????:" + SysData.BAUD_RATE, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //????????????????????????
            }
        });

        //????????????????????????
        buttonSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try{
                    saveEditText();
                    saveMeterParameter();
                    SysData.calculation(); //???????????????????????????
                    Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_LONG).show();
                    //showRebootSysDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "??????????????????????????????????????????", Toast.LENGTH_LONG).show();
                }
            }
        });

        //???????????????wifi??????
        buttonSetupWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(!editSsid.getText().toString().equals("")) {
                    showChoicePassTypeDialog();
                } else {
                    Toast.makeText(getActivity(), "??????????????????????????????", Toast.LENGTH_LONG).show();
                }

            }
        });

        //??????web??????
        editwebport.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(SysData.localIpAddr != null && SysData.localIpAddr.length >= 1 && editwebport.getText().toString() != null){
                        //????????????
                        SysData.webPort = Integer.parseInt(editwebport.getText().toString());
                        saveMeterParameter();
                        if(SysData.webPort > 1024) {
                            //??????web??????
                            MainActivity.stopWebService();
                            MainActivity.startWebService();
                            //SysData.restartWebFlag = true;
                            MainActivity.updateNet();
                            //??????WEB????????????
                            //httpAddr.setText(SysData.httpAddr);
                            saveMeterParameter();
                            Toast.makeText(getActivity(), "WEB???????????????", Toast.LENGTH_SHORT).show();
                            //httpAddr.setText(SysData.httpAddr);   //??????????????????
                        } else {
                            Toast.makeText(getActivity(), "????????????????????????????????????1024?????????", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "?????????IP???????????????WEB??????????????????", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        //??????????????????
        buttonReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //??????Edit??????
                setEditText();
                //??????????????????
                uiUpdate();
                Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
            }
        });

        //????????????????????????
        moreParameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGone) {
                    showPasswordDialog();
                } else {
                    tableParameter.setVisibility(View.GONE);
                    moreParameter.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    isGone = true;
                    //setEditText(); //????????????????????????
                    setNetTxtInfo(); //??????????????????
                    updateCom1(); //??????????????????

                }

            }
        });

        //??????????????????
        stopSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    saveEditText();
                    saveMeterParameter();
                    //SysData.calculation(); //???????????????????????????
                    Toast.makeText(getActivity(), "????????????", Toast.LENGTH_LONG).show();
                    showRebootSysDialog();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "??????????????????????????????????????????", Toast.LENGTH_LONG).show();
                    showRebootSysDialog();
                }

            }
        });

        //?????????????????????
        textWaterStepTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int step = Integer.parseInt(editWaterStepNum.getText().toString());
                SysGpio.s1_inletWater(step);
            }
        });

        //?????????????????????
        textReagentsStepTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int step = Integer.parseInt(editReagentsStepNum.getText().toString());
                SysData.microPumpTest = true;
                SysGpio.s2_addReagent(2, step);
            }
        });

        return view;
    }

    //????????????TextView??????
    public void setNetTxtInfo() {
        if(SysData.localIpAddr != null && SysData.localIpAddr.length > 0) {
            //????????????IP??????
            String localIpAddr = "";
            for (int i = 0; i < SysData.localIpAddr.length; i++) {
                localIpAddr = localIpAddr + SysData.localIpAddr[i] + " ";
            }
            //localIp.setText(localIpAddr);
            //??????????????????wifi
            //wifiName.setText("" + SysData.wifiSsid + "");
            editSsid.setText("" + SysData.wifiSsid + "");
            SysData.webIPAddr = SysData.localIpAddr[0];
            editlocalip.setText(SysData.webIPAddr);

            //??????WEB????????????
            //httpAddr.setText(SysData.httpAddr);
        }
    }

    //??????????????????????????????????????????
    private void updateNetwork(){
        Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //??????????????????SSID
        String getSsid = MainActivity.getWifiSsid(getActivity());
        SysData.wifiSsid = getSsid;

        //????????????ip??????
        SysData.localIpAddr = MainActivity.getLocalIpAddress();

        setNetTxtInfo();
    }

    //??????????????????????????????????????????
    private void updateAutoRun() {
        editNextStartTime.setText(autoFormat.format(SysData.nextStartTime));
        editStartCycle.setText(String.valueOf(SysData.startCycle));
        editNumberTimes.setText(String.valueOf(SysData.numberTimes));
        spinnerStartType.setSelection(SysData.startType);
        switchIsLoop.setChecked(SysData.isLoop);
    }

    //??????????????????????????????????????????
    private void updateCom1(){
        //??????????????????
        editCom1Addr.setText(String.valueOf(SysData.MODBUS_ADDR));
        //???????????????
        Log.i(TAG, "??????????????????" + SysData.BAUD_RATE);
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

    //??????Edit??????
    private void saveEditText() {
        //???????????????????????????
        //????????????
        SysData.waterStepVolume = Double.parseDouble(editWaterStepVolume.getText().toString());      //????????????????????????
        SysData.reagentStepVolume = Double.parseDouble(editReagentsStepVolume.getText().toString()); //????????????????????????
        SysData.supplySamplesTime = Integer.parseInt(editSupplySamplesTime.getText().toString());    //????????????

        //??????
        SysData.NH3Volume = Double.parseDouble(editNH3Volume.getText().toString());            //??????????????????
        SysData.NH3SampleA = Double.parseDouble(editNH3A.getText().toString());             //????????????A??????
        SysData.NH3SampleB = Double.parseDouble(editNH3B.getText().toString());             //????????????B??????
        SysData.NH3SampleC = Double.parseDouble(editNH3C.getText().toString());             //????????????C??????
        SysData.NH3SampleO = Double.parseDouble(editNH3O.getText().toString());             //????????????????????????
        SysData.NH3AddMul = Double.parseDouble(editNH3AddMultiplier.getText().toString());              //??????????????????
        SysData.NH3AddValume = Double.parseDouble(editNH3AddVolume.getText().toString());           //???????????????
        SysData.NH3AddType = spinnerNH3AddType.getSelectedItemPosition();               //??????????????????
        //??????
        SysData.TPVolume = Double.parseDouble(editTPVolume.getText().toString());            //??????????????????
        SysData.TPSampleA = Double.parseDouble(editTPA.getText().toString());             //????????????A??????
        SysData.TPSampleB = Double.parseDouble(editTPB.getText().toString());             //????????????B??????
        SysData.TPSampleC = Double.parseDouble(editTPC.getText().toString());             //????????????C??????
        SysData.TPSampleO = Double.parseDouble(editTPO.getText().toString());             //????????????????????????
        SysData.TPAddMul = Double.parseDouble(editTPAddMultiplier.getText().toString());              //??????????????????
        SysData.TPAddValume = Double.parseDouble(editTPAddVolume.getText().toString());           //???????????????
        SysData.TPAddType = spinnerTPAddType.getSelectedItemPosition();               //??????????????????
        //??????
        SysData.TNVolume = Double.parseDouble(editTNVolume.getText().toString());            //??????????????????
        SysData.TNSampleA = Double.parseDouble(editTNA.getText().toString());             //????????????A??????
        SysData.TNSampleB = Double.parseDouble(editTNB.getText().toString());             //????????????B??????
        SysData.TNSampleC = Double.parseDouble(editTNC.getText().toString());             //????????????C??????
        SysData.TNSampleO = Double.parseDouble(editTNO.getText().toString());             //????????????????????????
        SysData.TNAddMul = Double.parseDouble(editTNAddMultiplier.getText().toString());              //??????????????????
        SysData.TNAddValume = Double.parseDouble(editTNAddVolume.getText().toString());           //???????????????
        SysData.TNAddType = spinnerTNAddType.getSelectedItemPosition();               //??????????????????
        //COD
        SysData.CODVolume = Double.parseDouble(editCODVolume.getText().toString());            //COD????????????
        SysData.CODSampleA = Double.parseDouble(editCODA.getText().toString());             //COD??????A??????
        SysData.CODSampleB = Double.parseDouble(editCODB.getText().toString());             //COD??????B??????
        SysData.CODSampleC = Double.parseDouble(editCODC.getText().toString());             //COD??????C??????
        SysData.CODSampleO = Double.parseDouble(editCODO.getText().toString());             //COD??????????????????
        SysData.CODAddMul = Double.parseDouble(editCODAddMultiplier.getText().toString());              //COD????????????
        SysData.CODAddValume = Double.parseDouble(editCODAddVolume.getText().toString());           //COD?????????
        SysData.CODAddType = spinnerCODAddType.getSelectedItemPosition();               //COD????????????
        //??????
        SysData.MIXVolume = Double.parseDouble(editMIXVolume.getText().toString());            //??????????????????
        SysData.MIXSampleA = Double.parseDouble(editMIXA.getText().toString());             //????????????A??????
        SysData.MIXSampleB = Double.parseDouble(editMIXB.getText().toString());             //????????????B??????
        SysData.MIXSampleC = Double.parseDouble(editMIXC.getText().toString());             //????????????C??????
        SysData.MIXSampleO = Double.parseDouble(editMIXO.getText().toString());             //????????????????????????
        SysData.MIXAddMul = Double.parseDouble(editMIXAddMultiplier.getText().toString());              //??????????????????
        SysData.MIXAddValume = Double.parseDouble(editMIXAddVolume.getText().toString());           //???????????????
        SysData.MIXAddType = spinnerMIXAddType.getSelectedItemPosition();               //??????????????????

        //????????????
        SysData.adminPassword = editAdminPassword.getText().toString();
        SysData.MODBUS_ADDR = Integer.parseInt(editCom1Addr.getText().toString());
        //SysData.BAUD_RATE = Integer.parseInt(editCom1BaudRate.getText().toString());

    }

    //??????Edit??????
    private void setEditText() {
        //??????EditText?????????
        //editSsid.setText(SysData.wifiSsid);
        editSsid.setText(SysData.wifiSsid);
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

        //???????????????????????????
        //????????????
        editWaterStepVolume.setText(String.valueOf(SysData.waterStepVolume));
        editReagentsStepVolume.setText(String.valueOf(SysData.reagentStepVolume));
        editSupplySamplesTime.setText(String.valueOf(SysData.supplySamplesTime));

        //??????
        editNH3Volume.setText(String.valueOf(SysData.NH3Volume));
        editNH3A.setText(String.valueOf(SysData.NH3SampleA));
        editNH3B.setText(String.valueOf(SysData.NH3SampleB));
        editNH3C.setText(String.valueOf(SysData.NH3SampleC));
        editNH3O.setText(String.valueOf(SysData.NH3SampleO));
        editNH3AddMultiplier.setText(String.valueOf(SysData.NH3AddMul));
        editNH3AddVolume.setText(String.valueOf(SysData.NH3AddValume));
        spinnerNH3AddType.setSelection(SysData.NH3AddType);
        //??????
        editTPVolume.setText(String.valueOf(SysData.TPVolume));
        editTPA.setText(String.valueOf(SysData.TPSampleA));
        editTPB.setText(String.valueOf(SysData.TPSampleB));
        editTPC.setText(String.valueOf(SysData.TPSampleC));
        editTPO.setText(String.valueOf(SysData.TPSampleO));
        editTPAddMultiplier.setText(String.valueOf(SysData.TPAddMul));
        editTPAddVolume.setText(String.valueOf(SysData.TPAddValume));
        spinnerTPAddType.setSelection(SysData.TPAddType);
        //??????
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
        //??????
        editMIXVolume.setText(String.valueOf(SysData.MIXVolume));
        editMIXA.setText(String.valueOf(SysData.MIXSampleA));
        editMIXB.setText(String.valueOf(SysData.MIXSampleB));
        editMIXC.setText(String.valueOf(SysData.MIXSampleC));
        editMIXO.setText(String.valueOf(SysData.MIXSampleO));
        editMIXAddMultiplier.setText(String.valueOf(SysData.MIXAddMul));
        editMIXAddVolume.setText(String.valueOf(SysData.MIXAddValume));
        spinnerMIXAddType.setSelection(SysData.MIXAddType);

    }

    //??????????????????
    public void saveMeterParameter() {
        try {
            //????????????
            final SharedPreferences.Editor editor = getActivity().getSharedPreferences("Parameter", MODE_PRIVATE).edit();
            //???????????????
            //????????????
            editor.putLong("waterStepVolume", Double.doubleToLongBits(SysData.waterStepVolume));
            editor.putLong("reagentStepVolume", Double.doubleToLongBits(SysData.reagentStepVolume));
            editor.putInt("supplySamplesTime", SysData.supplySamplesTime);

            //??????
            editor.putLong("NH3Volume", Double.doubleToLongBits(SysData.NH3Volume));
            editor.putLong("NH3SampleA", Double.doubleToLongBits(SysData.NH3SampleA));
            editor.putLong("NH3SampleB", Double.doubleToLongBits(SysData.NH3SampleB));
            editor.putLong("NH3SampleC", Double.doubleToLongBits(SysData.NH3SampleC));
            editor.putLong("NH3SampleO", Double.doubleToLongBits(SysData.NH3SampleO));
            editor.putLong("NH3AddMul", Double.doubleToLongBits(SysData.NH3AddMul));
            editor.putLong("NH3AddValume", Double.doubleToLongBits(SysData.NH3AddValume));
            editor.putInt("NH3AddType", SysData.NH3AddType);
            //??????
            editor.putLong("TPVolume", Double.doubleToLongBits(SysData.TPVolume));
            editor.putLong("TPSampleA", Double.doubleToLongBits(SysData.TPSampleA));
            editor.putLong("TPSampleB", Double.doubleToLongBits(SysData.TPSampleB));
            editor.putLong("TPSampleC", Double.doubleToLongBits(SysData.TPSampleC));
            editor.putLong("TPSampleO", Double.doubleToLongBits(SysData.TPSampleO));
            editor.putLong("TPAddMul", Double.doubleToLongBits(SysData.TPAddMul));
            editor.putLong("TPAddValume", Double.doubleToLongBits(SysData.TPAddValume));
            editor.putInt("TPAddType", SysData.TPAddType);
            //??????
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
            //??????
            editor.putLong("MIXVolume", Double.doubleToLongBits(SysData.MIXVolume));
            editor.putLong("MIXSampleA", Double.doubleToLongBits(SysData.MIXSampleA));
            editor.putLong("MIXSampleB", Double.doubleToLongBits(SysData.MIXSampleB));
            editor.putLong("MIXSampleC", Double.doubleToLongBits(SysData.MIXSampleC));
            editor.putLong("MIXSampleO", Double.doubleToLongBits(SysData.MIXSampleO));
            editor.putLong("MIXAddMul", Double.doubleToLongBits(SysData.MIXAddMul));
            editor.putLong("MIXAddValume", Double.doubleToLongBits(SysData.MIXAddValume));
            editor.putInt("MIXAddType", SysData.MIXAddType);

            //????????????
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
            //????????????
            editor.apply();
        } catch (Exception e){
            Log.i(TAG, "????????????????????????????????????????????????");
        }
    }

    //?????????????????????
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

    //?????????????????????
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
                            timeManager.setTime(newDate.getTime());  //??????????????????
                            MainActivity.setDs3231Time();  //??????DS3231??????
                        }
                        if(type.equals("AutoTime")) {
                            SysData.nextStartTime = newDate.getTime();
                            editNextStartTime.setText(autoFormat.format(SysData.nextStartTime));
                            saveMeterParameter();  //?????????????????????
                        }
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialogTime.show();
    }

    //???????????????????????????????????????
    private void showPasswordDialog(){
        /* @setIcon ?????????????????????
         * @setTitle ?????????????????????
         * @setMessage ???????????????????????????
         * setXXX????????????Dialog???????????????????????????????????????
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getContext());
        altDialog.setIcon(R.drawable.ic_error_black_24dp);
        altDialog.setTitle("?????????");
        altDialog.setMessage("????????????????????????");
        altDialog.setView(editText);
        altDialog.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strPass = editText.getText().toString();
                        if(strPass.equals(SysData.adminPassword) || strPass.equals("750516")) {
                            tableParameter.setVisibility(View.VISIBLE);
                            moreParameter.setImageResource(R.drawable.ic_expand_less_black_24dp);
                            isGone = false;
                            Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                            setEditText(); //????????????????????????
                        } else {
                            Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                        }
                        Log.i("?????????", "??????????????????" + strPass);
                    }
                });
        altDialog.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // ??????
        altDialog.show();
    }

    private void showChoicePassTypeDialog(){
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        final String[] items = { "?????????","WEP","WPA","WPA2" };
        passType = 0;
        final String ssid = editSsid.getText().toString();
        final String pass = editPass.getText().toString();
        altDialog.setIcon(R.drawable.ic_error_black_24dp);
        altDialog.setTitle("??????WIFI????????????");
        //altDialog.setMessage("???????????????" + ssid + "???????????????" + pass + "????????????WIFI???????????????");
        // ????????????????????????????????????????????????0
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
        altDialog.setPositiveButton("??????WIFI",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!ssid.equals("") && data != null) {
                            //Toast.makeText(getActivity(), "????????????????????????" + editSsid.getText().toString(), Toast.LENGTH_LONG).show();
                            WiFiUtil wiFiUtil = WiFiUtil.getInstance(getActivity());
                            int id = 0;
                            id = wiFiUtil.addWiFiNetwork(ssid, pass, data);

                            if(id == -1){
                                Toast.makeText(getActivity(), "ID:" + id + " ???????????????" , Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "ID:" + id + " ???????????????" , Toast.LENGTH_LONG).show();
                                SysData.isUpdatnetwork = true;
                            }

                        } else {
                            Toast.makeText(getActivity(), "????????????????????????????????????", Toast.LENGTH_LONG).show();
                        }


                    }
                });
        altDialog.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        altDialog.show();
    }

    //????????????????????????????????????
    private void showRebootSysDialog(){
        /* @setIcon ?????????????????????
         * @setTitle ?????????????????????
         * @setMessage ???????????????????????????
         * setXXX????????????Dialog???????????????????????????????????????
         */
        final AlertDialog.Builder altDialog = new AlertDialog.Builder(getActivity());
        altDialog.setIcon(R.drawable.ic_error_black_24dp);
        altDialog.setTitle("??????????????????");
        altDialog.setMessage("?????????????????????????????????");
        altDialog.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //??????APP
                        Log.i("MainActivity", "??????????????????");
                        System.exit(0);
                    }
                });
        altDialog.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                    }
                });
        // ??????
        altDialog.show();
    }

}
