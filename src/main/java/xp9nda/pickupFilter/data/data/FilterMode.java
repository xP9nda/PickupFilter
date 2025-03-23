package xp9nda.pickupFilter.data.data;

public enum FilterMode {
    WHITELIST,
    BLACKLIST;

    public Object getOpposite() {
        if (this == WHITELIST) {
            return BLACKLIST;
        } else {
            return WHITELIST;
        }
    }
}
