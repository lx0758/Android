package com.liux.android.rx.lifecycle;

import android.app.Activity;
import android.app.Fragment;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * 2018/3/15
 * By Liux
 * lx0758@qq.com
 */

public class EventHelper {

    public static EventHelper with(Activity activity) {
        return new EventHelper(activity);
    }

    public static EventHelper with(Fragment fragment) {
        return new EventHelper(fragment);
    }

    public static EventHelper with(androidx.fragment.app.Fragment fragment) {
        return new EventHelper(fragment);
    }

    private Object mTarget;

    public EventHelper(Object target) {
        mTarget = target;
    }

    public <T> T get(Event event) {
        if (mTarget instanceof Activity) {
            return getActivtyEvent(event);
        } else if (mTarget instanceof Fragment ||
                mTarget instanceof androidx.fragment.app.Fragment) {
            return getFragmentEvent(event);
        } else {
            throw new IllegalStateException("wrong event");
        }
    }

    private <T> T getActivtyEvent(Event event) {
        switch (event) {
            case CREATE:
                return (T) ActivityEvent.CREATE;
            case START:
                return (T) ActivityEvent.START;
            case RESUME:
                return (T) ActivityEvent.RESUME;
            case PAUSE:
                return (T) ActivityEvent.PAUSE;
            case STOP:
                return (T) ActivityEvent.STOP;
            case DESTROY:
                return (T) ActivityEvent.DESTROY;
            default:
                return (T) ActivityEvent.DESTROY;
        }
    }

    private <T> T getFragmentEvent(Event event) {
        switch (event) {
            case CREATE:
                return (T) FragmentEvent.CREATE;
            case START:
                return (T) FragmentEvent.START;
            case RESUME:
                return (T) FragmentEvent.RESUME;
            case PAUSE:
                return (T) FragmentEvent.PAUSE;
            case STOP:
                return (T) FragmentEvent.STOP;
            case DESTROY:
                return (T) FragmentEvent.DESTROY;
            default:
                return (T) FragmentEvent.DESTROY;
        }
    }
}
