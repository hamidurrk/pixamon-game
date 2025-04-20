package com.main.lutemon.model.storage;

import com.main.lutemon.model.lutemon.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Storage implements Serializable {
    private static final long serialVersionUID = 1L;
    private static volatile Storage instance;
    private final Map<Integer, Lutemon> lutemons;
    private final Map<Integer, Location> lutemonLocations;
    private int nextId;

    private Storage() {
        lutemons = new ConcurrentHashMap<>();
        lutemonLocations = new ConcurrentHashMap<>();
        nextId = 1;
    }

    public static Storage getInstance() {
        if (instance == null) {
            synchronized (Storage.class) {
                if (instance == null) {
                    instance = new Storage();
                }
            }
        }
        return instance;
    }

    public synchronized void addLutemon(Lutemon lutemon) {
        if (lutemon == null) {
            throw new IllegalArgumentException("Lutemon cannot be null");
        }
        lutemon.setId(nextId++);
        lutemons.put(lutemon.getId(), lutemon);
        lutemonLocations.put(lutemon.getId(), Location.HOME);
    }

    public synchronized void moveToLocation(int lutemonId, Location location) {
        if (!lutemons.containsKey(lutemonId)) {
            throw new IllegalArgumentException("Invalid Lutemon ID: " + lutemonId);
        }
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        lutemonLocations.put(lutemonId, location);
    }

    public List<Lutemon> getLutemonsAtLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        List<Lutemon> result = new ArrayList<>();
        lutemonLocations.entrySet().stream()
            .filter(entry -> entry.getValue() == location)
            .forEach(entry -> {
                Lutemon lutemon = lutemons.get(entry.getKey());
                if (lutemon != null) {
                    result.add(lutemon);
                }
            });
        return Collections.unmodifiableList(result);
    }

    public List<Lutemon> getAllLutemons() {
        return Collections.unmodifiableList(new ArrayList<>(lutemons.values()));
    }

    public synchronized void clear() {
        lutemons.clear();
        lutemonLocations.clear();
        nextId = 1;
    }

    public void trainLutemon(int id) {
        Lutemon lutemon = lutemons.get(id);
        if (lutemon != null && lutemonLocations.get(id) == Location.TRAINING) {
            lutemon.train();
        }
    }

    public Lutemon getLutemon(int id) {
        return lutemons.get(id);
    }

    public Location getLutemonLocation(int id) {
        return lutemonLocations.get(id);
    }

    public synchronized void removeLutemon(int id) {
        lutemons.remove(id);
        lutemonLocations.remove(id);
    }

    public void healAllAtHome() {
        lutemonLocations.entrySet().stream()
            .filter(entry -> entry.getValue() == Location.HOME)
            .forEach(entry -> {
                Lutemon lutemon = lutemons.get(entry.getKey());
                if (lutemon != null) {
                    lutemon.heal();
                }
            });
    }

    public int getNextId() {
        return nextId;
    }

    // For serialization purposes
    protected Object readResolve() {
        instance = this;
        return instance;
    }

    public enum Location {
        HOME,
        TRAINING,
        BATTLE
    }
}
