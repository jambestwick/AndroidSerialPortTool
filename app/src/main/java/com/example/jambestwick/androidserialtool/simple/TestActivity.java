package com.example.jambestwick.androidserialtool.simple;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.example.jambestwick.androidserialtool.utils.SerialPortUtil;

/**
 * @Author jambestwick
 * @create 2021/7/31 0031  20:31
 * @email jambestwick@126.com
 */
public class TestActivity extends Activity implements SerialPortUtil.ResponseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testSerial();
    }

    void testSerial() {
        SerialPortUtil.getInstance().openSerialPort();
        /**
         * write command
         * */
        SerialPortUtil.getInstance().SERIAL_EXECUTOR.execute(new SerialPortUtil.WriteSerialPort("FFFF000001FF"));

        /**
         * read command
         *
         * */
        SerialPortUtil.getInstance().SERIAL_EXECUTOR.execute(new SerialPortUtil.ReadSerialPort(this));
    }

    @Override
    public void onDataReceive(Object object, long currentTime) {
        Log.d(TestActivity.class.getName(), "onDataReceive: " + object);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * realase SerialPort
         * **/
        SerialPortUtil.getInstance().closeSerialPort();
    }
}
