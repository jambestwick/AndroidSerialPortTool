package com.example.jambestwick.androidserialtool.utils;

import android.os.SystemClock;
import android.util.Log;
import com.gxwl.device.reader.dal.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * <p>文件描述：串口文件工具<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/11/4<p>
 * <p>更新时间：2019/11/4<p>
 * <p>版本号：<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class SerialPortUtil {
    private String path = "sys/dev/ttyS0";//串口在android系统的通讯位置
    private int baudrate = 115200;//串口传输的波特率
    public boolean serialPortStatus = false; //是否打开串口标志

    private SerialPort serialPort = null;
    private volatile static SerialPortUtil instance;
    private static InputStream inputStream = null;
    private static OutputStream outputStream = null;

    private static final String TAG = "SerialPortUtil";
    public static long serialReadOutMillSecond = 150;
    public ExecutorService SERIAL_EXECUTOR = Executors.newCachedThreadPool();

    public static SerialPortUtil getInstance() {
        if (instance == null) {
            synchronized (SerialPortUtil.class) {
                if (instance == null) {
                    instance = new SerialPortUtil();
                }
            }
        }
        return instance;
    }

    private SerialPortUtil() {

    }

    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    public SerialPort openSerialPort() {
        try {
            serialPort = new SerialPort(new File(path), baudrate, 0);
            this.serialPortStatus = true;
            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (Exception e) {
            Log.e(TAG, Thread.currentThread().getName() + ",openSerialPort: 打开串口异常：" + Log.getStackTraceString(e));
            return serialPort;
        }
        Log.i(TAG, Thread.currentThread().getName() + ",openSerialPort: 打开串口");
        return serialPort;
    }


    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(serialReadOutMillSecond);
                    if (serialPort != null) {
                        serialPort.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                    serialPortStatus = false;
                } catch (IOException e) {
                    Log.e(TAG, Thread.currentThread().getName() + ",closeSerialPort: 关闭串口IO异常：" + Log.getStackTraceString(e));
                } catch (InterruptedException e) {
                    Log.e(TAG, Thread.currentThread().getName() + ",closeSerialPort: 关闭串口异常：");
                }
            }
        }.start();

        Log.i(TAG, Thread.currentThread().getName() + ",closeSerialPort: 关闭串口");
    }


    public interface ResponseListener {
        void onDataReceive(Object object, long currentTime);
    }


    /**
     * read serial response
     */
    public static class ReadSerialPort extends Thread {
        private ResponseListener responseListener;

        public ReadSerialPort(ResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        @Override
        public void run() {
            try {
                readOne(responseListener, System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        private boolean readOne(ResponseListener responseListener, long startTime) throws IOException {
            byte[] buffer = new byte[1024];
            int size; //读取数据的大小
            byte[] responseTotal = null;
            while (true) {
                int available = inputStream.available();
                if (available > 0) {
                    size = inputStream.read(buffer);
                    if (size > 0) {
                        byte[] responseBuffer = ByteConvert.subByte(buffer, 0, size);
                        // Log.i(TAG, Thread.currentThread().getName() + "read:receiveData:" + ByteConvert.bytesToHex(responseBuffer));
                        if (responseTotal == null) {
                            //指令的第一次返回,判断如果不是ff ff开头，丢弃继续读
                            if (!CommandUtil.isHead(responseBuffer)) {
                                //Log.i(TAG, Thread.currentThread().getName() + ",read:receiveData,不是ff ff开头,丢弃:" + ByteConvert.bytesToHex(responseBuffer));
                                continue;
                            }
                            responseTotal = responseBuffer;
                        } else {
                            responseTotal = ByteConvert.byteMergerNew(responseTotal, responseBuffer);
                        }
                        if (CommandUtil.checkRespHeadAndEnd(responseTotal)) {
                            //Log.i(TAG, Thread.currentThread().getName() + "该条指令总长read:receiveData:" + ByteConvert.bytesToHex(responseTotal));
                            if (CommandUtil.middleHasFF(responseTotal)) {
                                //丢弃
                                //Log.i(TAG, Thread.currentThread().getName() + ",read:receiveData,串口返回的格式有误中间包含FF，退出:" + ByteConvert.bytesToHex(responseTotal));
                                return true;
                            }
                            if (CommandUtil.checkResponseData(responseTotal)) {
                                //Log.i(TAG, Thread.currentThread().getName() + ",read:receiveData通过开头、结尾、长度、XOR效验:" + ByteConvert.bytesToHex(responseTotal));
                                //解析OBU相关数据
                                responseListener.onDataReceive(responseTotal, System.currentTimeMillis());
                                return true;
                            }
                            break;
                        }

                    } else {
                        //判断是不是读到了数据流的末尾 ，防止出现死循环。
                        break;
                    }
                } else {//流里没有数据了，则退出操作
                    SystemClock.sleep(1); // 暂停一点时间，免得一直循环造成CPU占用率过高
                    if (System.currentTimeMillis() - startTime > serialReadOutMillSecond) {
                        //Log.d(TAG, Thread.currentThread().getName() + ",read读取指令，流里available没数据但本轮已超过:" + serialReadOutMillSecond + "ms,不再继续往下执行");
                        return true;
                    }
                }

            }

            return false;
        }
    }


    /**
     * write serial command
     */
   public static class WriteSerialPort extends Thread {
        private String hexCommand;

        public WriteSerialPort(String hexCommand) {
            this.hexCommand = hexCommand;
        }

        @Override
        public void run() {
            try {
                write(ByteConvert.hexStringToBytes(hexCommand));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void write(byte[] writeBytes) throws IOException {
            Log.i(TAG, Thread.currentThread().getName() + ",write:" + ByteConvert.bytesToHex(writeBytes));
            outputStream.write(writeBytes);
            outputStream.flush();
        }
    }


}

