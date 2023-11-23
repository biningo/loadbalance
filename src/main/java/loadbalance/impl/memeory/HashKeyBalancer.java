package loadbalance.impl.memeory;

import loadbalance.Element;
import loadbalance.KeyBalancer;
import loadbalance.NoElementFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashKeyBalancer implements KeyBalancer {

    private final ReadWriteLock rwLocker = new ReentrantReadWriteLock();

    private final List<Element> elements = new ArrayList<>();

    @Override
    public void add(Element element) {
        try {
            this.rwLocker.writeLock().lock();
            if (this.elements.contains(element)) {
                return;
            }
            this.elements.add(element);
        } finally {
            this.rwLocker.writeLock().unlock();
        }
    }

    @Override
    public void remove(Element element) {
        this.rwLocker.writeLock().lock();
        this.elements.remove(element);
        this.rwLocker.writeLock().unlock();
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public void feedback(Element element) {
    }

    @Override
    public Iterator<Element> iterator() {
        return this.elements.iterator();
    }

    @Override
    public void clear() {
        this.rwLocker.writeLock().lock();
        this.elements.clear();
        this.rwLocker.writeLock().unlock();
    }

    @Override
    public Element acquire(String key) throws NoElementFoundException {
        try {
            this.rwLocker.readLock().lock();
            if (this.elements.isEmpty()) {
                throw new NoElementFoundException();
            }
            return this.elements.get(key.hashCode() % size());
        } finally {
            this.rwLocker.readLock().unlock();
        }
    }
}
