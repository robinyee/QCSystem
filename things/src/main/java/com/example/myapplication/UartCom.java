package com.example.myapplication;

import android.nfc.Tag;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

import static com.example.myapplication.CRC16.getCRC16;
import static com.example.myapplication.CRC32.getCRC32;

public class UartCom {
    private String TAG = "UartCom";
    private String UART_DEVICE_NAME;
    private int BAUD_RATE;
    private int DATA_BITS;
    private int STOP_BITS;
    private int CHUNK_SIZE = 256;
    private UartDevice mUartDevice;
    private String backMsg;

    public UartCom(String UART_DEVICE_NAME, int BAUD_RATE, int DATA_BITS, int STOP_BITS) {
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
                //读取PC终端发来的数据 ，并原封返回给PC
                byte[] buffer = new byte[CHUNK_SIZE];
                byte[] data = new byte[CHUNK_SIZE];
                int read;
                int num = 0;
                String msg = null;
                while ((read = mUartDevice.read(buffer, buffer.length)) > 0) {
                    System.arraycopy(buffer, 0, data, num, read);
                    num = num + read;
                    //Log.v(TAG,"本次读取的数据长度：" + read);
                    Thread.sleep(10);
                    /*
                    String text = new String(buffer, 0, read);
                    msg = msg + text;

                    Log.v(TAG,"接收到的数据：" + text);

                    */
                    //Log.w(TAG, "read from PC:" + text);
                    //Log.v(TAG, "接收的数据:" + buffer[0] + " " + buffer[1]  + " " + buffer[2] + " " + buffer[3] + " " + buffer[4] + " " + buffer[5]  + " " + buffer[6] + " " + buffer[7]);
                    //byte[] srtbyte = text.getBytes();
                    //mUartDevice.write(buffer, read);
                    //mUartDevice.write(srtbyte, srtbyte.length);
                }
                //显示数据包的数据
                //Log.v(TAG,"数据包长度：" + num);
                for(int i = 0; i < num; i++){
                    //Log.v(TAG,i + ":" + data[i]);
                }
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
        //Log.w(TAG, "数据长度：" + b.length);
        if(num == 13) {
            if (b[0] == (byte) 0xcc && b[11] == (byte) 0xdd && b[12] == (byte) 0xff) {
                //Log.w(TAG, "扩展板数据");
                //Log.w(TAG, "数据长度：" + b.length);
                int temp_in, temp_out;
                if (b[1] == 0x10 && num == 13) {
                    //Log.w(TAG, "扩展版数据");
                    temp_in = b[4] & 0xFF | (b[3] & 0xFF) << 8;
                    SysData.tempIn = ((double) temp_in) / 10;
                    //Log.w(TAG, "反应液温度：" + SysData.tempIn);
                    temp_out = b[6] & 0xFF | (b[5] & 0xFF) << 8;
                    SysData.tempOut = ((double) temp_out) / 10;
                    //Log.w(TAG, "加热器温度：" + SysData.tempOut);
                    SysData.adLight = b[8] & 0xFF | (b[7] & 0xFF) << 8;
                    //Log.w(TAG, "反应器光电值：" + SysData.adLight);
                    SysData.adBack = b[10] & 0xFF | (b[9] & 0xFF) << 8;
                    //Log.w(TAG, "备用模拟量值：" + SysData.adBack);
                }
            }
        }
        if(num == 8) {
            //校验返回的数据是否出错
            boolean result = checkData(b, 2);
            //Log.w(TAG, "数据检验：" + result);
            if (result) {
                int i = (int) b[1];
                SysData.Pump[i] = b[2];  //写入泵的状态
                //Log.w(TAG, i + "号泵状态：" + SysData.Pump[i]);
            }
        }
    }

    //采集模拟量值
    public void getAd(){
        byte bytes[] = new byte[]{(byte) 0xCC, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xDD};
        sendBytes(bytes, true, 2);
    }

    //生成注射泵指令代码并发送
    public void pumpCmd(int devId, String cmdStr, int num){
        byte[] cmdLine = new byte[] {(byte) 0xcc, (byte) devId, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xdd};
        switch (cmdStr) {
            case "status" : cmdLine[2] = (byte) 0x4a;
                break;
            case "pull" : cmdLine[2] = (byte) 0x41;
                break;
            case "push" : cmdLine[2] = (byte) 0x42;
                break;
            case "back" : cmdLine[2] = (byte) 0x45;
                break;
            case "start" : cmdLine[2] = (byte) 0x47;
                break;
            case "stop" : cmdLine[2] = (byte) 0x49;
                break;
            case "turn" : cmdLine[2] = (byte) 0x46;
                break;
        }
        if(num > 0){
            cmdLine[3] = (byte) num;
            cmdLine[4] = (byte) (num>>8);
        }
        sendBytes(cmdLine, true, 2);
    }

    //注射泵初始化，修改默认地址，devId:进样泵-01，锰泵-02，钠泵-03
    public void pumpInit(int devId){
        byte[] cmdLine = new byte[] {(byte) 0xcc, (byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xee, (byte) 0xbb, (byte) 0xaa, (byte) devId, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xdd};
        sendBytes(cmdLine, true, 2);
    };

    /**
     * 校验接收的数据是否正确
     *
     * @param bytes 接收的byte数组
     * @param num   校验和位数
     * @return 返回校验结果
     */
    public boolean checkData(byte bytes[], int num) {
        //计算字节码的和校验值，并与校验数据比较
        byte newBytes[] = new byte[bytes.length - num];
        byte sumBytes[] = new byte[num];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length - num);
        System.arraycopy(bytes, bytes.length - num, sumBytes, 0, num);
        byte result[] = SumCheck(newBytes, num);
        //Log.w(TAG, "校验结果：" + result[0] + " " + result[1]);
        for(int i=0; i<num; i++){
            if(result[i] != sumBytes[i]) {
                //Log.w(TAG, "校验出错" );
                return false;
            }
        }
        //Log.w(TAG, "校验正确" );
        return true;
    }

    //发送字符串
    public void sendMsg(String text) {
        byte[] srtbyte = text.getBytes();
        try {
            mUartDevice.write(srtbyte, srtbyte.length);
            //Log.w(TAG, "已发送数据：" + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送字节数组
     *
     * @param bytes      需要发送的byte数组
     * @param isSumCheck 是否进行校验和
     * @param num        校验和位数
     * @return 返回发送的字节数，返回-1发送失败
     */
    public int sendBytes(byte bytes[], boolean isSumCheck, int num) {
        //计算字节码的和校验值，并添加到待发送数字bytes后面
        if (isSumCheck) {
            byte newBytes[] = new byte[bytes.length + num];
            byte add[] = SumCheck(bytes, num);
            System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
            System.arraycopy(add, 0, newBytes, bytes.length, add.length);
            bytes = newBytes;
        }
        try {
            mUartDevice.write(bytes, bytes.length);
            //Log.v(TAG, "已发送数据:" + bytes[0] + " " + bytes[1]  + " " + bytes[2] + " " + bytes[3] + " " + bytes[4]
            //+ " " + bytes[5]  + " " + bytes[6] + " " + bytes[7]);

            return bytes.length;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
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

    public void testCRC32(String str) {
        //String str = "00000";
        //System.out.println("CRC32:"+getCRC32(str));
        Log.w(TAG, "String:" + str);
        Log.w(TAG, "CRC32:" + getCRC32(str));
        java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        crc32.update(str.getBytes());
        //System.out.println("CRC32:"+Long.toHexString(crc32.getValue()));
        Log.w(TAG, "String:" + str);
        Log.w(TAG, "CRC32:" + Long.toHexString(crc32.getValue()));
        Log.w(TAG, "String:" + str);
        Log.w(TAG, "CRC16:" + getCRC16(str));
    }

    //关闭通讯端口
    public void closeUart() {
        if (mUartDevice != null) {
            try {
                mUartDevice.close();
                mUartDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close UART device", e);
            }
        }
    }

}

