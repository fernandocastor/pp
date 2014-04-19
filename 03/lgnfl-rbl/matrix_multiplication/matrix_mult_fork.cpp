#include <cstdlib>
#include <cstdio>
#include <cassert>
#include <algorithm>
#include <sys/wait.h>
#include <unistd.h>
#include <sys/mman.h>

using namespace std;

#define MAXN 1001
#define MOD 1000003

int A[MAXN][MAXN];
int B[MAXN][MAXN];
int *C;
int jobs = 8;

int main(int argc, char *argv[]) {
  freopen("A.txt", "r", stdin);
  int rowsA, rowsB, colsA, colsB;
  scanf("%d%d",&rowsA,&colsA);
  for (int i = 0; i < rowsA; ++i)
    for (int j = 0; j < colsA; ++j)
      scanf("%d", &A[i][j]);

  freopen("B.txt", "r", stdin);
  scanf("%d%d",&rowsB,&colsB);
  for (int i = 0; i < rowsB; ++i)
    for (int j = 0; j < colsB; ++j)
      scanf("%d", &B[i][j]);

  assert(colsA == rowsB);
  int rowsC = rowsA, colsC = colsB;
  C = (int *)mmap(NULL, MAXN * MAXN * sizeof(int),
    PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
  int children = 0;
  int sizePerJob = max(rowsC / jobs, 1);
  int jobStart = 0;
  for (int z = 0; z < jobs; ++z) {
    if (jobStart == rowsC) break;
    int jobEnd = min(jobStart + sizePerJob, rowsC);
    if (z == jobs-1) jobEnd = rowsC;
    ++children;
    int pid = fork();
    if (pid < 0) {
      fprintf(stderr, "error: forking\n");
      return 1;
    } else if (pid == 0) {
      // child start heres
      int i0 = jobStart, i1 = jobEnd, jNum = z;
      for (int i = i0; i < i1; ++i) {
        for (int j = 0; j < colsC; ++j) {
          int index = i*MAXN + j;
          C[index] = 0;
          for (int k = 0; k < colsA; ++k) {
            C[index] += (A[i][k] * B[k][j]) % MOD;
            C[index] %= MOD;
          }
        }
      }
      return 0;
    }
    jobStart = jobEnd;
  }

  int status;
  while (children) {
    int pid = waitpid(-1, &status, 0);
    --children;
  }

  for (int i = 0; i < rowsC; ++i) {
    for (int j = 0; j < colsC; ++j) {
      if (j) printf(" ");
      int index = i*MAXN + j;
      printf("%d", C[index]);
    }
    printf("\n");
  }
  munmap(C, MAXN * MAXN * sizeof(int));
  return 0;
}
