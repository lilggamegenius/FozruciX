#pragma once
#include <cstdint>
#define ramSize 0x10000
#define M68kRamOffset 0xE00000
#define M68kRamOffsetMirror 0xFF0000

class M68KSimulator
{
public:
	M68KSimulator();
	~M68KSimulator();
	typedef union
	{
		uint8_t u8[ramSize];
		uint16_t u16[ramSize / 2];
		uint32_t u32[ramSize / 4];

		int8_t s8[ramSize];
		int16_t s16[ramSize / 2];
		int32_t s32[ramSize / 4];
	} mem_union;

	mem_union* ramStart;
	mem_union dataRegisters[8];
	mem_union* addressRegisters[8];
	uint32_t programCounter;

	void setByte(uint16_t address, uint8_t num);

	void setWord(uint16_t address, uint16_t num);

	void setLongWord(uint16_t address, uint32_t num);

	void addByte(uint16_t address, uint8_t num);

	void addWord(uint16_t address, uint16_t num);

	void addLongWord(uint16_t address, uint32_t num);


};

M68KSimulator* instance = new M68KSimulator;