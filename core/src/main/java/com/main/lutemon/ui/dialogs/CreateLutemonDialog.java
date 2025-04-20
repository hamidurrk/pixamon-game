package com.main.lutemon.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.main.lutemon.model.lutemon.*;
import com.main.lutemon.model.storage.Storage;
import com.main.lutemon.screens.HomeScreen;
import com.main.lutemon.ui.components.AnimatedAvatar;
import com.main.lutemon.utils.Constants;

/**
 * Dialog for creating a new Lutemon.
 * Shows all available Lutemon types with their stats and animations.
 */
public class CreateLutemonDialog extends Window {
    private final HomeScreen homeScreen;
    private final Skin skin;
    private LutemonType selectedType = LutemonType.WHITE; // Default selection
    private TextField nameField;
    private Label errorLabel;
    private Table previewTable;
    private AnimatedAvatar previewAvatar;
    private Label statsLabel;

    /**
     * Creates a new dialog for creating Lutemons.
     *
     * @param homeScreen The home screen
     * @param skin The skin to use for UI elements
     */
    public CreateLutemonDialog(HomeScreen homeScreen, Skin skin) {
        super("", skin);
        this.homeScreen = homeScreen;
        this.skin = skin;

        // Set up dialog properties
        setModal(true);
        setMovable(false);
        setResizable(false);

        float width = Constants.getScreenWidth() * 0.75f;
        float height = Constants.getScreenHeight() * 0.75f;
        setSize(width, height);
        setPosition((Constants.getScreenWidth() - width) / 2, (Constants.getScreenHeight() - height) / 2);

        createContent();
    }

    /**
     * Creates the dialog content.
     */
    private void createContent() {
        Table contentTable = new Table();
        contentTable.pad(20);

        Table leftSide = new Table();
        Table rightSide = new Table();

        // Left side - Lutemon preview and type selection
        leftSide.top();

        // Title for left side
        Label previewTitle = new Label("Select Lutemon Type", skin, "default");
        previewTitle.setAlignment(Align.center);
        leftSide.add(previewTitle).pad(10).row();

        // Create a horizontal layout with type buttons on left and preview on right
        Table horizontalLayout = new Table();

        // Type selection buttons - vertical layout
        Table typeButtonsTable = new Table();
        typeButtonsTable.top().left();

        ButtonGroup<TextButton> typeGroup = new ButtonGroup<>();
        typeGroup.setMinCheckCount(1);
        typeGroup.setMaxCheckCount(1);

        // Color map for Lutemon types
        java.util.Map<LutemonType, Color> typeColors = new java.util.HashMap<>();
        typeColors.put(LutemonType.WHITE, new Color(0.9f, 0.9f, 0.9f, 1));
        typeColors.put(LutemonType.GREEN, new Color(0.2f, 0.8f, 0.2f, 1));
        typeColors.put(LutemonType.PINK, new Color(0.9f, 0.5f, 0.8f, 1));
        typeColors.put(LutemonType.ORANGE, new Color(1.0f, 0.6f, 0.2f, 1));
        typeColors.put(LutemonType.BLACK, new Color(0.3f, 0.3f, 0.3f, 1));

        // Create a button for each Lutemon type
        for (LutemonType type : LutemonType.values()) {
            TextButton typeButton = new TextButton(type.toString(), skin, "toggle");
            typeButton.setChecked(type == selectedType);

            // Set button color based on Lutemon type
            Color typeColor = typeColors.get(type);
            if (typeColor != null) {
                typeButton.setColor(typeColor);
            }

            typeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedType = type;
                    updatePreview();
                }
            });
            typeGroup.add(typeButton);
            typeButtonsTable.add(typeButton).pad(10).width(120).height(60).row();
        }

        // Preview area
        previewTable = new Table();
        previewTable.setBackground(skin.getDrawable("default-pane"));
        previewTable.pad(20);

        // Create preview avatar with larger size
        previewAvatar = new AnimatedAvatar(selectedType.toString(), 450); // 3x larger (150 * 3)
        previewTable.add(previewAvatar).size(450).pad(20).row();

        // Stats label with smaller font
        statsLabel = new Label("", skin);
        statsLabel.setAlignment(Align.left);
        // Add the stats label with left alignment and padding
        previewTable.add(statsLabel).pad(0).width(200).padLeft(30).align(Align.left).row();

        // Add type buttons and preview to horizontal layout
        horizontalLayout.add(typeButtonsTable).padRight(40);
        horizontalLayout.add(previewTable).expandX().fill();

        leftSide.add(horizontalLayout).expand().fill().pad(10);

        // Right side - Name input and buttons
        rightSide.top();

        // Title for right side
        Label inputTitle = new Label("Enter Lutemon Name", skin, "default");
        inputTitle.setAlignment(Align.center);
        rightSide.add(inputTitle).pad(10).row();

        // Error label (initially hidden) - make it more prominent
        errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);
        errorLabel.setAlignment(Align.center);
        errorLabel.setFontScale(0.6f); // Larger font for error
        errorLabel.setVisible(false);
        rightSide.add(errorLabel).pad(5).row();

        // Name input field - larger size with bigger font
        nameField = new TextField("", skin);
        nameField.setMessageText("Enter name here");
        // Increase the font size of the input text
        nameField.getStyle().font.getData().setScale(1.5f); // Increase font size
        rightSide.add(nameField).width(500).height(100).pad(20).row();

        // Buttons
        Table buttonTable = new Table();

        // Larger buttons
        TextButton createButton = new TextButton("Create", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        // Increase font size for buttons
        createButton.getLabel().setFontScale(0.5f);
        cancelButton.getLabel().setFontScale(0.5f);

        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createLutemon();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove(); // Close dialog
            }
        });

        buttonTable.add(createButton).width(200).height(70).pad(30);
        buttonTable.add(cancelButton).width(200).height(70).pad(30);

        rightSide.add(buttonTable).pad(20).row();

        // Add sides to content table
        contentTable.add(leftSide).width(getWidth() * 0.5f).expand().fill();
        contentTable.add(rightSide).width(getWidth() * 0.5f).expand().fill();

        add(contentTable).expand().fill();

        updatePreview();
    }

    /**
     * Updates the Lutemon preview based on the selected type.
     */
    private void updatePreview() {
        previewAvatar.setLutemonType(selectedType.toString().toLowerCase());

        StringBuilder statsText = new StringBuilder();
        statsText.append("Type: ").append(selectedType).append("\\n\\n");
        statsText.append("Attack: ").append(selectedType.getAttack()).append("\\n");
        statsText.append("Defense: ").append(selectedType.getDefense()).append("\\n");
        statsText.append("Max Health: ").append(selectedType.getMaxHealth());

        statsLabel.setText(statsText.toString().replace("\\n", "\n"));
        statsLabel.setFontScale(0.8f); // Smaller font
    }

    /**
     * Creates a new Lutemon with the selected type and name.
     */
    private void createLutemon() {
        String name = nameField.getText().trim();

        // Validate name
        if (name.isEmpty()) {
            errorLabel.setText("Name cannot be empty");
            errorLabel.setVisible(true);
            return;
        }

        // Create Lutemon based on selected type
        Lutemon lutemon = null;
        switch (selectedType) {
            case WHITE:
                lutemon = new WhiteLutemon(Storage.getInstance().getNextId(), name);
                break;
            case GREEN:
                lutemon = new GreenLutemon(Storage.getInstance().getNextId(), name);
                break;
            case PINK:
                lutemon = new PinkLutemon(Storage.getInstance().getNextId(), name);
                break;
            case ORANGE:
                lutemon = new OrangeLutemon(Storage.getInstance().getNextId(), name);
                break;
            case BLACK:
                lutemon = new BlackLutemon(Storage.getInstance().getNextId(), name);
                break;
        }

        // Add Lutemon to storage
        if (lutemon != null) {
            Storage.getInstance().addLutemon(lutemon);
            homeScreen.updateLutemonList();
            homeScreen.getGame().saveGame();
            remove(); // Close dialog
        }
    }

    /**
     * Shows the dialog on the given stage.
     *
     * @param stage The stage to show the dialog on
     */
    public void show(Stage stage) {
        stage.addActor(this);
    }
}
