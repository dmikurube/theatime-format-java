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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;

class TryStrftime {
    TryStrftime(final String path) {
        this.path = path;
    }

    String strftime(
            final String format,
            final int year,
            final int monthOfYear,
            final int dayOfMonth,
            final int hourOfDay,
            final int minuteOfHour,
            final int secondOfMinute,
            final int dayOfWeek,
            final int dayOfYear,
            final int isDst,
            final String locale) {
        final String result;
        try {
            result = this.invoke(
                    format, year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, dayOfWeek, dayOfYear, isDst, locale);
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    String invoke(
            final String format,
            final int year,
            final int monthOfYear,
            final int dayOfMonth,
            final int hourOfDay,
            final int minuteOfHour,
            final int secondOfMinute,
            final int dayOfWeek,
            final int dayOfYear,
            final int isDst,
            final String locale) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(
                this.path,
                format,
                "" + year,
                "" + monthOfYear,
                "" + dayOfMonth,
                "" + hourOfDay,
                "" + minuteOfHour,
                "" + secondOfMinute,
                "" + dayOfWeek,
                "" + dayOfYear,
                "" + isDst,
                locale);
        final Map<String, String> environment = processBuilder.environment();
        environment.put("TZ", "UTC");

        final Process process = processBuilder.start();  // IOException possibly.
        final int exitStatus = process.waitFor();  // InterruptedException possibly.

        final byte[] buffer = new byte[8192];

        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        final InputStream stderrInputStream = process.getErrorStream();
        while (true) {
            final int lengthRead = stderrInputStream.read(buffer);
            if (0 > lengthRead) {
                break;
            }
            stderr.write(buffer, 0, lengthRead);
        }

        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final InputStream stdoutInputStream = process.getInputStream();
        while (true) {
            final int lengthRead = stdoutInputStream.read(buffer);
            if (0 > lengthRead) {
                break;
            }
            stdout.write(buffer, 0, lengthRead);
        }

        if (stderr.size() > 0) {
            throw new RuntimeException(
                    "Detected output in standard error.\n"
                    + "stdout: " + stdout.toString() + "\n"
                    + "stderr: " + stderr.toString());
        }

        // "try_strftime" should have the trailing newline character in the end.
        final String stdoutWithTrailingNewline = stdout.toString();
        if (stdoutWithTrailingNewline.charAt(stdoutWithTrailingNewline.length() - 1) != '\n') {
            throw new RuntimeException("Output did not end with a newline character (\'\\n\').");
        }
        return stdoutWithTrailingNewline.substring(0, stdoutWithTrailingNewline.length() - 1);
    }

    private final String path;
}
