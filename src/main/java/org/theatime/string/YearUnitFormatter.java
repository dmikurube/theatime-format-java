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
 * The year, including the century (for example, 1988).
 *
 * {@code %Y} in POSIX
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/009695399/functions/strftime.html">strftime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/007904875/functions/strptime.html">strptime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strptime.html">strptime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 */
public final class YearUnitFormatter extends UnitFormatter {
    private YearUnitFormatter(final int digits) {
        this.digits = digits;
    }

    @Override
    public boolean format(final DateTimeFormattingContext context, final StringBuilder builder) {
        builder.append();
        return true;
    }

    @Override
    public int parse(DateTimeParsingContext context, CharSequence string, int position) {
        return ;
    }

    /**
     * The number of digits to read.
     *
     * <p>In case of the POSIX formatter {@code %Y}, it would be {@code 4} if it is followed by a number-based formatter,
     * or {@link Integer.MAX_VALUE} otherwise.
     */
    private final int digits;
}
