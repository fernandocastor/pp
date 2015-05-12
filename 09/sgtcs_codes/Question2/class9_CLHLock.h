#include <mutex>
#include <thread>
#include <atomic>
#include <new>

struct QNode {
	bool locked;
};

std::atomic<QNode*> tail;
thread_local QNode *myPred;
thread_local QNode *myNode;

class CLHLock : std::mutex {
public:
	CLHLock() {
		QNode *t = new QNode;
		t->locked = false;
		tail.store(t);
	}

	void lock() {
		QNode *qnode = myNode;
		qnode->locked = true;
		QNode *pred = tail.exchange(qnode);
		myPred = pred;
		while ( pred->locked ) {}
	}

	void unlock() {
		QNode *qnode = myNode;
		qnode->locked = false;
		myNode = myPred;
	}
};
