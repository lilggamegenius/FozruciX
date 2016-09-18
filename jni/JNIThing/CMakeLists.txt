cmake_minimum_required(VERSION 3.6)
project(JNIThing)
set(CMAKE_CXX_FLAGS
        "${CMAKE_CXX_FLAGS}
        -std=c++11
        -fno-operator-names")

set(SOURCE_FILES
    JNIThing/M68KSimulator.cpp
    JNIThing/M68KSimulator.h)

add_library(JNIThing
        SHARED
        ${SOURCE_FILES})