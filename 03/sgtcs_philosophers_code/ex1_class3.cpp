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

/*
 * Verifica, de forma separada (primeiro o de um lado e depois o do outro), se os pauzinhos estão disponíveis.
 */
void get_chopstick ( int philo_ID, int chop_ID, bool first ) {
	int ret_lock;
	ret_lock = mtx[chop_ID].try_lock();
	if ( ret_lock ) {
		cout << "Philosopher " << philo_ID << " got chopstick " << chop_ID << "\n";
		if ( first ) {
			int temp_chop_ID = (chop_ID + 1) % limit;
			get_chopstick(philo_ID, temp_chop_ID, false);
		} else {
			cout << "Philosopher " << philo_ID << " is eating...\n";
			cout << "Philosopher " << philo_ID << " puts down the chopsticks and again starts to think\n\n";
		}
		mtx[chop_ID].unlock();
	} else {
		cout << "Philosopher " << philo_ID << " failed to feed (Chopstick " << chop_ID << " in use)\n\n";
	}
}

int main () {
	vector<thread> philo(limit);

	for ( int j=0; j<limit; j++ ) {
		philo[j] = thread(get_chopstick,j,j,true);
	}

	for ( auto &p : philo  ) {
		p.join();
	}

	return 0;
}
