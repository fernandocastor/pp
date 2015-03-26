CC = gcc -std=c99
# compiler flags:
# -g adds debugging information to the executable file
# -Wall turns on most, but not all, compiler warnings
# -Wextra turn on extra flags that are not enabled by -Wall
# -Werror make all warnings into errors.
CFLAGS = -g -Wall -Wextra -Werror

ifeq ($(OS), Windows_NT)
LDFLAGS =
else
LDFLAGS = -pthread
endif

all:
	$(CC) $(LDFLAGS) dpp1.c -o dpp1
	$(CC) $(LDFLAGS) dpp2.c -o dpp2
	$(CC) $(LDFLAGS) dpp3.c -o dpp3
	$(CC) $(LDFLAGS) dpp4.c -o dpp4

.PHONY: clean

clean:
	rm -f dpp1 dpp2 dpp3 dpp4
