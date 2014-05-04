#include <iostream>

#include <deque>

#include <mutex>

std::mutex io_mutex;

void print(const std::string &s) {
    std::lock_guard<std::mutex> lock(io_mutex);
    std::cout << s << std::endl;
}

template<class T>
class compound_deque
{
public:
    compound_deque() { }

    int size() {
        int size;

        m_fmutex.lock();
        m_bmutex.lock();
        size = m_front.size() + m_back.size();
        m_fmutex.unlock();
        m_bmutex.unlock();

        return size;
    }

    void push_front(const T& item) {
        m_fmutex.lock();
        m_front.push_front(item);
        m_fmutex.unlock();

        print("push_front: " + std::to_string(item));
    }

    void push_back(const T& item) {
        m_bmutex.lock();
        m_back.push_back(item);
        m_bmutex.unlock();

        print("push_back: " + std::to_string(item));
    }

    T pop_front() {
        T element = T();

        m_fmutex.lock();

        if (m_front.size() > 0) {
            element = m_front.front();
            m_front.pop_front();
        }
        else {
            m_bmutex.lock();

            if (m_back.size() > 0) {
                element = m_back.front();
                m_back.pop_front();
                rebalance();
            }

            m_bmutex.unlock();
        }

        m_fmutex.unlock();

        print("pop_front: " + std::to_string(element));
        return element;
    }

    T pop_back() {
        T element = T();

        m_bmutex.lock();
        if (m_back.size() > 0) {
            element = m_back.back();
            m_back.pop_back();
        }
        else {
            m_bmutex.unlock();
            m_fmutex.lock();
            m_bmutex.lock();

            if (m_back.size() > 0) {
                element = m_back.back();
                m_back.pop_back();
            }
            else if (m_front.size() > 0) {
                element = m_front.back();
                m_front.pop_back();
                rebalance();
            }

            m_fmutex.unlock();
        }

        m_bmutex.unlock();

        print("pop_back: " + std::to_string(element));
        return element;
    }

private:
    void rebalance() {
        int front_size = m_front.size();
        int back_size = m_back.size();

        if (front_size == 0 && back_size == 0)
            return;
        else if (front_size == 1 && back_size == 0)
            return;
        else if (front_size == 0 && back_size == 1)
            return;

        print("-> before rebalance: " + std::to_string(front_size) + " - " + std::to_string(back_size));
        if (front_size == 0) {
            int mid = back_size / 2;
            m_front.insert(m_front.begin(), m_back.begin(), m_back.begin() + mid);
            m_back.erase(m_back.begin(), m_back.begin() + mid);
        }
        else {
            int mid = front_size / 2;
            int offset = front_size % 2;
            m_back.insert(m_back.begin(), m_front.rbegin(), m_front.rbegin() + mid);
            m_front.erase(m_front.begin() + mid + offset, m_front.end());
        }
        print("-> after rebalance: " + std::to_string(m_front.size()) + " - " + std::to_string(m_back.size()));
    }

    std::deque<T> m_front;
    std::mutex m_fmutex;
    std::deque<T> m_back;
    std::mutex m_bmutex;
};
