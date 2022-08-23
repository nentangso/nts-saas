package org.nentangso.core.service.utils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class NtsDateTimeUtils {
    private NtsDateTimeUtils() {
    }

    public static LocalDate max(LocalDate... dates) {
        return Stream.of(dates)
            .max(Comparator.comparing(date -> date))
            .orElse(null);
    }
}
