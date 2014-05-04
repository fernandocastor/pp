#include <atomic>
#include <thread>

#include "compund_deque.h"

int main(int argc, char** argv) {
    if (argc != 2) {
        std::cout << "Wrong usage! Try this way: ./deque <number-of-items>" << std::endl;
        return 1;
    }

    compound_deque<int> d;

    std::atomic<int> next_value(0);
    std::atomic<bool> finished(false);
    int total = std::stoi(argv[1]);

    std::thread producer([&]() {
        while (total > 0) {
            bool front = rand() % 2 == 0;
            int value = next_value++;
            if (front)
                d.push_front(value);
            else
                d.push_back(value);

            total--;
            std::this_thread::sleep_for(std::chrono::microseconds(rand() % 1000));
        }

        finished.store(true);
        print("--> producer finished!");
    });

    std::thread consumer([&]() {
        while (!finished.load() || d.size() > 0) {
            bool front = rand() % 2 == 0;
            if (front)
                d.pop_front();
            else
                d.pop_back();

            std::this_thread::sleep_for(std::chrono::microseconds(rand() % 1000));
        }

        print("--> consumer finished!");
    });

    producer.join();
    consumer.join();

    return 0;
}
