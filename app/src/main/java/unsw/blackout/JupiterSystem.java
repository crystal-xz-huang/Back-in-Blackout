package unsw.blackout;

import java.util.*;

import unsw.blackout.entities.Device;
import unsw.blackout.entities.Entity;
import unsw.blackout.entities.Satellite;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.getDistance;
import static unsw.utils.MathsHelper.isVisible;

public abstract class JupiterSystem {
    private Map<String, Entity> entities;

    public JupiterSystem() {
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

    public boolean inRangeAndVisible(Entity from, Entity to) {
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

    public abstract boolean canCommunicate(Entity from, Entity to);
}
