package com.main.lutemon.model.storage;

import com.main.lutemon.model.lutemon.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Storage implements Serializable {
    public static Storage instance;
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
            instance = new Storage();
        }
        return instance;
    }

    public void addLutemon(Lutemon lutemon) {
        lutemon.setId(nextId++);
        lutemons.put(lutemon.getId(), lutemon);
        lutemonLocations.put(lutemon.getId(), Location.HOME);
    }

    public void moveToLocation(int lutemonId, Location location) {
        lutemonLocations.put(lutemonId, location);
    }

    public List<Lutemon> getLutemonsAtLocation(Location location) {
        List<Lutemon> result = new ArrayList<>();
        for (Map.Entry<Integer, Location> entry : lutemonLocations.entrySet()) {
            if (entry.getValue() == location) {
                Lutemon lutemon = lutemons.get(entry.getKey());
                if (lutemon != null) {
                    result.add(lutemon);
                }
            }
        }
        return result;
    }

    public List<Lutemon> getAllLutemons() {
        return new ArrayList<>(lutemons.values());
    }

    public void clear() {
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

    public void removeLutemon(int id) {
        lutemons.remove(id);
        lutemonLocations.remove(id);
    }

    public void healAllAtHome() {
        for (Map.Entry<Integer, Location> entry : lutemonLocations.entrySet()) {
            if (entry.getValue() == Location.HOME) {
                Lutemon lutemon = lutemons.get(entry.getKey());
                if (lutemon != null) {
                    lutemon.heal();
                }
            }
        }
    }

    public enum Location {
        HOME,
        TRAINING,
        BATTLE
    }
}
