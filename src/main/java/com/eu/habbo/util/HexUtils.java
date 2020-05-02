package com.eu.habbo.util;

import java.nio.charset.StandardCharsets;

public class HexUtils {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] toBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }

    public static String pretty(byte[] array) {
        final int width = 32;

        StringBuilder builder = new StringBuilder();

        for (int rowOffset = 0; rowOffset < array.length; rowOffset += width) {
            builder.append(String.format("%06d:  ", rowOffset));

            for (int index = 0; index < width; index++) {
                if (rowOffset + index < array.length) {
                    builder.append(String.format("%02x ", array[rowOffset + index]));
                } else {
                    builder.append("   ");
                }
            }

            int asciiWidth = Math.min(width, array.length - rowOffset);
            builder.append("  |  ");
            builder.append(new String(array, rowOffset, asciiWidth, StandardCharsets.UTF_8).replaceAll("\r\n", " ").replaceAll("\n", " "));

            if (rowOffset + width < array.length) {
                builder.append(String.format("%n"));
            }
        }

        return builder.toString();
    }

}
