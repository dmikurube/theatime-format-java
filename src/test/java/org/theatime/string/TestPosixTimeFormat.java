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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestPosixTimeFormat {
    @Test
    public void testEmpty() {
        assertFormat("");
    }

    @ParameterizedTest
    @CsvSource({
            "abc",
            "%",
            "%@abc",
            "abc%@abc",
    })
    public void testUnmatch(final String format) {
        assertFormat(format, PosixTimeFormatSpecification.ordinaryCharacters(format));
    }

    @Test
    public void test1() {
        assertFormat(
                "%nabc",
                PosixTimeFormatSpecification.conversion('n', false, false, -1, -1, '\0', '\0', "%n"),
                PosixTimeFormatSpecification.ordinaryCharacters("abc"));
    }

    @Test
    public void test2() {
        assertFormat(
                "abc%nabc",
                PosixTimeFormatSpecification.ordinaryCharacters("abc"),
                PosixTimeFormatSpecification.conversion('n', false, false, -1, -1, '\0', '\0', "%n"),
                PosixTimeFormatSpecification.ordinaryCharacters("abc"));
    }

    @Test
    public void test3() {
        assertFormat(
                "abc%12nabc",
                PosixTimeFormatSpecification.ordinaryCharacters("abc"),
                PosixTimeFormatSpecification.conversion('n', false, false, 12, -1, '\0', '\0', "%12n"),
                PosixTimeFormatSpecification.ordinaryCharacters("abc"));
    }

    @ParameterizedTest
    @CsvSource({
            "%%,%,false,false,-1,-1,,,%%",
            "%n,n,false,false,-1,-1,,,%n",
            "%t,t,false,false,-1,-1,,,%t",
            "%12b,b,false,false,12,-1,,,%12b",
            "%z,z,false,false,-1,-1,,,%z",
            "%:z,z,false,false,-1,1,,,%:z",
            "%::z,z,false,false,-1,2,,,%::z",
            "%:::z,z,false,false,-1,3,,,%:::z",
            "%::::z,z,false,false,-1,4,,,%::::z",
    })
    public void testSingles(
            final String format,
            final String expectedCh,
            final String expectedUpperCase,
            final String expectedChangeCase,
            final String expectedPrecision,
            final String expectedColons,
            final String expectedPadding,
            final String expectedModifier,
            final String expectedOriginal) {
        assertFormat(format,
                     PosixTimeFormatSpecification.conversion(
                             expectedCh.charAt(0),
                             Boolean.parseBoolean(expectedUpperCase),
                             Boolean.parseBoolean(expectedChangeCase),
                             Integer.parseInt(expectedPrecision),
                             Integer.parseInt(expectedColons),
                             (expectedPadding == null || expectedPadding.isEmpty()) ? '\0' : expectedPadding.charAt(0),
                             (expectedModifier == null || expectedModifier.isEmpty()) ? '\0' : expectedModifier.charAt(0),
                             expectedOriginal));
    }

    private void assertFormat(final String format, final PosixTimeFormatSpecification... expectedFormatSpecifications) {
        final List<PosixTimeFormatSpecification> actual = PosixTimeFormatTokenizer.tokenize(format);
        assertEquals(Arrays.asList(expectedFormatSpecifications), actual);
        System.out.println("\"" + format + "\"");
        System.out.println(actual);
    }
}
