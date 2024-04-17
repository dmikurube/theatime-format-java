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

import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Objects;

/**
 * @see <a href="https://pubs.opengroup.org/onlinepubs/009695399/functions/strftime.html">strftime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html">strftime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/007904875/functions/strptime.html">strptime - The Open Group Base Specifications Issue 6 IEEE Std 1003.1, 2004 Edition</a>
 * @see <a href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strptime.html">strptime - The Open Group Base Specifications Issue 7, 2018 edition IEEE Std 1003.1-2017 (Revision of IEEE Std 1003.1-2008)</a>
 */
public interface Specification {
    String originalFormat();
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
abstract class AbstractSpecification implements Specification {
    AbstractSpecification(final Context context) {
        this.upperCase = context.upperCase;
        this.changeCase = context.changeCase;
        this.precision = context.precision;
        this.colons = context.colons;
        this.padding = context.padding;
        this.modifier = context.modifier;

        this.originalEntireFormat = context.originalEntireFormat;
        this.start = context.start;
        this.end = context.end;
    }

    public final String originalFormat() {
        return this.originalEntireFormat.substring(this.start, this.end);
    }

    abstract DateTimeFormatterBuilder appendTo(DateTimeFormatterBuilder formatter);

    static class Context {
        Context(final boolean upperCase,
                final boolean changeCase,
                final int precision,
                final int colons,
                final char padding,
                final char modifier,
                final String originalEntireFormat,
                final int start,
                final int end) {
            this.upperCase = upperCase;
            this.changeCase = changeCase;
            this.precision = precision;
            this.colons = colons;
            this.padding = padding;
            this.modifier = modifier;
            this.originalEntireFormat = originalEntireFormat;
            this.start = start;
            this.end = end;
        }

        final boolean upperCase;
        final boolean changeCase;
        final int precision;
        final int colons;
        final char padding;
        final char modifier;

        final String originalEntireFormat;
        final int start;
        final int end;
    }

    static class ContextSource {
        ContextSource(final String originalEntireFormat) {
            this.originalEntireFormat = originalEntireFormat;
            this.upperCase = false;
            this.changeCase = false;
            this.precision = -1;
            this.colons = -1;
            this.padding = '\0';
            this.modifier = '\0';
        }

        ContextSource upperCase() {
            this.upperCase = true;
            return this;
        }

        ContextSource changeCase() {
            this.changeCase = true;
            return this;
        }

        ContextSource precision(final int precision) {
            this.precision = precision;
            return this;
        }

        ContextSource colons(final int colons) {
            this.colons = colons;
            return this;
        }

        ContextSource padding(final char padding) {
            this.padding = padding;
            return this;
        }

        ContextSource modifier(final char modifier) {
            this.modifier = modifier;
            return this;
        }

        Context at(final int start, final int end) {
            return new Context(
                    this.upperCase,
                    this.changeCase,
                    this.precision,
                    this.colons,
                    this.padding,
                    this.modifier,
                    this.originalEntireFormat,
                    start,
                    end);
        }

        private final String originalEntireFormat;

        private boolean upperCase;
        private boolean changeCase;
        private int precision;
        private int colons;
        private char padding;
        private char modifier;
    }

    static final Context EMPTY = new Context(false, false, -1, -1, '\0', '\0', "", 0, 0);

    final boolean upperCase;
    final boolean changeCase;
    final int precision;
    final int colons;
    final char padding;
    final char modifier;

    final String originalEntireFormat;
    private final int start;
    private final int end;
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
abstract class ConversionSpecification extends AbstractSpecification {
    ConversionSpecification(
            final char conversionChar,
            final ConversionType conversionType,
            final Context context) {
        super(context);
        this.conversionChar = conversionChar;
        this.conversionType = conversionType;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof ConversionSpecification)) {
            return false;
        }

        final ConversionSpecification other = (ConversionSpecification) otherObject;
        return Objects.equals(this.getClass(), other.getClass())
                && Objects.equals(this.conversionChar, other.conversionChar)
                && Objects.equals(this.conversionType, other.conversionType)
                && Objects.equals(this.upperCase, other.upperCase)
                && Objects.equals(this.changeCase, other.changeCase)
                && Objects.equals(this.precision, other.precision)
                && Objects.equals(this.colons, other.colons)
                && Objects.equals(this.padding, other.padding)
                && Objects.equals(this.modifier, other.modifier)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getClass(),
                this.conversionChar,
                this.conversionType,
                this.upperCase,
                this.changeCase,
                this.precision,
                this.colons,
                this.padding,
                this.modifier);
    }

    public String explain() {
        final StringBuilder builder = new StringBuilder().append("<").append(this.originalEntireFormat);
        if (this.upperCase) {
            builder.append(":uppercase");
        }
        if (this.changeCase) {
            builder.append(":changecase");
        }
        if (this.precision >= 0) {
            builder.append(":precision=").append(this.precision);
        }
        if (this.colons >= 0) {
            builder.append(":colons=").append(this.colons);
        }
        if (this.padding != '\0') {
            builder.append(":padding=").append(this.padding);
        }
        if (this.modifier != '\0') {
            builder.append(":modifier=").append(this.modifier);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder().append("%");

        if (this.upperCase) {
            builder.append("^");
        }

        if (this.changeCase) {
            builder.append("#");
        }

        if (this.padding == '\0') {
            // Pass-through.
        } else if (this.padding == '0') {
            builder.append("0");
        } else if (this.padding == ' ') {
            builder.append("_");
        } else {
            throw new IllegalStateException("Illegal padding: " + this.padding);
        }

        if (this.precision >= 0) {
            builder.append(this.precision);
        }

        if (this.colons >= 0) {
            for (int i = 0; i < this.colons; i++) {
                builder.append(':');
            }
        }
        if (this.modifier == '\0') {
            // Pass-through.
        } else if (this.padding == 'E') {
            builder.append('E');
        } else if (this.padding == '0') {
            builder.append('O');
        } else {
            throw new IllegalStateException("Illegal modifier: " + this.modifier);
        }

        builder.append(this.conversionChar);

        return builder.toString();
    }

    final char conversionChar;
    final ConversionType conversionType;
}

/**
 * <ul>
 * <li><code>strftime</code> - Replaced by the locale's abbreviated weekday name. [tm_wday]
 * <li><code>strptime</code> - The day of the week, using the locale's weekday names; either the abbreviated or full name may be specified.
 * </ul>
 */
@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerA extends ConversionSpecification {
    LowerA(final Context context) {
        super('a', ConversionType.DAY_OF_WEEK_NAME_ABBREVIATED, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        if (this.padding == '0' || this.upperCase || this.changeCase) {
            throw new IllegalStateException();
        }
        if (this.precision >= 0) {
            formatter.padNext(this.precision, (this.padding == '\0' ? ' ' : this.padding));
        }
        return formatter.appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperA extends ConversionSpecification {
    UpperA(final Context context) {
        super('A', ConversionType.DAY_OF_WEEK_NAME_FULL, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        if (this.padding == '0' || this.upperCase || this.changeCase) {
            throw new IllegalStateException();
        }
        if (this.precision >= 0) {
            formatter.padNext(this.precision, (this.padding == '\0' ? ' ' : this.padding));
        }
        return formatter.appendText(ChronoField.DAY_OF_WEEK, TextStyle.FULL);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerB extends ConversionSpecification {
    LowerB(final Context context) {
        super('b', ConversionType.MONTH_NAME_ABBREVIATED, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        if (this.padding == '0' || this.upperCase || this.changeCase) {
            throw new IllegalStateException();
        }
        if (this.precision >= 0) {
            formatter.padNext(this.precision, (this.padding == '\0' ? ' ' : this.padding));
        }
        return formatter.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperB extends ConversionSpecification {
    UpperB(final Context context) {
        super('B', ConversionType.MONTH_NAME_FULL, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        if (this.padding == '0' || this.upperCase || this.changeCase) {
            throw new IllegalStateException();
        }
        if (this.precision >= 0) {
            formatter.padNext(this.precision, (this.padding == '\0' ? ' ' : this.padding));
        }
        return formatter.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerC extends ConversionSpecification {
    LowerC(final Context context) {
        super('c', ConversionType.DATE_AND_TIME, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        // In the C or POSIX locale, the E and O modifiers are ignored and
        // the replacement strings for the following specifiers are:
        //
        // ...
        //   ...
        // %c
        //   Equivalent to %a %b %e %T %Y.
        //
        // strftime, strftime_l - convert date and time to a string
        // The Open Group Base Specifications Issue 7, 2018 edition
        // https://pubs.opengroup.org/onlinepubs/9699919799/functions/strftime.html#tag_16_576_07
        if (this.padding == '0' || this.upperCase || this.changeCase) {
            throw new IllegalStateException();
        }
        return formatter
                .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT)
                .appendLiteral(" ")
                .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
                .appendLiteral(" ")
                .padNext(2, ' ')
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral(" ")
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(":")
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(":")
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendLiteral(" ")
                .appendValue(ChronoField.YEAR, 4, 19, SignStyle.NORMAL);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperC extends ConversionSpecification {
    UpperC(final Context context) {
        super('C', ConversionType.CENTURY, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerD extends ConversionSpecification {
    LowerD(final Context context) {
        super('d', ConversionType.DAY_OF_MONTH_ZERO, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperD extends ConversionSpecification {
    UpperD(final Context context) {
        super('D', ConversionType.DATE_MDY, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerE extends ConversionSpecification {
    LowerE(final Context context) {
        super('e', ConversionType.DAY_OF_MONTH_SPACE, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperF extends ConversionSpecification {
    UpperF(final Context context) {
        super('F', ConversionType.YEAR_MONTH_DAY, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerG extends ConversionSpecification {
    LowerG(final Context context) {
        super('g', ConversionType.WEEK_BASED_YEAR_OFFSET, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperG extends ConversionSpecification {
    UpperG(final Context context) {
        super('G', ConversionType.WEEK_BASED_YEAR_FULL, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerH extends ConversionSpecification {
    LowerH(final Context context) {
        super('h', ConversionType.MONTH_NAME_ABBREVIATED_ALIAS, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperH extends ConversionSpecification {
    UpperH(final Context context) {
        super('H', ConversionType.HOUR_24, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperI extends ConversionSpecification {
    UpperI(final Context context) {
        super('I', ConversionType.HOUR_12, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerJ extends ConversionSpecification {
    LowerJ(final Context context) {
        super('j', ConversionType.DAY_OF_YEAR, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerM extends ConversionSpecification {
    LowerM(final Context context) {
        super('m', ConversionType.MONTH, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperM extends ConversionSpecification {
    UpperM(final Context context) {
        super('M', ConversionType.MINUTE, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerN extends ConversionSpecification {
    LowerN(final Context context) {
        super('n', ConversionType.IMMEDIATE_WHITESPACE_NEWLINE, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerP extends ConversionSpecification {
    LowerP(final Context context) {
        super('p', ConversionType.AMPM, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerR extends ConversionSpecification {
    LowerR(final Context context) {
        super('r', ConversionType.TIME_12_AMPM, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperR extends ConversionSpecification {
    UpperR(final Context context) {
        super('R', ConversionType.HOUR_MINUTE_24, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperS extends ConversionSpecification {
    UpperS(final Context context) {
        super('S', ConversionType.SECOND, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerT extends ConversionSpecification {
    LowerT(final Context context) {
        super('t', ConversionType.IMMEDIATE_WHITESPACE_TAB, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperT extends ConversionSpecification {
    UpperT(final Context context) {
        super('T', ConversionType.TIME_24, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerU extends ConversionSpecification {
    LowerU(final Context context) {
        super('u', ConversionType.DAY_OF_WEEK_1_7, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperU extends ConversionSpecification {
    UpperU(final Context context) {
        super('U', ConversionType.WEEK_NUMBER_OF_YEAR_SUNDAY_0, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperV extends ConversionSpecification {
    UpperV(final Context context) {
        super('V', ConversionType.WEEK_NUMBER_OF_YEAR_MONDAY_1, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerW extends ConversionSpecification {
    LowerW(final Context context) {
        super('w', ConversionType.DAY_OF_WEEK_0_6, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperW extends ConversionSpecification {
    UpperW(final Context context) {
        super('W', ConversionType.WEEK_NUMBER_OF_YEAR_MONDAY_0, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerX extends ConversionSpecification {
    LowerX(final Context context) {
        super('x', ConversionType.DATE_LOCALE, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperX extends ConversionSpecification {
    UpperX(final Context context) {
        super('X', ConversionType.TIME_LOCALE, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerY extends ConversionSpecification {
    LowerY(final Context context) {
        super('y', ConversionType.YEAR_TWO_DIGITS, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperY extends ConversionSpecification {
    UpperY(final Context context) {
        super('Y', ConversionType.YEAR, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class LowerZ extends ConversionSpecification {
    LowerZ(final Context context) {
        super('z', ConversionType.ZONE, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class UpperZ extends ConversionSpecification {
    UpperZ(final Context context) {
        super('Z', ConversionType.ZONE_NAME, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class Plus extends ConversionSpecification {
    Plus(final Context context) {
        super('+', null, context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class Literal extends AbstractSpecification {
    private Literal(final String literal, final Context context) {
        super(context);
        this.literal = literal;
    }

    static Literal of(final String immediate, final Context context) {
        return new Literal(immediate, context);
    }

    static Literal ofPercent(final Context context) {
        return new Literal("%", context);
    }

    static Literal ofNewline(final Context context) {
        return new Literal("\n", context);
    }

    static Literal ofTab(final Context context) {
        return new Literal("\t", context);
    }

    @Override
    DateTimeFormatterBuilder appendTo(final DateTimeFormatterBuilder formatter) {
        return formatter.appendLiteral(this.literal);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Literal)) {
            return false;
        }

        final Literal other = (Literal) otherObject;
        return Objects.equals(this.literal, other.literal);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.literal);
    }

    @Override
    public final String toString() {
        return this.literal;
    }

    private final String literal;
}
