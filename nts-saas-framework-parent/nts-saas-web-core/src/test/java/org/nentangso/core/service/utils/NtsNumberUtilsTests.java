package org.nentangso.core.service.utils;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Number Utils Unit Tests power by nentangso.org")
public class NtsNumberUtilsTests {

    public static final BigDecimal ONE_DOT_ZERO = BigDecimal.ONE.setScale(1, RoundingMode.HALF_UP);

    @Test
    @DisplayName("Parse to BigDecimal: parse valid value")
    public void parseToDecimal() {
        assertEquals(BigDecimal.ONE, NtsNumberUtils.parseToDecimal(BigDecimal.ONE));
        assertEquals(BigDecimal.ONE, NtsNumberUtils.parseToDecimal(1));
        assertEquals(BigDecimal.ONE, NtsNumberUtils.parseToDecimal(1L));
        assertEquals(ONE_DOT_ZERO, NtsNumberUtils.parseToDecimal(1.0F));
        assertEquals(ONE_DOT_ZERO, NtsNumberUtils.parseToDecimal(1.0D));
        assertNull(NtsNumberUtils.parseToDecimal(null));
    }

    @Test
    @DisplayName("Parse to BigDecimal: throws ClassCastException")
    public void parseToDecimal_throws_class_cast_exception() {
        assertThrows(ClassCastException.class, () -> NtsNumberUtils.parseToDecimal(Boolean.FALSE));
    }
}
