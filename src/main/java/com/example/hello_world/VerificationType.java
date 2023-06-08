package com.example.hello_world;

import java.util.Arrays;

public enum VerificationType {

    EMAIL_VERIFICATION_NEW(0),
    EMAIL_VERIFICATION_LOGIN_ATTEMPT(1),
    PASSWORD_RECOVER_NEW(2);

    final int index;

    VerificationType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static boolean isValidIndex(int index) {
        return Arrays.stream(values()).anyMatch(type -> type.getIndex() == index);
    }


}
