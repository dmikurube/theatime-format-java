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

import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Objects;

/**
 * Represents a conversion specification, or ordinary characters, in POSIX {@code strftime} and {@code strptime} formats.
 *
 * <p>For example, conversion specifications such as {@code "%A"}, {@code "%-h"}, {@code "%04Y"}, or
 * ordinary characters such as {@code "T"}, {@code ":"}, {@code "hour"}, or {@code "時"}.
 *
 * <p>It assumes that it is running is in the "C" or "POSIX" locale.
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/009695399/functions/strftime.html">strftime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/007904875/functions/strptime.html">strptime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strptime.html">strptime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 */
final class PosixTimeFormatSpecification {
    private PosixTimeFormatSpecification(
            final PosixTimeFormatConversionType type,
            final boolean upperCase,
            final boolean changeCase,
            final int precision,
            final int colons,
            final char padding,
            final char modifier,
            final boolean isAvailableForFormatting,
            final boolean isAvailableForParsing,
            final String original) {
        if (type == null) {
            throw new NullPointerException("PosixTimeFormatConversionType is null");
        }
        if ((!isAvailableForFormatting) && (!isAvailableForParsing)) {
            throw new IllegalArgumentException("Unavailable neither for formatting nor parsing.");
        }

        this.type = type;

        this.upperCase = upperCase;
        this.changeCase = changeCase;
        this.precision = precision;
        this.colons = colons;
        this.padding = padding;
        this.modifier = modifier;
        this.isAvailableForFormatting = isAvailableForFormatting;
        this.isAvailableForParsing = isAvailableForParsing;

        this.original = original;
    }

    static PosixTimeFormatSpecification ordinaryCharacters(final String ordinaryCharacters) {
        return new PosixTimeFormatSpecification(
                PosixTimeFormatConversionType.LITERAL,  // type
                false,  // upperCase
                false,  // changeCase
                -1,  // precision
                -1,  // colons
                '\0',  // padding
                '\0',  // modifier
                true,  // isAvailableForFormatting
                true,  // isAvailableForParsing
                ordinaryCharacters);  // original
    }

    static PosixTimeFormatSpecification conversion(
            final PosixTimeFormatConversionType type,
            final boolean upperCase,
            final boolean changeCase,
            final int precision,
            final int colons,
            final char padding,
            final char modifier,
            final String original) {
        return new PosixTimeFormatSpecification(
                type,  // type
                upperCase,  // upperCase
                changeCase,  // changeCase
                precision,  // precision
                colons,  // colons
                padding,  // padding
                modifier,  // modifier
                true,  // isAvailableForFormatting
                true,  // isAvailableForParsing
                original);  // original
    }

    static class ConversionBuilder {
        ConversionBuilder() {
            this.upperCase = false;
            this.changeCase = false;
            this.precision = -1;
            this.colons = -1;
            this.padding = '\0';
            this.modifier = '\0';
            this.isAvailableForFormatting = true;
            this.isAvailableForParsing = true;
        }

        ConversionBuilder upperCase() {
            this.upperCase = true;
            return this;
        }

        ConversionBuilder changeCase() {
            this.changeCase = true;
            return this;
        }

        ConversionBuilder precision(final int precision) {
            this.precision = precision;
            return this;
        }

        ConversionBuilder colons(final int colons) {
            this.colons = colons;
            return this;
        }

        ConversionBuilder padding(final char padding) {
            this.padding = padding;
            return this;
        }

        ConversionBuilder modifier(final char modifier) {
            this.modifier = modifier;
            return this;
        }

        ConversionBuilder unavailableForFormatting() {
            this.isAvailableForFormatting = false;
            return this;
        }

        ConversionBuilder unavailableForParsing() {
            this.isAvailableForParsing = false;
            return this;
        }

        PosixTimeFormatSpecification build(
                final PosixTimeFormatConversionType type,
                final char paddingIfUnspecified,
                final String original) {
            return new PosixTimeFormatSpecification(
                    type,
                    this.upperCase,
                    this.changeCase,
                    this.precision,
                    this.colons,
                    this.padding == '\0' ? paddingIfUnspecified : this.padding,
                    this.modifier,
                    this.isAvailableForFormatting,
                    this.isAvailableForParsing,
                    original);
        }

        private boolean upperCase;
        private boolean changeCase;
        private int precision;
        private int colons;
        private char padding;
        private char modifier;
        private boolean isAvailableForFormatting;
        private boolean isAvailableForParsing;
    }

    boolean appendToDateTimeFormatterBuilder(final DateTimeFormatterBuilder formatterBuilder) {
        if ((!this.isAvailableForFormatting) || (!this.isAvailableForParsing)) {
            // java.time.DateTimeFormatter must be available both for formatting and parsing.
            return false;
        }

        switch (this.type) {
            case LITERAL:
                formatterBuilder.appendLiteral(this.original);
                return true;
            case DAY_OF_WEEK_TEXT_SHORT:
                if (this.padding == '0' || this.upperCase || this.changeCase) {
                    return false;
                }
                if (this.precision >= 0) {
                    formatterBuilder.padNext(this.precision, this.padding);
                }
                formatterBuilder.appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT);
                return true;
            case DAY_OF_WEEK_TEXT_FULL:
                if (this.padding == '0' || this.upperCase || this.changeCase) {
                    return false;
                }
                if (this.precision >= 0) {
                    formatterBuilder.padNext(this.precision, this.padding);
                }
                formatterBuilder.appendText(ChronoField.DAY_OF_WEEK, TextStyle.FULL);
                return true;
            case MONTH_OF_YEAR_TEXT_SHORT:
                if (this.padding == '0' || this.upperCase || this.changeCase) {
                    return false;
                }
                if (this.precision >= 0) {
                    formatterBuilder.padNext(this.precision, this.padding);
                }
                formatterBuilder.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT);
                return true;
            case MONTH_OF_YEAR_TEXT_FULL:
                if (this.padding == '0' || this.upperCase || this.changeCase) {
                    return false;
                }
                if (this.precision >= 0) {
                    formatterBuilder.padNext(this.precision, this.padding);
                }
                formatterBuilder.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL);
                return true;
            case COMPOSITE_LOCAL_DATE_TIME:
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
                if (this.padding == '0' || this.upperCase || this.changeCase) {
                    return false;
                }
                formatterBuilder.appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT);
                formatterBuilder.appendLiteral(" ");
                formatterBuilder.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT);
                formatterBuilder.appendLiteral(" ");
                formatterBuilder.padNext(2, ' ');
                formatterBuilder.appendValue(ChronoField.DAY_OF_MONTH);
                formatterBuilder.appendLiteral(" ");
                formatterBuilder.appendValue(ChronoField.HOUR_OF_DAY, 2);
                formatterBuilder.appendLiteral(":");
                formatterBuilder.appendValue(ChronoField.MINUTE_OF_HOUR, 2);
                formatterBuilder.appendLiteral(":");
                formatterBuilder.appendValue(ChronoField.SECOND_OF_MINUTE, 2);
                formatterBuilder.appendLiteral(" ");
                formatterBuilder.appendValue(ChronoField.YEAR, 4, 19, SignStyle.NORMAL);
                return true;
            // case 99, 00, 01:
            //     uu      2      appendValueReduced(ChronoField.YEAR, 2, 2000);
            default:
                break;
        }
        return false;
    }

    boolean isAvailableForFormatting() {
        return this.isAvailableForFormatting;
    }

    boolean isAvailableForParsing() {
        return this.isAvailableForParsing;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof PosixTimeFormatSpecification)) {
            return false;
        }

        final PosixTimeFormatSpecification other = (PosixTimeFormatSpecification) otherObject;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.upperCase, other.upperCase)
                && Objects.equals(this.changeCase, other.changeCase)
                && Objects.equals(this.precision, other.precision)
                && Objects.equals(this.colons, other.colons)
                && Objects.equals(this.padding, other.padding)
                && Objects.equals(this.modifier, other.modifier)
                && Objects.equals(this.isAvailableForFormatting, other.isAvailableForFormatting)
                && Objects.equals(this.isAvailableForParsing, other.isAvailableForParsing)
                && Objects.equals(this.original, other.original);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.type,
                this.upperCase,
                this.changeCase,
                this.precision,
                this.colons,
                this.padding,
                this.modifier,
                this.isAvailableForFormatting,
                this.isAvailableForParsing,
                this.original);
    }

    @Override
    public String toString() {
        if (this.type == PosixTimeFormatConversionType.LITERAL) {
            return this.original;
        }

        final StringBuilder builder = new StringBuilder().append("<").append(this.original);
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
        if (this.padding != '\0') {
            builder.append(":padding=").append(this.padding);
        }
        if (this.modifier != '\0') {
            builder.append(":modifier=").append(this.modifier);
        }
        if ((!this.isAvailableForFormatting) || (!isAvailableForParsing)) {
            if (this.isAvailableForFormatting) {
                builder.append(":only-for-formatting");
            } else if (this.isAvailableForFormatting) {
                builder.append(":only-for-parsing");
            } else {
                builder.append(":UNEXPECTED-NEITHER-FOR-FORMATTING-NOR-PARSING");
            }
        }
        return builder.append(">").toString();
    }

    private final PosixTimeFormatConversionType type;
    private final boolean upperCase;
    private final boolean changeCase;
    private final int precision;
    private final int colons;
    private final char padding;
    private final char modifier;
    private final boolean isAvailableForFormatting;
    private final boolean isAvailableForParsing;

    private final String original;
}
