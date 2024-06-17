package unsw.blackout;

import java.util.*;
import java.util.stream.Collectors;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private BlackoutSystem system = new BlackoutSystem();

    /**
     * Add a new device to the list of devices.
     * @param deviceId
     * @param type
     * @param position Angle relative to the x-axis
     */
    public void createDevice(String deviceId, String type, Angle position) {
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
        system.addEntity(device);
    }

    /**
     * Remove the device with the specified ID.
     * Do not need to cancel all current downloads/uploads.
     * @param deviceId
     */
    public void removeDevice(String deviceId) {
        system.removeEntity(deviceId);
    }

    /**
     * Create a new satellite with the specified parameters.
     * @param satelliteId
     * @param type
     * @param height Height measured from centre of Jupiter
     * @param position
     */
    public void createSatellite(String satelliteId, String type, double height, Angle position) {
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
        system.addEntity(satellite);
    }

    /**
     * Removes a satellite from orbit.
     * Do not need to cancel all current downloads/uploads.
     * @param satelliteId
     */
    public void removeSatellite(String satelliteId) {
        system.removeEntity(satelliteId);
    }

    /**
     * Lists all the device ids that currently exist.
     * @return List of device ids
     */
    public List<String> listDeviceIds() {
        return system.listEntityIds().stream().filter(id -> system.getEntity(id) instanceof Device)
                .collect(Collectors.toList());
    }

    /**
     * Lists all the satellite ids that currently exist.
     * @return List of satellite ids
     */
    public List<String> listSatelliteIds() {
        return system.listEntityIds().stream().filter(id -> system.getEntity(id) instanceof Satellite)
                .collect(Collectors.toList());
    }

    /**
     * Adds a file to a device (not a satellite). Files are added instantly.
     * @param deviceId
     * @param filename
     * @param content
     */
    public void addFileToDevice(String deviceId, String filename, String content) {
        SpaceEntity entity = system.getEntity(deviceId);
        entity.addFile(new File(filename, content));
    }

    /**
     * Get detailed information about a single device or a satellite.
     * @param id
     * @return EntityInfoResponse
     */
    public EntityInfoResponse getInfo(String id) {
        SpaceEntity entity = system.getEntity(id);
        return entity.getInfo();
    }

    /**
     * Simulate the Blackout system for one minute.
     * This will include moving satellites around and later on transferring files between satellites and devices.
     */
    public void simulate() {
        // TODO: Task 2a)
    }

    /**
     * Simulate for the specified number of minutes. You shouldn't need to modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    /**
     * Returns a list of device and satellite ids that are within range of the specified device.
     * @param id
     * @return List of ids that are within range
     */
    public List<String> communicableEntitiesInRange(String id) {
        SpaceEntity src = system.getEntity(id);
        List<SpaceEntity> entities = system.listEntities();
        List<SpaceEntity> relays = listRelaySatellites();
        return entities.stream().filter(dest -> canCommunicate(src, dest, relays)).map(SpaceEntity::getId)
                .collect(Collectors.toList());
    }

    /**
     * Send a file
     * @param fileName
     * @param fromId
     * @param toId
     * @throws FileTransferException
     */
    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
        SpaceEntity fromEntity = system.getEntity(fromId);
        SpaceEntity toEntity = system.getEntity(toId);
        // Check if the file exists on fromId and has finished transferring
        File fileToSend = null;
        for (File file : fromEntity.getFiles()) {
            if (file.getFilename().equals(fileName) && file.isTransferComplete()) {
                fileToSend = file;
                break;
            }
        }
        if (fileToSend == null) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }

        // Check if file exists on toId or is currently downloading to the target
        for (File file : toEntity.getFiles()) {
            if (file.getFilename().equals(fileName)) {
                throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
            }
        }

        //
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

    /**
     * Get a list of all entity ids that match the specified type.
     * @param type
     * @return List of entity ids
     */
    private List<SpaceEntity> listRelaySatellites() {
        return system.listEntities().stream().filter(e -> e instanceof RelaySatellite).collect(Collectors.toList());
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
    private boolean canCommunicate(SpaceEntity src, SpaceEntity dest, List<SpaceEntity> relays) {
        if (src.equals(dest) || src instanceof RelaySatellite || dest instanceof RelaySatellite) {
            return false;
        } else if (src.supports(dest) && inRangeAndVisible(src, dest)) {
            return true;
        } else {
            return hasRelayPath(src, dest, relays);
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
    private boolean hasRelayPath(SpaceEntity src, SpaceEntity dest, List<SpaceEntity> relays) {
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
}
