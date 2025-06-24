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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalQueries;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ZoneIdParser {

    private ZoneIdParser() {
        // No instantiation.
    }

    /**
     * Builds {@link DateTimeFormatter} for time zone ID.
     *
     * <p>The generated {@link DateTimeFormatter} should parse a time zone ID, as like {@code DateTimeFormatter.ofPattern("VV")}.
     * At the same time, it should accept non-standard time zone names which are given in the argument {@code map}.
     *
     * <p>When {@code map} contains a map from {@code "PST"} to {@code America/Los_Angeles}, the generated {@link DateTimeFormatter}
     * has to be able to parse the string {@code "PST"} into {@code ZoneId.of("America/Los_Angeles")}.
     *
     * @param map  the map from alternative time zone names to {@link ZoneId}
     * @return the generated {@link DateTimeFormatter} for time zones
     */
    public static DateTimeFormatter formatterForZoneId(final Map<String, ZoneId> map) {
        return new CustomZoneIdFormatter(map);
    }
    
    private static class CustomZoneIdFormatter extends DateTimeFormatter {
        private final DateTimeFormatter delegate;
        private final Map<String, ZoneId> customZoneMap;
        
        CustomZoneIdFormatter(final Map<String, ZoneId> customZoneMap) {
            this.delegate = DateTimeFormatter.ofPattern("VV");
            this.customZoneMap = customZoneMap != null ? customZoneMap : new java.util.HashMap<>();
        }
        
        @Override
        public <T> T parse(final CharSequence text, final TemporalQuery<T> query) {
            final String textStr = text.toString();
            
            if (customZoneMap.containsKey(textStr)) {
                final ZoneId zoneId = customZoneMap.get(textStr);
                if (query == TemporalQueries.zone()) {
                    @SuppressWarnings("unchecked")
                    final T result = (T) zoneId;
                    return result;
                }
            }
            
            return delegate.parse(text, query);
        }
        
        @Override
        public TemporalAccessor parse(final CharSequence text) {
            final String textStr = text.toString();
            
            if (customZoneMap.containsKey(textStr)) {
                final ZoneId zoneId = customZoneMap.get(textStr);
                return new ZoneOnlyTemporalAccessor(zoneId);
            }
            
            return delegate.parse(text);
        }
        
        @Override
        public String format(final TemporalAccessor temporal) {
            return delegate.format(temporal);
        }
    }
    
    private static class ZoneOnlyTemporalAccessor implements TemporalAccessor {
        private final ZoneId zoneId;
        
        ZoneOnlyTemporalAccessor(final ZoneId zoneId) {
            this.zoneId = zoneId;
        }
        
        @Override
        public boolean isSupported(final java.time.temporal.TemporalField field) {
            return false;
        }
        
        @Override
        public long getLong(final java.time.temporal.TemporalField field) {
            throw new java.time.temporal.UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <R> R query(final TemporalQuery<R> query) {
            if (query == TemporalQueries.zone()) {
                return (R) zoneId;
            }
            return TemporalAccessor.super.query(query);
        }
    }
}
