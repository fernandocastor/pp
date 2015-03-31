/*
 * @author Silas Garrido
 */

#include <iostream>
#include <thread>
#include <mutex>
#include <vector>

using namespace std;

const int limit = 5;

vector<mutex> mtx (limit);

// Verifica, de forma conjunta, se os pauzinhos estão disponíveis.
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
		cout << "Philosopher " << philo_ID << " failed to feed (Chopstick " << (ret_lock?((chop_ID + 1) % limit):(chop_ID)) << " in use)\n\n";
	}
}

int main() {
	vector<thread> philo(limit);

	for ( int j=0; j<limit; j++ ) {
		philo[j] = thread(get_chopstick,j,j);
	}

	for ( auto &p : philo  ) {
		p.join();
	}

	return 0;
}
