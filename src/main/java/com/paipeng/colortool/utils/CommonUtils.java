package com.paipeng.colortool.utils;

import java.util.Locale;

public class CommonUtils {
    public static Locale getCurrentLanguageLocale() {
        Locale locale;
        if (true) {
            locale = new Locale("zh", "Zh");

        } else {
            locale = new Locale("en", "En");

        }
        return locale;
    }
}
