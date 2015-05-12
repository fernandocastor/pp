#include <cmath>
#include <random>
#include <chrono>
#include <thread>

class Backoff {
	int minDelay, maxDelay;
	int limit;

public:
	Backoff( int min, int max ) {
		this->minDelay = min;
		this->maxDelay = max;
		this->limit = this->minDelay;
	}

	void backoff() {
		int delay = rand() % this->limit;
		this->limit = std::min(this->maxDelay, 2*this->limit);
		std::this_thread::sleep_for(std::chrono::milliseconds(delay));
	}
};
