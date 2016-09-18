#include "M68KSimulator.h"
#include <cstdlib>
#include <cstring>
#include <fstream>
#include <iostream>
#include <cerrno>

extern "C" EXPORT void start(){
	std::cout << "DEBUG - DLL Loaded" << std::endl;
	ramStart = reinterpret_cast<mem_union *>(malloc(ramSize));
	for(int i = 0;i < 8;i++){
		dataRegisters[i] = reinterpret_cast<registers *>(malloc(sizeof(uint32_t)));
	}
	programCounter = 0;
	std::cout << "DEBUG - M68K ram created. Starting offset: " << &ramStart << std::endl;
}

extern "C" EXPORT void close(){
	free(ramStart);
	for(int i = 0;i < 8;i++){
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

extern "C" EXPORT void setByte(uint16_t address, uint8_t num){
	ramStart->u8[address] = num;
}

extern "C" EXPORT void setWord(uint16_t address, uint16_t num){
	ramStart->u16[address] = num;
}

extern "C" EXPORT void setLongWord(uint16_t address, uint32_t num){
	ramStart->u32[address] = num;
}

extern "C" EXPORT void addByte(uint16_t address, uint8_t num){
	ramStart->u8[address] = +num;
}

extern "C" EXPORT void addWord(uint16_t address, uint16_t num){
	ramStart->u16[address] = +num;
}

extern "C" EXPORT void addLongWord(uint16_t address, uint32_t num){
	ramStart->u32[address] = +num;
}

extern "C" EXPORT uint8_t getByte(M68kAddr address){
	return ramStart->u8[address];
}
extern "C" EXPORT uint16_t getWord(M68kAddr address){
	return ramStart->u16[address];
}
extern "C" EXPORT uint32_t getLongWord(M68kAddr address){
	return ramStart->u32[address];
}

extern "C" EXPORT void clearMem(){
	memset(ramStart, 0, ramSize);
}

extern "C" EXPORT uint64_t getRamStart(){
	return reinterpret_cast<uint64_t>(&ramStart);
}

extern "C" EXPORT M68kAddr getRamSize(){
	return ramSize;
}

extern "C" EXPORT void memDump(){
	const char path[] = "./Data/M68kDump.bin";
	auto *f = fopen(path, "wb");
	if(!f){
		std::cout << "Problem writing to file \"" << f << "\" Error Number: " << errno << " Error: " << strerror(errno)
		          << std::endl;
	} else{
		fwrite(ramStart, 1, ramSize, f);
		fclose(f);
		std::cout << "Finished writing file to " << path << std::endl;
	}
}


EXPORT void lea(M68kAddr address, AddressRegister An){
	addressRegisters[An] = reinterpret_cast<registers *>(ramStart->u8[address]);
}

EXPORT void pea(M68kAddr address){
	addressRegisters[sp] -= 4;
	addressRegisters[sp]->u32 = address;
}

EXPORT void add(Size size, DataRegister dn, M68kAddr ea){
	switch(size){
	case Byte: addByte(ea, dataRegisters[dn]->u8[0]);
		break;
	case Word: addWord(ea, dataRegisters[dn]->u16[0]);
		break;
	case Longword: addLongWord(ea, dataRegisters[dn]->u32);
	}
}

EXPORT void add(Size size, M68kAddr ea, DataRegister dn){
	switch(size){
	case Byte: dataRegisters[dn]->u8[0] += getByte(ea);
		break;
	case Word: dataRegisters[dn]->u16[0] += getWord(ea);
		break;
	case Longword: dataRegisters[dn]->u32 += getLongWord(ea);
	}
}

EXPORT void adda(Size size, M68kAddr ea, AddressRegister an){
	switch(size){
	case Byte: addressRegisters[an]->u8[0] += getByte(ea);
		break;
	case Word: addressRegisters[an]->u16[0] += getWord(ea);
		break;
	case Longword: addressRegisters[an]->u32 += getLongWord(ea);
	}
}

EXPORT void addi(Size size, M68kAddr ea, uint32_t data){
	switch(size){
	case Byte: addByte(ea, static_cast<uint8_t>(data));
		break;
	case Word: addWord(ea, static_cast<uint16_t>(data));
		break;
	case Longword: addLongWord(ea, data);
	}
}

#define and_ and_

EXPORT void and_(Size size, DataRegister dn, M68kAddr ea) {
	switch (size) {
	case Byte: setByte(ea, getByte(ea) & dataRegisters[dn]->u8[0]);
		return;
	case Word: setWord(ea, getWord(ea) & dataRegisters[dn]->u16[0]);
		return;
	case Longword: setLongWord(ea, getLongWord(ea) & dataRegisters[dn]->u32);
	}
}

EXPORT void and_(Size size, M68kAddr ea, DataRegister dn) {
	switch (size) {
		case Byte: dataRegisters[dn]->u8[0] &=getByte(ea);
		return;
	case Word: dataRegisters[dn]->u16[0] &=getWord(ea);
		return;
	case Longword: dataRegisters[dn]->u32 &=getLongWord(ea);
	}
}

EXPORT void move(Size size, M68kAddr source, M68kAddr destination){
	switch(size){
	case Byte: setByte(destination, getByte(source));
		break;
	case Word: setWord(destination, getWord(source));
		break;
	case Longword: setLongWord(destination, getLongWord(source));
	}
}

EXPORT void move(Size size, M68kAddr source, DataRegister destination){
	switch(size){
	case Byte: dataRegisters[destination]->u8[0] = getByte(source);
		break;
	case Word: dataRegisters[destination]->u16[0] = getWord(source);
		break;
	case Longword: dataRegisters[destination]->u32 = getLongWord(source);
	}
}

EXPORT void move(Size size, DataRegister source, M68kAddr destination){
	switch(size){
	case Byte: setByte(destination, dataRegisters[source]->u8[0]);
		return;
	case Word: setWord(destination, dataRegisters[source]->u16[0]);
		return;
	case Longword: setLongWord(destination, dataRegisters[source]->u32);
	}
}

EXPORT void move(Size size, DataRegister source, DataRegister destination){
	switch(size){
	case Byte: dataRegisters[destination]->u8[0] = dataRegisters[source]->u8[0];
		break;
	case Word: dataRegisters[destination]->u16[0] = dataRegisters[source]->u16[0];
		break;
	case Longword: dataRegisters[destination]->u32 = dataRegisters[source]->u32;
	}
}

EXPORT void moveq(Size size, uint8_t source, M68kAddr destination){
	switch(size){
	case Byte: setByte(destination, static_cast<uint8_t>(source));
		break;
	case Word: setWord(destination, static_cast<uint16_t>(source));
		break;
	case Longword: setLongWord(destination, getLongWord(source));
	}
}