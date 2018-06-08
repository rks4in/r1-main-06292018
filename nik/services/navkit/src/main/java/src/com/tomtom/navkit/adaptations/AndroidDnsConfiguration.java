/*
 * Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 * 
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.navkit.adaptations;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Collects DNS host addresses with validation
 */
class DNSHosts {
  private List<String> hosts = new ArrayList<String>();

  /**
   * Get array of DNS host addresses
   * @return String array of DNS host addresses
   */
  public String[] get() {
    return hosts.toArray(new String[0]);
  }

  /**
   * Add new DNS host address if it is unique and a valid IPv4 or IPv6 address
   *
   * @param host The DNS host address
   */
  public void Add(String host) {
    final Pattern reIPv4 = Pattern.compile("^\\d+(\\.\\d+){3}$");
    final Pattern reIPv6 = Pattern.compile("^[0-9a-f]+(:[0-9a-f]*)+:[0-9a-f]+$");

    if (host != null && !host.isEmpty() && !hosts.contains(host)
        && (reIPv4.matcher(host).matches() || reIPv6.matcher(host).matches())) {
      hosts.add(host);
    }
  }
}

/**
 * Retrieves DNS hosts configuration
 */
public class AndroidDnsConfiguration {
  static private final String TAG = AndroidDnsConfiguration.class.getSimpleName();
  private Context context = null;

  public AndroidDnsConfiguration(Context context) {
    this.context = context;
  }

  // ======================================================================
  // C-to-Java callbacks
  // ======================================================================
  /**
   * Retrieves DNS host addresses
   * @return String array of unique and valid IPv4/IPv6 addresses
   */
  public String[] getDnsAddresses() {
    // The Android O deprecates system properties "net.dns1"..."net.dns4".
    // See https://developer.android.com/about/versions/oreo/android-8.0-changes.html#o-pri

    // Build version for Android O is not defined on pre-O versions.
    final int BUILD_VERSION_ANDROID_O = 26;
    if (Build.VERSION.SDK_INT < BUILD_VERSION_ANDROID_O) {
      Log.d(TAG, "Retrieving DNS servers from SystemProperties");
      return getDnsServersFromProps();
    } else {
      Log.d(TAG, "Retrieving DNS servers from ConnectivityManager");
      return getDnsServersFromConnectivityManager();
    }
  }

  /**
   * Retrieves DNS host addresses from System Properties.
   *
   * Method works on pre-O (API26) versions.
   *
   * @return String array of unique and valid IPv4/IPv6 addresses
   */
  private String[] getDnsServersFromProps() {
    DNSHosts hosts = new DNSHosts();

    /**
     * The API may not be available on Android O and later,
     * so it is implemented using Reflection API.
     */
    try {
      Class SystemProperties = Class.forName("android.os.SystemProperties");
      Method method = SystemProperties.getMethod("get", new Class[]{String.class});
      final String[] netdns = new String[]{"net.dns1", "net.dns2", "net.dns3", "net.dns4"};
      for (String prop: netdns) {
        Object[] args = new Object[]{prop};
        String host = (String) method.invoke(null, prop);
        hosts.Add(host);
      }
    } catch (Exception ex) {
      Log.e(TAG, "Failed to retrieve DNS servers from SystemProperties", ex);
    }

    return hosts.get();
  }

  /**
   * Retrieves DNS hosts addresses from ConnectivityManager.
   *
   * Method works on Android O (API26) and later
   *
   * @return String array of unique and valid IPv4/IPv6 addresses
   */
  private String[] getDnsServersFromConnectivityManager() {
    DNSHosts hosts = new DNSHosts();

    /**
     * The API may not be available before Android O,
     * so it is implemented using Reflection API.
     */
    try {
      Class CM = ConnectivityManager.class;
      Class Network = Class.forName("android.net.Network");
      Class NetworkInfo = Class.forName("android.net.NetworkInfo");
      Class LinkProperties = Class.forName("android.net.LinkProperties");
      Class InetAddress = Class.forName("java.net.InetAddress");

      Method getAllNetworks = CM.getMethod("getAllNetworks");
      Method getNetworkInfo = CM.getMethod("getNetworkInfo", new Class[]{Network});
      Method getLinkProperties = CM.getMethod("getLinkProperties", new Class[]{Network});
      Method isConnected  = NetworkInfo.getMethod("isConnected");
      Method getDnsServers = LinkProperties.getMethod("getDnsServers");
      Method getHostAddress = InetAddress.getMethod("getHostAddress");

      ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(this.context.CONNECTIVITY_SERVICE);

      Object[] networks = (Object[]) getAllNetworks.invoke(connectivityManager);
      for (Object network: networks) {
        Object networkInfo = getNetworkInfo.invoke(connectivityManager, network);
        if ((Boolean) isConnected.invoke(networkInfo)) {
          Object linkProperties = getLinkProperties.invoke(connectivityManager, network);
          List<Object> dnsServers = (List<Object>) getDnsServers.invoke(linkProperties);
          for (Object ia: dnsServers) {
            String host = (String) getHostAddress.invoke(ia);
            hosts.Add(host);
          }
        }
      }
    } catch (Exception ex) {
      Log.e(TAG, "Failed to retrieve DNS servers from ConnectivityManager", ex);
    }

    return hosts.get();
  }
}
