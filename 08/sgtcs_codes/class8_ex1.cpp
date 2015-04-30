#include <iostream>
#include <cstdio>
#include <deque>
#include <thread>
#include <mutex>

using namespace std;

mutex llock, rlock;

struct p_deque {
	deque<int> ldeq;
	deque<int> rdeq;
};

bool pdeq_pop_l ( p_deque *d ) {
	bool e = false;

	llock.lock();
	if ( (*d).ldeq.empty() ) {
		rlock.lock();
		if ( !(*d).rdeq.empty() ) { // Método de balanceamento pode melhorar desempenhos nas próximas iterações
			(*d).rdeq.pop_back();
			e = true;
		}
		rlock.unlock();
	} else {
		(*d).ldeq.pop_back();
		e = true;
	}
	llock.unlock();

	return e;
}

bool pdeq_pop_r ( p_deque *d ) {
	bool e = false;

	rlock.lock();
	if ( (*d).rdeq.empty() ) {
		rlock.unlock(); // Quando for travar os dois locks, sempre começar pelo esquerdo para evitar deadlocks.
		llock.lock();
		rlock.lock();
		if ( !(*d).ldeq.empty() ) { // Método de balanceamento pode melhorar desempenhos nas próximas iterações
			(*d).ldeq.pop_front();
			e = true;
		}
		llock.unlock();
	} else {
		(*d).rdeq.pop_front();
		e = true;
	}
	rlock.unlock();

	return e;
}

void pdeq_push_l( int e, p_deque *d ) {
	llock.lock();
	(*d).ldeq.push_back(e);
	llock.unlock();
}

void pdeq_push_r( int e, p_deque *d ) {
	rlock.lock();
	(*d).rdeq.push_front(e);
	rlock.unlock();
}

pair<int,bool> pdeq_front( p_deque d ) {
	pair<int,bool> r; r.second = false;

	if ( !d.rdeq.empty() ) {
		r.first = d.rdeq.front();
		r.second = true;
	} else {
		if ( !d.ldeq.empty() ) {
			r.first = d.ldeq.front();
			r.second = true;
		}
	}

	return r;
}

pair<int,bool> pdeq_back ( p_deque d ) {
	pair<int,bool> r; r.second = false;

	if ( !d.ldeq.empty() ) {
		r.first = d.ldeq.back();
		r.second = true;
	} else {
		if ( !d.rdeq.empty() ) {
			r.first = d.rdeq.back();
			r.second = true;
		}
	}

	return r;
}

int main() {

	return 0;
}
