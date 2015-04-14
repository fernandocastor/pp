/*
 * top_lite.cpp
 * Emiliano Firmino <emiliano.firmino@gmail.com>
 * Daker Fernandes
 *
 * Copyright (C) 2015
 *
 * Distributed under terms of the MIT license.
 */

#include <algorithm>
#include <iostream>
#include <fstream>
#include <string>
#include <unordered_map>
#include <vector>
#include <queue>

void topn(char * filename) {
    std::fstream file;
    file.open(filename, std::fstream::in);

    std::unordered_map<std::string, unsigned int> dict;

    std::string word;
    while (file >> word) {
        std::transform(word.begin(), word.end(), word.begin(), ::tolower);

        auto value = dict.find(word);
        if (value == dict.end() )
            dict.insert(std::make_pair(word, 1));
        else
            dict[word] = value->second + 1;
    }


    std::vector<std::pair<std::string, unsigned int>> list(dict.begin(), dict.end());

    std::sort(list.begin(), list.end(),
        [](const std::pair<std::string, unsigned int>& lhs,
           const std::pair<std::string, unsigned int>& rhs) {
               return lhs.second > rhs.second;
        });

    std::priority_queue<std::pair<unsigned int, std::string> > heap;
    for (const auto &w : list) {
        std::pair<unsigned int, std::string> p;
        p.first = w.second;
        p.second = w.first;
        heap.push(p);
    }

    for (int i = 0; i < 10; i++)
    {
        std::cout << heap.top().second << " " << heap.top().first << std::endl;
        heap.pop();
    }

}

int main(int argc, char ** argv) {
    for (int i = 1; i < argc; i++) {
        std::cout << "topn of " << argv[i] << "\n";
        topn(argv[i]);
    }
}
