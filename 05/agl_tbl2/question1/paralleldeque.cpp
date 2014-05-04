#include "paralleldeque.h"

void ParallelDeque::pushLeft(Node* node)
{
    m_leftMutex.lock();
    m_leftDeque.pushLeft(node);
    m_leftMutex.unlock();
}

Node* ParallelDeque::popLeft()
{
    m_leftMutex.lock();
    Node* node = m_leftDeque.popLeft();
    if (!node) {
        m_rightMutex.lock();
        node = m_rightDeque.popLeft();
        balanceToLeft();
        m_rightMutex.unlock();
    }
    m_leftMutex.unlock();
    return node;
}

void ParallelDeque::pushRight(Node* node)
{
    m_rightMutex.lock();
    m_rightDeque.pushRight(node);
    m_rightMutex.unlock();
}

Node* ParallelDeque::popRight()
{
    m_rightMutex.lock();
    Node* node = m_rightDeque.popRight();
    if (!node) {
        m_rightMutex.unlock();
        m_leftMutex.lock();
        m_rightMutex.lock();
        node = m_rightDeque.popRight();
        if (!node) {
            node = m_rightDeque.popLeft();
            balanceToLeft();
        }
        m_leftMutex.unlock();
    }
    m_rightMutex.unlock();
    return node;
}

void ParallelDeque::balanceToLeft()
{
    if (!m_rightDeque.size())
        return;

    unsigned halfSize = m_rightDeque.size() / 2;
    for (int i = 0; i < halfSize; ++i)
        m_leftDeque.pushRight(m_rightDeque.popLeft());
}

void ParallelDeque::balanceToRight()
{
    if (!m_leftDeque.size())
        return;

    unsigned halfSize = m_leftDeque.size() / 2;
    for (int i = 0; i < halfSize; ++i)
        m_rightDeque.pushLeft(m_leftDeque.popRight());
}

