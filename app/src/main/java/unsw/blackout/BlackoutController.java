package unsw.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.blackout.device.*;
import unsw.blackout.satellite.*;

/**
 * The controller for the Blackout system.
 *
 * WARNING: Do not move this file or modify any of the existing method
 * signatures
 */
public class BlackoutController {
    private List<Device> devices = new ArrayList<>();
    private List<Satellite> satellites = new ArrayList<>();

    /**
     * Add a new device to the list of devices.
     * @param deviceId
     * @param type
     * @param position Angle relative to the x-axis
     */
    public void createDevice(String deviceId, String type, Angle position) {
        // Create a new device based on the type and add it to the list of devices
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
        devices.add(device);
    }

    /**
     * Remove the device with the specified ID.
     * Do not need to cancel all current downloads/uploads.
     * @param deviceId
     */
    public void removeDevice(String deviceId) {
        Device device = findDevice(deviceId);
        devices.remove(device);
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
        satellites.add(satellite);
    }

    /**
     * Removes a satellite from orbit.
     * Do not need to cancel all current downloads/uploads.
     * @param satelliteId
     */
    public void removeSatellite(String satelliteId) {
        Satellite satellite = findSatellite(satelliteId);
        satellites.remove(satellite);
    }

    /**
     * Lists all the device ids that currently exist.
     * @return List of device ids
     */
    public List<String> listDeviceIds() {
        return devices.stream().map(Device::getDeviceId).toList();
    }

    /**
     * Lists all the satellite ids that currently exist.
     * @return List of satellite ids
     */
    public List<String> listSatelliteIds() {
        List<String> satelliteIds = new ArrayList<>();
        for (Satellite satellite : satellites) {
            satelliteIds.add(satellite.getSatelliteId());
        }
        return satelliteIds;
    }

    /**
     * Adds a file to a device (not a satellite). Files are added instantly.
     * @param deviceId
     * @param filename
     * @param content
     */
    public void addFileToDevice(String deviceId, String filename, String content) {
        Device device = findDevice(deviceId);
        device.addFile(new File(filename, content));
    }

    private Map<String, FileInfoResponse> getFileInfoResponses(List<File> files) {
        Map<String, FileInfoResponse> fileInfoResponses = new HashMap<>();
        for (File file : files) {
            fileInfoResponses.put(file.getFilename(), file.getFileInfoResponse());
        }
        return fileInfoResponses;
    }

    /**
     * Get detailed information about a single device or a satellite.
     * @param id
     * @return EntityInfoResponse
     */
    public EntityInfoResponse getInfo(String id) {
        EntityInfoResponse response = null;
        Satellite satellite = findSatellite(id);
        Device device = findDevice(id);
        if (satellite != null) {
            response = new EntityInfoResponse(satellite.getSatelliteId(), satellite.getPosition(),
                    satellite.getHeight(), satellite.getType(), getFileInfoResponses(satellite.getFiles()));
        } else if (device != null) {
            response = new EntityInfoResponse(device.getDeviceId(), device.getPosition(), device.getHeight(),
                    device.getType(), getFileInfoResponses(device.getFiles()));
        }
        return response;
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
     * @return List of device ids
     */
    public List<String> communicableEntitiesInRange(String id) {
        // TODO: Task 2 b)
        return new ArrayList<>();
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
     * Find a device by its ID.
     * @param deviceId
     * @return Device or null if not found
     */
    private Device findDevice(String deviceId) {
        for (Device device : devices) {
            if (device.getDeviceId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    /**
     * Find a satellite by its ID.
     * @param satelliteId
     * @return Satellite or null if not found
     */
    private Satellite findSatellite(String satelliteId) {
        for (Satellite satellite : satellites) {
            if (satellite.getSatelliteId().equals(satelliteId)) {
                return satellite;
            }
        }
        return null;
    }
}
