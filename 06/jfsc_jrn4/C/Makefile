CC = cc
CFLAGS = -std=c99 -pedantic -Wall -stdlib=libstdc++
OBJECTS = sequencialproblem_parallel_C.o

all: sequencialproblem_parallel_C

sequencialproblem_parallel_C.o: sequencialproblem_parallel_C.c
	$(CC) $(CFLAGS) -c sequencialproblem_parallel_C.c

sequencialproblem_parallel_C: $(OBJECTS)
	$(CC) $(OBJECTS) -o sequencialproblem_parallel_C

clean:
	rm -f *.o sequencialproblem_parallel_C
