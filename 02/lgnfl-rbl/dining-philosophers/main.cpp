#include <cstdlib>

#include <iostream>
#include <string>
#include <vector>

#include <thread>
#include <mutex>
#include <chrono>

std::mutex ioMutex;

void print(const std::string &s) {
    std::lock_guard<std::mutex> lock(ioMutex);
    std::cout << s << std::endl;
}

class Philosopher
{
public:
    Philosopher(const int id, std::mutex &leftFork, std::mutex &rightFork, bool leftie)
        : m_id(id)
        , m_leftFork(leftFork)
        , m_rightFork(rightFork)
        , m_leftie(leftie)
    { }

    void think() {
        print("Philosopher " + std::to_string(m_id) + " is thinking...");
        std::this_thread::sleep_for(std::chrono::microseconds(rand() % 1000));
    }

    void eat() {
        print("Philosopher " + std::to_string(m_id) + " is eating!");
        std::this_thread::sleep_for(std::chrono::microseconds(rand() % 10));
    }

    void getForks() {
        if (m_leftie) {
            m_leftFork.lock();
            m_rightFork.lock();
        } else {
            m_rightFork.lock();
            m_leftFork.lock();
        }
    }

    void putForks() {
        m_rightFork.unlock();
        m_leftFork.unlock();
    }

private:
    const int m_id;
    std::mutex &m_leftFork;
    std::mutex &m_rightFork;
    const bool m_leftie;
};

void philosopher_loop(Philosopher *philosopher) {
    for (int i = 0; i < 100; i++) {
        philosopher->think();
        philosopher->getForks();
        philosopher->eat();
        philosopher->putForks();
    }

    delete philosopher;
}

int main(int argc, char** argv) {
    if (argc != 2) {
        std::cout << "Wrong usage! Try this way: ./dining-philosophers <number-of-philosophers>" << std::endl;
        return 1;
    }

    const int n = std::stoi(argv[1]);
    if (n <= 2) {
        std::cout << "There is no dinner with less than three philosophers." << std::endl;
        return 1;
    }

    std::vector<std::mutex> forks(n);
    std::vector<std::thread> threads;

    // initialize the philosophers and spawn the threads
    for (int i = 0; i < n; i++) {
        Philosopher *p = new Philosopher(i, forks[i], forks[(i + 1) % n], i == (n - 1));
        threads.push_back(std::thread(philosopher_loop, p));
    }

    // wait the threads
    for (auto &thread : threads)
        thread.join();

    return 0;
}
