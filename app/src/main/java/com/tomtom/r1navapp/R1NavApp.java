package com.tomtom.r1navapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.appkit.ExtAppScreenContext;
import com.tomtom.navui.appkit.ThemeDetails;
import com.tomtom.navui.controlport.ControlContext;
import com.tomtom.navui.focusuikit.FocusUiContext;
import com.tomtom.navui.promptkit.PromptContext;
import com.tomtom.navui.promptport.AudioEngineContext;
import com.tomtom.navui.promptport.audioplayer.AudioPlayerEngineFactory;
import com.tomtom.navui.rendererkit.RendererContext;
import com.tomtom.navui.sigappkit.SigAppContext;
import com.tomtom.navui.sigappkit.util.time.TimeFormattingUtilWrapperImpl;
import com.tomtom.navui.sigfocusuikit.SigFocusUiContext;
import com.tomtom.navui.sigmapappkit.SigMapAppContext;
import com.tomtom.navui.sigmapviewkit.SigMapViewContext;
import com.tomtom.navui.sigpromptkit.SigPromptContext;
import com.tomtom.navui.sigrendererkit3.SigRendererContext3;
import com.tomtom.navui.sigtaskkit.SigTaskContext;
import com.tomtom.navui.sigviewkit.SigViewContext;
import com.tomtom.navui.stockaudio.StockAudioEngineContext;
import com.tomtom.navui.stockaudio.spp.SoundPromptPlayerFactory;
import com.tomtom.navui.stockcontrolport.StockControlContext;
import com.tomtom.navui.stockplatformtexttospeech.StockPlatformTextToSpeechEngineFactory;
import com.tomtom.navui.stocksystemport.StockApplication;
import com.tomtom.navui.stocksystemport.StockDefaultErrorReporter;
import com.tomtom.navui.stocksystemport.StockSystemContext;
import com.tomtom.navui.systemport.ErrorReporter;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.SystemMapConfigurationManager;
import com.tomtom.navui.systemport.SystemMapConfigurationManager.SystemMapColorScheme;
import com.tomtom.navui.systemport.SystemMapConfigurationManager.SystemMapColorScheme.ColorSchemeType;
import com.tomtom.navui.systemport.SystemSettings;
import com.tomtom.navui.systemport.SystemSettingsConstants;
import com.tomtom.navui.taskkit.TaskContext;
import com.tomtom.navui.promptport.texttospeech.TextToSpeechEngineFactory;
import com.tomtom.navui.util.AccentColorUtils;
import com.tomtom.navui.util.Log;
import com.tomtom.navui.viewkit.ExtViewContext;
import com.tomtom.navui.viewkit.ViewContext;

import java.util.LinkedHashMap;

/**
 * R1 navigation application class
 */
public class R1NavApp extends StockApplication {
    /** Logging tag for debugging purposes */
    private static final String TAG = "R1NavApp";

    @Override
    public void onCreate() {
        super.onCreate();
        getErrorReporter().setEnabled();
    }

    @NonNull
    @Override
    protected ErrorReporter createErrorReporter() {
        return new StockDefaultErrorReporter();
    }

    @Override
    public AppContext createAppKit() {
        if (Log.ENTRY) Log.entry(TAG, "createAppKit()");

        final Context applicationContext = getApplicationContext();

        final ControlContext controls = new StockControlContext();

        // configure which view kit
        final ViewContext viewKit = new SigViewContext(controls);
        final SigMapViewContext mapViewKit = new SigMapViewContext(viewKit);
        viewKit.addExt(ExtViewContext.class, mapViewKit);

        // configure which system port
        final Intent intent = new Intent(this, R1MainActivity.class);
        final PendingIntent pendingLaunchIntent = PendingIntent.getActivity(this, 0, intent, 0);
        final StockSystemContext systemPort = new StockSystemContext(applicationContext,
                R1NavKitLifecycleManagementService.class, pendingLaunchIntent, controls);
        setupMapColorSchemes(systemPort);

        // configure which task kit
        final TaskContext taskKit = new SigTaskContext(systemPort);

        // configure which audio port
        final AudioPlayerEngineFactory audioPlayerEngineFactory = new SoundPromptPlayerFactory(applicationContext);
        final TextToSpeechEngineFactory ttsEngineFactory = new StockPlatformTextToSpeechEngineFactory(applicationContext);
        final AudioEngineContext audioPort = new StockAudioEngineContext(applicationContext, audioPlayerEngineFactory, ttsEngineFactory);

        // configure which prompt kit
        final PromptContext promptKit = new SigPromptContext(applicationContext, audioPort, systemPort);

        // configure which app kit
        final SigAppContext appKit = new SigAppContext(viewKit, taskKit, promptKit, systemPort, null, null, null, this);
        final SigMapAppContext mapAppKit = new SigMapAppContext();
        appKit.addExt(ExtAppScreenContext.class, mapAppKit);

        // configure RendererKit.
        final RendererContext rendererContext = new SigRendererContext3(this, systemPort.getRendererContextSystemAdaptation());
        appKit.addKit(rendererContext, RendererContext.NAME);

        configureFocusUiContext(appKit);

        setupThemes(appKit);

        setDefaultTheme(systemPort);

        TimeFormattingUtilWrapperImpl.init();

        return appKit;
    }

    @Override
    public int getSettingResourceId() {
        return R.xml.default_settings;
    }

    @Override
    public int getThemeResourceId() {
        return R.style.navui_SignatureTheme;
    }

    @Override
    public String getMainMenuResourceReference() {
        // Return a reference to the external test menu. If this cannot be
        // found, then the default main menu is used.
        return "com.tomtom.navui.appkit.externalmenu.test:xml/main_menu";
    }

    @Override
    public String getShortcutMenuResourceReference() {
        return "com.tomtom.navui.appkit.externalmenu.test:xml/shortcut_menu";
    }

    @Override
    public int getProductTheme() {
        // Return a resource ID for the product particulars style
        return com.tomtom.r1navapp.R.style.navui_SignatureProductTheme;
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    private void configureFocusUiContext(final AppContext appContext) {
        final SystemSettings settings = appContext.getSystemPort().getSettings(SystemContext.NAVUI_SETTINGS);
        final boolean focusUiKitEnabled = settings.getBoolean(SystemSettingsConstants.NAVUI_FEATURES_FOCUS_UI_ENABLED, false);
        if (focusUiKitEnabled) {
            final FocusUiContext focusUi = SigFocusUiContext.create(getApplicationContext(),
                    getResources().getString(R.string.keycode_mapping_configuration),
                    null);
            appContext.addKit(focusUi, FocusUiContext.NAME);
        }
    }

    /**
     * Set a default UI theme, since MapSDK does not support custom themes
     *
     * @param systemPort Reference to the system port.
     */
    private void setDefaultTheme(@NonNull final StockSystemContext systemPort) {
        final SystemSettings settings = systemPort.getSettings(SystemContext.NAVUI_SETTINGS);
        settings.putString(SystemSettingsConstants.NAVUI_UI_THEME_ID, "");
        AccentColorUtils.reset();
    }

    /**
     * Register all of the map color schemes this product supports.
     *
     * @param systemPort Reference to the system port.
     */
    private void setupMapColorSchemes(final StockSystemContext systemPort) {
        final SystemMapConfigurationManager mapConfigurationManager = systemPort.getSystemMapConfigurationManager();

        // Create and register NDS map color schemes
        final SystemMapColorScheme colorSchemeNDSNormal = retrieveMapColorScheme(systemPort,
                R.style.navui_MapColorScheme_NDS_Normal,
                R.drawable.navui_ic_menu_colorschemenatural_special,
                R.string.navui_map_color_normal,
                true);
        mapConfigurationManager.registerMapColorScheme(ColorSchemeType.NDS, colorSchemeNDSNormal);

        // Create and register NDS DTM map color schemes
        final SystemMapColorScheme colorSchemeNDSTerrainNormal = retrieveMapColorScheme(systemPort,
                R.style.navui_MapColorScheme_NDS_Terrain_Normal,
                R.drawable.navui_ic_menu_colorschemenatural_special,
                R.string.navui_map_color_normal,
                true);
        mapConfigurationManager.registerMapColorScheme(ColorSchemeType.NDS_TERRAIN, colorSchemeNDSTerrainNormal);
    }

    private SystemMapColorScheme retrieveMapColorScheme(final StockSystemContext systemPort, final int themeStyle,
                                                        final int themeIconResId, final int themeNameResId,
                                                        final boolean isDefaultTheme) {
        final Context context = getApplicationContext();
        final TypedArray typedArray =
                context.obtainStyledAttributes(null, R.styleable.navui_MapColorScheme, themeStyle, themeStyle);
        final String themeId = typedArray.getString(R.styleable.navui_MapColorScheme_navui_mapColorSchemeId);
        final ThemeDetails themeDetails = new ThemeDetails(themeId, 0, themeIconResId, themeNameResId, isDefaultTheme);

        final String twoDDay = "browsing_day";
        final String threeDDay = "driving_day";
        final String twoDNight = "browsing_night";
        final String threeDNight = "driving_night";

        typedArray.recycle();

        return systemPort.getSystemMapConfigurationManager().
                createNewColorScheme(themeDetails, twoDDay, threeDDay, twoDNight, threeDNight);
    }

    private void setupThemes(final SigAppContext appKit) {
        // LinkedHashMap so that themes can be retrieved in a fixed order which is used to display
        // a list of themes in the UI
        final LinkedHashMap<String, ThemeDetails> supportedThemes = new LinkedHashMap<>();

        // Party Pink Theme (In the code referenced as: Vibrant Violet)
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_vibrant_violet),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_vibrant_violet),
                        R.style.navui_SignatureThemeVibrantViolet,
                        R.drawable.navui_ic_menu_changetheme_vibrantviolet_base,
                        R.string.navui_theme_vibrant_violet,
                        false));

        // Proud Purple Theme (In the code referenced as: Pride Plum)
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_pride_plum),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_pride_plum),
                        R.style.navui_SignatureThemePridePlum,
                        R.drawable.navui_ic_menu_changetheme_prideplum_base,
                        R.string.navui_theme_pride_plum,
                        false,
                        true));

        // Rhythmic Purple Theme (In the code referenced as: Outstanding Orchid)
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_outstanding_orchid),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_outstanding_orchid),
                        R.style.navui_SignatureThemeOutstandingOrchid,
                        R.drawable.navui_ic_menu_changetheme_outstandingorchid_base,
                        R.string.navui_theme_outstanding_orchid,
                        false));

        // Cosmic Blue Theme (In the code referenced as: Stylish Steel Blue)
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_stylish_steel_blue),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_stylish_steel_blue),
                        R.style.navui_SignatureThemeStylishSteelBlue,
                        R.drawable.navui_ic_menu_changetheme_stylishsteelblue_base,
                        R.string.navui_theme_stylish_steel_blue,
                        false));

        // Summer Blue Theme (In the code referenced as: Bright Blue)
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_bright_blue),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_bright_blue),
                        R.style.navui_SignatureTheme,
                        R.drawable.navui_ic_menu_changetheme_brightblue_base,
                        R.string.navui_theme_bright_blue,
                        true));

        // Gushing Green Theme (In the code referenced as: Soothing Sea Green)
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_soothing_sea_green),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_soothing_sea_green),
                        R.style.navui_SignatureThemeSoothingSeaGreen,
                        R.drawable.navui_ic_menu_changetheme_soothingseagreen_base,
                        R.string.navui_theme_soothing_sea_green,
                        false,
                        true));

        // Serene Green Theme (In the code referenced as: Silky Spring Green)
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_silky_spring_green),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_silky_spring_green),
                        R.style.navui_SignatureThemeSilkySpringGreen,
                        R.drawable.navui_ic_menu_changetheme_silkyspringgreen_base,
                        R.string.navui_theme_silky_spring_green,
                        false));

        // Lucky Lime Theme
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_lucky_lime),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_lucky_lime),
                        R.style.navui_SignatureThemeLuckyLime,
                        R.drawable.navui_ic_menu_changetheme_luckylime_base,
                        R.string.navui_theme_lucky_lime,
                        false));

        // Glorious Yellow Theme
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_glorious_yellow),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_glorious_yellow),
                        R.style.navui_SignatureThemeGloriousYellow,
                        R.drawable.navui_ic_menu_changetheme_gloriousyellow_base,
                        R.string.navui_theme_glorious_yellow,
                        false));

        // Greedy Gold Theme
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_greedy_gold),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_greedy_gold),
                        R.style.navui_SignatureThemeGreedyGold,
                        R.drawable.navui_ic_menu_changetheme_greedygold_base,
                        R.string.navui_theme_greedy_gold,
                        false,
                        true));

        // Outrageous Orange Theme
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_outrageous_orange),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_outrageous_orange),
                        R.style.navui_SignatureThemeOutrageousOrange,
                        R.drawable.navui_ic_menu_changetheme_outrageousorange_base,
                        R.string.navui_theme_outrageous_orange,
                        false,
                        true));

        // Ruby Red Theme
        supportedThemes.put(getThemeKeyFromTheme(R.string.navui_theme_name_ruby_red),
                new ThemeDetails(getThemeKeyFromTheme(R.string.navui_theme_name_ruby_red),
                        R.style.navui_SignatureThemeRubyRed,
                        R.drawable.navui_ic_menu_changetheme_rubyred_base,
                        R.string.navui_theme_ruby_red,
                        false,
                        true));

        appKit.registerSupportedThemes(supportedThemes);
    }

    private String getThemeKeyFromTheme(final int themeKeyResId) {
        return getResources().getString(themeKeyResId);
    }
}
