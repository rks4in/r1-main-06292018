LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_STATIC_LIBRARIES := adaptation-base abstraction assert backtrace boost-thread boost-filesystem boost-system

LOCAL_MODULE    := NavKitJniBind
LOCAL_C_INCLUDES := $(LOCAL_PATH)/adaptations/src/ConfidentialValueStore/cpp \
	$(LOCAL_PATH)/adaptations/src/DefaultSettings \
	$(LOCAL_PATH)/adaptations/src/DeviceInformation/cpp \
	$(LOCAL_PATH)/adaptations/src/DnsConfiguration/cpp \
	$(LOCAL_PATH)/adaptations/src/InternetConnectionStateNotifier/cpp \
	$(LOCAL_PATH)/adaptations/src/JniUtils/cpp \
	$(LOCAL_PATH)/adaptations/src/Properties/cpp \
	$(LOCAL_PATH)/adaptations/src/ProxyConfiguration/cpp \
	$(LOCAL_PATH)/adaptations/src/RDSTMCService/cpp \
	$(LOCAL_PATH)/adaptations/src/RemovableMediaStateNotifier/cpp \
	$(LOCAL_PATH)/adaptations/src/TimeZoneChangedObserver/cpp \
	$(LOCAL_PATH)/adaptations/src/FeatureConfiguration/cpp \
	$(LOCAL_PATH)/adaptations/src/OnlineRoutingConfiguration/cpp \
	$(LOCAL_PATH)/adaptations/src/OnlineSearchConfiguration/cpp \
	$(LOCAL_PATH)/adaptations/src/ResourceCategoryLayout/cpp \
	$(LOCAL_PATH)/adaptations/src/NavCloudConfiguration/cpp \
	$(LOCAL_PATH)/processhost \
	$(LOCAL_PATH)/shared/adaptations/DeviceInformation/Interface \
	$(LOCAL_PATH)/shared/adaptations/MapSecurity/Interface \
	$(LOCAL_PATH)/shared/adaptations/RDSTMCService \
	$(LOCAL_PATH)/shared/adaptations/RDSTMCService \
	$(LOCAL_PATH)/shared/adaptations/MapSecurity/Interface \
	$(LOCAL_PATH)/shared/utils/Utils/Interface \
	$(LOCAL_PATH)/utils/Interface \
	$(LOCAL_PATH)/../../../build/native/all/include/Framework.ThirdParty.Boost \
	$(LOCAL_PATH)/../../../build/native/all/include/Framework.Porting.OsAbstractionLayer \
	$(LOCAL_PATH)/../../../build/native/all/include/Framework.Porting.Assert \
	$(LOCAL_PATH)/../../../build/native/all/include/Framework.Porting.Backtrace \
	$(LOCAL_PATH)/../../../build/native/all/include/Framework.Porting.Adaptations \
	$(LOCAL_PATH)/../../../build/native/all/include/NavKit.FOR.NDS

ifeq ($(APP_OPTIM),debug)
    LOCAL_STATIC_LIBRARIES += clock
    LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../../build/native/debug/include/Framework.Porting.Clock
endif

LOCAL_CPPFLAGS += -frtti -fexceptions -DLINUX_TARGET
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_CFLAGS += -std=gnu++11

LOCAL_SRC_FILES := adaptations/src/ConfidentialValueStore/cpp/AndroidSecureKeyValueStore.cpp \
  adaptations/src/DeviceInformation/cpp/AndroidPropertiesDeviceInformation.cpp \
  adaptations/src/DnsConfiguration/cpp/AndroidDnsConfiguration.cpp \
  adaptations/src/InternetConnectionStateNotifier/cpp/InternetConnectionStateNotifierByAndroidIntent.cpp \
  adaptations/src/JniUtils/cpp/AndroidJniUtil.cpp \
  adaptations/src/Properties/cpp/AndroidProperties.cpp \
  adaptations/src/ProxyConfiguration/cpp/AndroidProxyConfiguration.cpp \
  adaptations/src/RDSTMCService/cpp/RDSTMCServiceAndroid.cpp \
  adaptations/src/RemovableMediaStateNotifier/cpp/RemovableMediaStateNotifierByAndroidIntent.cpp \
  adaptations/src/ResourceCategoryLayout/cpp/AndroidResourceCategoryLayout.cpp \
  adaptations/src/TimeZoneChangedObserver/cpp/TimeZoneChangedObserverByAndroidIntent.cpp \
  adaptations/src/NavCloudConfiguration/cpp/NavCloudConfiguration.cpp \
  adaptations/src/OnlineRoutingConfiguration/cpp/AndroidOnlineRoutingConfiguration.cpp \
  adaptations/src/OnlineSearchConfiguration/cpp/AndroidOnlineSearchConfiguration.cpp \
  processhost/AndroidServiceAdaptationProvider.cpp \
  processhost/JniWrapper.cpp \
  processhost/NavKitJavaVM.cpp \
  processhost/NavKitRunnerAndroid.cpp \
  processhost/ProductContextAndroid.cpp \
  processhost/ProductContext.cpp \
  processhost/StdioConverter.cpp \
  shared/adaptations/DeviceInformation/Implementation/Muid.cpp \
  shared/adaptations/DeviceInformation/Implementation/MUIDUtils.cpp \
  shared/adaptations/MapSecurity/Implementation/KeyStoreMapSecurity.cpp \
  shared/adaptations/RDSTMCService/RDSTMCService.cpp \
  shared/utils/Utils/Implementation/AdaptationsMutex.cpp \
  shared/utils/Utils/Implementation/AdaptationsMutexLocker.cpp \
  shared/utils/Utils/Implementation/StringUtils.cpp \

include $(BUILD_SHARED_LIBRARY)
