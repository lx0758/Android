package com.liux.android.tool;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.HashMap;

public class StateMachine {

    private static final Object SM_HANDLER_OBJ = new Object();
    private static final int SM_QUIT_CMD = -1;

    private HandlerThread mHandlerThread;
    private SmHandler mHandler;

    private final HashMap<State, StateInfo> mStateInfo = new HashMap<>();
    private boolean mIsStarted = false;
    private State mInitialState;
    private State mCurrentState;
    private State mDestState;

    private QuitState mQuitState = new QuitState();

    public StateMachine(String name) {
        mHandlerThread = new HandlerThread(name);
        mHandlerThread.start();
        mHandler = new SmHandler(
                mHandlerThread.getLooper()
        );
    }

    public void start() {
        mIsStarted = true;
        transitionTo(mInitialState);
    }

    public void quit() {
        sendMessage(SM_QUIT_CMD, 0 , 0, SM_HANDLER_OBJ);
    }

    public final Handler getHandler() {
        return mHandler;
    }

    public final void sendMessage(int what) {
        sendMessage(what, 0 , 0, null);
    }

    public final void sendMessage(int what, int arg1, int arg2, Object obj) {
        mHandler.sendMessage(
                mHandler.obtainMessage(what, arg1, arg2, obj)
        );
    }

    public final void sendMessageDelayed(int what, long delayMillis) {
        sendMessageDelayed(what, 0 , 0, null, delayMillis);
    }

    public final void sendMessageDelayed(int what, int arg1, int arg2, Object obj, long delayMillis) {
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(what, arg1, arg2, obj),
                delayMillis
        );
    }

    public final void removeMessages(int what) {
        mHandler.removeMessages(what);
    }

    protected final void addState(State state, State parent) {
        doAddState(state, parent);
    }

    protected final void setInitialState(State initialState) {
        mInitialState = initialState;
    }

    protected final void transitionTo(State destState) {
        if (!mIsStarted) {
            return;
        }

        mDestState = destState;
        mHandler.post(this::performTransitions);
    }

    private StateInfo doAddState(State state, State parent) {
        StateInfo parentStateInfo = null;
        if (parent != null) {
            parentStateInfo = mStateInfo.get(parent);
            if (parentStateInfo == null) {
                parentStateInfo = doAddState(parent, null);
            }
        }
        StateInfo stateInfo = mStateInfo.get(state);
        if (stateInfo == null) {
            stateInfo = new StateInfo();
            mStateInfo.put(state, stateInfo);
        }

        if ((stateInfo.parentStateInfo != null)
                && (stateInfo.parentStateInfo != parentStateInfo)) {
            throw new RuntimeException("state already added");
        }
        stateInfo.state = state;
        stateInfo.parentStateInfo = parentStateInfo;
        return stateInfo;
    }

    private void performTransitions() {
        State currentState = mCurrentState;
        State destState = mDestState;
        if (destState != null) {
            while (true) {
                if (currentState != null) {
                    currentState.exit();
                }
                destState.enter();

                if (destState != mDestState) {
                    destState = mDestState;
                } else {
                    mDestState = null;
                    break;
                }
            }
            mCurrentState = destState;
        }

        if (destState != null && destState == mQuitState) {
            cleanupAfterQuitting();
        }
    }

    private void cleanupAfterQuitting() {
        mHandler.getLooper().quit();
        mHandler = null;
        mHandlerThread = null;

        mStateInfo.clear();
        mIsStarted = false;
        mInitialState = null;
        mCurrentState = null;
        mDestState = null;
    }

    private class SmHandler extends Handler {

        private Message mMsg;

        public SmHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (!mIsStarted) {
                throw new RuntimeException("MicroStateMachine.handleMessage: "
                        + "The start method not called, received msg: " + msg);
            }

            mMsg = msg;

            if (msg.what == SM_QUIT_CMD && msg.obj == SM_HANDLER_OBJ) {
                transitionTo(mQuitState);
                return;
            }

            StateInfo curStateInfo = mStateInfo.get(mCurrentState);
            while (!curStateInfo.state.processMessage(msg)) {
                if (curStateInfo != null) {
                    curStateInfo = curStateInfo.parentStateInfo;
                }
            }
        }

        public Message getMsg() {
            return mMsg;
        }
    }

    public static class State {

        protected State() {}

        public void enter() {}

        public void exit() {}

        public boolean processMessage(Message msg) {
            return false;
        }

        public String getName() {
            String name = getClass().getName();
            int lastDollar = name.lastIndexOf('$');
            return name.substring(lastDollar + 1);
        }
    }

    private class StateInfo {
        State state;
        StateInfo parentStateInfo;
    }

    private static class QuitState extends State {
        private static final boolean NOT_HANDLED = false;
        @Override
        public boolean processMessage(Message msg) {
            return NOT_HANDLED;
        }
    }
}
