#include <iostream>
#include <cstdio>

#include "class9_backoffLockTTAS.h"

using namespace std;

#define NUMBER_THREADS 11

backoffLockTTAS bl;
unsigned long count=0;
bool sleeping=true;

vector<thread> thd (NUMBER_THREADS);
vector<unsigned long> count_thread (NUMBER_THREADS-1);

void increase ( int index ) {
	do {
		bl.lock();
		count++;
		count_thread[index]++;
		bl.unlock();
	} while ( sleeping );
}

void sleep() {
	this_thread::sleep_for(chrono::minutes(2));
	sleeping=false;
}

int main () {

	for ( int i=0; i<NUMBER_THREADS; i++ ) {
		if ( i < NUMBER_THREADS-1 ) {
			thd[i] = thread(increase, i);
		} else {
			thd[i] = thread(sleep);
		}
	}

	for ( auto &t: thd ) {
		t.join();
	}

	for ( int i=0; i<NUMBER_THREADS-1; i++ ) {
		cout << "Thread " << i+1 << ": " << count_thread[i] << "\n";
	}

	cout << "Final Count: " << count << "\n";

	return 0;
}
