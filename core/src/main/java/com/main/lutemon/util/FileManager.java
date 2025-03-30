package com.main.lutemon.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.main.lutemon.model.storage.Storage;

import java.io.*;

public class FileManager {
    private static final String SAVE_FILE = "lutemon_save.dat";

    public static void saveGame() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            ObjectOutputStream out = new ObjectOutputStream(file.write(false));
            out.writeObject(Storage.getInstance());
            out.close();
        } catch (IOException e) {
            Gdx.app.error("FileManager", "Error saving game", e);
        }
    }

    public static boolean loadGame() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (file.exists()) {
                ObjectInputStream in = new ObjectInputStream(file.read());
                Storage.instance = (Storage) in.readObject();
                in.close();
                return true;
            }
        } catch (Exception e) {
            Gdx.app.error("FileManager", "Error loading game", e);
        }
        return false;
    }
}
