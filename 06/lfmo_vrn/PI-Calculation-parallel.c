#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>
#include <sys/wait.h>

#include <sys/mman.h>

#define NPOINTS 100000000
#define XCENTER 0.5
#define YCENTER 0.5
#define RADIUS 0.5

struct T_Circle{
	double xCenter, yCenter;
	double radius;
};
typedef struct T_Circle Circle;

static int *circlePoints; //Shared variable between process

int Generate ();
int isInsideTheCircle(Circle cicle, double x, double y);

int main(){
        //The mmap() function shall establish a mapping between a process' address space and a shared memory object 
        circlePoints = mmap(NULL, sizeof *circlePoints, PROT_READ | PROT_WRITE, 
                    MAP_SHARED | MAP_ANONYMOUS, -1, 0); //Map a shared variable
        int status; //used by parent to wait (JOIN) the child
	int i;
	double PI;
	clock_t startTime, endTime;
        double tempoGasto;
        int temp_points;
    
	startTime = clock();
        int pid;
        pid = fork();
        if( pid == 0 ){
          //child: The return of fork() is zero
          //printf("Child: I'm the child: %d\n", pid);
          temp_points = Generate(2); //2 = number of threads. 
          *circlePoints = *circlePoints + temp_points;
          exit(-1); //Eliminate child proces
        }else if (pid > 0){
          //parent: The return of fork() is the process of id of the child
          //printf("Parent: I'm the parent: %d\n", pid);
          temp_points = Generate(2);
          wait(&status); //Wait(JOIN) the child finishing to continue..
          *circlePoints = *circlePoints + temp_points;

        }else{
          //error: The return of fork() is negative
          perror("fork failed");
          _exit(2); //exit failure, hard
        }

	PI = (double) 4.0*((double)*circlePoints/(double)NPOINTS);
	endTime = clock();
	printf("PI~: %lf\n", PI);
	printf("Tempo em segundos: %lf\n", (double)(endTime-startTime)/CLOCKS_PER_SEC);
	munmap(circlePoints, sizeof *circlePoints);
	return 0;
}

int Generate (int division){
	int circleCount = 0, i;
	double xCoordinate, yCoordinate;
	Circle circle;
	
	circle.xCenter = XCENTER;
	circle.yCenter = YCENTER;
	circle.radius = RADIUS;
	
	srand((unsigned)time(NULL));
	
	for(i=0; i<(NPOINTS/division); i++){
		xCoordinate = (double)(rand()%1001)/1000;
		yCoordinate = (double)(rand()%1001)/1000;
		circleCount+= isInsideTheCircle(circle, xCoordinate, yCoordinate);
	}
	
	return circleCount;
}

int isInsideTheCircle(Circle circle, double x, double y){
	if(((x-circle.xCenter)*(x-circle.xCenter)) + ((y-circle.yCenter)*(y-circle.yCenter)) > ((circle.radius)*(circle.radius))){
		return 0;
	}
	return 1;
}

