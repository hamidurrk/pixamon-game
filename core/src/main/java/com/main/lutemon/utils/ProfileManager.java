package com.main.lutemon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.model.lutemon.LutemonType;
import com.main.lutemon.model.lutemon.WhiteLutemon;
import com.main.lutemon.model.lutemon.GreenLutemon;
import com.main.lutemon.model.lutemon.PinkLutemon;
import com.main.lutemon.model.lutemon.OrangeLutemon;
import com.main.lutemon.model.lutemon.BlackLutemon;
import com.main.lutemon.model.profile.Profile;
import com.main.lutemon.model.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages player profiles, including saving and loading profiles.
 */
public class ProfileManager {
    private static final String PROFILES_DIRECTORY = "profiles";
    private static volatile ProfileManager instance;
    private Profile currentProfile;
    private final Json json;

    private ProfileManager() {
        json = new Json();
        json.setIgnoreUnknownFields(true); // Ignore unknown fields during deserialization

        // Register the Profile class with the Json serializer
        json.setTypeName("class");
        json.addClassTag("profile", Profile.class);

        // Create profiles directory if it doesn't exist
        FileHandle profilesDir = Gdx.files.local(PROFILES_DIRECTORY);
        if (!profilesDir.exists()) {
            profilesDir.mkdirs();
            Gdx.app.log("ProfileManager", "Creating profiles directory: " + profilesDir.path());
        } else {
            Gdx.app.log("ProfileManager", "Profiles directory already exists: " + profilesDir.path());
        }

        // Print the absolute path to help locate the directory
        String absolutePath = profilesDir.file().getAbsolutePath();
        Gdx.app.log("ProfileManager", "ABSOLUTE PATH TO PROFILES: " + absolutePath);
        System.out.println("ABSOLUTE PATH TO PROFILES: " + absolutePath);
    }

    public static ProfileManager getInstance() {
        if (instance == null) {
            synchronized (ProfileManager.class) {
                if (instance == null) {
                    instance = new ProfileManager();
                }
            }
        }
        return instance;
    }

    /**
     * Creates a new profile with the given name.
     *
     * @param name The name of the profile
     * @return The created profile
     */
    public Profile createProfile(String name) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Profile name cannot be empty");
        }

        // Check if profile already exists
        if (profileExists(name)) {
            throw new IllegalArgumentException("Profile with name '" + name + "' already exists");
        }

        // Reset the game state before creating a new profile
        Gdx.app.log("ProfileManager", "Resetting game state before creating new profile");

        // Reset storage
        Storage.getInstance().clear();

        // Reset statistics manager
        StatisticsManager.getInstance().reset();

        // Create new profile
        Profile profile = new Profile(name);

        // Ensure creation date is set to current time
        profile.setCreationDate(new java.util.Date());
        profile.setLastPlayedDate(new java.util.Date());

        currentProfile = profile;

        // Set flags to indicate this is a newly created profile
        profileJustCreated = true;
        profileJustLoaded = false;

        // Save profile
        boolean saved = saveProfile(profile);
        Gdx.app.log("ProfileManager", "Created profile: " + name + ", saved: " + saved + ", creation time: " + profile.getCreationDate().getTime());

        return profile;
    }

    /**
     * Loads a profile with the given name.
     *
     * @param name The name of the profile
     * @return The loaded profile
     */
    public Profile loadProfile(String name) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Profile name cannot be empty");
        }

        // Check if profile exists
        if (!profileExists(name)) {
            throw new IllegalArgumentException("Profile with name '" + name + "' does not exist");
        }

        try {
            // Load profile
            FileHandle file = Gdx.files.local(PROFILES_DIRECTORY + "/" + name + ".json");
            Gdx.app.log("ProfileManager", "Loading profile from: " + file.path());
            String profileData = file.readString();
            Gdx.app.log("ProfileManager", "Profile data: " + profileData);

            // Create profile from JSON data
            Profile profile = createProfileFromJson(name, profileData);

            // Validate profile
            if (profile == null) {
                throw new IllegalStateException("Failed to deserialize profile");
            }

            Gdx.app.log("ProfileManager", "Profile loaded: " + profile.getName());

            // Set as current profile
            currentProfile = profile;

            // Set flags to indicate this is a loaded profile, not a new one
            profileJustCreated = false;
            profileJustLoaded = true;

            // Only update last played date, preserve creation date
            java.util.Date originalCreationDate = profile.getCreationDate();
            profile.updateLastPlayedDate();
            profile.setCreationDate(originalCreationDate); // Restore original creation date
            saveProfile(profile);

            // Completely reset the game state before loading the profile
            Gdx.app.log("ProfileManager", "Resetting game state before loading profile");

            // Reset storage
            Storage.getInstance().clear();

            // Reset statistics manager
            StatisticsManager statsManager = StatisticsManager.getInstance();
            statsManager.reset();

            // Dump the entire profile JSON for debugging
            Gdx.app.log("ProfileManager", "Profile JSON data: " + profileData);

            // Check if the profile has lutemons
            if (profile.getLutemons() != null) {
                Gdx.app.log("ProfileManager", "Profile has lutemons list: " + (profile.getLutemons() != null));
                Gdx.app.log("ProfileManager", "Profile lutemons list size: " + profile.getLutemons().size());
            } else {
                Gdx.app.log("ProfileManager", "Profile lutemons list is null");
            }

            // Load Lutemons into Storage
            if (profile.getLutemons() != null && !profile.getLutemons().isEmpty()) {
                Gdx.app.log("ProfileManager", "Loading " + profile.getLutemons().size() + " Lutemons from profile");
                for (Lutemon lutemon : profile.getLutemons()) {
                    if (lutemon != null) {
                        Gdx.app.log("ProfileManager", "Loading Lutemon: " + lutemon.getName() + ", Type: " + lutemon.getType());
                        Storage.getInstance().addLutemonWithoutStats(lutemon);
                    } else {
                        Gdx.app.log("ProfileManager", "Found null Lutemon in profile");
                    }
                }

                // Verify lutemons were loaded correctly
                List<Lutemon> loadedLutemons = Storage.getInstance().getAllLutemons();
                Gdx.app.log("ProfileManager", "Verified " + loadedLutemons.size() + " Lutemons in storage after loading");
                for (Lutemon lutemon : loadedLutemons) {
                    Gdx.app.log("ProfileManager", "Verified Lutemon: " + lutemon.getName() + ", Type: " + lutemon.getType() + ", Location: " + Storage.getInstance().getLutemonLocation(lutemon.getId()));
                }
            } else {
                Gdx.app.log("ProfileManager", "No Lutemons to load from profile");

                // Try to create default lutemons for this profile if it has none
                if (profile.getTotalLutemonsCreated() > 0) {
                    Gdx.app.log("ProfileManager", "Profile has totalLutemonsCreated > 0 but no lutemons list. Creating default lutemons.");

                    // Create default lutemons
                    Storage storage = Storage.getInstance();
                    storage.addLutemonWithoutStats(new WhiteLutemon(storage.getNextId(), "White Warrior"));
                    storage.addLutemonWithoutStats(new GreenLutemon(storage.getNextId(), "Green Fighter"));
                    storage.addLutemonWithoutStats(new PinkLutemon(storage.getNextId(), "Pink Striker"));
                    storage.addLutemonWithoutStats(new OrangeLutemon(storage.getNextId(), "Orange Blade"));
                    storage.addLutemonWithoutStats(new BlackLutemon(storage.getNextId(), "Black Shadow"));

                    // Save the profile with the new lutemons
                    saveCurrentProfile();
                }
            }

            // Load statistics
            Gdx.app.log("ProfileManager", "Setting statistics: " +
                "Lutemons created: " + profile.getTotalLutemonsCreated() +
                ", Battles: " + profile.getTotalBattles() +
                ", Training sessions: " + profile.getTotalTrainingSessions());

            // Make sure we set the statistics in the StatisticsManager
            statsManager.setTotalLutemonsCreated(profile.getTotalLutemonsCreated());
            statsManager.setTotalBattles(profile.getTotalBattles());
            statsManager.setTotalTrainingSessions(profile.getTotalTrainingSessions());

            // Log the statistics after setting them
            Gdx.app.log("ProfileManager", "Verified statistics after loading: " +
                "Lutemons created: " + statsManager.getTotalLutemonsCreated() +
                ", Battles: " + statsManager.getTotalBattles() +
                ", Training sessions: " + statsManager.getTotalTrainingSessions());

            return profile;
        } catch (Exception e) {
            Gdx.app.error("ProfileManager", "Error loading profile: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("Error loading profile: " + e.getMessage(), e);
        }
    }

    /**
     * Saves the current profile.
     *
     * @return True if the profile was saved successfully, false otherwise
     */
    public boolean saveCurrentProfile() {
        if (currentProfile == null) {
            Gdx.app.error("ProfileManager", "No current profile to save");
            return false;
        }

        // Update profile data
        currentProfile.updateLastPlayedDate();

        // Get all lutemons from storage
        List<Lutemon> allLutemons = Storage.getInstance().getAllLutemons();
        Gdx.app.log("ProfileManager", "Saving " + allLutemons.size() + " Lutemons to profile");

        // Create a deep copy of the lutemons to avoid reference issues
        List<Lutemon> lutemonsCopy = new ArrayList<>();

        // Log each lutemon being saved
        for (Lutemon lutemon : allLutemons) {
            Gdx.app.log("ProfileManager", "Saving Lutemon: " + lutemon.getName() + ", Type: " + lutemon.getType());

            // Create a new lutemon of the same type
            Lutemon copy = null;
            switch (lutemon.getType()) {
                case WHITE:
                    copy = new WhiteLutemon(lutemon.getId(), lutemon.getName());
                    break;
                case GREEN:
                    copy = new GreenLutemon(lutemon.getId(), lutemon.getName());
                    break;
                case PINK:
                    copy = new PinkLutemon(lutemon.getId(), lutemon.getName());
                    break;
                case ORANGE:
                    copy = new OrangeLutemon(lutemon.getId(), lutemon.getName());
                    break;
                case BLACK:
                    copy = new BlackLutemon(lutemon.getId(), lutemon.getName());
                    break;
            }

            if (copy != null) {
                // Copy the stats from the original lutemon
                com.main.lutemon.model.lutemon.stats.LutemonStats originalStats = lutemon.getStats();

                // Log the original stats
                Gdx.app.log("ProfileManager", "Original Lutemon stats for " + lutemon.getName() +
                          ": Experience=" + originalStats.getExperience() +
                          ", Battles=" + originalStats.getBattles() +
                          ", Wins=" + originalStats.getWins() +
                          ", TrainingDays=" + originalStats.getTrainingDays());

                // Add experience points
                int experience = originalStats.getExperience();
                if (experience > 0) {
                    copy.addExperience(experience);
                }

                // Set battle stats
                int battles = originalStats.getBattles();
                int wins = originalStats.getWins();
                int trainingDays = originalStats.getTrainingDays();

                // Log the stats we're about to copy
                Gdx.app.log("ProfileManager", "Copying stats to Lutemon " + copy.getName() +
                          ": Battles=" + battles +
                          ", Wins=" + wins +
                          ", TrainingDays=" + trainingDays);

                for (int i = 0; i < battles; i++) {
                    copy.getStats().incrementBattles();
                }

                for (int i = 0; i < wins; i++) {
                    copy.getStats().incrementWins();
                }

                for (int i = 0; i < trainingDays; i++) {
                    copy.getStats().incrementTrainingDays();
                }

                // Set current health
                copy.getStats().setCurrentHealth(originalStats.getCurrentHealth());

                // Verify the stats were copied correctly
                Gdx.app.log("ProfileManager", "Verified copied stats for Lutemon " + copy.getName() +
                          ": Battles=" + copy.getStats().getBattles() +
                          ", Wins=" + copy.getStats().getWins() +
                          ", TrainingDays=" + copy.getStats().getTrainingDays());

                lutemonsCopy.add(copy);
                Gdx.app.log("ProfileManager", "Created copy of Lutemon: " + copy.getName() +
                          ", Type: " + copy.getType() +
                          ", Experience: " + copy.getExperience() +
                          ", Attack: " + copy.getAttack() +
                          ", Defense: " + copy.getStats().getDefense() +
                          ", Health: " + copy.getStats().getCurrentHealth() + "/" + copy.getStats().getMaxHealth());
            }
        }

        // Set lutemons in profile
        currentProfile.setLutemons(lutemonsCopy);
        Gdx.app.log("ProfileManager", "Set " + lutemonsCopy.size() + " Lutemons in profile");

        // Update statistics
        StatisticsManager statsManager = StatisticsManager.getInstance();
        currentProfile.setTotalLutemonsCreated(statsManager.getTotalLutemonsCreated());
        currentProfile.setTotalBattles(statsManager.getTotalBattles());
        currentProfile.setTotalTrainingSessions(statsManager.getTotalTrainingSessions());

        // Save profile
        return saveProfile(currentProfile);
    }

    /**
     * Saves a profile.
     *
     * @param profile The profile to save
     * @return True if the profile was saved successfully, false otherwise
     */
    private boolean saveProfile(Profile profile) {
        try {
            // Ensure profiles directory exists
            FileHandle profilesDir = Gdx.files.local(PROFILES_DIRECTORY);
            if (!profilesDir.exists()) {
                profilesDir.mkdirs();
                Gdx.app.log("ProfileManager", "Creating profiles directory: " + profilesDir.path());
            }

            // Log the lutemons being saved
            if (profile.getLutemons() != null) {
                Gdx.app.log("ProfileManager", "Saving profile with " + profile.getLutemons().size() + " lutemons");
                for (Lutemon lutemon : profile.getLutemons()) {
                    if (lutemon != null) {
                        Gdx.app.log("ProfileManager", "Saving Lutemon in profile: " + lutemon.getName() +
                            " (ID: " + lutemon.getId() + ", Type: " + lutemon.getType() + ")");
                    }
                }
            } else {
                Gdx.app.log("ProfileManager", "Saving profile with no lutemons (null list)");
            }

            String profileData = json.toJson(profile);
            Gdx.app.log("ProfileManager", "Profile JSON data: " + profileData);

            FileHandle file = Gdx.files.local(PROFILES_DIRECTORY + "/" + profile.getName() + ".json");
            Gdx.app.log("ProfileManager", "Saving profile to: " + file.path());
            file.writeString(profileData, false);
            Gdx.app.log("ProfileManager", "Profile saved successfully: " + profile.getName());
            return true;
        } catch (Exception e) {
            Gdx.app.error("ProfileManager", "Error saving profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a profile with the given name exists.
     *
     * @param name The name of the profile
     * @return True if the profile exists, false otherwise
     */
    public boolean profileExists(String name) {
        FileHandle file = Gdx.files.local(PROFILES_DIRECTORY + "/" + name + ".json");
        boolean exists = file.exists();
        Gdx.app.log("ProfileManager", "Checking if profile exists: " + file.path() + ", exists: " + exists);
        return exists;
    }

    /**
     * Gets a list of all available profiles.
     *
     * @return A list of all available profiles
     */
    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();

        FileHandle profilesDir = Gdx.files.local(PROFILES_DIRECTORY);
        Gdx.app.log("ProfileManager", "Looking for profiles in directory: " + profilesDir.path());

        if (profilesDir.exists()) {
            Gdx.app.log("ProfileManager", "Profiles directory exists");
            if (profilesDir.isDirectory()) {
                Gdx.app.log("ProfileManager", "Profiles directory is a directory");
                FileHandle[] files = profilesDir.list(".json");
                Gdx.app.log("ProfileManager", "Found " + files.length + " profile files");

                for (FileHandle file : files) {
                    Gdx.app.log("ProfileManager", "Processing profile file: " + file.name());
                    try {
                        String profileData = file.readString();
                        Gdx.app.log("ProfileManager", "Profile data: " + profileData);

                        // Create a new profile manually from the JSON data
                        String fileName = file.nameWithoutExtension();
                        Profile profile = createProfileFromJson(fileName, profileData);

                        if (profile != null) {
                            profiles.add(profile);
                            Gdx.app.log("ProfileManager", "Successfully loaded profile: " + profile.getName());
                        } else {
                            Gdx.app.error("ProfileManager", "Failed to create profile from file: " + file.name());
                        }
                    } catch (Exception e) {
                        Gdx.app.error("ProfileManager", "Error loading profile from file: " + file.name() + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                Gdx.app.error("ProfileManager", "Profiles directory is not a directory");
            }
        } else {
            Gdx.app.error("ProfileManager", "Profiles directory does not exist");
        }

        return profiles;
    }

    /**
     * Deletes a profile with the given name.
     *
     * @param name The name of the profile
     * @return True if the profile was deleted successfully, false otherwise
     */
    public boolean deleteProfile(String name) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Profile name cannot be empty");
        }

        // Check if profile exists
        if (!profileExists(name)) {
            throw new IllegalArgumentException("Profile with name '" + name + "' does not exist");
        }

        // Delete profile
        FileHandle file = Gdx.files.local(PROFILES_DIRECTORY + "/" + name + ".json");
        Gdx.app.log("ProfileManager", "Deleting profile: " + file.path());
        boolean success = file.delete();
        Gdx.app.log("ProfileManager", "Profile deleted: " + success);

        // If the deleted profile was the current profile, set current profile to null
        if (success && currentProfile != null && currentProfile.getName().equals(name)) {
            currentProfile = null;
        }

        return success;
    }

    /**
     * Gets the current profile.
     *
     * @return The current profile
     */
    public Profile getCurrentProfile() {
        return currentProfile;
    }

    // Flag to track if a profile was just created or loaded
    private boolean profileJustCreated = false;
    private boolean profileJustLoaded = false;

    /**
     * Checks if the current profile is new (just created).
     *
     * @return True if the profile was just created, false otherwise
     */
    public boolean isNewProfile() {
        if (currentProfile == null) {
            return false;
        }

        // If we know this profile was just loaded (not created), return false
        if (profileJustLoaded) {
            Gdx.app.log("ProfileManager", "Profile " + currentProfile.getName() +
                " was just loaded, not new");
            return false;
        }

        // If we know this profile was just created, return true
        if (profileJustCreated) {
            Gdx.app.log("ProfileManager", "Profile " + currentProfile.getName() +
                " was just created, is new");
            return true;
        }

        // As a fallback, check if the profile was created recently (within the last minute)
        long creationTime = currentProfile.getCreationDate().getTime();
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - creationTime;

        // If the profile was created less than a minute ago, consider it new
        boolean isNew = timeDiff < 60000; // 60 seconds in milliseconds

        Gdx.app.log("ProfileManager", "Profile " + currentProfile.getName() +
            " creation time: " + creationTime +
            ", current time: " + currentTime +
            ", diff: " + timeDiff +
            ", isNew: " + isNew);

        return isNew;
    }

    /**
     * Creates a profile from JSON data.
     *
     * @param fileName The name of the file (used as fallback for profile name)
     * @param jsonData The JSON data
     * @return The created profile, or null if creation failed
     */
    private Profile createProfileFromJson(String fileName, String jsonData) {
        try {
            // Create a new profile with the file name
            Profile profile = new Profile(fileName);

            // Parse the JSON data
            com.badlogic.gdx.utils.JsonValue jsonValue = new com.badlogic.gdx.utils.JsonReader().parse(jsonData);

            // Extract profile data
            if (jsonValue.has("name")) {
                profile.setName(jsonValue.getString("name"));
            }

            // Extract dates if available
            if (jsonValue.has("creationDate")) {
                try {
                    long timestamp = Long.parseLong(jsonValue.getString("creationDate"));
                    profile.setCreationDate(new java.util.Date(timestamp));
                } catch (Exception e) {
                    Gdx.app.error("ProfileManager", "Error parsing creation date: " + e.getMessage());
                }
            }

            if (jsonValue.has("lastPlayedDate")) {
                try {
                    long timestamp = Long.parseLong(jsonValue.getString("lastPlayedDate"));
                    profile.setLastPlayedDate(new java.util.Date(timestamp));
                } catch (Exception e) {
                    Gdx.app.error("ProfileManager", "Error parsing last played date: " + e.getMessage());
                }
            }

            // Extract statistics
            if (jsonValue.has("totalLutemonsCreated")) {
                profile.setTotalLutemonsCreated(jsonValue.getInt("totalLutemonsCreated"));
            }

            if (jsonValue.has("totalBattles")) {
                profile.setTotalBattles(jsonValue.getInt("totalBattles"));
            }

            if (jsonValue.has("totalTrainingSessions")) {
                profile.setTotalTrainingSessions(jsonValue.getInt("totalTrainingSessions"));
            }

            // Extract Lutemons if available
            if (jsonValue.has("lutemons")) {
                Gdx.app.log("ProfileManager", "Found lutemons field in JSON");
                com.badlogic.gdx.utils.JsonValue lutemonsArray = jsonValue.get("lutemons");
                if (lutemonsArray != null) {
                    Gdx.app.log("ProfileManager", "Lutemons array is not null");
                    if (lutemonsArray.isArray()) {
                        Gdx.app.log("ProfileManager", "Lutemons field is an array with " + lutemonsArray.size + " elements");

                        for (com.badlogic.gdx.utils.JsonValue lutemonJson : lutemonsArray) {
                            try {
                                // Create a new Lutemon from the JSON data
                                // Log the lutemon JSON data
                                Gdx.app.log("ProfileManager", "Parsing Lutemon JSON: " + lutemonJson.toString());

                                String type = lutemonJson.getString("type", "White");
                                String name = lutemonJson.getString("name", "Unknown");
                                int id = lutemonJson.getInt("id", -1);

                                // Extract stats if available
                                int experience = 0;
                                int attack = 0;
                                int defense = 0;
                                int maxHealth = 0;
                                int currentHealth = 0;
                                int level = 1;
                                int trainingDays = 0;
                                int battles = 0;
                                int wins = 0;
                                int losses = 0;

                                Gdx.app.log("ProfileManager", "Extracting stats for Lutemon: " + name);

                                // Check if stats are available in the JSON
                                if (lutemonJson.has("stats")) {
                                    com.badlogic.gdx.utils.JsonValue statsJson = lutemonJson.get("stats");
                                    if (statsJson != null) {
                                        Gdx.app.log("ProfileManager", "Found stats for Lutemon: " + name);

                                        if (statsJson.has("experience")) {
                                            experience = statsJson.getInt("experience", 0);
                                        }
                                        if (statsJson.has("attack")) {
                                            attack = statsJson.getInt("attack", 0);
                                        }
                                        if (statsJson.has("defense")) {
                                            defense = statsJson.getInt("defense", 0);
                                        }
                                        if (statsJson.has("maxHealth")) {
                                            maxHealth = statsJson.getInt("maxHealth", 0);
                                        }
                                        if (statsJson.has("currentHealth")) {
                                            currentHealth = statsJson.getInt("currentHealth", 0);
                                        }
                                        if (statsJson.has("level")) {
                                            level = statsJson.getInt("level", 1);
                                        }
                                        if (statsJson.has("trainingDays")) {
                                            trainingDays = statsJson.getInt("trainingDays", 0);
                                            Gdx.app.log("ProfileManager", "Found trainingDays for Lutemon " + name + ": " + trainingDays);
                                        } else {
                                            Gdx.app.log("ProfileManager", "No trainingDays found for Lutemon " + name);
                                        }
                                        if (statsJson.has("battles")) {
                                            battles = statsJson.getInt("battles", 0);
                                        }
                                        if (statsJson.has("wins")) {
                                            wins = statsJson.getInt("wins", 0);
                                        }
                                        if (statsJson.has("losses")) {
                                            losses = statsJson.getInt("losses", 0);
                                        }
                                    }
                                }

                                Gdx.app.log("ProfileManager", "Parsed Lutemon data - Type: " + type + ", Name: " + name + ", ID: " + id +
                                          ", Experience: " + experience);

                                // Get a temporary ID for the Lutemon
                                // The actual ID will be assigned when added to storage
                                int tempId = id >= 0 ? id : 0;

                                Lutemon lutemon = null;
                                switch (type) {
                                    case "WHITE":
                                    case "White":
                                        lutemon = new WhiteLutemon(tempId, name);
                                        break;
                                    case "GREEN":
                                    case "Green":
                                        lutemon = new GreenLutemon(tempId, name);
                                        break;
                                    case "PINK":
                                    case "Pink":
                                        lutemon = new PinkLutemon(tempId, name);
                                        break;
                                    case "ORANGE":
                                    case "Orange":
                                        lutemon = new OrangeLutemon(tempId, name);
                                        break;
                                    case "BLACK":
                                    case "Black":
                                        lutemon = new BlackLutemon(tempId, name);
                                        break;
                                    default:
                                        Gdx.app.error("ProfileManager", "Unknown Lutemon type: " + type + ", defaulting to White");
                                        lutemon = new WhiteLutemon(tempId, name);
                                        break;
                                }

                                if (lutemon != null) {
                                    lutemon.setId(id);

                                    // Apply the stats to the lutemon
                                    if (experience > 0) {
                                        // Add experience points
                                        lutemon.addExperience(experience);
                                        Gdx.app.log("ProfileManager", "Applied experience to Lutemon: " + experience);
                                    }

                                    // Set other stats if they were loaded
                                    com.main.lutemon.model.lutemon.stats.LutemonStats stats = lutemon.getStats();

                                    // Always log the stats we're about to apply
                                    Gdx.app.log("ProfileManager", "Applying stats to Lutemon " + lutemon.getName() +
                                              ": Battles=" + battles + ", Wins=" + wins + ", TrainingDays=" + trainingDays);

                                    // Set battle stats
                                    if (battles > 0) {
                                        for (int i = 0; i < battles; i++) {
                                            stats.incrementBattles();
                                        }
                                        Gdx.app.log("ProfileManager", "Applied " + battles + " battles to Lutemon " + lutemon.getName());
                                    }

                                    // Set win stats
                                    if (wins > 0) {
                                        for (int i = 0; i < wins; i++) {
                                            stats.incrementWins();
                                        }
                                        Gdx.app.log("ProfileManager", "Applied " + wins + " wins to Lutemon " + lutemon.getName());
                                    }

                                    // Set training days
                                    if (trainingDays > 0) {
                                        for (int i = 0; i < trainingDays; i++) {
                                            stats.incrementTrainingDays();
                                        }
                                        Gdx.app.log("ProfileManager", "Applied " + trainingDays + " training days to Lutemon " + lutemon.getName());
                                    }

                                    // Verify the stats were applied correctly
                                    Gdx.app.log("ProfileManager", "Verified stats for Lutemon " + lutemon.getName() +
                                              ": Battles=" + stats.getBattles() +
                                              ", Wins=" + stats.getWins() +
                                              ", TrainingDays=" + stats.getTrainingDays());

                                    // Set health if it was loaded
                                    if (currentHealth > 0) {
                                        lutemon.getStats().setCurrentHealth(currentHealth);
                                        Gdx.app.log("ProfileManager", "Set current health for Lutemon: " + currentHealth);
                                    }

                                    profile.getLutemons().add(lutemon);
                                    Gdx.app.log("ProfileManager", "Added Lutemon to profile: " + lutemon.getName() +
                                              " (ID: " + lutemon.getId() + ", Type: " + lutemon.getType() +
                                              ", Experience: " + lutemon.getExperience() +
                                              ", Attack: " + lutemon.getAttack() +
                                              ", Defense: " + lutemon.getStats().getDefense() +
                                              ", Health: " + lutemon.getStats().getCurrentHealth() + "/" + lutemon.getStats().getMaxHealth() + ")");
                                }
                            } catch (Exception e) {
                                Gdx.app.error("ProfileManager", "Error parsing Lutemon: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }

                return profile;
            }
        } catch (Exception e) {
            Gdx.app.error("ProfileManager", "Error creating profile from JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return null;
    }
}

