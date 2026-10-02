# Micro-Éditeur

Éditeur de formes géométriques vectorielles en Java AWT, démontrant l'application de 6 design patterns pour une architecture maintenable et testable. Projet universitaire réalisé **seul**, dans le cadre d'un cours d'architecture logicielle à l'**Université de Bordeaux** (avril 2026). Le sujet imposait 8 cas d'usage ; cette version les couvre tous.

## Fonctionnalités

- Créer et éditer des formes (rectangles, polygones réguliers)
- Grouper/dégrouper des formes (groupes imbriqués)
- Annuler/rétablir toute opération (undo/redo)
- Éditer les propriétés d'une forme (taille, position, rotation, couleur...)
- Glisser-déposer des formes depuis la barre d'outils
- Sauvegarder/charger un document
- Persistance de l'état de la barre d'outils au redémarrage

## Technologies

- **Langage** : Java (AWT, Swing pour les dialogues)
- **Build** : Maven (`pom.xml`)
- **Tests** : JUnit

## Installation et lancement

Prérequis : Java 11 (version configurée dans `pom.xml`), Maven 3.6+.

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="ui.MainFrame"
```

## Tests

```bash
mvn test
```

**Résultat vérifié sur cette version** : 20 tests, tous passent (8 tests Command, 10 tests Model, 2 tests Persistence).

## Architecture et design patterns

Le projet suit une architecture **MVC** (Modèle `model/`, Vue `ui/`, Contrôleur `SelectionController`) et met en œuvre 6 design patterns :

| Pattern | Classe(s) | Rôle |
|---|---|---|
| **Command** | `Command`, `CommandHistory`, `AddShapeCommand`, `EditShapeCommand`... | Undo/redo via deux piles (annuler/rétablir) |
| **Memento** | `ShapeMemento` | Capture/restauration de l'état d'une forme avant modification |
| **Observer** | `Scene`, `SceneListener` | Notifie l'interface quand le modèle change |
| **Strategy** | `PropertyEditorStrategy`, `RectanglePropertyEditor`, `PolygonPropertyEditor`, `GroupPropertyEditor` | Édition de propriétés spécifique à chaque type de forme, sans `if/else` en cascade |
| **Factory** | `PropertyEditorFactory`, méthodes factory de `EditShapeCommand` | Création d'objets sans exposer le type concret à l'appelant |
| **Composite** | `Shape` (interface), `Rectangle`/`RegularPolygon` (feuilles), `Group` (composite) | Traite groupes et formes individuelles de façon uniforme |

## Structure du code

```
src/main/java/
├── model/       # Shape, Rectangle, RegularPolygon, Group, Scene
├── ui/          # MainFrame, WhiteboardPanel, ToolbarPanel, PropertyDialog...
│   └── strategy/  # Éditeurs de propriétés (Strategy)
├── command/     # Command, CommandHistory, et toutes les commandes
└── util/        # GeometryUtils
src/test/java/   # 20 tests JUnit (command/, model/, persistence/)
```

## Limites connues

- Certains endroits utilisent encore `instanceof` pour distinguer les types de formes (clamping des limites, rendu) — une interface dédiée ou un pattern Visitor réglerait ça proprement
- Toute modification du modèle déclenche un repaint complet, même pour un simple changement de sélection

## Auteur

Projet réalisé seul par **Kousay Amara**.

## Ce que j'ai réalisé

L'intégralité du projet (conception, implémentation des 6 patterns, tests) : architecture MVC, moteur d'undo/redo (Command + Memento), persistance (sauvegarde/chargement de documents et de la barre d'outils), et le mécanisme d'édition de propriétés par Strategy.

## Origine du sujet

Le sujet (8 cas d'usage imposés) a été fourni dans le cadre du cours. Son contenu n'est pas reproduit ici ; seule l'implémentation est de mon fait.

## Licence

Ce projet est sous licence [MIT](LICENSE).
