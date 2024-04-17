#include <locale.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main(int argc, char **argv) {
  char s[8192];
  struct tm tm;

  if (argc <= 11) {
    fprintf(stderr, "ERROR: too few arguments.\n");
    return -1;
  }

  setlocale(LC_TIME, argv[11]);
  setlocale(LC_ALL, argv[11]);

  tm.tm_sec = atoi(argv[7]);
  tm.tm_min = atoi(argv[6]);
  tm.tm_hour = atoi(argv[5]);
  tm.tm_mday = atoi(argv[4]);
  tm.tm_mon = atoi(argv[3]) - 1;
  tm.tm_year = atoi(argv[2]) - 1900;
  tm.tm_wday = atoi(argv[8]);
  tm.tm_yday = atoi(argv[9]) - 1;
  tm.tm_isdst = atoi(argv[10]);

  if (strftime(s, sizeof(s) - 1, argv[1], &tm) == 0) {
    fprintf(stderr, "ERROR: strftime returned 0.\n");
    return -1;
  }

  printf("%s\n", s);
  return 0;
}
