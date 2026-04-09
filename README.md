# <img alt="" height="24" src="assets/app_icon.png" width="24"/> Clock Plucker

Clock Plucker is a utility app for Storytellers of the social deduction game **Blood on the Clocktower**. It streamlines the process of bagging a script and enables the Gardener Loric to be player-driven (that is, enables players to request specific characters) by intelligently assigning characters based on player preferences while strictly adhering to all setup rules and character interactions.

## <img alt="" height="24" src="assets/icon_amnesiac.png" width="24"/> Features 


- **Intelligent Role Assignment**: Uses a constraint satisfaction engine to ensure all script-specific setup modifiers (e.g., Baron, Sentinel, Balloonist) are correctly applied.
- **Storyteller Options**: Supports multiple selection modes:
    - **Selection Restrictions**: The Storyteller can decide how many characters each player can select: any number, a specified number of each alignment, or a specified number of each type.
    - **Alignment/Type Priority**: Prioritize giving players specific types (Townsfolk, Minion, etc.) or alignments.
    - **Weighted Selection**: Factors in player history and preferences to ensure a fair distribution of roles over time.
- **Script Support**: Import custom scripts in standard JSON format. Supports official characters and "bootlegger" custom content.
- **Complex Logic Handling**: Built-in support for tricky character interactions like the **Marionette** (ensuring they sit next to their Demon), **Legion**, **Huntsman/Damsel** dependencies, **Djinn** jinxes, and more.
- **Modern UI**: A clean, step-by-step Jetpack Compose interface for managing players, choosing scripts, and revealing the Grimoire.

## <img alt="" height="24" src="assets/icon_engineer.png" width="24"/> Technical Implementations

### Role Solver Engine
Clock Plucker utilizes the **Choco Solver** library to assign characters to players. It models the role assignment problem as a Constraint Satisfaction Problem (CSP).
- **Constraints**: Implements rules for character counts, mutual exclusions (hard jinxes), and conditional setup modifiers.
- **Optimization**: Maximizes a "profit matrix" derived from player preferences and historical data to find the most satisfying valid assignment.
- **Deception Logic**: Correctly handles characters who "think" they are someone else (e.g., Drunk, Lunatic, Marionette) by managing "reserved" character slots that don't physically exist in the bag but affect the player's perceived identity.

### Data Architecture
- **Room Persistence**: Uses Room database to store imported scripts, metadata, and last-accessed timestamps.
- **JSON Parsing**: Script loading handles various BoTC JSON formats, including the official Script Tool exports and custom character definitions.
- **ViewModel & State**: Leverages `MainViewModel` with Compose's `mutableState` and `StateFlow` for reactive UI updates and survival across configuration changes.

## <img alt="" height="24" src="assets/icon_librarian.png" width="24"/> Libraries Used

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android toolkit for building native UI.
- **Constraint Solving**: [Choco Solver](https://choco-solver.org/) - Java library for Constraint Programming.
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation) - For handling app flow.
- **Database**: [Room](https://developer.android.com/training/data-storage/room) - SQLite abstraction layer.
- **Concurrency**: Kotlin Coroutines & Flow.

## <img alt="" height="24" src="assets/icon_tinker.png" width="24"/> Building and Running

1. **Prerequisites**:
    - Android Studio Ladybug (or newer)
    - JDK 17
    - Android SDK 34+

2. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/ClockPlucker.git
   ```

3. **Open in Android Studio**:
   Open the project folder and allow Gradle to sync.

4. **Build**:
   Run `./gradlew assembleDebug` or use the "Run" button in Android Studio to deploy to a device/emulator.

## <img alt="" height="24" src="assets/icon_mayor.png" width="24"/> License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
