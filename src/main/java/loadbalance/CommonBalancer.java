package loadbalance;

public interface CommonBalancer extends BalanceElementManager {

    /**
     * Get one of the elements by load balancing policy
     *
     * @return selected element
     * @throws NoElementFoundException if the balancer has no elements
     */
    Element acquire() throws NoElementFoundException;

    /**
     * Get one of the elements in a group by load balancing policy
     *
     * @param group elements group
     * @return selected element
     * @throws NoElementFoundException if the balancer has no elements
     */
    Element acquire(ElementGroup group) throws NoElementFoundException;

}
