#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define NPOINTS 100000000
#define XCENTER 0.5
#define YCENTER 0.5
#define RADIUS 0.5

struct T_Circle{
	double xCenter, yCenter;
	double radius;
};
typedef struct T_Circle Circle;

int Generate ();
int isInsideTheCircle(Circle cicle, double x, double y);

int main(){
	int circlePoints = 0, i;
	double PI;
	clock_t startTime, endTime;
    double tempoGasto;
    
	startTime = clock();
	circlePoints = Generate();
	PI = (double) 4.0*((double)circlePoints/(double)NPOINTS);
	endTime = clock();
	printf("PI~: %lf\n", PI);
	printf("Tempo em segundos: %lf", (double)(endTime-startTime)/CLOCKS_PER_SEC);
	
	return 0;
}

int Generate (){
	int circleCount = 0, i;
	double xCoordinate, yCoordinate;
	Circle circle;
	
	circle.xCenter = XCENTER;
	circle.yCenter = YCENTER;
	circle.radius = RADIUS;
	
	srand((unsigned)time(NULL));
	
	for(i=0; i<NPOINTS; i++){
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
