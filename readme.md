# 8-Puzzle GUI

A Java Swing graphical interface for creating, editing, randomizing, and solving sliding-tile puzzles. The application uses the `8puzzle` solver library and can display solver progress and the solution path.

## Features

- Interactive puzzle board with manual tile movement
- Randomize and reset the current board
- Store and restore a board state
- Solve puzzles using heuristic strategies:
  - None
  - Hamming
  - Weighted Hamming
  - Manhattan
  - Weighted Manhattan
- Display a solution stepwise or at selectable speeds
- Show solver statistics such as elapsed time, visited states, duplicate states, and move count
- Support for an `n x n` board size from the command line

## Requirements

- Java Development Kit (JDK)
- IntelliJ IDEA, or a Java compiler
- The companion `8puzzle` library, which provides:
  - `Board8Puzzle`
  - `HeuristicStateSpaceSolver`
  - `AStateSpaceSolver`
  - `IEvaluate`

The IntelliJ project currently expects the library at:

```text
../2018-java-8puzzle/out/artifacts/8puzzle/8puzzle.jar
```

If the library is stored elsewhere, update the `8puzzle` library entry in IntelliJ IDEA or provide the correct JAR on the compile/runtime classpath.

## Running in IntelliJ IDEA

1. Open the project directory in IntelliJ IDEA.
2. Ensure the project uses a configured JDK.
3. Ensure the `8puzzle` library is available and resolves in `GUI_8Puzzle.iml`.
4. Create or select a run configuration for:

   ```text
   de.hrw.wi.puzzle.gui.MainGUI
   ```

5. Run the configuration.

The default board is 3x3. To use another dimension, add one positive integer as the program argument, for example `4` for a 4x4 board.

## Command-line compilation and execution

From the project root, assuming the dependency is at the path above:

```bash
mkdir -p out
javac -cp ../2018-java-8puzzle/out/artifacts/8puzzle/8puzzle.jar \
      -d out \
      $(find src -name '*.java')

java -cp out:../2018-java-8puzzle/out/artifacts/8puzzle/8puzzle.jar \
     de.hrw.wi.puzzle.gui.MainGUI
```

On Windows, replace `:` in the classpath with `;`.

To start a different board size:

```bash
java -cp out:../2018-java-8puzzle/out/artifacts/8puzzle/8puzzle.jar \
     de.hrw.wi.puzzle.gui.MainGUI 4
```

## Project structure

```text
src/
└── de/hrw/wi/puzzle/gui/
    ├── MainGUI.java                 # Application entry point
    ├── window/
    │   ├── EightPuzzleGUI.java      # Main Swing window and controls
    │   ├── BoardPanel.java          # Board buttons and user interaction
    │   └── GUIBoard8Puzzle.java     # GUI-specific puzzle and solver integration
    └── deprecated/
        ├── DynamicPuzzleGUI.java    # Legacy GUI implementation
        └── GUI.java                 # Legacy GUI implementation
```

There is no Maven or Gradle build file in this repository; IntelliJ module files and the external `8puzzle` library provide the current project configuration.
