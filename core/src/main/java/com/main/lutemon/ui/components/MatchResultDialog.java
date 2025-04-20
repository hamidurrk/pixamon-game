package com.main.lutemon.ui.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.main.lutemon.model.lutemon.Lutemon;
import com.main.lutemon.screens.BattleScreen;

/**
 * Dialog to display the match result and provide options to restart or quit.
 */
public class MatchResultDialog extends Window {
    private final BattleScreen battleScreen;
    private final Lutemon playerLutemon;
    private final Lutemon opponentLutemon;
    private final boolean playerWon;

    /**
     * Creates a new match result dialog.
     *
     * @param battleScreen The battle screen
     * @param playerLutemon The player's Lutemon
     * @param opponentLutemon The opponent's Lutemon
     * @param playerWon Whether the player won
     * @param skin The skin to use for UI elements
     */
    public MatchResultDialog(BattleScreen battleScreen, Lutemon playerLutemon, Lutemon opponentLutemon,
                            boolean playerWon, Skin skin) {
        super(playerWon ? "Victory!" : "Defeat!", skin);
        this.battleScreen = battleScreen;
        this.playerLutemon = playerLutemon;
        this.opponentLutemon = opponentLutemon;
        this.playerWon = playerWon;

        // Set up dialog properties
        setModal(true);
        setMovable(false);
        setResizable(false);

        // Set size to 70% of the screen size
        float width = battleScreen.getStage().getWidth() * 0.7f;
        float height = battleScreen.getStage().getHeight() * 0.7f;
        setSize(width, height);
        setPosition((battleScreen.getStage().getWidth() - width) / 2,
                   (battleScreen.getStage().getHeight() - height) / 2);

        // Create content
        createContent(skin);
    }

    /**
     * Creates the dialog content.
     *
     * @param skin The skin to use for UI elements
     */
    private void createContent(Skin skin) {
        Table contentTable = new Table();
        contentTable.pad(20);

        // Result message
        String resultMessage = playerWon
            ? "Congratulations! Your " + playerLutemon.getName() + " has won the battle!"
            : "Oh no! Your " + playerLutemon.getName() + " has been defeated!";

        Label resultLabel = new Label(resultMessage, skin);
        resultLabel.setWrap(true);
        resultLabel.setAlignment(Align.center);
        resultLabel.setFontScale(1.5f); // Larger font for better visibility

        // Stats message
        String statsMessage = playerWon
            ? "Your " + playerLutemon.getName() + " defeated " + opponentLutemon.getName() + "!"
            : opponentLutemon.getName() + " defeated your " + playerLutemon.getName() + "!";

        Label statsLabel = new Label(statsMessage, skin);
        statsLabel.setWrap(true);
        statsLabel.setAlignment(Align.center);
        statsLabel.setFontScale(1.3f); // Larger font for better visibility

        // Buttons with larger text
        TextButton restartButton = new TextButton("Restart Match", skin);
        TextButton quitButton = new TextButton("Quit to Home", skin);

        // Make button text larger
        restartButton.getLabel().setFontScale(1f);
        quitButton.getLabel().setFontScale(1f);

        // Button listeners
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                battleScreen.restartBattle(playerLutemon, opponentLutemon);
                remove(); // Remove dialog
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                battleScreen.getGame().navigateToHome();
                remove(); // Remove dialog
            }
        });

        // Add to content table
        // Make content scale with dialog size
        float contentWidth = getWidth() * 0.8f;
        float buttonWidth = getWidth() * 0.8f;
        float buttonHeight = getHeight() * 0.15f;

        contentTable.add(resultLabel).width(contentWidth).pad(20).row();
        contentTable.add(statsLabel).width(contentWidth).pad(20).row();
        contentTable.add(restartButton).width(buttonWidth).height(buttonHeight).pad(20).row();
        contentTable.add(quitButton).width(buttonWidth).height(buttonHeight).pad(20);

        add(contentTable).expand().fill();
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
