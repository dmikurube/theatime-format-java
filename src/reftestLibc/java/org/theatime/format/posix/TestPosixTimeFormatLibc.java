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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
                "e"
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

        final DateTimeFormatter actualFormatter = PosixTimeFormat.compile(format).toDateTimeFormatter();
        final OffsetDateTime actualDateTime = OffsetDateTime.of(
                year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0, ZoneOffset.UTC);
        assertEquals(dayOfWeek, actualDateTime.getDayOfWeek());
        assertEquals(dayOfYear, actualDateTime.getDayOfYear());
        final String actualFormatted = actualFormatter.format(actualDateTime);

        System.out.println("\"" + format + "\"");
        System.out.println("\"" + expectedFormatted + "\"");
        System.out.println("\"" + actualFormatted + "\"");
        assertEquals(expectedFormatted, actualFormatted);
    }

    private static Random RANDOM = new Random();
}
