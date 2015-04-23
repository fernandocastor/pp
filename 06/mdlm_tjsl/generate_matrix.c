// gcc generate_matrix.c -o generate_matrix

#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
    int i, j, n = 3, m = 3, max_val = 50, min_val = -max_val;
    
    if (argc < 3) {
        fprintf(stderr, "Too few arguments\n");
        return 1;
    }

    n = atoi(argv[1]);
    m = atoi(argv[2]);
    if (argc == 4) {
        max_val = atoi(argv[3]);
        min_val = -max_val;
    } else if (argc > 4) {
        min_val = atoi(argv[3]);
        max_val = atoi(argv[4]);
    }
    
    if (min_val >= max_val) {
        fprintf(stderr, "The minumum value must be less than maximum value\n");
        return 1;
    }
    
    srand(time(NULL));
    fprintf(stdout, "%d %d\n", n, m);
    for (i = 0; i < n; ++i) {
        for (j = 0; j < m; ++j) {
            int r = (rand() % (max_val-min_val+1)) + min_val;
            fprintf(stdout, "%d ", r);
        }
        fprintf(stdout, "\n");
    }
    fprintf(stdout, "\n");
    return 0;
}

