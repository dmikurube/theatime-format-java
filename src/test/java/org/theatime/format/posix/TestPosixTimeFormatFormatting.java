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
    public void testDateTimeFormatterLowerE() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%e").toDateTimeFormatter();
        assertEquals(" 1", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 9", formatter.format(ZonedDateTime.of(2023, 4, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("17", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("31", formatter.format(ZonedDateTime.of(2023, 1, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerEWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%0e").toDateTimeFormatter();
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("09", formatter.format(ZonedDateTime.of(2023, 4, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("17", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerEWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%4e").toDateTimeFormatter();
        assertEquals("   1", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("   9", formatter.format(ZonedDateTime.of(2023, 4, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("  17", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("  31", formatter.format(ZonedDateTime.of(2023, 1, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
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
            "' 1',%e,2023,1,1,12,0,0,0",
            "' 9',%e,2023,1,9,12,0,0,0",
            "'17',%e,2023,1,17,12,0,0,0",
            "'31',%e,2023,1,31,12,0,0,0",
            "'01',%0e,2023,1,1,12,0,0,0",
            "'09',%0e,2023,1,9,12,0,0,0",
            "'   1',%4e,2023,1,1,12,0,0,0",
            "'  17',%4e,2023,1,17,12,0,0,0",
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

    @Test
    public void testFormattingByLowerM() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%m").toDateTimeFormatter();
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("02", formatter.format(ZonedDateTime.of(2023, 2, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("03", formatter.format(ZonedDateTime.of(2023, 3, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByLowerMwithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_4m").toDateTimeFormatter();
        assertEquals("   1", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  10", formatter.format(ZonedDateTime.of(2023, 10, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  12", formatter.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByLowerMwithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%04m").toDateTimeFormatter();
        assertEquals("0001", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0010", formatter.format(ZonedDateTime.of(2023, 10, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0012", formatter.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperH() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%H").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 15, 1, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("23", formatter.format(ZonedDateTime.of(2023, 1, 15, 23, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperHwithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_4H").toDateTimeFormatter();
        assertEquals("   0", formatter.format(ZonedDateTime.of(2023, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  12", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  23", formatter.format(ZonedDateTime.of(2023, 1, 15, 23, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperHwithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%04H").toDateTimeFormatter();
        assertEquals("0000", formatter.format(ZonedDateTime.of(2023, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0012", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0023", formatter.format(ZonedDateTime.of(2023, 1, 15, 23, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperI() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%I").toDateTimeFormatter();
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 15, 1, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("11", formatter.format(ZonedDateTime.of(2023, 1, 15, 11, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 15, 13, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("11", formatter.format(ZonedDateTime.of(2023, 1, 15, 23, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperIwithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_4I").toDateTimeFormatter();
        assertEquals("  12", formatter.format(ZonedDateTime.of(2023, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("   1", formatter.format(ZonedDateTime.of(2023, 1, 15, 1, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  12", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("   1", formatter.format(ZonedDateTime.of(2023, 1, 15, 13, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperIwithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%04I").toDateTimeFormatter();
        assertEquals("0012", formatter.format(ZonedDateTime.of(2023, 1, 15, 0, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0001", formatter.format(ZonedDateTime.of(2023, 1, 15, 1, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0012", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0001", formatter.format(ZonedDateTime.of(2023, 1, 15, 13, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByLowerJ() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%j").toDateTimeFormatter();
        assertEquals("001", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("032", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("059", formatter.format(ZonedDateTime.of(2023, 2, 28, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("060", formatter.format(ZonedDateTime.of(2023, 3, 1, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("365", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("366", formatter.format(ZonedDateTime.of(2024, 12, 31, 12, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByLowerJwithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_5j").toDateTimeFormatter();
        assertEquals("    1", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("   32", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  365", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByLowerJwithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%05j").toDateTimeFormatter();
        assertEquals("00001", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("00032", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("00365", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperY() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%Y").toDateTimeFormatter();
        assertEquals("2023", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("1999", formatter.format(ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC"))));
        assertEquals("2000", formatter.format(ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("1", formatter.format(ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperYwithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_6Y").toDateTimeFormatter();
        assertEquals("  2023", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("    99", formatter.format(ZonedDateTime.of(99, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC"))));
        assertEquals("     1", formatter.format(ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperYwithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%06Y").toDateTimeFormatter();
        assertEquals("002023", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("000099", formatter.format(ZonedDateTime.of(99, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC"))));
        assertEquals("000001", formatter.format(ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperS() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%S").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 1, 0, ZoneId.of("UTC"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 30, 0, ZoneId.of("UTC"))));
        assertEquals("59", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 59, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperSwithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_4S").toDateTimeFormatter();
        assertEquals("   0", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  30", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 30, 0, ZoneId.of("UTC"))));
        assertEquals("  59", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 59, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperSwithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%04S").toDateTimeFormatter();
        assertEquals("0000", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0030", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 30, 0, ZoneId.of("UTC"))));
        assertEquals("0059", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 59, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperM() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%M").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 1, 0, 0, ZoneId.of("UTC"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 30, 0, 0, ZoneId.of("UTC"))));
        assertEquals("59", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 59, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperMwithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_4M").toDateTimeFormatter();
        assertEquals("   0", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  30", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 30, 0, 0, ZoneId.of("UTC"))));
        assertEquals("  59", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 59, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void testFormattingByUpperMwithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%04M").toDateTimeFormatter();
        assertEquals("0000", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0030", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 30, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0059", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 59, 0, 0, ZoneId.of("UTC"))));
    }
}
