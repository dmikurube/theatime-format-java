/*
 * Copyright 2024 Dai MIKURUBE
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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Map;

public final class PosixFields {
    private PosixFields() {
        // No instantiation.
    }

    /**
     * The field that represents the last two digits of the year.
     *
     * <p>This field allows the last two digits of the year to be queried and set.
     * The the last two digits of the year has values from 0 to 99.
     *
     * <p>The last two digits of the year can only be calculated if the year is available.
     */
    public static final TemporalField YEAR_OF_POSIX_CENTURY_1969_2068 = Field.YEAR_OF_POSIX_CENTURY_1969_2068;

    /**
     * The field that represents all but the last two digits of the year. In other words,
     * the year divided by 100 and truncated to an integer.
     */
    public static final TemporalField POSIX_CENTURY = Field.POSIX_CENTURY;

    private static enum Field implements TemporalField {
        YEAR_OF_POSIX_CENTURY_1969_2068 {
            @Override
            public TemporalUnit getBaseUnit() {
                return ChronoUnit.YEARS;
            }

            @Override
            public TemporalUnit getRangeUnit() {
                return ChronoUnit.CENTURIES;
            }

            @Override
            public ValueRange range() {
                return ValueRange.of(0, 99);
            }

            @Override
            public boolean isDateBased() {
                return true;
            }

            @Override
            public boolean isTimeBased() {
                return false;
            }

            @Override
            public boolean isSupportedBy(final TemporalAccessor temporal) {
                return temporal.isSupported(ChronoField.YEAR) && Chronology.from(temporal).equals(IsoChronology.INSTANCE);
            }

            @Override
            public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
                if (this.isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: YearOfPosixCentury");
                }
                final long yearOfPosixCentury = temporal.getLong(ChronoField.YEAR) % 100;
                return ValueRange.of(yearOfPosixCentury, yearOfPosixCentury);
            }

            @Override
            public long getFrom(final TemporalAccessor temporal) {
                if (this.isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: YearOfPosixCentury");
                }
                return temporal.getLong(ChronoField.YEAR) % 100;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <R extends Temporal> R adjustInto(final R temporal, final long newValue) {
                this.range().checkValidValue(newValue, this);  // 0 <= yearOfPosixCentury <= 99

                if (temporal.isSupported(ChronoField.YEAR)) {
                    final long year = temporal.getLong(ChronoField.YEAR);
                    final long posixCenturyFromYear = year / 100;
                    return (R) temporal.with(ChronoField.YEAR, posixCenturyFromYear * 100 + newValue);
                }

                if (newValue >= 69) {
                    return (R) temporal.with(ChronoField.YEAR, 1900 + newValue);
                }
                return (R) temporal.with(ChronoField.YEAR, 2000 + newValue);
            }

            @Override
            public ChronoLocalDate resolve(
                    final Map<TemporalField, Long> fieldValues,
                    final TemporalAccessor partialTemporal,
                    final ResolverStyle resolverStyle) {
                final Long yearLong = fieldValues.get(ChronoField.YEAR);
                final Long posixCenturyLong = fieldValues.get(POSIX_CENTURY);

                // Expect it exists.
                final long yearOfPosixCenturyLong = fieldValues.get(YEAR_OF_POSIX_CENTURY_1969_2068);

                if (!Chronology.from(partialTemporal).equals(IsoChronology.INSTANCE)) {
                    throw new DateTimeException("Resolve requires IsoChronology");
                }

                final int yearOfPosixCentury;
                if (resolverStyle == ResolverStyle.SMART || resolverStyle == ResolverStyle.STRICT) {
                    yearOfPosixCentury = this.range().checkValidIntValue(yearOfPosixCenturyLong, this);
                } else {
                    yearOfPosixCentury = (int) yearOfPosixCenturyLong;
                }

                final int resolvedYear;
                if (posixCenturyLong != null) {
                    final int posixCentury;
                    if (resolverStyle == ResolverStyle.SMART || resolverStyle == ResolverStyle.STRICT) {
                        posixCentury = POSIX_CENTURY.range().checkValidIntValue(posixCenturyLong, POSIX_CENTURY);
                    } else {
                        posixCentury = posixCenturyLong.intValue();
                    }
                    if (resolverStyle == ResolverStyle.STRICT && yearLong != null && yearLong / 100 != posixCentury) {
                        throw new DateTimeException("Strict mode rejected resolved POSIX century as year is in a different POSIX century");
                    }
                    resolvedYear = posixCentury * 100 + yearOfPosixCentury;
                } else if (yearLong != null) {
                    final int posixCenturyFromYear;
                    if (resolverStyle == ResolverStyle.SMART || resolverStyle == ResolverStyle.STRICT) {
                        posixCenturyFromYear = ChronoField.YEAR.checkValidIntValue(yearLong) / 100;
                    } else {
                        posixCenturyFromYear = (int) (yearLong / 100);
                    }
                    resolvedYear = posixCenturyFromYear * 100 + yearOfPosixCentury;
                } else {
                    // STRICT does not guesstimate YEAR only from YEAR_OF_POSIX_CENTURY (+1900 or +2000).
                    // STRICT always requires POSIX_CENTURY or YEAR.
                    if (resolverStyle == ResolverStyle.STRICT) {
                        throw new DateTimeException("Strict mode rejected year of POSIX century without explicit POSIX century");
                    }
                    if (yearOfPosixCentury >= 69) {
                        resolvedYear = 1900 + yearOfPosixCentury;
                    } else {
                        resolvedYear = 2000 + yearOfPosixCentury;
                    }
                }

                fieldValues.remove(this);
                fieldValues.remove(POSIX_CENTURY);

                final Long monthLong = fieldValues.get(ChronoField.MONTH_OF_YEAR);
                final Long dayLong = fieldValues.get(ChronoField.DAY_OF_MONTH);
                if (monthLong == null || dayLong == null) {
                    // It cannot compose a date (LocalDate) if month or day are unavailable.
                    // It just complements a year, and returns null, in this case.
                    fieldValues.put(ChronoField.YEAR, Long.valueOf(resolvedYear));
                    return null;
                }

                fieldValues.remove(ChronoField.YEAR);

                // It composes a date (LocalDate) and returns the date if both month or day are available.
                final int month = ChronoField.MONTH_OF_YEAR.checkValidIntValue(monthLong);
                final int day = ChronoField.DAY_OF_MONTH.checkValidIntValue(dayLong);
                return LocalDate.of(resolvedYear, month, day);
            }

            @Override
            public String toString() {
                return "YearOfPosixCentury";
            }
        },

        POSIX_CENTURY {
            @Override
            public TemporalUnit getBaseUnit() {
                return ChronoUnit.CENTURIES;
            }

            @Override
            public TemporalUnit getRangeUnit() {
                return ChronoUnit.FOREVER;
            }

            @Override
            public ValueRange range() {
                return ValueRange.of(Year.MIN_VALUE / 100, Year.MAX_VALUE / 100);
            }

            @Override
            public boolean isDateBased() {
                return true;
            }

            @Override
            public boolean isTimeBased() {
                return false;
            }

            @Override
            public boolean isSupportedBy(final TemporalAccessor temporal) {
                return temporal.isSupported(ChronoField.YEAR) && Chronology.from(temporal).equals(IsoChronology.INSTANCE);
            }

            @Override
            public ValueRange rangeRefinedBy(final TemporalAccessor temporal) {
                if (this.isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: PosixCentury");
                }
                final long posixCentury = temporal.getLong(ChronoField.YEAR) / 100;
                return ValueRange.of(posixCentury, posixCentury);
            }

            @Override
            public long getFrom(final TemporalAccessor temporal) {
                if (this.isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: PosixCentury");
                }
                return temporal.getLong(ChronoField.YEAR) / 100;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <R extends Temporal> R adjustInto(final R temporal, final long newValue) {
                final long posixCentury = this.getFrom(temporal);
                final long year = temporal.getLong(ChronoField.YEAR);
                return (R) temporal.with(ChronoField.YEAR, posixCentury * 100 + (year % 100));
            }

            // Not to implement #resolve(Map<TemporalField, Long>, TemporalAccessor, ResolverStyle) here.

            @Override
            public String toString() {
                return "PosixCentury";
            }
        },

        ;
    }
}
