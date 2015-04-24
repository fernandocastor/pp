/*
 * @author Silas Garrido
 */

#include <cstdio>
#include <unistd.h>
#include <iostream>

using namespace std;

const int process_size = 4;

void doTask ( string classifier ) {
	string str = "java -cp moa.jar moa.DoTask \\ \"EvaluatePrequential -l "+classifier+"\"";
	system(str.c_str());
}

int main() {
	int process;
	for ( process=0; process<process_size-1; process++ ) {
		if ( fork() ) {
			break; // Não deixa processo pai continuar no loop para criar outros filhos.
		}
	}

	switch( process ) {
	case 0:
		doTask("trees.HoeffdingTree");
		break;

	case 1:
		doTask("bayes.NaiveBayes");
		break;

	case 2:
		doTask("functions.Perceptron");
		break;

	case 3:
		doTask("functions.MajorityClass");
		break;
	}

	return 0;
}
