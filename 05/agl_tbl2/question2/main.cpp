#include "paralleldeque.h"
#include "node.h"
#include <stdio.h>
#include <string>
#include <thread>

ParallelDeque* p;
bool insertionThreadsDone = false;

void insertValues(int index, bool left)
{
    int max = ((index + 1) * 10000) - 1;

    char* side = "left";
    if (!left)
        side = "right";
    for (int i = index; i <= max; ++i) {
        if (left)
            p->pushLeft(new Node(i));
        else
            p->pushRight(new Node(i));

    }
}

void removeValues(bool left) {
    int i;
    Node* node;
    char* side = "left";
    if (!left)
        side = "right";
    while (!insertionThreadsDone || p->size()) {
        if (left)
            node = p->popLeft();
        else
            node = p->popRight();
    }
}

int main(int argc, char** argv)
{
    if (argc < 2) {
        printf("Please specify the number of buckets\n");
        return 1;
    }

    p = new ParallelDeque(atoi(argv[1]));

    std::thread removeThread1(removeValues, true);
    std::thread removeThread2(removeValues, false);

    std::thread insertLeft1(insertValues, 0, true);
    std::thread insertLeft2(insertValues, 1, true);
    std::thread insertLeft3(insertValues, 2, true);
    std::thread insertLeft4(insertValues, 3, true);
    std::thread insertLeft5(insertValues, 4, true);
    std::thread insertLeft6(insertValues, 0, true);
    std::thread insertLeft7(insertValues, 1, true);
    std::thread insertLeft8(insertValues, 2, true);
    std::thread insertLeft9(insertValues, 3, true);
    std::thread insertLeft10(insertValues, 4, true);

    std::thread insertRight1(insertValues, 5, false);
    std::thread insertRight2(insertValues, 6, false);
    std::thread insertRight3(insertValues, 7, false);
    std::thread insertRight4(insertValues, 8, false);
    std::thread insertRight5(insertValues, 9, false);
    std::thread insertRight6(insertValues, 5, false);
    std::thread insertRight7(insertValues, 6, false);
    std::thread insertRight8(insertValues, 7, false);
    std::thread insertRight9(insertValues, 8, false);
    std::thread insertRight10(insertValues, 9, false);

    insertLeft1.join();
    insertLeft2.join();
    insertLeft3.join();
    insertLeft4.join();
    insertLeft5.join();
    insertLeft6.join();
    insertLeft7.join();
    insertLeft8.join();
    insertLeft9.join();
    insertLeft10.join();

    insertRight1.join();
    insertRight2.join();
    insertRight3.join();
    insertRight4.join();
    insertRight5.join();
    insertRight6.join();
    insertRight7.join();
    insertRight8.join();
    insertRight9.join();
    insertRight10.join();

    insertionThreadsDone = true;

    removeThread1.join();
    removeThread2.join();
    return 0;
}
