#include <mutex>
#include <condition_variable>

class Driver {
	int active;
	std::mutex mtx;
	std::condition_variable cv;

public:
	Driver( int active ) {
		this->active = active;
	}

	void await() {
		std::unique_lock<std::mutex> lock(mtx);
		this->cv.wait(lock);
	}

	void countDown() {
		std::unique_lock<std::mutex> lock(mtx);
		this->active--;
		if ( this->active == 0 ) {
			this->cv.notify_all();
		}
	}
};
