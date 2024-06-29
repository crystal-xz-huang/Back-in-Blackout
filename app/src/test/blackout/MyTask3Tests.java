package blackout;

import org.junit.jupiter.api.Test;
import java.util.Arrays;

import unsw.blackout.BlackoutController;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static blackout.MyTestHelpers.getNewPosition;
import static blackout.MyTestHelpers.minutesUntilInRange;
import static unsw.utils.MathsHelper.CLOCKWISE;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

public class MyTask3Tests {
    @Test
    public void testResumeTransientTransfer() {
        final double velocity = 2500;
        final double satelliteHeight = 72000;
        final Angle satelliteAngle = Angle.fromDegrees(100);
        final Angle deviceAngle = Angle.fromDegrees(110);
        final double deviceRange = 50000;

        BlackoutController controller = new BlackoutController();
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(110));
        controller.createSatellite("Satellite", "ElephantSatellite", satelliteHeight, satelliteAngle);

        byte[] content = new byte[50];
        Arrays.fill(content, (byte) 'a');
        String msg = new String(content);
        controller.addFileToDevice("DeviceA", "File", msg);
        assertDoesNotThrow(() -> controller.sendFile("File", "DeviceA", "Satellite"));

        // receives 20 bytes/min
        controller.simulate();
        String expectedData = msg.substring(0, 20);
        assertEquals(new FileInfoResponse("File", expectedData, msg.length(), false),
                controller.getInfo("Satellite").getFiles().get("File"));

        // goes out of range and marks the file as transient
        controller.simulate();
        assertEquals(new FileInfoResponse("File", expectedData, msg.length(), false),
                controller.getInfo("Satellite").getFiles().get("File"));

        Angle currentPosition = getNewPosition(velocity, satelliteHeight, satelliteAngle, CLOCKWISE, 2);
        int mins = minutesUntilInRange(satelliteHeight, currentPosition, deviceAngle, deviceRange, 0, velocity,
                CLOCKWISE);
        controller.simulate(mins);
        // check it is now in range
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite"), controller.communicableEntitiesInRange("DeviceA"));

        // file should resume transfer once in range
        expectedData = msg.substring(0, 40);
        assertEquals(new FileInfoResponse("File", expectedData, msg.length(), false),
                controller.getInfo("Satellite").getFiles().get("File"));
    }

    @Test
    public void testDeleteOneTransientTransfer() {
        final double velocity = 2500;
        final double satelliteHeight = 72500;
        final Angle satelliteAngle = Angle.fromDegrees(100);
        final Angle deviceAngle = Angle.fromDegrees(110);
        final double deviceRange = 50000;

        BlackoutController controller = new BlackoutController();
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(110));
        controller.createSatellite("Satellite", "ElephantSatellite", satelliteHeight, satelliteAngle);

        // receives 20 bytes/min
        byte[] content = new byte[20];
        Arrays.fill(content, (byte) 'a');
        String msg1 = new String(content);

        content = new byte[30];
        Arrays.fill(content, (byte) 'b');
        String msg2 = new String(content);

        content = new byte[10];
        Arrays.fill(content, (byte) 'c');
        String msg3 = new String(content);

        // Add FileA (20 bytes), FileB (30 bytes), FileC (10 bytes) to DeviceA = total 60 bytes
        controller.addFileToDevice("DeviceA", "FileA", msg1);
        controller.addFileToDevice("DeviceA", "FileB", msg2);

        assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "Satellite"));
        assertDoesNotThrow(() -> controller.sendFile("FileB", "DeviceA", "Satellite"));

        // after 1 min, each file receives 10 bytes/min = FileA (10/20), FileB (10/30)
        controller.simulate();
        String data1 = msg1.substring(0, 10);
        String data2 = msg2.substring(0, 10);
        assertEquals(new FileInfoResponse("FileA", data1, msg1.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileB", data2, msg2.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileB"));

        // add FileC (10 bytes) to DeviceA = total 70 bytes
        controller.addFileToDevice("DeviceA", "FileC", msg3);
        assertDoesNotThrow(() -> controller.sendFile("FileC", "DeviceA", "Satellite"));
        assertEquals(new FileInfoResponse("FileC", "", msg3.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileC"));

        // each file now receives 6 bytes/min = FileA (16/20), FileB (16/30), FileC (6/10) after 1 min
        controller.simulate();
        data1 = msg1.substring(0, 16);
        data2 = msg2.substring(0, 16);
        String data3 = msg3.substring(0, 6);
        assertEquals(new FileInfoResponse("FileA", data1, msg1.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileB", data2, msg2.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileB"));
        assertEquals(new FileInfoResponse("FileC", data3, msg3.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileC"));

        // out of range after 3 mins and marks FileA, FileB, FileC as transient
        controller.simulate();
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceA"));
        Angle currentPosition = getNewPosition(velocity, satelliteHeight, satelliteAngle, CLOCKWISE, 3);
        int mins = minutesUntilInRange(satelliteHeight, currentPosition, deviceAngle, deviceRange, 0, velocity,
                CLOCKWISE);

        // add a new transfer (FileE) to the satellite and FileC is deleted because it is less valuable (6/10 bytes)
        controller.simulate(mins - 1);
        content = new byte[40];
        Arrays.fill(content, (byte) 'd');
        String msg4 = new String(content);
        controller.addFileToDevice("DeviceA", "FileD", msg4);
        assertDoesNotThrow(() -> controller.sendFile("FileD", "DeviceA", "Satellite"));

        assertEquals(new FileInfoResponse("FileA", data1, msg1.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileB", data2, msg2.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileB"));
        assertEquals(null, controller.getInfo("Satellite").getFiles().get("FileC"));
        assertEquals(new FileInfoResponse("FileD", "", msg4.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileD"));

        // FileA, FileB, FileD should resume transfer once in range = 6 bytes/min
        // FileA is now complete
        controller.simulate();
        data2 = msg2.substring(0, 22);
        String data4 = msg4.substring(0, 6);
        assertEquals(new FileInfoResponse("FileA", msg1, msg1.length(), true),
                controller.getInfo("Satellite").getFiles().get("FileA"));
        assertEquals(new FileInfoResponse("FileB", data2, msg2.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileB"));
        assertEquals(new FileInfoResponse("FileD", data4, msg4.length(), false),
                controller.getInfo("Satellite").getFiles().get("FileD"));
    }

}
