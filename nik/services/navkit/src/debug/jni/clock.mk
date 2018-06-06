LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := clock
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../build/native/debug/lib/Framework.Porting.Clock/$(TARGET_ARCH_ABI)/libFramework.Porting.Clock.a
include $(PREBUILT_STATIC_LIBRARY)
