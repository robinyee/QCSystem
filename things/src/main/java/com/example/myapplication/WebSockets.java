package com.example.myapplication;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;

import com.google.android.things.pio.Gpio;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fi.iki.elonen.NanoWSD;

import static android.content.ContentValues.TAG;


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
                object.put("respond", "COD欢迎您");
                send(object.toString());
            } catch (IOException | JSONException e) {
                // handle
            }
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            try {
                JSONObject object = new JSONObject();
                object.put("respond", "COD再见");
                send(object.toString());
            } catch (IOException | JSONException e) {
                // handle
            }
        }

        //override onOpen, onClose, onPong and onException methods

        @Override
        protected void onMessage(WebSocketFrame webSocketFrame) {
            String cmd = webSocketFrame.getTextPayload();
            SimpleDateFormat formater = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            //启动水质分析流程
            if(cmd.endsWith("RUN_Start")) {
                if(!SysData.isRun) {
                    SysGpio.s7_ShuiZhiCeDing();
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
            //仪表紧急停止
            if(cmd.endsWith("RUN_Stop")) {
                SysGpio.s12_Stop();
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
                SysGpio.s8_Reset();
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "仪表已运行复位程序,大约需要5分钟完成复位程序");
                    send(object.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            //获取仪表参数
            if(cmd.endsWith("GET_Setup")) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("respond", "GET_Setup");
                    object.put("nextStartTime", formater.format(SysData.nextStartTime));
                    object.put("startCycle", SysData.startCycle);
                    object.put("numberTimes", SysData.numberTimes);
                    object.put("isLoop", SysData.isLoop);
                    object.put("xiaojieTemp", SysData.xiaojieTemp);
                    object.put("xiaojieTime", SysData.xiaojieTime);
                    object.put("biaodingValue", SysData.biaodingValue);
                    object.put("deviceList", SysData.deviceList.get(2));
                    object.put("BAUD_RATE", SysData.BAUD_RATE);
                    object.put("MODBUS_ADDR", SysData.MODBUS_ADDR);
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
                    JSONObject object = new JSONObject();
                    object.put("respond", "SET_Setup");
                    switch (cmdName) {
                        case "nextStartTime":
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date date = simpleDateFormat.parse(cmdData);
                            SysData.nextStartTime = date.getTime();
                            object.put(cmdName, SysData.nextStartTime);
                            break;
                        case "startCycle":
                            SysData.startCycle = Integer.parseInt(cmdData);
                            object.put(cmdName, SysData.startCycle);
                            break;
                        case "numberTimes":
                            SysData.numberTimes = Integer.parseInt(cmdData);
                            object.put(cmdName, SysData.numberTimes);
                            break;
                        case "isLoop":
                            SysData.isLoop = Boolean.parseBoolean(cmdData);
                            object.put(cmdName, SysData.isLoop);
                            Log.i(TAG, "cmdName：" + cmdName);
                            Log.i(TAG, "当前值：" + SysData.isLoop);
                            break;
                        case "xiaojieTemp":
                            SysData.xiaojieTemp = Double.parseDouble(cmdData);
                            object.put(cmdName, SysData.xiaojieTemp);
                            break;
                        case "xiaojieTime":
                            SysData.xiaojieTime = Integer.parseInt(cmdData);
                            object.put(cmdName, SysData.xiaojieTime);
                            break;
                        case "biaodingValue":
                            SysData.biaodingValue = Double.parseDouble(cmdData);
                            object.put(cmdName, SysData.biaodingValue);
                            break;
                        case "BAUD_RATE":
                            SysData.BAUD_RATE = Integer.parseInt(cmdData);
                            object.put(cmdName, SysData.BAUD_RATE);
                            break;
                        case "MODBUS_ADDR":
                            SysData.MODBUS_ADDR = Integer.parseInt(cmdData);
                            object.put(cmdName, SysData.MODBUS_ADDR);
                            break;
                        case "COM1":
                            if(cmdData.equals("Restart")){
                                if(SysData.deviceList.size() >= 3) {
                                    MainActivity.com1.closeUart();   //关闭com1串口通信
                                    MainActivity.com1 = null;
                                    //打开串口1通讯
                                    MainActivity.com1 = new OutCom(SysData.deviceList.get(2), SysData.BAUD_RATE, SysData.DATA_BITS, SysData.STOP_BITS);// 为U转串接口
                                    MainActivity.com1.openUart();
                                }
                            }
                            //object.put(cmdName, "Restart");
                            Log.i(TAG, "Com1已重启" );
                            break;
                    }

                    send(object.toString());
                } catch (IOException | JSONException | ParseException e) {
                    e.printStackTrace();
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
                    object.put("codVolue", SysData.codVolue);
                    object.put("progressRate", SysData.progressRate);
                    object.put("statusMsg", SysData.statusMsg);
                    object.put("startTime", formater.format(SysData.startTime));
                    object.put("endTime", formater.format(SysData.endTime));
                    object.put("tempIn", SysData.tempIn);
                    object.put("tempOut", SysData.tempOut);
                    object.put("adLight", SysData.adLight);
                    object.put("errorMsg", SysData.errorMsg);
                    object.put("startXiaojie", SysData.startXiaojie);
                    object.put("endXiaoJie", SysData.endXiaoJie);
                    object.put("didingNum", SysData.didingNum);
                    object.put("didingSumVolume", SysData.didingSumVolume);
                    object.put("deviceList", SysData.deviceList.get(2));
                    object.put("webServiceFlag", SysData.webServiceFlag);
                    object.put("workType", SysData.workType);
                    object.put("workFrom", SysData.workFrom);
                    object.put("tempBox", SysData.tempBox);
                    send(object.toString());
                } catch (JSONException | IOException e) {
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
                    object.put("mGpioIn1",SysGpio.mGpioIn1.getValue());
                    object.put("mGpioIn2",SysGpio.mGpioIn2.getValue());
                    object.put("mGpioIn3",SysGpio.mGpioIn3.getValue());
                    object.put("mGpioIn4",SysGpio.mGpioIn4.getValue());
                    object.put("statusS1",SysGpio.statusS1);
                    object.put("statusS2",SysGpio.statusS2);
                    object.put("statusS3",SysGpio.statusS3);
                    object.put("statusS4",SysGpio.statusS4);
                    object.put("statusS5",SysGpio.statusS5);
                    object.put("statusS6",SysGpio.statusS6);
                    object.put("statusS7",SysGpio.statusS7);
                    object.put("statusS8",SysGpio.statusS8);
                    object.put("statusS9",SysGpio.statusS9);
                    object.put("statusS10",SysGpio.statusS10);
                    object.put("statusS11",SysGpio.statusS11);
                    object.put("statusS12",SysGpio.statusS12);
                    object.put("isEmptyPipeline",SysData.isEmptyPipeline);
                    object.put("isNotice",SysData.isNotice);
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
                            case "mGpioOutD5":
                                SysGpio.mGpioOutD5.setValue(!SysGpio.mGpioOutD5.getValue());
                                break;
                            case "mGpioOutD6":
                                SysGpio.mGpioOutD6.setValue(!SysGpio.mGpioOutD6.getValue());
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
                            case "statusS1":
                                SysGpio.s1_JiaShuiYang();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS2":
                                SysGpio.s2_JiaLiuSuan();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS3":
                                SysGpio.s3_JiaGaoMengSuanJIa();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS4":
                                SysGpio.s4_JiaCaoSuanNa();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS5":
                                SysGpio.s5_XiaoJie();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS6":
                                SysGpio.s6_DiDing();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS7":
                                SysGpio.s7_ShuiZhiCeDing();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS8":
                                SysGpio.s8_Reset();
                                SysData.workFrom = "Web启动";
                                break;
                            case "statusS9":
                                //to-do
                                break;
                            case "statusS10":
                                //to-do
                                break;
                            case "statusS11":
                                //to-do
                                break;
                            case "statusS12":
                                SysGpio.s12_Stop();
                                SysData.workFrom = "Web启动";
                                break;
                        }
                        JSONObject object = new JSONObject();
                        object.put("respond", "任务已经执行");
                        send(object.toString());
                    } else {
                        JSONObject object = new JSONObject();
                        object.put("respond", "仪器正在运行，无法执行指令");
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