package org.nentangso.core.service.utils;

import com.google.errorprone.annotations.InlineMe;

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
    @InlineMe(replacement = "NtsDateTimeUtils.greatest(dates)", imports = "org.nentangso.core.service.utils.NtsDateTimeUtils")
    public static LocalDate max(LocalDate... dates) {
        return greatest(dates);
    }

    public static LocalDate greatest(LocalDate... dates) {
        return Stream.of(dates)
            .max(Comparator.comparing(date -> date))
            .orElse(null);
    }
}
