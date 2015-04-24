/*
 * @author Silas Garrido
 */

#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <vector>
#include <string>

using namespace std;

void doTask () {
	vector<string> method = {"trees.HoeffdingTree", "bayes.NaiveBayes", "functions.Perceptron", "functions.MajorityClass"};
	string str;

	for ( auto &m : method ) {
		str = "java -cp moa.jar moa.DoTask \\ \"EvaluatePrequential -l "+m+"\"";
		system(str.c_str());
	}
}

int main() {
	doTask();

	return 0;
}
