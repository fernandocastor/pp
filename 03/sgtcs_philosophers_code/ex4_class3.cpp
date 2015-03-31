/*
 * @author Silas Garrido
 */

#include <iostream>
#include <thread>
#include <mutex>
#include <vector>
#include <queue>

using namespace std;

const int limit = 7; // Quantidade de filósofos/pauzinhos pode ser alterada para "n".

vector<mutex> mtx (limit);

queue<int> chop;

void get_chopstick ( int philo_ID, int chop_ID ) {
	int ret_lock = try_lock(mtx[chop_ID],mtx[(chop_ID + 1) % limit]);
	if ( ret_lock == -1 ) {
		cout << "Philosopher " << philo_ID << " got chopstick " << chop_ID << "\n";
		cout << "Philosopher " << philo_ID << " got chopstick " << (chop_ID + 1) % limit << "\n";
		cout << "Philosopher " << philo_ID << " is eating...\n";
		cout << "Philosopher " << philo_ID << " puts down the chopsticks and again starts to think\n\n";
		mtx[(chop_ID + 1) % limit].unlock();
		mtx[chop_ID].unlock();
	} else {
		int failed = (ret_lock?((chop_ID + 1) % limit):(chop_ID));
		cout << "Philosopher " << philo_ID << " failed to feed (Chopstick " << failed << " in use)\n\n";
		chop.push(philo_ID);
	}
}

int main() {
	vector<thread> philo;

	for ( int i=0; i<limit; i++ ) {
		chop.push(i);
	}

	do {
		while ( !chop.empty() ) {
			philo.push_back(thread(get_chopstick,chop.front(),chop.front()));
			chop.pop();
		}

		for ( auto &p : philo  ) {
			p.join();
		}

		philo.clear();

	} while ( !chop.empty() );

	return 0;
}
