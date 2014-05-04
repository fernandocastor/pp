#ifndef NODE_H
#define NODE_H

class Node {
public:
    Node(int value, Node* previous = nullptr, Node* next = nullptr)
        : m_value(value)
        , m_previous(previous)
        , m_next(next)
    {
    }

    Node* previous() { return m_previous; }
    void setPrevious(Node* node) { m_previous = node; }

    Node* next() { return m_next; }
    void setNext(Node* node) { m_next = node; }

    int value() { return m_value; }

private:
    int m_value;
    Node* m_previous;
    Node* m_next;

};

#endif // NODE_H

