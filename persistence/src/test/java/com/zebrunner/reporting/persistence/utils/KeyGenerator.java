package com.zebrunner.reporting.persistence.utils;

import java.util.Random;

public class KeyGenerator {

    public static Integer getKey() {
        Random random = new Random();
        int key = 0;

        for (int i = 0; i < 10; i++) {
            key = key * 10 + random.nextInt();
        }
        return key;
    }
}
