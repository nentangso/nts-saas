package org.nentangso.core.service.utils;

import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Date Time Utils Unit Tests power by nentangso.org")
public class NtsDateTimeUtilsTests {
    public static final LocalDate DATE_1 = LocalDate.of(2022, 8, 25);
    public static final LocalDate DATE_2 = LocalDate.of(2023, 1, 31);
    public static final LocalDate DATE_3 = LocalDate.of(2020, 11, 3);

    @Test
    @DisplayName("Least of dates (type LocalDate)")
    public void least() {
        LocalDate least = NtsDateTimeUtils.least(DATE_1, DATE_2, DATE_3);
        assertEquals(DATE_3, least);
        assertNull(NtsDateTimeUtils.least());
    }

    @Test
    @DisplayName("Greatest of dates (type LocalDate)")
    public void greatest() {
        LocalDate greatest = NtsDateTimeUtils.greatest(DATE_1, DATE_2, DATE_3);
        assertEquals(DATE_2, greatest);
        assertNull(NtsDateTimeUtils.greatest());
    }
}
