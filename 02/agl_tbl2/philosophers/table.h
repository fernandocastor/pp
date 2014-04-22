#ifndef TABLE_H
#define TABLE_H

#include <vector>

class Philosopher;
struct Chopstick;

class Table {
public:
    Table(unsigned numberOfPhilosophers);

    void acquireChopsticks(unsigned philosoperID);
    void releaseChopsticks(unsigned philosoperID);
    bool ready() const;

private:
    void arrangeDinner();

    std::vector<Philosopher*> m_philosophers;
    std::vector<Chopstick*> m_chopsticks;
    bool m_ready;
    unsigned m_numberOfPhilosophers;
};

#endif // TABLE_H

