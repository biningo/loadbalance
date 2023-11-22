package loadbalance.impl.memeory;

import loadbalance.CommonBalancer;
import loadbalance.Element;
import loadbalance.NoElementFoundException;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBalancer implements CommonBalancer {
    private final List<Element> elements = new CopyOnWriteArrayList<>();

    @Override
    public void add(Element element) {
        if (this.elements.contains(element)) {
            return;
        }
        this.elements.add(element);
    }

    @Override
    public void remove(Element element) {
        this.elements.remove(element);
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public void clear() {
        this.elements.clear();
    }

    @Override
    public Element acquire() throws NoElementFoundException {
        if (this.elements.isEmpty()) {
            throw new NoElementFoundException();
        }
        int index = ThreadLocalRandom.current().nextInt(0, this.elements.size());
        return this.elements.get(index);
    }

    @Override
    public void feedback(Element element) {
    }

    @Override
    public Iterator<Element> iterator() {
        return this.elements.iterator();
    }
}
