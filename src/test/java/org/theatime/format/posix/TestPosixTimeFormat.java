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

    /*
     * Import test cases that failed in reftests.
     */
    @ParameterizedTest
    @CsvSource({
            "OCT,%^2b,2020,10,26,7,48,59,526929475",
            // "'        29',%-10d,1985,9,29,4,23,20,62491315",
            // "0000000TUE,%0#10a,2008,9,2,5,31,03,340494545",
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
