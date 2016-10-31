#include "M68KSimulator.h"
#include <cstdlib>
#include <cstring>
#include <fstream>
#include <iostream>
#include <iomanip>
// ReSharper disable once CppUnusedIncludeDirective
#include <cerrno>

// Make sure to remove the underscore when compiling
#define start_ start
#define and_ and


#ifdef  _DEBUG

#define log std::cout

#else

#define log logFile
#define useLogFile

std::ofstream logFile;

#endif

extern "C" EXPORT void start_(){
#ifdef useLogFile
	const char path[] = "./Data/M68kLogFile.txt";
	logFile.open(path, std::ios::out | std::ios::binary);
	if(!logFile){
		std::cout << "ERROR - Problem writing to file \"" << path
		          << "\" Error Number: " << errno
		          << " Error: " << strerror(errno)
		          << std::endl;
	} else{
		std::cout << "Log file created to " << path << std::endl;
	}
#endif
	log << std::hex << std::uppercase << "DEBUG - DLL Loaded" << std::endl;
	ramStart = reinterpret_cast<mem_union *>(malloc(ramSize));
	for(int i = 0;i < 8;i++){
		dataRegisters[i] = reinterpret_cast<registers *>(malloc(sizeof(uint32_t)));
	}
	programCounter = 0;
	log << "DEBUG - M68K ram created. Starting offset: " << &ramStart << std::endl;
}

extern "C" EXPORT void close(){
	free(ramStart);
	free(dataRegisters);
	free(addressRegisters);
	log << "M68K memory freed" << std::endl;
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
	log << "DEBUG - Setting " << address << " as " << num << ". Now is " << ramStart->u8[address] << std::endl;
}

extern "C" EXPORT void setWord(uint16_t address, uint16_t num){
	ramStart->u16[address] = num;
	log << "DEBUG - Setting " << address << " as " << num << ". Now is " << ramStart->u16[address] << std::endl;

}

extern "C" EXPORT void setLongWord(uint16_t address, uint32_t num){
	ramStart->u32[address] = num;
	log << "DEBUG - Setting " << address << " as " << num << ". Now is " << ramStart->u32[address] << std::endl;
}

extern "C" EXPORT void addByte(uint16_t address, uint8_t num){
	ramStart->u8[address] = +num;
	log << "DEBUG - Adding " << address << " to " << num << ". Now is " << ramStart->u8[address] << std::endl;

}

extern "C" EXPORT void addWord(uint16_t address, uint16_t num){
	ramStart->u16[address] = +num;
	log << "DEBUG - Adding " << address << " to " << num << ". Now is " << ramStart->u16[address] << std::endl;

}

extern "C" EXPORT void addLongWord(uint16_t address, uint32_t num){
	ramStart->u32[address] = +num;
	log << "DEBUG - Adding " << address << " to " << num << ". Now is " << ramStart->u32[address] << std::endl;

}

extern "C" EXPORT uint8_t getByte(uint16_t address){
	return ramStart->u8[address];
}
extern "C" EXPORT uint16_t getWord(uint16_t address){
	return ramStart->u16[address];
}
extern "C" EXPORT uint32_t getLongWord(uint16_t address){
	return ramStart->u32[address];
}

extern "C" EXPORT void clearMem(){
	memset(ramStart, 0, ramSize);
}

extern "C" EXPORT uint64_t getRamStart(){
	uint64_t ramStartAddr = reinterpret_cast<uint64_t>(&ramStart);
	log << "DEBUG - Ram Start Address is " << ramStartAddr << std::endl;
	return ramStartAddr;
}

extern "C" EXPORT M68kAddr getRamSize(){
	log << "DEBUG - M68K Memory size is " << ramSize << std::endl;
	return ramSize;
}

extern "C" EXPORT void memDump(){
	const char path[] = "./Data/M68kDump.bin";
	std::ofstream f(path, std::ios::out | std::ios::binary);
	if(!f){
		std::cout << "ERROR - Problem writing to file \"" << path
		          << "\" Error Number: " << errno
		          << " Error: " << strerror(errno)
		          << std::endl;
	} else{
		M68kAddr i;
		for(i = 0; i < ramSize; i++){
			f << ramStart->u8[i];
		}
		for(i = 0; i <= usp; i++){
			f << addressRegisters[i];
		}
		for (; !(i % 16); i++){
			f << 0;
		}
		for (i = 0; i <= d7; i++) {
			f << dataRegisters[i];
		}
		f.close();
		log << "INFO - Finished writing file to " << path << std::endl;
	}
}


EXPORT void lea(M68kAddr address, AddressRegister An){
	if(An > usp){
		throw new std::exception();
	}
	registers* ptr = reinterpret_cast<registers *>(ramStart->u8[address]);
	addressRegisters[An] = ptr;
	log << "DEBUG - Setting Address register a" << (int)An << " to " << address << std::endl;
}

EXPORT void lea(uint32_t ea, AddressRegister An){
	if(An > usp){
		throw new std::exception();
	}
	registers* ptr = reinterpret_cast<registers *>(ramStart->u8);
	ptr += ea;
	addressRegisters[An] = ptr;
	log << "DEBUG - Setting Address register a" << (int)An << " to " << ea << std::endl;
}

EXPORT void pea(M68kAddr address){
	addressRegisters[sp] -= 4; // Size of a LongWord (Or a 32-bit int)
	addressRegisters[sp]->u32 = address;
	log << "DEBUG - Pushed stack pointer to " << addressRegisters[sp] << " With address " << address << std::endl;
}

EXPORT void add(Size size, DataRegister dn, M68kAddr ea){
	log << "add";
	switch(size){
	case Byte: log << ".B " << dataRegisters[dn]->u8[0] << " + " << getByte(ea);
		addByte(ea, dataRegisters[dn]->u8[0]);
		log << " = " << getByte(ea) << std::endl;
		return;
	case Word: log << ".W " << dataRegisters[dn]->u16[0] << " + " << getWord(ea);
		addWord(ea, dataRegisters[dn]->u16[0]);
		log << " = " << getWord(ea) << std::endl;
		return;
	case Longword: log << ".L " << dataRegisters[dn]->u32 << " + " << getLongWord(ea);
		addLongWord(ea, dataRegisters[dn]->u32);
		log << " = " << getLongWord(ea) << std::endl;
	}
}

EXPORT void add(Size size, M68kAddr ea, DataRegister dn){
	log << "add";
	switch(size){
	case Byte: log << ".B " << dataRegisters[dn]->u8[0] << " + " << getByte(ea);
		dataRegisters[dn]->u8[0] += getByte(ea);
		log << " = " << dataRegisters[dn]->u8[0] << std::endl;
		return;
	case Word: log << ".W " << dataRegisters[dn]->u16[0] << " + " << getWord(ea);
		dataRegisters[dn]->u16[0] += getWord(ea);
		log << " = " << dataRegisters[dn]->u16[0] << std::endl;
		return;
	case Longword: log << ".L " << dataRegisters[dn]->u32 << " + " << getLongWord(ea);
		dataRegisters[dn]->u32 += getLongWord(ea);
		log << " = " << dataRegisters[dn]->u32 << std::endl;
	}
}

EXPORT void adda(Size size, M68kAddr ea, AddressRegister an){
	log << "adda";
	switch(size){
	case Byte: log << ".B " << addressRegisters[an]->u8[0] << " + " << getByte(ea);
		addressRegisters[an]->u8[0] += getByte(ea);
		log << " = " << addressRegisters[an]->u8[0] << std::endl;
		return;
	case Word: log << ".W " << addressRegisters[an]->u16[0] << " + " << getWord(ea);
		addressRegisters[an]->u16[0] += getWord(ea);
		log << " = " << addressRegisters[an]->u16[0] << std::endl;
		return;
	case Longword: log << ".L " << addressRegisters[an]->u32 << " + " << getLongWord(ea);
		addressRegisters[an]->u32 += getLongWord(ea);
		log << " = " << addressRegisters[an]->u32 << std::endl;
	}
}

EXPORT void addi(Size size, uint32_t data, M68kAddr ea){
	log << "addi";
	switch(size){
	case Byte: log << ".B " << getByte(ea) << " + " << data;
		addByte(ea, static_cast<uint8_t>(data));
		log << " = " << getByte(ea) << std::endl;
		return;
	case Word: log << ".W " << getWord(ea) << " + " << data;
		addWord(ea, static_cast<uint16_t>(data));
		log << " = " << getWord(ea) << std::endl;
		return;
	case Longword: log << ".L " << getLongWord(ea) << " + " << data;
		addLongWord(ea, data);
		log << " = " << getLongWord(ea) << std::endl;
	}
}

EXPORT void and_(Size size, DataRegister dn, M68kAddr ea) {
	log << "and";
	switch (size) {
	case Byte: log << ".B " << dataRegisters[dn]->u8[0] << " & " << getByte(ea);
		setByte(ea, getByte(ea) & dataRegisters[dn]->u8[0]);
		log << " = " << getByte(ea) << std::endl;
		return;
	case Word: log << ".W " << dataRegisters[dn]->u16[0] << " & " << getWord(ea);
		setWord(ea, getWord(ea) & dataRegisters[dn]->u16[0]);
		log << " = " << getWord(ea) << std::endl;
		return;
	case Longword: log << ".L " << dataRegisters[dn]->u32 << " & " << getLongWord(ea);
		setLongWord(ea, getLongWord(ea) & dataRegisters[dn]->u32);
		log << " = " << getLongWord(ea) << std::endl;
	}
}

EXPORT void and_(Size size, M68kAddr ea, DataRegister dn) {
	log << "and";
	switch (size) {
	case Byte: log << ".B " << getByte(ea) << " & " << dataRegisters[dn]->u8[0];
		dataRegisters[dn]->u8[0] &=getByte(ea);
		log << " = " << dataRegisters[dn]->u8[0] << std::endl;
		return;
	case Word: log << ".W " << getWord(ea) << " & " << dataRegisters[dn]->u16[0];
		dataRegisters[dn]->u16[0] &=getWord(ea);
		log << " = " << dataRegisters[dn]->u16[0] << std::endl;
		return;
	case Longword: log << ".L " << getLongWord(ea) << " & " << dataRegisters[dn]->u32;
		dataRegisters[dn]->u32 &=getLongWord(ea);
		log << " = " << dataRegisters[dn]->u32 << std::endl;
	}
}

EXPORT void move(Size size, M68kAddr source, M68kAddr destination){
	log << "move";
	switch(size){
	case Byte: log << ".B " << source << " -> " << destination;
		setByte(destination, getByte(source));
		return;
	case Word: log << ".W " << source << " -> " << destination;
		setWord(destination, getWord(source));
		return;
	case Longword: log << ".L " << source << " -> " << destination;
		setLongWord(destination, getLongWord(source));
	}
}

EXPORT void move(Size size, M68kAddr source, DataRegister destination){
	log << "move";
	switch(size){
	case Byte: log << ".B " << source << " -> d" << destination;
		dataRegisters[destination]->u8[0] = getByte(source);
		return;
	case Word: log << ".W " << source << " -> d" << destination;
		dataRegisters[destination]->u16[0] = getWord(source);
		return;
	case Longword: log << ".L " << source << " -> d" << destination;
		dataRegisters[destination]->u32 = getLongWord(source);
	}
}

EXPORT void move(Size size, DataRegister source, M68kAddr destination){
	log << "move";
	switch(size){
	case Byte: log << ".B d" << source << " -> " << destination;
		setByte(destination, dataRegisters[source]->u8[0]);
		return;
	case Word: log << ".W d" << source << " -> " << destination;
		setWord(destination, dataRegisters[source]->u16[0]);
		return;
	case Longword: log << ".L d" << source << " -> " << destination;
		setLongWord(destination, dataRegisters[source]->u32);
	}
}

EXPORT void move(Size size, DataRegister source, DataRegister destination){
	log << "move";
	switch(size){
	case Byte: log << ".B d" << source << " -> d" << destination;
		dataRegisters[destination]->u8[0] = dataRegisters[source]->u8[0];
		return;
	case Word: log << ".W d" << source << " -> d" << destination;
		dataRegisters[destination]->u16[0] = dataRegisters[source]->u16[0];
		return;
	case Longword: log << ".L d" << source << " -> d" << destination;
		dataRegisters[destination]->u32 = dataRegisters[source]->u32;
	}
}

EXPORT void moveq(uint8_t source, M68kAddr destination){
	log << "moveq " << source << " -> " << destination << "(" << getByte(destination) << ")";
	setByte(destination, source);
}

/*
 * All supported instructions for the M68000
 * ABCD         - Add Decimal with Extend
 * ADD          - Add
 * ADDA         - Address
 * ADDI         - Add Immediate
 * ADDQ         - Add Quick
 * ADDX         - Add with Extend
 * AND          - Logical AND
 * ANDI         - Logical AND Immediate
 * ANDI to CCR  - AND Immediate to Condition Code Register
 * ANDI to SR 1 - AND Immediate to Status Register
 * ASL, ASR     - Arithmetic Shift Left and Right
 * Bcc          - Branch Conditionally
 * BCHG         - Test Bit and Change
 * BCLR         - Test Bit and Clear
 * BRA          - Branch
 * BSET         - Test Bit and Set
 * BSR          - Branch to Subroutine
 * BTST         - Test Bit
 * CHK          - Check Register Against Bound
 * CLR          - Clear
 * CMP          - Compare
 * CMPA         - Compare Address
 * CMPI         - Compare Immediate
 * CMPM         - Compare Memory to Memory
 * DBcc         - Test Condition, Decrement and Branch
 * DIVS         - Signed Divide
 * DIVU         - Unsigned Divide
 * EOR          - Logical Exclusive-OR
 * EORI         - Logical Exclusive-OR Immediate
 * EORI to CCR  - Exclusive-OR Immediate to Condition Code Register
 * EORI to SR 1 - Exclusive-OR Immediate to Status Register
 * EXG          - Exchange Registers
 * EXT          - Sign Extend
 * LLEGAL       - Take Illegal Instruction Trap
 * JMP          - Jump
 * JSR          - Jump to Subroutine
 * LEA          - Load Effective Address
 * LINK         - Link and Allocate
 * LSL,LSR      - Logical Shift Left and Right
 * MOVE         - Move
 * MOVEA        - Move Address
 * MOVE to CCR  - Move to Condition Code Register
 * MOVE from SR 1 - Move from Status Register
 * MOVE to SR 1 - Move to Status Register
 * MOVE USP 1   - Move User Stack Pointer
 * MOVEM        - Move Multiple Registers
 * MOVEP        - Move Peripheral
 * MOVEQ        - Move Quick
 * MULS         - Signed Multiply
 * MULU         - Unsigned Multiply
 * NBCD         - Negate Decimal with Extend
 * NEG          - Negate
 * NEGX         - Negate with Extend
 * NOP          - No Operation
 * NOT          - Logical Complement
 * OR           - Logical Inclusive-OR
 * ORI          - Logical Inclusive-OR Immediate
 * ORI to CCR   - Inclusive-OR Immediate to Condition Code Register
 * ORI to SR 1  - Inclusive-OR Immediate to Status Register
 * PEA          - Push Effective Address
 * RESET 1      - Reset External Devices
 * ROL,ROR      - Rotate Left and Right
 * ROXL, ROXR   - Rotate with Extend Left and Right
 * RTE 1        - Return from Exception
 * RTR          - Return and Restore
 * RTS          - Return from Subroutine
 * SBCD         - Subtract Decimal with Extend
 * Scc          - Set Conditionally
 * STOP 1       - Stop
 * SUB          - Subtract
 * SUBA         - Subtract Address
 * SUBI         - Subtract Immediate
 * SUBQ         - Subtract Quick
 * SUBX         - Subtract with Extend
 * SWAP         - Swap Register Words
 * TAS          - Test Operand and Set
 * TRAP         - Trap
 * TRAPV        - Trap on Overflow
 * TST          - Test Operand
 * UNLK         - Unlink
 */