package org.nentangso.core.service.utils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class NtsDateTimeUtils {
    private NtsDateTimeUtils() {
    }

    public static LocalDate least(LocalDate... dates) {
        return Stream.of(dates)
            .min(Comparator.comparing(date -> date))
            .orElse(null);
    }

    @Deprecated(forRemoval = true, since = "1.0.4")
    public static LocalDate max(LocalDate... dates) {
        return greatest(dates);
    }

    public static LocalDate greatest(LocalDate... dates) {
        return Stream.of(dates)
            .max(Comparator.comparing(date -> date))
            .orElse(null);
    }
}
