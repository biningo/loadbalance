package loadbalance;

public interface KeyBalancer extends BalanceElementManager {

    /**
     * Get one of the elements by client key and load balancing policy
     *
     * @param key client key for load balancing policy selection
     * @return selected element
     * @throws NoElementFoundException if the balancer has no elements
     */
    Element acquire(String key) throws NoElementFoundException;

    /**
     * Get one of the elements in a group by client key and load balancing policy
     *
     * @param key   client key for load balancing policy selection
     * @param group element group
     * @return selected element
     * @throws NoElementFoundException if the balancer has no elements
     */
    Element acquire(String key, ElementGroup group) throws NoElementFoundException;

}
