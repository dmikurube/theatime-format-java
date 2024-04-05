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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tokenizes a POSIX {@code strftime}/{@code strptime}-like format string into dissolved specifications.
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/009695399/functions/strftime.html">strftime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/007904875/functions/strptime.html">strptime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strptime.html">strptime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 */
final class Tokenizer {
    private Tokenizer(final String format, final PosixTimeFormatOptions options) {
        this.format = format;
        this.options = options;

        this.pos = 0;
        this.formatSpecifications = null;
    }

    /**
     * "Tokenizes" the POSIX {@code strftime}/{@code strptime} format into conversion specifications.
     *
     * <p>Note that it does not take care of semantics. The semantics includes a flexible witdh, for example.
     * For more specific example, Ruby's extended {@code strptime} accepts an year with longer than 4 digits
     * when the <strong>next</strong> specification is <strong>not</strong> a number pattern.
     */
    static List<Specification> tokenize(final String format, final PosixTimeFormatOption... options) {
        return new Tokenizer(format, PosixTimeFormatOptions.of(options)).tokenizeInitial();
    }

    private List<Specification> tokenizeInitial() {
        int firstOrdinaryCharacter = 0;

        this.pos = 0;
        this.formatSpecifications = new ArrayList<>();

        while (true) {
            final int posPercent = this.format.indexOf('%', this.pos);

            if (posPercent < 0) {
                if (firstOrdinaryCharacter < this.format.length()) {
                    this.formatSpecifications.add(Specification.ordinaryCharacters(
                            this.format.substring(firstOrdinaryCharacter)));
                }
                break;
            }

            this.pos = posPercent;
            final Specification formatSpecification = this.tokenizeConversion();
            if (formatSpecification != null) {
                if (firstOrdinaryCharacter < posPercent) {
                    this.formatSpecifications.add(Specification.ordinaryCharacters(
                            this.format.substring(firstOrdinaryCharacter, posPercent)));
                }
                this.formatSpecifications.add(formatSpecification);
                firstOrdinaryCharacter = this.pos;
            }
        }

        return Collections.unmodifiableList(this.formatSpecifications);
    }

    /**
     * Tokenizes {@code format} from {@code this.pos} as a conversion specification.
     *
     * <p>It expects {@code this.pos} points {@code '%'} of the target conversion specification. Otherwise,
     * it throws {@link AssertionError}.
     *
     * <p>If it recognizes a format specifier, it moves {@code this.pos} forward next to the terminating conversion
     * specifier.
     *
     * @return {@link Specification} instance if recognized as a format specification, or {@code null} otherwise.
     * @throws AssertionError  if {@code this.pos} does not point {@code '%'}
     */
    @SuppressWarnings({"checkstyle:FallThrough", "checkstyle:LeftCurly"})
    private Specification tokenizeConversion() {
        assert this.format.charAt(this.pos) == '%';
        final int posPercent = this.pos;
        this.pos++;

        final Specification.ConversionBuilder builder = new Specification.ConversionBuilder();
        boolean hasPrecisionProcessed = false;

        for (; this.pos < this.format.length(); this.pos++) {
            final char ch = this.format.charAt(this.pos);

            switch (ch) {
                // The immediate conversions are tokenized still as conversion specifications
                // so that options can work for formatting. For example,
                //
                //    irb(main):001:0> Time.now.strftime("%10%")
                //    => "         %"
                //    irb(main):002:0> Time.now.strftime("%010n")
                //    => "000000000\n"
                case '%':  // ConversionType.IMMEDIATE_PERCENT
                case 'n':  // ConversionType.IMMEDIATE_WHITESPACE_NEWLINE
                case 't':  // ConversionType.IMMEDIATE_WHITESPACE_TAB

                case 'a':  // FormatDirective.DAY_OF_WEEK_ABBREVIATED_NAME
                case 'A':  // FormatDirective.DAY_OF_WEEK_FULL_NAME
                case 'h':  // FormatDirective.MONTH_OF_YEAR_ABBREVIATED_NAME_ALIAS_SMALL_H
                case 'b':  // FormatDirective.MONTH_OF_YEAR_ABBREVIATED_NAME
                case 'B':  // FormatDirective.MONTH_OF_YEAR_FULL_NAME

                case 'P':  // FormatDirective.AMPM_OF_DAY_LOWER_CASE
                case 'p':  // FormatDirective.AMPM_OF_DAY_UPPER_CASE

                case 'c':  // FormatDirective.RECURRED_UPPER_C
                case 'x':  // FormatDirective.RECURRED_LOWER_X
                case 'X':  // FormatDirective.RECURRED_UPPER_X
                case 'D':  // FormatDirective.RECURRED_UPPER_D
                case 'r':  // FormatDirective.RECURRED_LOWER_R
                case 'R':  // FormatDirective.RECURRED_UPPER_R
                case 'T':  // FormatDirective.RECURRED_UPPER_T
                case 'F':  // FormatDirective.RECURRED_UPPER_F

                case 'd':  // FormatDirective.DAY_OF_MONTH_ZERO_PADDED
                case 'H':  // FormatDirective.HOUR_OF_DAY_ZERO_PADDED
                case 'I':  // FormatDirective.HOUR_OF_AMPM_ZERO_PADDED
                case 'j':  // FormatDirective.DAY_OF_YEAR
                case 'm':  // FormatDirective.MONTH_OF_YEAR
                case 'M':  // FormatDirective.MINUTE_OF_HOUR
                case 'S':  // FormatDirective.SECOND_OF_MINUTE
                case 'U':  // FormatDirective.WEEK_OF_YEAR_STARTING_WITH_SUNDAY
                case 'W':  // FormatDirective.WEEK_OF_YEAR_STARTING_WITH_MONDAY
                case 'w':  // FormatDirective.DAY_OF_WEEK_STARTING_WITH_SUNDAY_0
                case 'y':  // FormatDirective.YEAR_WITHOUT_CENTURY
                case 'Y':  // FormatDirective.YEAR_WITH_CENTURY
                case 'e':  // FormatDirective.DAY_OF_MONTH_BLANK_PADDED
                case 'C':  // FormatDirective.CENTURY -- FMTV
                case 'V':  // FormatDirective.WEEK_OF_WEEK_BASED_YEAR
                case 'u':  // FormatDirective.DAY_OF_WEEK_STARTING_WITH_MONDAY_1

                case 'Q':  // FormatDirective.MILLISECONDS_SINCE_EPOCH
                        // %Q is only for parsing, not for formatting. Then, %Q never takes any option.
                        // So, a token of "%Q" can always be stringified straightforward to "%Q".

                case 'z':  // FormatDirective.TIME_OFFSET
                case 'Z':  // FormatDirective.TIME_ZONE_NAME

                case 'G':  // FormatDirective.WEEK_BASED_YEAR_WITH_CENTURY
                case 'g':  // FormatDirective.WEEK_BASED_YEAR_WITHOUT_CENTURY

                // GNU extensions.
                case 'k':  // FormatDirective.HOUR_OF_DAY_BLANK_PADDED
                case 'l':  // FormatDirective.HOUR_OF_AMPM_BLANK_PADDED
                case 's':  // FormatDirective.SECONDS_SINCE_EPOCH
                case 'v':  // FormatDirective.RECURRED_LOWER_V

                    this.pos++;
                    return builder.build(ch, this.format.substring(posPercent, this.pos));

                case 'L':  // Ruby extension: "%L"
                    this.pos++;
                    if (this.options.isUpperCaseLAsTerminatingConversionSpecifier()) {
                        return builder.build(ch, this.format.substring(posPercent, this.pos));
                    }
                    return null;

                case 'N':  // Ruby extension: "%N"
                    this.pos++;
                    if (this.options.isUpperCaseNAsTerminatingConversionSpecifier()) {
                        return builder.build(ch, this.format.substring(posPercent, this.pos));
                    }
                    return null;

                case '+':
                    // Legacy strftime recognizes "%+" as a terminating conversion specifier for date and time (date(1)).
                    if (this.options.isPlusSignAsTerminatingConversionSpecifier()) {
                        this.pos++;
                        return builder.build(ch, this.format.substring(posPercent, this.pos));
                    }

                    // Modern strftime recognizes "%+" as an optional flag.
                    if (hasPrecisionProcessed) {
                        this.pos++;
                        return null;
                    }
                    // TODO: Set '+' flag.
                    builder.padding('0');
                    break;

                case '-':
                    if (hasPrecisionProcessed) {
                        this.pos++;
                        return null;
                    }
                    // optionsBuilder.setLeft();
                    break;

                case '^':
                    if (hasPrecisionProcessed) {
                        this.pos++;
                        return null;
                    }
                    builder.upperCase();
                    break;

                case '#':
                    if (hasPrecisionProcessed) {
                        this.pos++;
                        return null;
                    }
                    builder.changeCase();
                    break;

                case '_':
                    if (hasPrecisionProcessed) {
                        this.pos++;
                        return null;
                    }
                    builder.padding(' ');
                    break;

                case ':':
                    // strptime accepts only 3 colons at maximum.
                    // strftime accepts unlimited number of colons.
                    for (int j = 1; ; j++) {
                        if (this.pos + j >= this.format.length()) {
                            this.pos++;
                            return null;
                        }
                        if (this.format.charAt(this.pos + j) == 'z') {
                            builder.colons(j);
                            this.pos += (j - 1);
                            break;
                        }
                        if (this.format.charAt(this.pos + j) != ':') {
                            this.pos++;
                            return null;
                        }
                    }
                    break;

                case 'E':
                    if (this.pos + 1 < this.format.length() && "cCxXyY".indexOf(this.format.charAt(this.pos + 1)) >= 0) {
                        builder.modifier('E');
                        break;
                    } else {
                        this.pos++;
                        return null;
                    }

                case 'O':
                    if (this.pos + 1 < this.format.length() && "deHImMSuUVwWy".indexOf(this.format.charAt(this.pos + 1)) >= 0) {
                        builder.modifier('O');
                        break;
                    } else {
                        this.pos++;
                        return null;
                    }

                case '0':
                    builder.padding('0');
                    // Pass-through.

                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                        final String digits = this.tokenizeDigitsForIntValue();
                        if (digits == null) {
                            this.pos++;
                            return null;
                        }
                        hasPrecisionProcessed = true;
                        builder.precision(Integer.parseInt(digits));
                        this.pos += digits.length() - 1;
                    }
                    break;

                default:
                    this.pos++;
                    return null;
            }
        }
        return null;
    }

    private String tokenizeDigitsForIntValue() {
        final int digitsBeginning = this.pos;

        int firstNonZero = this.pos;
        for (; firstNonZero < this.format.length() && this.format.charAt(firstNonZero) == '0'; firstNonZero++) {
        }

        int lastDigit = firstNonZero;
        for (; lastDigit < this.format.length() && isDigit(this.format.charAt(lastDigit)); lastDigit++) {
        }

        if (lastDigit - firstNonZero > 10) {
            return null;
        }

        final String digits = this.format.substring(firstNonZero, lastDigit);
        if (lastDigit > firstNonZero) {
            final long tokenizedLong = Long.parseLong(digits);
            if (tokenizedLong > Integer.MAX_VALUE) {
                return null;
            }
        }

        return this.format.substring(digitsBeginning, lastDigit);
    }

    private static boolean isDigit(final char ch) {
        return '0' <= ch && ch <= '9';
    }

    private final String format;
    private final PosixTimeFormatOptions options;

    private int pos;
    private List<Specification> formatSpecifications;
}
