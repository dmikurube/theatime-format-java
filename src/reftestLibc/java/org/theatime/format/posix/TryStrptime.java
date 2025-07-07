/*
 * Copyright 2025 Dai MIKURUBE
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

class TryStrptime {
    TryStrptime(final String path) {
        this.path = path;
    }

    static class Parsed {
        Parsed(final String parsedLine) {
            final String[] parsed = parsedLine.split("\t", 10);
            if (parsed.length < 10) {
                throw new RuntimeException("Output did not contain expected results:" + parsedLine);
            }

            this.year = Integer.parseInt(parsed[0]);
            this.mon = Integer.parseInt(parsed[1]);
            this.mday = Integer.parseInt(parsed[2]);
            this.hour = Integer.parseInt(parsed[3]);
            this.min = Integer.parseInt(parsed[4]);
            this.sec = Integer.parseInt(parsed[5]);
            this.wday = Integer.parseInt(parsed[6]);
            this.yday = Integer.parseInt(parsed[7]);
            this.isdst = Integer.parseInt(parsed[8]);
            this.remaining = parsed[9];
        }

        final int year;
        final int mon;
        final int mday;
        final int hour;
        final int min;
        final int sec;
        final int wday;
        final int yday;
        final int isdst;
        final String remaining;
    }

    Parsed strptime(
            final String format,
            final String datetime,
            final String locale) {
        final Parsed result;
        try {
            result = this.invoke(format, datetime, locale);
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    Parsed invoke(
            final String format,
            final String datetime,
            final String locale) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(
                this.path,
                datetime,
                format,
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

        if (exitStatus != 0) {
            throw new RuntimeException(
                    "Aborted from try_strptime with exit status: " + exitStatus + "\n"
                    + "stdout: " + stdout.toString() + "\n"
                    + "stderr: " + stderr.toString());
        }

        if (stderr.size() > 0) {
            throw new RuntimeException(
                    "Detected output in standard error.\n"
                    + "stdout: " + stdout.toString() + "\n"
                    + "stderr: " + stderr.toString());
        }

        // year<TAB>month<TAB>day<TAB>hour<TAB>minute<TAB>second<TAB>dayofweek<TAB>dayofyear<TAB>isdst<TAB>remaining
        final String stdoutWithTrailingNewline = stdout.toString();
        if (stdoutWithTrailingNewline.charAt(stdoutWithTrailingNewline.length() - 1) != '\n') {
            throw new RuntimeException("Output did not end with a newline character ('\\n').");
        }
        final String stdoutSingleLine = stdoutWithTrailingNewline.substring(0, stdoutWithTrailingNewline.length() - 1);

        return new Parsed(stdoutSingleLine);
    }

    private final String path;
}
