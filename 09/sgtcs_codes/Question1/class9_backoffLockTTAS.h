#include <mutex>
#include <atomic>
#include "class9_backoff.h"

class backoffLockTTAS : std::mutex {
	std::atomic<bool> state;
	static const int MIN_DELAY = 1;
	static const int MAX_DELAY = 100;

public:
	void lock() {
		Backoff backoff = Backoff(MIN_DELAY, MAX_DELAY);
		while ( true ) {
			while ( this->state.load(std::memory_order_seq_cst) ) {}
			if ( !this->state.exchange(true) ) {
				return;
			} else {
				backoff.backoff();
			}
		}
	}

	void unlock() {
		this->state.store(false);
	}
};
