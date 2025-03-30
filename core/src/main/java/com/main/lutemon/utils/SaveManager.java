package com.main.lutemon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.storage.Storage;

import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static final String SAVE_FILE = "lutemon_save.json";
    private final Json json;

    public SaveManager() {
        json = new Json();
    }

    public boolean saveGame() {
        try {
            List<Lutemon> lutemons = Storage.getInstance().getAllLutemons();
            String saveData = json.toJson(lutemons);
            FileHandle file = Gdx.files.local(SAVE_FILE);
            file.writeString(saveData, false);
            Gdx.app.log("SaveManager", "Game saved successfully");
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error saving game: " + e.getMessage());
        }
        return false;
    }

    public boolean loadGame() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (!file.exists()) {
                Gdx.app.log("SaveManager", "No save file found");
                return false;
            }

            String saveData = file.readString();
            List<Lutemon> lutemons = json.fromJson(ArrayList.class, Lutemon.class, saveData);
            Storage.getInstance().clear();
            for (Lutemon lutemon : lutemons) {
                Storage.getInstance().addLutemon(lutemon);
            }
            Gdx.app.log("SaveManager", "Game loaded successfully");
            return true;
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error loading game: " + e.getMessage());
            return false;
        }
    }

    public boolean hasSaveFile() {
        return Gdx.files.local(SAVE_FILE).exists();
    }
}
