//
// Created by ggonz on 3/9/2017.
//

#ifndef M68KSIMULATOR_RAM_H
#define M68KSIMULATOR_RAM_H

#include <cstdint>
#include <malloc.h>
#include "Address.h"


#define ramSize 0xFFFF

typedef union{
    uint8_t u8[ramSize];
    uint16_t u16[ramSize / 2];
    uint32_t u32[ramSize / 4];

    int8_t s8[ramSize];
    int16_t s16[ramSize / 2];
    int32_t s32[ramSize / 4];
} RamMap;

class Ram {
public:
    static RamMap* memory;
    static Address operator[](M68kAddr address);
};


#endif //M68KSIMULATOR_RAM_H