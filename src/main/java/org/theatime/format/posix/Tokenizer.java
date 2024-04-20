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
    private Tokenizer(final String format, final Options options) {
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
    static List<Specification> tokenize(final String format, final PosixTimeFormat.Option... options) {
        return new Tokenizer(format, Options.of(options)).tokenizeInitial();
    }

    private List<Specification> tokenizeInitial() {
        int firstOrdinaryCharacter = 0;

        this.pos = 0;
        this.formatSpecifications = new ArrayList<>();

        while (true) {
            final int posPercent = this.format.indexOf('%', this.pos);

            if (posPercent < 0) {
                if (firstOrdinaryCharacter < this.format.length()) {
                    this.formatSpecifications.add(Literal.of(
                            this.format.substring(firstOrdinaryCharacter),
                            new Specification.Context(false, false, -1, -1, '\0', '\0', this.format, firstOrdinaryCharacter, this.format.length()),
                            false));
                }
                break;
            }

            this.pos = posPercent;
            final Specification formatSpecification = this.tokenizeConversion();
            if (formatSpecification != null) {
                if (firstOrdinaryCharacter < posPercent) {
                    this.formatSpecifications.add(Literal.of(
                            this.format.substring(firstOrdinaryCharacter, posPercent),
                            new Specification.Context(false, false, -1, -1, '\0', '\0', this.format, firstOrdinaryCharacter, posPercent),
                            false));
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

        final Specification.ContextSource ctx = new Specification.ContextSource(this.format);
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
                case '%':
                    return Literal.ofPercent(ctx.at(posPercent, ++this.pos), false);

                case 'n':
                    return Literal.ofNewline(ctx.at(posPercent, ++this.pos), false);

                case 't':
                    return Literal.ofTab(ctx.at(posPercent, ++this.pos), false);

                case 'a':
                    return new LowerA(ctx.at(posPercent, ++this.pos));

                case 'A':
                    return new UpperA(ctx.at(posPercent, ++this.pos));

                case 'b':
                    return new LowerB(ctx.at(posPercent, ++this.pos));

                case 'B':
                    return new UpperB(ctx.at(posPercent, ++this.pos));

                case 'c':
                    return new LowerC(ctx.at(posPercent, ++this.pos));

                case 'C':
                    return new UpperC(ctx.at(posPercent, ++this.pos));

                case 'd':
                    return new LowerD(ctx.at(posPercent, ++this.pos));

                case 'D':
                    return new UpperD(ctx.at(posPercent, ++this.pos));

                case 'e':
                    return new LowerE(ctx.at(posPercent, ++this.pos));

                case 'F':
                    return new UpperF(ctx.at(posPercent, ++this.pos));

                case 'g':
                    return new LowerG(ctx.at(posPercent, ++this.pos));

                case 'G':
                    return new UpperG(ctx.at(posPercent, ++this.pos));

                case 'h':
                    return new LowerH(ctx.at(posPercent, ++this.pos));

                case 'H':
                    return new UpperH(ctx.at(posPercent, ++this.pos));

                case 'I':
                    return new UpperI(ctx.at(posPercent, ++this.pos));

                case 'j':
                    return new LowerJ(ctx.at(posPercent, ++this.pos));

                /*
                case 'k':  // GNU extension.
                */

                /*
                case 'l':  // GNU extension.
                */

                /*
                case 'L':  // Ruby extension: "%L"
                    this.pos++;
                    if (this.options.isUpperCaseLAsTerminatingConversionSpecifier()) {
                        return builder.build(ch, this.format.substring(posPercent, this.pos));
                    }
                    return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
                */

                case 'm':
                    return new LowerM(ctx.at(posPercent, ++this.pos));

                case 'M':
                    return new UpperM(ctx.at(posPercent, ++this.pos));

                /*
                case 'N':  // Ruby extension: "%N"
                    this.pos++;
                    if (this.options.isUpperCaseNAsTerminatingConversionSpecifier()) {
                        return builder.build(ch, this.format.substring(posPercent, this.pos));
                    }
                    return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
                */

                case 'p':
                    return new LowerP(ctx.at(posPercent, ++this.pos));

                /*
                case 'P':
                    return new UpperP(ctx.at(posPercent, ++this.pos));
                */

                /*
                // Ruby extensions.
                case 'Q':
                        // %Q is only for parsing, not for formatting. Then, %Q never takes any option.
                        // So, a token of "%Q" can always be stringified straightforward to "%Q".
                    break;
                */

                case 'r':
                    return new LowerR(ctx.at(posPercent, ++this.pos));

                case 'R':
                    return new UpperR(ctx.at(posPercent, ++this.pos));

                /*
                case 's':  // GNU extension.
                */

                case 'S':
                    return new UpperS(ctx.at(posPercent, ++this.pos));

                case 'T':
                    return new UpperT(ctx.at(posPercent, ++this.pos));

                case 'u':
                    return new LowerU(ctx.at(posPercent, ++this.pos));

                case 'U':
                    return new UpperU(ctx.at(posPercent, ++this.pos));

                /*
                case 'v':  // GNU extensions.
                */

                case 'V':
                    return new UpperV(ctx.at(posPercent, ++this.pos));

                case 'w':
                    return new LowerW(ctx.at(posPercent, ++this.pos));

                case 'W':
                    return new UpperW(ctx.at(posPercent, ++this.pos));

                case 'x':
                    return new LowerX(ctx.at(posPercent, ++this.pos));

                case 'X':
                    return new UpperX(ctx.at(posPercent, ++this.pos));

                case 'y':
                    return new LowerY(ctx.at(posPercent, ++this.pos));

                case 'Y':
                    return new UpperY(ctx.at(posPercent, ++this.pos));

                case 'z':
                    return new LowerZ(ctx.at(posPercent, ++this.pos));

                case 'Z':
                    return new UpperZ(ctx.at(posPercent, ++this.pos));

                /*
                case '+':
                    // Legacy strftime recognizes "%+" as a terminating conversion specifier for date and time (date(1)).
                    if (this.options.acceptsPlusSignAsTerminatingConversionSpecifier()) {
                        return new Plus(ctx.at(posPercent, ++this.pos));
                    }

                    // Modern strftime recognizes "%+" as an optional flag.
                    if (!hasPrecisionProcessed) {
                        // TODO: Set '+' flag.
                        ctx.pad('0');
                        break;
                    }

                    // The position is after the precision part.
                    this.pos++;
                    return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
                */

                case '-':
                    if (!hasPrecisionProcessed) {
                        ctx.pad('-');
                        break;
                    }

                    // The position is after the precision part.
                    this.pos++;
                    return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);

                case '^':
                    if (!hasPrecisionProcessed) {
                        ctx.upperCase();
                        break;
                    }

                    // The position is after the precision part.
                    this.pos++;
                    return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);

                case '#':
                    if (!hasPrecisionProcessed) {
                        ctx.changeCase();
                        break;
                    }

                    // The position is after the precision part.
                    this.pos++;
                    return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);

                case '_':
                    if (!hasPrecisionProcessed) {
                        ctx.pad('_');
                        break;
                    }

                    // The position is after the precision part.
                    this.pos++;
                    return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);

                case ':':
                    // strptime accepts only 3 colons at maximum.
                    // strftime accepts unlimited number of colons.
                    for (int j = 1; ; j++) {
                        if (this.pos + j >= this.format.length()) {
                            this.pos++;
                            return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
                        }
                        if (this.format.charAt(this.pos + j) == 'z') {
                            ctx.colons(j);
                            this.pos += (j - 1);
                            break;
                        }
                        if (this.format.charAt(this.pos + j) != ':') {
                            this.pos++;
                            return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
                        }
                    }
                    break;

                case 'E':
                    if (this.pos + 1 < this.format.length() && "cCxXyY".indexOf(this.format.charAt(this.pos + 1)) >= 0) {
                        ctx.modifier('E');
                        break;
                    } else {
                        this.pos++;
                        return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
                    }

                case 'O':
                    if (this.pos + 1 < this.format.length() && "deHImMSuUVwWy".indexOf(this.format.charAt(this.pos + 1)) >= 0) {
                        ctx.modifier('O');
                        break;
                    } else {
                        this.pos++;
                        return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
                    }

                case '0':
                    ctx.pad('0');
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
                            return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
                        }
                        hasPrecisionProcessed = true;
                        ctx.precision(Integer.parseInt(digits));
                        this.pos += digits.length() - 1;
                    }
                    break;

                default:
                    this.pos++;
                    return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
            }
        }
        return Literal.of(this.format.substring(posPercent, this.pos), ctx.at(posPercent, this.pos), false);
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
    private final Options options;

    private int pos;
    private List<Specification> formatSpecifications;
}
