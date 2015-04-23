// gcc row_matrix_mult.c -o row_matrix_mult

#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
    int i, j, m, p, c, *row_a, **matrix_b;
    
    if (argc < 2) {
        fprintf(stderr, "Too few arguments\n");
        return 1;
    }

    char *file_b = argv[1];
    FILE *fdb = fopen(file_b, "r");
    
    fscanf(fdb, "%d%d", &m, &p);
    row_a = malloc(sizeof(double) * m);
    matrix_b = malloc(sizeof(double*) * m);
    for (i = 0; i < m; ++i) {
        fscanf(stdin, "%d", &row_a[i]);
    }

    for (i = 0; i < m; ++i) {
        matrix_b[i] = malloc(sizeof(double) * p);
        for (j = 0; j < p; ++j) {
            fscanf(fdb, "%d", &matrix_b[i][j]);
        }
    }

    for (j = 0; j < p; ++j) {
        c = 0;
        for (i = 0; i < m; ++i) {
            c += (row_a[i] * matrix_b[i][j]);
        }
        fprintf(stdout, "%d ", c);
    }
    fprintf(stdout, "\n");
    return 0;
}

