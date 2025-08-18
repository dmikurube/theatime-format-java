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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestPosixTimeFormatLibc {
    @ParameterizedTest
    @MethodSource("formattingFormatsAndDateTimes")
    public void testFormattingWithPatterns(final String format, final LocalDateTime datetime) {
        assertStrftime(format,
                       datetime.getYear(),
                       datetime.getMonthValue(),
                       datetime.getDayOfMonth(),
                       datetime.getHour(),
                       datetime.getMinute(),
                       datetime.getSecond(),
                       datetime.getDayOfWeek(),
                       datetime.getDayOfYear(),
                       0,
                       "C");
    }

    @ParameterizedTest
    @MethodSource("parsableFormatsAndDateTimes")
    public void testParsingWithGeneratedPatterns(final String format, final LocalDateTime datetime) {
        assertStrptime(format, datetime, "C");
    }

    static Stream<Arguments> formattingFormatsAndDateTimes() {
        return dateTimes().flatMap(dt -> formattingFormats().map(f -> Arguments.of(f, dt)));
    }

    static Stream<String> formattingFormats() {
        return Stream.of(
                fixedFormattingFormats(),
                randomFormattingFormats()
                ).flatMap(Function.identity());
    }

    static Stream<String> fixedFormattingFormats() {
        return Stream.of(
                "%q",  // "%q" does not exist.
                // "%^q",
                // "%#q",
                "%10q",
                // "%^10q",
                // "%#10q",
                "%010q"
                // "%^010q",
                // "%#010q",
                );
    }

    static Stream<String> randomFormattingFormats() {
        final int precision = RANDOM.nextInt(20 - 4) + 4;

        return Stream.of(
                "a",
                "A",
                "b",
                "B",
                "c",
                "C",
                "d",
                "D",
                "e",
                "H",
                "I",
                "j",
                "m",
                "M",
                "S",
                "Y"
                ).flatMap(specifier -> Stream.of(
                    "%" + specifier,
                    "%^" + specifier,
                    "%#" + specifier,
                    "%-" + specifier,
                    "%^-" + specifier,
                    "%#-" + specifier,
                    "%_" + specifier,
                    "%^_" + specifier,
                    "%#_" + specifier,
                    "%0" + specifier,
                    "%^0" + specifier,
                    "%#0" + specifier,

                    "%1" + specifier,
                    "%^1" + specifier,
                    "%#1" + specifier,
                    "%-1" + specifier,
                    "%^-1" + specifier,
                    "%#-1" + specifier,
                    "%_1" + specifier,
                    "%^_1" + specifier,
                    "%#_1" + specifier,
                    "%01" + specifier,
                    "%^01" + specifier,
                    "%#01" + specifier,

                    "%2" + specifier,
                    "%^2" + specifier,
                    "%#2" + specifier,
                    "%-2" + specifier,
                    "%^-2" + specifier,
                    "%#-2" + specifier,
                    "%_2" + specifier,
                    "%^_2" + specifier,
                    "%#_2" + specifier,
                    "%02" + specifier,
                    "%^02" + specifier,
                    "%#02" + specifier,

                    "%3" + specifier,
                    "%^3" + specifier,
                    "%#3" + specifier,
                    "%-3" + specifier,
                    "%^-3" + specifier,
                    "%#-3" + specifier,
                    "%_3" + specifier,
                    "%^_3" + specifier,
                    "%#_3" + specifier,
                    "%03" + specifier,
                    "%^03" + specifier,
                    "%#03" + specifier,

                    "%4" + specifier,
                    "%^4" + specifier,
                    "%#4" + specifier,
                    "%-4" + specifier,
                    "%^-4" + specifier,
                    "%#-4" + specifier,
                    "%_4" + specifier,
                    "%^_4" + specifier,
                    "%#_4" + specifier,
                    "%04" + specifier,
                    "%^04" + specifier,
                    "%#04" + specifier,

                    "%" + precision + specifier,
                    "%^" + precision + specifier,
                    "%#" + precision + specifier,
                    "%-" + precision + specifier,
                    "%^-" + precision + specifier,
                    "%#-" + precision + specifier,
                    "%_" + precision + specifier,
                    "%^_" + precision + specifier,
                    "%#_" + precision + specifier,
                    "%0" + precision + specifier,
                    "%^0" + precision + specifier,
                    "%#0" + precision + specifier
                ));
    }

    static Stream<Arguments> parsableFormatsAndDateTimes() {
        return dateTimes().flatMap(dt -> fixedParsableFormats().map(f -> Arguments.of(f, dt)));
    }

    static Stream<String> fixedParsableFormats() {
        return Stream.of(
                "%d",
                // "%H",  // A single "%H" does not work for parsing.
                "%m",
                // "%M",  // A single "%M" does not work for parsing.
                // "%S",  // A single "%S" does not work for parsing.
                "%Y"
                );
    }

    static Stream<LocalDateTime> dateTimes() {
        return Stream.of(
                randomModernDateTimes(18),
                randomPastDateTimes(2),
                fixedDateTimes()
                ).flatMap(Function.identity());
    }

    static Stream<LocalDateTime> fixedDateTimes() {
        return Stream.of(
                LocalDateTime.of(8, 12, 31, 23, 59, 59, 999_999_999),
                LocalDateTime.of(1999, 12, 31, 23, 59, 59, 999_999_999),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0)
        );
    }

    static Stream<LocalDateTime> randomModernDateTimes(final int n) {
        return Stream.generate(() -> randomModernLocalDateTime()).limit(n);
    }

    static Stream<LocalDateTime> randomPastDateTimes(final int n) {
        return Stream.generate(() -> randomPastLocalDateTime()).limit(n);
    }

    private static LocalDateTime randomModernLocalDateTime() {
        final int year = RANDOM.nextInt(2100 - 1970) + 1970;
        final int month = RANDOM.nextInt(12) + 1;
        final int day = RANDOM.nextInt(YearMonth.of(year, month).lengthOfMonth()) + 1;
        return LocalDateTime.of(
                year,
                month,
                day,
                RANDOM.nextInt(24),
                RANDOM.nextInt(60),
                RANDOM.nextInt(60),
                RANDOM.nextInt(1_000_000_000));
    }

    private static LocalDateTime randomPastLocalDateTime() {
        final int year = RANDOM.nextInt(1970);
        final int month = RANDOM.nextInt(12) + 1;
        final int day = RANDOM.nextInt(YearMonth.of(year, month).lengthOfMonth()) + 1;
        return LocalDateTime.of(
                year,
                month,
                day,
                RANDOM.nextInt(24),
                RANDOM.nextInt(60),
                RANDOM.nextInt(60),
                RANDOM.nextInt(1_000_000_000));
    }

    private static void assertStrftime(
            final String format,
            final int year,
            final int monthOfYear,
            final int dayOfMonth,
            final int hourOfDay,
            final int minuteOfHour,
            final int secondOfMinute,
            final DayOfWeek dayOfWeek,
            final int dayOfYear,
            final int isDst,
            final String locale) {
        final String pathTryStrftime = System.getProperty("reftestLibcTryStrftime");
        assertNotNull(pathTryStrftime);
        final String expectedFormatted = new TryStrftime(pathTryStrftime).strftime(
                format,
                year,
                monthOfYear,
                dayOfMonth,
                hourOfDay,
                minuteOfHour,
                secondOfMinute,
                dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue(),
                dayOfYear,
                isDst,
                locale);

        System.out.println("\"" + format + "\"");
        System.out.println("\"" + expectedFormatted + "\"");

        final DateTimeFormatter actualFormatter;
        try {
            actualFormatter = PosixTimeFormat.compile(format).toDateTimeFormatter();
        } catch (final UnsupportedPaddingException ex) {
            System.out.println("The padding style is not supported: " + ex.getMessage());
            return;
        }

        final OffsetDateTime actualDateTime = OffsetDateTime.of(
                year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0, ZoneOffset.UTC);
        assertEquals(dayOfWeek, actualDateTime.getDayOfWeek());
        assertEquals(dayOfYear, actualDateTime.getDayOfYear());
        final String actualFormatted = actualFormatter.format(actualDateTime);

        System.out.println("\"" + actualFormatted + "\"");
        assertEquals(expectedFormatted, actualFormatted);
    }

    private static void assertStrptime(
            final String format,
            final LocalDateTime datetime,
            final String locale) {
        final String pathTryStrptime = System.getProperty("reftestLibcTryStrptime");
        assertNotNull(pathTryStrptime);

        final DateTimeFormatter formatter = PosixTimeFormat.compile(format).toDateTimeFormatter();
        final String formatted = formatter.format(datetime);

        final TryStrptime.Parsed expectedParsed = new TryStrptime(pathTryStrptime).strptime(format, formatted, locale);

        System.out.println("\"" + format + "\"");
        System.out.println("\"" + formatted + "\"");

        final TemporalAccessor actualParsed;
        try {
            actualParsed = formatter.parse(formatted);
        } catch (final Exception ex) {
            throw new AssertionError(
                    "Our parser failed but libc strptime succeeded. Input: " + formatted
                    + ", Format: " + format + ", Expected: " + expectedParsed, ex);
        }

        if (expectedParsed.year < 0) {
            System.out.println("Year: N/A");
            System.out.println("Year: "
                    + (actualParsed.isSupported(ChronoField.YEAR) ? ("" + actualParsed.get(ChronoField.YEAR)) : "N/A"));
            assertFalse(actualParsed.isSupported(ChronoField.YEAR));
        } else {
            System.out.println("Year: " + expectedParsed.year);
            assertTrue(actualParsed.isSupported(ChronoField.YEAR));
            System.out.println("Year: " + actualParsed.get(ChronoField.YEAR));
            assertEquals(expectedParsed.year, actualParsed.get(ChronoField.YEAR));
        }

        if (expectedParsed.mon < 0) {
            System.out.println("Month: N/A");
            System.out.println("Month: "
                    + (actualParsed.isSupported(ChronoField.MONTH_OF_YEAR) ? ("" + actualParsed.get(ChronoField.MONTH_OF_YEAR)) : "N/A"));
            assertFalse(actualParsed.isSupported(ChronoField.MONTH_OF_YEAR));
        } else {
            System.out.println("Month: " + expectedParsed.mon);
            assertTrue(actualParsed.isSupported(ChronoField.MONTH_OF_YEAR));
            System.out.println("Month: " + actualParsed.get(ChronoField.MONTH_OF_YEAR));
            assertEquals(expectedParsed.mon, actualParsed.get(ChronoField.MONTH_OF_YEAR));
        }

        if (expectedParsed.mday < 0) {
            System.out.println("Day: N/A");
            System.out.println("Day: "
                    + (actualParsed.isSupported(ChronoField.DAY_OF_MONTH) ? ("" + actualParsed.get(ChronoField.DAY_OF_MONTH)) : "N/A"));
            assertFalse(actualParsed.isSupported(ChronoField.DAY_OF_MONTH));
        } else {
            System.out.println("Day: " + expectedParsed.mday);
            assertTrue(actualParsed.isSupported(ChronoField.DAY_OF_MONTH));
            System.out.println("Day: " + actualParsed.get(ChronoField.DAY_OF_MONTH));
            assertEquals(expectedParsed.mday, actualParsed.get(ChronoField.DAY_OF_MONTH));
        }

        if (expectedParsed.hour < 0) {
            System.out.println("Hour: N/A");
            System.out.println("Hour: "
                    + (actualParsed.isSupported(ChronoField.HOUR_OF_DAY) ? ("" + actualParsed.get(ChronoField.HOUR_OF_DAY)) : "N/A"));
            assertFalse(actualParsed.isSupported(ChronoField.HOUR_OF_DAY));
        } else {
            System.out.println("Hour: " + expectedParsed.hour);
            assertTrue(actualParsed.isSupported(ChronoField.HOUR_OF_DAY));
            System.out.println("Hour: " + actualParsed.get(ChronoField.HOUR_OF_DAY));
            assertEquals(expectedParsed.hour, actualParsed.get(ChronoField.HOUR_OF_DAY));
        }

        if (expectedParsed.min < 0) {
            System.out.println("Minute: N/A");
            System.out.println("Minute: "
                    + (actualParsed.isSupported(ChronoField.MINUTE_OF_HOUR) ? ("" + actualParsed.get(ChronoField.MINUTE_OF_HOUR)) : "N/A"));
            assertFalse(actualParsed.isSupported(ChronoField.MINUTE_OF_HOUR));
        } else {
            System.out.println("Minute: " + expectedParsed.min);
            assertTrue(actualParsed.isSupported(ChronoField.MINUTE_OF_HOUR));
            System.out.println("Minute: " + actualParsed.get(ChronoField.MINUTE_OF_HOUR));
            assertEquals(expectedParsed.min, actualParsed.get(ChronoField.MINUTE_OF_HOUR));
        }

        if (expectedParsed.sec < 0) {
            System.out.println("Second: N/A");
            System.out.println("Second: "
                    + (actualParsed.isSupported(ChronoField.SECOND_OF_MINUTE) ? ("" + actualParsed.get(ChronoField.SECOND_OF_MINUTE)) : "N/A"));
            assertFalse(actualParsed.isSupported(ChronoField.SECOND_OF_MINUTE));
        } else {
            System.out.println("Second: " + expectedParsed.sec);
            assertTrue(actualParsed.isSupported(ChronoField.SECOND_OF_MINUTE));
            System.out.println("Second: " + actualParsed.get(ChronoField.SECOND_OF_MINUTE));
            assertEquals(expectedParsed.sec, actualParsed.get(ChronoField.SECOND_OF_MINUTE));
        }
    }

    private static Random RANDOM = new Random();
}
