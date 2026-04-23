# Micro-Editeur: Vector Graphics Editor

A Java AWT application demonstrating clean architecture and design patterns for building maintainable, extensible software.

## Quick Start

```bash
cd projet
mvn clean compile
mvn exec:java -Dexec.mainClass="ui.MainFrame"
```

## Project Overview

### What is it?

Micro-Editeur is a lightweight vector graphics editor allowing users to:
- Create and edit shapes (rectangles, regular polygons)
- Group/ungroup shapes
- Undo/redo operations
- Edit shape properties (size, position, rotation, color, etc.)
- Drag shapes to/from toolbar
- Save/load documents
- Restore toolbar state at startup

### Architecture

The project follows **Model-View-Controller (MVC)** pattern:

```
Model (Domain)           View (UI)              Controller (Logic)
├── Shape               ├── WhiteboardPanel    ├── SelectionController
├── Rectangle           ├── ToolbarPanel       └── Event Handlers
├── RegularPolygon      ├── PropertyDialog
├── Group               └── AwtShapeRenderer
└── Scene
```

**Key Design Patterns Used**:
- Command Pattern (undo/redo)
- Memento Pattern (state capture/restore)
- Observer Pattern (event notifications)
- Strategy Pattern (property editors)
- Factory Pattern (object creation)
- Composite Pattern (shape hierarchies)
- Controller Pattern (business logic)

## Documentation

### For Understanding Architecture
📄 **[ARCHITECTURE.md](../ARCHITECTURE.md)**
- Overall system design
- Data flow examples
- Pattern relationships
- SOLID principles compliance

### For Learning Design Patterns
📄 **[PATTERNS.md](../PATTERNS.md)**
- Quick reference for each pattern
- Before/after examples
- Problem/solution for each pattern
- Learning path through codebase

### For Project Report
📄 **[RAPPORT_FINAL.tex](RAPPORT_FINAL.tex)** / **[RAPPORT_FINAL.pdf](RAPPORT_FINAL.pdf)**
- Submission report aligned with the current codebase
- Implemented patterns and known limitations
- Test summary and use-case coverage

## Code Structure

```
projet/src/main/java/
├── model/              # Domain classes
│   ├── Shape.java     # Base interface
│   ├── Rectangle.java
│   ├── RegularPolygon.java
│   ├── Group.java     # Composite node
│   └── Scene.java     # Collection container
│
├── ui/                # User interface
│   ├── MainFrame.java
│   ├── WhiteboardPanel.java
│   ├── ToolbarPanel.java
│   ├── PropertyDialog.java
│   ├── SelectionController.java  # Extracted business logic
│   ├── UIConstants.java          # Configuration
│   ├── AwtShapeRenderer.java
│   ├── ShapeRenderer.java
│   ├── ControlPanel.java
│   └── strategy/      # Property editors (Strategy pattern)
│       ├── PropertyEditorStrategy.java
│       ├── RectanglePropertyEditor.java
│       ├── PolygonPropertyEditor.java
│       ├── GroupPropertyEditor.java
│       └── PropertyEditorFactory.java
│
├── command/           # Command pattern for undo/redo
│   ├── Command.java
│   ├── CommandHistory.java
│   ├── AddShapeCommand.java
│   ├── RemoveShapeCommand.java
│   ├── MoveShapeCommand.java
│   ├── EditShapeCommand.java
│   ├── GroupCommand.java
│   ├── UngroupCommand.java
│   ├── AddPrototypeCommand.java
│   ├── RemovePrototypeCommand.java
│   └── ShapeMemento.java
│
└── util/              # Utilities
    └── GeometryUtils.java  # Shared geometry calculations
```

## Key Features

### Undo/Redo
- **Double-stack pattern**: separate undo and redo stacks
- **All operations undoable**: shape creation, editing, moving, grouping
- **State restoration**: uses Memento pattern to capture/restore shapes

### Persistence
- **Document save/load**: saves the current scene to a file and reloads it later
- **Toolbar restoration**: toolbar prototypes are persisted and restored at startup
- **Model-only format**: persistence stores shapes, groups, and their properties without UI state

### Grouping
- **Composite pattern**: Groups behave like shapes
- **Recursive operations**: move, rotate, scale apply to all children
- **Nested groups**: groups can contain other groups

### Property Editing
- **Strategy pattern**: each shape type has dedicated editor
- **Type-safe editing**: dedicated fields for Rectangle, Polygon, Group
- **Inline validation**: bounds checking, side count validation

### Visual Feedback
- **Selection highlighting**: selected shapes are lighter/visible
- **Drag preview**: shapes preview while dragging
- **Toolbar preview**: each shape shows preview in toolbar

## Design Highlights

### Separation of Concerns

**WhiteboardPanel** (215 lines)
- Pure event handling
- Delegates to SelectionController
- Minimal business logic

**SelectionController** (150 lines)
- Selection state management
- Grouping operations
- Shape editing coordination

**AwtShapeRenderer** (285 lines)
- Pure rendering logic
- Uses GeometryUtils for calculations
- No business logic

### Extensibility (Open/Closed Principle)

**Adding New Shape Type**:
1. Create class implementing Shape
2. Create PropertyEditor for new type
3. Done! No changes needed to:
   - PropertyDialog
   - WhiteboardPanel
   - CommandHistory

**Adding New Operation**:
1. Implement Command interface
2. Done! CommandHistory handles undo/redo automatically

### Maintainability

**Magic Numbers**
- Centralized in UIConstants
- Change toolbar width in 1 place, affects entire UI

**Geometry Logic**
- Shared in GeometryUtils
- Used by renderer, bounds calculation, shape clamping
- Fix bugs in 1 location

**Property Editing**
- Duplicated code eliminated via Strategy pattern
- Each editor focuses on one shape type
- Common error handling

## Testing

### Running Tests

```bash
mvn test
```

**Coverage**: 20 tests
- 8 Command tests (undo/redo, commands)
- 10 Model tests (shapes, groups, operations)
- 2 Persistence tests (save/load of shapes and nested groups)

**All tests passing** ✅

### Test Strategy

- Unit tests for core domain logic
- Integration tests for command execution
- Tests verify undo/redo functionality

## Building

### Requirements
- Java 8+
- Maven 3.6+

### Build
```bash
mvn clean compile
```

### Run
```bash
mvn exec:java -Dexec.mainClass="ui.MainFrame"
```

## Known Limitations & Future Work

### Current Limitations

1. **Type Casting** (WhiteboardPanel bounds clamping)
   - Uses instanceof Rectangle vs RegularPolygon
   - Future: Implement BoundedShape interface

2. **Rendering Dispatch** (AwtShapeRenderer)
   - Uses instanceof for shape types
   - Future: Implement Visitor pattern

3. **Notifications** (SceneListener)
   - All changes trigger full repaint (including selection)
   - Future: Separate SelectionListener for selection-only changes

### Future Enhancements

- [ ] **Multi-level Undo**: Batch related commands (e.g., drag-move)
- [ ] **Layers**: Organize shapes into named layers
- [ ] **Performance**: Cache bounding boxes, optimize repaints
- [ ] **Transformation Tool**: Unified scale/rotate gizmo
- [ ] **Plugin Architecture**: Allow custom shape types at runtime

## Code Metrics

| Metric | Value |
|--------|-------|
| Total Lines (Java) | ~3,500 |
| Test Coverage | 20 tests |
| Passing Tests | 20/20 (100%) |
| Cyclomatic Complexity | Low (refactored) |
| Code Duplication | Eliminated (GeometryUtils, PropertyEditorStrategy) |

## Patterns Implemented

| Pattern | Location | Purpose |
|---------|----------|---------|
| **MVC** | model/, ui/, SelectionController | Separate concerns |
| **Command** | command/ | Undo/redo |
| **Memento** | ShapeMemento | State capture |
| **Observer** | Scene/SceneListener | Event notification |
| **Strategy** | PropertyEditorStrategy | Polymorphic editing |
| **Factory** | PropertyEditorFactory, EditShapeCommand | Object creation |
| **Composite** | Group, Shape | Shape hierarchies |
| **Controller** | SelectionController | Business logic |

## Learning Resources

### Understand the Patterns
1. Read [PATTERNS.md](../PATTERNS.md) for quick reference
2. Look at specific pattern implementation in code
3. Compare with the project report in [RAPPORT_FINAL.tex](RAPPORT_FINAL.tex)

### Understand the Architecture
1. Start with [ARCHITECTURE.md](../ARCHITECTURE.md)
2. Follow data flow examples
3. Trace user actions through code

### Improve the Code
1. Review "Future Work" above
2. Look at "Known Limitations"
3. See pattern recommendations in [PATTERNS.md](../PATTERNS.md)

## Contributing

When adding features:
1. Follow established patterns
2. Add tests for new functionality
3. Maintain separation of concerns
4. Update documentation (`../ARCHITECTURE.md`, `../PATTERNS.md`, `RAPPORT_FINAL.tex`)

## License

Educational project - MIT License

## Contact

For questions about patterns or architecture, see documentation files:
- [ARCHITECTURE.md](../ARCHITECTURE.md) – System design
- [PATTERNS.md](../PATTERNS.md) – Pattern reference
- [RAPPORT_FINAL.tex](RAPPORT_FINAL.tex) – Submission report
