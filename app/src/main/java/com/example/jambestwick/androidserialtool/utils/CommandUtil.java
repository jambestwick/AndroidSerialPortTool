package com.example.jambestwick.androidserialtool.utils;

import com.example.jambestwick.androidserialtool.type.EnumTAG;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author jambestwick
 * @create 2021/7/31 0031 17:57
 * @email jambestwick@126.com
 */
public class CommandUtil {

    /**
     * 处理设备厂商关于FF，FE转义的问题
     * <p>
     * 数据帧开始标志为FFFFH，帧结束标志为FFH。其他字段不能出现FFH，如果数据确实为FFH，需对其进行转义处理。
     * 发送数据时，如果在其它字段中出现FFH字节时，将FFH分解为FEH和01H这两个字节来发送；如果在其它字段出现FEH字节时，需将FEH分解为FEH和00H这两个字节来发送。
     * 接收数据时，如果出现“FE 01”这样连续两个字节时将之合为一个字节FFH；如果出现“FE 00”这样连续两个字节时将之合为一个字节FEH。
     **/
    public static byte[] convertSendCommand(byte[] totalCommand) {
        List<Byte> byteList = new ArrayList<>();
        for (int i = 0; i < totalCommand.length; i++) {
            if (totalCommand[i] == (byte) 0xFF) {
                byteList.add((byte) 0xFE);
                byteList.add((byte) 0x01);
                continue;
            }
            if (totalCommand[i] == (byte) 0xFE) {
                byteList.add((byte) 0xFE);
                byteList.add((byte) 0x00);
                continue;
            }
            byteList.add(totalCommand[i]);
        }
        byte[] returnData = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            returnData[i] = byteList.get(i);
        }
        return returnData;
    }

    /**
     * 接收数据时，如果出现“FE 01”这样连续两个字节时将之合为一个字节FFH；如果出现“FE 00”这样连续两个字节时将之合为一个字节FEH。
     */
    public static byte[] convertReceiveCommand(byte[] totalCommand) {//不包含开头ffff和结尾ff
        boolean pass = false;
        List<Byte> byteList = new ArrayList<>();
        for (int i = 0; i < totalCommand.length; i++) {
            if (pass) {
                pass = false;
                continue;
            }
            if (totalCommand[i] == (byte) 0xFE) {
                if (i != (totalCommand.length - 1)) {
                    if (totalCommand[i + 1] == (byte) 0x01) {
                        byteList.add((byte) 0xFF);
                        pass = true;
                    } else if (totalCommand[i + 1] == (byte) 0x00) {
                        byteList.add((byte) 0xFE);
                        pass = true;
                    } else {
                        byteList.add(totalCommand[i]);
                    }

                } else {
                    byteList.add(totalCommand[i]);
                }

            } else {
                byteList.add(totalCommand[i]);
            }

        }
        byte[] returnData = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            returnData[i] = byteList.get(i);
        }
        return returnData;
    }

    /**
     * 判断是不是ff ff开头
     */
    public static boolean isHead(byte[] buffer) {
        if (buffer != null && buffer.length >= 2) {
            if (buffer[0] != (byte) 0xff || buffer[1] != (byte) 0xff) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断中间出现ff
     */
    public static boolean middleHasFF(byte[] responseTotal) {
        String subTotalHex = ByteConvert.bytesToHex(responseTotal);
        if (subTotalHex.length() > 6) {//如果指令中间出现ff，则是垃圾数据需要丢弃
            String subNew = subTotalHex.substring(4, subTotalHex.length() - 2);
            if (subNew.contains("ff")) {
                return true;
            }
        }
        return false;
    }


    /**
     * 末尾判断
     **/
    public static int parseEnd(byte[] buffer) {
        if (buffer[buffer.length - 1] == (byte) 0xFF) {//假设说明最后一位
            return EnumTAG.END.getKey();
        }
        return EnumTAG.NOT_END.getKey();
    }

    /**
     * 判断结尾，并校验数据
     ***/
    public static boolean checkResponseData(byte[] responseTotal) {
        int isEnd = parseEnd(responseTotal);
        if (isEnd == EnumTAG.END.getKey()) {
            int isTrueData = CommandUtil.parseNewData(responseTotal);
            if (isTrueData == EnumTAG.END.getKey()) {//结束
                return true;
            }
        }
        return false;
    }

    /**
     * 判断开头结尾
     **/
    public static boolean checkRespHeadAndEnd(byte[] responseTotal) {
        int isEnd = parseEnd(responseTotal);
        boolean isStart = isHead(responseTotal);
        if (isStart && isEnd == EnumTAG.END.getKey()) {
            return true;
        }
        return false;

    }

    /**
     * 效验的返回数据
     **/
    private static int parseNewData(byte[] buffer) {
        if (buffer.length < 7) {
            return EnumTAG.NOT_END.getKey();
        }
        if (buffer[0] != (byte) 0xFF || buffer[1] != (byte) 0xFF) {//不是数据头
            return EnumTAG.NOT_HEAD.getKey();
        }
        if (buffer[2] != (byte) 0x80) {
            return EnumTAG.NOT_END.getKey();
        }
        byte[] rsctlAndData = ByteConvert.subByte(buffer, 2, buffer.length - 4);
        if (ByteConvert.xor(rsctlAndData) != buffer[buffer.length - 2]) {
            return EnumTAG.DATA_XOR_ERROR.getKey();
        }
        if (buffer[buffer.length - 1] != (byte) 0xFF) {
            return EnumTAG.NOT_END.getKey();
        }
        return EnumTAG.END.getKey();
    }

}
