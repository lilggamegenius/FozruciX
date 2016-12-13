//
// Created by lil-g on 12/8/16.
//

#include <cstring>
#include <cstdint>
#include <stdint.h>

#ifndef M68KSIMULATOR_ROM_HPP
#define M68KSIMULATOR_ROM_HPP


class Rom{
public:
	Rom(short size);

	Rom(char* filename);

	virtual ~Rom();
};


#endif //M68KSIMULATOR_ROM_HPP
