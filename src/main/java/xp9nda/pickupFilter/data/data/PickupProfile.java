package xp9nda.pickupFilter.data.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PickupProfile {


    private String profileName;

    private FilterMode filterMode = FilterMode.WHITELIST;
    private List<String> pickupFilter = new ArrayList<>();

    private final UUID profileUUID;

    // constructor
    public PickupProfile() {
        this.profileUUID = UUID.randomUUID();
    }


    public List<String> getPickupFilter() {
        return pickupFilter;
    }

    public void setPickupFilter(List<String> pickupFilter) {
        this.pickupFilter = pickupFilter;
    }

    public boolean isStringInFilter(String string) {
        return pickupFilter.contains(string);
    }

    public void addFilterString(String string) {
        pickupFilter.add(string);
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode profileState) {
        this.filterMode = profileState;
    }

    public UUID getProfileUUID() {
        return profileUUID;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void removeStringInFilterList(String serializedItem) {
        pickupFilter.remove(serializedItem);
    }
}
