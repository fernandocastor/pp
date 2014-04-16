#ifndef PHILOSOPHER_H
#define PHILOSOPHER_H

#include <thread>

class Table;

class Philosopher {
public:
    Philosopher(unsigned id, Table*);
    void join();

private:
    void run() const;
    void eat() const;
    void think() const;

    unsigned m_id;
    Table* m_table;
    std::thread m_worker;
};

#endif // PHILOSOPHER_H

