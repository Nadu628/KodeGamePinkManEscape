**Pink Man's Escape**

Pink Man’s Escape is an educational Android application designed to introduce children to the basics of algorithmic thinking using a guided, puzzle-based programming system.
The app includes both **child gameplay features** and a **parent dashboard** with data tracking and visualization.

**Overview**

Children solve mazes by dragging and dropping logic blocks such as movement commands, loops, conditionals, and simple functions.
Parents can register, add children, and view progress data based on completed levels and collected items.

The app supports five languages:

- English
- French
- Spanish
- Haitian Creole
- Japanese

All screens and UI text dynamically update when a language is selected.

**Features**

**Child Features**
- Drag-and-drop programming system
- Movement commands (Up, Down, Left, Right)
- Loop blocks (Repeat X times, Repeat 3×)
- Conditional blocks (If strawberry, While strawberry, Until goal)
- Function blocks (Start, End, Call Func)
- Animated character with idle, run, and jump states
- Easy and Hard difficulty modes
- Instructions screen
- Sound effects and background music
- Screen transition animations
- Multi-language support

**Procedural Maze Generation**  
The game features a procedural maze generator that creates levels dynamically rather than relying on static maps.

**Maze Generator Characteristics:**
- Generates new mazes for each level
- Ensures a valid path from start to goal
- Supports different rules for Easy and Hard modes
- Dynamically places:
  - Walls
  - Hazards (spikes)
  - Rewards (strawberries)
- Prevents unfair layouts by keeping hazards away from critical paths

This system provides high replayability and prevents players from memorizing solutions.

**Pause Menu**  
During gameplay, players can open a pause menu overlay that allows them to:
- Resume gameplay
- Turn background music on/off
- Change the application language
- Log out
  
**Parent Features**
- Parent login and registraion
- Add child profiles
- View list of registered children
- Navigate to child progress screen
- Logout button available on all dashboard-related screens

**Child Progress Tracking**
- Saves level completions and number of strawberries collected
- Displays data visualized with:
  - **Line chart** (strawberries by level)
  - **Bar chart** (strawberries by level)
- Charts built using MPAndroidChart
- Scrollable progress screen

**Localization System**
- Language picker available on the splash screen and user type screen
- Uses custom locale storage
- Uses dynamic, runtime text updates
- Strings stored in:
  - values/strings.xml (English)
  - values-fr/strings.xml (French)
  - values-es/strings.xml (Spanish)
  - values-ht/strings.xml (Haitian Creole)
  - values-ja/strings.xml (Japan)
- Uses a dedicated LocalizedString() composable for all text rendering

**Architecture**  
The project follows a modular and maintainable Android architecture.

- UI Layer (Jetpack Compose)
  -Screens
  - Navigation graph
  - Reusable composables
  - Custom animations
  - Sprite rendering
  - Dialogs and overlays

- Logic Layer
  - Game execution engine
  - Command parsing and execution
  - Program validation
  - Player movement rules
  - Maze generation logic

- Data Layer
  - Room database for persistence
  - Progress records per child
  - Parent/child relationships
  - Local storage for settings and progress

- Audio System
  - Centralized AudioManager
  - SoundPool for SFX
  - MediaPlayer for background music
  - Controlled from gameplay and pause menu

**Technologies Used**
- Kotlin
- Jetpack Compose
- Android Navigation Component
- Room Database
- MPAndroidChart
- SoundPool & MediaPlayer
- Coroutines
- Material Design Components

**Future Enhancements**  
Planned or potential improvements include:
- Achievement and badge system
- Character skin customization
- Difficulty scaling based on performance
- Daily challenge mode
- Accessibility options (text-to-speech, high contrast mode)
- Cloud backup for progress
- Online leaderboard (optional)
- In-app maze editor for children

**Demo Video:**  
Full Walkthrough: https://www.youtube.com/watch?v=nyXTztZDa9s
<img width="1600" height="900" alt="kodegame_thumbnail" src="https://github.com/user-attachments/assets/4958cbc4-e1f3-4f13-a629-a2a57ec45254" />

