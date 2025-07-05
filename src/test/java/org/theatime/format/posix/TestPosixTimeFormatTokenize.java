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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestPosixTimeFormatTokenize {
    @Test
    public void testTokenizeEmpty() {
        assertFormat("");
    }

    @Test
    public void testTokenize1() {
        assertFormat(
                "%nabc",
                Literal.of("\n", C),
                Literal.of("abc", C));
    }

    @Test
    public void testTokenize2() {
        assertFormat(
                "abc%nabc",
                Literal.of("abc", C),
                Literal.of("\n", C),
                Literal.of("abc", C));
    }

    @Test
    public void testTokenize3() {
        assertFormat(
                "abc%12nabc",
                Literal.of("abc", C),
                Literal.of("\n", C),
                Literal.of("abc", C));
    }

    @ParameterizedTest
    @CsvSource({
            "abc",
            "%",
    })
    public void testTokenizeWithoutAnySpecification(final String format) {
        assertFormat(format, Literal.of(format, C));
    }

    @Test
    public void testTokenizeEndingWithSinglePercent() {
        assertFormat("%", Literal.of("%", C));
        assertFormat("foo%", Literal.of("foo", C), Literal.of("%", C));
    }

    @Test
    public void testTokenizeOfLiteral1() {
        assertFormat("foo",
                     Literal.of("foo", C));
    }

    @Test
    public void testTokenizeOfLiteral2() {
        assertFormat("%&",
                     Literal.of("%&", C));
    }

    @Test
    public void testTokenizeOfLiteral3() {
        assertFormat("%10&",
                     Literal.of("%10&", new Specification.Context(false, false, 10, -1, '0', '\0', "", 0, 0)));
    }

    @Test
    public void testTokenizeOfLiteral4() {
        assertFormat("foo%&bar",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C));
    }

    @Test
    public void testTokenizeOfLiteral5() {
        assertFormat("%&foo%&",
                     Literal.of("%&", C),
                     Literal.of("foo", C),
                     Literal.of("%&", C));
    }

    @Test
    public void testTokenizeOfLiteral6() {
        assertFormat("%&foo%&bar%&",
                     Literal.of("%&", C),
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     Literal.of("%&", C));
    }

    @Test
    public void testTokenizeOfLiteral7() {
        assertFormat("foo%&bar%&baz",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C));
    }

    @Test
    public void testTokenizeOfMixed1() {
        assertFormat("foo%bbar",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C));
    }

    @Test
    public void testTokenizeOfMixed2() {
        assertFormat("%bfoo%a",
                     new LowerB(C),
                     Literal.of("foo", C),
                     new LowerA(C));
    }

    @Test
    public void testTokenizeOfMixed3() {
        assertFormat("foo%&bar%bbaz",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     new LowerB(C),
                     Literal.of("baz", C));
    }

    @Test
    public void testTokenizeOfMixed4() {
        assertFormat("foo%bbar%&baz",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C));
    }

    @Test
    public void testTokenizeOfMixed5() {
        assertFormat("foo%&bar%bbaz%a",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     new LowerB(C),
                     Literal.of("baz", C),
                     new LowerA(C));
    }

    @Test
    public void testTokenizeOfMixed6() {
        assertFormat("foo%bbar%&baz%a",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C),
                     new LowerA(C));
    }

    @Test
    public void testTokenizeOfMixed7() {
        assertFormat("foo%&bar%bbaz%$",
                     Literal.of("foo", C),
                     Literal.of("%&", C),
                     Literal.of("bar", C),
                     new LowerB(C),
                     Literal.of("baz", C),
                     Literal.of("%$", C));
    }

    @Test
    public void testTokenizeOfMixed8() {
        assertFormat("foo%bbar%&baz%$",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C),
                     Literal.of("%$", C));
    }

    @Test
    public void testTokenizeOfMixed9() {
        assertFormat("foo%bbar%&baz%",
                     Literal.of("foo", C),
                     new LowerB(C),
                     Literal.of("bar", C),
                     Literal.of("%&", C),
                     Literal.of("baz", C),
                     Literal.of("%", C));
    }

    @Test
    public void testTokenizeOfMixed10() {
        assertFormat("%b %e, %Y",
                     new LowerB(C),
                     Literal.of(" ", C),
                     new LowerE(C),
                     Literal.of(", ", C),
                     new UpperY(C));
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
    public void testTokenizeOfSingleSpecification(
            final String format,
            final String expectedChar,
            final String expectedUpperCase,
            final String expectedChangeCase,
            final String expectedPrecision,
            final String expectedColons,
            final String expectedPad,
            final String expectedModifier,
            final String expectedOriginal) {
        assertSingleFormatOptions(
                expectedChar.charAt(0),
                Boolean.parseBoolean(expectedUpperCase),
                Boolean.parseBoolean(expectedChangeCase),
                Integer.parseInt(expectedPrecision),
                Integer.parseInt(expectedColons),
                (expectedPad == null || expectedPad.isEmpty()) ? '\0' : expectedPad.charAt(0),
                (expectedModifier == null || expectedModifier.isEmpty()) ? '\0' : expectedModifier.charAt(0),
                format);
    }

    @Test
    public void testTokenizeOfLowerA() {
        assertFormat("%a", new LowerA(C));
        assertFormat("%0a", new LowerA(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
    }

    @Test
    public void testTokenizeOfLowerE() {
        assertFormat("%e", new LowerE(C));
        assertFormat("%0e", new LowerE(new Specification.Context(false, false, 0, -1, '0', '\0', "", 0, 0)));
        assertFormat("%_e", new LowerE(new Specification.Context(false, false, -1, -1, '_', '\0', "", 0, 0)));
        assertFormat("%-e", new LowerE(new Specification.Context(false, false, -1, -1, '-', '\0', "", 0, 0)));
        assertFormat("%3e", new LowerE(new Specification.Context(false, false, 3, -1, '\0', '\0', "", 0, 0)));
    }

    private void assertFormat(final String format, final Specification... expectedFormatSpecifications) {
        final List<Specification> actual = Tokenizer.tokenize(format);
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
            final char expectedPad,
            final char expectedModifier,
            final String format) {
        final List<Specification> actual = Tokenizer.tokenize(format);
        assertEquals(1, actual.size());
        assertTrue(actual.get(0) instanceof ConversionSpecification);
        final ConversionSpecification actualConversion = (ConversionSpecification) actual.get(0);
        assertEquals(expectedChar, actualConversion.terminatingConversionSpecifier.toString().charAt(0));
        assertEquals(expectedUpperCase, actualConversion.upperCase);
        assertEquals(expectedChangeCase, actualConversion.changeCase);
        assertEquals(expectedPrecision, actualConversion.precision);
        assertEquals(expectedColons, actualConversion.colons);
        assertEquals(expectedPad, actualConversion.pad);
        assertEquals(expectedModifier, actualConversion.modifier);
    }

    private static Specification.Context C = Specification.EMPTY;
}
