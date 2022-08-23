package org.nentangso.core.service.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class NtsNumberUtils {
    private NtsNumberUtils() {
    }

    public static BigDecimal parseToDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Integer) {
            return new BigDecimal((int) value);
        }
        if (value instanceof Long) {
            return new BigDecimal((long) value);
        }
        if (value instanceof Float) {
            return new BigDecimal((float) value);
        }
        if (value instanceof Double) {
            return new BigDecimal((double) value);
        }
        return null;
    }

    public static BigDecimal parseToDecimal(String value) {
        return StringUtils.isNotBlank(value) ? new BigDecimal(value) : null;
    }

    public static boolean equals(BigDecimal num1, BigDecimal num2) {
        if (num1 == null && num2 == null) return true;
        if (num1 == null || num2 == null) return false;
        return num1.compareTo(num2) == 0;
    }

    public static BigDecimal min(BigDecimal num1, BigDecimal num2) {
        assert num1 != null;
        assert num2 != null;
        return num1.compareTo(num2) < 0 ? num1 : num2;
    }
}
