#include <mutex>

#include "blocking_deque.h"

#define NUMBER_OF_BUCKETS 4

template<class T>
class parallel_deque {
public:
    parallel_deque() {
        m_back_index = 0;
        m_front_index = 1;
    }

    size_t size() {
        m_back_mutex.lock();
        m_front_mutex.lock();
        size_t size = 0;
        for (int i = 0; i < NUMBER_OF_BUCKETS; ++i) {
            size += m_bucket[i].size();
        }
        m_front_mutex.unlock();
        m_back_mutex.unlock();
        return size;
    }

    void push_back(T element) {
        m_back_mutex.lock();
        m_bucket[m_back_index].push_back(element);
        m_back_index = move_index_left(m_back_index);
        m_back_mutex.unlock();
    }

    void push_front(T element) {
        m_front_mutex.lock();
        m_bucket[m_front_index].push_back(element);
        m_front_index = move_index_right(m_front_index);
        m_front_mutex.unlock();
    }

    bool pop_back(T* element) {
        m_back_mutex.lock();
        int i = move_index_right(m_back_index);
        bool success = m_bucket[i].pop_back(element);
        if (success)
            m_back_index = i;
        m_back_mutex.unlock();
        return success;
    }

    bool pop_front(T* element) {
        m_front_mutex.lock();
        int i = move_index_left(m_front_index);
        bool success = m_bucket[i].pop_front(element);
        if (success)
            m_front_index = i;
        m_front_mutex.unlock();
        return success;
    }

private:
    int move_index_right(int index) {
        return (NUMBER_OF_BUCKETS + index + 1) % NUMBER_OF_BUCKETS;
    }
    int move_index_left(int index) {
        return (NUMBER_OF_BUCKETS + index - 1) % NUMBER_OF_BUCKETS;
    }

    blocking_deque<int> m_bucket[NUMBER_OF_BUCKETS];
    std::mutex m_back_mutex;
    std::mutex m_front_mutex;
    int m_back_index;
    int m_front_index;
};
