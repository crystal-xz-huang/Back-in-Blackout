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

    public void addEntity(SpaceEntity entity) {
        entities.put(entity.getId(), entity);
    }

    public void removeEntity(String entityId) {
        entities.remove(entityId);
    }

    public SpaceEntity getEntity(String entityId) {
        return entities.get(entityId);
    }

    public List<SpaceEntity> listEntities() {
        return new ArrayList<>(entities.values());
    }

    public List<String> listEntityIds() {
        return new ArrayList<>(entities.keySet());
    }

    public List<Satellite> listSatellites() {
        return listEntities().stream().filter(e -> e instanceof Satellite).map(e -> (Satellite) e)
                .collect(Collectors.toList());
    }

    public List<Device> listDevices() {
        return listEntities().stream().filter(e -> e instanceof Device).map(e -> (Device) e)
                .collect(Collectors.toList());
    }

    public void simulate() {
        for (Satellite satellite : listSatellites()) {
            satellite.orbit(1);
        }
    }

    public boolean canCommunicate(SpaceEntity src, SpaceEntity dest) {
        if (src.equals(dest) || src instanceof RelaySatellite || dest instanceof RelaySatellite) {
            return false;
        } else if (src.supports(dest) && inRangeAndVisible(src, dest)) {
            return true;
        } else {
            return hasRelayPath(src, dest);
        }
    }

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
