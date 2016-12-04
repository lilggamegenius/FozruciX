cmake_minimum_required(VERSION 3.6)
project(M68KSimulator)
set(CMAKE_CXX_FLAGS
        "${CMAKE_CXX_FLAGS} -save-temps -std=c++11 -fno-operator-names")

set(SOURCE_FILES
    M68KSimulator/M68KSimulator.cpp
    M68KSimulator/M68KSimulator.h)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ../)

add_library(M68KSimulator
        SHARED
        ${SOURCE_FILES})
