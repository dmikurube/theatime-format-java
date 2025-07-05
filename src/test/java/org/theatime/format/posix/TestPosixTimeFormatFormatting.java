/*
 * Copyright 2023 Dai MIKURUBE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.theatime.format.posix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestPosixTimeFormatFormatting {

    @Test
    public void testFormattingByLowerA() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_10a").toDateTimeFormatter();
        assertEquals("       Mon", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testFormattingByUpperA1() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%8A").toDateTimeFormatter();
        assertEquals("  Monday",
                     formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" Tuesday",
                     formatter.format(ZonedDateTime.of(2023, 4, 18, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("Wednesday",
                     formatter.format(ZonedDateTime.of(2023, 4, 19, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("Thursday",
                     formatter.format(ZonedDateTime.of(2023, 4, 20, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testFormattingByUpperA2() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%8A").toDateTimeFormatter();
        System.out.println(formatter.parse("  Monday").getLong(ChronoField.DAY_OF_WEEK));
        System.out.println(formatter.parse(" Tuesday").getLong(ChronoField.DAY_OF_WEEK));
        System.out.println(formatter.parse("Wednesday").getLong(ChronoField.DAY_OF_WEEK));
        System.out.println(formatter.parse("Thursday").getLong(ChronoField.DAY_OF_WEEK));
    }

    @Test
    public void testFormattingByUpperB() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%7B").toDateTimeFormatter();
        assertEquals("January",
                     formatter.format(ZonedDateTime.of(2023, 1, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("February",
                     formatter.format(ZonedDateTime.of(2023, 2, 18, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("  March",
                     formatter.format(ZonedDateTime.of(2023, 3, 19, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("    May",
                     formatter.format(ZonedDateTime.of(2023, 5, 20, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" August",
                     formatter.format(ZonedDateTime.of(2023, 8, 20, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("September",
                     formatter.format(ZonedDateTime.of(2023, 9, 20, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testFormattingLowerY() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%y").toDateTimeFormatter();
        assertEquals(2000, formatter.parse("00").getLong(ChronoField.YEAR));
        assertEquals(2068, formatter.parse("68").getLong(ChronoField.YEAR));
        assertEquals(1969, formatter.parse("69").getLong(ChronoField.YEAR));
        assertEquals(1970, formatter.parse("70").getLong(ChronoField.YEAR));
        assertEquals(1999, formatter.parse("99").getLong(ChronoField.YEAR));
    }

    @Test
    public void testFormattingMixed1() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%a%A%10q%b%B").toDateTimeFormatter();
        assertEquals("MonMonday      %10qAprApril",
                     formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testFormattingMixed2() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%C ... %y").toDateTimeFormatter();
        assertEquals("20 ... 23", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        final TemporalAccessor accessor = formatter.parse("19 ... 87");
        assertEquals(87, accessor.getLong(PosixFields.YEAR_OF_POSIX_CENTURY_1969_2068));
        assertEquals(19, accessor.getLong(PosixFields.POSIX_CENTURY));
        assertEquals(1987, accessor.getLong(ChronoField.YEAR));
    }

    /*
     * Import test cases that failed in reftests.
     */
    @ParameterizedTest
    @CsvSource({
            "OCT,%^2b,2020,10,26,7,48,59,526929475",
            // "'        29',%-10d,1985,9,29,4,23,20,62491315",
            // "0000000TUE,%0#10a,2008,9,2,5,31,03,340494545",
    })
    public void testFormattingSingleSpecification(
            final String expectedFormatted,
            final String format,
            final int year,
            final int monthValue,
            final int dayOfMonth,
            final int hourOfDay,
            final int minuteOfHour,
            final int secondOfMinute,
            final int nanoOfSecond) {
        final DateTimeFormatter actualFormatter = PosixTimeFormat.compile(format).toDateTimeFormatter();
        final OffsetDateTime actualDateTime = OffsetDateTime.of(
                year, monthValue, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond, ZoneOffset.UTC);
        final String actualFormatted = actualFormatter.format(actualDateTime);
        assertEquals(expectedFormatted, actualFormatted);
    }
}
