#include "paralleldeque.h"

int ParallelDeque::moveLeft(int index)
{
    return index == 0 ? m_numberOfBuckets - 1 : index - 1;
}

int ParallelDeque::moveRight(int index)
{
    return index == m_numberOfBuckets - 1 ? 0 : index + 1;
}

void ParallelDeque::pushLeft(Node* node)
{
    m_leftMutex.lock();
    m_deques[m_leftIndex].pushLeft(node);
    m_leftIndex = moveLeft(m_leftIndex);
    m_leftMutex.unlock();
}

Node* ParallelDeque::popLeft()
{
    m_leftMutex.lock();
    int index = moveRight(m_leftIndex);
    Node* node = m_deques[index].popLeft();
    if (node)
        m_leftIndex = index;
    m_leftMutex.unlock();
    return node;
}

void ParallelDeque::pushRight(Node* node)
{
    m_leftMutex.lock();
    m_deques[m_rightIndex].pushRight(node);
    m_rightIndex = moveRight(m_rightIndex);
    m_leftMutex.unlock();
}

Node* ParallelDeque::popRight()
{
    m_leftMutex.lock();
    int index = moveLeft(m_rightIndex);
    Node* node = m_deques[index].popRight();
    if (node)
        m_rightIndex = index;
    m_leftMutex.unlock();
    return node;
}

unsigned ParallelDeque::size() const
{
    unsigned size = 0;
    for (int i = 0; i < m_numberOfBuckets; ++i)
        size += m_deques[i].size();

    return size;
}

