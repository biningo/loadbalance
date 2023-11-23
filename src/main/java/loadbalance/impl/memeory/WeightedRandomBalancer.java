package loadbalance.impl.memeory;

import loadbalance.CommonBalancer;
import loadbalance.Element;
import loadbalance.NoElementFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WeightedRandomBalancer implements CommonBalancer {

    private final ReadWriteLock rwLocker = new ReentrantReadWriteLock();

    private final List<Element> elements = new ArrayList<>();
    private int totalWeight = 0;

    @Override
    public void add(Element element) {
        try {
            this.rwLocker.writeLock().lock();
            if (this.elements.contains(element)) {
                return;
            }
            this.elements.add(element);
            this.totalWeight += element.getWeight();
        } finally {
            this.rwLocker.writeLock().unlock();
        }
    }

    @Override
    public void remove(Element element) {
        this.rwLocker.writeLock().lock();
        this.elements.remove(element);
        this.totalWeight -= element.getWeight();
        this.rwLocker.writeLock().unlock();
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public void clear() {
        this.rwLocker.writeLock().lock();
        this.elements.clear();
        this.totalWeight = 0;
        this.rwLocker.writeLock().unlock();
    }

    @Override
    public Element acquire() throws UnsupportedOperationException, NoElementFoundException {
        try {
            this.rwLocker.readLock().lock();
            if (this.elements.isEmpty()) {
                throw new NoElementFoundException();
            }
            int index = ThreadLocalRandom.current().nextInt(0, this.totalWeight);
            for (Element element : this.elements) {
                index -= element.getWeight();
                if (index < 0) {
                    return element;
                }
            }
            return elements.get(0);
        } finally {
            this.rwLocker.readLock().unlock();
        }
    }

    @Override
    public void feedback(Element element) {
    }

    @Override
    public Iterator<Element> iterator() {
        return this.elements.iterator();
    }
}
