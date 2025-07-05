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

class DayOfWeekNames {
    private DayOfWeekNames() {
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
            names.put(1L, "Mon");
            names.put(2L, "Tue");
            names.put(3L, "Wed");
            names.put(4L, "Thu");
            names.put(5L, "Fri");
            names.put(6L, "Sat");
            names.put(7L, "Sun");
            NAMES = Collections.unmodifiableMap(names);
        }
    }

    private static class ShortUpperCase {  // Initialization-on-demand holder idiom.
        static final Map<Long, String> NAMES;

        static {
            final HashMap<Long, String> names = new HashMap<>();
            names.put(1L, "MON");
            names.put(2L, "TUE");
            names.put(3L, "WED");
            names.put(4L, "THU");
            names.put(5L, "FRI");
            names.put(6L, "SAT");
            names.put(7L, "SUN");
            NAMES = Collections.unmodifiableMap(names);
        }
    }

    private static class FullTitleCase {  // Initialization-on-demand holder idiom.
        static final Map<Long, String> NAMES;

        static {
            final HashMap<Long, String> names = new HashMap<>();
            names.put(1L, "Monday");
            names.put(2L, "Tuesday");
            names.put(3L, "Wednesday");
            names.put(4L, "Thursday");
            names.put(5L, "Friday");
            names.put(6L, "Saturday");
            names.put(7L, "Sunday");
            NAMES = Collections.unmodifiableMap(names);
        }
    }

    private static class FullUpperCase {  // Initialization-on-demand holder idiom.
        static final Map<Long, String> NAMES;

        static {
            final HashMap<Long, String> names = new HashMap<>();
            names.put(1L, "MONDAY");
            names.put(2L, "TUESDAY");
            names.put(3L, "WEDNESDAY");
            names.put(4L, "THURSDAY");
            names.put(5L, "FRIDAY");
            names.put(6L, "SATURDAY");
            names.put(7L, "SUNDAY");
            NAMES = Collections.unmodifiableMap(names);
        }
    }
}
