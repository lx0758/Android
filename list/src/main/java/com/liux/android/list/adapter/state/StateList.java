package com.liux.android.list.adapter.state;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 包裹条目状态的 ArrayList
 * @param <T>
 */
public class StateList<T> extends ArrayList<T> {

    private List<State<T>> mStates = new ArrayList<>();

    public static <T> StateList<T> from(List<T> data) {
        StateList<T> stateList = new StateList<>();
        if (data != null) {
            stateList.addAll(data);
        }
        return stateList;
    }

    @Override
    public boolean add(T t) {
        mStates.add(new State<>(t));
        return super.add(t);
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index != -1) {
            mStates.remove(index);
        }
        return super.remove(o);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        List<State<T>> list = new ArrayList<>();
        for (T t : c) {
            list.add(new State<>(t));
        }
        mStates.addAll(list);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        List<State<T>> list = new ArrayList<>();
        for (T t : c) {
            list.add(new State<>(t));
        }
        mStates.addAll(index, list);
        return super.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        Iterator<State<T>> iterator = mStates.iterator();
        while (iterator.hasNext()) {
            State<T> state = iterator.next();
            if (c.contains(state.getData())) {
                iterator.remove();
            }
        }
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        Iterator<State<T>> iterator = mStates.iterator();
        while (iterator.hasNext()) {
            State<T> state = iterator.next();
            if (!c.contains(state.getData())) {
                iterator.remove();
            }
        }
        return super.retainAll(c);
    }

    @Override
    public void clear() {
        mStates.clear();
        super.clear();
    }

    @Override
    public T set(int index, T element) {
        mStates.set(index, new State<>(element));
        return super.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        mStates.add(index, new State<>(element));
        super.add(index, element);
    }

    @Override
    public T remove(int index) {
        mStates.remove(index);
        return super.remove(index);
    }

    public State<T> getState(int index) {
        return mStates.get(index);
    }

    public State<T> getState(T t) {
        int index = indexOf(t);
        if (index != -1) {
            return mStates.get(index);
        }
        return null;
    }

    public List<State<T>> getStates() {
        return mStates;
    }
}
