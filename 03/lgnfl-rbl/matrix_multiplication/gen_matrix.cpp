#include <cstdio>
#include <cstdlib>
#include <ctime>

using namespace std;

int main() {
  int R = 1000, C = 1000;
  printf("%d %d\n", R, C);
  for (int i = 0; i < R; ++i) {
    for (int j = 0; j < C; ++j) {
      if (j) printf(" ");
      printf("%d", (rand() % 100) + 1);
    }
    printf("\n");
  }
  return 0;
}
