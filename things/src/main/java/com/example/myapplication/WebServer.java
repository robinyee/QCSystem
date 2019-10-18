package com.example.myapplication;
import fi.iki.elonen.NanoHTTPD;

class WebServer extends NanoHTTPD {

    public WebServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<title>CODMn分析仪</title>\n" +
                "<script>document.createElement(\"myHero\")</script>\n" +
                "<style>\n" +
                "myHero {\n" +
                "\tdisplay: block;\n" +
                "\tbackground-color: #ddd;\n" +
                "\tpadding: 50px;\n" +
                "\tfont-size: 30px;\n" +
                "} \n" +
                "</style> \n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "<h1>NS210型COD</h1>\n" +
                "\n" +
                "<p>欢迎您使用COD控制系统！</p>\n" +
                "\n" +
                "<myHero><button type=\"button\" onclick=\"alert('启动测试')\">启动分析</button>\n" +
                "<button type=\"button\" onclick=\"alert('停止分析')\">停止分析</button></myHero>\n" +
                "\n" +
                "</body>\n" +
                "</html>");

        return newFixedLengthResponse(builder.toString());
    }
}
