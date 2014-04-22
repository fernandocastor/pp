#include <mutex>
#include "philosopher.h"
#include "table.h"
#include <thread>

struct Chopstick {
    std::mutex& mutex() { return m_mutex; }

private:
    std::mutex m_mutex;
};

Table::Table(unsigned numberOfPhilosophers)
    : m_ready(false)
    , m_numberOfPhilosophers(numberOfPhilosophers)
{
    arrangeDinner();
}

void Table::arrangeDinner()
{
    for (int i = 0; i < m_numberOfPhilosophers; ++i) {
        m_chopsticks.push_back(new Chopstick);
        m_philosophers.push_back(new Philosopher(i, this));
    }

    m_ready = true;

    for (int i = 0; i < m_numberOfPhilosophers; ++i)
        m_philosophers[i]->join();
}

bool Table::ready() const
{
    return m_ready;
}

void Table::acquireChopsticks(unsigned philosoperID)
{
    unsigned left = philosoperID == 0 ? 0 : philosoperID - 1;
    unsigned right = philosoperID == 0 ? philosoperID + 1 : philosoperID;
    std::lock(m_chopsticks[left]->mutex(), m_chopsticks[right]->mutex());
}

void Table::releaseChopsticks(unsigned philosoperID)
{
    unsigned left = philosoperID == 0 ? 0 : philosoperID - 1;
    unsigned right = philosoperID == 0 ? philosoperID + 1 : philosoperID;
    m_chopsticks[left]->mutex().unlock();
    m_chopsticks[right]->mutex().unlock();
}
