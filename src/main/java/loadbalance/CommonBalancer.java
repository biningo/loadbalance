package loadbalance;

public interface CommonBalancer extends BalanceElementManager {

    /**
     * Get one of the elements by load balancing policy
     *
     * @return selected element
     * @throws NoElementFoundException if the balancer has no elements
     */
    Element acquire() throws NoElementFoundException;
}
