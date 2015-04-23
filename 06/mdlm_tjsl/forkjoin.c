// gcc forkjoin.c -o forkjoin
#include <stdlib.h>

#include <unistd.h>

#include <sys/types.h>
#include <sys/wait.h>

#include <errno.h>

#include <stdio.h>

void waitall();

int main(int argc, char *argv[]) {
    int n = 3; // number of rows
    int m = 3; // number of columns

    if (argc < 3) {
        fprintf(stderr, "Too few arguments\n");
        return 1;
    }
    int i, j;
    
    char *file_a = argv[1];
    char *file_b = argv[2];

    FILE *fda = fopen(file_a, "r");
    fscanf(fda, "%d %d", &n, &m);

    int m_b, p;
    FILE *fdb = fopen(file_b, "r");
    fscanf(fdb, "%d %d", &m_b, &p);

    if (m != m_b) {
        fprintf(stderr, "Invalid matrices\n");
        return 1;
    }

    int **matrix_a = malloc(sizeof(int*) * n);
    for (i = 0; i < n; i++) {
        matrix_a[i] = malloc(sizeof(int) * m);
        for (j = 0; j < m; j++) {
            int x;
            fscanf(fda, "%d", &x);
            matrix_a[i][j] = x;
        }
    }

    int **matrix_b = malloc(sizeof(int*) * m);
    for (i = 0; i < m; i++) {
        matrix_b[i] = malloc(sizeof(int) * p);
        for (j = 0; j < p; j++) {
            int x;
            fscanf(fdb, "%d", &x);
            matrix_b[i][j] = x;
        }
    }

    // Used to store the pid of each row
    pid_t *pids = malloc(sizeof(pid_t) * n);
    pid_t pid;
    int root_pid = getpid();
    

    for (i = 0; i < n; i++) {
        pid = fork();
        if (pid <= 0)
            break;
        pids[i] = pid;
    }

    if (pid == 0) {
        char filepath[15];
        sprintf(filepath, "%d.row", getpid());
        FILE *fd = fopen(filepath, "w");
        for (j = 0; j < p; ++j) {
            int c = 0;
            for (i = 0; i < m; ++i) {
                c += (matrix_a[getpid() - root_pid - 1][i] * matrix_b[i][j]);
            }
            fprintf(fd, "%d ", c);
        }
        fclose(fd);
    } else if (pid < 0) {
        perror("fork\n");
        exit(-1);
    } else {
        waitall();
        char filepath[15];
        size_t bufsize = 255;
        char content[bufsize];
        for (i = 0; i < n; i++) {
            // For each row, read the entire row file and print to stdout
            sprintf(filepath, "%d.row", pids[i]);
            FILE *fd = fopen(filepath, "r");
            for (;;) {
                size_t n = fread(content, 1, bufsize-1, fd);
                content[n] = 0;
                printf("%s", content);
                if (n < bufsize-1) {
                    printf("\n");
                    break;
                }
            }
            fclose(fd);
            unlink(filepath);
        }
    }

    return 0;
}

void waitall() {
    pid_t pid;
    int status;

    for (;;) {
        pid = wait(&status);
        if (pid == -1) {
            if (errno == ECHILD)
                break;
            perror("wait\n");
            exit(-1);
        }
    }
}
