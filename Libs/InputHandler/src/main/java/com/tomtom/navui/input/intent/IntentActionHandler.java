package com.tomtom.navui.input.intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Handler;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.action.Action;
import com.tomtom.navui.util.ActionHandler;
import com.tomtom.navui.input.errhandler.InputDataErrorDialogHandler;
import com.tomtom.navui.input.errhandler.InputDataErrorHandler;
import com.tomtom.navui.input.parser.InputParser;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.intent.ContactsLocationParser;
import com.tomtom.navui.input.parser.intent.LauncherShortcutParser;
import com.tomtom.navui.input.parser.intent.GeoLocationParser;
import com.tomtom.navui.input.parser.intent.GoogleMapLocationParser;
import com.tomtom.navui.input.parser.intent.NavigationParser;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.taskkit.TaskContext.ContextStateListener;
import com.tomtom.navui.taskkit.TaskContext.MapState;
import com.tomtom.navui.util.Log;

/**
 * Implementation of {@link ActionHandler} interface. Implementation provide support for external
 * intent events.
 *
 */
public class IntentActionHandler implements ActionHandler<Intent>, ContextStateListener {

    private static final String KEY_ALREADY_HANDLED = "alreadyHandled";

    private static final String TAG = "IntentActionHandler";
    private final AppContext mAppContext;
    private final List<InputParser<Intent>> mIntentParsers = new ArrayList<InputParser<Intent>>();
    private final Map<InputParser<Intent>, InputDataErrorHandler> mErrorHandlersMap = new HashMap<InputParser<Intent>, InputDataErrorHandler>();
    private Intent mInput;
    private InputDataErrorHandler mDefaultInputDataErrorHandler;

    public IntentActionHandler(final AppContext appKit) {
        mAppContext = appKit;
        mDefaultInputDataErrorHandler = new InputDataErrorDialogHandler(mAppContext);

        // pass null so mDefaultInputDataErrorHandler will be used in case of error
        addInputParser(new GeoLocationParser(), null);
        addInputParser(new GoogleMapLocationParser(), null);
        addInputParser(new NavigationParser(), null);
        addInputParser(new ContactsLocationParser(appKit), null);
        addInputParser(new LauncherShortcutParser(), null);
    }

    public void addInputParser(final InputParser<Intent> inputParser, final InputDataErrorHandler errorHandler) {
        mIntentParsers.add(inputParser);
        if (errorHandler != null) {
            mErrorHandlersMap.put(inputParser, errorHandler);
        }
    }

    /**
     * Set new instance of an object responsible for default handling error situations during parsing
     * intents
     *
     * @param handler {@link InputDataErrorHandler}
     */
    public void setDefaultErrorHandler(final InputDataErrorHandler handler) {
        mDefaultInputDataErrorHandler = handler;
    }

    @Override
    public void handle(final Intent input) {
        if (isLaunchIntent(input)) {
            if (Log.ENTRY) {
                Log.entry(TAG, "Intent is a launcher intent " + input);
            }
            return;
        }
        mInput = input;

        final TaskContext taskKit = mAppContext.getTaskKit();
        if (Log.D) {
            Log.d(TAG, "taskKit.isReady() " + taskKit.isReady());
        }
        if (taskKit.isReady()) {
            handleIntent(input);
        } else {
            taskKit.removeContextStateListener(this);
            taskKit.addContextStateListener(this);
        }

    }

    protected void handleIntent(final Intent input) {
        Exception exceptionToHandle = null;
        InputDataErrorHandler dedicatedErrorHandler = null;
        try {
            if (Log.D) {
                Log.d(TAG, "intent.getDataString() " + input.getDataString());
            }
            //protection against device rotation lifecycle
            if (!isHandled(input)) {
                setHandled(input);
            } else {
                return; //Intent already handled
            }

            ParseResult parseResult = null;
            for (final InputParser<Intent> it : mIntentParsers) {
                if (it.accept(input)) {
                    dedicatedErrorHandler = mErrorHandlersMap.get(it);
                    parseResult = it.parse(input);
                    break;
                }
            }

            if (parseResult == null) {
                if (Log.D) {
                    Log.d(TAG, "Could not find parser to parse data input " + input);
                }
                return;
            }

            final Action action = mAppContext.newAction(parseResult.getAction());
            action.addParameter(parseResult.getParsedData());
            action.addParameter(dedicatedErrorHandler == null ? mDefaultInputDataErrorHandler : dedicatedErrorHandler);

            if (parseResult.isAsynchronous()) {
                new Handler().post(new DispatchActionRunnable(action));
            } else {
                action.dispatchAction();
            }

        } catch (final IllegalArgumentException illegalArgEx) {
            exceptionToHandle = illegalArgEx;
        } catch (final ParseException parseEx) {
            exceptionToHandle = parseEx;
        } catch (final ParseFailureException parseFailEx) {
            exceptionToHandle = parseFailEx;
        } finally {
            if (exceptionToHandle != null) {
                if (dedicatedErrorHandler == null) {
                    mDefaultInputDataErrorHandler.handle(exceptionToHandle);
                } else {
                    dedicatedErrorHandler.handle(exceptionToHandle);
                }
            }
        }
    }

    private boolean isHandled(final Intent input) {
        return input.getBooleanExtra(KEY_ALREADY_HANDLED, false);
    }

    private void setHandled(final Intent input) {
        input.putExtra(KEY_ALREADY_HANDLED, true);
    }

    /**
     * Checks if the intent is a regular launch intent such as one fired when the user presses
     * shortcut icon from home screen. In this case, the intent should not be parsed.
     *
     * When it is not a launch intent, the intent was fired from a navigation app, browser, contact
     * or something similar and should be parsed to find a location to navigate to.
     *
     * @return True iff the intent is a regular launch intent and should not be parsed
     */
    boolean isLaunchIntent(final Intent intent) {
        if (Log.D) {
            Log.d(TAG, "isLaunchIntent " + intent);
        }

        if (intent == null) {
            return true; // Ignore intent
        } else {
            final String intentAction = intent.getAction();
            return (intentAction == null || Intent.ACTION_MAIN.equals(intentAction));
        }
    }

    @Override
    public void onTaskContextReady() {
        if (Log.D) {
            Log.d(TAG, "onTaskContextReady ");
        }
        handleIntent(mInput);
    }

    @Override
    public void onTaskContextMapStateChange(final MapState mapState) {
        if (Log.D) {
            Log.d(TAG, "onTaskContextMapStateChange ");
        }
    }

    @Override
    public void onTaskContextLost(final Boolean recoverable, final ErrorCode error) {
        if (Log.D) {
            Log.d(TAG, "onTaskContextLost recoverable? " + recoverable + " Error code " + error);
        }
    }

    private static class DispatchActionRunnable implements Runnable {

        private final Action mAction;

        public DispatchActionRunnable(final Action action) {
            mAction = action;
        }

        @Override
        public void run() {
            mAction.dispatchAction();
        }
    }

}
