#include <mutex>
#include <condition_variable>

class SharedBathroom {
	int male, female;
	std::mutex mtx;
	std::condition_variable cv;

public:
	SharedBathroom() {
		this->male = 0;
		this->female = 0;
	}

	void enterMale() {
		std::unique_lock<std::mutex> lock(mtx);
		while ( this->female ) {
			this->cv.wait(lock);
		}
		this->male++;
	}

	void leaveMale() {
		std::unique_lock<std::mutex> lock(mtx);
		this->male--;
		if ( this->male == 0 ) {
			this->cv.notify_all();
		}
	}

	void enterFemale() {
		std::unique_lock<std::mutex> lock(mtx);
		while ( this->male ) {
			this->cv.wait(lock);
		}
		this->female++;
	}

	void leaveFemale() {
		std::unique_lock<std::mutex> lock(mtx);
		this->female--;
		if ( this->female == 0 ) {
			this->cv.notify_all();
		}
	}
};
