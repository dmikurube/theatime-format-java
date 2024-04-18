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

/**
 * The type of conversion to be applied by POSIX {@code strftime} and {@code strptime}.
 *
 * @see <a href="https://pubs.opengroup.org/onlinepubs/009695399/functions/strftime.html">strftime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/007904875/functions/strptime.html">strptime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strptime.html">strptime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 */
@SuppressWarnings("checkstyle:LineLength")
enum ConversionType {
    POSIX_LOWER_A('a'),

    POSIX_UPPER_A('A'),

    POSIX_LOWER_B('b'),

    POSIX_UPPER_B('B'),

    POSIX_LOWER_C('c'),

    POSIX_UPPER_C('C'),

    POSIX_LOWER_D('d'),

    POSIX_UPPER_D('D'),

    POSIX_LOWER_E('e'),

    POSIX_UPPER_F('F'),

    POSIX_LOWER_G('g'),

    POSIX_UPPER_G('G'),

    POSIX_LOWER_H('h'),

    POSIX_UPPER_H('H'),

    POSIX_UPPER_I('I'),

    POSIX_LOWER_J('j'),

    POSIX_LOWER_M('m'),

    POSIX_UPPER_M('M'),

    /**
     * {@code %n}
     *
     * <p>Formatting - Replaced by a {@code <newline>}.
     *
     * <p>Parsing - Any white space.
     */
    IMMEDIATE_WHITESPACE_NEWLINE('n'),

    POSIX_LOWER_P('p'),

    POSIX_LOWER_R('r'),

    POSIX_UPPER_R('R'),

    POSIX_UPPER_S('S'),

    /**
     * {@code %t}
     *
     * <p>Formatting - Replaced by a {@code <tab>}.
     *
     * <p>Parsing - Any white space.
     */
    IMMEDIATE_WHITESPACE_TAB('t'),

    POSIX_UPPER_T('T'),

    POSIX_LOWER_U('u'),

    POSIX_UPPER_U('U'),

    POSIX_UPPER_V('V'),

    POSIX_LOWER_W('w'),

    POSIX_UPPER_W('W'),

    POSIX_LOWER_X('x'),

    POSIX_UPPER_X('X'),

    POSIX_LOWER_Y('y'),

    POSIX_UPPER_Y('Y'),

    POSIX_LOWER_Z('z'),

    POSIX_UPPER_Z('Z'),

    /**
     * {@code %%}
     *
     * <p>Formatting - Replaced by {@code %}.
     *
     * <p>Parsing - Replaced by {@code %}.
     */
    IMMEDIATE_PERCENT('%'),

    ;

    ConversionType(final char character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return "" + this.character;
    }

    final char character;
}
