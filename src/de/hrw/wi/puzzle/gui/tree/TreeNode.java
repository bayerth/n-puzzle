package de.hrw.wi.puzzle.gui.tree;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeNode<T> {

    private T payload;
    private boolean isMarked;
    private int weight;
    private List<TreeNode<T>> children;


    public boolean dfs(ICommand<T> cmd) {
        boolean result = false;
        if (!isMarked) {
            result = cmd.execute(payload);
            if (result) { return result; }
            for (TreeNode<T> node : children) {
                result = node.dfs(cmd);
                if (result) { return result; }
            }
        }
        return false;
    }

    public TreeNode<T> getChild(int i) {
        return null;
    }

    public TreeNode(T payload, int weight) {
        init(payload, weight);
    }

    private void init(T payload, int weight) {
        this.payload = payload;
        this.weight = weight;
        children = new LinkedList<>();
    }

    public TreeNode(T payload) {
        init(payload, 0);
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked() {
        isMarked = true;
    }

    public boolean isLead() {return children.isEmpty();}

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public boolean addChild(T t) {
        if (children.stream().filter(s -> s.getPayload().equals(t)).collect(Collectors.counting()) == 0) {
            return children.add(new TreeNode<>(t, this.weight + 1));
        }
        return false;
    }

    public int getWeight() {
        return weight;
    }

    public boolean addChild(TreeNode<T> node) {
        return false;
    }
}
