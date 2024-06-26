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
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class TestPosixTimeFormatLibc {
    @ParameterizedTest
    @CsvSource({
            "%a,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%10a,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%A,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%10A,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%b,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%10b,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%B,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%10B,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%c,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%c,9999,1,2,15,4,5,SATURDAY,2,0,C",
            // "%c,10000,1,2,15,4,5,SUNDAY,2,0,C",  // libc may not work for this case -- the day of week to be: "?"
    })
    public void test(
            final String format,
            final String year,
            final String monthOfYear,
            final String dayOfMonth,
            final String hourOfDay,
            final String minuteOfHour,
            final String secondOfMinute,
            final String dayOfWeek,
            final String dayOfYear,
            final String isDst,
            final String locale) {
        assertStrftime(format,
                       Integer.parseInt(year),
                       Integer.parseInt(monthOfYear),
                       Integer.parseInt(dayOfMonth),
                       Integer.parseInt(hourOfDay),
                       Integer.parseInt(minuteOfHour),
                       Integer.parseInt(secondOfMinute),
                       DayOfWeek.valueOf(dayOfWeek),
                       Integer.parseInt(dayOfYear),
                       Integer.parseInt(isDst),
                       locale);
    }

    @ParameterizedTest
    @MethodSource("formatAndOrdinaryDateTime")
    public void testOrdinaryDateTime(final String format, final LocalDateTime datetime) {
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

    static Stream<Arguments> formatAndOrdinaryDateTime() {
        return ordinaryDateTime().flatMap(dt -> formats().map(f -> Arguments.of(f, dt)));
    }

    static Stream<String> formats() {
        return Stream.of(
                "%a",
                "%^a",
                "%#a",
                "%10a",
                "%^10a",
                "%#10a",
                "%010a",
                "%^010a",
                "%#010a",
                "%A",
                "%^A",
                "%#A",
                "%10A",
                "%^10A",
                "%#10A",
                "%010A",
                "%^010A",
                "%#010A",
                "%b",
                "%^b",
                "%#b",
                "%10b",
                "%^10b",
                "%#10b",
                "%010b",
                "%^010b",
                "%#010b",
                "%B",
                "%^B",
                "%#B",
                "%10B",
                "%^10B",
                "%#10B",
                "%010B",
                "%^010B",
                "%#010B",
                "%c",
                "%C",
                "%^C",
                "%#C",
                "%10C",
                "%^10C",
                "%#10C",
                "%010C",
                "%^010C",
                "%#010C",
                "%q",  // "%q" does not exist.
                // "%^q",
                // "%#q",
                "%10q",
                // "%^10q",
                // "%#10q",
                "%010q",
                // "%^010q",
                // "%#010q",
                "%%",
                "%^%",
                "%#%",
                "%10%",
                "%^10%",
                "%#10%",
                "%010%",
                "%^010%",
                "%#010%"
                );
    }

    static Stream<LocalDateTime> ordinaryDateTime() {
        return Stream.generate(() -> randomOrdinaryLocalDateTime()).limit(20);
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

        System.out.println("\"" + expectedFormatted + "\"");
        System.out.println("\"" + actualFormatted + "\"");
        assertEquals(expectedFormatted, actualFormatted);
    }

    private static LocalDateTime randomOrdinaryLocalDateTime() {
        final int year = RANDOM.nextInt(2024 - 1970) + 1970;
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

    private static Random RANDOM = new Random();
}
