#include "M68KSimulator.h"
#include <cstdio>
#include <cstdlib>
#include <cstring>

extern "C" EXPORT void start() {
	printf("DEBUG - DLL Loaded");
	ramStart = reinterpret_cast<mem_union*>(malloc(ramSize));
	for (int i = 0; i < 8; i++) {
		dataRegisters[i] = reinterpret_cast<registers*>(malloc(sizeof(uint32_t)));
	}
	programCounter = 0;
	printf("DEBUG - M68K ram created. Starting offset: %p", &ramStart);
}

extern "C" EXPORT void close(){
	free(ramStart);
	for (int i = 0; i < 8; i++) {
		free(dataRegisters[i]);
	}
}

/*extern "C" BOOL WINAPI DllMain(_In_ HINSTANCE hDllHandle, _In_ DWORD nReason, _In_ LPVOID lpvReserved){
	switch (nReason){
	case DLL_PROCESS_ATTACH:
		//  For optimization.
		DisableThreadLibraryCalls(hDllHandle);
		start();
		break;

	case DLL_PROCESS_DETACH:	
		close();
		break;
	}
	return true;
}*/

extern "C" EXPORT void setByte(uint16_t address, uint8_t num) {
	ramStart->u8[address] = num;
}

extern "C" EXPORT void setWord(uint16_t address, uint16_t num) {
	ramStart->u16[address] = num;
}

extern "C" EXPORT void setLongWord(uint16_t address, uint32_t num) {
	ramStart->u32[address] = num;
}

extern "C" EXPORT void addByte(uint16_t address, uint8_t num) {
	ramStart->u8[address] = +num;
}

extern "C" EXPORT void addWord(uint16_t address, uint16_t num) {
	ramStart->u16[address] = +num;
}

extern "C" EXPORT void addLongWord(uint16_t address, uint32_t num) {
	ramStart->u32[address] = +num;
}

extern "C" EXPORT uint8_t getByte(M68kAddr address) {
	return ramStart->u8[address];
}
extern "C" EXPORT uint16_t getWord(M68kAddr address) {
	return ramStart->u16[address];
}
extern "C" EXPORT uint32_t getLongWord(M68kAddr address) {
	return ramStart->u32[address];
}

extern "C" EXPORT void clearMem() {
	memset(ramStart, 0, ramSize);
}

extern "C" EXPORT uint64_t getRamStart() {
	return reinterpret_cast<uint64_t>(&ramStart);
}

extern "C" EXPORT M68kAddr getRamSize() {
	return ramSize;
}

extern "C" EXPORT void memDump() {
	FILE *memDumpFile;
	fopen_s(&memDumpFile, "Data\\M68kDump.bin", "wb");  // w for write, b for binary
	fwrite(ramStart, 1, ramSize, memDumpFile); // write from our buffer
}


EXPORT void lea(M68kAddr address, AddressRegister An){
	addressRegisters[An] = reinterpret_cast<registers*>(ramStart->u8[address]);
}

EXPORT void pea(M68kAddr address) {
	addressRegisters[sp] -= 4;
	addressRegisters[sp]->u32 = address;
}

EXPORT void add(Size size, DataRegister dn, M68kAddr ea) {
	switch (size) {
	case Byte:
		addByte(ea, dataRegisters[dn]->u8[0]);
		break;
	case Word:
		addWord(ea, dataRegisters[dn]->u16[0]);
		break;
	case Longword:
		addLongWord(ea, dataRegisters[dn]->u32);
	}
}

EXPORT void add(Size size, M68kAddr ea, DataRegister dn) {
	switch (size) {
	case Byte:
		dataRegisters[dn]->u8[0] += getByte(ea);
		break;
	case Word:
		dataRegisters[dn]->u16[0] += getWord(ea);
		break;
	case Longword:
		dataRegisters[dn]->u32 += getLongWord(ea);
	}
}

EXPORT void adda(Size size, M68kAddr ea, AddressRegister an) {
	switch (size) {
	case Byte:
		addressRegisters[an]->u8[0] += getByte(ea);
		break;
	case Word:
		addressRegisters[an]->u16[0] += getWord(ea);
		break;
	case Longword:
		addressRegisters[an]->u32 += getLongWord(ea);
	}
}

EXPORT void addi(Size size, M68kAddr ea, uint32_t data){
	switch (size){
	case Byte:
		addByte(ea, static_cast<uint8_t>(data));
		break;
	case Word:
		addWord(ea, static_cast<uint16_t>(data));
		break;
	case Longword:
		addLongWord(ea, data);
	}
}

EXPORT void and(Size size, DataRegister dn, M68kAddr ea) {
	switch (size) {
	case Byte:
		setByte(ea, getByte(ea) & dataRegisters[dn]->u8[0]);
		break;
	case Word:
		setWord(ea, getWord(ea) & dataRegisters[dn]->u16[0]);
		break;
	case Longword:
		setLongWord(ea, getLongWord(ea) & dataRegisters[dn]->u32);
	}
}

EXPORT void and(Size size, M68kAddr ea, DataRegister dn) {
	switch (size) {
	case Byte:
		dataRegisters[dn]->u8[0] &= getByte(ea);
		break;
	case Word:
		dataRegisters[dn]->u16[0] &= getWord(ea);
		break;
	case Longword:
		dataRegisters[dn]->u32 &= getLongWord(ea);
	}
}

EXPORT void move(Size size, M68kAddr source, M68kAddr destination) {
	switch (size){
	case 0: //byte
		setByte(destination, getByte(source));
		break;
	case 1: //word
		setWord(destination, getWord(source));
		break;
	case 2: //longWord
		setLongWord(destination, getLongWord(source));
	}
}

EXPORT void move(Size size, M68kAddr source, DataRegister destination) {
	switch (size) {
	case 0: //byte
		dataRegisters[destination]->u8[0] = getByte(source);
		break;
	case 1: //word
		dataRegisters[destination]->u16[0] = getWord(source);
		break;
	case 2: //longWord
		dataRegisters[destination]->u32 = getLongWord(source);
	}
}

EXPORT void move(Size size, DataRegister source, M68kAddr destination) {
	switch (size) {
	case 0: //byte
		setByte(destination, dataRegisters[source]->u8[0]);
		break;
	case 1: //word
		setWord(destination, dataRegisters[source]->u16[0]);
		break;
	case 2: //longWord
		setLongWord(destination, dataRegisters[source]->u32);
	}
}

EXPORT void move(Size size, DataRegister source, DataRegister destination) {
	switch (size) {
	case 0: //byte
		dataRegisters[destination]->u8[0] = dataRegisters[source]->u8[0];
		break;
	case 1: //word
		dataRegisters[destination]->u16[0] = dataRegisters[source]->u16[0];
		break;
	case 2: //longWord
		dataRegisters[destination]->u32 = dataRegisters[source]->u32;
	}
}

EXPORT void moveq(Size size, uint8_t source, M68kAddr destination) {
	switch (size) {
	case 0: //byte
		setByte(destination, static_cast<uint8_t>(source));
		break;
	case 1: //word
		setWord(destination, static_cast<uint16_t>(source));
		break;
	case 2: //longWord
		setLongWord(destination, getLongWord(source));
	}
}