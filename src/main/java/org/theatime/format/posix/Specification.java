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

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents each individual specification in POSIX strftime / strptime formats.
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/009695399/functions/strftime.html">strftime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/007904875/functions/strptime.html">strptime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strptime.html">strptime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 */
public abstract class Specification {
    Specification(final Context context) {
        this.upperCase = context.upperCase;
        this.changeCase = context.changeCase;
        this.precision = context.precision;
        this.colons = context.colons;
        this.pad = context.pad;
        this.modifier = context.modifier;

        this.entireFormat = context.entireFormat;
        this.start = context.start;
        this.end = context.end;
    }

    public final String original() {
        return this.entireFormat.substring(this.start, this.end);
    }

    abstract DateTimeFormatterBuilder appendTo(
            DateTimeFormatterBuilder formatter,
            PaddingStyle paddingStyle,
            Optional<Locale> locale);

    final char effectivePadWithDefault(final char defaultPad) {
        if (this.pad == '\0') {
            return defaultPad;
        } else if (this.pad == '0') {
            return '0';
        } else if (this.pad == '_') {
            return ' ';
        } else if (this.pad == '-') {
            return ' ';
        }
        throw new IllegalStateException("Illegal pad: '" + this.pad + "'");
    }

    final boolean isLeftAligned() {
        return this.pad == '-';
    }

    static class Context {
        Context(final boolean upperCase,
                final boolean changeCase,
                final int precision,
                final int colons,
                final char pad,
                final char modifier,
                final String entireFormat,
                final int start,
                final int end) {
            this.upperCase = upperCase;
            this.changeCase = changeCase;
            this.precision = precision;
            this.colons = colons;
            this.pad = pad;
            this.modifier = modifier;
            this.entireFormat = entireFormat;
            this.start = start;
            this.end = end;
        }

        final boolean upperCase;
        final boolean changeCase;
        final int precision;
        final int colons;
        final char pad;
        final char modifier;

        final String entireFormat;
        final int start;
        final int end;
    }

    static class ContextSource {
        ContextSource(final String entireFormat) {
            this.entireFormat = entireFormat;
            this.upperCase = false;
            this.changeCase = false;
            this.precision = -1;
            this.colons = -1;
            this.pad = '\0';
            this.modifier = '\0';
        }

        ContextSource upperCase() {
            this.upperCase = true;
            return this;
        }

        ContextSource changeCase() {
            this.changeCase = true;
            return this;
        }

        ContextSource precision(final int precision) {
            this.precision = precision;
            return this;
        }

        ContextSource colons(final int colons) {
            this.colons = colons;
            return this;
        }

        ContextSource pad(final char pad) {
            this.pad = pad;
            return this;
        }

        ContextSource modifier(final char modifier) {
            this.modifier = modifier;
            return this;
        }

        Context at(final int start, final int end) {
            return new Context(
                    this.upperCase,
                    this.changeCase,
                    this.precision,
                    this.colons,
                    this.pad,
                    this.modifier,
                    this.entireFormat,
                    start,
                    end);
        }

        private final String entireFormat;

        private boolean upperCase;
        private boolean changeCase;
        private int precision;
        private int colons;
        private char pad;
        private char modifier;
    }

    static final Context EMPTY = new Context(false, false, -1, -1, '\0', '\0', "", 0, 0);

    final boolean upperCase;
    final boolean changeCase;
    final int precision;
    final int colons;
    final char pad;
    final char modifier;

    final String entireFormat;
    final int start;
    final int end;
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
abstract class ConversionSpecification extends Specification {
    ConversionSpecification(
            final ConversionType terminatingConversionSpecifier,
            final Context context) {
        super(context);
        this.terminatingConversionSpecifier = terminatingConversionSpecifier;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof ConversionSpecification)) {
            return false;
        }

        final ConversionSpecification other = (ConversionSpecification) otherObject;
        return Objects.equals(this.getClass(), other.getClass())
                && Objects.equals(this.terminatingConversionSpecifier, other.terminatingConversionSpecifier)
                && Objects.equals(this.upperCase, other.upperCase)
                && Objects.equals(this.changeCase, other.changeCase)
                && Objects.equals(this.precision, other.precision)
                && Objects.equals(this.colons, other.colons)
                && Objects.equals(this.pad, other.pad)
                && Objects.equals(this.modifier, other.modifier)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getClass(),
                this.terminatingConversionSpecifier,
                this.upperCase,
                this.changeCase,
                this.precision,
                this.colons,
                this.pad,
                this.modifier);
    }

    public String explain() {
        final StringBuilder builder = new StringBuilder().append("<").append(this.entireFormat);
        if (this.upperCase) {
            builder.append(":uppercase");
        }
        if (this.changeCase) {
            builder.append(":changecase");
        }
        if (this.precision >= 0) {
            builder.append(":precision=").append(this.precision);
        }
        if (this.colons >= 0) {
            builder.append(":colons=").append(this.colons);
        }
        if (this.pad != '\0') {
            builder.append(":pad=").append(this.pad);
        }
        if (this.modifier != '\0') {
            builder.append(":modifier=").append(this.modifier);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder().append("%");

        if (this.upperCase) {
            builder.append("^");
        }

        if (this.changeCase) {
            builder.append("#");
        }

        if (this.pad == '\0') {
            // Pass-through.
        } else if (this.pad == '0') {
            builder.append("0");
        } else if (this.pad == '_') {
            builder.append("_");
        } else if (this.pad == '-') {
            builder.append("-");
        } else {
            throw new IllegalStateException("Illegal pad: " + this.pad);
        }

        if (this.precision > 0) {
            builder.append(this.precision);
        }

        if (this.colons >= 0) {
            for (int i = 0; i < this.colons; i++) {
                builder.append(':');
            }
        }
        if (this.modifier == '\0') {
            // Pass-through.
        } else if (this.modifier == 'E') {
            builder.append('E');
        } else if (this.modifier == '0') {
            builder.append('O');
        } else {
            throw new IllegalStateException("Illegal modifier: " + this.modifier);
        }

        builder.append(this.terminatingConversionSpecifier);

        return builder.toString();
    }

    static String repeat(final String s, final int n) {
        return String.join("", Collections.nCopies(n, s));
    }

    final ConversionType terminatingConversionSpecifier;
}

/**
 * {@code %a} - The day of the week, using the locale's weekday names.
 *
 * <p>Formatting - Replaced by the locale's abbreviated weekday name. [tm_wday]
 *
 * <p>Parsing - The day of the week, using the locale's weekday names; either the abbreviated or full name may be specified.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerA extends ConversionSpecification {
    LowerA(final Context context) {
        super(ConversionType.POSIX_LOWER_A, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 4) {
            formatter.padNext(this.precision, this.effectivePadWithDefault(' '));
        }

        if (locale.isPresent()) {
            if (this.upperCase || this.changeCase) {
                // TODO: https://stackoverflow.com/a/55236266
                throw new DateTimeException("\"%a\" does not accept \"^\" nor \"#\" with a locale.");
            }
            return formatter.appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT);
        }

        if (this.upperCase || this.changeCase) {
            return formatter.appendText(ChronoField.DAY_OF_WEEK, DayOfWeekNames.shortUpperCase());
        } else {
            return formatter.appendText(ChronoField.DAY_OF_WEEK, DayOfWeekNames.shortTitleCase());
        }
    }
}

/**
 * {@code %A} - The day of the week, using the locale's weekday names.
 *
 * <p>Formatting - Replaced by the locale's full weekday name. [tm_wday]
 *
 * <p>Parsing - The day of the week, using the locale's weekday names; either the abbreviated or full name may be specified.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperA extends ConversionSpecification {
    UpperA(final Context context) {
        super(ConversionType.POSIX_UPPER_A, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (locale.isPresent()) {
            throw new DateTimeException("\"%A\" does not accept a locale.");
        }

        /*
         * Parsing accepts lenient parsing:
         * * https://github.com/ThreeTen/threeten/issues/235
         * * https://github.com/ThreeTen/threeten/issues/65
         *
         * Formatting does not accept padding shorter than the precision, though.
         */
        if (this.precision <= 6 || this.precision >= 9) {
            if (this.precision >= 9) {
                formatter.padNext(this.precision, this.effectivePadWithDefault(' '));
            }
            if (this.upperCase || this.changeCase) {
                return formatter.appendText(ChronoField.DAY_OF_WEEK, DayOfWeekNames.fullUpperCase());
            } else {
                return formatter.appendText(ChronoField.DAY_OF_WEEK, DayOfWeekNames.fullTitleCase());
            }
        } else {
            if (this.upperCase || this.changeCase) {
                return formatter.appendText(ChronoField.DAY_OF_WEEK, softPad(DayOfWeekNames.fullUpperCase(), precision, this.effectivePadWithDefault(' ')));
            } else {
                return formatter.appendText(ChronoField.DAY_OF_WEEK, softPad(DayOfWeekNames.fullTitleCase(), precision, this.effectivePadWithDefault(' ')));
            }
        }
    }

    private static Map<Long, String> softPad(final Map<Long, String> baseTexts, final int precision, final char pad) {
        final HashMap<Long, String> texts = new HashMap<>();
        for (final Map.Entry<Long, String> entry : baseTexts.entrySet()) {
            texts.put(entry.getKey(), softPad(entry.getValue(), precision, pad));
        }
        return Collections.unmodifiableMap(texts);
    }

    private static String softPad(final String text, final int precision, final char pad) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < precision - text.length(); i++) {
            builder.append(pad);
        }
        builder.append(text);
        return builder.toString();
    }
}

/**
 * {@code %b} - The month, using the locale's month names.
 *
 * <p>Formatting - Replaced by the locale's abbreviated month name. [tm_mon]
 *
 * <p>Parsing - The month, using the locale's month names; either the abbreviated or full name may be specified.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerB extends ConversionSpecification {
    LowerB(final Context context) {
        super(ConversionType.POSIX_LOWER_B, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 4) {
            formatter.padNext(this.precision, this.effectivePadWithDefault(' '));
        }

        if (locale.isPresent()) {
            if (this.upperCase || this.changeCase) {
                // TODO: https://stackoverflow.com/a/55236266
                throw new DateTimeException("\"%b\" does not accept \"^\" nor \"#\" with a locale.");
            }
            return formatter.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT);
        }

        if (this.upperCase || this.changeCase) {
            return formatter.appendText(ChronoField.MONTH_OF_YEAR, MonthOfYearNames.shortUpperCase());
        } else {
            return formatter.appendText(ChronoField.MONTH_OF_YEAR, MonthOfYearNames.shortTitleCase());
        }
    }
}

/**
 * {@code %B} - The month, using the locale's month names.
 *
 * <p>Formatting - Replaced by the locale's full month name. [tm_mon]
 *
 * <p>Parsing - The month, using the locale's month names; either the abbreviated or full name may be specified.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperB extends ConversionSpecification {
    UpperB(final Context context) {
        super(ConversionType.POSIX_UPPER_B, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (locale.isPresent()) {
            throw new DateTimeException("\"%B\" does not accept a locale.");
        }

        if (this.precision <= 3 || this.precision >= 9) {
            if (this.precision >= 9) {
                formatter.padNext(this.precision, this.effectivePadWithDefault(' '));
            }
            if (this.upperCase || this.changeCase) {
                return formatter.appendText(ChronoField.MONTH_OF_YEAR, MonthOfYearNames.fullUpperCase());
            } else {
                return formatter.appendText(ChronoField.MONTH_OF_YEAR, MonthOfYearNames.fullTitleCase());
            }
        } else {
            if (this.upperCase || this.changeCase) {
                return formatter.appendText(ChronoField.MONTH_OF_YEAR, softPad(MonthOfYearNames.fullUpperCase(), precision, this.effectivePadWithDefault(' ')));
            } else {
                return formatter.appendText(ChronoField.MONTH_OF_YEAR, softPad(MonthOfYearNames.fullTitleCase(), precision, this.effectivePadWithDefault(' ')));
            }
        }
    }

    private static Map<Long, String> softPad(final Map<Long, String> baseTexts, final int precision, final char pad) {
        final HashMap<Long, String> texts = new HashMap<>();
        for (final Map.Entry<Long, String> entry : baseTexts.entrySet()) {
            texts.put(entry.getKey(), softPad(entry.getValue(), precision, pad));
        }
        return Collections.unmodifiableMap(texts);
    }

    private static String softPad(final String text, final int precision, final char pad) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < precision - text.length(); i++) {
            builder.append(pad);
        }
        builder.append(text);
        return builder.toString();
    }
}

/**
 * {@code %c}
 *
 * <p>Formatting - Replaced by the locale's appropriate date and time representation.
 * (See the Base Definitions volume of POSIX.1-2017, {@code <time.h>}.)
 *
 * <p>Parsing - Replaced by the locale's appropriate date and time representation.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerC extends ConversionSpecification {
    LowerC(final Context context) {
        super(ConversionType.POSIX_LOWER_C, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        // In the C or POSIX locale, the E and O modifiers are ignored and
        // the replacement strings for the following specifiers are:
        //
        // ...
        //   ...
        // %c
        //   Equivalent to %a %b %e %T %Y.
        //
        // strftime, strftime_l - convert date and time to a string
        // The Open Group Base Specifications Issue 7, 2018 edition
        // https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html#tag_16_576_07

        return formatter
                .appendText(ChronoField.DAY_OF_WEEK, (this.upperCase ? DayOfWeekNames.shortUpperCase() : DayOfWeekNames.shortTitleCase()))
                .appendLiteral(" ")
                .appendText(ChronoField.MONTH_OF_YEAR, (this.upperCase ? MonthOfYearNames.shortUpperCase() : MonthOfYearNames.shortTitleCase()))
                .appendLiteral(" ")
                .padNext(2, ' ')
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral(" ")
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(":")
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(":")
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendLiteral(" ")
                .appendValue(ChronoField.YEAR, 1, 19, SignStyle.NORMAL);
    }
}

/**
 * {@code %C}
 *
 * <p>Formatting - Replaced by the year divided by 100 and truncated to an integer, as a decimal number. [tm_year]
 *
 * <p>If a minimum field width is not specified, the number of characters placed into the array pointed to by s will be
 * the number of digits in the year divided by 100 or two, whichever is greater. [CX] [Option Start]  If a minimum field
 * width is specified, the number of characters placed into the array pointed to by s will be the number of digits in the
 * year divided by 100 or the minimum field width, whichever is greater. [Option End]
 *
 * <p>Parsing - All but the last two digits of the year {2}; leading zeros shall be permitted but shall not be required.
 * A leading '+' or '-' character shall be permitted before any leading zeros but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperC extends ConversionSpecification {
    UpperC(final Context context) {
        super(ConversionType.POSIX_UPPER_C, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 0) {
            final char pad = this.effectivePadWithDefault('0');
            if (pad == '0') {
                if (this.precision >= 2) {
                    return formatter.appendValue(PosixFields.POSIX_CENTURY, this.precision);
                } else {
                    return formatter.appendValue(PosixFields.POSIX_CENTURY);
                }
            } else {
                if (this.precision >= 2) {
                    formatter.padNext(this.precision, pad);
                }
                return formatter.appendValue(PosixFields.POSIX_CENTURY);
            }
        }
        return formatter.appendValue(PosixFields.POSIX_CENTURY);
    }
}

/**
 * {@code %d}
 *
 * <p>Formatting - Replaced by the day of the month as a decimal number [01,31]. [tm_mday]
 *
 * <p>Parsing - The day of the month [01,31]; leading zeros shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerD extends ConversionSpecification {
    LowerD(final Context context) {
        super(ConversionType.POSIX_LOWER_D, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 0) {
            final char pad = this.effectivePadWithDefault('0');
            if (pad == '0') {
                return formatter.appendValue(ChronoField.DAY_OF_MONTH, (this.precision > 2 ? this.precision : 2));
            } else {
                if (this.isLeftAligned()) {
                    if (this.precision >= 2) {
                        formatter.padNext(this.precision, pad);
                    }
                } else {
                    formatter.padNext((this.precision > 2 ? this.precision : 2), pad);
                }
                return formatter.appendValue(ChronoField.DAY_OF_MONTH);
            }
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(2, this.effectivePadWithDefault('0'));
        }
        return formatter.appendValue(ChronoField.DAY_OF_MONTH);
    }
}

/**
 * {@code %D}
 *
 * <p>Formatting - Equivalent to {@code %m / %d / %y}. [{@code tm_mon}, {@code tm_mday}, {@code tm_year}]
 *
 * <p>Parsing - The date as {@code %m / %d / %y}.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperD extends ConversionSpecification {
    UpperD(final Context context) {
        super(ConversionType.POSIX_UPPER_D, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision > 8) {
            // The padding for "%D" is independent from the padding for MONTH_OF_YEAR.
            if (this.effectivePadWithDefault(' ') == '0') {
                formatter.appendLiteral(repeat("0", this.precision - 8));
            } else {
                formatter.appendLiteral(repeat(" ", this.precision - 8));
            }
        }

        return formatter
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)  // The padding for this MONTH_OF_YEAR is always '0'.
                .appendLiteral('/')
                .appendValue(ChronoField.DAY_OF_MONTH, 2)
                .appendLiteral('/')
                .appendValueReduced(ChronoField.YEAR, 2, 2, 1900);
    }
}

/**
 * {@code %e}
 *
 * <p>Formatting - Replaced by the day of the month as a decimal number [1,31]; a single digit is preceded by a space. [tm_mday]
 *
 * <p>Parsing - Equivalent to {@code %d}.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerE extends ConversionSpecification {
    LowerE(final Context context) {
        super(ConversionType.POSIX_LOWER_E, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 0) {
            final char pad = this.effectivePadWithDefault(' ');
            if (pad == '0') {
                return formatter.appendValue(ChronoField.DAY_OF_MONTH, (this.precision > 2 ? this.precision : 2));
            } else {
                if (this.isLeftAligned()) {
                    if (this.precision >= 2) {
                        formatter.padNext(this.precision, pad);
                    }
                } else {
                    formatter.padNext((this.precision > 2 ? this.precision : 2), pad);
                }
                return formatter.appendValue(ChronoField.DAY_OF_MONTH);
            }
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(2, ' ');
        }
        return formatter.appendValue(ChronoField.DAY_OF_MONTH);
    }
}

/**
 * {@code %F}
 *
 * Formatter only.
 *
 * <p>[CX] Equivalent to %+4[Option End]Y-%m-%d if no flag and no minimum field width are specified. [ tm_year, tm_mon, tm_mday]
 *
 * <p>[CX] [Option Start] If a minimum field width of x is specified, the year shall be output as if by the Y specifier
 * (described below) with whatever flag was given and a minimum field width of x-6. If x is less than 6, the behavior shall
 * be as if x equalled 6.
 *
 * <p>If the minimum field width is specified to be 10, and the year is four digits long, then the output string produced
 * will match the ISO 8601:2000 standard subclause 4.1.2.2 complete representation, extended format date representation of
 * a specific day. If a + flag is specified, a minimum field width of x is specified, and x-7 bytes are sufficient to hold
 * the digits of the year (not including any needed sign character), then the output will match the ISO 8601:2000 standard
 * subclause 4.1.2.4 complete representation, expanded format date representation of a specific day. [Option End]
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperF extends ConversionSpecification {
    UpperF(final Context context) {
        super(ConversionType.POSIX_UPPER_F, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision > 6) {
            final char pad = this.effectivePadWithDefault(' ');
            if (pad == '0') {
                formatter.appendValue(ChronoField.YEAR, Math.max(this.precision - 6, 1), 19, SignStyle.NORMAL);
            } else {
                // When padding with ' ', ranged padding does not work, therefore strftime cannot be emulated 100%.
                //
                // For example, strftime formats 10-12-31 into:
                //   * into "10-12-31" by "%_8F"
                //   * into " 10-12-31" by "%_9F"
                //   * into "  10-12-31" by "%_10F".
                //
                // At the same time, strftime formats 1000-12-31 into:
                //   * into "1000-12-31" by "%_8Y"
                //   * into "1000-12-31" by "%_9Y"
                //   * into "1000-12-31" by "%_10Y".
                //
                // This flexibility of ranged padding cannot be realized in java.time.format.DateTimeFormatterBuilder.
                switch (paddingStyle) {
                    case STRICT:
                        throw new UnsupportedPaddingException("Padding %F is not supported in the STRICT padding style.");
                    case SMART:
                        if (this.precision < 10) {
                            throw new UnsupportedPaddingException(
                                "Padding %F with width" + this.precision + " is not supported in the SMART padding style.");
                        }
                        formatter.padNext(this.precision - 6, pad);
                        break;
                    case LENIENT:
                        // No padding actually.
                        break;
                    default:
                        throw new UnsupportedPaddingException();
                }
                formatter.appendValue(ChronoField.YEAR, 1, 19, SignStyle.NORMAL);
            }
        } else {
            formatter.appendValue(ChronoField.YEAR, 1, 19, SignStyle.NORMAL);
        }

        return formatter
                .appendLiteral('-')
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(ChronoField.DAY_OF_MONTH, 2);
    }
}

/**
 * {@code %g}
 *
 * Formatter only.
 *
 * <p>Formatting - Replaced by the last 2 digits of the week-based year (see below) as a decimal number [00,99].
 * [ tm_year, tm_wday, tm_yday]
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerG extends ConversionSpecification {
    LowerG(final Context context) {
        super(ConversionType.POSIX_LOWER_G, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        final char pad = this.effectivePadWithDefault('0');
        if (this.precision >= 0) {
            if (pad == '0') {
                if (this.precision > 2) {
                    formatter.padNext(this.precision, pad);
                    return formatter.appendValueReduced(IsoFields.WEEK_BASED_YEAR, 1, 2, 1900);
                } else {
                    if (this.isLeftAligned()) {
                        return formatter.appendValueReduced(IsoFields.WEEK_BASED_YEAR, 1, 2, 1900);
                    } else {
                        return formatter.appendValueReduced(IsoFields.WEEK_BASED_YEAR, 2, 2, 1900);
                    }
                }
            } else {
                if (this.isLeftAligned()) {
                    if (this.precision >= 2) {
                        formatter.padNext(this.precision, pad);
                    }
                } else {
                    if (this.precision > 2) {
                        formatter.padNext(this.precision, pad);
                    } else {
                        formatter.padNext(2, pad);
                    }
                }
                return formatter.appendValueReduced(IsoFields.WEEK_BASED_YEAR, 1, 2, 1900);
            }
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(2, pad);
        }
        return formatter.appendValueReduced(IsoFields.WEEK_BASED_YEAR, 1, 2, 1900);
    }
}

/**
 * {@code %G}
 *
 * Formatter only.
 *
 * <p>Replaced by the week-based year (see below) as a decimal number (for example, 1977). [ tm_year, tm_wday, tm_yday]
 *
 * <p>[CX] [Option Start] If a minimum field width is specified, the number of characters placed into the array pointed to
 * by s will be the number of digits and leading sign characters (if any) in the year, or the minimum field width, whichever
 * is greater. [Option End]
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperG extends ConversionSpecification {
    UpperG(final Context context) {
        super(ConversionType.POSIX_UPPER_G, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        final char pad = this.effectivePadWithDefault('0');
        if (this.precision > 0) {
            if (pad == '0') {
                if (this.precision > 0) {
                    return formatter.appendValue(IsoFields.WEEK_BASED_YEAR, this.precision, 19, SignStyle.NORMAL);
                } else {
                    return formatter.appendValue(IsoFields.WEEK_BASED_YEAR, 1, 19, SignStyle.NORMAL);
                }
            } else {
                // When padding with ' ', ranged padding does not work, therefore strftime cannot be emulated 100%.
                //
                // For example, strftime formats 10 into:
                //   * into "10" by "%_2G"
                //   * into " 10" by "%_3G"
                //   * into "  10" by "%_4G".
                //
                // This flexibility of ranged padding cannot be realized in java.time.format.DateTimeFormatterBuilder.
                switch (paddingStyle) {
                    case STRICT:
                        throw new UnsupportedPaddingException("Padding %G is not supported in the STRICT padding style.");
                    case SMART:
                        if (this.precision < 4) {
                            throw new UnsupportedPaddingException(
                                    "Padding %G with width" + this.precision + " is not supported in the SMART padding style.");
                        }
                        formatter.padNext(this.precision, pad);
                        break;
                    case LENIENT:
                        // No padding actually.
                        break;
                    default:
                        throw new UnsupportedPaddingException();
                }
                return formatter.appendValue(IsoFields.WEEK_BASED_YEAR, 1, 19, SignStyle.NORMAL);
            }
        }

        return formatter.appendValue(IsoFields.WEEK_BASED_YEAR, 1, 19, SignStyle.NORMAL);
    }
}

/**
 * {@code %h}
 *
 * <p>Formatting - Equivalent to %b. [tm_mon]
 *
 * <p>Parsing - Equivalent to %b.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerH extends ConversionSpecification {
    LowerH(final Context context) {
        super(ConversionType.POSIX_LOWER_H, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT);
    }
}

/**
 * {@code %H}
 *
 * <p>Formatting - Replaced by the hour (24-hour clock) as a decimal number [00,23]. [tm_hour]
 *
 * <p>Parsing - The hour (24-hour clock) [00,23]; leading zeros shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperH extends ConversionSpecification {
    UpperH(final Context context) {
        super(ConversionType.POSIX_UPPER_H, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 0) {
            final char pad = this.effectivePadWithDefault('0');
            if (pad == '0') {
                return formatter.appendValue(ChronoField.HOUR_OF_DAY, (this.precision > 2 ? this.precision : 2));
            } else {
                if (this.isLeftAligned()) {
                    if (this.precision >= 2) {
                        formatter.padNext(this.precision, pad);
                    }
                } else {
                    formatter.padNext((this.precision > 2 ? this.precision : 2), pad);
                }
                return formatter.appendValue(ChronoField.HOUR_OF_DAY);
            }
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(2, this.effectivePadWithDefault('0'));
        }
        return formatter.appendValue(ChronoField.HOUR_OF_DAY);
    }
}

/**
 * {@code %I}
 *
 * <p>Formatting - Replaced by the hour (12-hour clock) as a decimal number [01,12]. [tm_hour]
 *
 * <p>Parsing - The hour (12-hour clock) [01,12]; leading zeros shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperI extends ConversionSpecification {
    UpperI(final Context context) {
        super(ConversionType.POSIX_UPPER_I, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 0) {
            final char pad = this.effectivePadWithDefault('0');
            if (pad == '0') {
                return formatter.appendValue(ChronoField.CLOCK_HOUR_OF_AMPM, (this.precision > 2 ? this.precision : 2));
            } else {
                if (this.isLeftAligned()) {
                    if (this.precision >= 2) {
                        formatter.padNext(this.precision, pad);
                    }
                } else {
                    formatter.padNext((this.precision > 2 ? this.precision : 2), pad);
                }
                return formatter.appendValue(ChronoField.CLOCK_HOUR_OF_AMPM);
            }
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(2, this.effectivePadWithDefault('0'));
        }
        return formatter.appendValue(ChronoField.CLOCK_HOUR_OF_AMPM);
    }
}

/**
 * {@code %j}
 *
 * <p>Formatting - Replaced by the day of the year as a decimal number [001,366]. [ tm_yday]
 *
 * <p>Parsing - The day number of the year [001,366]; leading zeros shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerJ extends ConversionSpecification {
    LowerJ(final Context context) {
        super(ConversionType.POSIX_LOWER_J, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        final char pad = this.effectivePadWithDefault('0');
        if (pad == '0') {
            // When padding with '0', ranged padding works.
            return formatter.appendValue(ChronoField.DAY_OF_YEAR, (this.precision > 3 ? this.precision : 3));
        }

        if (this.precision > 0) {
            // When padding with ' ', ranged padding does not work, therefore strftime cannot be emulated 100%.
            //
            // For example, strftime formats 10 into:
            //   * into "10" by "%_2j"
            //   * into " 10" by "%_3j"
            //   * into "  10" by "%_4j".
            //
            // This flexibility of ranged padding cannot be realized in java.time.format.DateTimeFormatterBuilder.
            switch (paddingStyle) {
                case STRICT:
                    throw new UnsupportedPaddingException("Padding %j is not supported in the STRICT padding style.");
                case SMART:
                    if (this.precision < 3) {
                        throw new UnsupportedPaddingException(
                                "Padding %j with width" + this.precision + " is not supported in the SMART padding style.");
                    }
                    formatter.padNext(this.precision, pad);
                    break;
                case LENIENT:
                    // No padding actually.
                    break;
                default:
                    throw new UnsupportedPaddingException();
            }
            return formatter.appendValue(ChronoField.DAY_OF_YEAR);
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(3, this.effectivePadWithDefault('0'));
        }
        return formatter.appendValue(ChronoField.DAY_OF_YEAR);
    }
}

/**
 * {@code %m}
 *
 * <p>Formatting - Replaced by the month as a decimal number [01,12]. [ tm_mon]
 *
 * <p>Parsing - The month number [01,12]; leading zeros shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerM extends ConversionSpecification {
    LowerM(final Context context) {
        super(ConversionType.POSIX_LOWER_M, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 0) {
            final char pad = this.effectivePadWithDefault('0');
            if (pad == '0') {
                return formatter.appendValue(ChronoField.MONTH_OF_YEAR, (this.precision > 2 ? this.precision : 2));
            } else {
                if (this.isLeftAligned()) {
                    if (this.precision >= 2) {
                        formatter.padNext(this.precision, pad);
                    }
                } else {
                    formatter.padNext((this.precision > 2 ? this.precision : 2), pad);
                }
                return formatter.appendValue(ChronoField.MONTH_OF_YEAR);
            }
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(2, this.effectivePadWithDefault('0'));
        }
        return formatter.appendValue(ChronoField.MONTH_OF_YEAR);
    }
}

/**
 * {@code %M}
 *
 * <p>Formatting - Replaced by the minute as a decimal number [00,59]. [ tm_min]
 *
 * <p>Parsing - The minute [00,59]; leading zeros shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperM extends ConversionSpecification {
    UpperM(final Context context) {
        super(ConversionType.POSIX_UPPER_M, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision >= 0) {
            final char pad = this.effectivePadWithDefault('0');
            if (pad == '0') {
                return formatter.appendValue(ChronoField.MINUTE_OF_HOUR, (this.precision > 2 ? this.precision : 2));
            } else {
                if (this.isLeftAligned()) {
                    if (this.precision >= 2) {
                        formatter.padNext(this.precision, pad);
                    }
                } else {
                    formatter.padNext((this.precision > 2 ? this.precision : 2), pad);
                }
                return formatter.appendValue(ChronoField.MINUTE_OF_HOUR);
            }
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(2, this.effectivePadWithDefault('0'));
        }
        return formatter.appendValue(ChronoField.MINUTE_OF_HOUR);
    }
}

/**
 * {@code %p}
 *
 * <p>Formatting - Replaced by the locale's equivalent of either a.m. or p.m. [tm_hour]
 *
 * <p>Parsing - The locale's equivalent of a.m. or p.m.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerP extends ConversionSpecification {
    LowerP(final Context context) {
        super(ConversionType.POSIX_LOWER_P, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter;
    }
}

/**
 * {@code %r}
 *
 * <p>Formatting - Replaced by the time in a.m. and p.m. notation; [CX] [Option Start]  in the POSIX locale this shall be
 * equivalent to {@code %I : %M : %S %p}. [Option End] [tm_hour, tm_min, tm_sec]
 *
 * <p>Parsing - 12-hour clock time using the AM/PM notation if t_fmt_ampm is not an empty string in the LC_TIME portion
 * of the current locale; in the POSIX locale, this shall be equivalent to {@code %I : %M : %S %p}.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerR extends ConversionSpecification {
    LowerR(final Context context) {
        super(ConversionType.POSIX_LOWER_R, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision > 11) {
            // The padding for "%r" is independent from the padding for CLOCK_HOUR_OF_AMPM.
            if (this.effectivePadWithDefault(' ') == '0') {
                formatter.appendLiteral(repeat("0", this.precision - 11));
            } else {
                formatter.appendLiteral(repeat(" ", this.precision - 11));
            }
        }

        return formatter
                .appendValue(ChronoField.CLOCK_HOUR_OF_AMPM, 2)  // The padding for this CLOCK_HOUR_OF_AMPM is always '0'.
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendLiteral(' ')
                .appendText(ChronoField.AMPM_OF_DAY);
    }
}

/**
 * {@code %R}
 *
 * <p>Formatting - Replaced by the time in 24-hour notation ({@code %H : %M}). [tm_hour, tm_min]
 *
 * <p>Parsing - The time as {@code %H : %M}.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperR extends ConversionSpecification {
    UpperR(final Context context) {
        super(ConversionType.POSIX_UPPER_R, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision > 5) {
            // The padding for "%R" is independent from the padding for HOUR_OF_DAY.
            if (this.effectivePadWithDefault(' ') == '0') {
                formatter.appendLiteral(repeat("0", this.precision - 5));
            } else {
                formatter.appendLiteral(repeat(" ", this.precision - 5));
            }
        }

        return formatter
                .appendValue(ChronoField.HOUR_OF_DAY, 2)  // The padding for this HOUR_OF_DAY is always '0'.
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2);
    }
}

/**
 * {@code %S}
 *
 * <p>Formatting - Replaced by the second as a decimal number [00,60]. [tm_sec]
 *
 * <p>Parsing - The seconds [00,60]; leading zeros shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperS extends ConversionSpecification {
    UpperS(final Context context) {
        super(ConversionType.POSIX_UPPER_S, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        final char pad = this.effectivePadWithDefault('0');
        if (this.precision >= 0) {
            if (pad == '0') {
                return formatter.appendValue(ChronoField.SECOND_OF_MINUTE, (this.precision > 2 ? this.precision : 2));
            } else {
                if (this.isLeftAligned()) {
                    if (this.precision >= 2) {
                        formatter.padNext(this.precision, pad);
                    }
                } else {
                    formatter.padNext((this.precision > 2 ? this.precision : 2), pad);
                }
                return formatter.appendValue(ChronoField.SECOND_OF_MINUTE);
            }
        }

        if (!this.isLeftAligned()) {
            formatter.padNext(2, pad);
        }
        return formatter.appendValue(ChronoField.SECOND_OF_MINUTE);
    }
}

/**
 * {@code %T}
 *
 * <p>Formatting - Replaced by the time ({@code %H : %M : %S}). [tm_hour, tm_min, tm_sec]
 *
 * <p>Parsing - The time as {@code %H : %M : %S}.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperT extends ConversionSpecification {
    UpperT(final Context context) {
        super(ConversionType.POSIX_UPPER_T, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision > 8) {
            // The padding for "%T" is independent from the padding for HOUR_OF_DAY.
            if (this.effectivePadWithDefault(' ') == '0') {
                formatter.appendLiteral(repeat("0", this.precision - 8));
            } else {
                formatter.appendLiteral(repeat(" ", this.precision - 8));
            }
        }

        return formatter
                .appendValue(ChronoField.HOUR_OF_DAY, 2)  // The padding for this HOUR_OF_DAY is always '0'.
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2);
    }
}

/**
 * {@code %u}
 *
 * Formatter only.
 *
 * <p>Formatting - Replaced by the weekday as a decimal number [1,7], with 1 representing Monday. [tm_wday]
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerU extends ConversionSpecification {
    LowerU(final Context context) {
        super(ConversionType.POSIX_LOWER_U, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        if (this.precision > 1) {
            final char pad = this.effectivePadWithDefault('0');
            if (pad == '0') {
                return formatter.appendValue(ChronoField.DAY_OF_WEEK, this.precision);
            } else {
                formatter.padNext(this.precision, pad);
                return formatter.appendValue(ChronoField.DAY_OF_WEEK);
            }
        }

        return formatter.appendValue(ChronoField.DAY_OF_WEEK);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class WeekOfYearStartingWithSunday implements TemporalField {
    private WeekOfYearStartingWithSunday() {
        // No instantiation.
    }

    @Override
    public String getDisplayName(final Locale locale) {
        return "WeekOfYearStartingWithSunday";
    }

    @Override
    public TemporalUnit getBaseUnit() {
        return ChronoUnit.WEEKS;
    }

    @Override
    public TemporalUnit getRangeUnit() {
        return ChronoUnit.YEARS;
    }

    @Override
    public ValueRange range() {
        return ValueRange.of(0, 53);
    }

    @Override
    public boolean isDateBased() {
        return true;
    }

    @Override
    public boolean isTimeBased() {
        return false;
    }

    @Override
    public boolean isSupportedBy(final TemporalAccessor temporal) {
        return temporal.isSupported(ChronoField.DAY_OF_YEAR) && temporal.isSupported(ChronoField.YEAR);
    }

    @Override
    public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
        return range();
    }

    @Override
    public long getFrom(final TemporalAccessor temporal) {
        final int dayOfYear = temporal.get(ChronoField.DAY_OF_YEAR);
        final int year = temporal.get(ChronoField.YEAR);

        final LocalDate firstOfYear = LocalDate.of(year, 1, 1);
        final DayOfWeek firstDayOfWeek = firstOfYear.getDayOfWeek();

        final int daysToFirstSunday = (7 - firstDayOfWeek.getValue()) % 7;
        final int firstSundayDayOfYear = 1 + daysToFirstSunday;

        if (dayOfYear < firstSundayDayOfYear) {
            return 0;
        } else {
            return ((dayOfYear - firstSundayDayOfYear) / 7) + 1;
        }
    }

    @Override
    public <R extends Temporal> R adjustInto(final R temporal, final long newValue) {
        throw new UnsupportedTemporalTypeException("Cannot adjust " + this);
    }

    static final TemporalField FIELD = new WeekOfYearStartingWithSunday();
}

/**
 * {@code %U}
 *
 * <p>Formatting - Replaced by the week number of the year as a decimal number [00,53]. The first Sunday of January is the
 * first day of week 1; days in the new year before this are in week 0. [ tm_year, tm_wday, tm_yday]
 *
 * <p>Parsing - The week number of the year (Sunday as the first day of the week) as a decimal number [00,53]; leading zeros
 * shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperU extends ConversionSpecification {
    UpperU(final Context context) {
        super(ConversionType.POSIX_UPPER_U, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        final char pad = this.effectivePadWithDefault('0');
        if (this.precision > 1) {
            if (pad == '0') {
                return formatter.appendValue(WeekOfYearStartingWithSunday.FIELD, this.precision);
            } else {
                formatter.padNext(this.precision, pad);
                return formatter.appendValue(WeekOfYearStartingWithSunday.FIELD);
            }
        }

        if (this.isLeftAligned()) {
            return formatter.appendValue(WeekOfYearStartingWithSunday.FIELD);
        } else {
            if (pad == '0') {
                return formatter.appendValue(WeekOfYearStartingWithSunday.FIELD, 2);
            } else {
                formatter.padNext(2, pad);
                return formatter.appendValue(WeekOfYearStartingWithSunday.FIELD);
            }
        }
    }
}

/**
 * {@code %V}
 *
 * Formatter only.
 *
 * <p>Formatting - Replaced by the week number of the year (Monday as the first day of the week) as a decimal number [01,53].
 * If the week containing 1 January has four or more days in the new year, then it is considered week 1. Otherwise, it is the
 * last week of the previous year, and the next week is week 1. Both January 4th and the first Thursday of January are always
 * in week 1. [ tm_year, tm_wday, tm_yday]
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperV extends ConversionSpecification {
    UpperV(final Context context) {
        super(ConversionType.POSIX_UPPER_V, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        final char pad = this.effectivePadWithDefault('0');
        if (this.precision > 1) {
            if (pad == '0') {
                return formatter.appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, this.precision);
            } else {
                formatter.padNext(this.precision, pad);
                return formatter.appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            }
        }

        if (this.isLeftAligned()) {
            return formatter.appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        } else {
            if (pad == '0') {
                return formatter.appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2);
            } else {
                formatter.padNext(2, pad);
                return formatter.appendValue(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            }
        }
    }
}

/**
 * {@code %w}
 *
 * <p>Formatting - Replaced by the weekday as a decimal number [0,6], with 0 representing Sunday. [tm_wday]
 *
 * <p>Parsing - The weekday as a decimal number [0,6], with 0 representing Sunday.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerW extends ConversionSpecification {
    LowerW(final Context context) {
        super(ConversionType.POSIX_LOWER_W, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter;
    }
}

/**
 * {@code %W}
 *
 * <p>Formatting - Replaced by the week number of the year as a decimal number [00,53]. The first Monday of January is
 * the first day of week 1; days in the new year before this are in week 0. [ tm_year, tm_wday, tm_yday]
 *
 * <p>Parsing - The week number of the year (Monday as the first day of the week) as a decimal number [00,53]; leading
 * zeros shall be permitted but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperW extends ConversionSpecification {
    UpperW(final Context context) {
        super(ConversionType.POSIX_UPPER_W, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter;
    }
}

/**
 * {@code %x}
 *
 * <p>Formatting - Replaced by the locale's appropriate date representation. (See the Base Definitions volume of POSIX.1-2017,
 * {@code <time.h>}.)
 *
 * <p>Parsing - The date, using the locale's date format.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerX extends ConversionSpecification {
    LowerX(final Context context) {
        super(ConversionType.POSIX_LOWER_X, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter;
    }
}

/**
 * {@code %X}
 *
 * <p>Formatting - Replaced by the locale's appropriate time representation. (See the Base Definitions volume of POSIX.1-2017,
 * {@code <time.h>}.)
 *
 * <p>Parsing - The time, using the locale's time format.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperX extends ConversionSpecification {
    UpperX(final Context context) {
        super(ConversionType.POSIX_UPPER_X, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter;
    }
}

/**
 * {@code %y}
 *
 * <p>Formatting - Replaced by the last two digits of the year as a decimal number [00,99]. [tm_year]
 *
 * <p>Parsing - The last two digits of the year. When format contains neither a C conversion specifier nor a Y conversion
 * specifier, values in the range [69,99] shall refer to years 1969 to 1999 inclusive and values in the range [00,68] shall
 * refer to years 2000 to 2068 inclusive; leading zeros shall be permitted but shall not be required. A leading '+' or '-'
 * character shall be permitted before any leading zeros but shall not be required.
 *
 * Note:
 * It is expected that in a future version of this standard the default century inferred from a 2-digit year will change.
 * (This would apply to all commands accepting a 2-digit year as input.)
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerY extends ConversionSpecification {
    LowerY(final Context context) {
        super(ConversionType.POSIX_LOWER_Y, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter.appendValue(PosixFields.YEAR_OF_POSIX_CENTURY_1969_2068, 2);
    }
}

/**
 * {@code %Y}
 *
 * <p>Formatting - Replaced by the year as a decimal number (for example, 1997). [tm_year]
 *
 * [CX] [Option Start] If a minimum field width is specified, the number of characters placed into the array pointed to
 * by s will be the number of digits and leading sign characters (if any) in the year, or the minimum field width,
 * whichever is greater. [Option End]
 *
 * <p>Parsing - The full year {4}; leading zeros shall be permitted but shall not be required. A leading '+' or '-'
 * character shall be permitted before any leading zeros but shall not be required.
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperY extends ConversionSpecification {
    UpperY(final Context context) {
        super(ConversionType.POSIX_UPPER_Y, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        final char pad = this.effectivePadWithDefault('0');
        if (this.precision >= 0) {
            if (pad == '0') {
                // When padding with '0', ranged padding works.
                return formatter.appendValue(ChronoField.YEAR, Math.max(this.precision, 1), 19, SignStyle.NORMAL);
            }
        }

        if (this.precision > 0) {
            // When padding with ' ', ranged padding does not work, therefore strftime cannot be emulated 100%.
            //
            // For example, strftime formats 10 into:
            //   * into "10" by "%_2Y"
            //   * into " 10" by "%_3Y"
            //   * into "  10" by "%_4Y".
            //
            // At the same time, strftime formats 1000 into:
            //   * into "1000" by "%_2Y"
            //   * into "1000" by "%_3Y"
            //   * into "1000" by "%_4Y".
            //
            // This flexibility of ranged padding cannot be realized in java.time.format.DateTimeFormatterBuilder.
            switch (paddingStyle) {
                case STRICT:
                    throw new UnsupportedPaddingException("Padding %Y is not supported in the STRICT padding style.");
                case SMART:
                    if (this.precision < 4) {
                        throw new UnsupportedPaddingException(
                                "Padding %Y with width" + this.precision + " is not supported in the SMART padding style.");
                    }
                    formatter.padNext(this.precision, pad);
                    break;
                case LENIENT:
                    // No padding actually.
                    break;
                default:
                    throw new UnsupportedPaddingException();
            }
        }
        return formatter.appendValue(ChronoField.YEAR);
    }
}

/**
 * {@code %z}
 *
 * Formatter only.
 *
 * <p>Formatting - Replaced by the offset from UTC in the ISO 8601:2000 standard format ( +hhmm or -hhmm ), or by no
 * characters if no timezone is determinable. For example, "-0430" means 4 hours 30 minutes behind UTC (west of Greenwich).
 * [CX] [Option Start]  If tm_isdst is zero, the standard time offset is used. If tm_isdst is greater than zero, the daylight
 * savings time offset is used. If tm_isdst is negative, no characters are returned. [Option End] [ tm_isdst]
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerZ extends ConversionSpecification {
    LowerZ(final Context context) {
        super(ConversionType.POSIX_LOWER_Z, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter;
    }
}

/**
 * {@code %Z}
 *
 * Formatter only.
 *
 * <p>Formatting - Replaced by the timezone name or abbreviation, or by no bytes if no timezone information exists. [tm_isdst]
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperZ extends ConversionSpecification {
    UpperZ(final Context context) {
        super(ConversionType.POSIX_UPPER_Z, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter;
    }
}

/*
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class Plus extends ConversionSpecification {
    Plus(final Context context) {
        super(null, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(
            final DateTimeFormatterBuilder formatter,
            final PaddingStyle paddingStyle,
            final Optional<Locale> locale) {
        return formatter;
    }
}
*/
