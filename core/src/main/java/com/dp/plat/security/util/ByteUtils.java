package com.dp.plat.security.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteUtils {
    
    /**
     * 使用KMP算法在给定的字节数组中查找指定的字符串。
     *
     * @param text 主数组
     * @param pattern 要查找的子字符串
     * @return 子字符串在主数组中的起始位置，如果未找到则返回-1
     */
    public static int indexOf(byte[] text, String pattern) {
        return indexOf(text, pattern.getBytes());
    }

    /**
     * 使用KMP算法在给定的字节数组中查找指定的字节数组。
     *
     * @param text 主数组
     * @param pattern 要查找的子数组
     * @return 子数组在主数组中的起始位置，如果未找到则返回-1
     */
    public static int indexOf(byte[] text, byte[] pattern) {
        // 参数验证
        if (text == null || pattern == null || text.length == 0 || pattern.length == 0) {
            return -1;
        }
        if (pattern.length > text.length) {
            return -1;
        }

        int[] lps = computeLPSArray(pattern);
        int i = 0; // 主数组的索引
        int j = 0; // 模式数组的索引

        while (i < text.length) {
            if (pattern[j] == text[i]) {
                i++;
                j++;
            }

            if (j == pattern.length) {
                return i - j; // 找到了匹配的子数组，返回其起始位置
            } else if (i < text.length && pattern[j] != text[i]) {
                // 发生不匹配
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i = i + 1;
                }
            }
        }

        return -1; // 未找到匹配的子数组
    }

    /**
     * 计算LPS数组，用于KMP算法中的部分匹配表。
     *
     * @param pattern 模式数组
     * @return LPS数组
     */
    private static int[] computeLPSArray(byte[] pattern) {
        int len = 0; // 最长前缀后缀的长度
        int i = 1;
        int[] lps = new int[pattern.length]; // LPS数组

        lps[0] = 0; // LPS[0] 总是0

        while (i < pattern.length) {
            if (pattern[i] == pattern[len]) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
    
    /**
     * 向DirectByteBuffer中添加字节数组。
     *
     * @param builder 当前的DirectByteBuffer
     * @param bytes 要添加的字节数组
     * @return 
     */
    public static ByteBuffer append(ByteBuffer builder, byte[] bytes) {
        // 检查容量是否足够
        if (builder.remaining() < bytes.length) {
            // 扩展DirectByteBuffer的容量
            builder = expandDirectByteBuffer(builder, bytes.length - builder.remaining());
        }

        // 添加字节数组
        builder.put(bytes);
        return builder;
    }
    
    /**
     * 扩展DirectByteBuffer的容量。
     *
     * @param builder 当前的DirectByteBuffer
     * @param additionalCapacity 需要额外的容量
     * @return 扩展后的DirectByteBuffer
     */
    private static ByteBuffer expandDirectByteBuffer(ByteBuffer builder, int additionalCapacity) {
        // 计算新的DirectByteBuffer容量
        int newCapacity = Math.max(builder.capacity() * 2, builder.capacity() + additionalCapacity);

        // 创建一个新的DirectByteBuffer，容量为原DirectByteBuffer容量的两倍或足够容纳额外的数据
        ByteBuffer newByteBuffer = ByteBuffer.allocateDirect(newCapacity);

        // 复制原有DirectByteBuffer中的数据到新的DirectByteBuffer
        builder.flip();
        newByteBuffer.put(builder);

        // 返回新的DirectByteBuffer
        return newByteBuffer;
    }
    
    /**
     * 从DirectByteBuffer中读取字节数组。
     *
     * @param builder DirectByteBuffer
     * @return 读取的字节数组
     */
    public static byte[] readBytes(ByteBuffer builder) {
        builder.flip();
        byte[] result = new byte[builder.limit()];
        builder.get(result);
        return result;
    }
    
    /**
     * 向DirectByteBuffer中添加字节数组。
     *
     * @param builder 当前的DirectByteBuffer
     * @param bytes 要添加的字节数组
     * @return 
     * @throws IOException 
     */
    public static ByteArrayOutputStream append(ByteArrayOutputStream builder, byte[] bytes) throws IOException {
        // 添加字节数组
        builder.write(bytes);
        return builder;
    }
    
}
