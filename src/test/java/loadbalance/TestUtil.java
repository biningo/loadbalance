package loadbalance;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {
    public static List<Element> mockElements(int size) {
        ArrayList<Element> elements = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            elements.add(new Element("node_" + i));
        }
        return elements;
    }
}
