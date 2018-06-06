LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := assert
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../build/native/debug/lib/Framework.Porting.Assert/$(TARGET_ARCH_ABI)/libFramework.Porting.Assert.a
include $(PREBUILT_STATIC_LIBRARY)
