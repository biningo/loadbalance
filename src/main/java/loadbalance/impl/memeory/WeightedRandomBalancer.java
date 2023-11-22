package loadbalance.impl.memeory;

import loadbalance.CommonBalancer;
import loadbalance.Element;
import loadbalance.NoElementFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedRandomBalancer implements CommonBalancer {

    private final List<Element> elements = new ArrayList<>();
    private int totalWeight = 0;

    @Override
    public synchronized void add(Element element) {
        if (this.elements.contains(element)) {
            return;
        }
        this.elements.add(element);
        this.totalWeight += element.getWeight();
    }

    @Override
    public synchronized void remove(Element element) {
        this.elements.remove(element);
        this.totalWeight -= element.getWeight();
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public synchronized void clear() {
        this.elements.clear();
        this.totalWeight = 0;
    }

    @Override
    public synchronized Element acquire() throws UnsupportedOperationException, NoElementFoundException {
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
    }

    @Override
    public void feedback(Element element) {
    }

    @Override
    public Iterator<Element> iterator() {
        return this.elements.iterator();
    }
}
