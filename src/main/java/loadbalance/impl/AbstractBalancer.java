package loadbalance.impl;


import loadbalance.Balancer;
import loadbalance.Element;
import loadbalance.ElementGroup;
import loadbalance.NoElementFoundException;

public abstract class AbstractBalancer implements Balancer {
    @Override
    public Element acquire() throws UnsupportedOperationException, NoElementFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element acquire(String key) throws UnsupportedOperationException, NoElementFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element acquire(ElementGroup group) throws UnsupportedOperationException, NoElementFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element acquire(String key, ElementGroup group) throws UnsupportedOperationException, NoElementFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void feedback(Element element) {
        throw new UnsupportedOperationException();
    }
}
