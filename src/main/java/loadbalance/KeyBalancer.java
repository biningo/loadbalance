package loadbalance;

public interface KeyBalancer extends BalanceElementManager {

    /**
     * Get one of the elements by client key and load balancing policy
     *
     * @param key client key for load balancing policy selection
     * @return selected element
     * @throws NoElementFoundException if the balancer has no elements
     */
    Element acquire(Object key) throws NoElementFoundException;
}
