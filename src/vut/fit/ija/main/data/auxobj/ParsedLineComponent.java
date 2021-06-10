package vut.fit.ija.main.data.auxobj;

import java.util.HashMap;
import java.util.Map;

/**
 * Auxiliary class
 */
public class ParsedLineComponent {
    public String lineId;
    public String componentId;
    public ComponentType type;

    public ParsedLineComponent(String lineId, String componentId, ComponentType type) {
        this.lineId = lineId;
        this.componentId = componentId;
        this.type = type;
    }

    public enum ComponentType{
        STOP(0),
        STREET(1);

        private final int value;
        private static Map<Integer,ComponentType> typesByValue = new HashMap<>();

        static {
            for (ComponentType ct : ComponentType.values()) {
                typesByValue.put(ct.value, ct);
            }
        }

        ComponentType(int value) {
            this.value = value;
        }

        /**
         * Get ComponentType by its value
         * @return type with value x
         */
        public static ComponentType valueOf(int x) {
            return typesByValue.get(x);
        }

        public int getValue() {
            return this.value;
        }
    }

    @Override
    public String toString() {
        return "ParsedLineComponent{" +
                "lineId='" + lineId + '\'' +
                ", componentId='" + componentId + '\'' +
                ", type=" + type +
                '}';
    }
}
