package loadbalance;

public interface Balancer {

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
     * Get one of the elements by load balancing policy
     *
     * @return selected element
     * @throws NoElementFoundException       if the balancer has no elements
     * @throws UnsupportedOperationException if the acquire policy does not support
     */
    Element acquire() throws NoElementFoundException, UnsupportedOperationException;

    /**
     * Get one of the elements by client key and load balancing policy
     *
     * @param key client key for load balancing policy selection
     */
    Element acquire(String key) throws NoElementFoundException, UnsupportedOperationException;

    /**
     * Get one of the elements in a group by load balancing policy
     *
     * @param group elements group
     */
    Element acquire(ElementGroup group) throws NoElementFoundException, UnsupportedOperationException;

    /**
     * Get one of the elements in a group by client key and load balancing policy
     */
    Element acquire(String key, ElementGroup group) throws NoElementFoundException, UnsupportedOperationException;

    /**
     * Feedback to the load balancer after the element has been used
     *
     * @param element element already used
     */
    void feedback(Element element);
}
