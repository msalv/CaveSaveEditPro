package com.leo.cse.util;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObjectPool<T> {
    private final Callable<T> factory;
    private final Queue<T> pool = new ConcurrentLinkedQueue<>();

    public ObjectPool(Callable<T> factory) {
        this.factory = factory;
    }

    public T borrowObject() throws Exception {
        T obj = pool.poll();
        return obj != null ? obj : factory.call();
    }

    public void returnObject(T obj) {
        pool.add(obj);
    }
}
