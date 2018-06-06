LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := adaptation-base
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../build/native/debug/lib/Framework.Porting.Adaptations/$(TARGET_ARCH_ABI)/libFramework.Porting.Adaptations.a
include $(PREBUILT_STATIC_LIBRARY)