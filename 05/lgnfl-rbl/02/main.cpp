#include <atomic>
#include <thread>

#include "parallel_deque.h"

int main(int argc, char** argv) {
    if (argc != 2) {
        printf("Wrong usage! Try this way: ./deque <number-of-items>\n");
        return 1;
    }

    blocking_deque<int> d;

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
            printf("stored %d on front? %d\n", value, (int)front);

            total--;
            std::this_thread::sleep_for(std::chrono::microseconds(rand() % 1000));
        }

        finished.store(true);
        printf("--> producer finished!\n");
    });

    std::thread consumer([&]() {
        while (!finished.load() || d.size() > 0) {
            bool front = rand() % 2 == 0;
            bool success = false;
            int element = 0;
            if (front)
                success = d.pop_front(&element);
            else
                success = d.pop_back(&element);
            printf("front? %d success? %d value = %d\n", (int)front, (int)success, element);

            std::this_thread::sleep_for(std::chrono::microseconds(rand() % 1000));
        }

        printf("--> consumer finished!\n");
    });

    producer.join();
    consumer.join();

    return 0;
}
