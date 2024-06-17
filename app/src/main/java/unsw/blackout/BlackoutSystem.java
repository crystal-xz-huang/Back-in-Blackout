package unsw.blackout;

import java.util.*;
import java.util.stream.Collectors;

public class BlackoutSystem extends SpaceSystem {
    private FileSystem fileSystem;

    public BlackoutSystem() {
        super();
        fileSystem = new FileSystem();
    }

    @Override
    public boolean canCommunicate(SpaceEntity src, SpaceEntity dest) {
        if (src.equals(dest) || src instanceof RelaySatellite || dest instanceof RelaySatellite) {
            return false;
        } else if (src.supports(dest) && inRangeAndVisible(src, dest)) {
            return true;
        } else {
            return hasRelayPath(src, dest);
        }
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        SpaceEntity fromEntity = getEntity(fromId);
        SpaceEntity toEntity = getEntity(toId);
        fileSystem.sendFile(fromEntity, toEntity, fileName);
    }

    public void transferFiles() {
        fileSystem.updateTransfers();
    }

    public void moveSatellites() {
        for (Satellite satellite : listSatellites()) {
            satellite.orbit();
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

    private List<Satellite> listSatellites() {
        return listEntities().stream().filter(e -> e instanceof Satellite).map(e -> (Satellite) e)
                .collect(Collectors.toList());
    }

    private List<SpaceEntity> listRelaySatellites() {
        return listEntities().stream().filter(e -> e instanceof RelaySatellite).collect(Collectors.toList());
    }

}
