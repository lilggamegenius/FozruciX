package com.LilG.utils;

import java.util.LinkedList;

public class SizedArray<T> extends LinkedList<T> {
    private int maxSize;

    public SizedArray() {
		this(20);
	}

    public SizedArray(int size) {
        super();
        maxSize = size;
    }

    public void setMaxSize(int size) {
        maxSize = size;
		clean();
	}

    @Override
    public boolean add(T object) {
		clean();
		return super.add(object);
	}

    public T get() {
        if (size() == 0) {
            return null;
        }
        return super.get(size() - 1);
    }

	private void clean() {
		//If the array is too big, remove elements until it's the right size.
		while (size() >= maxSize) {
			remove(0);
		}
	}
}