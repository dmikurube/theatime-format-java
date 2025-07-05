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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class MonthOfYearNames {
    private MonthOfYearNames() {
        // No instantiation.
    }

    public static Map<Long, String> shortTitleCase() {
        return ShortTitleCase.NAMES;
    }

    public static Map<Long, String> shortUpperCase() {
        return ShortUpperCase.NAMES;
    }

    public static Map<Long, String> fullTitleCase() {
        return FullTitleCase.NAMES;
    }

    public static Map<Long, String> fullUpperCase() {
        return FullUpperCase.NAMES;
    }

    private static class ShortTitleCase {  // Initialization-on-demand holder idiom.
        static final Map<Long, String> NAMES;

        static {
            final HashMap<Long, String> names = new HashMap<>();
            names.put(1L, "Jan");
            names.put(2L, "Feb");
            names.put(3L, "Mar");
            names.put(4L, "Apr");
            names.put(5L, "May");
            names.put(6L, "Jun");
            names.put(7L, "Jul");
            names.put(8L, "Aug");
            names.put(9L, "Sep");
            names.put(10L, "Oct");
            names.put(11L, "Nov");
            names.put(12L, "Dec");
            NAMES = Collections.unmodifiableMap(names);
        }
    }

    private static class ShortUpperCase {  // Initialization-on-demand holder idiom.
        static final Map<Long, String> NAMES;

        static {
            final HashMap<Long, String> names = new HashMap<>();
            names.put(1L, "JAN");
            names.put(2L, "FEB");
            names.put(3L, "MAR");
            names.put(4L, "APR");
            names.put(5L, "MAY");
            names.put(6L, "JUN");
            names.put(7L, "JUL");
            names.put(8L, "AUG");
            names.put(9L, "SEP");
            names.put(10L, "OCT");
            names.put(11L, "NOV");
            names.put(12L, "DEC");
            NAMES = Collections.unmodifiableMap(names);
        }
    }

    private static class FullTitleCase {  // Initialization-on-demand holder idiom.
        static final Map<Long, String> NAMES;

        static {
            final HashMap<Long, String> names = new HashMap<>();
            names.put(1L, "January");
            names.put(2L, "February");
            names.put(3L, "March");
            names.put(4L, "April");
            names.put(5L, "May");
            names.put(6L, "June");
            names.put(7L, "July");
            names.put(8L, "August");
            names.put(9L, "September");
            names.put(10L, "October");
            names.put(11L, "November");
            names.put(12L, "December");
            NAMES = Collections.unmodifiableMap(names);
        }
    }

    private static class FullUpperCase {  // Initialization-on-demand holder idiom.
        static final Map<Long, String> NAMES;

        static {
            final HashMap<Long, String> names = new HashMap<>();
            names.put(1L, "JANUARY");
            names.put(2L, "FEBRUARY");
            names.put(3L, "MARCH");
            names.put(4L, "APRIL");
            names.put(5L, "MAY");
            names.put(6L, "JUNE");
            names.put(7L, "JULY");
            names.put(8L, "AUGUST");
            names.put(9L, "SEPTEMBER");
            names.put(10L, "OCTOBER");
            names.put(11L, "NOVEMBER");
            names.put(12L, "DECEMBER");
            NAMES = Collections.unmodifiableMap(names);
        }
    }
}
