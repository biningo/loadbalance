package loadbalance;

import java.util.Objects;

public class Element {

    private ElementGroup group = ElementGroup.DEFAULT_GROUP;

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

    public Element(ElementGroup group, String value) {
        this.value = value;
        this.group = group;
    }

    public Element(ElementGroup group, String value, int weight) {
        this.value = value;
        this.group = group;
        this.weight = weight;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public ElementGroup getGroup() {
        return group;
    }

    public void setGroup(ElementGroup group) {
        this.group = group;
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
        return String.format(
                "Element{group=%s,value=%s,weight=%s,valid=%s}",
                group, value, weight, valid
        );
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
