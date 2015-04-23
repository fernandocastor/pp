// g++ threads.c -pthread -o threads
#include <pthread.h>

#include <stdio.h>
#include <stdlib.h>

#include <map>

void *compute(void *args);

int m, p;
int **matrix_a, **matrix_b;
std::map<int, int> mids;

int main(int argc, char *argv[]) {
    int n = 3; // number of rows
    m = 3; // number of columns
    int i, j;

    if (argc < 3) {
        fprintf(stderr, "Too few arguments\n");
        return 1;
    }

    char *file_a = argv[1];
    char *file_b = argv[2];

    FILE *fda = fopen(file_a, "r");
    fscanf(fda, "%d %d", &n, &m);

    int m_b;
    FILE *fdb = fopen(file_b, "r");
    fscanf(fdb, "%d %d", &m_b, &p);

    if (m != m_b) {
        fprintf(stderr, "Invalid matrices\n");
        return 1;
    }

    matrix_a = (int**) malloc(sizeof(int*) * n);
    for (i = 0; i < n; i++) {
        matrix_a[i] = (int*) malloc(sizeof(int) * m);
        for (j = 0; j < m; j++) {
            int x;
            fscanf(fda, "%d", &x);
            matrix_a[i][j] = x;
        }
    }

    matrix_b = (int**) malloc(sizeof(int*) * m);
    for (i = 0; i < m; i++) {
        matrix_b[i] = (int*) malloc(sizeof(int) * p);
        for (j = 0; j < p; j++) {
            int x;
            fscanf(fdb, "%d", &x);
            matrix_b[i][j] = x;
        }
    }

    // Used to store the tid of each row
    pthread_t *tids = (pthread_t*) malloc(sizeof(pthread_t) * n);
    int **matrix = (int**) malloc(sizeof(int*) * n);

    for (i = 0; i < n; i++) {
        matrix[i] = (int*) malloc(sizeof(int) * p);
        pthread_t tid;
        if (pthread_create(&tid, NULL, compute, matrix[i]) != 0) {
            perror("pthread_create\n");
            exit(-1);
        }
        tids[i] = tid;
        mids[tid] = i;
    }

    for (i = 0; i < n; i++) {
        void *vp;
        if (pthread_join(tids[i], &vp) != 0) {
            perror("pthread_join\n");
            exit(-1);
        }

        int j;
        for (j = 0; j < p; j++) {
            printf("%d ", matrix[i][j]);
        }
        printf("\n");
    }

    return 0;
}

void *compute(void *args) {
    int *elements = (int*) args;
    int i, j, id = mids[pthread_self()];
    
    for (j = 0; j < p; ++j) {
        int c = 0;
        for (i = 0; i < m; ++i) {
            c += (matrix_a[id][i] * matrix_b[i][j]);
        }
        elements[j] = c;
    }
    
    return NULL;
}
