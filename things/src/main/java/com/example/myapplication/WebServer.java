package com.example.myapplication;
import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fi.iki.elonen.NanoHTTPD;

class WebServer extends NanoHTTPD {

    private Context mainContext;

    public WebServer(int port, Context context) {
        super(port);
        mainContext = context;
    }

    @Override
    public Response serve(IHTTPSession session) {

        String uri = session.getUri();
        System.out.println("WebServer:" + uri);
        String filename = uri.substring(1);

        if (uri.equals("/"))
            filename = "index.html";

        if (uri.equals("/run")) {
            //如果仪器在空闲状态时启动分析流程
            if (SysData.isRun == false) {
                SysGpio.s7_ShuiZhiCeDing();
                filename = "run_ok.html";
            } else {
                filename = "run_no.html";
            }
        }

        boolean is_ascii = true;
        String mimetype = "text/html";
        if (filename.contains(".html") || filename.contains(".htm")) {
            mimetype = "text/html";
            is_ascii = true;
        } else if (filename.contains(".js")) {
            mimetype = "text/javascript";
            is_ascii = true;
        } else if (filename.contains(".css")) {
            mimetype = "text/css";
            is_ascii = true;
        } else if (filename.contains(".gif")) {
            mimetype = "text/gif";
            is_ascii = false;
        } else if (filename.contains(".jpeg") || filename.contains(".jpg")) {
            mimetype = "text/jpeg";
            is_ascii = false;
        } else if (filename.contains(".png")) {
            mimetype = "image/png";
            is_ascii = false;
        } else if (filename.contains(".ico")) {
            mimetype = "image/x-icon";
            is_ascii = false;
        } else if (filename.contains(".svg")) {
            mimetype = "image/svg";
            is_ascii = false;
        } else {
            filename = "tools.html";
            mimetype = "text/html";
        }

        if (is_ascii) {
            String response = "";
            String line = "";
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(mainContext.getAssets().open(filename)));

                while ((line = reader.readLine()) != null) {
                    if(filename.equals("socket.js") && line.equals("        address: 'ws://127.0.0.1:9501',")){
                        line = "        address: 'ws://" + SysData.webIPAddr + ":" + (SysData.webPort + 1) + "',";
                    }
                    if(filename.equals("socketCod.js") && line.equals("        address: 'ws://10.10.0.139:8081',")){
                        line = "        address: 'ws://" + SysData.webIPAddr + ":" + (SysData.webPort + 1) + "',";
                    }
                    response = response + line + "\n";
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newFixedLengthResponse(Response.Status.OK, mimetype, response);
        } else {
            InputStream isr;
            try {
                isr = mainContext.getAssets().open(filename);
                return newFixedLengthResponse(Response.Status.OK, mimetype, isr, isr.available());
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.OK, mimetype, "");
            }
        }
    }

}

