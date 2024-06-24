package unsw.blackout;

import java.util.*;
import java.util.stream.Collectors;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private BlackoutSystem system = new BlackoutSystem();

    public void createDevice(String deviceId, String type, Angle position) {
        switch (type) {
        case "HandheldDevice":
            system.addEntity(new HandheldDevice(deviceId, type, position));
            break;
        case "LaptopDevice":
            system.addEntity(new LaptopDevice(deviceId, type, position));
            break;
        case "DesktopDevice":
            system.addEntity(new DesktopDevice(deviceId, type, position));
            break;
        default:
            break;
        }
    }

    public void removeDevice(String deviceId) {
        system.removeEntity(deviceId);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        switch (type) {
        case "StandardSatellite":
            system.addEntity(new StandardSatellite(satelliteId, type, position, height));
            break;
        case "TeleportingSatellite":
            system.addEntity(new TeleportingSatellite(satelliteId, type, position, height));
            break;
        case "RelaySatellite":
            system.addEntity(new RelaySatellite(satelliteId, type, position, height));
            break;
        default:
            break;
        }
    }

    public void removeSatellite(String satelliteId) {
        system.removeEntity(satelliteId);
    }

    public List<String> listDeviceIds() {
        return system.listEntityIds().stream().filter(id -> system.getEntity(id) instanceof Device)
                .collect(Collectors.toList());
    }

    public List<String> listSatelliteIds() {
        return system.listEntityIds().stream().filter(id -> system.getEntity(id) instanceof Satellite)
                .collect(Collectors.toList());
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        system.addFileToDevice(deviceId, filename, content);
    }

    public EntityInfoResponse getInfo(String id) {
        Entity entity = system.getEntity(id);
        return entity.getInfo();
    }

    public void simulate() {
        system.moveSatellites();
        system.transferFiles();
    }

    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            System.out.println("Minute " + (i + 1));
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        Entity src = system.getEntity(id);
        List<Entity> entities = system.listEntities();
        return entities.stream().filter(dest -> system.canCommunicate(src, dest)).map(Entity::getId)
                .collect(Collectors.toList());
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        system.sendFile(fileName, fromId, toId);
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

}
