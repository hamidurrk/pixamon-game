package com.main.lutemon.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.main.lutemon.LutemonGame;
import com.main.lutemon.model.profile.Profile;
import com.main.lutemon.screens.MainMenuScreen;
import com.main.lutemon.utils.ProfileManager;

/**
 * Dialog for creating a new profile.
 */
public class CreateProfileDialog extends Window {
    private final MainMenuScreen screen;
    private final TextField nameField;

    /**
     * Creates a new profile creation dialog.
     *
     * @param screen The main menu screen
     * @param skin The skin to use for UI elements
     */
    public CreateProfileDialog(MainMenuScreen screen, Skin skin) {
        super("", skin);
        this.screen = screen;

        // Set up dialog properties
        setModal(true);
        setMovable(false);
        setResizable(false);

        float width = Gdx.graphics.getWidth() * 0.6f;
        float height = Gdx.graphics.getHeight() * 0.4f;
        setSize(width, height);
        setPosition((Gdx.graphics.getWidth() - width) / 2, (Gdx.graphics.getHeight() - height) / 2);

        // Create content
        Table contentTable = new Table();
        contentTable.pad(30);

        // Name field
        Label nameLabel = new Label("Profile Name:", skin);
        nameLabel.setFontScale(1.5f);

        nameField = new TextField("", skin);
        nameField.setMessageText("Enter profile name");
        nameField.setAlignment(Align.left);
        nameField.setMaxLength(20);

        TextField.TextFieldStyle style = new TextField.TextFieldStyle(nameField.getStyle());
        style.font.getData().setScale(1.5f);
        nameField.setStyle(style);

        // Set a minimum height for the text field
        nameField.getStyle().background.setMinHeight(60);

        // Error label (initially hidden)
        final Label errorLabel = new Label("", skin);
        errorLabel.setColor(1, 0, 0, 1);
        errorLabel.setFontScale(0.8f);
        errorLabel.setVisible(false);

        // Buttons with increased size
        TextButton createButton = new TextButton("Create", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        // Increase button text size
        createButton.getLabel().setFontScale(1f);
        cancelButton.getLabel().setFontScale(1f);

        // Button listeners
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = nameField.getText().trim();

                // Validate name
                if (name.isEmpty()) {
                    errorLabel.setText("Profile name cannot be empty");
                    errorLabel.setVisible(true);
                    return;
                }

                // Check if profile already exists
                if (ProfileManager.getInstance().profileExists(name)) {
                    errorLabel.setText("Profile with this name already exists");
                    errorLabel.setVisible(true);
                    return;
                }

                try {
                    // Create profile
                    Profile profile = ProfileManager.getInstance().createProfile(name);

                    // Navigate to home screen
                    screen.getGame().navigateToHome();

                    // Close dialog
                    remove();
                } catch (Exception e) {
                    errorLabel.setText("Error creating profile: " + e.getMessage());
                    errorLabel.setVisible(true);
                    Gdx.app.error("CreateProfileDialog", "Error creating profile", e);
                }
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove(); // Close dialog
            }
        });

        // Add to content table
        contentTable.add(nameLabel).padRight(20).left();
        contentTable.add(nameField).expandX().fillX().height(70).row();
        contentTable.add(errorLabel).colspan(2).padTop(15).row();

        // Add buttons with increased size
        Table buttonTable = new Table();
        buttonTable.add(createButton).width(350).height(80).padRight(40);
        buttonTable.add(cancelButton).width(350).height(80);

        contentTable.add(buttonTable).colspan(2).padTop(30).right();

        add(contentTable).expand().fill();
    }

    /**
     * Shows the dialog on the given stage.
     *
     * @param stage The stage to show the dialog on
     */
    public void show(Stage stage) {
        stage.addActor(this);
        stage.setKeyboardFocus(nameField); // Focus on name field
    }
}
