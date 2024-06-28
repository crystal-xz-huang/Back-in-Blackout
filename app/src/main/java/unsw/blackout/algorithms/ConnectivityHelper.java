package unsw.blackout.algorithms;

import java.util.*;
import unsw.blackout.entities.*;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

public class ConnectivityHelper {
    public static boolean hasRelayPath(Entity from, Entity to, List<Entity> relays) {
        Queue<Entity> queue = new LinkedList<>();
        Set<Entity> visited = new HashSet<>();

        visited.add(from);
        queue.add(from);

        while (!queue.isEmpty()) {
            Entity current = queue.poll();
            if (current.equals(to)) {
                return true;
            }

            for (Entity relay : relays) {
                if (!visited.contains(relay) && inRangeAndVisible(current, relay)) {
                    visited.add(relay);
                    queue.add(relay);
                }
            }

            if (inRangeAndVisible(current, to)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inRangeAndVisible(Entity from, Entity to) {
        double h1 = from.getHeight();
        double h2 = to.getHeight();
        Angle p1 = from.getPosition();
        Angle p2 = to.getPosition();
        if (from instanceof Satellite && to instanceof Satellite) {
            return getDistance(h1, p1, h2, p2) <= from.getRange() && isVisible(h1, p1, h2, p2);
        } else if (from instanceof Satellite && to instanceof Device) {
            return getDistance(h1, p1, p2) <= from.getRange() && isVisible(h1, p1, p2);
        } else if (from instanceof Device && to instanceof Satellite) {
            return getDistance(h2, p2, p1) <= from.getRange() && isVisible(h2, p2, p1);
        } else {
            return false;
        }
    }
}
