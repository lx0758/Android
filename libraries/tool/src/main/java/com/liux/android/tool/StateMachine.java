package com.liux.android.tool;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.HashMap;

/** @noinspection unused*/
public class StateMachine {

    private static final Object SM_HANDLER_OBJ = new Object();
    private static final int SM_QUIT_CMD = -1;

    private SMLogger mSMLogger;
    private HandlerThread mHandlerThread;
    private SmHandler mHandler;

    private final HashMap<State, StateInfo> mStateInfo = new HashMap<>();
    private boolean mIsStarted = false;
    private State mInitialState;
    private State mCurrentState;
    private State mDestState;

    private final QuitState mQuitState = new QuitState();

    public StateMachine(String name) {
        mHandlerThread = new HandlerThread(name);
        mHandlerThread.start();
        mHandler = new SmHandler(
                mHandlerThread.getLooper()
        );
    }

    public void setLogger(SMLogger smLogger) {
        mSMLogger = smLogger;
    }

    public void start() {
        logger("start");
        mIsStarted = true;
        transitionTo(mInitialState);
    }

    public void quit() {
        logger("quit");
        sendMessage(SM_QUIT_CMD, 0 , 0, SM_HANDLER_OBJ);
    }

    public final void sendMessage(int what) {
        logger("sendMessage, what:" + getMessageDescription(what));
        mHandler.sendMessage(
                mHandler.obtainMessage(what)
        );
    }

    public final void sendMessage(int what, int arg1, int arg2, Object obj) {
        logger("sendMessage, what:" + getMessageDescription(what) + ", arg1:" + arg1 + ", arg2:" + arg2 + ", obj:" + obj);
        mHandler.sendMessage(
                mHandler.obtainMessage(what, arg1, arg2, obj)
        );
    }

    public final void sendMessageDelayed(int what, long delayMillis) {
        logger("sendMessageDelayed, what:" + getMessageDescription(what) + ", delayMillis:" + delayMillis);
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(what),
                delayMillis
        );
    }

    public final void sendMessageDelayed(int what, int arg1, int arg2, Object obj, long delayMillis) {
        logger("sendMessage, what:" + getMessageDescription(what) + ", arg1:" + arg1 + ", arg2:" + arg2 + ", obj:" + obj + ", delayMillis:" + delayMillis);
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(what, arg1, arg2, obj),
                delayMillis
        );
    }

    public final void removeMessages(int what) {
        logger("removeMessages, what:" + getMessageDescription(what));
        mHandler.removeMessages(what);
    }

    protected final void addState(State state, State parent) {
        logger("addState, state:" + state + ", parent:" + parent);
        doAddState(state, parent);
    }

    protected final void removeState(State state) {
        logger("removeState, state:" + state);
        doRemoveState(state);
    }

    protected final void setInitialState(State initialState) {
        logger("setInitialState, initialState:" + initialState);
        mInitialState = initialState;
    }

    protected final void transitionTo(State destState) {
        logger("transitionTo, destState:" + destState);
        if (!mIsStarted) {
            return;
        }

        mDestState = destState;
        mHandler.post(this::performTransitions);
    }

    protected final Handler getHandler() {
        return mHandler;
    }

    protected final State getCurrentState() {
        return mCurrentState;
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
        stateInfo.active = false;
        return stateInfo;
    }

    private void doRemoveState(State state) {
        StateInfo stateInfo = mStateInfo.get(state);
        if (stateInfo == null || stateInfo.active) {
            return;
        }
        boolean isParent = false;
        for (StateInfo value : mStateInfo.values()) {
            if (value.parentStateInfo == stateInfo) {
                isParent = true;
                break;
            }
        }
        if (isParent) {
            return;
        }
        mStateInfo.remove(state);
    }

    private void performTransitions() {
        State currentState = mCurrentState;
        State destState = mDestState;
        if (destState != null) {
            while (true) {
                if (currentState != null) {
                    invokeExitMethods(currentState);
                }
                invokeEnterMethods(destState);

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

    private void invokeExitMethods(State state) {
        StateInfo curStateInfo = mStateInfo.get(state);
        while (curStateInfo != null) {
            logger(curStateInfo.state + "#exit");
            curStateInfo.state.exit();
            curStateInfo.active = false;
            curStateInfo = curStateInfo.parentStateInfo;
        }
    }

    private void invokeEnterMethods(State state) {
        StateInfo curStateInfo = mStateInfo.get(state);
        while (curStateInfo != null) {
            logger(curStateInfo.state + "#enter");
            curStateInfo.state.enter();
            curStateInfo.active = true;
            curStateInfo = curStateInfo.parentStateInfo;
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

    private void logger(String message) {
        if (mSMLogger != null) {
            mSMLogger.logger(message);
        }
    }

    private String getMessageDescription(int what) {
        if (mSMLogger != null) {
            return mSMLogger.getMessageDescription(what);
        }
        return String.valueOf(what);
    }

    public static class State {

        protected State() {}

        public void enter() {}

        public void exit() {}

        public boolean processMessage(@NonNull Message msg) {
            return false;
        }

        @NonNull
        @Override
        public String toString() {
            return getName();
        }

        public String getName() {
            String name = getClass().getName();
            int lastDollar = name.lastIndexOf('$');
            return name.substring(lastDollar + 1);
        }
    }

    public interface SMLogger {
        void logger(@NonNull String message);

        String getMessageDescription(int what);
    }

    private class SmHandler extends Handler {

        private Message mMsg;

        public SmHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
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
            while (curStateInfo != null) {
                logger(curStateInfo.state + "#processMessage, msg:" + getMessageDescription(msg.what));
                if (curStateInfo.state.processMessage(msg)) {
                    logger(curStateInfo.state + "#processMessage, msg:" + getMessageDescription(msg.what) + ", result:true");
                    return;
                }
                logger(curStateInfo.state + "#processMessage, msg:" + getMessageDescription(msg.what) + ", result:false");
                curStateInfo = curStateInfo.parentStateInfo;
            }

            logger("unhandledMessage: msg:" + getMessageDescription(msg.what));
        }

        public Message getMsg() {
            return mMsg;
        }
    }

    private static class StateInfo {
        State state;
        StateInfo parentStateInfo;
        boolean active = false;
    }

    private static class QuitState extends State {
        private static final boolean NOT_HANDLED = false;
        @Override
        public boolean processMessage(@NonNull Message msg) {
            return NOT_HANDLED;
        }
    }
}
