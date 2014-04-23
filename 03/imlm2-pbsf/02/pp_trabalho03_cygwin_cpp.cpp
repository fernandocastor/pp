#include <iostream>
#include <vector>
#include <sstream>
#include <fstream>
#include <unistd.h>
#include <stdio.h>
#include <sys/wait.h>
#include <cstdlib>
#include <math.h>
#include <string>
#include <cstring>
#include <map>

using namespace std;

// From: http://stackoverflow.com/a/1120224
std::vector<std::string> getNextLineAndSplitIntoTokens(std::istream& str, char delim=';')
{
    std::vector<std::string>   result;
    std::string                line;
    std::getline(str, line);

    std::stringstream          lineStream(line);
    std::string                cell;

    while(getline(lineStream, cell, delim))
    {
        result.push_back(cell);
    }
    return result;
}

std::vector<std::vector<std::string> > parseCSV(std::istream& str) {
	std::vector<std::vector<std::string> > lines;
	while(!str.eof()) {
		std::vector<std::string> nextRow = getNextLineAndSplitIntoTokens(str);
		if(nextRow.size() > 0)
			lines.push_back(nextRow);
	}
	return lines;
}

std::vector<int> strVectorToIntVector(std::vector<string>& strV) {
	std::vector<int> ints;
	for (unsigned int i = 0; i < strV.size(); ++i) {
		ints.push_back(atoi(strV[i].c_str()));
	}
	return ints;
}

void merge(std::vector<int>& target, std::vector<int>& source) {
	for (unsigned int i = 0; i < source.size(); ++i) {
		if(i >= target.size()) {
			target.push_back(source[i]);
		} else {
			target[i] += source[i];
		}
	}
}

std::map<std::string, std::vector<int> > processCSV(std::vector<std::vector<std::string> >& lines) {
	std::map<std::string, std::vector<int> > packagesMetrics;
	for (unsigned int i = 0; i < lines.size(); ++i) {
		try {
			std::vector<string> line = lines[i];
			string clazzName = line[0];
			unsigned int lastSlash = clazzName.find_last_of("\\");
			string packageName;
			if(lastSlash != string::npos)
				packageName = clazzName.substr(0, lastSlash);
			else
				packageName = string("");

			std::vector<string> strValues(line.begin() + 1, line.end());
			std::vector<int> values = strVectorToIntVector(strValues);
			if(packagesMetrics.count(packageName) == 0) {
				packagesMetrics[packageName] = values;
			} else {
				merge(packagesMetrics[packageName], values);
			}

		} catch(...) {
			cout << "Something pretty bad happened" << endl;
		}

	}
	return packagesMetrics;
}
void outCSV(map<std::string, std::vector<int> > lines, std::string inPath) {
	std::string outPath;
	unsigned int lastDot = inPath.find_last_of(".");
	if(lastDot != string::npos) {
		outPath = inPath.substr(0, lastDot) + string("-packages-cprocessed.csv");
	}
	cout << "Outputing to" << outPath << endl;
	std::fstream out(outPath, ios_base::out);
	for (map<std::string, std::vector<int> >::iterator i = lines.begin(); i != lines.end(); ++i) {
		out << i->first;
		std::vector<int> values = i->second;
		for (unsigned int j = 0; j < values.size(); ++j) {
			out << ";" << values[j];
		}
		out << endl;
	}
	out.close();
}

void waitAll(int procs_count) {
	for (int i = 0; i < procs_count; ++i) {
		int status;
		wait(&status);
	}
}

int main(int argc, char *argv[]) {
	if(argc < 3) {
		cout << "Please specify the number of processes and the files list" << endl;
		return -1;
	}

	/* Data partition */
	int procs_count = std::max(atoi(argv[1]), 1);

	// Amount of files to be read
	int files_count = argc - 2;

	// Number of files a given process will read
	int step = (files_count % procs_count) == 0 ? (files_count / procs_count) : (files_count / procs_count) + 1;

	// Initial index in argv that a process will start reading from
	int current_index = 2;

	while (--procs_count > 0) { // procs_count - 1 to account for the parent process itself
		while(files_count - step < procs_count) {
			step--;
		}
		pid_t pid = fork();
		if(pid == 0) {
			break;
		} else if (pid < 0) {
			printf("Something went terribly wrong!\n");
		} else {
			current_index += step;
			files_count -= step;
		}
	}

	/* Data processing */
	for (int i = current_index; i < std::min(current_index + step, argc); ++i) {
		char* arg = argv[i];
		std::fstream in(arg, ios_base::in);
		std::vector<std::vector<std::string> > lines = parseCSV(in);
		std::map<string, std::vector<int> > aggregated = processCSV(lines);
		outCSV(aggregated, string(arg));
		in.close();
	}

	waitAll(procs_count - 1);
	return 0;
}
