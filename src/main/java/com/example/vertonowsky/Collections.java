package com.example.vertonowsky;

import java.util.Collection;

public class Collections {

    public static boolean isNullOrEmpty(Collection< ? > collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
