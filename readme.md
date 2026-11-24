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
- Maven 3.x, optionally used for building and running tests
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
3. Ensure the `8puzzle` JAR is available at the path above.
4. Right-click `pom.xml` and select **Add as Maven Project** (or **Load Maven Project**). If it is already linked, reload it from the Maven tool window.
5. Create or select a run configuration for:

   ```text
   de.hrw.wi.puzzle.gui.MainGUI
   ```

6. Run the configuration.

The default board is 3x3. To use another dimension, add one positive integer as the program argument, for example `4` for a 4x4 board.

## Running with Maven

Start the default 3x3 board from the project root:

```bash
mvn compile exec:java
```

To start a different board size, pass the dimension as an argument:

```bash
mvn compile exec:java -Dexec.args="4"
```

The `compile` phase is needed before starting the application, especially after
`mvn clean` or a change to the source files.

## Command-line compilation and execution

From the project root, assuming the dependency is at the path above:

```bash
mkdir -p out
javac -cp ../2018-java-8puzzle/out/artifacts/8puzzle/8puzzle.jar \
      -d out \
      $(find src -name '*.java' ! -path 'src/test/*')

java -cp out:../2018-java-8puzzle/out/artifacts/8puzzle/8puzzle.jar \
     de.hrw.wi.puzzle.gui.MainGUI
```

On Windows, replace `:` in the classpath with `;`.

To start a different board size:

```bash
java -cp out:../2018-java-8puzzle/out/artifacts/8puzzle/8puzzle.jar \
     de.hrw.wi.puzzle.gui.MainGUI 4
```

## Running tests

The solver tests use JUnit 5 and Maven:

```bash
mvn test
```

For individual tests in IntelliJ, first import or reload `pom.xml` as described above. Maven supplies JUnit and marks `src/test/java` as a test source root. Then run a test using the gutter icon next to the test class or method. The older IntelliJ module configuration does not include JUnit on its own.

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

## Copyright and license

Copyright (C) 2019 Antony Hierlemann.

Copyright (C) 2019-2026 Thomas Bayer.

Antony Hierlemann created the original project work for a 3x3 puzzle, including the initial GUI and a restricted solver implementation. Thomas Bayer subsequently developed and maintains the n-puzzle GUI, the generalized n x n support, the extended solver integration and the remaining project components.

The project is free software licensed under the **GNU General Public License v3.0 or later**. See [LICENSE](LICENSE) for the full license text.
