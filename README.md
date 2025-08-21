theatime-format-java
=====================

A date-time formatter to/from a string that aims at:

* extending the JSR 310: Date and Time API
* formatting flexibly not only with ISO 8601, but also with POSIX strftime/strptime
* being explicit and stable with locales and timezone

Example Code
-------------

```java
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.theatime.format.posix.PosixTimeFormat;

final DateTimeFormatter formatter = PosixTimeFormat.compile("%Y-%m-%d %H:%M:%S").toDateTimeFormatter();
final String formatted = formatter.format(ZonedDateTime.of(2023, 12, 31, 12, 34, 56, 123456789, ZoneOffset.UTC));

assert "2023-12-31 12:34:56".equals(formatted);
```
