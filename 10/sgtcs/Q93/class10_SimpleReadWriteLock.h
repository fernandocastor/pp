#include <mutex>
#include <condition_variable>

class SimpleReadWriteLock {
	int readers;
	bool writer;
	std::mutex mtx;
	std::condition_variable cv;

public:
	SimpleReadWriteLock() {
		this->writer = false;
		this->readers = 0;
	}

	void readLock() {
		std::unique_lock<std::mutex> lock(mtx);
		while ( this->writer ) {
			this->cv.wait(lock);
		}
		this->readers++;
	}

	void readUnlock() {
		std::unique_lock<std::mutex> lock(mtx);
		this->readers--;
		if ( this->readers == 0 ) {
			this->cv.notify_all();
		}
	}

	void writeLock() {
		std::unique_lock<std::mutex> lock(mtx);
		while ( this->readers > 0 || this->writer ) {
			this->cv.wait(lock);
		}
		this->writer = true;
	}

	void writeUnlock() {
		std::unique_lock<std::mutex> lock(mtx);
		this->writer = false;
		this->cv.notify_all();
	}
};
