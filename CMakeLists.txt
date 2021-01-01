cmake_minimum_required(VERSION 3.17)
project(SPL_Assignment3)

set(CMAKE_CXX_STANDARD 14)

include_directories(Client/include)

add_executable(SPL_Assignment3
        Client/include/connectionHandler.h
        Client/src/connectionHandler.cpp
        Client/src/echoClient.cpp)
