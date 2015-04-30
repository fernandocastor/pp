#include <iostream>
#include <cstdio>
#include <deque>
#include <thread>
#include <mutex>
#include <vector>

using namespace std;

#define MAX 1000

mutex llock, rlock;

struct pdeq {
	int lidx, lstart;
	int ridx, rstart;
	int bkt[MAX];

	pdeq() {
		this->lstart = this->lidx = MAX/2;
		this->rstart = this->ridx = this->lstart+1;
	}
};

bool pdeq_pop_l( pdeq *d ) {
	bool ret = false;

	llock.lock();
	if ( (*d).lidx < (*d).lstart ) {
		(*d).lidx++;
		ret = true;
	} else {
		rlock.lock();
		if ( (*d).ridx != (*d).rstart ) { // Se lado direito do vetor não estiver vazio...
			(*d).rstart++;
			(*d).lstart++;
			(*d).lidx++; // Reduz o tamanho da direita e aumenta o tamanho da esquerda.
			ret = true;
		}
		rlock.unlock();
	}
	llock.unlock();

	return ret;
}

bool pdeq_pop_r( pdeq *d ) {
	bool ret = false;

	rlock.lock();
	if ( (*d).ridx > (*d).rstart ) {
		(*d).ridx--;
		ret = true;
	} else {
		rlock.unlock();
		llock.lock(); // Realiza a trava sempre começando pelo lado esquerdo para evitar deadlock.
		rlock.lock();
		if ( (*d).lidx != (*d).lstart ) {
			(*d).lstart--;
			(*d).rstart--;
			(*d).ridx--;
			ret = true;
		}
		llock.unlock();
	}
	rlock.unlock();

	return ret;
}

bool pdeq_push_l( int e, pdeq *d ) {
	bool ret = false;

	llock.lock();
	if ( (*d).lidx-1 > 0 ) {
		(*d).bkt[(*d).lidx] = e; // Adiciona elemento
		(*d).lidx--;
		ret = true;
	}
	llock.unlock();

	return ret;
}

bool pdeq_push_r( int e, pdeq *d ) {
	bool ret = false;

	rlock.lock();
	if ( (*d).ridx < MAX-1 ) {
		(*d).bkt[(*d).ridx] = e; // Adiciona elemento
		(*d).ridx++;
		ret = true;
	}
	rlock.unlock();

	return ret;
}

pair<int,bool> pdeq_front( pdeq d ) {
	pair<int,bool> r; r.second = false;

	if ( d.ridx != d.rstart ) {
		r.first = d.bkt[d.ridx-1];
		r.second = true;
	} else {
		if ( d.lidx != d.lstart ) {
			r.first = d.bkt[d.lstart];
			r.second = true;
		}
	}

	return r;
}

pair<int,bool> pdeq_back( pdeq d ) {
	pair<int,bool> r; r.second = false;

	if ( d.lidx != d.lstart ) {
		r.first = d.bkt[d.lidx+1];
		r.second = true;
	} else {
		if ( d.ridx != d.rstart ) {
			r.first = d.bkt[d.rstart];
			r.second = true;
		}
	}

	return r;
}

void test_deque1 ( pdeq *pd ) {
	for ( int i=0; i<400; i++ ) {
		pdeq_push_r(i,pd);
	}

	for ( int i=0; i<400; i++ ) {
		pdeq_pop_l(pd);
	}
}

void test_deque2 ( pdeq *pd ) {
	for ( int i=400; i<800; i++ ) {
		pdeq_pop_r(pd);
	}

	for ( int i=400; i<800; i++ ) {
		pdeq_push_l(i,pd);
	}
}

int main () {
	pdeq pd;

	vector<thread> thd(4);
	thd[0] = thread(test_deque1, &pd); thd[1] = thread(test_deque2, &pd);
	thd[2] = thread(test_deque1, &pd); thd[3] = thread(test_deque2, &pd);

	for ( auto &t : thd ) {
		t.join();
	}

	cout << pdeq_back(pd).first << "\n";
	cout << pdeq_front(pd).first << "\n\n";

	return 0;
}
