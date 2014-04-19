all:
	g++ -o run_thread matrix_mult_thread.cpp -pthread -std=c++11
	g++ -o run_fork matrix_mult_fork.cpp
	g++ -o run_single matrix_mult.cpp

thread:
	g++ -o run_thread matrix_mult_thread.cpp -pthread -std=c++11
fork:
	g++ -o run_fork matrix_mult_fork.cpp
single:
	g++ -o run_single matrix_mult.cpp
clean:
	rm -f run_thread run_fork run_single

