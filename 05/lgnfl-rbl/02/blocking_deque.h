#include <cstdio>
#include <cstdlib>

#include <mutex>
#include <deque>

template<class T>
class blocking_deque {
public:
    blocking_deque() {
        m_deque.clear();
    }

    size_t size() {
        m_mutex.lock();
        size_t size = m_deque.size();
        m_mutex.unlock();
        return size;
    }

    void push_back(T value) {
        m_mutex.lock();
        m_deque.push_back(value);
        m_mutex.unlock();
    }

    void push_front(T value) {
        m_mutex.lock();
        m_deque.push_front(value);
        m_mutex.unlock();
    }

     bool pop_back(T* element) {
        bool success = false;
        m_mutex.lock();
        success = !m_deque.empty();
        if (success) {
            *element = m_deque.back();
            m_deque.pop_back();
        }
        m_mutex.unlock();
        return success;
    }

    bool pop_front(T* element) {
        bool success = false;
        m_mutex.lock();
        success = !m_deque.empty();
        if (success) {
            *element = m_deque.front();
            m_deque.pop_front();
        }
        m_mutex.unlock();
        return success;
    }

private:
    std::mutex m_mutex;
    std::deque<T> m_deque;
};