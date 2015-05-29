#include <mutex>
#include <condition_variable>

class SharedBathroom {
	int male, female;
	std::mutex mtx;
	std::condition_variable_any cv;

public:
	SharedBathroom() {
		this->male = 0;
		this->female = 0;
	}

	void enterMale() {
		mtx.lock();
		while ( this->female ) {
			this->cv.wait(mtx);
		}
		this->male++;
		mtx.unlock();
	}

	void leaveMale() {
		mtx.lock();
		this->male--;
		if ( this->male == 0 ) {
			this->cv.notify_all();
		}
		mtx.unlock();
	}

	void enterFemale() {
		mtx.lock();
		while ( this->male ) {
			this->cv.wait(mtx);
		}
		this->female++;
		mtx.unlock();
	}

	void leaveFemale() {
		mtx.lock();
		this->female--;
		if ( this->female == 0 ) {
			this->cv.notify_all();
		}
		mtx.unlock();
	}
};
