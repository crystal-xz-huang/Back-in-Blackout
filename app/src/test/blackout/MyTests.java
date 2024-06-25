package blackout;

import org.junit.jupiter.api.Test;
import java.util.Arrays;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class MyTests {
    @Test
    public void testStandardMovement() {
        final double height = 70011;
        final Angle position = Angle.fromDegrees(250);
        final String id = "Standard";
        final String type = "StandardSatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);
        assertEquals(new EntityInfoResponse(id, position, height, type), controller.getInfo(id));

        // After 1 minute: 247.954
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(247.954), height, type), controller.getInfo(id));

        // After 11 minutes: 227.4944
        controller.simulate(10);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(227.4944), height, type), controller.getInfo(id));

        // After 21 minutes: 207.0348
        controller.simulate(10);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(207.0348), height, type), controller.getInfo(id));

        // After 24 minutes: 200.897
        controller.simulate(3);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(200.897), height, type), controller.getInfo(id));

        // After 84 minutes: 78.1396
        controller.simulate(60);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(78.1396), height, type), controller.getInfo(id));

        // After 144 minutes: 315.3822
        controller.simulate(60);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(315.3822), height, type), controller.getInfo(id));

        // After 204 minutes: 192.6249
        controller.simulate(60);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(192.6249), height, type), controller.getInfo(id));

        // After 324 minutes: 307.1101
        controller.simulate(120);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(307.1101), height, type), controller.getInfo(id));
    }

    @Test
    public void testRelayMovementAtThreshold() {
        final double height = 70011;
        final Angle position = Angle.fromDegrees(345);
        final String id = "Relay1";
        final String type = "RelaySatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);
        assertEquals(new EntityInfoResponse(id, position, height, type), controller.getInfo(id));

        // After 12 minutes: The satellite is just before crossing 360° boundary (359.7308°)
        controller.simulate(12);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(359.7308), height, type), controller.getInfo(id));

        // After 13 minutes: The satellite crosses the 360° boundary (0.9584°)
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0.9584), height, type), controller.getInfo(id));

        // After 127 minutes: The satellite reaches 140° (140.9018°)
        controller.simulate(114);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.9018), height, type), controller.getInfo(id));

        // After 167 minutes: The satellite reaches 190° and reverses direction (190.0048°)
        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.0048), height, type), controller.getInfo(id));

        // After 168 minutes: The satellite reverses direction (188.7772°)
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(188.7772), height, type), controller.getInfo(id));

        // After 207 minutes: The satellite is just before the 140° boundary (140.9018°)
        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.9018), height, type), controller.getInfo(id));

        // After 208 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.6742), height, type), controller.getInfo(id));

        // After 209 minutes: The satellite is at 140.9018° and reverses direction
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.9018), height, type), controller.getInfo(id));

        // After 210 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(142.1294), height, type), controller.getInfo(id));

        // After 249 minutes: The satellite is crosses the 190° boundary (190.0048°) and reverses direction
        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.0048), height, type), controller.getInfo(id));

        // After 250 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(188.7772), height, type), controller.getInfo(id));

        // After 290 minutes
        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.6742), height, type), controller.getInfo(id));

        // After 291 minutes: The satellite is crosses the 140° boundary and reverses direction
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.9018), height, type), controller.getInfo(id));

        // After 292 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(142.1294), height, type), controller.getInfo(id));

        // After 331 minutes
        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.0048), height, type), controller.getInfo(id));

        // After 332 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(188.7772), height, type), controller.getInfo(id));

        // After 372 minutes
        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.6742), height, type), controller.getInfo(id));
    }

    @Test
    public void testRelayMovementBeforeLowerBound() {
        final double height = 70011;
        final Angle position = Angle.fromDegrees(139);
        final String id = "Relay2";
        final String type = "RelaySatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);
        assertEquals(new EntityInfoResponse(id, position, height, type), controller.getInfo(id));
        /**
         * 1 m: 140.2275
         * 41 m: 189.3305
         * 42 m: 190.5581
         * 43 m: 189.3305
         * 44 m: 188.1029
         * 45 m: 186.8753
         * 83 m: 140.2275
         * 84 m: 138.9999
         * 85 m: 140.2275
         * 86 m: 141.4551
         * 125 m: 189.3305
         * 126 m: 190.558
         * 127 m: 189.3305
         * 167 m: 140.2275
         * 168 m: 138.9999
         * 169 m: 140.2275
         */
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.2275), height, type), controller.getInfo(id));

        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189.3305), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.5581), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189.3305), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(188.1029), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(186.8753), height, type), controller.getInfo(id));

        controller.simulate(38);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.2275), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(138.9999), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.2275), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(141.4551), height, type), controller.getInfo(id));

        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189.3305), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.558), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189.3305), height, type), controller.getInfo(id));

        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.2275), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(138.9999), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.2275), height, type), controller.getInfo(id));
    }

    @Test
    public void testRelayMovementAfterLowerBound() {
        final double height = 70011;
        final Angle position = Angle.fromDegrees(141);
        final String id = "Relay3";
        final String type = "RelaySatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);
        assertEquals(new EntityInfoResponse(id, position, height, type), controller.getInfo(id));
        /**
         * 1 m: 139.7724
         * 41 m: 188.8753
         * 42 m: 190.1029
         * 43 m: 188.8753
         * 82 m: 140.9999
         * 83 m: 139.7724
         * 84 m: 140.9999
         * 123 m: 188.8753
         * 124 m: 190.1029
         * 125 m: 188.8753
         */
        System.out.println("Initial position: " + controller.getInfo(id));
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.7724), height, type), controller.getInfo(id));

        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(188.8753), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.1029), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(188.8753), height, type), controller.getInfo(id));

        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.9999), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.7724), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.9999), height, type), controller.getInfo(id));

        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(188.8753), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.1029), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(188.8753), height, type), controller.getInfo(id));
    }

    @Test
    public void testRelayMovementBeforeUpperBound() {
        final double height = 70011;
        final Angle position = Angle.fromDegrees(189);
        final String id = "Relay4";
        final String type = "RelaySatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);
        assertEquals(new EntityInfoResponse(id, position, height, type), controller.getInfo(id));
        /**
         * 1 m: 187.7724
         * 39 m: 141.1246
         * 40 m: 139.897
         * 41 m: 141.1246
         * 80 m: 189
         * 81 m: 190.2275
         * 82 m: 189
         * 121 m: 141.1246
         * 122 m: 139.897
         * 123 m: 141.1246
         * 162 m: 189
         * 163 m: 190.2275
         * 164 m: 189
         */
        System.out.println("Initial position: " + controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(187.7724), height, type), controller.getInfo(id));

        controller.simulate(38);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(141.1246), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.897), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(141.1246), height, type), controller.getInfo(id));

        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.2275), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189), height, type), controller.getInfo(id));

        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(141.1246), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.897), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(141.1246), height, type), controller.getInfo(id));

        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(190.2275), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189), height, type), controller.getInfo(id));
    }

    @Test
    public void testRelayMovementAfterUpperBound() {
        final double height = 70011;
        final Angle position = Angle.fromDegrees(191);
        final String id = "Relay5";
        final String type = "RelaySatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);
        assertEquals(new EntityInfoResponse(id, position, height, type), controller.getInfo(id));
        /**
         * 1 m: 189.7724
         * 41 m: 140.6694
         * 42 m: 139.4419
         * 43 m: 140.6694
         * 83 m: 189.7724
         * 84 m: 191
         * 85 m: 189.7724
         * 125 m: 140.6694
         * 126 m: 139.4419
         * 127 m: 140.6694
         */
        System.out.println("Initial position: " + controller.getInfo(id));
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189.7724), height, type), controller.getInfo(id));

        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.6694), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.4419), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.6694), height, type), controller.getInfo(id));

        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189.7724), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(191), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(189.7724), height, type), controller.getInfo(id));

        controller.simulate(40);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.6694), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(139.4419), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(140.6694), height, type), controller.getInfo(id));
    }

    @Test
    public void testRelayMovementEdgeCases() {
        final double height = 70011;
        final Angle position1 = Angle.fromDegrees(1);
        final Angle position2 = Angle.fromDegrees(359);
        final String id1 = "Relay6";
        final String id2 = "Relay7";
        final String type = "RelaySatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id1, type, height, position1);
        controller.createSatellite(id2, type, height, position2);
        assertEquals(new EntityInfoResponse(id1, position1, height, type), controller.getInfo(id1));
        assertEquals(new EntityInfoResponse(id2, position2, height, type), controller.getInfo(id2));

        System.out.println("Initial position: " + controller.getInfo(id2));

        /**
         * Satellite at 1°
         * 1 m: 2.2275
         * 2 m: 3.4551
         * 114 m: 140.9434
         * 153 m: 188.8187
         * 154 m: 190.0463
         * 155 m: 188.8187
         * 194 m: 140.9434
         * 195 m: 139.7158
         * 196 m: 140.9434
         */

        /**
         * Satellite at 359°
         * 1 m: 0.2275
         * 2 m: 1.4551
         * 155 m: 189.2739
         * 156 m: 190.5015
         * 157 m: 189.2739
         * 197 m: 140.1709
         * 198 m: 138.9434
         * 199 m: 140.1709
         */

        controller.simulate();
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(2.2275), height, type), controller.getInfo(id1));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(0.2275), height, type), controller.getInfo(id2));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(3.4551), height, type), controller.getInfo(id1));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(1.4551), height, type), controller.getInfo(id2));

        // At 114 minutes
        controller.simulate(112);
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(140.9434), height, type), controller.getInfo(id1));

        // At 153 minutes
        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(188.8187), height, type), controller.getInfo(id1));

        // At 154 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(190.0463), height, type), controller.getInfo(id1));

        // At 155 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(188.8187), height, type), controller.getInfo(id1));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(189.2739), height, type), controller.getInfo(id2));

        // At 194 minutes
        controller.simulate(39);
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(140.9434), height, type), controller.getInfo(id1));

        // At 195 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(139.7158), height, type), controller.getInfo(id1));

        // At 196 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id1, Angle.fromDegrees(140.9434), height, type), controller.getInfo(id1));

        // At 197 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(140.1709), height, type), controller.getInfo(id2));

        // At 198 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(138.9434), height, type), controller.getInfo(id2));

        // At 199 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(140.1709), height, type), controller.getInfo(id2));

        // At 200 minutes
        controller.simulate();
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(141.3985), height, type), controller.getInfo(id2));

    }

    @Test
    public void testTeleportingMovementBefore180() {
        // Start before 180° (179°)
        final double height = 70011;
        final Angle position = Angle.fromDegrees(179);
        final String id = "Teleporting1";
        final String type = "TeleportingSatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);
        // 1 m: 179.8183
        // 2 m: 360
        // 222 m: 0
        // 442 m: 360.0
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(179.8183), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(360), height, type), controller.getInfo(id));

        controller.simulate(220);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0), height, type), controller.getInfo(id));

        controller.simulate(220);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(360), height, type), controller.getInfo(id));
    }

    @Test
    public void testTeleportingMovementAt180() {
        // Start on 180°
        final double height = 70011;
        final Angle position = Angle.fromDegrees(180);
        final String id = "Teleporting2";
        final String type = "TeleportingSatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);

        System.out.println("Initial position: " + controller.getInfo(id));
        // 1 m: 180.8183
        // 2 m: 181.6367
        // 219 m: 359.2257
        // 220 m: 0.0441
        // 440 m: 360.0
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(180.8183), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(181.6367), height, type), controller.getInfo(id));

        controller.simulate(217);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(359.2257), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0.0441), height, type), controller.getInfo(id));

        controller.simulate(220);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(360), height, type), controller.getInfo(id));
    }

    @Test
    public void testTeleportingMovementAfter180() {
        // Start after 180° (181°)
        final double height = 70011;
        final Angle position = Angle.fromDegrees(181);
        final String id = "Teleporting2";
        final String type = "TeleportingSatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);

        System.out.println("Initial position: " + controller.getInfo(id));
        // 1 m: 181.8183
        // 218 m: 359.4073
        // 219 m: 0.2257
        // 220 m: 1.0441
        // 439 m: 360.0
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(181.8183), height, type), controller.getInfo(id));

        controller.simulate(217);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(359.4073), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0.2257), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(1.0441), height, type), controller.getInfo(id));

        controller.simulate(219);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(360), height, type), controller.getInfo(id));
    }

    @Test
    public void testTeleportingMovementat360() {
        // Start on 0° (360° and 0°)
        final double height = 70011;
        final Angle position = Angle.fromDegrees(360);
        final Angle position2 = Angle.fromDegrees(0);
        final String id = "Teleporting";
        final String id2 = "Teleporting2";
        final String type = "TeleportingSatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);
        controller.createSatellite(id2, type, height, position2);

        System.out.println("Initial position: " + controller.getInfo(id));
        // 1 m: 0.8183
        // 2 m: 1.6367
        // 219 m: 179.2257
        // 220 m: 360.0
        // 221 m: 359.1816
        // 439 m: 180.7742
        // 440 m: 0
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0.8183), height, type), controller.getInfo(id));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(0.8183), height, type), controller.getInfo(id2));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(1.6367), height, type), controller.getInfo(id));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(1.6367), height, type), controller.getInfo(id2));

        controller.simulate(217);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(179.2257), height, type), controller.getInfo(id));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(179.2257), height, type), controller.getInfo(id2));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(360), height, type), controller.getInfo(id));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(360), height, type), controller.getInfo(id2));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(359.1816), height, type), controller.getInfo(id));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(359.1816), height, type), controller.getInfo(id2));

        controller.simulate(218);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(180.7742), height, type), controller.getInfo(id));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(180.7742), height, type), controller.getInfo(id2));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0), height, type), controller.getInfo(id));
        assertEquals(new EntityInfoResponse(id2, Angle.fromDegrees(0), height, type), controller.getInfo(id2));
    }

    @Test
    public void testTeleportingMovementbefore360() {
        // Start before 0° (359)
        final double height = 70011;
        final Angle position = Angle.fromDegrees(359);
        final String id = "Teleporting";
        final String type = "TeleportingSatellite";

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);

        System.out.println("Initial position: " + controller.getInfo(id));

        // 1 m: 359.8183
        // 2 m: 0.6367
        // 3 m: 1.4551
        // 222 m: 360
        // 442 m: 0
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(359.8183), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0.6367), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(1.4551), height, type), controller.getInfo(id));

        controller.simulate(219);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(360), height, type), controller.getInfo(id));

        controller.simulate(220);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0), height, type), controller.getInfo(id));
    }

    @Test
    public void testTeleportingMovemenafter360() {
        // Start after 0° (1°)
        final double height = 70011;
        final Angle position = Angle.fromDegrees(1);
        final String id = "Teleporting";
        final String type = "TeleportingSatellite";

        // 1 m: 1.8183
        // 2 m: 2.6367
        // 122 m: 100.8426
        // 219 m: 360.0
        // 439 m: 0
        // 440 m: 0.8183

        BlackoutController controller = new BlackoutController();
        controller.createSatellite(id, type, height, position);

        System.out.println("Initial position: " + controller.getInfo(id));
        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(1.8183), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(2.6367), height, type), controller.getInfo(id));

        controller.simulate(120);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(100.8426), height, type), controller.getInfo(id));

        controller.simulate(97);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(360), height, type), controller.getInfo(id));

        controller.simulate(220);
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0), height, type), controller.getInfo(id));

        controller.simulate();
        assertEquals(new EntityInfoResponse(id, Angle.fromDegrees(0.8183), height, type), controller.getInfo(id));
    }

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
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("SatelliteA", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(359));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(359));

        // Check receiving bandwidth Exception - Teleporting can only receive 15 bytes
        // If we have more than 15 files, the satellite will not be able to receive any more files
        String msg = "Hi";
        controller.addFileToDevice("DeviceA", "FileA", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileA", "DeviceA", "SatelliteA"));
        for (int i = 0; i < 14; i++) {
            final int index = i; // declare a final local variable inside the loop
            controller.addFileToDevice("DeviceA", "File" + index, msg);
            assertDoesNotThrow(() -> controller.sendFile("File" + index, "DeviceA", "SatelliteA"));
        }
        controller.addFileToDevice("DeviceA", "FileBeta", msg);
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("FileBeta", "DeviceA", "SatelliteA"));
        controller.simulate(msg.length());

        // Check sending bandwidth Exception - Teleporting can only send 10 bytes per minute (no more than 10 files)
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(359));
        for (int i = 0; i < 10; i++) {
            final int index = i; // declare a final local variable inside the loop
            assertDoesNotThrow(() -> controller.sendFile("File" + index, "SatelliteA", "DeviceB"));
        }
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("FileA", "SatelliteA", "DeviceB"));

        // Check storage exception-Teleporting can store up to 200 bytes
        controller.createSatellite("SatelliteB", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(359));
        Byte[] content = new Byte[200];
        msg = content.toString();
        controller.addFileToDevice("DeviceB", "FileEpsilon", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileEpsilon", "DeviceB", "SatelliteB"));
        controller.addFileToDevice("DeviceB", "FileB", "1");
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                () -> controller.sendFile("FileB", "DeviceB", "SatelliteB"));
    }

    @Test
    public void testMultipleFilesAtOnce() {
        BlackoutController controller = new BlackoutController();
        // 10000 + RADIUS_OF_JUPITER = 10000 + 69911 = 79911
        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(320));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        controller.addFileToDevice("DeviceC", "FileBeta", msg);

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertDoesNotThrow(() -> controller.sendFile("FileBeta", "DeviceC", "Satellite1"));

        controller.simulate();

        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
    }

    @Test
    public void testAroundTheWorld() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("1", "StandardSatellite", 78880, Angle.fromDegrees(118));
        controller.createSatellite("2", "RelaySatellite", 78783, Angle.fromDegrees(71));
        controller.createSatellite("3", "RelaySatellite", 79204, Angle.fromDegrees(24));
        controller.createSatellite("4", "RelaySatellite", 76868, Angle.fromDegrees(335));
        controller.createSatellite("5", "RelaySatellite", 77307, Angle.fromDegrees(317));
        controller.createSatellite("6", "RelaySatellite", 77961, Angle.fromDegrees(275));
        controller.createSatellite("7", "RelaySatellite", 78762, Angle.fromDegrees(235));
        controller.createSatellite("8", "RelaySatellite", 79726, Angle.fromDegrees(189));

        controller.createDevice("Device", "HandheldDevice", Angle.fromDegrees(186));

        String msg = "Hey";
        controller.addFileToDevice("Device", "FileAlpha", msg);

        assertListAreEqualIgnoringOrder(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"),
                controller.communicableEntitiesInRange("Device"));
    }

    @Test
    public void testInRangeExtendedByRelayIsNotSupported() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "RelaySatellite", 88568, Angle.fromDegrees(112));
        controller.createSatellite("Satellite2", "StandardSatellite", 82871, Angle.fromDegrees(151));
        controller.createDevice("Device1", "DesktopDevice", Angle.fromDegrees(80));

        // Device1 is not in sight of Satellite2
        assertTrue(!MathsHelper.isVisible(82871, Angle.fromDegrees(151), Angle.fromDegrees(80)));

        // Device1 cannot communicate with Satellite2, as StandardSatellites cannot communicate with DesktopDevice
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.communicableEntitiesInRange("Device1"));
    }

    @Test
    public void testOutOfRange() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "StandardSatellite", 80000, Angle.fromDegrees(0));
        controller.createDevice("Device1", "HandheldDevice", Angle.fromDegrees(30));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.communicableEntitiesInRange("Device1"));

        // Creates a file and sends it to the satellite
        String msg = "Hey";
        controller.addFileToDevice("Device1", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Device1", "Satellite1"));

        controller.simulate();

        // Satellite goes out of range
        assertListAreEqualIgnoringOrder(java.util.Collections.emptyList(),
                controller.communicableEntitiesInRange("Device1"));

        // The file transfer is cancelled
        assertTrue(controller.getInfo("Satellite1").getFiles().isEmpty());
    }

}
