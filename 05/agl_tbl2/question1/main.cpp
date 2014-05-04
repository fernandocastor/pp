#include "paralleldeque.h"
#include "node.h"
#include <stdio.h>
#include <string>
#include <thread>

ParallelDeque p;

void insertValues(int index, bool left)
{
    int max = ((index + 1) * 10) - 1;

    char* side = "left";
    if (!left)
        side = "right";
    for (int i = index; i <= max; ++i) {
        if (left)
            p.pushLeft(new Node(i));
        else
            p.pushRight(new Node(i));

        printf("pushed %d %s\n", i, side);
    }
}

void removeValues() {
    int i;
    Node* node;
    char* side;
    while (true) {
        i = rand() % 2;
        if (i) {
            node = p.popLeft();
            side = "left";
        } else {
            node = p.popRight();
            side = "right";
        }
        if (node)
            printf("poped %d %s\n", node->value(), side);
        else
            printf("poped null node!!\n");
    }
}

int main(int argc, char** argv)
{
    std::thread removeThread1(removeValues);
    std::thread removeThread2(removeValues);

    std::thread insertLeft1(insertValues, 0, true);
    std::thread insertLeft2(insertValues, 1, true);
    std::thread insertLeft3(insertValues, 2, true);
    std::thread insertLeft4(insertValues, 3, true);
    std::thread insertLeft5(insertValues, 4, true);

    std::thread insertRight1(insertValues, 5, false);
    std::thread insertRight2(insertValues, 6, false);
    std::thread insertRight3(insertValues, 7, false);
    std::thread insertRight4(insertValues, 8, false);
    std::thread insertRight5(insertValues, 9, false);

    insertLeft1.join();
    insertLeft2.join();
    insertLeft3.join();
    insertLeft4.join();
    insertLeft5.join();

    insertRight1.join();
    insertRight2.join();
    insertRight3.join();
    insertRight4.join();
    insertRight5.join();

    removeThread1.join();
    removeThread2.join();
    return 0;
}
