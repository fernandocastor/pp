#include <iostream>
#include <thread>
#include <vector>

using namespace std;

#define PEOPLE 5
#define FEED 100

vector<thread> people (5);
vector<int> stew_eaten (5);

void stew ( int people_number ) {
	stew_eaten[(people_number+1)%5]++;
}

void eat ( int index ) {
	do {
		stew( index );
	} while ( stew_eaten[(index+1)%5] != FEED );
}

int main() {
	for ( int i=0; i<PEOPLE; i++ ) {
		people[i] = thread(eat, i);
	}

	for ( auto &p : people ) {
		p.join();
	}

	for ( int i=0; i<(int)stew_eaten.size(); i++ ) {
		cout << "People " << i << ": " << stew_eaten[i] << "\n";
	}

	return 0;
}
