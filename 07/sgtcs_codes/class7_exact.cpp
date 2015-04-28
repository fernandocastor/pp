#include <iostream>
#include <cstdio>
#include <vector>
#include <thread>
#include <mutex>
#include <cmath>
#include <atomic>

using namespace std;

typedef unsigned long ul;

const ul NR_THREADS = 4;

ul num_online_thread = NR_THREADS-1;
bool finish = false;

vector<atomic<int>> ctrandmax (NR_THREADS-1);
ul globalcountmax = pow(2,31);
ul globalcount = 0;
ul globalreserve = 0;
vector<thread> counterp (NR_THREADS);
mutex gblcnt_mutex;
#define CM_BITS (sizeof(atomic<int>) * 4)
#define MAX_COUNTERMAX ((1 << CM_BITS) - 1)

static void split_ctrandmax_int(int cami, int *c, int *cm) {
	*c = (cami >> CM_BITS) & MAX_COUNTERMAX;
	*cm = cami & MAX_COUNTERMAX;
}

static void split_ctrandmax( int index, int *old, int *c, int *cm  ) {
	unsigned int cami = ctrandmax[index].load(memory_order_relaxed); // Faz leitura atomicamente

	*old = cami;
	split_ctrandmax_int(cami, c, cm);
}

static int merge_ctrandmax( int c, int cm ) {
	unsigned int cami;

	cami = (c << CM_BITS) | cm;
	return ((int)cami);
}

static void globalize_count( int index ) {
	int c, cm, old;

	split_ctrandmax( index, &old, &c, &cm );
	globalcount += c;
	globalreserve -= cm;
	old = merge_ctrandmax(0,0);
	ctrandmax[index].store(old, memory_order_relaxed); // Faz atribuição atomicamente
}

static void flush_local_count() {
	int c, cm, old, zero;

	if ( globalreserve == 0 ) {
		return;
	}

	zero = merge_ctrandmax(0,0);
	for ( auto &ctr : ctrandmax ){
		old = ctr.exchange(zero); // Adiciona o valor antigo de "ctr" em "old" e define "ctr" como "zero"
		split_ctrandmax_int(old, &c, &cm);
		globalcount += c;
		globalreserve -= cm;
	}
}

static void balance_count( int index ) {
	int c, cm, old;
	ul limit;

	limit = globalcountmax - globalcount - globalreserve;
	limit /= num_online_thread;
	if ( limit > MAX_COUNTERMAX ) {
		cm = MAX_COUNTERMAX;
	} else {
		cm = limit;
	}
	globalreserve += cm;
	c = cm / 2;
	if ( c > (int)globalcount ) {
		c = globalcount;
	}
	globalcount -= c;
	old = merge_ctrandmax(c, cm);
	ctrandmax[index].store(old, memory_order_relaxed);
}

bool add_count ( int index, ul delta ) {
	int c, cm, old, neww;

	do { // Fastpath
		split_ctrandmax( index, &old, &c, &cm );
		if ( delta > MAX_COUNTERMAX || c + delta > cm ) {
			goto slowpath;
		}
		neww = merge_ctrandmax(c+delta, cm);
	} while( !atomic_compare_exchange_strong(&ctrandmax[index], &old, neww) ); // Compare and Swap (CAS)

	return true;

	slowpath:
	int lock = gblcnt_mutex.try_lock();
	if ( lock ) {
		globalize_count( index );
		if ( globalcountmax - globalcount - globalreserve < delta ) {
			flush_local_count();
			if ( globalcountmax - globalcount - globalreserve < delta ) {
				gblcnt_mutex.unlock();
				return false;
			}
		}
		globalcount += delta;
		balance_count( index );
		gblcnt_mutex.unlock();
	} else {
		return false;
	}

	return true;
}

unsigned long read_count() {
	int c, cm, old;
	ul sum;

	int lock = gblcnt_mutex.try_lock();
	if ( lock ) {
		sum = globalcount;
		for ( int i=0; i<(int)ctrandmax.size(); i++ ) {
			split_ctrandmax(i, &old, &c, &cm);
			sum += c;
		}
		gblcnt_mutex.unlock();
		return sum;
	} else {
		return 0;
	}
}

void thread_count ( int index ) {
	do {
		add_count( index, 1 );
	} while ( !finish );
}

void thread_read () {
	while ( read_count() < globalcountmax ) { }
	finish = true;
}

int main() {
	for ( int i=0; i<(int)NR_THREADS-1; i++ ) {
		counterp[i] = thread(thread_count, i);
	} counterp[NR_THREADS-1] = thread(thread_read);

	for ( auto &ctp : counterp ) {
		ctp.join();
	}

	cout << "Read Count = " << read_count() << "\n";

	return 0;
}
