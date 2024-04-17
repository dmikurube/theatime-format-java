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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    @Test
    public void testEndWithSinglePercent() {
        assertFormat("%", Literal.of("%", C));
        assertFormat("foo%", Literal.of("foo%", C));
    }

    @Test
    public void testLowerA() {
        assertFormat("%a", new LowerA(C));
        assertFormat("%+a", new LowerA(new AbstractSpecification.Context(false, false, -1, -1, '0', '\0', "", 0, 0)));
    }

    @Test
    public void testLiteral() {
        assertFormat("foo", Literal.of("foo", C));
        assertFormat("%&", Literal.of("%&", C));
        assertFormat("foo%&bar", Literal.of("foo%&bar", C));
        assertFormat("%&foo%&", Literal.of("%&foo%&", C));
        assertFormat("%&foo%&bar%&", Literal.of("%&foo%&bar%&", C));
        assertFormat("foo%&bar%&baz", Literal.of("foo%&bar%&baz", C));
    }

    @Test
    public void testMixed() {
        assertFormat("foo%bbar", Literal.of("foo", C), new LowerB(C), Literal.of("bar", C));
        assertFormat("%bfoo%a", new LowerB(C), Literal.of("foo", C), new LowerA(C));
        assertFormat("foo%&bar%bbaz", Literal.of("foo%&bar", C), new LowerB(C), Literal.of("baz", C));
        assertFormat("foo%bbar%&baz", Literal.of("foo", C), new LowerB(C), Literal.of("bar%&baz", C));
        assertFormat("foo%&bar%bbaz%a", Literal.of("foo%&bar", C), new LowerB(C), Literal.of("baz", C), new LowerA(C));
        assertFormat("foo%bbar%&baz%a", Literal.of("foo", C), new LowerB(C), Literal.of("bar%&baz", C), new LowerA(C));
        assertFormat("foo%&bar%bbaz%$", Literal.of("foo%&bar", C), new LowerB(C), Literal.of("baz%$", C));
        assertFormat("foo%bbar%&baz%$", Literal.of("foo", C), new LowerB(C), Literal.of("bar%&baz%$", C));

        assertFormat("foo%bbar%&baz%", Literal.of("foo", C), new LowerB(C), Literal.of("bar%&baz%", C));
    }

    @ParameterizedTest
    @CsvSource({
            "abc",
            "%",
            "%@abc",
            "abc%@abc",
    })
    public void testUnmatch(final String format) {
        assertFormat(format, Literal.of(format, C));
    }

    @Test
    public void testDateTimeFormatter() {
        final DateTimeFormatter formatter = PosixTimeFormat.compile("%a%A%q%b%B").toDateTimeFormatter();
        assertEquals("MonMonday%qAprApril", formatter.format(ZonedDateTime.of(2023, 4, 17, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo"))));
    }

    @Test
    public void test1() {
        assertFormat(
                "%nabc",
                Literal.of("\nabc", C));
    }

    @Test
    public void test2() {
        assertFormat(
                "abc%nabc",
                Literal.of("abc\nabc", C));
    }

    @Test
    public void test3() {
        assertFormat(
                "abc%12nabc",
                Literal.of("abc\nabc", C));
    }

    @ParameterizedTest
    @CsvSource({
            // "%%,%,false,false,-1,-1,,,%%",
            // "%n,n,false,false,-1,-1,,,%n",
            // "%t,t,false,false,-1,-1,,,%t",
            "%12b,b,false,false,12,-1,,,%12b",
            "%z,z,false,false,-1,-1,,,%z",
            "%:z,z,false,false,-1,1,,,%:z",
            "%::z,z,false,false,-1,2,,,%::z",
            "%:::z,z,false,false,-1,3,,,%:::z",
            "%::::z,z,false,false,-1,4,,,%::::z",
    })
    public void testSingles(
            final String format,
            final String expectedChar,
            final String expectedUpperCase,
            final String expectedChangeCase,
            final String expectedPrecision,
            final String expectedColons,
            final String expectedPadding,
            final String expectedModifier,
            final String expectedOriginal) {
        assertSingleFormatOptions(
                expectedChar.charAt(0),
                Boolean.parseBoolean(expectedUpperCase),
                Boolean.parseBoolean(expectedChangeCase),
                Integer.parseInt(expectedPrecision),
                Integer.parseInt(expectedColons),
                (expectedPadding == null || expectedPadding.isEmpty()) ? '\0' : expectedPadding.charAt(0),
                (expectedModifier == null || expectedModifier.isEmpty()) ? '\0' : expectedModifier.charAt(0),
                format);
    }

    private void assertFormat(final String format, final AbstractSpecification... expectedFormatSpecifications) {
        final List<AbstractSpecification> actual = Tokenizer.tokenize(format);
        assertEquals(Arrays.asList(expectedFormatSpecifications), actual);
        System.out.println("\"" + format + "\"");
        System.out.println(actual);
    }

    private void assertSingleFormatOptions(
            final char expectedChar,
            final boolean expectedUpperCase,
            final boolean expectedChangeCase,
            final int expectedPrecision,
            final int expectedColons,
            final char expectedPadding,
            final char expectedModifier,
            final String format) {
        final List<AbstractSpecification> actual = Tokenizer.tokenize(format);
        assertEquals(1, actual.size());
        assertTrue(actual.get(0) instanceof ConversionSpecification);
        final ConversionSpecification actualConversion = (ConversionSpecification) actual.get(0);
        assertEquals(expectedChar, actualConversion.conversionChar);
        assertEquals(expectedUpperCase, actualConversion.upperCase);
        assertEquals(expectedChangeCase, actualConversion.changeCase);
        assertEquals(expectedPrecision, actualConversion.precision);
        assertEquals(expectedColons, actualConversion.colons);
        assertEquals(expectedPadding, actualConversion.padding);
        assertEquals(expectedModifier, actualConversion.modifier);
    }

    private static AbstractSpecification.Context C = AbstractSpecification.EMPTY;
}
