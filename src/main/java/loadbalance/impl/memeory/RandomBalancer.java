package loadbalance.impl.memeory;

import loadbalance.impl.AbstractCommonBalancer;
import loadbalance.Element;
import loadbalance.ElementGroup;
import loadbalance.NoElementFoundException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBalancer extends AbstractCommonBalancer {

    private final Map<ElementGroup, List<Element>> elementGroups = new ConcurrentHashMap<>();

    @Override
    public void add(Element element) {
        ElementGroup group = element.getGroup();
        if (!elementGroups.containsKey(group)) {
            elementGroups.put(group, new CopyOnWriteArrayList<>());
        }
        List<Element> elements = elementGroups.get(group);
        for (Element ele : elements) {
            if (ele.equals(element)) {
                return;
            }
        }
        elements.add(element);
    }

    @Override
    public void remove(Element element) {
        ElementGroup group = element.getGroup();
        List<Element> elements = elementGroups.get(group);
        if (elements == null) {
            return;
        }
        elements.remove(element);
        if (elements.isEmpty()) {
            elementGroups.remove(group);
        }
    }

    @Override
    public Element acquire(ElementGroup group) throws NoElementFoundException {
        List<Element> elements = elementGroups.get(group);
        if (elements == null || elements.isEmpty()) {
            throw new NoElementFoundException();
        }
        int index = ThreadLocalRandom.current().nextInt(0, elements.size());
        return elements.get(index);
    }
}
