#define _XOPEN_SOURCE  // Required for strptime
#include <locale.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

int main(int argc, char **argv) {
  struct tm tm;
  char *remaining;

  if (argc < 4) {
    fprintf(stderr, "ERROR: too few arguments.\n");
    return -1;
  }

  setlocale(LC_TIME, argv[3]);
  setlocale(LC_ALL, argv[3]);

  tm.tm_year = -1 - 1900;
  tm.tm_mon = -1 - 1;
  tm.tm_mday = -1;
  tm.tm_hour = -1;
  tm.tm_min = -1;
  tm.tm_sec = -1;
  tm.tm_wday = -1;
  tm.tm_yday = -1;
  tm.tm_isdst = -1;

  remaining = strptime(argv[1], argv[2], &tm);
  if (remaining == NULL) {
    fprintf(stderr, "ERROR: strptime failed to parse input.\n");
    return -1;
  }

  printf("%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%s\n",
         tm.tm_year + 1900,
         tm.tm_mon + 1,
         tm.tm_mday,
         tm.tm_hour,
         tm.tm_min,
         tm.tm_sec,
         tm.tm_wday,
         tm.tm_yday + 1,
         tm.tm_isdst,
         remaining ? remaining : "");

  return 0;
}
