package blackout;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class MyTests {
    @Test
    public void testOutOfRangeStandard() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite", "StandardSatellite", 100000, Angle.fromDegrees(200));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(200));

        // create a message that is 35 bytes long
        byte[] msg = new byte[35];
        Arrays.fill(msg, (byte) 'a');
        String content = new String(msg);
        controller.addFileToDevice("DeviceA", "File", content);
        assertDoesNotThrow(() -> controller.sendFile("File", "DeviceA", "Satellite"));

        // takes a total 32 minutes for satellite to be out of range
        controller.simulate(30);

        // partially downloaded file should be removed from the satellite
        assertEquals(null, controller.getInfo("Satellite").getFiles().get("File"));
    }

    @Test
    public void testDeviceToTeleportingSatelliteTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite", "TeleportingSatellite", 80011, Angle.fromDegrees(179));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(207.1304));

        String content = "This is a test file and after satellite teleports there should be no remaining T bytes left";
        controller.addFileToDevice("DeviceA", "FileT", content);
        assertDoesNotThrow(() -> controller.sendFile("FileT", "DeviceA", "Satellite"));
        // expect file to be not fully downloaded on the satellite
        assertEquals(new FileInfoResponse("FileT", "", content.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileT"));
        // expect file to be fully downloaded on the device
        assertEquals(new FileInfoResponse("FileT", content, content.length(), true),
                controller.getInfo("DeviceA").getFiles().get("FileT"));

        // after 1 min, teleporting satellite expected to have 15/91 bytes downloaded: "This is a test"
        controller.simulate();
        assertEquals(new FileInfoResponse("FileT", "This is a test ", content.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileT"));
        // after 1 min, device expected to have complete file
        assertEquals(new FileInfoResponse("FileT", content, content.length(), true),
                controller.getInfo("DeviceA").getFiles().get("FileT"));

        // after 2 mins, satellite teleports and the file is removed from the satellite
        controller.simulate();
        // after 2 mins, satellite teleports and the file is removed from the satellite
        assertEquals(null, controller.getInfo("Satellite").getFiles().get("FileT"));
        // all T bytes are removed from the file on the device, with 81/81 bytes downloaded
        String expectedContent = "This is a es file and afer saellie elepors here should be no remaining T byes lef";
        assertEquals(new FileInfoResponse("FileT", expectedContent, expectedContent.length(), true),
                controller.getInfo("DeviceA").getFiles().get("FileT"));
    }

    @Test
    public void testTeleportingSatelliteTransfer() {
        BlackoutController controller = new BlackoutController();

        // SatelliteA teleports to 360 after 2 mins
        controller.createSatellite("SatelliteA", "TeleportingSatellite", 80011, Angle.fromDegrees(132));
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(120));

        String content = "This is a test file and after satellite teleports there should be no remaining T bytes left";
        int finalSize = content.length();
        controller.addFileToDevice("DeviceA", "FileA", content);

        // Send a file to SatelliteA
        assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "SatelliteA"));

        // expect file to be not fully downloaded on the satellite
        assertEquals(new FileInfoResponse("FileA", "", finalSize, false),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));

        // expect file to be fully downloaded on the device
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("DeviceA").getFiles().get("FileA"));

        // after 1 min, teleporting satellite expected to have 15/91 bytes downloaded: "This is a test"
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", "This is a test ", finalSize, false),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("DeviceA").getFiles().get("FileA"));

        // after 2 mins, expect "This is a test file and after" with 30/91 bytes downloaded
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", "This is a test file and after ", finalSize, false),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("DeviceA").getFiles().get("FileA"));

        // after 3 mins, expect "This is a test file and after satellite telep"
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", "This is a test file and after satellite telep", finalSize, false),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("DeviceA").getFiles().get("FileA"));

        // after 5 minutes, expect file to be fully downloaded on the satellite
        controller.simulate(5);
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("DeviceA").getFiles().get("FileA"));

        // attempt to send file to device: VirtualFileAlreadyExistsException:FileA
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
                () -> controller.sendFile("FileA", "SatelliteA", "DeviceA"));
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
                () -> controller.sendFile("FileA", "DeviceA", "SatelliteA"));

        // create a new SatelliteB that teleports to 0 after 2 mins
        controller.createSatellite("SatelliteB", "TeleportingSatellite", 80011, Angle.fromDegrees(179));

        // send the same file from SatelliteA to SatelliteB
        assertDoesNotThrow(() -> controller.sendFile("FileA", "SatelliteA", "SatelliteB"));

        // after 1 min, SatelliteB should have 5/91 bytes downloaded: "This "
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", "This is a ", finalSize, false),
                controller.getInfo("SatelliteB").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));

        // after 2 mins, SatelliteB teleports and remaining bytes are downloaded but all "t" bytes are removed
        String newData = "This is a es file and afer saellie elepors here should be no remaining T byes lef";
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", newData, newData.length(), true),
                controller.getInfo("SatelliteB").getFiles().get("FileA"));

        // SatelliteA should have the same file
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));
    }

    @Test
    public void testTeleportingSatelliteConcurrentTransfers() {
        BlackoutController controller = new BlackoutController();

        // SatelliteA teleports to 360 after 2 mins
        controller.createSatellite("SatelliteA", "TeleportingSatellite", 80011, Angle.fromDegrees(132));
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(120));

        String content = "This is a test file and after satellite teleports there should be no remaining T bytes left";
        int finalSize = content.length();
        controller.addFileToDevice("DeviceA", "FileA", content);

        // Send a file to SatelliteA
        assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "SatelliteA"));

        // expect file to fully downloaded after 10 mins
        controller.simulate(10);
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("DeviceA").getFiles().get("FileA"));

        // create a new SatelliteB that teleports to 0 after 2 mins and another satellite that does not
        controller.createSatellite("SatelliteB", "TeleportingSatellite", 80011, Angle.fromDegrees(179));
        controller.createSatellite("SatelliteC", "TeleportingSatellite", 80011, Angle.fromDegrees(100));

        assertDoesNotThrow(() -> controller.sendFile("FileA", "SatelliteA", "SatelliteB"));
        assertDoesNotThrow(() -> controller.sendFile("FileA", "SatelliteA", "SatelliteC"));

        // after 1 min, both have 5/91 bytes downloaded: "This "
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", "This ", finalSize, false),
                controller.getInfo("SatelliteB").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", "This ", finalSize, false),
                controller.getInfo("SatelliteC").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));

        // after 2 mins, SatelliteB teleports and remaining bytes are downloaded but all "t" bytes are removed
        controller.simulate();
        String newData = "This is a es file and afer saellie elepors here should be no remaining T byes lef";
        assertEquals(new FileInfoResponse("FileA", newData, newData.length(), true),
                controller.getInfo("SatelliteB").getFiles().get("FileA"));

        // SatelliteC does not teleport and has 15/91 bytes downloaded
        assertEquals(new FileInfoResponse("FileA", "This is a test ", finalSize, false),
                controller.getInfo("SatelliteC").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteA").getFiles().get("FileA"));

        // after 3 mins, SatelliteC has 25/91 bytes downloaded
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", "This is a test file and a", finalSize, false),
                controller.getInfo("SatelliteC").getFiles().get("FileA"));

        // after 4 mins, SatelliteC has 35/91 bytes downloaded
        controller.simulate();
        newData = "This is a test file and after satel";
        assertEquals(new FileInfoResponse("FileA", newData, finalSize, false),
                controller.getInfo("SatelliteC").getFiles().get("FileA"));

        // create a new SatelliteD that teleports after 2 mins
        // create 3 relays so that SatelliteB can reach SatelliteD
        controller.createSatellite("SatelliteD", "TeleportingSatellite", 80011, Angle.fromDegrees(179));
        controller.createSatellite("RelayA", "RelaySatellite", 85852, Angle.fromDegrees(46.0927));
        controller.createSatellite("RelayB", "RelaySatellite", 78949, Angle.fromDegrees(89.1854));
        controller.createSatellite("RelayC", "RelaySatellite", 80849, Angle.fromDegrees(123.5484));

        content = "This is a es file and afer saellie elepors here should be no remaining T byes lef";
        finalSize = content.length();

        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
                () -> controller.sendFile("FileA", "SatelliteB", "SatelliteA"));
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
                () -> controller.sendFile("FileA", "SatelliteB", "SatelliteC"));
        assertDoesNotThrow(() -> controller.sendFile("FileA", "SatelliteB", "SatelliteD"));

        // after 1 min, SatelliteD has 10/81 bytes downloaded
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", "This is a ", finalSize, false),
                controller.getInfo("SatelliteD").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteB").getFiles().get("FileA"));

        // after 2 mins, satelliteD teleports and remaining bytes are downloaded but all "t" bytes are removed
        controller.simulate();
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteD").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileA", content, finalSize, true),
                controller.getInfo("SatelliteB").getFiles().get("FileA"));
    }

    @Test
    public void testStandardTransferExceptions() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("SatelliteA", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(320));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(320));

        // Check bandwidth Exception - Standard can only send or receive 1 byte
        String msg = "Hey";
        controller.addFileToDevice("DeviceA", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceA", "SatelliteA"));

        controller.addFileToDevice("DeviceA", "FileBeta", msg);
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("FileBeta", "DeviceA", "SatelliteA"));

        // Check storage exception - Standard can only hold 3 files
        controller.simulate(msg.length());
        assertDoesNotThrow(() -> controller.sendFile("FileBeta", "DeviceA", "SatelliteA"));

        controller.simulate(msg.length());
        controller.addFileToDevice("DeviceA", "FileGamma", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileGamma", "DeviceA", "SatelliteA"));

        controller.simulate(msg.length());
        controller.addFileToDevice("DeviceA", "FileDelta", msg);
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                () -> controller.sendFile("FileDelta", "DeviceA", "SatelliteA"));

        // Check storage exception - Standard can only hold 80 bytes
        controller.createSatellite("SatelliteB", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(320));

        byte[] content = new byte[81];
        Arrays.fill(content, (byte) 'a');
        msg = new String(content);
        controller.addFileToDevice("DeviceA", "FileEpsilon", msg);
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                () -> controller.sendFile("FileEpsilon", "DeviceA", "SatelliteB"));

    }

    @Test
    public void testTeleportingTransferExceptions() {
        // 10000 + RADIUS_OF_JUPITER = 10000 + 69911 = 79911
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("SatelliteA", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(359));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(359));
        controller.createDevice("DeviceB", "DesktopDevice", Angle.fromDegrees(15));

        // check receiving bandwidth exception - teleporting can only receive 15 bytes (no more than 15 files)
        String msg = "Hi";
        // send 15 files (total 30 bytes) to the satellite
        for (int i = 1; i <= 15; i++) {
            final int index = i;
            controller.addFileToDevice("DeviceA", "File" + index, msg);
            assertDoesNotThrow(() -> controller.sendFile("File" + index, "DeviceA", "SatelliteA"));
        }

        // send 16th file (total 32 bytes) to the satellite => should throw exception
        controller.addFileToDevice("DeviceA", "File16", msg);
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("File16", "DeviceA", "SatelliteA"));

        // complete the transfer of the first 15 files
        controller.simulate(msg.length());

        // check sending bandwidth exception - teleporting can only send 10 bytes (no more than 10 files)
        // send 10 files (total 20 bytes) to deviceB
        for (int i = 1; i <= 10; i++) {
            final int index = i;
            assertDoesNotThrow(() -> controller.sendFile("File" + index, "SatelliteA", "DeviceB"));
        }

        // send 11th file (total 22 bytes) to deviceB => should throw exception
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("File11", "SatelliteA", "DeviceB"));

        // check storage exception - teleporting can store up to 200 bytes
        controller.createSatellite("SatelliteB", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(30));
        // send 200 bytes to the satelliteB
        String msg1 = "a".repeat(200);
        assertEquals(200, msg1.length());
        controller.addFileToDevice("DeviceB", "FileAlpha", msg1);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceB", "SatelliteB"));
        // send another 1 byte to the satelliteB => should throw exception
        controller.addFileToDevice("DeviceB", "FileBeta", "a");
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                () -> controller.sendFile("FileBeta", "DeviceB", "SatelliteB"));
    }

    @Test
    public void testEntitiesInRange() {
        BlackoutController controller = new BlackoutController();

        // Relays should show up in the list of entities in range BUT you cannot send files to them
        controller.createSatellite("1", "StandardSatellite", 78880, Angle.fromDegrees(0));
        controller.createSatellite("2", "RelaySatellite", 79113, Angle.fromDegrees(33.4653));
        controller.createSatellite("3", "TeleportingSatellite", 75854, Angle.fromDegrees(69.2667));
        controller.createSatellite("4", "StandardSatellite", 76868, Angle.fromDegrees(112.5332));
        controller.createSatellite("5", "StandardSatellite", 77889, Angle.fromDegrees(149.2775));
        controller.createSatellite("6", "TeleportingSatellite", 75402, Angle.fromDegrees(186.1701));
        controller.createSatellite("7", "RelaySatellite", 77516, Angle.fromDegrees(222.9833));
        controller.createSatellite("8", "TeleportingSatellite", 77784, Angle.fromDegrees(257.4711));
        controller.createSatellite("9", "RelaySatellite", 78387, Angle.fromDegrees(296.07));
        controller.createSatellite("10", "RelaySatellite", 77889, Angle.fromDegrees(329.2775));

        controller.createDevice("A", "HandheldDevice", Angle.fromDegrees(276.1701));
        controller.createDevice("B", "HandheldDevice", Angle.fromDegrees(202.6198));
        controller.createDevice("C", "HandheldDevice", Angle.fromDegrees(164.389));
        controller.createDevice("D", "HandheldDevice", Angle.fromDegrees(90));
        controller.createDevice("E", "HandheldDevice", Angle.fromDegrees(28.429));

        String msg = "Test File";
        controller.addFileToDevice("A", "FileA", msg);
        controller.addFileToDevice("B", "FileB", msg);
        controller.addFileToDevice("C", "FileC", msg);
        controller.addFileToDevice("D", "FileD", msg);
        controller.addFileToDevice("E", "FileE", msg);

        // checking devices
        String[] inRangeOfA = {
                "1", "8", "9", "10"
        };
        String[] inRangeOfB = {
                "6", "7", "8"
        };
        String[] inRangeOfC = {
                "5", "6"
        };
        String[] inRangeOfD = {
                "3", "4"
        };
        String[] inRangeOfE = {
                "1", "2", "3"
        };
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOfA), controller.communicableEntitiesInRange("A"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOfB), controller.communicableEntitiesInRange("B"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOfC), controller.communicableEntitiesInRange("C"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOfD), controller.communicableEntitiesInRange("D"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOfE), controller.communicableEntitiesInRange("E"));

        // checking satellites
        String[] inRangeOf1 = {
                "2", "3", "8", "9", "10", "E", "A"
        };
        String[] inRangeOf2 = {
                "1", "3", "E"
        };
        String[] inRangeOf3 = {
                "1", "2", "4", "D", "E"
        };
        String[] inRangeOf4 = {
                "3", "5", "D"
        };
        String[] inRangeOf5 = {
                "4", "6", "C"
        };
        String[] inRangeOf6 = {
                "5", "7", "8", "B", "C"
        };
        String[] inRangeOf7 = {
                "6", "8", "B"
        };
        String[] inRangeOf8 = {
                "1", "6", "7", "9", "10", "A", "B"
        };
        String[] inRangeOf9 = {
                "1", "8", "10", "A"
        };
        String[] inRangeOf10 = {
                "1", "8", "9", "A"
        };
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf1), controller.communicableEntitiesInRange("1"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf2), controller.communicableEntitiesInRange("2"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf3), controller.communicableEntitiesInRange("3"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf4), controller.communicableEntitiesInRange("4"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf5), controller.communicableEntitiesInRange("5"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf6), controller.communicableEntitiesInRange("6"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf7), controller.communicableEntitiesInRange("7"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf8), controller.communicableEntitiesInRange("8"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf9), controller.communicableEntitiesInRange("9"));
        assertListAreEqualIgnoringOrder(Arrays.asList(inRangeOf10), controller.communicableEntitiesInRange("10"));

        // sending files from devices to entities in range
        assertAll(() -> {
            // VirtualFileNoStorageSpaceException:Max Files Reached on 9 and 10
            assertDoesNotThrow(() -> controller.sendFile("FileA", "A", "1"));
            assertDoesNotThrow(() -> controller.sendFile("FileA", "A", "8"));
            assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                    () -> controller.sendFile("FileA", "A", "9"));
            assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                    () -> controller.sendFile("FileA", "A", "10"));
        });

        assertAll(() -> {
            // VirtualFileNoStorageSpaceException:Max Files Reached on 6 and 7
            assertDoesNotThrow(() -> controller.sendFile("FileB", "B", "6"));
            assertDoesNotThrow(() -> controller.sendFile("FileB", "B", "8"));
            assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                    () -> controller.sendFile("FileB", "B", "7"));
        });

        assertAll(() -> {
            for (String id : inRangeOfC) {
                assertDoesNotThrow(() -> controller.sendFile("FileC", "C", id));
            }
        });

        assertAll(() -> {
            for (String id : inRangeOfD) {
                assertDoesNotThrow(() -> controller.sendFile("FileD", "D", id));
            }
        });

        assertAll(() -> {
            // VirtualFileNoBandwidthException:1
            assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                    () -> controller.sendFile("FileE", "E", "1"));
            // VirtualFileNoStorageSpaceException:Max Files Reached on 2
            assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                    () -> controller.sendFile("FileE", "E", "2"));
            assertDoesNotThrow(() -> controller.sendFile("FileE", "E", "3"));
        });
    }

    @Test
    public void testEntitiesInRangeRelay() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 72369, Angle.fromDegrees(8.376));
        controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(53.2072));

        // Device1 not visible to any
        assertListAreEqualIgnoringOrder(java.util.Collections.emptyList(),
                controller.communicableEntitiesInRange("Device1"));

        controller.createSatellite("Relay1", "RelaySatellite", 76513, Angle.fromDegrees(32.7884));

        // Device1 can now communicate with Satellite1
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Relay1"),
                controller.communicableEntitiesInRange("Device1"));

        // Satellite1 can now communicate with all
        assertListAreEqualIgnoringOrder(Arrays.asList("Device1", "Relay1"),
                controller.communicableEntitiesInRange("Satellite1"));
    }

    @Test
    public void testRemoveSenderDuringTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 80000, Angle.fromDegrees(0));
        controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(30));
        controller.createSatellite("Relay1", "RelaySatellite", 83611, Angle.fromDegrees(39.1736));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Relay1"),
                controller.communicableEntitiesInRange("Device1"));

        // Creates a file and sends it to the satellite
        String msg = "Test File";
        controller.addFileToDevice("Device1", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Device1", "Satellite1"));

        controller.simulate();

        // Remove the device while the file is being transferred
        assertDoesNotThrow(() -> controller.removeDevice("Device1"));

        // Satellite should still have file but incomplete (1/9 bytes)
        assertEquals(new FileInfoResponse("FileAlpha", "T", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
    }

    @Test
    public void testOutOfRangeDuringTransfer() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(30));
        controller.createSatellite("Relay1", "RelaySatellite", 81259, Angle.fromDegrees(58.1282));
        controller.createSatellite("Relay2", "RelaySatellite", 80535, Angle.fromDegrees(113.334));
        controller.createSatellite("Relay3", "RelaySatellite", 81472, Angle.fromDegrees(156.012));

        // TeleportingSatellite1 teleports after 2 mins
        controller.createSatellite("Teleporting1", "TeleportingSatellite", 70011, Angle.fromDegrees(179));
        controller.createSatellite("Teleporting2", "TeleportingSatellite", 76819, Angle.fromDegrees(88.8308));

        assertListAreEqualIgnoringOrder(Arrays.asList("Relay1", "Relay2", "Relay3", "Teleporting1", "Teleporting2"),
                controller.communicableEntitiesInRange("Device1"));

        // Creates a file and sends it to the satellite1 and satellite2
        String msg = "This is a test file with many tttttsss";
        controller.addFileToDevice("Device1", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Device1", "Teleporting1"));
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Device1", "Teleporting2"));

        controller.simulate();

        // Check that each have 15/38 bytes downloaded
        assertEquals(new FileInfoResponse("FileAlpha", "This is a test ", msg.length(), false),
                controller.getInfo("Teleporting1").getFiles().get("FileAlpha"));

        assertEquals(new FileInfoResponse("FileAlpha", "This is a test ", msg.length(), false),
                controller.getInfo("Teleporting2").getFiles().get("FileAlpha"));

        controller.simulate();
        // Check that teleporting1 has no file after teleporting
        assertEquals(null, controller.getInfo("Teleporting1").getFiles().get("FileAlpha"));
        // Device has t bytes removed
        assertEquals(new FileInfoResponse("FileAlpha", "This is a es file wih many sss", 30, true),
                controller.getInfo("Device1").getFiles().get("FileAlpha"));
        // Teleporting2 has 30/38 bytes downloaded
        assertEquals(new FileInfoResponse("FileAlpha", "This is a test file with many ", msg.length(), false),
                controller.getInfo("Teleporting2").getFiles().get("FileAlpha"));

        controller.simulate();
        // Teleporting2 out of range and file is removed
        assertEquals(null, controller.getInfo("Teleporting2").getFiles().get("FileAlpha"));
    }
}
