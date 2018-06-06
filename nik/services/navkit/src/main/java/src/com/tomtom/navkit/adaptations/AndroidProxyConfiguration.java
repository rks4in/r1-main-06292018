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
import android.util.Log;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

public class AndroidProxyConfiguration {
  static private final String TAG = AndroidProxyConfiguration.class.getSimpleName();
  private Context context = null;

  public AndroidProxyConfiguration(Context context) {
    this.context = context;
  }

  // ======================================================================
  // C-to-Java callbacks
  // ======================================================================
  public String getProxyConfigurationForUrl(String aUrl) {
    String proxyUrl = null;
    try {
      List<Proxy> proxyList = ProxySelector.getDefault().select(new URI(aUrl));
      if (proxyList.isEmpty()) {
        return null;
      }

      Proxy proxy = proxyList.get(0);
      if ( (proxy.type() == Proxy.Type.HTTP) && (proxy.address() instanceof InetSocketAddress) ) {

        InetSocketAddress addr = (InetSocketAddress)proxy.address();

        if(addr != null) {
          if (addr.isUnresolved()) {
            proxyUrl = "http://" + addr.getHostName() + ":" + addr.getPort();
          }
          else if (addr.getAddress() != null) {
            proxyUrl = "http://" + addr.getAddress().getHostAddress() + ":" + addr.getPort();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return proxyUrl;
  }
}
