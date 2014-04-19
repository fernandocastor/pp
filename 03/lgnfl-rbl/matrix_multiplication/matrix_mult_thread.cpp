#include <cstdlib>
#include <cstdio>
#include <cassert>
#include <algorithm>
#include <thread>

using namespace std;

#define MAXN 1001
#define MOD 1000003
#define JOBS 8

int A[MAXN][MAXN];
int B[MAXN][MAXN];
int C[MAXN][MAXN];
int rowsA, rowsB, rowsC, colsA, colsB, colsC;
int children;
thread threads[JOBS];

void thread_loop(int jobNum, int jobStart, int jobEnd) {
  for (int i = jobStart; i < jobEnd; ++i) {
    for (int j = 0; j < colsC; ++j) {
      C[i][j] = 0;
      for (int k = 0; k < colsA; ++k) {
        C[i][j] += (A[i][k] * B[k][j]) % MOD;
        C[i][j] %= MOD;
      }
    }
  }
}

int main(int argc, char *argv[]) {
  freopen("A.txt", "r", stdin);
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
  rowsC = rowsA;
  colsC = colsB;
  
  int sizePerJob = max(rowsC / JOBS, 1);
  int jobStart = 0;
  for (int z = 0; z < JOBS; ++z) {
    if (jobStart == rowsC) break;
    int jobEnd = min(jobStart + sizePerJob, rowsC);
    if (z == JOBS-1) jobEnd = rowsC;

    threads[children++] = thread(thread_loop, z, jobStart, jobEnd);

    jobStart = jobEnd;
  }

  // wait for children threads to finish
  while (children) {
    threads[children-1].join();
    --children;
  }

  for (int i = 0; i < rowsC; ++i) {
    for (int j = 0; j < colsC; ++j) {
      if (j) printf(" ");
      printf("%d", C[i][j]);
    }
    printf("\n");
  }
  return 0;
}
