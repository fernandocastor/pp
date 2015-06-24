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

int main(int argc, char ** argv) {
    
    std::unordered_map<std::string, unsigned int> dict;

    for (int i = 1; i < argc; i++) {
        std::fstream file;
        file.open(argv[i], std::fstream::in);
        std::string word;
        while (file >> word) {
            unsigned int count;
            file >> count;
            std::transform(word.begin(), word.end(), word.begin(), ::tolower);

            auto value = dict.find(word);
            if (value == dict.end() )
                dict.insert(std::make_pair(word, count));
            else
                dict[word] = value->second + count;
        }
        file.close();
    }

    std::vector<std::pair<std::string, unsigned int>> list(dict.begin(), dict.end());

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
