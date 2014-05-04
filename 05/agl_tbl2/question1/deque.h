#ifndef DEQUE_H
#define DEQUE_H

class Node;

class Deque {
public:
    Deque()
        : m_left(nullptr)
        , m_right(nullptr)
    {
    }

    void pushLeft(Node* node);
    Node* popLeft();

    void pushRight(Node* node);
    Node* popRight();

    Node* extractHalfLeft();
    Node* extractHalfRight();

    unsigned size();

private:
    Node* m_left;
    Node* m_right;
    unsigned m_size;
};

#endif // DEQUE_H

