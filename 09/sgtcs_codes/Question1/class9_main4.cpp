#include <iostream>
#include <cstdio>
#include <mutex>
#include <thread>
#include <vector>

using namespace std;

#define NUMBER_THREADS 200

recursive_mutex rm;
unsigned long count=0;

vector<thread> thd (NUMBER_THREADS);
vector<int> count_thread (NUMBER_THREADS);

void increase ( int index ) {
	for ( int i=0; i<1000; i++ ) {
		rm.lock();
		count++;
		count_thread[index]++;
		rm.unlock();
	}
}

int main () {

	for ( int i=0; i<NUMBER_THREADS; i++ ) {
		thd[i] = thread(increase, i);
	}

	for ( auto &t: thd ) {
		t.join();
	}

	for ( int i=0; i<NUMBER_THREADS; i++ ) {
		cout << "Thread " << i+1 << ": " << count_thread[i] << "\n";
	}

	cout << "Final Count: " << count << "\n";

	return 0;
}
