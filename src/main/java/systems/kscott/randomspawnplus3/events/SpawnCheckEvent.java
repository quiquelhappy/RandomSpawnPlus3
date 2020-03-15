package systems.kscott.randomspawnplus3.events;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SpawnCheckEvent extends Event {

    private Location location;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean valid;
    private String validReason = "UNK";


    public SpawnCheckEvent(Location location) {
        this.location = location;
        this.valid = true;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Location getLocation() {
        return location;
    }

    public void setValid(boolean valid, String reason) {
        this.validReason = reason;
        this.valid = valid;
    }
    
    public String getReason() {
        return validReason;
    }

    public boolean isValid() {
        return valid;
    }
}
