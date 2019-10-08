package com.example.myapplication;

import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

public class UartCom {
    private String TAG = "UartCom";
    private String UART_DEVICE_NAME = "UART0";
    private int BAUD_RATE = 115200;
    private int DATA_BITS = 8;
    private int STOP_BITS = 1;
    private int CHUNK_SIZE = 1024;
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
                int read;
                String msg = null;
                while ((read = mUartDevice.read(buffer, buffer.length)) > 0) {
                    String text = new String(buffer, 0, read);
                    msg = msg + text;
                    //Log.w(TAG, "read from PC:" + text);
                    Log.v(TAG, "接收的数据:" + buffer[0] + " " + buffer[1]  + " " + buffer[2] + " " + buffer[3] + " " + buffer[4]
                            + " " + buffer[5]  + " " + buffer[6] + " " + buffer[7]);
                    //byte[] srtbyte = text.getBytes();
                    //mUartDevice.write(buffer, read);
                    //mUartDevice.write(srtbyte, srtbyte.length);
                }
                //校验返回的数据是否出错
                if (msg != null) {
                    boolean result = checkData(msg.getBytes(), 2);
                    if (result) {
                        backMsg = msg;
                    }
                }
            } catch (IOException e) {
                Log.w(TAG, "Unable to transfer data over UART", e);
            }
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w(TAG, uart + ": Error event " + error);
        }
    };

    public void sendCMD(byte devId, byte cmd, int num){

    }


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
        if (result.equals(sumBytes)) {
            return true;
        } else {
            return false;
        }
    }

    //发送字符串
    public void sendMsg(String text) {
        byte[] srtbyte = text.getBytes();
        try {
            mUartDevice.write(srtbyte, srtbyte.length);
            Log.w(TAG, "已发送数据：" + text);
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
            Log.v(TAG, "已发送数据:" + bytes[0] + " " + bytes[1]  + " " + bytes[2] + " " + bytes[3] + " " + bytes[4]
                    + " " + bytes[5]  + " " + bytes[6] + " " + bytes[7]);

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