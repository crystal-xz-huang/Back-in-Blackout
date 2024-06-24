package unsw.blackout;

import java.util.*;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

public abstract class SpaceSystem {
    private Map<String, Entity> entities;

    public SpaceSystem() {
        this.entities = new HashMap<>();
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public void removeEntity(String entityId) {
        entities.remove(entityId);
    }

    public Entity getEntity(String entityId) {
        return entities.get(entityId);
    }

    public List<Entity> listEntities() {
        return new ArrayList<>(entities.values());
    }

    public List<String> listEntityIds() {
        return new ArrayList<>(entities.keySet());
    }

    public boolean inRangeAndVisible(Entity src, Entity dest) {
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

    public abstract boolean canCommunicate(Entity src, Entity dest);
}
