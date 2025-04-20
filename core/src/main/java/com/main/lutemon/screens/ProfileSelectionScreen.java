package com.main.lutemon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.main.lutemon.LutemonGame;
import com.main.lutemon.model.profile.Profile;
import com.main.lutemon.utils.Constants;
import com.main.lutemon.utils.ProfileManager;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Screen for selecting a profile to load.
 */
public class ProfileSelectionScreen implements Screen {
    private final LutemonGame game;
    private Stage stage;
    private final OrthographicCamera camera;
    private TextureRegion backgroundTexture;
    public ProfileSelectionScreen(LutemonGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Constants.getScreenWidth(), Constants.getScreenHeight());
        initialize();
    }

    private void initialize() {
        try {
            stage = new Stage(new FitViewport(Constants.getScreenWidth(), Constants.getScreenHeight(), camera));
            Gdx.input.setInputProcessor(stage);
            backgroundTexture = game.getAssetLoader().getBackground("menu");
            createUI();
        } catch (Exception e) {
            Gdx.app.error("ProfileSelectionScreen", "Error initializing: " + e.getMessage());
            throw e;
        }
    }

    private void createUI() {
        // Main container table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        float padding = Constants.getPadding();
        float buttonWidth = Constants.getButtonWidth();
        float buttonHeight = Constants.getScreenHeight() * Constants.BUTTON_HEIGHT_PERCENT;

        // Title
        Label titleLabel = new Label("Select Profile", game.getAssetLoader().getSkin(), "title");
        mainTable.add(titleLabel).pad(padding * 2).expandX().center().row();

        // Get profiles
        List<Profile> profiles = ProfileManager.getInstance().getAllProfiles();

        if (profiles.isEmpty()) {
            // No profiles found
            Label noProfilesLabel = new Label("No profiles found", game.getAssetLoader().getSkin());
            mainTable.add(noProfilesLabel).pad(padding * 2).expandX().center().row();
        } else {
            // Create a table for profiles
            Table profilesTable = new Table();
            profilesTable.top();

            // Headers
            Table headerRow = new Table();
            headerRow.add(new Label("Name", game.getAssetLoader().getSkin())).width(500).pad(padding).left();
            headerRow.add(new Label("", game.getAssetLoader().getSkin())).width(700).pad(padding).center();
            profilesTable.add(headerRow).expandX().fillX().row();

            // Add a row for each profile
            for (final Profile profile : profiles) {
                Table profileRow = new Table();

                profileRow.add(new Label(profile.getName(), game.getAssetLoader().getSkin())).width(500).pad(padding).left();

                // Create a table for buttons
                Table buttonTable = new Table();

                // Load button
                TextButton loadButton = new TextButton("Load", game.getAssetLoader().getSkin());
                loadButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        loadProfile(profile.getName());
                    }
                });
                loadButton.setColor(0f, 0f, 0.5f, 1f);

                // Delete button
                TextButton deleteButton = new TextButton("Delete", game.getAssetLoader().getSkin());
                deleteButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showDeleteConfirmationDialog(profile.getName());
                    }
                });
                deleteButton.setColor(1f, 0f, 0f, 1f);

                // Add buttons to the button table
                buttonTable.add(loadButton).width(260).pad(padding / 2);
                buttonTable.add(deleteButton).width(420).pad(padding / 2);

                profileRow.add(buttonTable).width(700).pad(padding).center();

                profilesTable.add(profileRow).expandX().fillX().row();
            }

            // Create a scroll pane for the profiles
            ScrollPane scrollPane = new ScrollPane(profilesTable, game.getAssetLoader().getSkin());
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false);

            mainTable.add(scrollPane).expand().fill().pad(padding).row();
        }

        // Back button
        TextButton backButton = new TextButton("Back", game.getAssetLoader().getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        mainTable.add(backButton).size(buttonWidth, buttonHeight).pad(padding).row();

        // Add table to stage
        stage.addActor(mainTable);
    }

    private void loadProfile(String name) {
        try {
            // Load profile
            ProfileManager.getInstance().loadProfile(name);

            // Navigate to home screen
            game.navigateToHome();
        } catch (Exception e) {
            Gdx.app.error("ProfileSelectionScreen", "Error loading profile: " + e.getMessage());

            // Show error dialog
            Dialog errorDialog = new Dialog("Error", game.getAssetLoader().getSkin());
            errorDialog.text("Error loading profile: " + e.getMessage());

            // Add OK button with listener
            TextButton okButton = new TextButton("OK", game.getAssetLoader().getSkin());
            okButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    errorDialog.hide();
                }
            });

            errorDialog.button(okButton);
            errorDialog.show(stage);
        }
    }

    /**
     * Shows a confirmation dialog for deleting a profile.
     *
     * @param name The name of the profile to delete
     */
    private void showDeleteConfirmationDialog(final String name) {
        Dialog confirmDialog = new Dialog("Confirm Delete", game.getAssetLoader().getSkin());
        confirmDialog.text("Are you sure you want to delete the profile '" + name + "'?\nThis action cannot be undone.");

        // Add buttons with listeners
        TextButton cancelButton = new TextButton("Cancel", game.getAssetLoader().getSkin());
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                confirmDialog.hide();
            }
        });
        cancelButton.setColor(0f, 0.5f, 0f, 1f);

        TextButton deleteButton = new TextButton("Delete", game.getAssetLoader().getSkin());
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                confirmDialog.hide();
                deleteProfile(name);
            }
        });
        deleteButton.setColor(1f, 0f, 0f, 1f);

        // Add buttons to dialog
        confirmDialog.button(cancelButton);
        confirmDialog.button(deleteButton);

        confirmDialog.show(stage);
    }

    /**
     * Deletes a profile.
     *
     * @param name The name of the profile to delete
     */
    private void deleteProfile(String name) {
        try {
            // Delete profile
            boolean success = ProfileManager.getInstance().deleteProfile(name);

            if (success) {
                Gdx.app.log("ProfileSelectionScreen", "Profile deleted: " + name);

                // Show success message
                Dialog successDialog = new Dialog("Success", game.getAssetLoader().getSkin());
                successDialog.text("Profile '" + name + "' has been deleted.");

                // Add OK button with listener
                TextButton okButton = new TextButton("OK", game.getAssetLoader().getSkin());
                okButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        successDialog.hide();
                    }
                });

                successDialog.button(okButton);
                successDialog.show(stage);

                // Refresh the screen to update the profile list
                initialize();
            } else {
                Gdx.app.error("ProfileSelectionScreen", "Failed to delete profile: " + name);

                // Show error dialog
                Dialog errorDialog = new Dialog("Error", game.getAssetLoader().getSkin());
                errorDialog.text("Failed to delete profile: " + name);

                // Add OK button with listener
                TextButton okButton = new TextButton("OK", game.getAssetLoader().getSkin());
                okButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        errorDialog.hide();
                    }
                });

                errorDialog.button(okButton);
                errorDialog.show(stage);
            }
        } catch (Exception e) {
            Gdx.app.error("ProfileSelectionScreen", "Error deleting profile: " + e.getMessage());

            // Show error dialog
            Dialog errorDialog = new Dialog("Error", game.getAssetLoader().getSkin());
            errorDialog.text("Error deleting profile: " + e.getMessage());

            // Add OK button with listener
            TextButton okButton = new TextButton("OK", game.getAssetLoader().getSkin());
            okButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    errorDialog.hide();
                }
            });

            errorDialog.button(okButton);
            errorDialog.show(stage);
        }
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        // Draw background
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        game.getBatch().draw(backgroundTexture, 0, 0, Constants.getScreenWidth(), Constants.getScreenHeight());
        game.getBatch().end();

        // Draw UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update camera and viewport
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);

        // Recreate UI to adjust to new screen size
        stage.clear();
        createUI();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
