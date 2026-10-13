package com.cristian.expensetracker.vendors;

import java.util.Locale;

public class VendorKeys {

    public static String normalizeKey(String displayName) {
        return displayName.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
