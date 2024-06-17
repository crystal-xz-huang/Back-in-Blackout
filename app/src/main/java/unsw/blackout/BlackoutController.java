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

    public void removeDevice(String deviceId) {
        system.removeEntity(deviceId);
    }

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
        SpaceEntity entity = system.getEntity(deviceId);
        entity.addFile(filename, content);
    }

    public EntityInfoResponse getInfo(String id) {
        SpaceEntity entity = system.getEntity(id);
        return entity.getInfo();
    }

    public void simulate() {
        system.moveSatellites();
        system.transferFiles();
    }

    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        SpaceEntity src = system.getEntity(id);
        List<SpaceEntity> entities = system.listEntities();
        return entities.stream().filter(dest -> system.canCommunicate(src, dest)).map(SpaceEntity::getId)
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
