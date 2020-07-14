package com.example.myapplication;

import android.nfc.Tag;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

import static com.example.myapplication.CRC16.getCRC16;
import static com.example.myapplication.CRC32.getCRC32;

public class OutCom {
    private String TAG = "OutCom";
    private String UART_DEVICE_NAME;
    private int BAUD_RATE;
    private int DATA_BITS;
    private int STOP_BITS;
    private int CHUNK_SIZE = 256;
    private UartDevice mUartDevice;
    private String backMsg;

    public OutCom(String UART_DEVICE_NAME, int BAUD_RATE, int DATA_BITS, int STOP_BITS) {
        this.UART_DEVICE_NAME = UART_DEVICE_NAME;
        this.BAUD_RATE = BAUD_RATE;
        this.DATA_BITS = DATA_BITS;
        this.STOP_BITS = STOP_BITS;
    }

    public String getUART_DEVICE_NAME() {
        return UART_DEVICE_NAME;
    }

    public void setUART_DEVICE_NAME(String UART_DEVICE_NAME) {
        this.UART_DEVICE_NAME = UART_DEVICE_NAME;
    }

    public int getBAUD_RATE() {
        return BAUD_RATE;
    }

    public void setBAUD_RATE(int BAUD_RATE) {
        this.BAUD_RATE = BAUD_RATE;
    }

    public int getDATA_BITS() {
        return DATA_BITS;
    }

    public void setDATA_BITS(int DATA_BITS) {
        this.DATA_BITS = DATA_BITS;
    }

    public int getSTOP_BITS() {
        return STOP_BITS;
    }

    public void setSTOP_BITS(int STOP_BITS) {
        this.STOP_BITS = STOP_BITS;
    }

    public int getCHUNK_SIZE() {
        return CHUNK_SIZE;
    }

    public void setCHUNK_SIZE(int CHUNK_SIZE) {
        this.CHUNK_SIZE = CHUNK_SIZE;
    }

    public String getBackMsg() {
        return backMsg;
    }

    public void setBackMsg(String backMsg) {
        this.backMsg = backMsg;
    }

    public void openUart() {
        PeripheralManager manager = PeripheralManager.getInstance();
        try {
            //通过UART接口名称UART0，打开接口
            mUartDevice = manager.openUartDevice(UART_DEVICE_NAME);
            //设置波特率、数据大小、校验等参数
            mUartDevice.setBaudrate(BAUD_RATE);
            mUartDevice.setDataSize(DATA_BITS);
            mUartDevice.setParity(UartDevice.PARITY_NONE);
            mUartDevice.setStopBits(STOP_BITS);
            //注册数据监听，在有数据可读的时候回调
            mUartDevice.registerUartDeviceCallback(mUartDeviceCallback);
            Log.w(TAG, "打开端口成功:" + this.UART_DEVICE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "打开端口失败:" + this.UART_DEVICE_NAME);
        }
    }

    private UartDeviceCallback mUartDeviceCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            try {
                //读取PC终端发来的数据
                byte[] buffer = new byte[CHUNK_SIZE];
                byte[] data = new byte[CHUNK_SIZE];
                int read;
                int num = 0;
                String msg = null;

                while ((read = mUartDevice.read(buffer, buffer.length)) > 0) {
                    System.arraycopy(buffer, 0, data, num, read);
                    num = num + read;
                    Thread.sleep(10);
                }

                //显示数据包的数据
                Log.v(TAG,"数据包长度：" + num);
                /*
                for(int i = 0; i < num; i++){
                    Log.v(TAG,i + ":" + data[i]);
                }

                 */
                //去除数据数组的空值，解析数据
                byte[] backData = new byte[num];
                System.arraycopy(data, 0, backData, 0, num);
                byteArrayToData(backData, num);   //提取数据

            } catch (IOException e) {
                Log.w(TAG, "Unable to transfer data over UART", e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w(TAG, uart + ": Error event " + error);
        }
    };

    //接收的byte[]数据解析
    public void byteArrayToData(byte[] b, int num) {
        //接收的数据进行校验
        if(b.length >= 8) {
            String rxData = bytes2HexString(b);
            Log.w(TAG, "收到的内容：" + rxData);
            String data = rxData.substring(0, rxData.length() - 4);
            String crc = rxData.substring(rxData.length() - 4, rxData.length());
            Log.w(TAG, "其中数据为：" + data);
            Log.w(TAG, "其中校验位为：" + crc);
            //Log.w(TAG, "CRC16校验结果为：" + getCRC16(data));
            byte[] byteData = hexString2Bytes(data);
            byte[] byteSum = SumCheck(byteData, 2);
            //Log.w(TAG, "和校验结果为：" + bytes2HexString(byteSum));
            Log.w(TAG, "CRC校验值：" + getCRC(byteData));
            if(!crc.equals(getCRC(byteData).toUpperCase())) {
                Log.w(TAG, "CRC校验结果为：错误");
                return;
            } else {
                Log.w(TAG, "CRC校验结果为：正确");
            }
        } else {
            return;
        }
        if( b[0] == SysData.MODBUS_ADDR) {
            Log.w(TAG,  "地址位正确：" + b[0]);
            Log.w(TAG,  "功能码：" + b[1]);
            switch (b[1]) {
                case 0x01:
                    sendAckErr(b[1], 0x01);
                    mods_01H();
                    putMsg(0x01, 0);
                    Log.w(TAG,  "功能码：" + b[1]);
                    break;
                case 0x02:
                    sendAckErr(b[1], 0x01);
                    mods_02H(b, num);
                    putMsg(0x01, 0);
                    Log.w(TAG,  "功能码：" + b[1]);
                    break;
                case 0x03:
                    //sendAckErr(b[1], 0x01);
                    mods_03H(b, num);
                    putMsg(0x01, 0);
                    Log.w(TAG,  "功能码：" + b[1]);
                    break;
                case 0x04:
                    sendAckErr(b[1], 0x01);
                    mods_04H(b, num);
                    putMsg(0x01, 0);
                    Log.w(TAG,  "读取数据：" + b[1]);
                    break;
                case 0x05:
                    sendAckErr(b[1], 0x01);
                    mods_05H(b, num);
                    putMsg(0x01, 0);
                    Log.w(TAG,  "功能码：" + b[1]);
                    break;
                case 0x06:
                    //sendAckErr(b[1], 0x01);
                    mods_06H(b, num);
                    putMsg(0x01, 0);
                    Log.w(TAG,  "功能码：" + b[1]);
                    break;
                case 0x10:
                    sendAckErr(b[1], 0x01);
                    mods_10H();
                    putMsg(0x01, 0);
                    Log.w(TAG,  "功能码：" + b[1]);
                    break;
                default:
                    sendAckErr(b[1], 0x01);
                    Log.w(TAG,  "功能码：" + b[1]);   //功能码错误
            }
        } else {
            Log.w(TAG, "地址位错误：" + b[0]);
        }
    }

    //处理指令01H
    public void mods_01H() {

    }


    //处理指令02H
    public void mods_02H(byte[] b, int num) {

    }

    //处理指令03H
    public void mods_03H(byte[] b, int num) {
        String rxStr = bytes2HexString(b);
        String addrStr = rxStr.substring(4, 8);
        int addr = Integer.valueOf(addrStr, 16);
        Log.w(TAG,  "读取数据的地址" + addr);

        if(addr == 0) {
            //读取仪表cod值 03 03 00 00 00 01 XX XX
            Log.w(TAG,  "读取仪表cod值");
            String askStr,dataStr;
            int codValue = (int) (SysData.codValue * 100);
            //codValue = 1023;
            askStr = "";
            byte[] askByte = new byte[]{(byte) b[0], (byte) b[1], (byte) 0x02};
            String codStr = Integer.toHexString(codValue);
            if(codValue == 0) {
                codStr = "0000";
            }
            if(codStr.length() < 4) {
                for (int i = 0; i <= (4 - codStr.length()); i++ ) {
                    codStr = "0" + codStr;
                }
            }

            Log.w(TAG,  "cod的值:" + codValue);
            Log.w(TAG,  "cod的值H:" + codStr);
            askStr = bytes2HexString(askByte);
            askStr = askStr + codStr;
            Log.w(TAG,  "askByte数据：" + askStr);
            for(int i = 0; i < askByte.length; i++) {
                Log.w(TAG,  "askByte[" + (i) + "]数据：" + askByte[i]);
            }
            sendBytes(askStr, true);
        }
        if(addr == 2) {
            //读取仪表错误代码 03 03 00 02 00 01 XX XX
            Log.w(TAG,  "读取仪表错误代码");
            String askStr = "";
            int errorId = SysData.errorId;
            //errorId = 5;
            byte[] askByte = new byte[]{(byte) b[0], (byte) b[1], (byte) 0x02, (byte) 0x00, (byte) 0x00};
            askByte[4] = (byte) errorId;
            Log.w(TAG,  "读取出错代码:" + (Integer.toHexString(errorId)));
            askStr = bytes2HexString(askByte);
            Log.w(TAG,  "askByte数据：" + askStr);
            for(int i = 0; i < askByte.length; i++) {
                Log.w(TAG,  "askByte[" + (i) + "]数据：" + askByte[i]);
            }
            sendBytes(askStr, true);
        }
        if(addr == 4) {
            //读取仪表状态 03 03 00 04 00 01 XX XX
            Log.w(TAG,  "读取仪表状态");
            String askStr = "";
            int status = 0;
            if(SysData.isRun) {
                status = SysData.progressRate;
                if(status == 0) {
                    status = 1;
                }
            } else {
                status = 0;
            }
            byte[] askByte = new byte[]{(byte) b[0], (byte) b[1], (byte) 0x02, (byte) 0x00, (byte) 0x00};
            askByte[4] = (byte) status;
            Log.w(TAG,  "读取仪器状态:" + (Integer.toHexString(status)));
            askStr = bytes2HexString(askByte);
            Log.w(TAG,  "askByte数据：" + askStr);
            for(int i = 0; i < askByte.length; i++) {
                Log.w(TAG,  "askByte[" + (i) + "]数据：" + askByte[i]);
            }
            sendBytes(askStr, true);
        }
    }

    //处理指令04H
    public void mods_04H(byte[] b, int num) {

    }

    //处理指令05H
    public void mods_05H(byte[] b, int num) {

    }

    //处理指令06H
    public void mods_06H(byte[] b, int num) {
        String rxStr = bytes2HexString(b);
        String addrStr = rxStr.substring(4, 8);
        int addr = Integer.valueOf(addrStr, 16);
        Log.w(TAG,  "写入数据的地址" + addr);

        if(addr == 6) {
            //写入仪表值 03 06 00 0A 00 01 XX XX
            if(b[5] == 1) {
                Log.w(TAG, "上位机启动仪表开始测试");
                if(!SysData.isRun) {
                    SysGpio.s7_ShuiZhiCeDing();             //启动仪表
                    SysData.workFrom = "串口启动";           //启动分析命令来自于 触摸屏、串口、Web、定时启动
                }
                String sendStr = bytes2HexString(b);
                sendBytes(sendStr, false);
            }
            if(b[5] == 2) {
                Log.w(TAG, "上位机启动仪表复位");
                if(!SysGpio.statusS8) {
                    SysGpio.s8_Reset();                     //仪表复位
                }
                SysData.errorId = 0;                        //复位错误代码
                SysData.errorMsg = "";                      //复位错误信息
                SysData.resetAlert();                       //复位数据库报警记录
                String sendStr = bytes2HexString(b);
                sendBytes(sendStr, false);
            }
            if(b[5] == 3) {
                Log.w(TAG, "上位机启动仪表校准");
                if(!SysData.isRun) {
                    SysGpio.s11_Calibration();             //仪表校准
                    SysData.workFrom = "串口启动";           //启动分析命令来自于 触摸屏、串口、Web、定时启动
                }
                String sendStr = bytes2HexString(b);
                sendBytes(sendStr, false);
            }
        }
    }

    //处理指令10H
    public void mods_10H() {

    }

    //发送消息
    public void putMsg(int work, int x) {

    }

    //发送应答指令
    public void sendAckOk() {

    }

    //发送错误代码
    public void sendAckErr(int code, int error) {
        byte[] askByte = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00};
        askByte[1] = (byte) SysData.MODBUS_ADDR;
        askByte[1] = (byte)(code + 0x80);
        askByte[2] = (byte) error;
        String askStr = bytes2HexString(askByte);
        Log.w(TAG,  "askByte数据：" + askStr);
        sendBytes(askStr, true);
    }

    /*
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 校验接收的数据是否正确
     *
     * @param bytes 接收的byte数组
     * @return 返回校验结果
     */
    public boolean checkData(byte bytes[]) {
        //计算字节码的和校验值，并与校验数据比较

        return true;
    }

    /**
     * 发送字节数组
     *
     * @param dataStr      需要发送的byte数组
     * @param isCRCCheck 是否进行校验CRC
     * @return 返回发送的字节数，返回-1发送失败
     */
    public int sendBytes(String dataStr, boolean isCRCCheck) {
        //计算字节码的校验值，并添加到待发送数字bytes后面
        String sendStr = dataStr;
        byte[] byteData = hexString2Bytes(dataStr);
        if (isCRCCheck) {
            sendStr = dataStr + getCRC(byteData);
            Log.w(TAG,  "sendStr数据：" + sendStr);
            //Log.w(TAG,  "sendStr数据：" + dataStr + "|" + getCRC(byteData));
        }
        try {
            byte[] bytes = hexString2Bytes(sendStr);
            if(bytes != null) {
                mUartDevice.write(bytes, bytes.length);
            }
            return bytes.length;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /*
     * 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }

    }

    /*
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    /**
     * 校验和
     *
     * @param msg    需要计算校验和的byte数组
     * @param length 校验和位数
     * @return 计算出的校验和数组
     */
    private static byte[] SumCheck(byte[] msg, int length) {
        long mSum = 0;
        byte[] mByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (byte byteMsg : msg) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            mSum += mNum;
        } /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int i = 0; i < length; i++) {
            // mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
            mByte[i] = (byte) (mSum >> (i * 8) & 0xff);
        }
        /** end of for (int i = length - 1; i >= 0; i--) 校验和的字节顺序*/
        return mByte;
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
        String CRCStr = Integer.toHexString(CRC);
        if(CRCStr.length() == 3) {
            CRCStr = "0" + CRCStr;
        }
        return CRCStr;
    }

    //关闭通讯端口
    public void closeUart() {
        if (mUartDevice != null) {
            try {
                mUartDevice.unregisterUartDeviceCallback(mUartDeviceCallback);
                mUartDevice.close();
                mUartDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close UART device", e);
            }
        }
    }

}

