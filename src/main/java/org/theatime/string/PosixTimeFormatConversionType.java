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

package org.theatime.string;

/**
 * The type of conversion to be applied by POSIX {@code strftime} and {@code strptime}.
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/009695399/functions/strftime.html">strftime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/007904875/functions/strptime.html">strptime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strptime.html">strptime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 */
@SuppressWarnings("checkstyle:LineLength")
enum PosixTimeFormatConversionType {
    LITERAL,

    /**
     * {@code %a} - The day of the week, using the locale's weekday names.
     *
     * <p>Formatting - Replaced by the locale's abbreviated weekday name. [tm_wday]
     *
     * <p>Parsing - The day of the week, using the locale's weekday names; either the abbreviated or full name may be specified.
     */
    DAY_OF_WEEK_TEXT_SHORT,

    /**
     * {@code %A} - The day of the week, using the locale's weekday names.
     *
     * <p>Formatting - Replaced by the locale's full weekday name. [tm_wday]
     *
     * <p>Parsing - The day of the week, using the locale's weekday names; either the abbreviated or full name may be specified.
     */
    DAY_OF_WEEK_TEXT_FULL,

    /**
     * {@code %b} - The month, using the locale's month names.
     *
     * <p>Formatting - Replaced by the locale's abbreviated month name. [tm_mon]
     *
     * <p>Parsing - The month, using the locale's month names; either the abbreviated or full name may be specified.
     */
    MONTH_OF_YEAR_TEXT_SHORT,

    /**
     * {@code %B} - The month, using the locale's month names.
     *
     * <p>Formatting - Replaced by the locale's full month name. [tm_mon]
     *
     * <p>Parsing - The month, using the locale's month names; either the abbreviated or full name may be specified.
     */
    MONTH_OF_YEAR_TEXT_FULL,

    /**
     * {@code %c}
     *
     * <p>Formatting - Replaced by the locale's appropriate date and time representation. (See the Base Definitions volume of POSIX.1-2017, {@code <time.h>}.)
     *
     * <p>Parsing - Replaced by the locale's appropriate date and time representation.
     */
    COMPOSITE_LOCAL_DATE_TIME,

    /**
     * {@code %C}
     *
     * <p>Formatting - Replaced by the year divided by 100 and truncated to an integer, as a decimal number. [tm_year]
     *
     * <p>If a minimum field width is not specified, the number of characters placed into the array pointed to by s will be the number of digits in the year divided by 100 or two, whichever is greater. [CX] [Option Start]  If a minimum field width is specified, the number of characters placed into the array pointed to by s will be the number of digits in the year divided by 100 or the minimum field width, whichever is greater. [Option End]
     *
     * <p>Parsing - All but the last two digits of the year {2}; leading zeros shall be permitted but shall not be required. A leading '+' or '-' character shall be permitted before any leading zeros but shall not be required.
     */
    CENTURY,

    /**
     * {@code %d}
     *
     * <p>Formatting - Replaced by the day of the month as a decimal number [01,31]. [tm_mday]
     *
     * <p>Parsing - The day of the month [01,31]; leading zeros shall be permitted but shall not be required.
     */
    DAY_OF_MONTH_ZERO,

    /**
     * {@code %D}
     *
     * <p>Formatting - Equivalent to {@code %m / %d / %y}. [{@code tm_mon}, {@code tm_mday}, {@code tm_year}]
     *
     * <p>Parsing - The date as {@code %m / %d / %y}.
     */
    DATE_MDY,

    /**
     * {@code %e}
     *
     * <p>Formatting - Replaced by the day of the month as a decimal number [1,31]; a single digit is preceded by a space. [tm_mday]
     *
     * <p>Parsing - Equivalent to {@code %d}.
     */
    DAY_OF_MONTH_SPACE,

    /**
     * {@code %F}
     *
     * Formatter only.
     *
     * <p>[CX] Equivalent to %+4[Option End]Y-%m-%d if no flag and no minimum field width are specified. [ tm_year, tm_mon, tm_mday]
     *
     * <p>[CX] [Option Start] If a minimum field width of x is specified, the year shall be output as if by the Y specifier (described below) with whatever flag was given and a minimum field width of x-6. If x is less than 6, the behavior shall be as if x equalled 6.
     *
     * <p>If the minimum field width is specified to be 10, and the year is four digits long, then the output string produced will match the ISO 8601:2000 standard subclause 4.1.2.2 complete representation, extended format date representation of a specific day. If a + flag is specified, a minimum field width of x is specified, and x-7 bytes are sufficient to hold the digits of the year (not including any needed sign character), then the output will match the ISO 8601:2000 standard subclause 4.1.2.4 complete representation, expanded format date representation of a specific day. [Option End]
     */
    YEAR_MONTH_DAY,

    /**
     * {@code %g}
     *
     * Formatter only.
     *
     * <p>Formatting - Replaced by the last 2 digits of the week-based year (see below) as a decimal number [00,99]. [ tm_year, tm_wday, tm_yday]
    */
    WEEK_BASED_YEAR_OFFSET,

    /**
     * {@code %G}
     *
     * Formatter only.
     *
     * <p>Replaced by the week-based year (see below) as a decimal number (for example, 1977). [ tm_year, tm_wday, tm_yday]
     *
     * <p>[CX] [Option Start] If a minimum field width is specified, the number of characters placed into the array pointed to by s will be the number of digits and leading sign characters (if any) in the year, or the minimum field width, whichever is greater. [Option End]
     */
    WEEK_BASED_YEAR_FULL,

    /**
     * {@code %h}
     *
     * <p>Formatting - Equivalent to %b. [tm_mon]
     *
     * <p>Parsing - Equivalent to %b.
     */
    MONTH_TEXT_SHORT_ALIAS,

    /**
     * {@code %H}
     *
     * <p>Formatting - Replaced by the hour (24-hour clock) as a decimal number [00,23]. [tm_hour]
     *
     * <p>Parsing - The hour (24-hour clock) [00,23]; leading zeros shall be permitted but shall not be required.
     */
    HOUR_OF_DAY,

    /**
     * {@code %I}
     *
     * <p>Formatting - Replaced by the hour (12-hour clock) as a decimal number [01,12]. [tm_hour]
     *
     * <p>Parsing - The hour (12-hour clock) [01,12]; leading zeros shall be permitted but shall not be required.
     */
    HOUR_OF_AMPM,

    /**
     * {@code %j}
     *
     * <p>Formatting - Replaced by the day of the year as a decimal number [001,366]. [ tm_yday]
     *
     * <p>Parsing - The day number of the year [001,366]; leading zeros shall be permitted but shall not be required.
     */
    DAY_OF_YEAR,

    /**
     * {@code %m}
     *
     * <p>Formatting - Replaced by the month as a decimal number [01,12]. [ tm_mon]
     *
     * <p>Parsing - The month number [01,12]; leading zeros shall be permitted but shall not be required.
     */
    MONTH_OF_YEAR,

    /**
     * {@code %M}
     *
     * <p>Formatting - Replaced by the minute as a decimal number [00,59]. [ tm_min]
     *
     * <p>Parsing - The minute [00,59]; leading zeros shall be permitted but shall not be required.
     */
    MINUTE_OF_HOUR,

    /**
     * {@code %n}
     *
     * <p>Formatting - Replaced by a {@code <newline>}.
     *
     * <p>Parsing - Any white space.
     */
    LITERAL_WHITESPACE_NEWLINE,

    /**
     * {@code %p}
     *
     * <p>Formatting - Replaced by the locale's equivalent of either a.m. or p.m. [tm_hour]
     *
     * <p>Parsing - The locale's equivalent of a.m. or p.m.
     */
    AMPM_OF_DAY,

    /**
     * {@code %r}
     *
     * <p>Formatting - Replaced by the time in a.m. and p.m. notation; [CX] [Option Start]  in the POSIX locale this shall be equivalent to {@code %I : %M : %S %p}. [Option End] [tm_hour, tm_min, tm_sec]
     *
     * <p>Parsing - 12-hour clock time using the AM/PM notation if t_fmt_ampm is not an empty string in the LC_TIME portion of the current locale; in the POSIX locale, this shall be equivalent to {@code %I : %M : %S %p}.
     */
    TIME_12_AMPM,

    /**
     * {@code %R}
     *
     * <p>Formatting - Replaced by the time in 24-hour notation ({@code %H : %M}). [tm_hour, tm_min]
     *
     * <p>Parsing - The time as {@code %H : %M}.
     */
    HOUR_MINUTE_24,

    /**
     * {@code %S}
     *
     * <p>Formatting - Replaced by the second as a decimal number [00,60]. [tm_sec]
     *
     * <p>Parsing - The seconds [00,60]; leading zeros shall be permitted but shall not be required.
     */
    SECOND_OF_MINUTE,

    /**
     * {@code %t}
     *
     * <p>Formatting - Replaced by a {@code <tab>}.
     *
     * <p>Parsing - Any white space.
     */
    LITERAL_WHITESPACE_TAB,

    /**
     * {@code %T}
     *
     * <p>Formatting - Replaced by the time ({@code %H : %M : %S}). [tm_hour, tm_min, tm_sec]
     *
     * <p>Parsing - The time as {@code %H : %M : %S}.
     */
    TIME_24,

    /**
     * {@code %u}
     *
     * Formatter only.
     *
     * <p>Formatting - Replaced by the weekday as a decimal number [1,7], with 1 representing Monday. [tm_wday]
     */
    DAY_OF_WEEK_1_7,

    /**
     * {@code %U}
     *
     * <p>Formatting - Replaced by the week number of the year as a decimal number [00,53]. The first Sunday of January is the first day of week 1; days in the new year before this are in week 0. [ tm_year, tm_wday, tm_yday]
     *
     * <p>Parsing - The week number of the year (Sunday as the first day of the week) as a decimal number [00,53]; leading zeros shall be permitted but shall not be required.
     */
    WEEK_NUMBER_OF_YEAR_SUNDAY_0,

    /**
     * {@code %V}
     *
     * Formatter only.
     *
     * <p>Formatting - Replaced by the week number of the year (Monday as the first day of the week) as a decimal number [01,53]. If the week containing 1 January has four or more days in the new year, then it is considered week 1. Otherwise, it is the last week of the previous year, and the next week is week 1. Both January 4th and the first Thursday of January are always in week 1. [ tm_year, tm_wday, tm_yday]
     */
    WEEK_NUMBER_OF_YEAR_MONDAY_1,

    /**
     * {@code %w}
     *
     * <p>Formatting - Replaced by the weekday as a decimal number [0,6], with 0 representing Sunday. [tm_wday]
     *
     * <p>Parsing - The weekday as a decimal number [0,6], with 0 representing Sunday.
     */
    DAY_OF_WEEK_0_6,

    /**
     * {@code %W}
     *
     * <p>Formatting - Replaced by the week number of the year as a decimal number [00,53]. The first Monday of January is the first day of week 1; days in the new year before this are in week 0. [ tm_year, tm_wday, tm_yday]
     *
     * <p>Parsing - The week number of the year (Monday as the first day of the week) as a decimal number [00,53]; leading zeros shall be permitted but shall not be required.
     */
    WEEK_NUMBER_OF_YEAR_MONDAY_0,

    /**
     * {@code %x}
     *
     * <p>Formatting - Replaced by the locale's appropriate date representation. (See the Base Definitions volume of POSIX.1-2017, {@code <time.h>}.)
     *
     * <p>Parsing - The date, using the locale's date format.
     */
    DATE_LOCALE,

    /**
     * {@code %X}
     *
     * <p>Formatting - Replaced by the locale's appropriate time representation. (See the Base Definitions volume of POSIX.1-2017, {@code <time.h>}.)
     *
     * <p>Parsing - The time, using the locale's time format.
     */
    TIME_LOCALE,

    /**
     * {@code %y}
     *
     * <p>Formatting - Replaced by the last two digits of the year as a decimal number [00,99]. [tm_year]
     *
     * <p>Parsing - The last two digits of the year. When format contains neither a C conversion specifier nor a Y conversion specifier, values in the range [69,99] shall refer to years 1969 to 1999 inclusive and values in the range [00,68] shall refer to years 2000 to 2068 inclusive; leading zeros shall be permitted but shall not be required. A leading '+' or '-' character shall be permitted before any leading zeros but shall not be required.
Note:
It is expected that in a future version of this standard the default century inferred from a 2-digit year will change. (This would apply to all commands accepting a 2-digit year as input.)
     */
    YEAR_TWO_DIGITS,

    /**
     * {@code %Y}
     *
     * <p>Formatting - Replaced by the year as a decimal number (for example, 1997). [tm_year]
     *
     * [CX] [Option Start] If a minimum field width is specified, the number of characters placed into the array pointed to by s will be the number of digits and leading sign characters (if any) in the year, or the minimum field width, whichever is greater. [Option End]
     *
     * <p>Parsing - The full year {4}; leading zeros shall be permitted but shall not be required. A leading '+' or '-' character shall be permitted before any leading zeros but shall not be required.
     */
    YEAR,

    /**
     * {@code %z}
     *
     * Formatter only.
     *
     * <p>Formatting - Replaced by the offset from UTC in the ISO 8601:2000 standard format ( +hhmm or -hhmm ), or by no characters if no timezone is determinable. For example, "-0430" means 4 hours 30 minutes behind UTC (west of Greenwich). [CX] [Option Start]  If tm_isdst is zero, the standard time offset is used. If tm_isdst is greater than zero, the daylight savings time offset is used. If tm_isdst is negative, no characters are returned. [Option End] [ tm_isdst]
     */
    ZONE_OFFSET,

    /**
     * {@code %Z}
     *
     * Formatter only.
     *
     * <p>Formatting - Replaced by the timezone name or abbreviation, or by no bytes if no timezone information exists. [tm_isdst]
     */
    ZONE_NAME,

    /**
     * {@code %%}
     *
     * <p>Formatting - Replaced by {@code %}.
     *
     * <p>Parsing - Replaced by {@code %}.
     */
    LITERAL_PERCENT,
    ;
}
