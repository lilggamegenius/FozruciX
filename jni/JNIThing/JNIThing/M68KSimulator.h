#pragma once

#include <cstdint>


#define ramSize (M68kAddr)0x10000
#define M68kRamOffset 0xE00000
#define M68kRamOffsetMirror 0xFF0000

#if defined(_MSC_VER)
//  Microsoft 
#define EXPORT __declspec(dllexport)
#define IMPORT __declspec(dllimport)
#elif defined(__GNUC__)
//  GCC
#define EXPORT __attribute__((visibility("default")))
#define IMPORT
#else
//  do nothing and hope for the best?
#define EXPORT
#define IMPORT
#pragma warning Unknown dynamic link import/export semantics.
#endif

typedef uint16_t M68kAddr;

#pragma warning( push )
#pragma warning( disable : 4200 )
typedef union{
	uint8_t u8[ramSize];
	uint16_t u16[ramSize / 2];
	uint32_t u32[ramSize / 4];

	int8_t s8[ramSize];
	int16_t s16[ramSize / 2];
	int32_t s32[ramSize / 4];
} mem_union;
#pragma warning( pop )

typedef union{
	uint8_t u8[4];
	uint16_t u16[2];
	uint32_t u32;

	int8_t s8[4];
	int16_t s16[2];
	int32_t s32;
} registers;

mem_union* ramStart;
registers* dataRegisters[8]; // d0 - d7
registers* addressRegisters[9];
uint32_t programCounter;

enum Size{
	Byte, Word, Longword
};

enum DataRegister{
	d0, d1, d2, d3, d4, d5, d6, d7
};

enum AddressRegister {
	a0, a1, a2, a3, a4, a5, a6, a7, sp=a7, usp
};