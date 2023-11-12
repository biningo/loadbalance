package loadbalance.impl;


import loadbalance.CommonBalancer;
import loadbalance.Element;
import loadbalance.ElementGroup;
import loadbalance.NoElementFoundException;

public abstract class AbstractCommonBalancer implements CommonBalancer {
    @Override
    public Element acquire() throws NoElementFoundException {
        return acquire(ElementGroup.DEFAULT_GROUP);
    }

    @Override
    public void feedback(Element element) {
    }
}
