#ifndef PARALLELDEQUE_H
#define PARALLELDEQUE_H

#include "deque.h"
#include <mutex>

class ParallelDeque {
public:
    void pushLeft(Node*);
    Node* popLeft();

    void pushRight(Node*);
    Node* popRight();

private:
    void balanceToLeft();
    void balanceToRight();
    std::mutex m_leftMutex;
    std::mutex m_rightMutex;
    Deque m_leftDeque;
    Deque m_rightDeque;
};

#endif // PARALLELDEQUE_H

