package loadbalance;

import java.util.Iterator;
import java.util.Set;

public interface BalanceElementManager {

    /**
     * Add element into this balancer
     *
     * @param element element to be appended to this balancer
     */
    void add(Element element);

    /**
     * Remove element from this balancer
     *
     * @param element element to be removed from this balancer
     */
    void remove(Element element);

    /**
     * Get the number of elements
     *
     * @return the number of elements
     */
    int size();

    /**
     * Feedback to the load balancer after the element has been used
     *
     * @param element element already used
     */
    void feedback(Element element);

    /**
     * Returns the iterator over the elements
     */
    Iterator<Element> iterator();

    /**
     * Remove all elements
     */
    void clear();
}
