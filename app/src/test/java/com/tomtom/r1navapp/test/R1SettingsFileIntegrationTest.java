package com.tomtom.r1navapp.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.google.common.base.Joiner;
import com.tomtom.navui.appkit.AppContext;
import com.tomtom.navui.setting.Setting;
import com.tomtom.navui.setting.SettingGroup;
import com.tomtom.navui.setting.SettingInflater;
import com.tomtom.navui.setting.SettingProvider;
import com.tomtom.navui.setting.SettingScreen;
import com.tomtom.navui.systemport.SystemContext;
import com.tomtom.navui.systemport.SystemSettings;
import com.tomtom.navui.viewkit.ViewContext;
import com.tomtom.r1navapp.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class R1SettingsFileIntegrationTest {

    public static final String IN_PRODUCT_SUFFIX = "_IN_PRODUCT";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private AppContext mMockAppContext;
    @Mock
    private ViewContext mMockViewContext;
    @Mock
    private SystemContext mMockSystemContext;
    @Mock
    private SystemSettings mMockSystemSettings;
    @Mock
    private SettingProvider mMockSettingProvider;
    @Mock
    private Comparator<String> mMockVersionComparator;

    @Before
    public void mockAppContextForSettingInflater() {
        when(mMockAppContext.getSettingProvider()).thenReturn(mMockSettingProvider);
        when(mMockVersionComparator.compare(anyString(), anyString())).thenReturn(-1);
        when(mMockSettingProvider.getVersionComparator()).thenReturn(mMockVersionComparator);
        when(mMockAppContext.getSystemPort()).thenReturn(mMockSystemContext);
        when(mMockSystemContext.getSettings(anyString())).thenReturn(mMockSystemSettings);
    }

    @Test
    public void shouldBeInflatable() {
        //GIVEN
        final Context context = InstrumentationRegistry.getTargetContext();
        final SettingInflater settingInflater = new SettingInflater(mMockAppContext, context);
        //WHEN
        final SettingScreen settingScreen = settingInflater.inflate(R.xml.r1_settings);
        //THEN
        assertNotNull("Inflating R1 settings failed", settingScreen);
    }

    @Test
    @Ignore
    public void shouldMatchLastIntegratedVersion() throws IOException {
        //GIVEN
        final Context context = InstrumentationRegistry.getTargetContext();
        final SettingInflater settingInflater = new SettingInflater(mMockAppContext, context);

        //WHEN
        final SettingScreen navUiSettingScreen = settingInflater.inflate(R.xml.default_settings);
        final String navUiVersion = navUiSettingScreen.getVersion();

        final SettingScreen r1SettingScreen = settingInflater.inflate(R.xml.r1_settings);
        final String r1Version = r1SettingScreen.getVersion();

        //THEN
        final boolean condition = r1Version.equals(navUiVersion);
        assertTrue("The navui settings version was upgraded to " + navUiVersion +
                " but the last integrated version was " + r1Version +
                ". Please integrate default_settings.xml to r1_settings.xml", condition);
    }

    @Test
    public void shouldHaveAllNavUiFeatures() {
        //GIVEN
        final Context context = InstrumentationRegistry.getTargetContext();
        final SettingInflater settingInflater = new SettingInflater(mMockAppContext, context);
        final SettingScreen navuiSettings = settingInflater.inflate(R.xml.default_settings);
        final SettingScreen r1Settings = settingInflater.inflate(R.xml.r1_settings);

        //WHEN
        final Set<String> navuiFeatures = new HashSet<>();
        final Set<String> r1Features = new HashSet<>();
        findFeatures(navuiFeatures, navuiSettings);
        findFeatures(r1Features, r1Settings);

        //THEN
        final List<String> missingFeatures = new ArrayList<>();
        for (String feature : navuiFeatures) {
            if (!(r1Features.contains(feature) || r1Features.contains(feature + IN_PRODUCT_SUFFIX))) {
                missingFeatures.add(feature);
            }
        }
        if (!missingFeatures.isEmpty()) {
            fail("r1_settings.xml is missing a default value for " + Joiner.on(", ").join(missingFeatures));
        }

    }

    private static void findFeatures(Set<String> features, Setting setting) {
        if (setting == null) {
            return;
        }

        if (setting instanceof SettingGroup) {
            final SettingGroup group = (SettingGroup) setting;
            final int settingCount = group.getSettingCount();
            for (int i = 0; i < settingCount; i++) {
                findFeatures(features, group.getSetting(i));
            }
        } else {
            final String settingKey = setting.getKey();
            if (!TextUtils.isEmpty(settingKey) && settingKey.contains("navui.setting.feature")) {
                features.add(settingKey);
            }
        }
    }
}
