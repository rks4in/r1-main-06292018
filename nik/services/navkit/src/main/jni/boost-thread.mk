LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := boost-thread
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../build/native/all/lib/Framework.ThirdParty.Boost.Thread/$(TARGET_ARCH_ABI)/libFramework.ThirdParty.Boost.Thread.a
include $(PREBUILT_STATIC_LIBRARY)
