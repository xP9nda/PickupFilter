package xp9nda.pickupFilter.data.data;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PickupUser {

    private UUID playerUUID;
    private boolean pickupEnabled = true;

    private UUID activeProfileUUID;

    private HashMap<UUID, PickupProfile> pickupProfiles = new HashMap<>();

    public boolean userHasProfileWithName(String profileName) {
        if (pickupProfiles == null) {
            return false;
        }

        for (PickupProfile profile : pickupProfiles.values()) {
            if (profile.getProfileName().equalsIgnoreCase(profileName)) {
                return true;
            }
        }

        return false;
    }


    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public boolean isPickupEnabled() {
        return pickupEnabled;
    }

    public void setPickupEnabled(boolean pickupEnabled) {
        this.pickupEnabled = pickupEnabled;
    }

    public HashMap<UUID, PickupProfile> getPickupProfiles() {
        return pickupProfiles;
    }

    public void setPickupProfiles(HashMap<UUID, PickupProfile> pickupProfiles) {
        this.pickupProfiles = pickupProfiles;
    }

    public void registerNewPickupProfile(String profileName) {
        if (pickupProfiles == null) {
            pickupProfiles = new HashMap<>();
        }

        PickupProfile newProfile = new PickupProfile();
        newProfile.setProfileName(profileName);

        pickupProfiles.put(newProfile.getProfileUUID(), newProfile);
    }

    public PickupProfile getPickupProfile(String profileName) {
        if (pickupProfiles == null) {
            return null;
        }

        for (PickupProfile profile : pickupProfiles.values()) {
            if (profile.getProfileName().equalsIgnoreCase(profileName)) {
                return profile;
            }
        }

        return null;
    }

    public PickupProfile getPickupProfile(UUID profileUUID) {
        if (pickupProfiles == null) {
            return null;
        }

        return pickupProfiles.get(profileUUID);
    }

    public void removePickupProfile(String profileName) {
        if (pickupProfiles == null) {
            return;
        }

        for (PickupProfile profile : pickupProfiles.values()) {
            if (profile.getProfileName().equalsIgnoreCase(profileName)) {
                pickupProfiles.remove(profile.getProfileUUID());
                return;
            }
        }
    }

    public void removePickupProfile(UUID profileUUID) {
        if (pickupProfiles == null) {
            return;
        }

        pickupProfiles.remove(profileUUID);
    }

    public UUID getActiveProfileUUID() {
        return activeProfileUUID;
    }

    public void setActiveProfileUUID(UUID activeProfileUUID) {
        this.activeProfileUUID = activeProfileUUID;
    }

    public boolean userHasProfileWithUUID(UUID profileUUID) {
        if (pickupProfiles == null) {
            return false;
        }

        return pickupProfiles.containsKey(profileUUID);
    }
}
