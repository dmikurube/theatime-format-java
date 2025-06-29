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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestPosixTimeFormat {
    @Test
    public void testEmpty() {
        assertFormat("");
    }

    @Test
    public void testEndWithSinglePercent() {
        assertFormat("%", Literal.of("%", C));
        assertFormat("foo%", Literal.of("foo", C), Literal.of("%", C));
    }

    @Test
    public void testLowerA() {
        assertFormat("%a", new LowerA(C));
        assertFormat("%0a", new LowerA(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
    }

    @Test
    public void testLowerE() {
        assertFormat("%e", new LowerE(C));
        assertFormat("%0e", new LowerE(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_e", new LowerE(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-e", new LowerE(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%3e", new LowerE(new Specification.Context(false, false, 3, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testLowerD() {
        assertFormat("%d", new LowerD(C));
        assertFormat("%0d", new LowerD(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_d", new LowerD(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-d", new LowerD(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%4d", new LowerD(new Specification.Context(false, false, 4, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testLowerM() {
        assertFormat("%m", new LowerM(C));
        assertFormat("%0m", new LowerM(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_m", new LowerM(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-m", new LowerM(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%4m", new LowerM(new Specification.Context(false, false, 4, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testUpperY() {
        assertFormat("%Y", new UpperY(C));
        assertFormat("%0Y", new UpperY(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_Y", new UpperY(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-Y", new UpperY(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%6Y", new UpperY(new Specification.Context(false, false, 6, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testUpperH() {
        assertFormat("%H", new UpperH(C));
        assertFormat("%0H", new UpperH(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_H", new UpperH(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-H", new UpperH(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%3H", new UpperH(new Specification.Context(false, false, 3, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testUpperI() {
        assertFormat("%I", new UpperI(C));
        assertFormat("%0I", new UpperI(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_I", new UpperI(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-I", new UpperI(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%3I", new UpperI(new Specification.Context(false, false, 3, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testLowerJ() {
        assertFormat("%j", new LowerJ(C));
        assertFormat("%0j", new LowerJ(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_j", new LowerJ(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-j", new LowerJ(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%5j", new LowerJ(new Specification.Context(false, false, 5, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testLowerT() {
        assertFormat("%t", Literal.of("\t", C));
    }

    @Test
    public void testLowerG() {
        assertFormat("%g", new LowerG(C));
        assertFormat("%0g", new LowerG(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_g", new LowerG(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-g", new LowerG(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%4g", new LowerG(new Specification.Context(false, false, 4, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testUpperG() {
        assertFormat("%G", new UpperG(C));
        assertFormat("%0G", new UpperG(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_G", new UpperG(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-G", new UpperG(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%6G", new UpperG(new Specification.Context(false, false, 6, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testUpperS() {
        assertFormat("%S", new UpperS(C));
        assertFormat("%0S", new UpperS(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_S", new UpperS(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-S", new UpperS(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%3S", new UpperS(new Specification.Context(false, false, 3, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testUpperM() {
        assertFormat("%M", new UpperM(C));
        assertFormat("%0M", new UpperM(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_M", new UpperM(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-M", new UpperM(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%3M", new UpperM(new Specification.Context(false, false, 3, -1, '\0', '\0', "", 0, 0)));
    }

    @Test
    public void testLiteral() {
        assertFormat("foo",
                     Literal.of("foo", C));
        assertFormat("%&",
                     Literal.of("%&", C));
        assertFormat("%10&",
                     Literal.of("%10&", new Specification.Context(false, false, 10, -1, '0', '\0', "", 0, 0)));
        assertFormat("foo%&bar",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C));
        assertFormat("%&foo%&",
                     Literal.of("%&", C),
                     Literal.of("foo", C),
                     Literal.of("%&", C));
        assertFormat("%&foo%&bar%&",
                     Literal.of("%&", C),
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     Literal.of("%&", C));
        assertFormat("foo%&bar%&baz",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C));
    }

    @Test
    public void testMixed() {
        assertFormat("foo%bbar",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C));
        assertFormat("%bfoo%a",
                     new LowerB(C),
                     Literal.of("foo", C),
                     new LowerA(C));
        assertFormat("foo%&bar%bbaz",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     new LowerB(C),
                     Literal.of("baz", C));
        assertFormat("foo%bbar%&baz",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C));
        assertFormat("foo%&bar%bbaz%a",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     new LowerB(C),
                     Literal.of("baz", C),
                     new LowerA(C));
        assertFormat("foo%bbar%&baz%a",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C),
                     new LowerA(C));
        assertFormat("foo%&bar%bbaz%$",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     new LowerB(C),
                     Literal.of("baz", C),
                     Literal.of("%$", C));
        assertFormat("foo%bbar%&baz%$",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C),
                     Literal.of("%$", C));
        assertFormat("foo%bbar%&baz%",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C),
                     Literal.of("%", C));
        assertFormat("%b %e, %Y",
                     new LowerB(C),
                     Literal.of(" ", C),
                     new LowerE(C),
                     Literal.of(", ", C),
                     new UpperY(C));
    }

    @ParameterizedTest
    @CsvSource({
            "abc",
            "%",
    })
    public void testUnmatch(final String format) {
        assertFormat(format, Literal.of(format, C));
    }

    @Test
    public void testDateTimeFormatter() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%a%A%10q%b%B").toDateTimeFormatter();
        assertEquals("MonMonday      %10qAprApril",
                     formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatter2() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_10a").toDateTimeFormatter();
        assertEquals("       Mon", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatter3() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%C ... %y").toDateTimeFormatter();
        assertEquals("20 ... 23", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        final TemporalAccessor accessor = formatter.parse("19 ... 87");
        assertEquals(87, accessor.getLong(PosixFields.YEAR_OF_POSIX_CENTURY_1969_2068));
        assertEquals(19, accessor.getLong(PosixFields.POSIX_CENTURY));
        assertEquals(1987, accessor.getLong(ChronoField.YEAR));
    }

    @Test
    public void testDateTimeFormatter4() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%y").toDateTimeFormatter();
        assertEquals(2000, formatter.parse("00").getLong(ChronoField.YEAR));
        assertEquals(2068, formatter.parse("68").getLong(ChronoField.YEAR));
        assertEquals(1969, formatter.parse("69").getLong(ChronoField.YEAR));
        assertEquals(1970, formatter.parse("70").getLong(ChronoField.YEAR));
        assertEquals(1999, formatter.parse("99").getLong(ChronoField.YEAR));
    }

    @Test
    public void testDateTimeFormatter5() {
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
    public void testDateTimeFormatter6() {
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
    public void testDateTimeFormatter7() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%8A").toDateTimeFormatter();
        System.out.println(formatter.parse("  Monday").getLong(ChronoField.DAY_OF_WEEK));
        System.out.println(formatter.parse(" Tuesday").getLong(ChronoField.DAY_OF_WEEK));
        System.out.println(formatter.parse("Wednesday").getLong(ChronoField.DAY_OF_WEEK));
        System.out.println(formatter.parse("Thursday").getLong(ChronoField.DAY_OF_WEEK));
    }

    @Test
    public void testDateTimeFormatterLowerN1() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%n").toDateTimeFormatter();
        assertEquals("\n", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerN2() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%n%n%n").toDateTimeFormatter();
        assertEquals("\n\n\n", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerT1() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%t").toDateTimeFormatter();
        assertEquals("\t", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerT2() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%t%t%t").toDateTimeFormatter();
        assertEquals("\t\t\t", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerG1() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%g").toDateTimeFormatter();
        assertEquals("23", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerG2() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%g").toDateTimeFormatter();
        assertEquals("24", formatter.format(ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerG3() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%g").toDateTimeFormatter();
        assertEquals("22", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerGboundaries() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%g").toDateTimeFormatter();

        // Week-based year boundaries - January 1st cases
        assertEquals("22", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Sunday, week-based year 2022
        assertEquals("24", formatter.format(ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Monday, week-based year 2024
        assertEquals("25", formatter.format(ZonedDateTime.of(2025, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Wednesday, week-based year 2025
        assertEquals("26", formatter.format(ZonedDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Thursday, week-based year 2026
        assertEquals("26", formatter.format(ZonedDateTime.of(2027, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Friday, week-based year 2026
        assertEquals("27", formatter.format(ZonedDateTime.of(2028, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Saturday, week-based year 2027
        assertEquals("21", formatter.format(ZonedDateTime.of(2022, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Saturday, week-based year 2021

        // December 31st cases - end of year boundaries
        assertEquals("22", formatter.format(ZonedDateTime.of(2022, 12, 31, 12, 0, 0, 0, ZoneId.of("UTC")))); // Saturday, week-based year 2022
        assertEquals("23", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("UTC")))); // Sunday, week-based year 2023
        assertEquals("25", formatter.format(ZonedDateTime.of(2024, 12, 31, 12, 0, 0, 0, ZoneId.of("UTC")))); // Tuesday, week-based year 2025

        // Cases where January 1st belongs to previous/same week-based year
        assertEquals("20", formatter.format(ZonedDateTime.of(2020, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Wednesday, week-based year 2020
        assertEquals("20", formatter.format(ZonedDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Friday, week-based year 2020

        // Century transitions
        assertEquals("98", formatter.format(ZonedDateTime.of(1999, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Friday, week-based year 1998
        assertEquals("99", formatter.format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Saturday, week-based year 1999
        assertEquals("01", formatter.format(ZonedDateTime.of(2001, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")))); // Monday, week-based year 2001
    }

    @Test
    public void testDateTimeFormatterLowerGwithPrecision() {
        // Test precision padding
        final DateTimeFormatter formatter4 = PosixTimeFormat.compile("%4g").toDateTimeFormatter();
        assertEquals("0023", formatter4.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0000", formatter4.format(ZonedDateTime.of(2000, 6, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("0099", formatter4.format(ZonedDateTime.of(1999, 6, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));

        final DateTimeFormatter formatter6 = PosixTimeFormat.compile("%6g").toDateTimeFormatter();
        assertEquals("000023", formatter6.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));

        // Test space padding - libc behavior: treats last 2 digits as variable-width number
        final DateTimeFormatter formatterSpace = PosixTimeFormat.compile("%_4g").toDateTimeFormatter();
        assertEquals("  23", formatterSpace.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));
        assertEquals("   0", formatterSpace.format(ZonedDateTime.of(2000, 6, 15, 12, 0, 0, 0, ZoneId.of("UTC"))));  // "0" not "00"
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
    public void testDateTimeFormatterLowerD() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%d").toDateTimeFormatter();
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("09", formatter.format(ZonedDateTime.of(2023, 4, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("17", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("31", formatter.format(ZonedDateTime.of(2023, 1, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerDWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_d").toDateTimeFormatter();
        assertEquals(" 1", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 9", formatter.format(ZonedDateTime.of(2023, 4, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("17", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerDWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%4d").toDateTimeFormatter();
        assertEquals("0001", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0009", formatter.format(ZonedDateTime.of(2023, 4, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0017", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0031", formatter.format(ZonedDateTime.of(2023, 1, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerDMonthBoundaries() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%d").toDateTimeFormatter();
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("31", formatter.format(ZonedDateTime.of(2023, 1, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("28", formatter.format(ZonedDateTime.of(2023, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("29", formatter.format(ZonedDateTime.of(2024, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 4, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerDWithDifferentPrecisions() {
        final DateTimeFormatter formatter3 = PosixTimeFormat.compile("%3d").toDateTimeFormatter();
        assertEquals("001", formatter3.format(ZonedDateTime.of(2023, 6, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("015", formatter3.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("031", formatter3.format(ZonedDateTime.of(2023, 5, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter5 = PosixTimeFormat.compile("%5d").toDateTimeFormatter();
        assertEquals("00001", formatter5.format(ZonedDateTime.of(2023, 6, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("00031", formatter5.format(ZonedDateTime.of(2023, 5, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerDWithSpacePaddingVariations() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_d").toDateTimeFormatter();
        assertEquals(" 1", formatter.format(ZonedDateTime.of(2023, 6, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 5", formatter.format(ZonedDateTime.of(2023, 6, 5, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("15", formatter.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("31", formatter.format(ZonedDateTime.of(2023, 5, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter4 = PosixTimeFormat.compile("%_4d").toDateTimeFormatter();
        assertEquals("   1", formatter4.format(ZonedDateTime.of(2023, 6, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("  15", formatter4.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterUpperD() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%D").toDateTimeFormatter();
        assertEquals("01/01/23", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("04/09/23", formatter.format(ZonedDateTime.of(2023, 4, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12/31/99", formatter.format(ZonedDateTime.of(1999, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("02/29/00", formatter.format(ZonedDateTime.of(2000, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("07/04/76", formatter.format(ZonedDateTime.of(1976, 7, 4, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperDCenturyTransition() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%D").toDateTimeFormatter();
        assertEquals("12/31/68", formatter.format(ZonedDateTime.of(2068, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01/01/69", formatter.format(ZonedDateTime.of(1969, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12/31/69", formatter.format(ZonedDateTime.of(1969, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01/01/70", formatter.format(ZonedDateTime.of(1970, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperDLeapYears() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%D").toDateTimeFormatter();
        // Test leap year dates
        assertEquals("02/29/00", formatter.format(ZonedDateTime.of(2000, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("02/29/04", formatter.format(ZonedDateTime.of(2004, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("02/29/20", formatter.format(ZonedDateTime.of(2020, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("02/29/96", formatter.format(ZonedDateTime.of(1996, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        // Test non-leap year century years
        assertEquals("02/28/00", formatter.format(ZonedDateTime.of(1900, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("03/01/00", formatter.format(ZonedDateTime.of(1900, 3, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperDMonthBoundaries() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%D").toDateTimeFormatter();
        // Test all months with boundary days
        assertEquals("01/01/23", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01/31/23", formatter.format(ZonedDateTime.of(2023, 1, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("02/01/23", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("02/28/23", formatter.format(ZonedDateTime.of(2023, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("04/30/23", formatter.format(ZonedDateTime.of(2023, 4, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("05/01/23", formatter.format(ZonedDateTime.of(2023, 5, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("06/30/23", formatter.format(ZonedDateTime.of(2023, 6, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("09/30/23", formatter.format(ZonedDateTime.of(2023, 9, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("11/30/23", formatter.format(ZonedDateTime.of(2023, 11, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12/31/23", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperDHistoricalDates() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%D").toDateTimeFormatter();
        // Important historical dates
        assertEquals("07/04/76", formatter.format(ZonedDateTime.of(1976, 7, 4, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // US Bicentennial
        assertEquals("01/01/00", formatter.format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Y2K
        assertEquals("09/11/01", formatter.format(ZonedDateTime.of(2001, 9, 11, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // 9/11
        assertEquals("12/07/41", formatter.format(ZonedDateTime.of(1941, 12, 7, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Pearl Harbor
        assertEquals("11/09/89", formatter.format(ZonedDateTime.of(1989, 11, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Berlin Wall fell
        assertEquals("07/20/69", formatter.format(ZonedDateTime.of(1969, 7, 20, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Moon landing
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperDYearRange() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%D").toDateTimeFormatter();
        // Test the full POSIX century range boundaries
        assertEquals("01/01/69", formatter.format(ZonedDateTime.of(1969, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Start of 20th century mapping
        assertEquals("12/31/99", formatter.format(ZonedDateTime.of(1999, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // End of 20th century
        assertEquals("01/01/00", formatter.format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Start of 21st century mapping
        assertEquals("12/31/68", formatter.format(ZonedDateTime.of(2068, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // End of 21st century mapping

        // Test years around boundaries
        assertEquals("12/31/68", formatter.format(ZonedDateTime.of(1968, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Last day of 1968 (20th century)
        assertEquals("01/01/69", formatter.format(ZonedDateTime.of(2069, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // 2069 wraps to 69
    }

    @Test
    public void testDateTimeFormatterUpperF() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%F").toDateTimeFormatter();
        assertEquals("2023-01-01", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-04-09", formatter.format(ZonedDateTime.of(2023, 4, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("1999-12-31", formatter.format(ZonedDateTime.of(1999, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2000-02-29", formatter.format(ZonedDateTime.of(2000, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("1976-07-04", formatter.format(ZonedDateTime.of(1976, 7, 4, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperFLeapYears() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%F").toDateTimeFormatter();
        // Test leap year dates
        assertEquals("2000-02-29", formatter.format(ZonedDateTime.of(2000, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2004-02-29", formatter.format(ZonedDateTime.of(2004, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2020-02-29", formatter.format(ZonedDateTime.of(2020, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("1996-02-29", formatter.format(ZonedDateTime.of(1996, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        // Test non-leap year century years
        assertEquals("1900-02-28", formatter.format(ZonedDateTime.of(1900, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("1900-03-01", formatter.format(ZonedDateTime.of(1900, 3, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperFMonthBoundaries() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%F").toDateTimeFormatter();
        // Test all months with boundary days
        assertEquals("2023-01-01", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-01-31", formatter.format(ZonedDateTime.of(2023, 1, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-02-01", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-02-28", formatter.format(ZonedDateTime.of(2023, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-04-30", formatter.format(ZonedDateTime.of(2023, 4, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-05-01", formatter.format(ZonedDateTime.of(2023, 5, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-06-30", formatter.format(ZonedDateTime.of(2023, 6, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-09-30", formatter.format(ZonedDateTime.of(2023, 9, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-11-30", formatter.format(ZonedDateTime.of(2023, 11, 30, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023-12-31", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperFHistoricalDates() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%F").toDateTimeFormatter();
        // Important historical dates in ISO 8601 format
        assertEquals("1976-07-04", formatter.format(ZonedDateTime.of(1976, 7, 4, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // US Bicentennial
        assertEquals("2000-01-01", formatter.format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Y2K
        assertEquals("2001-09-11", formatter.format(ZonedDateTime.of(2001, 9, 11, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // 9/11
        assertEquals("1941-12-07", formatter.format(ZonedDateTime.of(1941, 12, 7, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Pearl Harbor
        assertEquals("1989-11-09", formatter.format(ZonedDateTime.of(1989, 11, 9, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Berlin Wall fell
        assertEquals("1969-07-20", formatter.format(ZonedDateTime.of(1969, 7, 20, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")))); // Moon landing
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperFYearRange() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%F").toDateTimeFormatter();
        // Test wide year range for ISO 8601 format
        assertEquals("0001-01-01", formatter.format(ZonedDateTime.of(1, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("1000-12-31", formatter.format(ZonedDateTime.of(1000, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("1969-01-01", formatter.format(ZonedDateTime.of(1969, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("1999-12-31", formatter.format(ZonedDateTime.of(1999, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2000-01-01", formatter.format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2068-12-31", formatter.format(ZonedDateTime.of(2068, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2069-01-01", formatter.format(ZonedDateTime.of(2069, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("9999-12-31", formatter.format(ZonedDateTime.of(9999, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerM() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%m").toDateTimeFormatter();
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("04", formatter.format(ZonedDateTime.of(2023, 4, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("09", formatter.format(ZonedDateTime.of(2023, 9, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerMWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_m").toDateTimeFormatter();
        assertEquals(" 1", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 4", formatter.format(ZonedDateTime.of(2023, 4, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 9", formatter.format(ZonedDateTime.of(2023, 9, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerMWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%4m").toDateTimeFormatter();
        assertEquals("0001", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0004", formatter.format(ZonedDateTime.of(2023, 4, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0009", formatter.format(ZonedDateTime.of(2023, 9, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0012", formatter.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerMAllMonths() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%m").toDateTimeFormatter();
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("02", formatter.format(ZonedDateTime.of(2023, 2, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("03", formatter.format(ZonedDateTime.of(2023, 3, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("06", formatter.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("11", formatter.format(ZonedDateTime.of(2023, 11, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerMWithDifferentPrecisions() {
        final DateTimeFormatter formatter3 = PosixTimeFormat.compile("%3m").toDateTimeFormatter();
        assertEquals("001", formatter3.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("012", formatter3.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter5 = PosixTimeFormat.compile("%5m").toDateTimeFormatter();
        assertEquals("00001", formatter5.format(ZonedDateTime.of(2023, 1, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("00012", formatter5.format(ZonedDateTime.of(2023, 12, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterUpperY() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%Y").toDateTimeFormatter();
        assertEquals("1999", formatter.format(ZonedDateTime.of(1999, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2000", formatter.format(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2023", formatter.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2024", formatter.format(ZonedDateTime.of(2024, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperYWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_Y").toDateTimeFormatter();
        assertEquals("2023", formatter.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("99", formatter.format(ZonedDateTime.of(99, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperYWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%6Y").toDateTimeFormatter();
        assertEquals("002023", formatter.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("000099", formatter.format(ZonedDateTime.of(99, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperYHistoricalYears() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%Y").toDateTimeFormatter();
        assertEquals("1600", formatter.format(ZonedDateTime.of(1600, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("1900", formatter.format(ZonedDateTime.of(1900, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2000", formatter.format(ZonedDateTime.of(2000, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("2100", formatter.format(ZonedDateTime.of(2100, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperYWithDifferentPrecisions() {
        final DateTimeFormatter formatter8 = PosixTimeFormat.compile("%8Y").toDateTimeFormatter();
        assertEquals("00002023", formatter8.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("00000099", formatter8.format(ZonedDateTime.of(99, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter10 = PosixTimeFormat.compile("%10Y").toDateTimeFormatter();
        assertEquals("0000002023", formatter10.format(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0000000099", formatter10.format(ZonedDateTime.of(99, 6, 15, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterUpperH() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%H").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("09", formatter.format(ZonedDateTime.of(2023, 4, 1, 9, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("23", formatter.format(ZonedDateTime.of(2023, 4, 1, 23, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperHWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_H").toDateTimeFormatter();
        assertEquals(" 0", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 9", formatter.format(ZonedDateTime.of(2023, 4, 1, 9, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperHWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%3H").toDateTimeFormatter();
        assertEquals("000", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("009", formatter.format(ZonedDateTime.of(2023, 4, 1, 9, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("012", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("023", formatter.format(ZonedDateTime.of(2023, 4, 1, 23, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperHAllHours() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%H").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 4, 1, 1, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("06", formatter.format(ZonedDateTime.of(2023, 4, 1, 6, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("11", formatter.format(ZonedDateTime.of(2023, 4, 1, 11, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("13", formatter.format(ZonedDateTime.of(2023, 4, 1, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("18", formatter.format(ZonedDateTime.of(2023, 4, 1, 18, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("23", formatter.format(ZonedDateTime.of(2023, 4, 1, 23, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperHWithSpacePaddingVariations() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_H").toDateTimeFormatter();
        assertEquals(" 0", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 6", formatter.format(ZonedDateTime.of(2023, 4, 1, 6, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("23", formatter.format(ZonedDateTime.of(2023, 4, 1, 23, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter4 = PosixTimeFormat.compile("%_4H").toDateTimeFormatter();
        assertEquals("   0", formatter4.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("  12", formatter4.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperI() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%I").toDateTimeFormatter();
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("09", formatter.format(ZonedDateTime.of(2023, 4, 1, 9, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("11", formatter.format(ZonedDateTime.of(2023, 4, 1, 23, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperIWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_I").toDateTimeFormatter();
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 9", formatter.format(ZonedDateTime.of(2023, 4, 1, 9, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperIWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%3I").toDateTimeFormatter();
        assertEquals("012", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("009", formatter.format(ZonedDateTime.of(2023, 4, 1, 9, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("012", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("011", formatter.format(ZonedDateTime.of(2023, 4, 1, 23, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperIAllHours() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%I").toDateTimeFormatter();
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 4, 1, 1, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("06", formatter.format(ZonedDateTime.of(2023, 4, 1, 6, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("11", formatter.format(ZonedDateTime.of(2023, 4, 1, 11, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 4, 1, 13, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("06", formatter.format(ZonedDateTime.of(2023, 4, 1, 18, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("11", formatter.format(ZonedDateTime.of(2023, 4, 1, 23, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperIWithSpacePaddingVariations() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_I").toDateTimeFormatter();
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 1", formatter.format(ZonedDateTime.of(2023, 4, 1, 1, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 6", formatter.format(ZonedDateTime.of(2023, 4, 1, 6, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("11", formatter.format(ZonedDateTime.of(2023, 4, 1, 11, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("12", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterLowerJ() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%j").toDateTimeFormatter();
        assertEquals("001", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("032", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("091", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("365", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerJWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_j").toDateTimeFormatter();
        assertEquals("  1", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 32", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 91", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("365", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerJWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%5j").toDateTimeFormatter();
        assertEquals("00001", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("00032", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("00091", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("00365", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerJLeapYear() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%j").toDateTimeFormatter();
        assertEquals("060", formatter.format(ZonedDateTime.of(2024, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("366", formatter.format(ZonedDateTime.of(2024, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("059", formatter.format(ZonedDateTime.of(2023, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("365", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerJSpecialYears() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%j").toDateTimeFormatter();

        // Century years divisible by 100 but not 400 (not leap years)
        assertEquals("059", formatter.format(ZonedDateTime.of(1900, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("365", formatter.format(ZonedDateTime.of(1900, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("059", formatter.format(ZonedDateTime.of(2100, 2, 28, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("365", formatter.format(ZonedDateTime.of(2100, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        // Century years divisible by 400 (leap years)
        assertEquals("060", formatter.format(ZonedDateTime.of(2000, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("366", formatter.format(ZonedDateTime.of(2000, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("060", formatter.format(ZonedDateTime.of(1600, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("366", formatter.format(ZonedDateTime.of(1600, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        // Regular leap years (divisible by 4 but not century years)
        assertEquals("060", formatter.format(ZonedDateTime.of(2020, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("366", formatter.format(ZonedDateTime.of(2020, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("060", formatter.format(ZonedDateTime.of(2016, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("366", formatter.format(ZonedDateTime.of(2016, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        // Years divisible by 4 but in the past/future
        assertEquals("060", formatter.format(ZonedDateTime.of(1996, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("366", formatter.format(ZonedDateTime.of(1996, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("060", formatter.format(ZonedDateTime.of(2028, 2, 29, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("366", formatter.format(ZonedDateTime.of(2028, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerJMidYearDays() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%j").toDateTimeFormatter();
        assertEquals("100", formatter.format(ZonedDateTime.of(2023, 4, 10, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("200", formatter.format(ZonedDateTime.of(2023, 7, 19, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("300", formatter.format(ZonedDateTime.of(2023, 10, 27, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerJWithLeftAlign() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%-j").toDateTimeFormatter();
        assertEquals("001", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("032", formatter.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("100", formatter.format(ZonedDateTime.of(2023, 4, 10, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("365", formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerJWithDifferentPrecisions() {
        final DateTimeFormatter formatter4 = PosixTimeFormat.compile("%4j").toDateTimeFormatter();
        assertEquals("0001", formatter4.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0032", formatter4.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("0365", formatter4.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter6 = PosixTimeFormat.compile("%6j").toDateTimeFormatter();
        assertEquals("000001", formatter6.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("000032", formatter6.format(ZonedDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("000365", formatter6.format(ZonedDateTime.of(2023, 12, 31, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterLowerJWithSpacePaddingVariations() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_j").toDateTimeFormatter();
        assertEquals("  1", formatter.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 10", formatter.format(ZonedDateTime.of(2023, 1, 10, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("100", formatter.format(ZonedDateTime.of(2023, 4, 10, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter5 = PosixTimeFormat.compile("%_5j").toDateTimeFormatter();
        assertEquals("    1", formatter5.format(ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("  100", formatter5.format(ZonedDateTime.of(2023, 4, 10, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterUpperS() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%S").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("05", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 5, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("59", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 59, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperSWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_S").toDateTimeFormatter();
        assertEquals(" 0", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 5", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 5, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperSWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%3S").toDateTimeFormatter();
        assertEquals("000", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("005", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 5, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("030", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("059", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 59, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperSAllSeconds() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%S").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 1, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("15", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 15, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("45", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 45, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("59", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 59, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperSWithSpacePaddingVariations() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_S").toDateTimeFormatter();
        assertEquals(" 0", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 5", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 5, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("15", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 15, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("59", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 59, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter4 = PosixTimeFormat.compile("%_4S").toDateTimeFormatter();
        assertEquals("   0", formatter4.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 0, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("  30", formatter4.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void testDateTimeFormatterUpperM() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%M").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("05", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 5, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("59", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 59, 30, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperMWithPadding() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_M").toDateTimeFormatter();
        assertEquals(" 0", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 5", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 5, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperMWithPrecision() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%3M").toDateTimeFormatter();
        assertEquals("000", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("005", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 5, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("030", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("059", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 59, 30, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperMAllMinutes() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%M").toDateTimeFormatter();
        assertEquals("00", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("01", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 1, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("15", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 15, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("30", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("45", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 45, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("59", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 59, 30, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void testDateTimeFormatterUpperMWithSpacePaddingVariations() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%_M").toDateTimeFormatter();
        assertEquals(" 0", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals(" 5", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 5, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("15", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 15, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("59", formatter.format(ZonedDateTime.of(2023, 4, 1, 12, 59, 30, 0, ZoneId.of("Asia/Tokyo"))));

        final DateTimeFormatter formatter4 = PosixTimeFormat.compile("%_4M").toDateTimeFormatter();
        assertEquals("   0", formatter4.format(ZonedDateTime.of(2023, 4, 1, 12, 0, 30, 0, ZoneId.of("Asia/Tokyo"))));
        assertEquals("  30", formatter4.format(ZonedDateTime.of(2023, 4, 1, 12, 30, 30, 0, ZoneId.of("Asia/Tokyo"))));
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
            "'01',%d,2023,1,1,12,0,0,0",
            "'09',%d,2023,1,9,12,0,0,0",
            "'17',%d,2023,1,17,12,0,0,0",
            "'31',%d,2023,1,31,12,0,0,0",
            "' 1',%_d,2023,1,1,12,0,0,0",
            "' 9',%_d,2023,1,9,12,0,0,0",
            "'0001',%4d,2023,1,1,12,0,0,0",
            "'0017',%4d,2023,1,17,12,0,0,0",
            "'01',%m,2023,1,15,12,0,0,0",
            "'04',%m,2023,4,15,12,0,0,0",
            "'09',%m,2023,9,15,12,0,0,0",
            "'12',%m,2023,12,15,12,0,0,0",
            "' 1',%_m,2023,1,15,12,0,0,0",
            "' 4',%_m,2023,4,15,12,0,0,0",
            "'0001',%4m,2023,1,15,12,0,0,0",
            "'0009',%4m,2023,9,15,12,0,0,0",
            "'1999',%Y,1999,12,31,12,0,0,0",
            "'2000',%Y,2000,1,1,12,0,0,0",
            "'2023',%Y,2023,6,15,12,0,0,0",
            "'2024',%Y,2024,2,29,12,0,0,0",
            "'99',%_Y,99,6,15,12,0,0,0",
            "'002023',%6Y,2023,6,15,12,0,0,0",
            "'00',%H,2023,4,1,0,0,0,0",
            "'09',%H,2023,4,1,9,0,0,0",
            "'12',%H,2023,4,1,12,0,0,0",
            "'23',%H,2023,4,1,23,0,0,0",
            "' 0',%_H,2023,4,1,0,0,0,0",
            "' 9',%_H,2023,4,1,9,0,0,0,0",
            "'000',%3H,2023,4,1,0,0,0,0",
            "'023',%3H,2023,4,1,23,0,0,0",
            "'00',%S,2023,4,1,12,30,0,0",
            "'05',%S,2023,4,1,12,30,5,0",
            "'30',%S,2023,4,1,12,30,30,0",
            "'59',%S,2023,4,1,12,30,59,0",
            "' 0',%_S,2023,4,1,12,30,0,0",
            "' 5',%_S,2023,4,1,12,30,5,0",
            "'000',%3S,2023,4,1,12,30,0,0",
            "'059',%3S,2023,4,1,12,30,59,0",
            "'00',%M,2023,4,1,12,0,30,0",
            "'05',%M,2023,4,1,12,5,30,0",
            "'30',%M,2023,4,1,12,30,30,0",
            "'59',%M,2023,4,1,12,59,30,0",
            "' 0',%_M,2023,4,1,12,0,30,0",
            "' 5',%_M,2023,4,1,12,5,30,0",
            "'000',%3M,2023,4,1,12,0,30,0",
            "'059',%3M,2023,4,1,12,59,30,0",
            "'12',%I,2023,4,1,0,0,0,0",
            "'09',%I,2023,4,1,9,0,0,0",
            "'12',%I,2023,4,1,12,0,0,0",
            "'11',%I,2023,4,1,23,0,0,0",
            "'12',%_I,2023,4,1,0,0,0,0",
            "' 9',%_I,2023,4,1,9,0,0,0",
            "'012',%3I,2023,4,1,0,0,0,0",
            "'011',%3I,2023,4,1,23,0,0,0",
            "'001',%j,2023,1,1,12,0,0,0",
            "'032',%j,2023,2,1,12,0,0,0",
            "'091',%j,2023,4,1,12,0,0,0",
            "'365',%j,2023,12,31,12,0,0,0",
            "'  1',%_j,2023,1,1,12,0,0,0",
            "' 32',%_j,2023,2,1,12,0,0,0",
            "'00001',%5j,2023,1,1,12,0,0,0",
            "'00365',%5j,2023,12,31,12,0,0,0",
    })
    public void testFormatSingleWithDateTimeFormatter(
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
    public void test1() {
        assertFormat(
                "%nabc",
                Literal.of("\n", C),
                Literal.of("abc", C));
    }

    @Test
    public void test2() {
        assertFormat(
                "abc%nabc",
                Literal.of("abc", C),
                Literal.of("\n", C),
                Literal.of("abc", C));
    }

    @Test
    public void test3() {
        assertFormat(
                "abc%12nabc",
                Literal.of("abc", C),
                Literal.of("\n", C),
                Literal.of("abc", C));
    }

    @ParameterizedTest
    @CsvSource({
            // "%%,%,false,false,-1,-1,,,%%",
            // "%n,n,false,false,-1,-1,,,%n",
            // "%t,t,false,false,-1,-1,,,%t",
            "%12b,b,false,false,12,-1,,,%12b",
            "%z,z,false,false,-1,-1,,,%z",
            "%:z,z,false,false,-1,1,,,%:z",
            "%::z,z,false,false,-1,2,,,%::z",
            "%:::z,z,false,false,-1,3,,,%:::z",
            "%::::z,z,false,false,-1,4,,,%::::z",
    })
    public void testSingles(
            final String format,
            final String expectedChar,
            final String expectedUpperCase,
            final String expectedChangeCase,
            final String expectedPrecision,
            final String expectedColons,
            final String expectedPad,
            final String expectedModifier,
            final String expectedOriginal) {
        assertSingleFormatOptions(
                expectedChar.charAt(0),
                Boolean.parseBoolean(expectedUpperCase),
                Boolean.parseBoolean(expectedChangeCase),
                Integer.parseInt(expectedPrecision),
                Integer.parseInt(expectedColons),
                (expectedPad == null || expectedPad.isEmpty()) ? '\0' : expectedPad.charAt(0),
                (expectedModifier == null || expectedModifier.isEmpty()) ? '\0' : expectedModifier.charAt(0),
                format);
    }

    private void assertFormat(final String format, final Specification... expectedFormatSpecifications) {
        final List<Specification> actual = Tokenizer.tokenize(format);
        assertEquals(Arrays.asList(expectedFormatSpecifications), actual);
        System.out.println("\"" + format + "\"");
        System.out.println(actual);
    }

    private void assertSingleFormatOptions(
            final char expectedChar,
            final boolean expectedUpperCase,
            final boolean expectedChangeCase,
            final int expectedPrecision,
            final int expectedColons,
            final char expectedPad,
            final char expectedModifier,
            final String format) {
        final List<Specification> actual = Tokenizer.tokenize(format);
        assertEquals(1, actual.size());
        assertTrue(actual.get(0) instanceof ConversionSpecification);
        final ConversionSpecification actualConversion = (ConversionSpecification) actual.get(0);
        assertEquals(expectedChar, actualConversion.terminatingConversionSpecifier.toString().charAt(0));
        assertEquals(expectedUpperCase, actualConversion.upperCase);
        assertEquals(expectedChangeCase, actualConversion.changeCase);
        assertEquals(expectedPrecision, actualConversion.precision);
        assertEquals(expectedColons, actualConversion.colons);
        assertEquals(expectedPad, actualConversion.pad);
        assertEquals(expectedModifier, actualConversion.modifier);
    }

    private static Specification.Context C = Specification.EMPTY;
}
