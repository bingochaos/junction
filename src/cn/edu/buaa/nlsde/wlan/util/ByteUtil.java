/*
 * ByteUtil.java
 *
 * Created on 2012年3月29日, 下午1:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.util;

import java.util.*;

/**
 *
 * @author HuLei
 */
public class ByteUtil {

    /**
     * Creates a new instance of ByteUtil
     */
    public ByteUtil() {
    }

    //计算校验位,从第一个字节,依次跟后面的字节异或,直到最后一个字节
    public static byte getCheckCode(byte data[]) {
        byte check_byte = getCheckCode(data, 0, data.length);
        return check_byte;
    }

    //从开始字节开始, 依次同后一字节异或, 最后得到的byte为校验字节
    public static byte getCheckCode(byte data[], int start, int end) {
        byte check_byte = data[start];
        if (start != end) {
            for (int i = start + 1; i < end; i++) {
                int check_number = check_byte ^ data[i];
                check_byte = ByteUtil.intToByte(check_number);
            }
        }
        return check_byte;
    }

    public static byte[] addBytes(byte data_1[], byte data_2[]) {
        int size = data_1.length + data_2.length;
        byte add_data[] = new byte[size];
        int offset = 0;
        for (int i = 0; i < data_1.length; i++) {
            add_data[offset] = data_1[i];
            offset++;
        }

        for (int i = 0; i < data_2.length; i++) {
            add_data[offset] = data_2[i];
            offset++;
        }
        return add_data;
    }

    public static byte[] addBytes(byte data_1[], byte data_2[], byte data_3[]) {
        int size = data_1.length + data_2.length + data_3.length;
        byte add_data[] = new byte[size];
        int offset = 0;
        for (int i = 0; i < data_1.length; i++) {
            add_data[offset] = data_1[i];
            offset++;
        }

        for (int i = 0; i < data_2.length; i++) {
            add_data[offset] = data_2[i];
            offset++;
        }

        for (int i = 0; i < data_3.length; i++) {
            add_data[offset] = data_3[i];
            offset++;
        }
        return add_data;
    }

    public static byte[] addBytes(ArrayList byte_list) {
        int size = 0;
        for (int i = 0; i < byte_list.size(); i++) {
            byte data[] = (byte[]) byte_list.get(i);
            size = size + data.length;
        }

        byte add_data[] = new byte[size];
        int offset = 0;
        for (int i = 0; i < byte_list.size(); i++) {
            byte data[] = (byte[]) byte_list.get(i);
            for (int j = 0; j < data.length; j++) {
                add_data[offset] = data[j];
                offset++;
            }
        }

        return add_data;
    }

    public static byte intToByte(int i) {
        byte bt;
        bt = (byte) (0xff & i);
        return bt;
    }

    //小端模式
    public static byte[] intToByte2_little(int i) {
        byte[] bt = new byte[2];
        bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        return bt;
    }

    //小端模式
    public static byte[] intToByte4_little(int i) {
        byte[] bt = new byte[4];
        bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        bt[2] = (byte) ((0xff0000 & i) >> 16);
        bt[3] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    //大端模式
    public static byte[] intToByte2_big(int i) {
        byte[] bt = new byte[2];
        bt[1] = (byte) (0xff & i);
        bt[0] = (byte) ((0xff00 & i) >> 8);
        return bt;
    }

    //大端模式
    public static byte[] intToByte4_big(int i) {
        byte[] bt = new byte[4];
        bt[3] = (byte) (0xff & i);
        bt[2] = (byte) ((0xff00 & i) >> 8);
        bt[1] = (byte) ((0xff0000 & i) >> 16);
        bt[0] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    public static int byteToInt(byte bytes) {
        int num = bytes & 0xFF;
        return num;
    }

    public static int byteToIntCheckSign(byte one_byte) {
        int num = one_byte & 0xFF;
        if (num > 127) {
            return 0 - (256 - num);
        } else {
            return num;
        }
    }

    //小端模式
    public static int bytes2ToInt_little(byte[] bytes) {
        int num = bytes[0] & 0xFF;
        num |= ((bytes[1] << 8) & 0xFF00);
        return num;
    }

    //小端模式
    public static int bytes4ToInt_little(byte[] bytes) {
        int num = bytes[0] & 0xFF;
        num |= ((bytes[1] << 8) & 0xFF00);
        num |= ((bytes[2] << 16) & 0xFF0000);
        num |= ((bytes[3] << 24) & 0xFF000000);
        return num;
    }

    //大端模式
    public static int bytes2ToInt_big(byte[] bytes) {
        int num = bytes[1] & 0xFF;
        num |= ((bytes[0] << 8) & 0xFF00);
        return num;
    }

    //大端模式
    public static int bytes4ToInt_big(byte[] bytes) {
        int num = bytes[3] & 0xFF;
        num |= ((bytes[2] << 8) & 0xFF00);
        num |= ((bytes[1] << 16) & 0xFF0000);
        num |= ((bytes[0] << 24) & 0xFF000000);
        return num;
    }

    public static byte[] getSubBytes(byte data[], int num) {
        byte new_data[] = new byte[num];
        for (int i = 0; i < num; i++) {
            new_data[i] = data[i];
        }
        return new_data;
    }

    public static byte[] getSubBytes(byte data[], int start, int size) {
        byte new_data[] = new byte[size];
        for (int i = 0; i < size; i++) {
            new_data[i] = data[start + i];
        }
        return new_data;
    }

    //不算头尾两个字节, 因为头尾是0x7e
    //转义0x7d 0x02 ——>0x7e
    //转义0x7d 0x01 ——>0x7d
    public static byte[] getBytesTransferBack(byte[] data) {//转义还原
        int new_data_size = data.length;
        for (int i = 1; i < data.length - 1; i++) {
            if (data[i] == 0x7d) {
                new_data_size--;
            }
        }

        byte new_data[] = new byte[new_data_size];
        new_data[0] = data[0];
        new_data[new_data_size - 1] = data[data.length - 1];

        int index = 1;
        for (int i = 1; i < new_data_size - 1; i++) {
            if (data[index] != 0x7d) {
                new_data[i] = data[index];
            } else {
                index++;
                if (data[index] == 0x1) {
                    new_data[i] = 0x7d;
                } else if (data[index] == 0x2) {
                    new_data[i] = 0x7e;
                } else {
                    System.out.println("解析或者编码出错啦");
                }
            }
            index++;
        }
        return new_data;
    }

    //不算头尾两个字节, 因为头尾是0x7e
    //转义0x7e ——> 0x7d 0x02
    //转义0x7d ——> 0x7d 0x01
    public static byte[] getByteTransfer(byte[] message_byte) {
        int message_size = message_byte.length;
        for (int i = 1; i < message_byte.length - 1; i++) {
            if ((message_byte[i] == 0x7e) || (message_byte[i] == 0x7d)) {
                message_size++;
            }
        }

        byte message_byte_transfer[] = new byte[message_size];
        message_byte_transfer[0] = message_byte[0];
        message_byte_transfer[message_byte_transfer.length - 1] = message_byte[message_byte.length - 1];

        int index = 1;
        for (int i = 1; i < message_byte.length - 1; i++) {
            if (message_byte[i] == 0x7e) {
                message_byte_transfer[index] = 0x7d;
                index++;
                message_byte_transfer[index] = 0x02;
                index++;
            } else if (message_byte[i] == 0x7d) {
                message_byte_transfer[index] = 0x7d;
                index++;
                message_byte_transfer[index] = 0x01;
                index++;
            } else {
                message_byte_transfer[index] = message_byte[i];
                index++;
            }
        }

        return message_byte_transfer;
    }

    //比较两个byte组是否一致
    public static boolean byteEquals(byte[] data_1, byte[] data_2) {
        if (data_1.length != data_2.length) {
            return false;
        }
        for (int i = 0; i < data_1.length; i++) {
            if (data_1[i] != data_2[i]) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList splitByte(byte[] data, byte split_byte) {
        ArrayList result = new ArrayList();
        boolean split_flag = false;
        int offset = 0;
        int size;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == split_byte) {
                if (split_flag == false) {
                    split_flag = true;
                } else {
                    split_flag = false;
                }
            }

            if ((split_flag == false)) {
                size = i - offset + 1;
                byte[] data_byte = ByteUtil.getSubBytes(data, offset, size);
                result.add(data_byte);
                offset = offset + size;
            }
        }
        return result;
    }

    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] floatToByte4_big(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte4ToFloat_big(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

}
