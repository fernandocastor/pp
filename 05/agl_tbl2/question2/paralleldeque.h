#ifndef PARALLELDEQUE_H
#define PARALLELDEQUE_H

#include "deque.h"
#include <mutex>

class ParallelDeque {
public:
    ParallelDeque(int numberOfBuckets)
        : m_numberOfBuckets(numberOfBuckets)
        , m_leftIndex(0)
        , m_rightIndex(1)
    {
        m_deques = new Deque[m_numberOfBuckets];
    }

    void pushLeft(Node*);
    Node* popLeft();

    void pushRight(Node*);
    Node* popRight();

private:
    int moveLeft(int index);
    int moveRight(int index);
    Deque* m_deques;
    int m_numberOfBuckets;
    int m_leftIndex;
    int m_rightIndex;
    std::mutex m_leftMutex;
    std::mutex m_rightMutex;
};

#endif // PARALLELDEQUE_H

