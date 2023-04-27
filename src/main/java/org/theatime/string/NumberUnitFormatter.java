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

import java.time.DateTimeException;
import java.time.format.DecimalStyle;
import java.time.format.SignStyle;
import java.time.temporal.TemporalAccessor;

/**
 * The year, including the century (for example, 1988).
 *
 * {@code %Y} in POSIX
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/009695399/functions/strftime.html">strftime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/007904875/functions/strptime.html">strptime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strptime.html">strptime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 */
public abstract class NumberUnitFormatter extends UnitFormatter {
    protected NumberUnitFormatter(
            final int digits,
            final DecimalStyle decimalStyle,
            final SignStyle signStyle) {
        this.digits = digits;
        this.decimalStyle = decimalStyle;
        this.signStyle = signStyle;
        this.maxWidth = 4;
        this.minWidth = 2;
    }

    public final void formatNumber(final long value, final DateTimeFormattingContext context, final StringBuilder builder) {
        String str = (value == Long.MIN_VALUE ? "9223372036854775808" : Long.toString(Math.abs(value)));
        if (str.length() > this.maxWidth) {
            throw new DateTimeException("Field " + field +
                                        " cannot be printed as the value " + value +
                                        " exceeds the maximum print width of " + maxWidth);
        }
        str = this.decimalStyle.convertNumberToI18N(str);

        if (value >= 0) {
            switch (signStyle) {
            case EXCEEDS_PAD:
                if (minWidth < 19 && value >= EXCEED_POINTS[minWidth]) {
                    builder.append(decimalStyle.getPositiveSign());
                }
                break;
            case ALWAYS:
                builder.append(decimalStyle.getPositiveSign());
                break;
            }
        } else {
            switch (signStyle) {
            case NORMAL:
            case EXCEEDS_PAD:
            case ALWAYS:
                builder.append(decimalStyle.getNegativeSign());
                break;
            case NOT_NEGATIVE:
                throw new DateTimeException("Field " + field +
                                            " cannot be printed as the value " + value +
                                            " cannot be negative according to the SignStyle");
            }
        }
        for (int i = 0; i < minWidth - str.length(); i++) {
            builder.append(this.decimalStyle.getZeroDigit());
        }
        builder.append(str);
    }

    public final int parseNumber() {
        return 0;
    }

    /**
     * The number of digits to read.
     *
     * <p>In case of the POSIX formatter {@code %Y}, it would be {@code 4} if it is followed by a number-based formatter,
     * or {@link Integer.MAX_VALUE} otherwise.
     */
    private final int digits;

    private final int maxWidth;
    private final int minWidth;

    private final DecimalStyle decimalStyle;

    private final SignStyle signStyle;
}
