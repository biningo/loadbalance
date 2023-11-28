package loadbalance.impl.memeory;

import loadbalance.CommonBalancer;
import loadbalance.Element;
import loadbalance.NoElementFoundException;
import org.checkerframework.checker.units.qual.N;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LeastLoadBalancer implements CommonBalancer {

    private final ReadWriteLock rwLocker = new ReentrantReadWriteLock();

    private final ConcurrentSkipListMap<ElementCountWrapper, ElementCountWrapper> elements = new ConcurrentSkipListMap<>();


    @Override
    public void add(Element element) {
        ElementCountWrapper wrapper = ElementCountWrapper.wrapper(element);
        this.elements.put(wrapper, wrapper);
    }

    @Override
    public void remove(Element element) {
        this.elements.remove(ElementCountWrapper.wrapper(element));
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public void feedback(Element element) {
        try {
            this.rwLocker.writeLock().lock();
            ElementCountWrapper wrapper = this.elements.remove(ElementCountWrapper.wrapper(element));
            wrapper.incr();
            this.elements.put(wrapper, wrapper);
        } finally {
            this.rwLocker.writeLock().unlock();
        }
    }

    @Override
    public Iterator<Element> iterator() {
        ArrayList<Element> elementList = new ArrayList<>();
        this.elements.keySet().iterator().forEachRemaining(wrapper -> elementList.add(wrapper.element));
        return elementList.iterator();
    }

    @Override
    public void clear() {
        this.elements.clear();
    }

    @Override
    public Element acquire() throws NoElementFoundException {
        try {
            this.rwLocker.writeLock().lock();
            if (this.elements.isEmpty()) {
                throw new NoElementFoundException();
            }
            ElementCountWrapper wrapper = this.elements.pollFirstEntry().getKey();
            wrapper.incr();
            this.elements.put(wrapper, wrapper);
            return wrapper.getElement();
        } finally {
            this.rwLocker.writeLock().unlock();
        }
    }

    private static class ElementCountWrapper implements Comparator<ElementCountWrapper>, Comparable<ElementCountWrapper> {
        private final Element element;
        private final AtomicInteger counter = new AtomicInteger();

        private ElementCountWrapper(Element element) {
            this.element = element;
        }

        public static ElementCountWrapper wrapper(Element element) {
            return new ElementCountWrapper(element);
        }

        public int getCounter() {
            return this.counter.get();
        }

        public Element getElement() {
            return this.element;
        }

        public void incr() {
            this.counter.incrementAndGet();
        }

        public void decr() {
            this.counter.decrementAndGet();
        }

        public void add(int delta) {
            this.counter.addAndGet(delta);
        }

        public void reset() {
            this.counter.set(0);
        }

        @Override
        public int compare(ElementCountWrapper e1, ElementCountWrapper e2) {
            // If counter is equal,then compare element value
            if (e1.getCounter() == e2.getCounter()) {
                return e1.element.getValue().compareTo(e2.element.getValue());
            }
            // element duplication
            if (e1.element.equals(e2.element)) {
                return 0;
            }
            return e1.getCounter() - e2.getCounter();
        }

        @Override
        public int compareTo(ElementCountWrapper outer) {
            return compare(this, outer);
        }

        @Override
        public String toString() {
            return this.element.toString();
        }

        @Override
        public boolean equals(Object obj) {
            ElementCountWrapper outer = (ElementCountWrapper) obj;
            return this.element.equals(outer.element);
        }

        @Override
        public int hashCode() {
            return this.element.hashCode();
        }
    }

}
