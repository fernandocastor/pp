#include <iostream>
#include <cstdio>
#include <vector>
#include <thread>
#include <mutex>
#include <cmath>

using namespace std;

typedef unsigned long ul;

const ul NR_THREADS = 4;

ul num_online_thread = NR_THREADS-1;
bool finish = false;

vector<int> counter (NR_THREADS-1);
vector<int> countermax (NR_THREADS-1);
ul globalcountmax = pow(2,31); //26
ul globalcount = 0;
ul globalreserve = 0;
vector<thread> counterp (NR_THREADS);
mutex gblcnt_mutex;

static void globalize_count ( int *counter, int *countermax ) {
	globalcount += (ul)*counter;
	*counter = 0;
	globalreserve -= (ul)*countermax;
	*countermax = 0;
}

static void balance_count ( int *counter, int *countermax ) {
	*countermax = globalcountmax - globalcount - globalreserve;
	*countermax /= num_online_thread;
	globalreserve += (ul)*countermax;
	*counter = *countermax / 2;
	if ( (ul)*counter > globalcount ) {
		*counter = globalcount;
	}
	globalcount -= (ul)*counter;
}

bool add_count ( int delta, int *counter, int *countermax ) {
	if ( *countermax - *counter >= delta ) { // Fastpath
		*counter += delta;
		return true;
	}

	int lock = gblcnt_mutex.try_lock();
	if ( lock ) {
		globalize_count( counter, countermax );
		if ( globalcountmax - globalcount - globalreserve < (ul)delta ) {
			gblcnt_mutex.unlock();
			return false;
		}
		globalcount += (ul)delta; // Slowpath
		balance_count( counter, countermax );
		gblcnt_mutex.unlock();
		return true;
	} else {
		return false;
	}
}

ul read_count() {
	ul sum;

	int lock = gblcnt_mutex.try_lock();
	if ( lock ) {
		sum = globalcount;
		for ( auto &ctr : counter ) {
			sum += (ul)ctr;
		}
		gblcnt_mutex.unlock();
	} else {
		return 0;
	}

	return sum;
}

void thread_count ( ul index ) {
	do {
		add_count( 1, &counter[index], &countermax[index] );
	} while ( !finish );
}

void thread_read () {
	while ( read_count() < globalcountmax ) {}
	finish = true;
}

int main () {
	for ( ul i=0; i<NR_THREADS-1; i++ ) {
		counterp[i] = thread(thread_count, i);
	} counterp[NR_THREADS-1] = thread(thread_read);

	for ( auto &ctp : counterp ) {
		ctp.join();
	}

	cout << "Read Count = " << read_count() << "\n";

	return 0;
}
