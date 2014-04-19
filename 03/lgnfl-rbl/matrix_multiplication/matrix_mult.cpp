#include <cstdlib>
#include <cstdio>
#include <cassert>

using namespace std;

#define MAXN 1001
#define MOD 1000003

int A[MAXN][MAXN];
int B[MAXN][MAXN];
int C[MAXN][MAXN];

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
  int i0 = 0, i1 = rowsC;
  if (argc > 1) {
    sscanf(argv[1], "%d", &i0);
    sscanf(argv[2], "%d", &i1);
  }
  for (int i = i0; i < i1; ++i) {
    for (int j = 0; j < colsC; ++j) {
      C[i][j] = 0;
      for (int k = 0; k < colsA; ++k) {
        C[i][j] += (A[i][k] * B[k][j]) % MOD;
        C[i][j] %= MOD;
      }
    }
  }


  for (int i = i0; i < i1; ++i) {
    for (int j = 0; j < colsC; ++j) {
      if (j) printf(" ");
      printf("%d", C[i][j]);
    }
    printf("\n");
  }
  return 0;
}
