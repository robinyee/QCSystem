package com.example.myapplication;

import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

public class UartCom {
    private String TAG = "UartTest";
    private String UART_DEVICE_NAME = "UART0";
    private int BAUD_RATE = 115200;
    private int DATA_BITS = 8;
    private int STOP_BITS = 1;
    private int CHUNK_SIZE = 1024;
    private UartDevice mUartDevice;

    public UartCom(String UART_DEVICE_NAME, int BAUD_RATE, int DATA_BITS, int STOP_BITS) {
        this.UART_DEVICE_NAME = UART_DEVICE_NAME;
        this.BAUD_RATE = BAUD_RATE;
        this.DATA_BITS = DATA_BITS;
        this.STOP_BITS = STOP_BITS;
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
            Log.w(TAG, "Opened the UART0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UartDeviceCallback mUartDeviceCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            try {
                //读取PC终端发来的数据 ，并原封返回给PC
                byte[] buffer = new byte[CHUNK_SIZE];
                int read;
                while ((read = mUartDevice.read(buffer, buffer.length)) > 0) {
                    String text = new String(buffer, 0, read);
                    Log.w(TAG, "read from PC:" + text);
                    //byte[] srtbyte = text.getBytes();
                    //mUartDevice.write(buffer, read);
                    //mUartDevice.write(srtbyte, srtbyte.length);
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

    public void sendMsg(String text){
        byte[] srtbyte = text.getBytes();
        //mUartDevice.write(buffer, read);
        try {
            mUartDevice.write(srtbyte, srtbyte.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBytes(byte bytes[]){
        //mUartDevice.write(buffer, read);
        try {
            mUartDevice.write(bytes, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
