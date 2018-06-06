LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := boost-system
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../build/native/all/lib/Framework.ThirdParty.Boost.System/$(TARGET_ARCH_ABI)/libFramework.ThirdParty.Boost.System.a
include $(PREBUILT_STATIC_LIBRARY)
