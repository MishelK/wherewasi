package enums;

public enum InteractionsColumn {
    DEVICE_ID("device_id"),
    FIRST_SEEN("first_seen"),
    LAST_SEEN("last_seen"),
    INTERACTION_ID("interaction_id"),
    POSITIVE("positive");

    private String name;

    InteractionsColumn(String s) {
        name = s;
    }
}
