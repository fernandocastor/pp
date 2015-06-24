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
    std::fstream file;
    file.open(argv[1], std::fstream::in);

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

    for (const auto &w : list) {
        std::cout << w.first << " " << w.second << std::endl;
    }
}
