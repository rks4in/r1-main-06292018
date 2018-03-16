package com.tomtom.navui.input.parser.intent;

import android.content.Intent;

import com.tomtom.navui.input.parser.InputParser;
import com.tomtom.navui.input.parser.ParseException;
import com.tomtom.navui.input.parser.ParseFailureException;
import com.tomtom.navui.input.parser.data.ParseResult;
import com.tomtom.navui.input.parser.data.location.DataParseResult;
import com.tomtom.navui.taskkit.location.LocationStorageFolders;
import com.tomtom.navui.util.LauncherShortcutConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses intents that were fired from Android's launcher shortcuts.
 */
public class LauncherShortcutParser implements InputParser<Intent> {

    private static final Map<String, String> mLauncherShortcuts = new HashMap<String, String>();

    static {
        mLauncherShortcuts.put(LauncherShortcutConstants.ACTION_SEARCH,
                "action://LaunchScreen/SearchScreen?forwardsTo=action://LaunchScreen/HomeScreen%3Fhistory=clear&amp;navui-search-screen-store-changed-search-mode=true");

        mLauncherShortcuts.put(LauncherShortcutConstants.ACTION_MY_PLACES,
                "action://LaunchScreen/MyPlacesScreen");

        mLauncherShortcuts.put(LauncherShortcutConstants.ACTION_DRIVE_TO_WORK,
                "action://StartNavigateSpecialLocationNoPreview?specialLocation=" + LocationStorageFolders.LOCATION_TYPE_WORK);

        mLauncherShortcuts.put(LauncherShortcutConstants.ACTION_DRIVE_HOME,
                "action://StartNavigateSpecialLocationNoPreview?specialLocation=" + LocationStorageFolders.LOCATION_TYPE_HOME);
    }

    @Override
    public boolean accept(final Intent input) {
        return mLauncherShortcuts.containsKey(input.getAction());
    }

    @Override
    public ParseResult parse(final Intent input) throws ParseException, ParseFailureException {
        return new DataParseResult()
                .setAction(mLauncherShortcuts.get(input.getAction()))
                .setAsynchronous(true);
    }

}
