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
            "%d,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%d,2006,1,9,15,4,5,MONDAY,9,0,C",
            "%d,2006,1,31,15,4,5,TUESDAY,31,0,C",
            "%_d,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%_d,2006,1,9,15,4,5,MONDAY,9,0,C",
            "%4d,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%4d,2006,1,31,15,4,5,TUESDAY,31,0,C",
            "%D,2023,1,1,15,4,5,SUNDAY,1,0,C",
            "%D,2023,4,9,15,4,5,SUNDAY,99,0,C",
            "%D,1999,12,31,15,4,5,FRIDAY,365,0,C",
            "%D,2000,2,29,15,4,5,TUESDAY,60,0,C",
            "%D,1976,7,4,15,4,5,SUNDAY,186,0,C",
            "%D,2068,12,31,15,4,5,MONDAY,366,0,C",
            "%D,1969,1,1,15,4,5,WEDNESDAY,1,0,C",
            "%D,2000,1,1,15,4,5,SATURDAY,1,0,C",
            "%D,2001,9,11,15,4,5,TUESDAY,254,0,C",
            "%D,1969,7,20,15,4,5,SUNDAY,201,0,C",
            "%D,1941,12,7,15,4,5,SUNDAY,341,0,C",
            "%D,1989,11,9,15,4,5,THURSDAY,313,0,C",
            "%D,2020,2,29,15,4,5,SATURDAY,60,0,C",
            "%D,1900,2,28,15,4,5,WEDNESDAY,59,0,C",
            "%D,1900,3,1,15,4,5,THURSDAY,60,0,C",
            "%D,2004,2,29,15,4,5,SUNDAY,60,0,C",
            "%D,2023,1,31,15,4,5,TUESDAY,31,0,C",
            "%D,2023,2,28,15,4,5,TUESDAY,59,0,C",
            "%D,2023,4,30,15,4,5,SUNDAY,120,0,C",
            "%D,2023,6,30,15,4,5,FRIDAY,181,0,C",
            "%D,2023,9,30,15,4,5,SATURDAY,273,0,C",
            "%D,2023,11,30,15,4,5,THURSDAY,334,0,C",
            "%D,2023,12,31,15,4,5,SUNDAY,365,0,C",
            "%D,1968,12,31,15,4,5,TUESDAY,366,0,C",
            "%D,2069,1,1,15,4,5,TUESDAY,1,0,C",
            "%D,1996,2,29,15,4,5,THURSDAY,60,0,C",
            "%F,2023,1,1,15,4,5,SUNDAY,1,0,C",
            "%F,2023,4,9,15,4,5,SUNDAY,99,0,C",
            "%F,1999,12,31,15,4,5,FRIDAY,365,0,C",
            "%F,2000,2,29,15,4,5,TUESDAY,60,0,C",
            "%F,1976,7,4,15,4,5,SUNDAY,186,0,C",
            "%F,2000,1,1,15,4,5,SATURDAY,1,0,C",
            "%F,2001,9,11,15,4,5,TUESDAY,254,0,C",
            "%F,1969,7,20,15,4,5,SUNDAY,201,0,C",
            "%F,1941,12,7,15,4,5,SUNDAY,341,0,C",
            "%F,1989,11,9,15,4,5,THURSDAY,313,0,C",
            "%F,2020,2,29,15,4,5,SATURDAY,60,0,C",
            "%F,1900,2,28,15,4,5,WEDNESDAY,59,0,C",
            "%F,1900,3,1,15,4,5,THURSDAY,60,0,C",
            "%F,2004,2,29,15,4,5,SUNDAY,60,0,C",
            "%F,2023,1,31,15,4,5,TUESDAY,31,0,C",
            "%F,2023,2,28,15,4,5,TUESDAY,59,0,C",
            "%F,2023,4,30,15,4,5,SUNDAY,120,0,C",
            "%F,2023,6,30,15,4,5,FRIDAY,181,0,C",
            "%F,2023,9,30,15,4,5,SATURDAY,273,0,C",
            "%F,2023,11,30,15,4,5,THURSDAY,334,0,C",
            "%F,2023,12,31,15,4,5,SUNDAY,365,0,C",
            "%F,1968,12,31,15,4,5,TUESDAY,366,0,C",
            "%F,2069,1,1,15,4,5,TUESDAY,1,0,C",
            "%F,1996,2,29,15,4,5,THURSDAY,60,0,C",
            "%F,9999,12,31,15,4,5,FRIDAY,365,0,C",
            "%m,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%m,2006,4,15,15,4,5,SATURDAY,105,0,C",
            "%m,2006,9,30,15,4,5,SATURDAY,273,0,C",
            "%m,2006,12,25,15,4,5,MONDAY,359,0,C",
            "%_m,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%_m,2006,9,30,15,4,5,SATURDAY,273,0,C",
            "%4m,2006,1,2,15,4,5,MONDAY,2,0,C",
            "%4m,2006,12,25,15,4,5,MONDAY,359,0,C",
            "%Y,1999,12,31,15,4,5,FRIDAY,365,0,C",
            "%Y,2000,1,1,15,4,5,SATURDAY,1,0,C",
            "%Y,2006,6,15,15,4,5,THURSDAY,166,0,C",
            "%Y,2023,6,15,15,4,5,THURSDAY,166,0,C",
            "%_Y,99,6,15,15,4,5,MONDAY,166,0,C",
            "%6Y,2006,6,15,15,4,5,THURSDAY,166,0,C",
            "%H,2023,4,1,0,0,0,SATURDAY,91,0,C",
            "%H,2023,4,1,9,15,30,SATURDAY,91,0,C",
            "%H,2023,4,1,12,0,0,SATURDAY,91,0,C",
            "%H,2023,4,1,23,59,59,SATURDAY,91,0,C",
            "%_H,2023,4,1,0,0,0,SATURDAY,91,0,C",
            "%_H,2023,4,1,9,15,30,SATURDAY,91,0,C",
            "%3H,2023,4,1,0,0,0,SATURDAY,91,0,C",
            "%3H,2023,4,1,23,59,59,SATURDAY,91,0,C",
            "%S,2023,4,1,12,30,0,SATURDAY,91,0,C",
            "%S,2023,4,1,12,30,5,SATURDAY,91,0,C",
            "%S,2023,4,1,12,30,30,SATURDAY,91,0,C",
            "%S,2023,4,1,12,30,59,SATURDAY,91,0,C",
            "%_S,2023,4,1,12,30,0,SATURDAY,91,0,C",
            "%_S,2023,4,1,12,30,5,SATURDAY,91,0,C",
            "%3S,2023,4,1,12,30,0,SATURDAY,91,0,C",
            "%3S,2023,4,1,12,30,59,SATURDAY,91,0,C",
            "%M,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%M,2023,4,1,12,5,30,SATURDAY,91,0,C",
            "%M,2023,4,1,12,30,30,SATURDAY,91,0,C",
            "%M,2023,4,1,12,59,30,SATURDAY,91,0,C",
            "%_M,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%_M,2023,4,1,12,5,30,SATURDAY,91,0,C",
            "%3M,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%3M,2023,4,1,12,59,30,SATURDAY,91,0,C",
            "%I,2023,4,1,0,0,30,SATURDAY,91,0,C",
            "%I,2023,4,1,9,0,30,SATURDAY,91,0,C",
            "%I,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%I,2023,4,1,23,0,30,SATURDAY,91,0,C",
            "%_I,2023,4,1,0,0,30,SATURDAY,91,0,C",
            "%_I,2023,4,1,9,0,30,SATURDAY,91,0,C",
            "%3I,2023,4,1,0,0,30,SATURDAY,91,0,C",
            "%3I,2023,4,1,23,0,30,SATURDAY,91,0,C",
            "%j,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%j,2023,2,1,12,0,30,WEDNESDAY,32,0,C",
            "%j,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%j,2023,12,31,12,0,30,SUNDAY,365,0,C",
            "%_j,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%_j,2023,2,1,12,0,30,WEDNESDAY,32,0,C",
            "%5j,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%5j,2023,12,31,12,0,30,SUNDAY,365,0,C",
            "%j,2024,2,29,12,0,30,THURSDAY,60,0,C",
            "%j,2024,12,31,12,0,30,TUESDAY,366,0,C",
            "%j,2023,2,28,12,0,30,TUESDAY,59,0,C",
            "%j,2023,4,10,12,0,30,MONDAY,100,0,C",
            "%j,2023,7,19,12,0,30,WEDNESDAY,200,0,C",
            "%j,2023,10,27,12,0,30,FRIDAY,300,0,C",
            "%_j,2023,4,10,12,0,30,MONDAY,100,0,C",
            "%_j,2023,10,27,12,0,30,FRIDAY,300,0,C",
            "%4j,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%4j,2023,2,1,12,0,30,WEDNESDAY,32,0,C",
            "%4j,2023,12,31,12,0,30,SUNDAY,365,0,C",
            "%6j,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%6j,2023,4,10,12,0,30,MONDAY,100,0,C",
            "%6j,2024,12,31,12,0,30,TUESDAY,366,0,C",
            "%_4j,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%_4j,2023,4,10,12,0,30,MONDAY,100,0,C",
            "%j,1900,2,28,12,0,30,WEDNESDAY,59,0,C",
            "%j,1900,12,31,12,0,30,MONDAY,365,0,C",
            "%j,2100,2,28,12,0,30,SUNDAY,59,0,C",
            "%j,2100,12,31,12,0,30,FRIDAY,365,0,C",
            "%j,2000,2,29,12,0,30,TUESDAY,60,0,C",
            "%j,2000,12,31,12,0,30,SUNDAY,366,0,C",
            "%j,1600,2,29,12,0,30,TUESDAY,60,0,C",
            "%j,1600,12,31,12,0,30,SUNDAY,366,0,C",
            "%j,2020,2,29,12,0,30,SATURDAY,60,0,C",
            "%j,2020,12,31,12,0,30,THURSDAY,366,0,C",
            "%j,2016,2,29,12,0,30,MONDAY,60,0,C",
            "%j,2016,12,31,12,0,30,SATURDAY,366,0,C",
            "%j,1996,2,29,12,0,30,THURSDAY,60,0,C",
            "%j,1996,12,31,12,0,30,TUESDAY,366,0,C",
            "%j,2028,2,29,12,0,30,TUESDAY,60,0,C",
            "%j,2028,12,31,12,0,30,SUNDAY,366,0,C",
            "%d,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%d,2023,1,31,12,0,30,TUESDAY,31,0,C",
            "%d,2023,2,28,12,0,30,TUESDAY,59,0,C",
            "%d,2024,2,29,12,0,30,THURSDAY,60,0,C",
            "%d,2023,4,30,12,0,30,SUNDAY,120,0,C",
            "%3d,2023,6,1,12,0,30,THURSDAY,152,0,C",
            "%3d,2023,5,31,12,0,30,WEDNESDAY,151,0,C",
            "%5d,2023,6,1,12,0,30,THURSDAY,152,0,C",
            "%_d,2023,6,1,12,0,30,THURSDAY,152,0,C",
            "%_d,2023,6,5,12,0,30,MONDAY,156,0,C",
            "%_4d,2023,6,1,12,0,30,THURSDAY,152,0,C",
            "%m,2023,2,15,12,0,30,WEDNESDAY,46,0,C",
            "%m,2023,3,15,12,0,30,WEDNESDAY,74,0,C",
            "%m,2023,6,15,12,0,30,THURSDAY,166,0,C",
            "%m,2023,11,15,12,0,30,WEDNESDAY,319,0,C",
            "%3m,2023,1,15,12,0,30,SUNDAY,15,0,C",
            "%3m,2023,12,15,12,0,30,FRIDAY,349,0,C",
            "%5m,2023,1,15,12,0,30,SUNDAY,15,0,C",
            "%Y,1600,2,29,12,0,30,TUESDAY,60,0,C",
            "%Y,1900,2,28,12,0,30,WEDNESDAY,59,0,C",
            "%Y,2000,2,29,12,0,30,TUESDAY,60,0,C",
            "%Y,2100,2,28,12,0,30,SUNDAY,59,0,C",
            "%8Y,2023,6,15,12,0,30,THURSDAY,166,0,C",
            "%10Y,2023,6,15,12,0,30,THURSDAY,166,0,C",
            "%H,2023,4,1,1,0,0,SATURDAY,91,0,C",
            "%H,2023,4,1,6,0,0,SATURDAY,91,0,C",
            "%H,2023,4,1,11,0,0,SATURDAY,91,0,C",
            "%H,2023,4,1,13,0,0,SATURDAY,91,0,C",
            "%H,2023,4,1,18,0,0,SATURDAY,91,0,C",
            "%_H,2023,4,1,0,0,0,SATURDAY,91,0,C",
            "%_H,2023,4,1,6,0,0,SATURDAY,91,0,C",
            "%_4H,2023,4,1,0,0,0,SATURDAY,91,0,C",
            "%I,2023,4,1,1,0,0,SATURDAY,91,0,C",
            "%I,2023,4,1,6,0,0,SATURDAY,91,0,C",
            "%I,2023,4,1,11,0,0,SATURDAY,91,0,C",
            "%I,2023,4,1,13,0,0,SATURDAY,91,0,C",
            "%I,2023,4,1,18,0,0,SATURDAY,91,0,C",
            "%_I,2023,4,1,1,0,0,SATURDAY,91,0,C",
            "%_I,2023,4,1,6,0,0,SATURDAY,91,0,C",
            "%S,2023,4,1,12,30,1,SATURDAY,91,0,C",
            "%S,2023,4,1,12,30,15,SATURDAY,91,0,C",
            "%S,2023,4,1,12,30,45,SATURDAY,91,0,C",
            "%_S,2023,4,1,12,30,0,SATURDAY,91,0,C",
            "%_S,2023,4,1,12,30,5,SATURDAY,91,0,C",
            "%_4S,2023,4,1,12,30,0,SATURDAY,91,0,C",
            "%M,2023,4,1,12,1,30,SATURDAY,91,0,C",
            "%M,2023,4,1,12,15,30,SATURDAY,91,0,C",
            "%M,2023,4,1,12,45,30,SATURDAY,91,0,C",
            "%_M,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%_M,2023,4,1,12,5,30,SATURDAY,91,0,C",
            "%_4M,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%n,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%4n,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%04n,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%_n,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%_4n,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%n%n%n,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%t,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%4t,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%04t,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%_t,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%_4t,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%t%t%t,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%g,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%g,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%g,2024,1,1,12,0,30,MONDAY,1,0,C",
            "%g,2022,12,31,12,0,30,SATURDAY,365,0,C",
            "%_g,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%4g,2023,4,1,12,0,30,SATURDAY,91,0,C",
            // Week-based year boundary cases - January 1st
            "%g,2025,1,1,12,0,30,WEDNESDAY,1,0,C",
            "%g,2026,1,1,12,0,30,THURSDAY,1,0,C",
            "%g,2027,1,1,12,0,30,FRIDAY,1,0,C",
            "%g,2028,1,1,12,0,30,SATURDAY,1,0,C",
            "%g,2022,1,1,12,0,30,SATURDAY,1,0,C",
            "%g,2020,1,1,12,0,30,WEDNESDAY,1,0,C",
            "%g,2021,1,1,12,0,30,FRIDAY,1,0,C",
            // December 31st boundary cases
            "%g,2023,12,31,12,0,30,SUNDAY,365,0,C",
            "%g,2024,12,31,12,0,30,TUESDAY,366,0,C",
            "%g,2025,12,31,12,0,30,WEDNESDAY,365,0,C",
            // Century transitions - complex cases
            "%g,1999,1,1,12,0,30,FRIDAY,1,0,C",
            "%g,2000,1,1,12,0,30,SATURDAY,1,0,C",
            "%g,2001,1,1,12,0,30,MONDAY,1,0,C",
            "%g,1999,12,31,12,0,30,FRIDAY,365,0,C",
            "%g,2000,12,31,12,0,30,SUNDAY,366,0,C",
            // Edge years for testing boundaries
            "%g,2019,12,30,12,0,30,MONDAY,364,0,C",
            "%g,2020,1,6,12,0,30,MONDAY,6,0,C",
            "%g,2021,1,4,12,0,30,MONDAY,4,0,C",
            "%g,2022,1,3,12,0,30,MONDAY,3,0,C",
            // Precision and padding tests
            "%6g,2023,6,15,12,0,30,THURSDAY,166,0,C",
            "%_4g,2000,6,15,12,0,30,THURSDAY,167,0,C",
            "%_6g,1999,6,15,12,0,30,TUESDAY,166,0,C",
            "%-g,2023,6,15,12,0,30,THURSDAY,166,0,C",
            // %G tests (4-digit week-based year)
            "%G,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%G,2023,1,1,12,0,30,SUNDAY,1,0,C",
            "%G,2024,1,1,12,0,30,MONDAY,1,0,C",
            "%G,2022,12,31,12,0,30,SATURDAY,365,0,C",
            "%_G,2023,4,1,12,0,30,SATURDAY,91,0,C",
            "%6G,2023,4,1,12,0,30,SATURDAY,91,0,C",
            // Week-based year boundary cases - January 1st
            "%G,2025,1,1,12,0,30,WEDNESDAY,1,0,C",
            "%G,2026,1,1,12,0,30,THURSDAY,1,0,C",
            "%G,2027,1,1,12,0,30,FRIDAY,1,0,C",
            "%G,2028,1,1,12,0,30,SATURDAY,1,0,C",
            "%G,2022,1,1,12,0,30,SATURDAY,1,0,C",
            "%G,2020,1,1,12,0,30,WEDNESDAY,1,0,C",
            "%G,2021,1,1,12,0,30,FRIDAY,1,0,C",
            // December 31st boundary cases
            "%G,2023,12,31,12,0,30,SUNDAY,365,0,C",
            "%G,2024,12,31,12,0,30,TUESDAY,366,0,C",
            "%G,2025,12,31,12,0,30,WEDNESDAY,365,0,C",
            // Century transitions - complex cases
            "%G,1999,1,1,12,0,30,FRIDAY,1,0,C",
            "%G,2000,1,1,12,0,30,SATURDAY,1,0,C",
            "%G,2001,1,1,12,0,30,MONDAY,1,0,C",
            "%G,1999,12,31,12,0,30,FRIDAY,365,0,C",
            "%G,2000,12,31,12,0,30,SUNDAY,366,0,C",
            // Edge years for testing boundaries
            "%G,2019,12,30,12,0,30,MONDAY,364,0,C",
            "%G,2020,1,6,12,0,30,MONDAY,6,0,C",
            "%G,2021,1,4,12,0,30,MONDAY,4,0,C",
            "%G,2022,1,3,12,0,30,MONDAY,3,0,C",
            // Precision and padding tests
            "%8G,2023,6,15,12,0,30,THURSDAY,166,0,C",
            "%_6G,2000,6,15,12,0,30,THURSDAY,167,0,C",
            "%_8G,1999,6,15,12,0,30,TUESDAY,166,0,C",
            "%-G,2023,6,15,12,0,30,THURSDAY,166,0,C",
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
                "%e",
                "%^e",
                "%#e",
                "%10e",
                "%^10e",
                "%#10e",
                "%010e",
                "%^010e",
                "%#010e",
                "%0e",
                "%_e",
                "%-e",
                "%3e",
                "%4e",
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
