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

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class PosixTimeFormat {
    private PosixTimeFormat(final List<Specification> formatSpecifications) {
        this.formatSpecifications = formatSpecifications;
    }

    public enum Option {
        GNU_EXTENSION,

        /**
         * Recognizes the plus-sign character ({@code '+'}) as a terminating conversion specifier ({@code "%+"})
         * to represent date and time as produced by {@code date(1)}.
         *
         * <p>Otherwise, if this option is not specified, the plus-sign character ({@code '+'}) is recognized as
         * an optional flag to specify that the character used as the padding character is {@code '0'}, and that
         * if and only if the field being produced consumes more than four bytes to represent a year (for
         * {@code %F}, {@code %G}, or {@code %Y}) or more than two bytes to represent the year divided by 100
         * (for {@code %C}) then a leading plus-sign character shall be included if the year being processed is
         * greater than or equal to zero or a leading hyphen-minus character ({@code '-'}) shall be included if
         * the year is less than zero.
         *
         * <p>Recent standards and implementations adopts the option to recognize the plus-sign character as an
         * optional flag, such as POSIX (The Open Group Base Specifications Issue 7), and glibc2.
         *
         * <p>The other option to recognize the plus-sign character as a terminating conversion specifier is
         * considered old. But, some processors still adopts the option, such as Ruby's {@code Date.strftime}.
         *
         * @see <a href="https://docs.oracle.com/cd/E88353_01/html/E37843/strftime-3c.html">strftime(3C) - man pages section 3: Basic Library Functions - Oracle Solaris 11.4 Reference Library</a>
         * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime, The Open Group Base Specifications Issue 7, 2018 edition</a>
         * @see <a href="https://docs.ruby-lang.org/en/3.2/strftime_formatting_rdoc.html#label-Conversion+Specifiers">Conversion Specifiers, Formats for Dates and Times, Documentation for Ruby</a>
         */
        PLUS_SIGN_AS_TERMINATING_CONVERSION_SPECIFIER,

        /**
         * GNU extension.
         */
        UPPERCASE_F_FOR_PARSING,

        /**
         * GNU extension.
         */
        LOWERCASE_G_FOR_PARSING,

        /**
         * GNU extension.
         */
        UPPERCASE_G_FOR_PARSING,

        /**
         * GNU extension.
         */
        LOWERCASE_K_AS_TERMINATING_CONVERSION_SPECIFIER,

        /**
         * GNU extension.
         */
        LOWERCASE_L_AS_TERMINATING_CONVERSION_SPECIFIER,

        /**
         * GNU extension.
         */
        LOWERCASE_S_AS_TERMINATING_CONVERSION_SPECIFIER,

        /**
         * GNU extension.
         */
        LOWERCASE_U_FOR_PARSING,

        /**
         * GNU extension.
         */
        LOWERCASE_V_AS_TERMINATING_CONVERSION_SPECIFIER,

        /**
         * GNU extension.
         */
        UPPERCASE_V_FOR_PARSING,

        /**
         * GNU extension.
         */
        LOWERCASE_Z_FOR_PARSING,

        /**
         * GNU extension.
         */
        UPPERCASE_Z_FOR_PARSING,

        /**
         * Ruby extension.
         */
        UPPERCASE_L_AS_TERMINATING_CONVERSION_SPECIFIER,

        /**
         * Ruby extension.
         */
        UPPERCASE_N_AS_TERMINATING_CONVERSION_SPECIFIER,
        ;
    }

    public static PosixTimeFormat compile(final String format, final Option... options) {
        return new PosixTimeFormat(Tokenizer.tokenize(format, options));
    }

    public DateTimeFormatter toDateTimeFormatter() {
        return this.toDateTimeFormatter(PaddingStyle.SMART);
    }

    public DateTimeFormatter toDateTimeFormatter(final PaddingStyle paddingStyle) {
        return this.toDateTimeFormatter(Optional.empty(), paddingStyle);
    }

    public DateTimeFormatter toDateTimeFormatter(final Optional<Locale> locale, final PaddingStyle paddingStyle) {
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        for (final Specification specification : this.formatSpecifications) {
            specification.appendTo(builder, paddingStyle, locale);
        }
        return builder.toFormatter(locale.orElse(Locale.ROOT));
    }

    private final List<Specification> formatSpecifications;
}
