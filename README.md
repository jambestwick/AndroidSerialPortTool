# AndroidSerialTool

#### Description
Android 串口通讯 协议封装
针对一般数据帧的处理：

一般帧指令分为几个部分

| STX | RSCTL | DATA | BCC |ETX
| :-----| ----: | :----: |:----: |:----: |

|字段    |  描述   |
|-----  |-----    |
|STX    | 帧开始标志，取值为FFFFH，2byte； | 
|RSCTL  | 串口帧序列号,1byte <br> (1)RSU发送的串口帧序列号的低半字节为8，高半字节一般为0～7;<br>(2)PC发送的串口帧序列号是将收到的串口帧序列号高低半字节互换;<br>(3)RSU发送的串口帧序号为X8H,其中X为0～7;<br>(4)PC发送的串口帧序号为8XH,其中X为0～7;| 
|DATA   |发送的帧指令数据；|
|BCC    |异或效验值，1byte，从RSCTL到DATA所有字节的异或值；|
|ETX    |帧结尾，取值为FFH,1byte;|
#### Software Architecture
Software architecture description


# Android SerialPort protocol for #command

[![Release](https://jitpack.io/v/NaikSoftware/StompProtocolAndroid.svg)](https://jitpack.io/#NaikSoftware/StompProtocolAndroid)

## Overview

This library provide support for android-serialport protocol https://code.google.com/archive/p/android-serialport-api/
At now library works only as client for backend with support SerialPort-Command, such as
NodeJS (SerialPort.js or other) or Spring Boot (SockJS).

Add library as gradle dependency

```gradle
repositories { 
    jcenter()
    maven { url "https://jitpack.io" }
}
dependencies {
    implementation 'com.github.jambestwick:AndroidSerialTool:{latest version}'
}
```


Check out the full example server https://github.com/jambestwick/AndroidSerialPortTool

## Example library usage

**Basic usage**
``` java

        SerialPortUtil.getInstance().openSerialPort();
        /**
        **writeCommand Thread
        **/
        SerialPortUtil.getInstance().SERIAL_EXECUTOR.execute(new SerialPortUtil.WriteSerialPort("FFFF000001FF"));
        
        /**
        **readCommand Thread with callBack interface
        **/
        SerialPortUtil.getInstance().SERIAL_EXECUTOR.execute(new SerialPortUtil.ReadSerialPort(this));
 
        SerialPortUtil.getInstance().closeSerialPort();

```

See the full example https://github.com/jambestwick/AndroidSerialPortTool/tree/master/app/src/main/java/com/example/jambestwick/androidserialtool/simple/TestActivity.java



