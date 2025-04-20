# Lutemon Game - A 2D Pixel Art Game

Lutemon is a turn-based strategy game where players train, battle, and manage their Lutemons. Each Lutemon has unique stats, abilities, and animations, making the gameplay dynamic and engaging.

## Features

### 1. **Lutemon Management**
- **Create Lutemons**: Players can create Lutemons of different types (White, Green, Pink, Orange, Black) with unique base stats.
- **Train Lutemons**: Train Lutemons to gain experience and improve their stats.
- **Heal Lutemons**: Heal all Lutemons at home to restore their health.
- **Move Lutemons**: Move Lutemons between locations (Home, Training, Battle).

### 2. **Battle System**
- **Turn-Based Battles**: Engage in battles where players can attack, defend, or use special moves.
- **AI Opponent**: Battles include an AI-controlled enemy Lutemon with randomized actions.
- **Battle Stats**: Track wins, losses, and battles fought for each Lutemon.
- **Special Attacks**: Perform high-damage special attacks with a chance of missing.

### 3. **Training System**
- **Experience Gain**: Training increases Lutemon experience and improves stats.
- **Training Days**: Track the number of days a Lutemon has been trained.

### 4. **UI Components**
- **Home Screen**: Displays all Lutemons at home with their stats and animations.
- **Training Screen**: Allows players to select Lutemons for training.
- **Battle Arena**: A dynamic UI for battles, showing health bars, animations, and actions.
- **Stats Panel**: Displays detailed stats for each Lutemon, including health, attack, defense, experience, and battle records.

### 5. **Animations**
- **Animated Avatars**: Lutemons have animated avatars for idle, attack, defend, and special states.
- **Battle Animations**: Smooth animations for attacks, movements, and health changes.

### 6. **Persistence**
- **Save and Load**: Save the game state to a file and load it later.
- **Auto-Save**: Automatically saves the game progress at key points.

### 7. **Customizable UI**
- **Dynamic Scaling**: UI elements scale based on screen size for consistent appearance.
- **Theming**: Skins and fonts are customizable through asset files.

## Lutemon Types

Each Lutemon type has unique base stats:
- **White Lutemon**: Balanced stats.
- **Green Lutemon**: High attack, low defense.
- **Pink Lutemon**: High health, low defense.
- **Orange Lutemon**: High attack, low health.
- **Black Lutemon**: High defense, low health.

## Technical Details

### Core Classes
- **Lutemon**: Abstract base class for all Lutemon types.
- **LutemonStats**: Manages stats like health, attack, defense, experience, and level.
- **Battle**: Handles the logic for battles, including attacks, defenses, and special moves.
- **Storage**: Manages Lutemon storage and locations.
- **SaveManager**: Handles saving and loading game data.

### UI Components
- **HomeFragment**: Displays Lutemons at home.
- **TrainingScreen**: UI for training Lutemons.
- **BattleArena**: UI for battles, including health bars and animations.
- **StatsPanel**: Displays detailed stats for a selected Lutemon.

### Utilities
- **AssetLoader**: Loads textures, skins, and fonts.
- **AnimationManager**: Manages animations for Lutemons.
- **Constants**: Defines game-wide constants for scaling, paths, and gameplay mechanics.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 21 or higher.
- LibGDX framework.

### Running the Game
1. Clone the repository.
2. Open the project in your preferred IDE.
3. Build and run the project.

### Controls
- **Home Screen**: View and manage Lutemons.
- **Training Screen**: Select Lutemons for training.
- **Battle Arena**: Use buttons to attack, defend, or perform special moves.
