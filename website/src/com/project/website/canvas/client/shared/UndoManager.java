package com.project.website.canvas.client.shared;

import java.util.Stack;

import com.project.shared.data.Pair;
import com.project.shared.utils.IterableUtils;
import com.project.shared.utils.ObjectUtils;

public class UndoManager
{
    public interface UndoRedoPair
    {
        void redo();
        void undo();
    }

    private static UndoManager INSTANCE = new UndoManager();

    private final Stack<Pair<Object, UndoRedoPair>> _future = new Stack<Pair<Object, UndoRedoPair>>();
    private final Stack<Pair<Object, UndoRedoPair>> _past = new Stack<Pair<Object, UndoRedoPair>>();


    public static UndoManager get()
    {
        return UndoManager.INSTANCE;
    }

    public void addAndRedo(Object owner, UndoRedoPair undoRedoPair)
    {
        this.add(owner, undoRedoPair);
        undoRedoPair.redo();
    }

    public void add(Object owner, UndoRedoPair undoRedoPair)
    {
        this._past.push(new Pair<Object, UndoRedoPair>(owner, undoRedoPair));
        this._future.clear();
    }

    public void removeOwner(Object owner)
    {
        UndoManager.filter(owner, this._past);
        UndoManager.filter(owner, this._future);
    }

    public void undo()
    {
        Pair<Object, UndoRedoPair> step = UndoManager.moveStep(this._past, this._future);
        if (null != step) {
            step.getB().undo();
        }
    }

    public void redo()
    {
        Pair<Object, UndoRedoPair> step = UndoManager.moveStep(this._future, this._past);
        if (null != step) {
            step.getB().redo();
        }
    }

    private static void filter(Object owner, Stack<Pair<Object, UndoRedoPair>> stack)
    {
        for (Pair<Object, UndoRedoPair> step : IterableUtils.toList(stack)) {
            if (ObjectUtils.areEqual(step.getA(), owner)) {
                stack.remove(step);
            }
        }
    }

    private static Pair<Object, UndoRedoPair> moveStep(final Stack<Pair<Object, UndoRedoPair>> from,
            final Stack<Pair<Object, UndoRedoPair>> target)
    {
        if (from.isEmpty()) {
            return null;
        }
        Pair<Object, UndoRedoPair> step = from.pop();
        target.push(step);
        return step;
    }
}
