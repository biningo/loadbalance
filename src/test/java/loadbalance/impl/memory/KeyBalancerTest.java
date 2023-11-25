package loadbalance.impl.memory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import loadbalance.*;
import loadbalance.impl.memeory.HashKeyBalancer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KeyBalancerTest {
    public static int TEST_COUNT = 5;

    @Test
    public void testHashKeyBalancer() throws NoElementFoundException {
        for (int i = 0; i < TEST_COUNT; i++) {
            testAddAndRemoveByKeyBalancer(new HashKeyBalancer(), 1);
            testAddAndRemoveByKeyBalancer(new HashKeyBalancer(), 10);
            testMutilThreadHashKeyBalancerAcquire(1, new HashKeyBalancer());
            testMutilThreadHashKeyBalancerAcquire(10, new HashKeyBalancer());
        }
    }

    public void testMutilThreadHashKeyBalancerAcquire(int threadCount, KeyBalancer balancer) throws NoElementFoundException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount,
                0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("").build(),
                new ThreadPoolExecutor.AbortPolicy());

        List<Element> elements = TestUtil.mockElements(10);
        elements.forEach(balancer::add);

        String[] keys = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
        for (String key : keys) {
            Element element = balancer.acquire(key);
            for (int i = 0; i < threadCount; i++) {
                executor.execute(() -> {
                    try {
                        if (!element.equals(balancer.acquire(key))) {
                            Assert.fail();
                        }
                    } catch (NoElementFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            ThreadUtil.waitTasks(executor);
        }
    }

    private void testAddAndRemoveByKeyBalancer(KeyBalancer balancer, int threadCount) {
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
}
