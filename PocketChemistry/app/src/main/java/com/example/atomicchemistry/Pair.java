package com.example.atomicchemistry;

import java.io.Serializable;

public class Pair<T,V> implements Serializable {
    public T first;
    public V second;
    Pair(T _first, V _second){
        first = _first;
        second = _second;
    }
}
