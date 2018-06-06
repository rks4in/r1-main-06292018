TOP_PATH := $(call my-dir)

# Android Studio fails on first gradle sync without this.
# It tries to validate these files even before dependencies are downloaded.
NATIVE_RELEASE_DIR := $(strip $(wildcard $(TOP_PATH)/../../../build/native/release/lib/Framework.Porting.Backtrace/$(TARGET_ARCH_ABI)/libFramework.Porting.Backtrace.a))
NATIVE_DEBUG_DIR := $(strip $(wildcard $(TOP_PATH)/../../../build/native/debug/lib/Framework.Porting.Backtrace/$(TARGET_ARCH_ABI)/libFramework.Porting.Backtrace.a))
NATIVE_COMPILE_DIR := $(strip $(wildcard $(TOP_PATH)/../../../build/native/all/lib/Framework.ThirdParty.Boost.System/$(TARGET_ARCH_ABI)/libFramework.ThirdParty.Boost.System.a))
ifdef NATIVE_COMPILE_DIR
    include $(TOP_PATH)/boost-thread.mk
    include $(TOP_PATH)/boost-system.mk
    include $(TOP_PATH)/boost-filesystem.mk
endif
ifeq ($(APP_OPTIM),debug)
    ifdef NATIVE_DEBUG_DIR
        include $(TOP_PATH)/../../debug/jni/adaptation-base.mk
        include $(TOP_PATH)/../../debug/jni/abstraction.mk
        include $(TOP_PATH)/../../debug/jni/assert.mk
        include $(TOP_PATH)/../../debug/jni/backtrace.mk
        include $(TOP_PATH)/../../debug/jni/clock.mk
    endif
else
    ifdef NATIVE_RELEASE_DIR
        include $(TOP_PATH)/../../release/jni/adaptation-base.mk
        include $(TOP_PATH)/../../release/jni/abstraction.mk
        include $(TOP_PATH)/../../release/jni/assert.mk
        include $(TOP_PATH)/../../release/jni/backtrace.mk
    endif
endif

include $(TOP_PATH)/NavKitJniBind.mk