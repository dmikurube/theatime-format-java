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
final class PosixTimeFormatTokenizer {
    private PosixTimeFormatTokenizer(final String format, final PosixTimeFormatOptions options) {
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
    static List<PosixTimeFormatSpecification> tokenize(final String format, final PosixTimeFormatOption... options) {
        return new PosixTimeFormatTokenizer(format, PosixTimeFormatOptions.of(options)).tokenizeInitial();
    }

    private List<PosixTimeFormatSpecification> tokenizeInitial() {
        int firstOrdinaryCharacter = 0;

        this.pos = 0;
        this.formatSpecifications = new ArrayList<>();

        while (true) {
            final int posPercent = this.format.indexOf('%', this.pos);

            if (posPercent < 0) {
                if (firstOrdinaryCharacter < this.format.length()) {
                    this.formatSpecifications.add(PosixTimeFormatSpecification.ordinaryCharacters(
                            this.format.substring(firstOrdinaryCharacter)));
                }
                break;
            }

            this.pos = posPercent;
            final PosixTimeFormatSpecification formatSpecification = this.tokenizeConversion();
            if (formatSpecification != null) {
                if (firstOrdinaryCharacter < posPercent) {
                    this.formatSpecifications.add(PosixTimeFormatSpecification.ordinaryCharacters(
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
     * @return {@link PosixTimeFormatSpecification} instance if recognized as a format specification, or {@code null} otherwise.
     * @throws AssertionError  if {@code this.pos} does not point {@code '%'}
     */
    @SuppressWarnings({"checkstyle:FallThrough", "checkstyle:LeftCurly"})
    private PosixTimeFormatSpecification tokenizeConversion() {
        assert this.format.charAt(this.pos) == '%';
        final int posPercent = this.pos;
        this.pos++;

        final PosixTimeFormatSpecification.ConversionBuilder builder = new PosixTimeFormatSpecification.ConversionBuilder();
        boolean hasPrecisionProcessed = false;

        for (; this.pos < this.format.length(); this.pos++) {
            final char ch = this.format.charAt(this.pos);

            switch (ch) {
                case 'a':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DAY_OF_WEEK_TEXT_SHORT, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'A':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DAY_OF_WEEK_TEXT_FULL, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'b':  // Equivalent to %h.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.MONTH_OF_YEAR_TEXT_SHORT, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'B':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.MONTH_OF_YEAR_TEXT_FULL, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'c':  // TODO: Get expanded.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.COMPOSITE_LOCAL_DATE_TIME, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'C':  // TODO: Rename.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.CENTURY, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'd':  // TODO: Just with DAY_OF_MONTH with configuring padding.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DAY_OF_MONTH_ZERO, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'D':  // TODO: Get expanded.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DATE_MDY, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'e':  // TODO: Just with DAY_OF_MONTH with configuring padding.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DAY_OF_MONTH_SPACE, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'F':  // TODO: Get expanded.
                    this.pos++;
                    if (!this.options.acceptsUpperCaseFForParsing()) {  // GNU extension.
                        builder.unavailableForParsing();
                    }
                    return builder.build(PosixTimeFormatConversionType.YEAR_MONTH_DAY, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'g':  // TODO: Rename.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.WEEK_BASED_YEAR_OFFSET, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'G':  // TODO: Rename.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.WEEK_BASED_YEAR_FULL, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'h':  // Equivalent to %b.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.MONTH_OF_YEAR_TEXT_SHORT, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'H':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.HOUR_OF_DAY, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'I':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.HOUR_OF_AMPM, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'j':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DAY_OF_YEAR, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'm':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.MONTH_OF_YEAR, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'M':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.MINUTE_OF_HOUR, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'n':
                    // The immediate conversions are tokenized still as conversion specifications
                    // so that options can work for formatting. For example,
                    //
                    //    irb(main):001:0> Time.now.strftime("%10%")
                    //    => "         %"
                    //    irb(main):002:0> Time.now.strftime("%010n")
                    //    => "000000000\n"
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.LITERAL_WHITESPACE_NEWLINE, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'p':  // TODO: Just with AMPM_OF_DAY with configuring case.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.AMPM_OF_DAY, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'r':  // TODO: Get expanded.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.TIME_12_AMPM, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'R':  // TODO: Get expanded.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.HOUR_MINUTE_24, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'S':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.SECOND_OF_MINUTE, '0',
                                         this.format.substring(posPercent, this.pos));

                case 't':
                    // The immediate conversions are tokenized still as conversion specifications
                    // so that options can work for formatting. For example,
                    //
                    //    irb(main):001:0> Time.now.strftime("%10%")
                    //    => "         %"
                    //    irb(main):002:0> Time.now.strftime("%010n")
                    //    => "000000000\n"
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.LITERAL_WHITESPACE_TAB, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'T':  // TODO: Get expanded.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.TIME_24, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'u':  // TODO: Rename with DAY_OF_WEEK.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DAY_OF_WEEK_1_7, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'U':  // TODO: Rename.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.WEEK_NUMBER_OF_YEAR_SUNDAY_0, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'V':  // TODO: Rename.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.WEEK_NUMBER_OF_YEAR_MONDAY_1, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'w':  // TODO: Rename with DAY_OF_WEEK.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DAY_OF_WEEK_0_6, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'W':  // TODO: Rename.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.WEEK_NUMBER_OF_YEAR_MONDAY_0, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'x':  // TODO: Get expanded.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.DATE_LOCALE, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'X':  // TODO: Get expanded.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.TIME_LOCALE, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'y':  // TODO: Rename.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.YEAR_TWO_DIGITS, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'Y':
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.YEAR, '0',
                                         this.format.substring(posPercent, this.pos));

                case 'z':
                    // "%10z" prints "         +0000000000" in glibc.
                    // TODO: Make a decision on it.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.ZONE_OFFSET, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'Z':  // TODO: Rename.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.ZONE_NAME, ' ',
                                         this.format.substring(posPercent, this.pos));

                case '%':
                    // The immediate conversions are tokenized still as conversion specifications
                    // so that options can work for formatting. For example,
                    //
                    //    irb(main):001:0> Time.now.strftime("%10%")
                    //    => "         %"
                    //    irb(main):002:0> Time.now.strftime("%010n")
                    //    => "000000000\n"
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.LITERAL_PERCENT, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'k':  // GNU extension: blank-padded %H
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.HOUR_OF_DAY, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'l':  // GNU extension: blank-padded %I
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.HOUR_OF_AMPM, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 'P':  // TODO: Reverse case.
                    this.pos++;
                    return builder.build(PosixTimeFormatConversionType.AMPM_OF_DAY, ' ',
                                         this.format.substring(posPercent, this.pos));

                case 's':  // TODO: SECOND_SINCE_EPOCH
                    return null;

                case 'L':  // Ruby extension: "%L"
                    /*
                    this.pos++;
                    if (this.options.acceptsUpperCaseLAsTerminatingConversionSpecifier()) {
                        return builder.build(ch, this.format.substring(posPercent, this.pos));
                    }
                    */
                    return null;

                case 'N':  // Ruby extension: "%N"
                    /*
                    this.pos++;
                    if (this.options.acceptsUpperCaseNAsTerminatingConversionSpecifier()) {
                        return builder.build(ch, this.format.substring(posPercent, this.pos));
                    }
                    */
                    return null;

                case 'Q':  // Ruby extension: "%Q"
                    // %Q is only for parsing, not for formatting. Then, %Q never takes any option.
                    // So, a token of "%Q" can always be stringified straightforward to "%Q".
                    return null;

                case 'v':  // Ruby extension: "%v"
                    return null;

                case '+':
                    // Legacy strftime recognizes "%+" as a terminating conversion specifier for date and time (date(1)).
                    /*
                    if (this.options.acceptsPlusSignAsTerminatingConversionSpecifier()) {
                        this.pos++;
                        return builder.build(ch, this.format.substring(posPercent, this.pos));
                    }
                    */

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
    private List<PosixTimeFormatSpecification> formatSpecifications;
}
