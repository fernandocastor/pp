#include <stdio.h>
#include <stdlib.h>
#include "table.h"

int main(int argc, char** argv) {
    unsigned philosophers = 3;
    if (argc >= 2) {
        philosophers = atoi(argv[1]);
        if (philosophers <= 0)
            philosophers = 3;
    }

    printf("Starting philosophers dinner with %d philosophers!\n\n", philosophers);
    Table* table = new Table(philosophers);
    return 0;
}

