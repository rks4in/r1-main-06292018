LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := boost-filesystem
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../build/native/all/lib/Framework.ThirdParty.Boost.Filesystem/$(TARGET_ARCH_ABI)/libFramework.ThirdParty.Boost.Filesystem.a
include $(PREBUILT_STATIC_LIBRARY)
