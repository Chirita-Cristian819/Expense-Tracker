package com.cristian.expensetracker.vendors.tests;

import com.cristian.expensetracker.vendors.utils.VendorKeys;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VendorKeysTest {

    @ParameterizedTest
    @CsvSource({"'  LiDl', lidl",
                "'  mega iMage ', mega image",
                "'Băcănia Vestului', băcănia vestului"})
    void normalizeKeyTest(String input, String expected) {
        assertEquals(expected,  VendorKeys.normalizeKey(input));
    }
}
