package com.example.jambestwick.androidserialtool.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2019/10/24<p>
 * <p>更新时间：2019/10/24<p>
 * <p>版本号：${VERSION}<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class ByteConvert {


    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    // 使用ArrayList方法
//java 合并两个byte数组
    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        if (bt1[0] == (byte) 0x02) {
            int i = 0;
            for (byte bt : bt1) {
                bt3[i] = bt;
                i++;
            }

            for (byte bt : bt2) {
                bt3[i] = bt;
                i++;
            }
        }
        if (bt2[0] == (byte) 0x02) {
            int i = 0;
            for (byte bt : bt2) {
                bt3[i] = bt;
                i++;
            }
            for (byte bt : bt1) {
                bt3[i] = bt;
                i++;
            }

        }
        if (bt1[bt1.length - 1] == (byte) 0x03) {
            int i = 0;
            for (byte bt : bt2) {
                bt3[i] = bt;
                i++;
            }
            for (byte bt : bt1) {
                bt3[i] = bt;
                i++;
            }
        }
        if (bt2[bt2.length - 1] == (byte) 0x03) {
            int i = 0;

            for (byte bt : bt1) {
                bt3[i] = bt;
                i++;
            }
            for (byte bt : bt2) {
                bt3[i] = bt;
                i++;
            }
        }
        if (bt1[0] != (byte) 0x02 && bt2[0] != 0x02 && bt1[bt1.length - 1] != (byte) 0x03 && bt2[bt2.length - 1] != (byte) 0x03) {
            int i = 0;
            for (byte bt : bt1) {
                bt3[i] = bt;
                i++;
            }

            for (byte bt : bt2) {
                bt3[i] = bt;
                i++;
            }
        }
        return bt3;
    }

    /**
     * 新的合并顺序
     */
    public static byte[] byteMergerNew(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        int i = 0;
        for (byte bt : bt1) {
            bt3[i] = bt;
            i++;
        }

        for (byte bt : bt2) {
            bt3[i] = bt;
            i++;
        }
        return bt3;
    }

    private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * 字符串转字节数组
     *
     * @param s 字符串
     * @return 数组
     */
    public static byte[] hexStringToByteArray(String s) {
        if (s.length() < 2) {
            s = "0" + s;
        }
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 字节数组转字符串
     *
     * @param hexBytes 数组
     * @return 字符串
     */
    public static String hexBytesToString(byte[] hexBytes) {
        char[] hexChars = new char[hexBytes.length * 2];
        for (int j = 0; j < hexBytes.length; j++) {
            int v = hexBytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 16进制字符串转int
     *
     * @param hexString 字符串
     * @return int
     */
    public static int hexStringToInt(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

    /**
     * int到byte[] 由高位到低位
     *
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[1];
//        result[0] = (byte)((i >> 24) & 0xFF);
//        result[1] = (byte)((i >> 16) & 0xFF);
//        result[2] = (byte)((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 16进制字符串转byte数组
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * char转换位byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte xor(byte[] arrayByte) {
        byte temp = 0;
        for (int i = 0; i < arrayByte.length; i++) {
            temp ^= arrayByte[i];
        }
        return temp;
    }

    public static byte[] calendar2Bytes(Calendar calendar) {
        int time = (int) (calendar.getTimeInMillis() / 1000);
        byte[] bytes = new byte[4];
        for (int i = bytes.length - 1; i >= 0; i--) {
            bytes[i] = (byte) (time & 0xFF);
            time >>= 8;
        }
        return bytes;
    }

    /**
     * byte数组转换为二进制字符串,每个字节以","隔开
     **/
    public static String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2) + ",");
        }
        return result.toString().substring(0, result.length() - 1);
    }

    /**
     * 截取byte数组   不改变原数组
     *
     * @param b      原数组
     * @param off    偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }


    public static List<String> strToArray(String str) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            String subStr = str.substring(i, i + 1);
            strings.add(subStr);
        }
        return strings;
    }

    public static String append(String[] strings) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            result.append(strings[i]);
        }
        return result.toString();
    }

    /***
     * 判断02开头和03结尾
     *
     * ***/
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }
}
