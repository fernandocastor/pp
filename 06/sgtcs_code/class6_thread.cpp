/*
 * @author Silas Garrido
 */

#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <vector>
#include <string>
#include <thread>

using namespace std;

vector<string> method = {"trees.HoeffdingTree", "bayes.NaiveBayes", "functions.Perceptron", "functions.MajorityClass"};

void doTask ( string m ) {
	string str;

	str = "java -cp moa.jar moa.DoTask \\ \"EvaluatePrequential -l "+m+"\"";
	system(str.c_str());
}

int main() {
	int n = (int)method.size();
	vector<thread> thd(n);

	for ( int i=0; i<n; i++ ) {
		thd[i] = thread(doTask, method[i]);
	}

	for ( auto &t : thd ) {
		t.join();
	}

	return 0;
}
