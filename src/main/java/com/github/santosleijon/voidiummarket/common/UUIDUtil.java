package com.github.santosleijon.voidiummarket.common;

import java.util.UUID;

public class UUIDUtil {

    private UUIDUtil() {
        // Private constructor to prevent instantiation
    }

    public static String shorten(UUID id) {
        return id.toString().substring(0, 8) + "...";
    }
}
