package io.aicloud.tools.netty.util;

import java.util.BitSet;

/**
 * bytes utils
 *
 * @author fagongzi
 */
public final class BytesUtils {
    private BytesUtils() {
    }

    /**
     * int -> byte[]
     *
     * @param value int value
     * @return bytes value
     */
    public static byte[] int2Byte(int value) {
        return unsignedInt2Byte(value);
    }

    /**
     * unsigned int -> byte[]
     *
     * @param value unsigned int value
     * @return bytes value
     */
    public static byte[] unsignedInt2Byte(long value) {
        byte[] ret = new byte[4];
        ret[3] = (byte) (0xff & value);
        ret[2] = (byte) ((0xff00 & value) >> 8);
        ret[1] = (byte) ((0xff0000 & value) >> 16);
        ret[0] = (byte) ((0xff000000 & value) >> 24);

        return ret;
    }

    /**
     * byte[] -> int
     *
     * @param value  bytes value
     * @param offset offset
     * @return int value
     */
    public static int byte2Int(byte[] value, int offset) {
        return Math.toIntExact(byte2UnsignedInt(value, offset));
    }

    /**
     * byte[] -> int
     *
     * @param value bytes value
     * @return int value
     */
    public static int byte2Int(byte[] value) {
        return Math.toIntExact(byte2UnsignedInt(value, 0));
    }

    /**
     * byte[] -> unsigned int
     *
     * @param value bytes value
     * @return unsigned int value
     */
    public static long byte2UnsignedInt(byte[] value) {
        return byte2UnsignedInt(value, 0);
    }

    /**
     * byte[] -> unsigned int
     *
     * @param value  bytes value
     * @param offset offset
     * @return unsigned int value
     */
    public static long byte2UnsignedInt(byte[] value, int offset) {
        long ret = 0;
        for (int i = offset; i < offset + 4; i++) {
            ret <<= 8;
            ret |= value[i] & 0xff;
        }
        return ret;
    }

    /**
     * short -> byte[]
     *
     * @param value short value
     * @return bytes value
     */
    public static byte[] short2Byte(short value) {
        byte[] ret = new byte[2];
        ret[1] = (byte) (0xff & value);
        ret[0] = (byte) ((0xff00 & value) >> 8);

        return ret;
    }


    /**
     * byte[] -> short
     *
     * @param value bytes value
     * @return short value
     */
    public static short byte2Short(byte[] value) {
        return byte2Short(value, 0);
    }

    /**
     * byte[] -> short
     *
     * @param value  bytes value
     * @param offset offset
     * @return short value
     */
    public static short byte2Short(byte[] value, int offset) {
        short ret = 0;
        for (int i = offset; i < offset + 2; i++) {
            ret <<= 8;
            ret |= value[i] & 0xff;
        }
        return ret;
    }

    /**
     * long -> byte[]
     *
     * @param value long value
     * @return bytes value
     */
    public static byte[] long2byte(long value) {
        byte[] ret = new byte[8];
        ret[7] = (byte) (0xffl & value);
        ret[6] = (byte) ((0xff00l & value) >> 8);
        ret[5] = (byte) ((0xff0000l & value) >> 16);
        ret[4] = (byte) ((0xff000000l & value) >> 24);
        ret[3] = (byte) ((0xff00000000l & value) >> 32);
        ret[2] = (byte) ((0xff0000000000l & value) >> 40);
        ret[1] = (byte) ((0xff000000000000l & value) >> 48);
        ret[0] = (byte) ((0xff00000000000000l & value) >> 56);


        return ret;
    }

    /**
     * byte[] -> long
     *
     * @param value bytes value
     * @return long value
     */
    public static long byte2Long(byte[] value) {
        return byte2Long(value, 0);
    }

    /**
     * byte[] -> long
     *
     * @param value  bytes value
     * @param offset offset
     * @return long value
     */
    public static long byte2Long(byte[] value, int offset) {
        long ret = 0;
        for (int i = offset; i < offset + 8; i++) {
            ret <<= 8;
            ret |= value[i] & 0xff;
        }
        return ret;
    }

    /**
     * bits -> int
     * <p/>
     * <p>
     * <pre>
     * 00000000000000001000011100111111
     * 1.index = 30 nbits = 2 return : 3
     * 2.index = 29 nbits = 3 return : 7
     * 2.index = 28 nbits = 4 return : 15
     * </pre>
     *
     * @param value
     * @param offset offset
     * @param count  number of bits
     * @return in value
     */
    public static int intValueOfBits(int value, int offset, int count) {
        return Math.toIntExact(unsignedIntValueOfBits(value, offset, count));
    }

    /**
     * bits -> unsigned int <br/>
     * <pre>
     * 00000000000000001000011100111111
     * 1.index = 30 nbits = 2 return : 3
     * 2.index = 29 nbits = 3 return : 7
     * 2.index = 28 nbits = 4 return : 15
     * </pre>
     *
     * @param value
     * @param offset 开始位置
     * @param count  连续多少个bit位
     * @return
     */
    public static long unsignedIntValueOfBits(long value, int offset, int count) {
        long result = 0;

        for (int i = 0; i < 32; i++) {
            result <<= 1;
            if (i >= offset && i < offset + count) {
                result++;
            }
        }

        result &= value;
        return result >> (32 - offset - count);
    }

    /**
     * long -> BinaryString padding with 0
     *
     * @param value long values
     * @return binary string
     */
    public static String binaryString(long value) {
        StringBuilder sb = new StringBuilder("0");

        long index = 0x4000000000000000L;

        for (int i = 1; i < 64; i++) {
            sb.append(0 != (index & value) ? 1 : 0);
            index >>= 1;
        }

        return sb.toString();
    }

    /**
     * short -> BinaryString padding with 0
     *
     * @param value int value
     * @return binary string
     */
    public static String binaryString(int value) {
        StringBuilder sb = new StringBuilder("0");

        int index = 0x40000000;

        for (int i = 1; i < 32; i++) {
            sb.append(0 != (index & value) ? 1 : 0);
            index >>= 1;
        }

        return sb.toString();
    }

    /**
     * short -> BinaryString padding with 0
     *
     * @param value short value
     * @return binary string
     */
    public static String binaryString(short value) {
        StringBuilder sb = new StringBuilder("0");

        short index = 0x4000;

        for (int i = 1; i < 16; i++) {
            sb.append(0 != (index & value) ? 1 : 0);
            index >>= 1;
        }

        return sb.toString();
    }

    /**
     * byte -> binaryString padding with 0
     *
     * @param value byte value
     * @return binary string
     */
    public static String binaryString(byte value) {
        StringBuilder sb = new StringBuilder("0");

        byte index = 0x40;

        for (int i = 1; i < 8; i++) {
            sb.append(0 != (index & value) ? 1 : 0);
            index >>= 1;
        }

        return sb.toString();
    }

    /**
     * byte -> bit set
     *
     * @param value bytes value
     * @return bit set
     */
    public static BitSet byte2BitSet(byte[] value) {
        int bits = value.length * 8;

        BitSet set = new BitSet(bits);
        int offset = 0;
        for (int i = 0; i < value.length; i++) {
            set.or(byte2BitSet(value[i], bits, offset));
            offset += 8;
        }

        return set;
    }

    /**
     * byte -> bit set
     *
     * @param value  byte
     * @param count  bits
     * @param offset offset
     * @return bit set
     */
    public static BitSet byte2BitSet(byte value, int count, int offset) {
        BitSet set = new BitSet(count);
        int index = 128; // 10000000

        for (int i = offset; i < offset + 8; i++) {
            set.set(i, 0 != (index & value));
            index >>= 1;
        }

        return set;
    }
}
