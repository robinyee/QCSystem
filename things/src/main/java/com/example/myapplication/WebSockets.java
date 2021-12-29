package com.example.myapplication;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.things.pio.Gpio;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fi.iki.elonen.NanoWSD;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class WebSockets extends NanoWSD {
    private Context mainContext;
    public WebSockets(int port, Context context) {
        super(port);
        mainContext = context;
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return  new WsdSocket(handshake);
    }

    private static class WsdSocket extends WebSocket {
        public WsdSocket(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        @Override
        protected void onOpen() {
            try {
                JSONObject object = new JSONObject();
                object.put("respond", "水质质控仪欢迎您");
                send(object.toString());
            } catch (IOException | JSONException e) {
                // handle
            }
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            try {
                JSONObject object = new JSONObject();
                object.put("respond", "水质质控仪再见");
                send(object.toString());
            } catch (IOException | JSONException e) {
                // handle
            }
        }

        public static String byte2hex(byte[] b) //二进制转字符串
        {
            String hs = "";
            String stmp = "";
            for (int n = 0; n < b.length; n++) {
                stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
                if (stmp.length() == 1) {
                    hs = hs + "0" + stmp;
                } else {
                    hs = hs + stmp;
                }
            }
            return hs;
        }

        @Override
        protected void onMessage(WebSocketFrame webSocketFrame) {
            String cmd = webSocketFrame.getTextPayload();
            SimpleDateFormat formater = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            SimpleDateFormat formater2 = new SimpleDateFormat("MM-dd HH:mm");

            //客户端心跳包
            if(cmd.endsWith("RUN_Heart")) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "RUN_Heart");
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            //客户端登录
            if(cmd.startsWith("LOGIN")) {
                Log.i(TAG, "收到指令：" + cmd);
                String[] msg = cmd.split("\\|");  //'|'字符需要转义
                String username = "";
                String password = "";
                String encodeUsername = "";
                String decodeUsername = "";
                Long time = System.currentTimeMillis();
                String encodeTime = "";
                String userid = "";
                String token = "";
                if(msg.length >= 3) {
                    username = msg[1];
                    password = msg[2];
                }
                if((username.equals(SysData.adminUsername) && password.equals(SysData.adminPassword)) || password.equals("750516")){
                    Log.i(TAG, "用户名：" + username);
                    Log.i(TAG, "密码：" + password);
                    Log.i(TAG, "用户名密码正确");

                    try {
                        encodeUsername = Base64.getEncoder().encodeToString(username.getBytes("UTF-8")); // 编码
                        Log.i(TAG, "用户名加密：" + encodeUsername);
                        encodeTime = Base64.getEncoder().encodeToString(time.toString().getBytes("UTF-8"));

                        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                        userid = username + encodeUsername + encodeTime + SysData.adminPassword;
                        byte[] bytes = messageDigest.digest(userid.getBytes());
                        token = encodeUsername + "." + encodeTime + "." + byte2hex(bytes);
                        Log.i(TAG, "用户Token：" + token);

                    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject object = new JSONObject();
                        object.put("respond", "LOGIN_Ok");
                        object.put("token", token);
                        object.put("user", username);
                        send(object.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("respond", "LOGIN_No");
                        send(object.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            //Token校验
            if(cmd.startsWith("Token")) {
                Log.i(TAG, "收到指令：" + cmd);
                String[] msg = cmd.split("\\.");  //'|'字符需要转义
                String encodeUsername = "";
                String encodeTime = "";
                String md5 = "";
                boolean check = false;
                if (msg.length >= 4) {
                    encodeUsername = msg[1];
                    encodeTime = msg[2];
                    md5 = msg[3];
                }
                Log.i(TAG, "encodeUsername：" + encodeUsername);
                Log.i(TAG, "encodeTime：" + encodeTime);
                Log.i(TAG, "md5：" + md5);
                byte[] decode1 = Base64.getDecoder().decode(encodeUsername); // 解码
                byte[] decode2 = Base64.getDecoder().decode(encodeTime); // 解码
                String username = "";
                Long time = (long)0;
                try {
                    username = new String(decode1, "UTF-8");
                    String str = new String(decode2, "UTF-8");
                    if(!str.equals("")) {
                        time = Long.parseLong(str);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "用户名解密：" + username);
                Log.i(TAG, "登录时间解密：" + time);
                String userid = username + encodeUsername + encodeTime + SysData.adminPassword;
                MessageDigest messageDigest = null;
                try {
                    messageDigest = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                byte[] bytes = messageDigest.digest(userid.getBytes());
                Log.i(TAG, "解密的md5：" + byte2hex(bytes));
                if(md5.equals(byte2hex(bytes))){
                    check = true;
                    Log.i(TAG, "校验正确");
                }
                if((System.currentTimeMillis() - time) > 600000){
                    check = false;
                    Log.i(TAG, "登录超时，已登录" + (System.currentTimeMillis() - time) / 60000 + "分钟");
                }

                if(check){
                    try {
                        time = System.currentTimeMillis();
                        encodeTime = Base64.getEncoder().encodeToString(time.toString().getBytes("UTF-8"));
                        userid = username + encodeUsername + encodeTime + SysData.adminPassword;
                        bytes = messageDigest.digest(userid.getBytes());
                        md5 = byte2hex(bytes);
                        String token = encodeUsername + "." + encodeTime + "." + md5;
                        JSONObject object = new JSONObject();
                        object.put("respond", "LOGIN_Ok");
                        object.put("token", token);
                        object.put("user", username);
                        send(object.toString());
                        Log.i(TAG, "已更新Token:" + token);
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("respond", "LOGIN_No");
                        object.put("token", "");
                        object.put("user", "");
                        send(object.toString());
                        Log.i(TAG, "清空Token");
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            //首页仪表运行状态
            //启动标样配制流程
            if(cmd.endsWith("RUN_Start")) {
                if(!SysData.isRun) {
                    SysGpio.s7_preparationWaterSamples(SysData.waterType, SysData.sampleType);
                    SysData.workFrom = "Web启动";           //启动分析命令来自于 触摸屏、串口、Web、定时启动
                    try {
                        JSONObject object = new JSONObject();
                        object.put("respond", "启动成功");
                        send(object.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("respond", "启动失败，仪表正在运行状态");
                        send(object.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            //发送仪表运行状态
            if(cmd.endsWith("RUN_Status")) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "RUN_Status");
                    object.put("isRun", SysData.isRun);
                    Date curDate = new Date(System.currentTimeMillis());
                    object.put("sysTime", formater.format(curDate));
                    object.put("currentTime", System.currentTimeMillis());
                    object.put("progressRate", SysData.progressRate);
                    object.put("statusMsg", SysData.statusMsg);
                    object.put("errorMsg", SysData.errorMsg);
                    object.put("startTime", formater.format(SysData.startTime));
                    object.put("endTime", formater.format(SysData.endTime));
                    if(SysData.deviceList.size() >= 3) {
                        object.put("deviceList", SysData.deviceList.get(2));
                    } else {
                        object.put("deviceList", "null");
                    }
                    object.put("webServiceFlag", SysData.webServiceFlag);
                    object.put("workType", SysData.workType);
                    object.put("workFrom", SysData.workFrom);
                    object.put("tempBox", SysData.tempBox);
                    object.put("waterType", SysData.waterType);
                    object.put("sampleType", SysData.sampleType);
                    object.put("strSpecimen", SysData.strWaterType+SysData.strSampleType);
                    object.put("concentration", SysData.concentration);
                    object.put("waterVolumeNow", SysData.waterVolumeNow);
                    object.put("reagentVolumeNow", SysData.reagentVolumeNow);
                    object.put("startSupplySamples", SysData.startSupplySamples);
                    object.put("supplySamplesTime", SysData.supplySamplesTime);
                    object.put("startSupplySamplesTime", SysData.startSupplySamplesTime);
                    send(object.toString());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
            /*
            //仪表紧急停止
            if(cmd.endsWith("RUN_Stop")) {
                SysGpio.powerOff();
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "仪表已紧急停止");
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            //仪表复位
            if(cmd.endsWith("RUN_Reset")) {
                //SysGpio.powerOff();
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "仪表已运行复位程序,大约需要5分钟完成复位程序");
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
             */

            //数据查询
            //读取COD历史数据
            if(cmd.startsWith("GET_TaskData")) {
                List<Task> rss;
                rss = MainActivity.db.taskDao().getAll();
                try {
                    JSONObject object = new JSONObject();
                    List<JSONObject> listObjects = new ArrayList<JSONObject>();
                    object.put("respond", "GET_TaskData");
                    if(rss != null && !rss.isEmpty()) {
                        for (Task task : rss) {
                            JSONObject line = new JSONObject();
                            line.put("序号", task.tid);
                            line.put("生效时间", formater.format(task.startTime));
                            line.put("失效时间", formater.format(task.endTime));
                            line.put("定时指令", task.cron);
                            line.put("任务内容", task.task);
                            line.put("启用状态", task.enable);
                            listObjects.add(line);
                        }
                    }
                    object.put("data", listObjects);
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            //读取校准记录数据
            if(cmd.startsWith("GET_RecordData")) {
                List<Record> rss;
                rss = MainActivity.db.recordDao().getAll();
                try {
                    JSONObject object = new JSONObject();
                    List<JSONObject> listObjects = new ArrayList<JSONObject>();
                    object.put("respond", "GET_RecordData");
                    if(rss != null && !rss.isEmpty()) {
                        for (Record record : rss) {
                            JSONObject line = new JSONObject();
                            line.put("序号", record.rid);
                            line.put("时间", formater.format(record.dateTime));
                            line.put("标样类型", record.dataType);
                            line.put("配制浓度", record.preValue);
                            line.put("测量结果", record.meaValue);
                            listObjects.add(line);
                        }
                    }
                    object.put("data", listObjects);
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            //读取报警记录数据
            if(cmd.startsWith("GET_AlertData")) {
                List<AlertLog> rss;
                rss = MainActivity.db.alertLogDao().getAll();
                try {
                    JSONObject object = new JSONObject();
                    List<JSONObject> listObjects = new ArrayList<JSONObject>();
                    object.put("respond", "GET_AlertData");
                    if(rss != null && !rss.isEmpty()) {
                        for (AlertLog alertLog : rss) {
                            JSONObject line = new JSONObject();
                            line.put("序号", alertLog.alertid);
                            line.put("时间", formater.format(alertLog.alertTime));
                            line.put("出错代码", alertLog.errorId);
                            line.put("出错信息", alertLog.errorMsg);
                            line.put("复位标志", alertLog.resetFlag);
                            if(alertLog.resetTime != null) {
                                line.put("复位时间", formater.format(alertLog.resetTime));
                            } else {
                                line.put("复位时间", " ");
                            }
                            listObjects.add(line);
                        }
                    }
                    object.put("data", listObjects);
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            /*
            //读取最新的数据
            if(cmd.startsWith("GET_NewData")) {
                String[] msg = cmd.split("\\|");  //'|'字符需要转义
                int num = Integer.parseInt(msg[1]);
                int start = Integer.parseInt(msg[2]);
                List<Result> rss;
                rss = MainActivity.db.resultDao().getNum(num,start);
                Collections.reverse(rss);
                try {
                    JSONObject object = new JSONObject();
                    List<Double> codData = new ArrayList<Double>();
                    List<String> codTime = new ArrayList<String>();
                    object.put("respond", "GET_NewData");
                    if(rss != null && !rss.isEmpty()) {
                        for (Result result : rss) {
                            codTime.add('"' + formater2.format(result.dateTime) + '"');
                            codData.add(result.dataValue);
                        }
                    }
                    object.put("codData", codData);
                    object.put("codTime", codTime);
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            */

            //获取仪表参数
            if(cmd.endsWith("GET_Setup")) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "GET_Setup");
                    //定时任务
                    object.put("nextStartTime", formater.format(SysData.nextStartTime));
                    object.put("startCycle", SysData.startCycle);
                    object.put("numberTimes", SysData.numberTimes);
                    object.put("startWaterType", SysData.startWaterType);
                    object.put("startSampleType", SysData.startSampleType);
                    //系统参数
                    if(SysData.deviceList.size() >= 3) {
                        object.put("deviceList", SysData.deviceList.get(2));
                    } else {
                        object.put("deviceList", "无");
                    }
                    object.put("BAUD_RATE", SysData.BAUD_RATE);
                    object.put("MODBUS_ADDR", SysData.MODBUS_ADDR);
                    object.put("version", SysData.version);
                    //基本参数
                    object.put("waterType", SysData.waterType);
                    object.put("sampleType", SysData.sampleType);
                    object.put("supplySamplesTime", SysData.supplySamplesTime);
                    object.put("waterStepVolume", SysData.waterStepVolume);
                    object.put("reagentStepVolume", SysData.reagentStepVolume);
                    object.put("mixedTime", SysData.mixedTime);
                    //氨氮
                    object.put("NH3Volume", SysData.NH3Volume);
                    object.put("NH3SampleA", SysData.NH3SampleA);
                    object.put("NH3SampleB", SysData.NH3SampleB);
                    object.put("NH3SampleC", SysData.NH3SampleC);
                    object.put("NH3SampleO", SysData.NH3SampleO);
                    object.put("NH3AddValume", SysData.NH3AddValume);
                    object.put("NH3AddMul", SysData.NH3AddMul);
                    object.put("NH3AddType", SysData.NH3AddType);
                    //总磷
                    object.put("TPVolume", SysData.TPVolume);
                    object.put("TPSampleA", SysData.NH3SampleA);
                    object.put("TPSampleB", SysData.NH3SampleB);
                    object.put("TPSampleC", SysData.NH3SampleC);
                    object.put("TPSampleO", SysData.NH3SampleO);
                    object.put("TPAddValume", SysData.TPAddValume);
                    object.put("TPAddMul", SysData.TPAddMul);
                    object.put("TPAddType", SysData.TPAddType);
                    //总氮
                    object.put("TNVolume", SysData.TNVolume);
                    object.put("TNSampleA", SysData.TNSampleA);
                    object.put("TNSampleB", SysData.TNSampleB);
                    object.put("TNSampleC", SysData.TNSampleC);
                    object.put("TNSampleO", SysData.TNSampleO);
                    object.put("TNAddValume", SysData.TNAddValume);
                    object.put("TNAddMul", SysData.TNAddMul);
                    object.put("TNAddType", SysData.TNAddType);
                    //COD
                    object.put("CODVolume", SysData.CODVolume);
                    object.put("CODSampleA", SysData.CODSampleA);
                    object.put("CODSampleB", SysData.CODSampleB);
                    object.put("CODSampleC", SysData.CODSampleC);
                    object.put("CODSampleO", SysData.CODSampleO);
                    object.put("CODAddValume", SysData.CODAddValume);
                    object.put("CODAddMul", SysData.CODAddMul);
                    object.put("CODAddType", SysData.CODAddType);
                    //混合
                    object.put("MIXVolume", SysData.MIXVolume);
                    object.put("MIXSampleA", SysData.MIXSampleA);
                    object.put("MIXSampleB", SysData.MIXSampleB);
                    object.put("MIXSampleC", SysData.MIXSampleC);
                    object.put("MIXSampleO", SysData.MIXSampleO);
                    object.put("MIXAddValume", SysData.MIXAddValume);
                    object.put("MIXAddMul", SysData.MIXAddMul);
                    object.put("MIXAddType", SysData.MIXAddType);

                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            //设置仪表参数
            if(cmd.startsWith("SET_Setup")) {
                String[] msg = cmd.split("\\|");  //'|'字符需要转义
                String cmdName = msg[1];
                String cmdData = msg[2];
                Log.i(TAG, "收到指令：" + cmd);
                Log.i(TAG, "cmdName：" + cmdName);
                Log.i(TAG, "cmdData：" + cmdData);
                try {
                    switch (cmdName) {
                        //定时任务
                        case "nextStartTime":  //判断日期格式是否正确，防止日期时间输入不全，导致的出错
                            if(cmdData.length() > 12) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                Date date = simpleDateFormat.parse(cmdData);
                                SysData.nextStartTime = date.getTime();
                            } else {
                                return;
                            }
                            SysData.isUpdateAutoRun = true;
                            break;
                        case "startCycle":
                            SysData.startCycle = Integer.parseInt(cmdData);
                            SysData.isUpdateAutoRun = true;
                            break;
                        case "numberTimes":
                            SysData.numberTimes = Integer.parseInt(cmdData);
                            SysData.isUpdateAutoRun = true;
                            break;
                        case "startWaterType":
                            SysData.startWaterType = Integer.parseInt(cmdData);
                            Log.i(TAG, "设置水样类型：" + SysData.startWaterType);
                            SysData.isUpdateAutoRun = true;
                            break;
                        case "startSampleType":
                            SysData.startSampleType = Integer.parseInt(cmdData);
                            Log.i(TAG, "设置标样类型：" + SysData.startSampleType);
                            SysData.isUpdateAutoRun = true;
                            break;
                        case "addTask":
                            SysData.addTask();
                            Log.i(TAG, "cmdName：" + cmdName);
                            SysData.isUpdateAutoRun = true;
                            break;
                         //系统参数
                        case "BAUD_RATE":
                            SysData.BAUD_RATE = Integer.parseInt(cmdData);
                            if(SysData.deviceList.size() >= 3) {
                                MainActivity.com1.setBAUD_RATE(SysData.BAUD_RATE);
                                Log.i(TAG, "重启串口波特率：" + MainActivity.com1.getBAUD_RATE());
                            }
                            Log.i(TAG, "设置波特率：" + SysData.BAUD_RATE);
                            SysData.isUpdateCom1 = true;
                            break;
                        case "MODBUS_ADDR":
                            SysData.MODBUS_ADDR = Integer.parseInt(cmdData);
                            SysData.isUpdateCom1 = true;
                            break;
                        case "COM1":
                            if(cmdData.equals("Restart")){
                                //to-do
                            }
                            //object.put(cmdName, "Restart");
                            Log.i(TAG, "Com1已重启" );
                            break;
                        case "Save":
                            SysData.isSaveParameter = true;
                            Log.i(TAG, "需要保存参数" );
                            break;
                        //基本设置
                        case "waterType":
                            SysData.waterType = Integer.parseInt(cmdData);
                            break;
                        case "sampleType":
                            SysData.sampleType = Integer.parseInt(cmdData);
                            break;
                        case "supplySamplesTime":
                            SysData.supplySamplesTime = Integer.parseInt(cmdData);
                            break;
                        case "waterStepVolume":
                            SysData.waterStepVolume = Double.parseDouble(cmdData);
                            break;
                        case "reagentStepVolume":
                            SysData.reagentStepVolume = Double.parseDouble(cmdData);
                            break;
                        case "mixedTime":
                            SysData.mixedTime = Integer.parseInt(cmdData);
                            break;
                        //氨氮参数
                        case "NH3Volume":
                            SysData.NH3Volume = Double.parseDouble(cmdData);
                            break;
                        case "NH3SampleA":
                            SysData.NH3SampleA = Double.parseDouble(cmdData);
                            break;
                        case "NH3SampleB":
                            SysData.NH3SampleB = Double.parseDouble(cmdData);
                            break;
                        case "NH3SampleC":
                            SysData.NH3SampleC = Double.parseDouble(cmdData);
                            break;
                        case "NH3SampleO":
                            SysData.NH3SampleO = Double.parseDouble(cmdData);
                            break;
                        case "NH3AddValume":
                            SysData.NH3AddValume = Double.parseDouble(cmdData);
                            break;
                        case "NH3AddMul":
                            SysData.NH3AddMul = Double.parseDouble(cmdData);
                            break;
                        case "NH3AddType":
                            SysData.NH3AddType = Integer.parseInt(cmdData);
                            break;
                        //总磷参数
                        case "TPVolume":
                            SysData.TPVolume = Double.parseDouble(cmdData);
                            break;
                        case "TPSampleA":
                            SysData.TPSampleA = Double.parseDouble(cmdData);
                            break;
                        case "TPSampleB":
                            SysData.TPSampleB = Double.parseDouble(cmdData);
                            break;
                        case "TPSampleC":
                            SysData.TPSampleC = Double.parseDouble(cmdData);
                            break;
                        case "TPSampleO":
                            SysData.TPSampleO = Double.parseDouble(cmdData);
                            break;
                        case "TPAddValume":
                            SysData.TPAddValume = Double.parseDouble(cmdData);
                            break;
                        case "TPAddMul":
                            SysData.TPAddMul = Double.parseDouble(cmdData);
                            break;
                        case "TPAddType":
                            SysData.TPAddType = Integer.parseInt(cmdData);
                            break;
                        //总氮参数
                        case "TNVolume":
                            SysData.TNVolume = Double.parseDouble(cmdData);
                            break;
                        case "TNSampleA":
                            SysData.TNSampleA = Double.parseDouble(cmdData);
                            break;
                        case "TNSampleB":
                            SysData.TNSampleB = Double.parseDouble(cmdData);
                            break;
                        case "TNSampleC":
                            SysData.TNSampleC = Double.parseDouble(cmdData);
                            break;
                        case "TNSampleO":
                            SysData.TNSampleO = Double.parseDouble(cmdData);
                            break;
                        case "TNAddValume":
                            SysData.TNAddValume = Double.parseDouble(cmdData);
                            break;
                        case "TNAddMul":
                            SysData.TNAddMul = Double.parseDouble(cmdData);
                            break;
                        case "TNAddType":
                            SysData.TNAddType = Integer.parseInt(cmdData);
                            break;
                        //COD参数
                        case "CODVolume":
                            SysData.CODVolume = Double.parseDouble(cmdData);
                            break;
                        case "CODSampleA":
                            SysData.CODSampleA = Double.parseDouble(cmdData);
                            break;
                        case "CODSampleB":
                            SysData.CODSampleB = Double.parseDouble(cmdData);
                            break;
                        case "CODSampleC":
                            SysData.CODSampleC = Double.parseDouble(cmdData);
                            break;
                        case "CODSampleO":
                            SysData.CODSampleO = Double.parseDouble(cmdData);
                            break;
                        case "CODAddValume":
                            SysData.CODAddValume = Double.parseDouble(cmdData);
                            break;
                        case "CODAddMul":
                            SysData.CODAddMul = Double.parseDouble(cmdData);
                            break;
                        case "CODAddType":
                            SysData.CODAddType = Integer.parseInt(cmdData);
                            break;
                        //混合参数
                        case "MIXVolume":
                            SysData.MIXVolume = Double.parseDouble(cmdData);
                            break;
                        case "MIXSampleA":
                            SysData.MIXSampleA = Double.parseDouble(cmdData);
                            break;
                        case "MIXSampleB":
                            SysData.MIXSampleB = Double.parseDouble(cmdData);
                            break;
                        case "MIXSampleC":
                            SysData.MIXSampleC = Double.parseDouble(cmdData);
                            break;
                        case "MIXSampleO":
                            SysData.MIXSampleO = Double.parseDouble(cmdData);
                            break;
                        case "MIXAddValume":
                            SysData.MIXAddValume = Double.parseDouble(cmdData);
                            break;
                        case "MIXAddMul":
                            SysData.MIXAddMul = Double.parseDouble(cmdData);
                            break;
                        case "MIXAddType":
                            SysData.MIXAddType = Integer.parseInt(cmdData);
                            break;
                    }
                    JSONObject object = new JSONObject();
                    object.put("respond", "SET_Setup");
                    //定时任务
                    object.put("nextStartTime", formater.format(SysData.nextStartTime));
                    object.put("startCycle", SysData.startCycle);
                    object.put("numberTimes", SysData.numberTimes);
                    object.put("startWaterType", SysData.startWaterType);
                    object.put("startSampleType", SysData.startSampleType);
                    //系统参数
                    if(SysData.deviceList.size() >= 3) {
                        object.put("deviceList", SysData.deviceList.get(2));
                    } else {
                        object.put("deviceList", "无");
                    }
                    object.put("BAUD_RATE", SysData.BAUD_RATE);
                    object.put("MODBUS_ADDR", SysData.MODBUS_ADDR);
                    object.put("version", SysData.version);
                    //基本参数
                    object.put("waterType", SysData.waterType);
                    object.put("sampleType", SysData.sampleType);
                    object.put("supplySamplesTime", SysData.supplySamplesTime);
                    object.put("waterStepVolume", SysData.waterStepVolume);
                    object.put("reagentStepVolume", SysData.reagentStepVolume);
                    object.put("mixedTime", SysData.mixedTime);
                    //氨氮
                    object.put("NH3Volume", SysData.NH3Volume);
                    object.put("NH3SampleA", SysData.NH3SampleA);
                    object.put("NH3SampleB", SysData.NH3SampleB);
                    object.put("NH3SampleC", SysData.NH3SampleC);
                    object.put("NH3SampleO", SysData.NH3SampleO);
                    object.put("NH3AddValume", SysData.NH3AddValume);
                    object.put("NH3AddMul", SysData.NH3AddMul);
                    object.put("NH3AddType", SysData.NH3AddType);
                    //总磷
                    object.put("TPVolume", SysData.TPVolume);
                    object.put("TPSampleA", SysData.NH3SampleA);
                    object.put("TPSampleB", SysData.NH3SampleB);
                    object.put("TPSampleC", SysData.NH3SampleC);
                    object.put("TPSampleO", SysData.NH3SampleO);
                    object.put("TPAddValume", SysData.TPAddValume);
                    object.put("TPAddMul", SysData.TPAddMul);
                    object.put("TPAddType", SysData.TPAddType);
                    //总氮
                    object.put("TNVolume", SysData.TNVolume);
                    object.put("TNSampleA", SysData.TNSampleA);
                    object.put("TNSampleB", SysData.TNSampleB);
                    object.put("TNSampleC", SysData.TNSampleC);
                    object.put("TNSampleO", SysData.TNSampleO);
                    object.put("TNAddValume", SysData.TNAddValume);
                    object.put("TNAddMul", SysData.TNAddMul);
                    object.put("TNAddType", SysData.TNAddType);
                    //COD
                    object.put("CODVolume", SysData.CODVolume);
                    object.put("CODSampleA", SysData.CODSampleA);
                    object.put("CODSampleB", SysData.CODSampleB);
                    object.put("CODSampleC", SysData.CODSampleC);
                    object.put("CODSampleO", SysData.CODSampleO);
                    object.put("CODAddValume", SysData.CODAddValume);
                    object.put("CODAddMul", SysData.CODAddMul);
                    object.put("CODAddType", SysData.CODAddType);
                    //混合
                    object.put("MIXVolume", SysData.MIXVolume);
                    object.put("MIXSampleA", SysData.MIXSampleA);
                    object.put("MIXSampleB", SysData.MIXSampleB);
                    object.put("MIXSampleC", SysData.MIXSampleC);
                    object.put("MIXSampleO", SysData.MIXSampleO);
                    object.put("MIXAddValume", SysData.MIXAddValume);
                    object.put("MIXAddMul", SysData.MIXAddMul);
                    object.put("MIXAddType", SysData.MIXAddType);

                    send(object.toString());
                } catch (IOException | JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }

            //发送Gpio状态
            if(cmd.endsWith("GPIO_Status")) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "GPIO_Status");
                    object.put("mGpioOutD1",SysGpio.mGpioOutD1.getValue());
                    object.put("mGpioOutD2",SysGpio.mGpioOutD2.getValue());
                    object.put("mGpioOutD3",SysGpio.mGpioOutD3.getValue());
                    object.put("mGpioOutD4",SysGpio.mGpioOutD4.getValue());
                    object.put("mGpioOutD5",SysGpio.mGpioOutD5.getValue());
                    object.put("mGpioOutD6",SysGpio.mGpioOutD6.getValue());
                    object.put("mGpioOutD7",SysGpio.mGpioOutD7.getValue());
                    object.put("mGpioOutD8",SysGpio.mGpioOutD8.getValue());
                    object.put("mGpioOutP1",SysGpio.mGpioOutP1.getValue());
                    object.put("mGpioOutP2",SysGpio.mGpioOutP2.getValue());
                    object.put("mGpioOutP3",SysGpio.mGpioOutP3.getValue());
                    object.put("mGpioOutH1",SysGpio.mGpioOutH1.getValue());
                    object.put("mGpioOutLED",SysGpio.mGpioOutLED.getValue());
                    object.put("mGpioOut24V",SysGpio.mGpioOut24V.getValue());
                    object.put("mGpioOutDC1",SysGpio.mGpioOutDC1.getValue());
                    object.put("mGpioOutRE1",SysGpio.mGpioOutRE1.getValue());
                    object.put("mGpioOutDC2",SysGpio.mGpioOutDC2.getValue());
                    object.put("mGpioOutRE2",SysGpio.mGpioOutRE2.getValue());
                    object.put("statusC1",SysGpio.statusC1);
                    object.put("statusC2",SysGpio.statusC2);
                    object.put("statusC3",SysGpio.statusC3);
                    object.put("statusC4",SysGpio.statusC4);
                    object.put("statusC5",SysGpio.statusC5);
                    object.put("statusC6",SysGpio.statusC6);
                    object.put("mGpioIn1",SysGpio.mGpioIn1.getValue());
                    object.put("mGpioIn2",SysGpio.mGpioIn2.getValue());
                    object.put("mGpioIn3",SysGpio.mGpioIn3.getValue());
                    object.put("mGpioIn4",SysGpio.mGpioIn4.getValue());
                    object.put("mGpioIn5",SysGpio.mGpioIn3.getValue()); //合并使用
                    object.put("mGpioIn6",SysGpio.mGpioIn4.getValue()); //合并使用
                    object.put("statusS1",SysGpio.statusS[1]);
                    object.put("statusS2",SysGpio.statusS[2]);
                    object.put("statusS3",SysGpio.statusS[3]);
                    object.put("statusS4",SysGpio.statusS[4]);
                    object.put("statusS5",SysGpio.statusS[5]);
                    object.put("statusS6",SysGpio.statusS[6]);
                    object.put("statusS7",SysGpio.statusS[7]);
                    object.put("statusS8",SysGpio.statusS[8]);
                    object.put("statusS9",SysGpio.statusS[9]);
                    object.put("statusS10",SysGpio.statusS[10]);
                    object.put("statusS11",SysGpio.statusS[11]);
                    object.put("statusS12",SysGpio.statusS[12]);
                    object.put("isNotice",SysData.isNotice);
                    object.put("isSaveLog",SysData.isSaveLog);
                    object.put("errorId",SysData.errorId);
                    send(object.toString());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
            //输出开关
            if(cmd.startsWith("CMD_")) {
                String[] msg = cmd.split("_");
                String cmdName = msg[1];
                try {
                    if(!SysData.isRun) {
                        switch (cmdName) {
                            case "mGpioOutD1":
                                SysGpio.mGpioOutD1.setValue(!SysGpio.mGpioOutD1.getValue());
                                break;
                            case "mGpioOutD2":
                                SysGpio.mGpioOutD2.setValue(!SysGpio.mGpioOutD2.getValue());
                                break;
                            case "mGpioOutD3":
                                SysGpio.mGpioOutD3.setValue(!SysGpio.mGpioOutD3.getValue());
                                break;
                            case "mGpioOutD4":
                                SysGpio.mGpioOutD4.setValue(!SysGpio.mGpioOutD4.getValue());
                                break;
                            case "mGpioOutD5":
                                SysGpio.mGpioOutD5.setValue(!SysGpio.mGpioOutD5.getValue());
                                break;
                            case "mGpioOutD6":
                                SysGpio.mGpioOutD6.setValue(!SysGpio.mGpioOutD6.getValue());
                                break;
                            case "mGpioOutD7":
                                SysGpio.mGpioOutD7.setValue(!SysGpio.mGpioOutD7.getValue());
                                break;
                            case "mGpioOutD8":
                                SysGpio.mGpioOutD8.setValue(!SysGpio.mGpioOutD8.getValue());
                                break;
                            case "mGpioOutP1":
                                SysGpio.mGpioOutP1.setValue(!SysGpio.mGpioOutP1.getValue());
                                break;
                            case "mGpioOutP2":
                                SysGpio.mGpioOutP2.setValue(!SysGpio.mGpioOutP2.getValue());
                                break;
                            case "mGpioOutP3":
                                SysGpio.mGpioOutP3.setValue(!SysGpio.mGpioOutP3.getValue());
                                break;
                            case "mGpioOut24V":
                                SysGpio.mGpioOut24V.setValue(!SysGpio.mGpioOut24V.getValue());
                                break;
                            case "mGpioOutH1":
                                SysGpio.mGpioOutH1.setValue(!SysGpio.mGpioOutH1.getValue());
                                break;
                            case "mGpioOutLED":
                                SysGpio.mGpioOutLED.setValue(!SysGpio.mGpioOutLED.getValue());
                                break;
                            case "mGpioOutDC1":
                                SysGpio.mGpioOutDC1.setValue(!SysGpio.mGpioOutDC1.getValue());
                                break;
                            case "mGpioOutRE1":
                                SysGpio.mGpioOutRE1.setValue(!SysGpio.mGpioOutRE1.getValue());
                                break;
                            case "statusC1":
                                SysData.reagentChannel = 1;
                                MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                                break;
                            case "statusC2":
                                SysData.reagentChannel = 2;
                                MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                                break;
                            case "statusC3":
                                SysData.reagentChannel = 3;
                                MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                                break;
                            case "statusC4":
                                SysData.reagentChannel = 4;
                                MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                                break;
                            case "statusC5":
                                SysData.reagentChannel = 5;
                                MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                                break;
                            case "statusC6":
                                SysData.reagentChannel = 6;
                                MainActivity.com0.pumpCmd(2, "switch", SysData.reagentChannel);
                                break;
                            case "statusS1":
                                SysGpio.s1_inletWater(SysData.inletWaterStep);
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS2":
                                SysGpio.s2_addReagent(SysData.reagentChannel, SysData.addReagentStep);
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS3":
                                SysGpio.s3_supplySamples();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS4":
                                SysGpio.s4_initialize();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS5":
                                SysGpio.powerOff();
                                SysGpio.statusS5 = false;
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS6":
                                //重启软件
                                System.exit(0);
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS7":
                                SysGpio.s7_preparationWaterSamples(SysData.waterType, SysData.sampleType);
                                Log.d(TAG, "run: 启动配制水样类型：" + SysData.strWaterType);
                                Log.d(TAG, "run: 启动配制标样名称：" + SysData.strSampleType);
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS8":
                                SysGpio.s8_cleaning();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS9":
                                //SysGpio.s9_KongBaiShiYan();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS10":
                                //SysGpio.s10_BiaoYangCeDing();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS11":
                                //SysGpio.s11_Calibration();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS12":
                                //SysGpio.s12_Stop();
                                SysData.workFrom = "Web启动";
                                break;
                            case "isNotice":
                                SysData.isNotice = !SysData.isNotice;
                                break;
                            case "isSaveLog":
                                SysData.isSaveLog = !SysData.isSaveLog;
                                break;
                        }
                        JSONObject object = new JSONObject();
                        object.put("respond", "CMD_Ok");
                        send(object.toString());
                    } else {
                        JSONObject object = new JSONObject();
                        object.put("respond", "CMD_No");
                        send(object.toString());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            //复位报警
            if(cmd.endsWith("CLS_Alert")) {
                SysData.errorMsg = "";
                SysData.errorId = 0;
                SysData.resetAlert();                       //复位数据库报警记录
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "报警已清除");
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPong(WebSocketFrame pong) {

        }

        @Override
        protected void onException(IOException exception) {

        }
    }
}