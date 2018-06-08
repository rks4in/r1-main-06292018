LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := abstraction
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../build/native/debug/lib/Framework.Porting.OsAbstractionLayer/$(TARGET_ARCH_ABI)/libFramework.Porting.OsAbstractionLayer.a
include $(PREBUILT_STATIC_LIBRARY)
