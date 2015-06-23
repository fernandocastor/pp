#include <mutex>
#include <condition_variable>
#include <atomic>
#include <array>
#include <new>

template <class T>
class Node {
public:
	T value;
};

template <class T>
class BoudedQueue {
	std::mutex enqLock, deqLock;
	std::condition_variable_any notEmptyCondition, notFullCondition;
	std::atomic<int> size;
	Node<T> *node;
	volatile int head, tail;
	int capacity;

public:
	BoudedQueue( int capacity ) {
		node = new Node<T>[capacity];
		this->capacity = capacity;
		head = 0; tail = -1;
		size.store(0, std::memory_order_seq_cst);
	}

	void enq( T x ) {
		bool mustWakeDequeuers = false;
		enqLock.lock();
		while ( size.load(std::memory_order_seq_cst) == capacity ) {
			notFullCondition.wait(enqLock);
		}
		node[(tail+1)%capacity].value = x;
		tail = (tail+1)%capacity;
		if ( size.fetch_add(1, std::memory_order_seq_cst) == 0 ) {
			mustWakeDequeuers = true;
		}
		enqLock.unlock();

		if ( mustWakeDequeuers ) {
			deqLock.lock();
			notEmptyCondition.notify_all();
			deqLock.unlock();
		}
	}

	T deq() {
		T result;
		bool mustWakeEnqueuers = false;
		deqLock.lock();
		while ( size.load(std::memory_order_seq_cst) == 0 ) {
			notEmptyCondition.wait(deqLock);
		}
		result = node[head].value;
		head = (head+1)%capacity;
		if ( size.fetch_sub(1, std::memory_order_seq_cst) == capacity ) {
			mustWakeEnqueuers = true;
		}
		deqLock.unlock();

		if ( mustWakeEnqueuers ) {
			enqLock.lock();
			notFullCondition.notify_all();
			enqLock.unlock();
		}

		return result;
	}

	T back() {
		T result;
		enqLock.lock();
		result = node[tail].value;
		enqLock.unlock();

		return result;
	}

	T front() {
		T result;
		deqLock.lock();
		result = node[head].value;
		deqLock.unlock();

		return result;
	}
};
