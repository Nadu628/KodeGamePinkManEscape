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
