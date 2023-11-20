package loadbalance.impl;


import loadbalance.*;

public abstract class AbstractKeyBalancer implements KeyBalancer {
    @Override
    public Element acquire(String key) throws NoElementFoundException {
        return acquire(key, ElementGroup.DEFAULT_GROUP);
    }

    @Override
    public void feedback(Element element) {
    }

    @Override
    public int size() {
        return size(ElementGroup.DEFAULT_GROUP);
    }
}
