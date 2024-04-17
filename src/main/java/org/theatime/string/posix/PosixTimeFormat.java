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

package org.theatime.string.posix;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

public final class PosixTimeFormat {
    private PosixTimeFormat(final List<AbstractSpecification> formatSpecifications) {
        this.formatSpecifications = formatSpecifications;
    }

    public enum Option {
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

        UPPERCASE_L_AS_TERMINATING_CONVERSION_SPECIFIER,

        UPPERCASE_N_AS_TERMINATING_CONVERSION_SPECIFIER,
        ;
    }

    public static PosixTimeFormat compile(final String format, final Option... options) {
        return new PosixTimeFormat(Tokenizer.tokenize(format, options));
    }

    public DateTimeFormatter toDateTimeFormatter() {
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        for (final AbstractSpecification specification : this.formatSpecifications) {
            specification.appendTo(builder);
        }
        return builder.toFormatter(Locale.ROOT);
    }

    private final List<AbstractSpecification> formatSpecifications;
}
