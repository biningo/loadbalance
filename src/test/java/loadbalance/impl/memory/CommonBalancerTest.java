package loadbalance.impl.memory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import loadbalance.*;
import loadbalance.impl.memeory.LeastLoadBalancer;
import loadbalance.impl.memeory.RandomBalancer;
import loadbalance.impl.memeory.RoundRobinBalancer;
import loadbalance.impl.memeory.WeightedRandomBalancer;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

public class CommonBalancerTest {
    public static int TEST_COUNT = 5;

    @Test
    public void testRandomBalancer() {
        for (int i = 0; i < TEST_COUNT; i++) {
            testAddAndRemoveByCommonBalancer(new RandomBalancer(), 1);
            testAddAndRemoveByCommonBalancer(new RandomBalancer(), 10);
            testMutilThreadRandomBalancerAcquire(1);
            testMutilThreadRandomBalancerAcquire(10);
        }
    }

    @Test
    public void testWeightedRandomBalancer() throws NoElementFoundException {
        for (int i = 0; i < TEST_COUNT; i++) {
            testAddAndRemoveByCommonBalancer(new WeightedRandomBalancer(), 1);
            testAddAndRemoveByCommonBalancer(new WeightedRandomBalancer(), 10);
            testMutilThreadWeightedRandomBalancerAcquire(1);
            testMutilThreadWeightedRandomBalancerAcquire(10);
        }
    }

    @Test
    public void testRoundRobinBalancer() throws NoElementFoundException {
        for (int i = 0; i < TEST_COUNT; i++) {
            testAddAndRemoveByCommonBalancer(new RoundRobinBalancer(), 1);
            testAddAndRemoveByCommonBalancer(new RoundRobinBalancer(), 10);
            testMutilThreadRoundRobinBalancerAcquire(1);
            testMutilThreadRoundRobinBalancerAcquire(10);
        }
    }

    @Test
    public void testLeastLoadBalancer() throws NoElementFoundException {
        for (int i = 0; i < TEST_COUNT; i++) {
            testAddAndRemoveByCommonBalancer(new LeastLoadBalancer(), 1);
            testAddAndRemoveByCommonBalancer(new LeastLoadBalancer(), 10);
            testMutilThreadRoundRobinBalancerAcquire(1);
            testMutilThreadRoundRobinBalancerAcquire(10);
        }
    }

    private void testAddAndRemoveByCommonBalancer(CommonBalancer balancer, int threadCount) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount,
                0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("").build(),
                new ThreadPoolExecutor.AbortPolicy());
        // add and remove
        {
            int size = 10;
            List<Element> elements = TestUtil.mockElements(size);
            for (Element element : elements) {
                executor.execute(() -> balancer.add(element));
            }
            ThreadUtil.waitTasks(executor);
            Assert.assertEquals(size, balancer.size());
            for (Element element : elements) {
                executor.execute(() -> balancer.add(element));
            }
            ThreadUtil.waitTasks(executor);
            Assert.assertEquals(size, balancer.size());

            ArrayList<Element> actualElements = new ArrayList<>(size);
            balancer.iterator().forEachRemaining(actualElements::add);
            elements.sort(Comparator.comparing(Element::getValue));
            actualElements.sort(Comparator.comparing(Element::getValue));
            Assert.assertArrayEquals(elements.toArray(), actualElements.toArray());

            for (int idx = 0; idx < elements.size(); idx++) {
                int index = idx;
                executor.execute(() -> {
                    balancer.remove(elements.get(index));
                });
            }
            ThreadUtil.waitTasks(executor);
            Assert.assertEquals(0, balancer.size());
        }

        // clear
        {
            balancer.clear();
            Assert.assertEquals(0, balancer.size());

            int size = 10;
            for (Element element : TestUtil.mockElements(size)) {
                balancer.add(element);
            }
            Assert.assertEquals(size, balancer.size());
            balancer.clear();
            Assert.assertEquals(0, balancer.size());
        }
    }

    public void testMutilThreadRandomBalancerAcquire(int threadCount) {
        RandomBalancer balancer = new RandomBalancer();
        Map<Element, Integer> counter = new HashMap<>();
        int size = 10;
        List<Element> elements = TestUtil.mockElements(size);
        for (Element element : elements) {
            balancer.add(element);
            counter.put(element, 0);
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount,
                0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("").build(),
                new ThreadPoolExecutor.AbortPolicy());

        int totalChoice = 10000;
        for (int i = 0; i < totalChoice; i++) {
            executor.execute(() -> {
                try {
                    Element element = balancer.acquire();
                    synchronized (executor) {
                        counter.put(element, counter.get(element) + 1);
                    }
                } catch (NoElementFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        ThreadUtil.waitTasks(executor);

        counter.forEach((ele, count) -> {
            float rate = count * 100 / (float) totalChoice;
            Assert.assertTrue(Math.abs((float) 100 / elements.size() - rate) <= 1);
        });
    }

    public void testMutilThreadWeightedRandomBalancerAcquire(int threadCount) throws NoElementFoundException {
        CommonBalancer balancer = new WeightedRandomBalancer();
        Map<Element, Integer> counter = new HashMap<>();
        int size = 10;
        int totalWeight = 0;
        List<Element> elements = TestUtil.mockElements(size);
        for (Element element : elements) {
            int weight = ThreadLocalRandom.current().nextInt(1, 101);
            totalWeight += weight;
            element.setWeight(weight);
            balancer.add(element);
            counter.put(element, 0);
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount,
                0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("").build(),
                new ThreadPoolExecutor.AbortPolicy());

        int totalChoice = 50000;
        for (int i = 0; i < totalChoice; i++) {
            executor.execute(() -> {
                try {
                    Element element = balancer.acquire();
                    synchronized (executor) {
                        counter.put(element, counter.get(element) + 1);
                    }
                } catch (NoElementFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        ThreadUtil.waitTasks(executor);

        int totalWeightTmp = totalWeight;
        counter.forEach((ele, count) -> {
            float rate = count * 100 / (float) totalChoice;
            Assert.assertTrue(Math.abs((float) ele.getWeight() * 100 / totalWeightTmp - rate) <= 1);
        });
    }

    public void testMutilThreadRoundRobinBalancerAcquire(int threadCount) throws NoElementFoundException {
        CommonBalancer balancer = new RoundRobinBalancer();
        Map<Element, Integer> counter = new HashMap<>();
        int size = 10;
        List<Element> elements = TestUtil.mockElements(size);
        for (Element element : elements) {
            balancer.add(element);
            counter.put(element, 0);
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount,
                0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("").build(),
                new ThreadPoolExecutor.AbortPolicy());

        int totalChoice = size * 1000;
        for (int i = 0; i < totalChoice; i++) {
            executor.execute(() -> {
                try {
                    Element element = balancer.acquire();
                    synchronized (executor) {
                        counter.put(element, counter.get(element) + 1);
                    }
                } catch (NoElementFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        ThreadUtil.waitTasks(executor);

        int eleChoiceCount = totalChoice / size;
        counter.forEach((ele, count) -> {
            Assert.assertEquals(eleChoiceCount, (int) count);
        });
    }
}
