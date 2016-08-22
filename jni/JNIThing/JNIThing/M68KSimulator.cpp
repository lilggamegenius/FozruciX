#include "M68KSimulator.h"
#include <cstdlib>
#include "jni.h"
#include "com_LilG_Com_m68k_M68kSimImpl.h"
#include <cstring>
#include <windows.h>

BOOL WINAPI DllMain(_In_ HINSTANCE hinstDLL, _In_ DWORD fdwReason, _In_ LPVOID lpvReserved){
	printf("DLL Loaded");
	return true;
}

M68KSimulator::M68KSimulator(){
	ramStart = reinterpret_cast<mem_union*>(malloc(ramSize));
	programCounter = 0;
	printf("DEBUG - M68KSimulator created. Starting offset: %p", &ramStart);
}

M68KSimulator::~M68KSimulator(){
	free(ramStart);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved){
	printf("JNI Loaded");
	return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM* vm, void* reserved){
	delete instance;
	printf("JNI Unloaded");
}

/*
* Class:     com_LilG_Com_m68k_M68kSimImpl
* Method:    adda
* Signature: (Lcom/LilG/Com/m68k/M68kSim/Size;JI)V
*/
JNIEXPORT void JNICALL Java_com_LilG_Com_m68k_M68kSimImpl_adda
(JNIEnv* env, jobject, jobject size, jlong data, jint ea){
	jmethodID envelopeGetValueMethod = env->GetMethodID(env->FindClass("com/LilG/Com/m68k/Size"), "ordinal", "()I");
	uint8_t value = static_cast<uint8_t>(env->CallIntMethod(size, envelopeGetValueMethod));
	switch (value){
	case 0: //byte
		instance->addByte(static_cast<uint16_t>(ea), static_cast<uint8_t>(data));
		break;
	case 1: //word
		instance->addWord(static_cast<uint16_t>(ea), static_cast<uint16_t>(data));
		break;
	case 2: //longWord
		instance->addLongWord(static_cast<uint16_t>(ea), static_cast<uint32_t>(data));
	}
}

/*
* Class:     com_LilG_Com_m68k_M68kSimImpl
* Method:    move
* Signature: (Lcom/LilG/Com/m68k/M68kSim/Size;JI)V
*/
JNIEXPORT void JNICALL Java_com_LilG_Com_m68k_M68kSimImpl_move
(JNIEnv* env, jobject, jobject size, jlong data, jint ea){
	jmethodID envelopeGetValueMethod = env->GetMethodID(env->FindClass("com/LilG/Com/m68k/Size"), "ordinal", "()I");
	jint value = static_cast<jint>(env->CallIntMethod(size, envelopeGetValueMethod));
	switch (value){
	case 0: //byte
		instance->setByte(static_cast<uint16_t>(ea), static_cast<uint8_t>(data));
		break;
	case 1: //word
		instance->setWord(static_cast<uint16_t>(ea), static_cast<uint16_t>(data));
		break;
	case 2: //longWord
		instance->setLongWord(static_cast<uint16_t>(ea), static_cast<uint32_t>(data));
	}
}

/*
* Class:     com_LilG_Com_m68k_M68kSimImpl
* Method:    getByte
* Signature: (I)B
*/
JNIEXPORT jshort JNICALL Java_com_LilG_Com_m68k_M68kSimImpl_getByte
(JNIEnv*, jobject, jint address){
	return static_cast<jshort>(instance->ramStart->u8[address]);
}

/*
* Class:     com_LilG_Com_m68k_M68kSimImpl
* Method:    getWord
* Signature: (I)S
*/
JNIEXPORT jint JNICALL Java_com_LilG_Com_m68k_M68kSimImpl_getWord
(JNIEnv*, jobject, jint address){
	return static_cast<jint>(instance->ramStart->u16[address]);
}

/*
* Class:     com_LilG_Com_m68k_M68kSimImpl
* Method:    getLongWord
* Signature: (I)I
*/
JNIEXPORT jlong JNICALL Java_com_LilG_Com_m68k_M68kSimImpl_getLongWord
(JNIEnv* env, jobject, jint address){
	return static_cast<jlong>(instance->ramStart->u32[address]);
}

void M68KSimulator::setByte(uint16_t address, uint8_t num){
	ramStart->u8[address] = num;
}

void M68KSimulator::setWord(uint16_t address, uint16_t num){
	ramStart->u16[address] = num;
}

void M68KSimulator::setLongWord(uint16_t address, uint32_t num){
	ramStart->u32[address] = num;
}

void M68KSimulator::addByte(uint16_t address, uint8_t num){
	ramStart->u8[address] = +num;
}

void M68KSimulator::addWord(uint16_t address, uint16_t num){
	ramStart->u16[address] = +num;
}

void M68KSimulator::addLongWord(uint16_t address, uint32_t num){
	ramStart->u32[address] = + num;
}

/*
* Class:     com_LilG_Com_m68k_M68kSimImpl
* Method:    clearMem0
* Signature: ()V
*/
JNIEXPORT void JNICALL Java_com_LilG_Com_m68k_M68kSimImpl_clearMem0
(JNIEnv* env, jobject){
	memset(instance->ramStart, 0, ramSize);
}

/*
* Class:     com_LilG_Com_m68k_M68kSimImpl
* Method:    getRamStart
* Signature: ()J
*/
JNIEXPORT jlong JNICALL Java_com_LilG_Com_m68k_M68kSimImpl_getRamStart
(JNIEnv* env, jobject){
	return reinterpret_cast<jlong>(&instance->ramStart);
}

/*
* Class:     com_LilG_Com_m68k_M68kSimImpl
* Method:    getRamSize
* Signature: ()I
*/
JNIEXPORT jint JNICALL Java_com_LilG_Com_m68k_M68kSimImpl_getRamSize
(JNIEnv* env, jobject){
	return static_cast<jint>(ramSize);
}

