package loadbalance.impl.memeory;

import loadbalance.Element;
import loadbalance.ElementGroup;
import loadbalance.NoElementFoundException;
import loadbalance.impl.AbstractCommonBalancer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedRandomBalancer extends AbstractCommonBalancer {

    private final Map<ElementGroup, List<Element>> elementGroups = new ConcurrentHashMap<>();

    private final AtomicInteger totalWeight = new AtomicInteger();

    private final Map<ElementGroup, Map<Element, Integer>> elementGroupsAndWeight = new ConcurrentHashMap<>();

    @Override
    public void add(Element element) {
        ElementGroup group = element.getGroup();
        if (!elementGroups.containsKey(group)) {
            elementGroups.put(group, new CopyOnWriteArrayList<>());
            elementGroupsAndWeight.put(group, new ConcurrentHashMap<>());
        }
        List<Element> elements = elementGroups.get(group);
        Map<Element, Integer> elementsAndWeight = elementGroupsAndWeight.get(group);
        for (Element ele : elements) {
            if (ele.equals(element)) {
                return;
            }
        }
        elements.add(element);
        elementsAndWeight.put(element, element.getWeight());
        totalWeight.addAndGet(element.getWeight());
    }

    @Override
    public void remove(Element element) {
        ElementGroup group = element.getGroup();
        List<Element> elements = elementGroups.get(group);
        if (elements == null) {
            return;
        }
        Map<Element, Integer> elementsAndWeight = elementGroupsAndWeight.get(group);
        elements.remove(element);
        elementsAndWeight.remove(element);
        this.totalWeight.addAndGet(-element.getWeight());
        if (elements.isEmpty()) {
            elementGroups.remove(group);
            elementGroupsAndWeight.remove(group);
        }
    }

    @Override
    public int size(ElementGroup group) {
        return this.elementGroups.get(group).size();
    }

    @Override
    public Set<ElementGroup> getGroups() {
        return this.elementGroups.keySet();
    }

    @Override
    public Element acquire(ElementGroup group) throws UnsupportedOperationException, NoElementFoundException {
        List<Element> elements = this.elementGroups.get(group);
        if (elements == null || elements.isEmpty()) {
            throw new NoElementFoundException();
        }

        Map<Element, Integer> elementsAndWeight = this.elementGroupsAndWeight.get(group);
        int index = ThreadLocalRandom.current().nextInt(0, this.totalWeight.get());
        for (Element element : elementsAndWeight.keySet()) {
            index -= elementsAndWeight.get(element);
            if (index < 0) {
                return element;
            }
        }
        return elements.get(0);
    }
}
