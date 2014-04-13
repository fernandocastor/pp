#include "philosopher.h"
#include <stdio.h>
#include "table.h"

Philosopher::Philosopher(unsigned id, Table* table)
    : m_id(id)
    , m_table(table)
{
    m_worker = std::thread(&Philosopher::run, this);
}

void Philosopher::run() const
{
    while (!m_table->ready());

    while (true) {
        m_table->acquireChopsticks(m_id);
        eat();
        think();
    }
}

void Philosopher::eat() const
{
    printf("* Philosopher %d eating...\n", m_id);
    std::this_thread::sleep_for(std::chrono::milliseconds(2000));
    m_table->releaseChopsticks(m_id);
}

void Philosopher::think() const
{
    printf("Philosopher %d thinking...\n", m_id);
    std::this_thread::sleep_for(std::chrono::milliseconds(2000));
}

void Philosopher::join()
{
    m_worker.join();
}
