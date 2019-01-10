package de.hrw.wi.puzzle.gui.tree;

public interface ICommand<T> {
    boolean execute(T t);
}
