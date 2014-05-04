#include "deque.h"
#include "node.h"

void Deque::pushLeft(Node* node)
{
    node->setNext(m_left);
    if (m_left)
        m_left->setPrevious(node);
    else
        m_right = node;

    m_left = node;
    ++m_size;
}

void Deque::pushRight(Node* node)
{
    node->setPrevious(m_right);
    if (m_right)
        m_right->setNext(node);
    else
        m_left = node;

    m_right = node;
    ++m_size;
}

Node* Deque::popLeft()
{
    if (!m_size)
        return nullptr;

    Node* returnNode = m_left;
    m_left = m_left->next();
    returnNode->setNext(nullptr);
    if (m_left)
        m_left->setPrevious(nullptr);
    else
        m_right = nullptr;

    --m_size;
    return returnNode;
}

Node* Deque::popRight()
{
    if (!m_size)
        return nullptr;

    Node* returnNode = m_right;
    m_right = m_right->previous();
    returnNode->setPrevious(nullptr);
    if (m_right)
        m_right->setNext(nullptr);
    else
        m_left = nullptr;

    --m_size;
    return returnNode;
}

unsigned Deque::size()
{
    return m_size;
}

