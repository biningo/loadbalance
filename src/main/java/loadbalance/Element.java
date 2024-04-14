package loadbalance;

import com.alibaba.fastjson.JSON;

import java.util.Objects;

public class Element {

    private String value;

    private boolean valid = true;

    private int weight;

    public Element(String value) {
        this.value = value;
    }

    public Element(String value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static Element parseFromString(String elementStr) {
        return JSON.parseObject(elementStr, Element.class);
    }

    @Override
    public boolean equals(Object obj) {
        Element target = (Element) obj;
        return Objects.equals(this.value, target.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
