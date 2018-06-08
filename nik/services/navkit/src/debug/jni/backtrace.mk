LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := backtrace
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../build/native/debug/lib/Framework.Porting.Backtrace/$(TARGET_ARCH_ABI)/libFramework.Porting.Backtrace.a
include $(PREBUILT_STATIC_LIBRARY)
