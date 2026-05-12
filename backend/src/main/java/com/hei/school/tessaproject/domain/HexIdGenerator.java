package com.hei.school.tessaproject.domain;

import java.security.SecureRandom;

final class HexIdGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private HexIdGenerator() {
    }

    static String newId() {
        byte[] bytes = new byte[12];
        RANDOM.nextBytes(bytes);
        char[] chars = new char[24];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xff;
            chars[i * 2] = HEX[value >>> 4];
            chars[i * 2 + 1] = HEX[value & 0x0f];
        }
        return new String(chars);
    }
}
