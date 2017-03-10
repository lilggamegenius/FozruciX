//
// Created by ggonz on 3/9/2017.
//

#include <memory>
#include "Ram.h"

static RamMap* Ram::memory = (RamMap*)malloc(ramSize);

Address Ram::operator[](M68kAddr address) {
    return Address(address);
}
