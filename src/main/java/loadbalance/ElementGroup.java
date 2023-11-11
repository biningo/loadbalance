package loadbalance;

public class ElementGroup {

    private static final String DEFAULT_GROUP_NAME = "default";

    public static ElementGroup DEFAULT_GROUP;

    static {
        DEFAULT_GROUP = new ElementGroup(DEFAULT_GROUP_NAME);
    }

    private String name;

    public ElementGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
