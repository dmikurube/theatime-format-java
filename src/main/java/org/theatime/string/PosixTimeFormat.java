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

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

public final class PosixTimeFormat {
    private PosixTimeFormat(final List<PosixTimeFormatSpecification> formatSpecifications) {
        this.formatSpecifications = formatSpecifications;
    }

    public static PosixTimeFormat compile(final String format, final PosixTimeFormatOption... options) {
        return new PosixTimeFormat(PosixTimeFormatTokenizer.tokenize(format, options));
    }

    public static DateTimeFormatter compileToDateTimeFormatter(final String format, final PosixTimeFormatOption... options) {
        final List<PosixTimeFormatSpecification> specifications = PosixTimeFormatTokenizer.tokenize(format, options);
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        for (final PosixTimeFormatSpecification specification : specifications) {
            if (!specification.appendToDateTimeFormatterBuilder(builder)) {
                throw new IllegalArgumentException("Invalid format for java.time.format.DateTimeFormatter.");
            }
        }
        return builder.toFormatter();
    }

    private final List<PosixTimeFormatSpecification> formatSpecifications;
}
