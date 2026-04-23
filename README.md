<<<<<<< HEAD
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

**Coverage**: 18 tests
- 8 Command tests (undo/redo, commands)
- 10 Model tests (shapes, groups, operations)

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

- [ ] **Persistence**: Save/load scenes to file
- [ ] **Multi-level Undo**: Batch related commands (e.g., drag-move)
- [ ] **Layers**: Organize shapes into named layers
- [ ] **Performance**: Cache bounding boxes, optimize repaints
- [ ] **Transformation Tool**: Unified scale/rotate gizmo
- [ ] **Plugin Architecture**: Allow custom shape types at runtime

## Code Metrics

| Metric | Value |
|--------|-------|
| Total Lines (Java) | ~3,500 |
| Test Coverage | 18 tests |
| Passing Tests | 18/18 (100%) |
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
=======
# Archi Java



## Getting started

To make it easy for you to get started with GitLab, here's a list of recommended next steps.

Already a pro? Just edit this README.md and make it your own. Want to make it easy? [Use the template at the bottom](#editing-this-readme)!

## Add your files

* [Create](https://docs.gitlab.com/user/project/repository/web_editor/#create-a-file) or [upload](https://docs.gitlab.com/user/project/repository/web_editor/#upload-a-file) files
* [Add files using the command line](https://docs.gitlab.com/topics/git/add_files/#add-files-to-a-git-repository) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin https://gitlab.emi.u-bordeaux.fr/koamara/Archi-java.git
git branch -M main
git push -uf origin main
```

## Integrate with your tools

* [Set up project integrations](https://gitlab.emi.u-bordeaux.fr/koamara/Archi-java/-/settings/integrations)

## Collaborate with your team

* [Invite team members and collaborators](https://docs.gitlab.com/user/project/members/)
* [Create a new merge request](https://docs.gitlab.com/user/project/merge_requests/creating_merge_requests/)
* [Automatically close issues from merge requests](https://docs.gitlab.com/user/project/issues/managing_issues/#closing-issues-automatically)
* [Enable merge request approvals](https://docs.gitlab.com/user/project/merge_requests/approvals/)
* [Set auto-merge](https://docs.gitlab.com/user/project/merge_requests/auto_merge/)

## Test and Deploy

Use the built-in continuous integration in GitLab.

* [Get started with GitLab CI/CD](https://docs.gitlab.com/ci/quick_start/)
* [Analyze your code for known vulnerabilities with Static Application Security Testing (SAST)](https://docs.gitlab.com/user/application_security/sast/)
* [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://docs.gitlab.com/topics/autodevops/requirements/)
* [Use pull-based deployments for improved Kubernetes management](https://docs.gitlab.com/user/clusters/agent/)
* [Set up protected environments](https://docs.gitlab.com/ci/environments/protected_environments/)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!). Thanks to [makeareadme.com](https://www.makeareadme.com/) for this template.

## Suggestions for a good README

Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
>>>>>>> ef8889ec131af456e319848c480e2760b1d01df2
