package com.example.myapplication;
import android.content.Context;

import java.io.IOException;

import fi.iki.elonen.NanoWSD;

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
                send("COD欢迎您！");
            } catch (IOException e) {
                // handle
            }
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            try {
                send("COD再见！");
            } catch (IOException e) {
                // handle
            }
        }

        //override onOpen, onClose, onPong and onException methods

        @Override
        protected void onMessage(WebSocketFrame webSocketFrame) {
            String cmd = webSocketFrame.getTextPayload();
            try {
                send("收到指令：" + cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(cmd.endsWith("RUN_Start")) {
                if(!SysData.isRun) {
                    SysGpio.s7_ShuiZhiCeDing();
                    try {
                        send("启动成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        send("启动失败，仪表正在运行状态");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            if(cmd.endsWith("RUN_Stop")) {
                SysGpio.s12_Stop();
                try {
                    send("仪表已紧急停止");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(cmd.endsWith("RUN_Reset")) {
                SysGpio.s8_Reset();
                try {
                    send("仪表已运行复位程序,大约需要5分钟完成复位程序");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(cmd.endsWith("RUN_Status")) {
                try {
                    send("仪表状态：" + SysData.statusMsg + " " + SysData.errorMsg);
                    send("COD值：" + SysData.codVolue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(SysData.isRun) {
                    try {
                        send("开始时间：" + SysData.startTime);
                        send("当前进度：" + SysData.progressRate);
                        send("反应温度：" + SysData.tempIn);
                        send("加热器温度：" + SysData.tempOut);
                        send("光电值：" + SysData.adLight);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(cmd.endsWith("CLS_Alert")) {
                SysData.errorMsg = "";
                try {
                    send("报警已清除");
                } catch (IOException e) {
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