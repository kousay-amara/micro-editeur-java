package command;

import model.Scene;
import model.Shape;
import model.Group;
import java.util.ArrayList;
import java.util.List;

public class UngroupCommand implements Command {
    private Scene scene;
    private Group group;
    private Group parentGroup;
    private List<Shape> children;
    private int groupIndexInParent;
    private int groupIndexInScene;
    
    public UngroupCommand(Scene scene, Group group) {
        this.scene = scene;
        this.group = group;
        this.children = new ArrayList<>(group.getChildren());
        this.parentGroup = group.getParent();
        
        if (parentGroup != null) {
            this.groupIndexInParent = parentGroup.getChildren().indexOf(group);
        } else {
            this.groupIndexInScene = scene.getShapes().indexOf(group);
        }
    }
    
    @Override
    public void execute() {
        if (parentGroup != null) {
            groupIndexInParent = parentGroup.getChildren().indexOf(group);
            parentGroup.remove(group);
            // Ajouter enfants à la position du groupe supprimé + clear parent refs
            for (int i = 0; i < children.size(); i++) {
                Shape child = children.get(i);
                if (child instanceof Group) {
                    ((Group) child).setParent(null);
                }
                parentGroup.addAt(groupIndexInParent + i, child);
            }
        } else {
            groupIndexInScene = scene.getShapes().indexOf(group);
            scene.removeShape(group);
            // Ajouter enfants à la position du groupe supprimé
            for (int i = 0; i < children.size(); i++) {
                Shape child = children.get(i);
                if (child instanceof Group) {
                    ((Group) child).setParent(null);
                }
                scene.addShapeAt(groupIndexInScene + i, child);
            }
        }
    }
    
    @Override
    public void undo() {
        if (parentGroup != null) {
            // Retirer les enfants dans l'ordre inverse pour éviter les décalages d'index
            for (int i = children.size() - 1; i >= 0; i--) {
                parentGroup.remove(children.get(i));
            }
            // Remettre le groupe au bon index et restaurer parent refs
            parentGroup.addAt(groupIndexInParent, group);
            for (Shape child : children) {
                if (child instanceof Group) {
                    ((Group) child).setParent(group);
                }
            }
        } else {
            // Retirer les enfants dans l'ordre inverse
            for (int i = children.size() - 1; i >= 0; i--) {
                scene.removeShape(children.get(i));
            }
            // Remettre le groupe au bon index
            scene.addShapeAt(groupIndexInScene, group);
            for (Shape child : children) {
                if (child instanceof Group) {
                    ((Group) child).setParent(group);
                }
            }
        }
    }
    
    @Override
    public String getDescription() {
        return "Dégrouper " + children.size() + " formes";
    }
}
