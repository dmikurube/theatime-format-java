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

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public final class PosixTimeFormatOptions {
    private PosixTimeFormatOptions(
            final boolean isPlusSignAsTerminatingConversionSpecifier,
            final boolean isUpperCaseLAsTerminatingConversionSpecifier,
            final boolean isUpperCaseNAsTerminatingConversionSpecifier) {
        this.isPlusSignAsTerminatingConversionSpecifier = isPlusSignAsTerminatingConversionSpecifier;
        this.isUpperCaseLAsTerminatingConversionSpecifier = isUpperCaseLAsTerminatingConversionSpecifier;
        this.isUpperCaseNAsTerminatingConversionSpecifier = isUpperCaseNAsTerminatingConversionSpecifier;
    }

    public static PosixTimeFormatOptions of(final PosixTimeFormatOption[] options) {
        boolean isPlusSignAsTerminatingConversionSpecifier = false;
        boolean isUpperCaseLAsTerminatingConversionSpecifier = false;
        boolean isUpperCaseNAsTerminatingConversionSpecifier = false;

        for (final PosixTimeFormatOption option : options) {
            switch (option) {
                case PLUS_SIGN_AS_TERMINATING_CONVERSION_SPECIFIER:
                    isPlusSignAsTerminatingConversionSpecifier = true;
                    break;
                case UPPERCASE_L_AS_TERMINATING_CONVERSION_SPECIFIER:
                    isUpperCaseLAsTerminatingConversionSpecifier = true;
                    break;
                case UPPERCASE_N_AS_TERMINATING_CONVERSION_SPECIFIER:
                    isUpperCaseNAsTerminatingConversionSpecifier = true;
                    break;
                default:
                    break;
            }
        }

        return new PosixTimeFormatOptions(
                isPlusSignAsTerminatingConversionSpecifier,
                isUpperCaseLAsTerminatingConversionSpecifier,
                isUpperCaseNAsTerminatingConversionSpecifier);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public boolean isPlusSignAsTerminatingConversionSpecifier() {
        return this.isPlusSignAsTerminatingConversionSpecifier;
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public boolean isUpperCaseLAsTerminatingConversionSpecifier() {
        return this.isUpperCaseLAsTerminatingConversionSpecifier;
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public boolean isUpperCaseNAsTerminatingConversionSpecifier() {
        return this.isUpperCaseNAsTerminatingConversionSpecifier;
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private final boolean isPlusSignAsTerminatingConversionSpecifier;
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private final boolean isUpperCaseLAsTerminatingConversionSpecifier;
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private final boolean isUpperCaseNAsTerminatingConversionSpecifier;
}
