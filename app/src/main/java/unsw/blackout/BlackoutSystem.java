package unsw.blackout;

import java.util.*;
import java.util.stream.Collectors;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

public class BlackoutSystem {
    private Map<String, SpaceEntity> entities;

    public BlackoutSystem() {
        this.entities = new HashMap<>();
    }

    /**
     * Add a new Device to the system
     * @param entity
     * @param type
     * @param position Angle relative to the x-axis
     */
    public void addDevice(String deviceId, String type, Angle position) {
        Device device = null;

        switch (type) {
        case "HandheldDevice":
            device = new HandheldDevice(deviceId, type, position);
            break;
        case "LaptopDevice":
            device = new LaptopDevice(deviceId, type, position);
            break;
        case "DesktopDevice":
            device = new DesktopDevice(deviceId, type, position);
            break;
        default:
            break;
        }
        entities.put(deviceId, device);
    }

    /**
     * Add a new Satellite to the system
     * @param satelliteId
     * @param type
     * @param height Height measured from centre of Jupiter
     * @param position
     */
    public void addSatellite(String satelliteId, String type, double height, Angle position) {
        Satellite satellite = null;
        switch (type) {
        case "StandardSatellite":
            satellite = new StandardSatellite(satelliteId, type, height, position);
            break;
        case "TeleportingSatellite":
            satellite = new TeleportingSatellite(satelliteId, type, height, position);
            break;
        case "RelaySatellite":
            satellite = new RelaySatellite(satelliteId, type, height, position);
            break;
        default:
            break;
        }
        entities.put(satelliteId, satellite);
    }

    /**
     * Add a new entity to the system
     * @param entity
     */
    public void addEntity(SpaceEntity entity) {
        entities.put(entity.getId(), entity);
    }

    /**
     * Remove the entity with the specified ID
     * @param entityId
     */
    public void removeEntity(String entityId) {
        entities.remove(entityId);
    }

    /**
     * Get the entity with the specified ID
     * @param entityId
     * @return
     */
    public SpaceEntity getEntity(String entityId) {
        return entities.get(entityId);
    }

    /**
     * Get the list of all entities
     * @return
     */
    public List<SpaceEntity> listEntities() {
        return new ArrayList<>(entities.values());
    }

    /**
     * Get the ID of all entities
     * @return List of entity IDs
     */
    public List<String> listEntityIds() {
        return new ArrayList<>(entities.keySet());
    }

    /**
     * Get the list of satellites
     * @return
     */
    public List<Satellite> listSatellites() {
        return listEntities().stream().filter(e -> e instanceof Satellite).map(e -> (Satellite) e)
                .collect(Collectors.toList());
    }

    /**
     * Get the list of devices
     * @return
     */
    public List<Device> listDevices() {
        return listEntities().stream().filter(e -> e instanceof Device).map(e -> (Device) e)
                .collect(Collectors.toList());
    }

    /**
     * Simulate the Blackout system for 1 minute.
     * This will include moving satellites around
     * @param minutes
     */
    public void simulateSatellites() {
        for (Satellite satellite : listSatellites()) {
            satellite.orbit(1);
        }
    }

    /**
     * Check if the source entity can communicate with the destination entity.
     * Either:
     * (1) src supports dest and dest is visible and in range of src, or
     * (2) src supports dest and there is a path from src to dest through any number of relays.
     * Note:
     * (1) src and dest should not be the same entity
     * (2) src and dest cannot be relay satellites
     * @param src
     * @param dest
     * @param relays List of relay satellites
     * @return
     */
    public boolean canCommunicate(SpaceEntity src, SpaceEntity dest) {
        if (src.equals(dest) || src instanceof RelaySatellite || dest instanceof RelaySatellite) {
            return false;
        } else if (src.supports(dest) && inRangeAndVisible(src, dest)) {
            return true;
        } else {
            return hasRelayPath(src, dest);
        }
    }

    /**
     * Check if the destination entity is in range and visible to the source entity.
     * @param src
     * @param dest
     * @return
     */
    private boolean inRangeAndVisible(SpaceEntity src, SpaceEntity dest) {
        double h1 = src.getHeight();
        double h2 = dest.getHeight();
        Angle p1 = src.getPosition();
        Angle p2 = dest.getPosition();
        if (src instanceof Satellite && dest instanceof Satellite) {
            return getDistance(h1, p1, h2, p2) <= src.getRange() && isVisible(h1, p1, h2, p2);
        } else if (src instanceof Satellite && dest instanceof Device) {
            return getDistance(h1, p1, p2) <= src.getRange() && isVisible(h1, p1, p2);
        } else if (src instanceof Device && dest instanceof Satellite) {
            return getDistance(h2, p2, p1) <= src.getRange() && isVisible(h2, p2, p1);
        } else {
            return false;
        }
    }

    /**
     * Check if there is a path from src to dest through any number of relays.
     * For instance, src -> Relay1 -> Relay2 -> dest
     * This requires that Relay1 is visible and in range of src,
     * Relay2 is visible and in range of Relay1 and dest is visible and in range of Relay2.
     * @param src
     * @param dest
     * @param relays
     * @return true if there is a path, false otherwise
     */
    private boolean hasRelayPath(SpaceEntity src, SpaceEntity dest) {
        List<SpaceEntity> relays = listRelaySatellites();

        Queue<SpaceEntity> queue = new LinkedList<>();
        Set<SpaceEntity> visited = new HashSet<>();

        visited.add(src);
        queue.add(src);

        while (!queue.isEmpty()) {
            SpaceEntity current = queue.poll();
            if (current.equals(dest)) {
                return true;
            }

            for (SpaceEntity relay : relays) {
                if (!visited.contains(relay) && inRangeAndVisible(current, dest)) {
                    visited.add(relay);
                    queue.add(relay);
                }
            }
        }
        return false;
    }

    private List<SpaceEntity> listRelaySatellites() {
        return listEntities().stream().filter(e -> e instanceof RelaySatellite).collect(Collectors.toList());
    }

}
