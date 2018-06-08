//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#include <assert.h>
#include <dlfcn.h>
#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <string>
#include <sys/types.h>
#include <sys/stat.h>

#include "NavKitJavaVM.h"
#include "NavKitRunnerAndroid.h"
#include "ProductContext.h"
#include "NavKitMain.h"

using namespace std;

static const char* TAG = "NavKitJniWrapper";

static void* sLibNavKit = NULL;
static void* sLibSQLite = NULL;

typedef TNavKitMainCode (*TFnNavKitMain)(NNavKit::IProductContext& aProductContext);
typedef TNavKitTerminateCode (*TFnNavKitTerminate)();

static TFnNavKitMain sFnNavKitMain = NULL;
static TFnNavKitTerminate sFnNavKitTerminate = NULL;

TNavKitMainCode NavKitMain(NNavKit::IProductContext& aProductContext)
{
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "Jni:NavKitMain = %p", sFnNavKitMain);
  assert(sFnNavKitMain != NULL);
  return (*sFnNavKitMain)(aProductContext);
}

TNavKitTerminateCode NavKitTerminate()
{
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "Jni:NavKitTerminate = %p", sFnNavKitTerminate);
  assert(sFnNavKitTerminate != NULL);
  return (*sFnNavKitTerminate)();
}

static bool OpenNavKit(const char* aLibNavKitSoPath)
{
  assert(aLibNavKitSoPath != NULL);
  assert(sLibNavKit == NULL);
  assert(sLibSQLite == NULL);

  bool ok = true;

  const char* fileTitle = strrchr(aLibNavKitSoPath, '/');
  if (fileTitle == NULL)
  {
    fileTitle = aLibNavKitSoPath;
  }
  else
  {
    fileTitle++;
  }
  string libSQLiteSoPath(aLibNavKitSoPath, fileTitle - aLibNavKitSoPath);
  libSQLiteSoPath += "libSQLite.so";

  __android_log_print(ANDROID_LOG_INFO, TAG, "dlopen('%s', RTLD_NOW)", libSQLiteSoPath.c_str());
  sLibSQLite = dlopen(libSQLiteSoPath.c_str(), RTLD_NOW);
  if (sLibSQLite == NULL)
  {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not dlopen('%s'): %s", libSQLiteSoPath.c_str(), dlerror());
      ok = false;
  }
  else
  {
    __android_log_print(ANDROID_LOG_INFO, TAG, "'%s' successfully loaded!", libSQLiteSoPath.c_str());
  }

  __android_log_print(ANDROID_LOG_INFO, TAG, "dlopen('%s', RTLD_NOW)", aLibNavKitSoPath);
  sLibNavKit = dlopen(aLibNavKitSoPath, RTLD_NOW);
  if (sLibNavKit == NULL)
  {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not dlopen('%s'): %s", aLibNavKitSoPath, dlerror());
      ok = false;
  }
  else
  {
    __android_log_print(ANDROID_LOG_INFO, TAG, "'%s' successfully loaded!", aLibNavKitSoPath);

    sFnNavKitMain = (TFnNavKitMain) dlsym(sLibNavKit, "NavKitMain");
    if (sFnNavKitMain == NULL)
    {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "Symbol 'NavKitMain' is missing from shared library!!\n");
      ok = false;
    }

    sFnNavKitTerminate = (TFnNavKitTerminate) dlsym(sLibNavKit, "NavKitTerminate");
    if (sFnNavKitTerminate == NULL)
    {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "Symbol 'NavKitTerminate' is missing from shared library!!\n");
      ok = false;
    }
  }

  return ok;
}

static void CloseNavKit()
{
  sFnNavKitMain = NULL;
  sFnNavKitTerminate = NULL;
  if (sLibNavKit != NULL)
  {
    __android_log_print(ANDROID_LOG_INFO, TAG, "dlclose(NavKit)");
    if (0 != dlclose(sLibNavKit))
    {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "dlclose(NavKit) failed: %s", dlerror());
    }
    else
    {
      __android_log_print(ANDROID_LOG_DEBUG, TAG, "dlclose(NavKit) finished");
    }
    sLibNavKit = NULL;
  }
  if (sLibSQLite != NULL)
  {
    __android_log_print(ANDROID_LOG_INFO, TAG, "dlclose(SQLite)");
    if (0 != dlclose(sLibSQLite))
    {
      __android_log_print(ANDROID_LOG_ERROR, TAG, "dlclose(SQLite) failed: %s", dlerror());
    }
    else
    {
      __android_log_print(ANDROID_LOG_DEBUG, TAG, "dlclose(SQLite) finished");
    }
    sLibSQLite = NULL;
  }
}

extern "C" JNIEXPORT jlong JNICALL Java_com_tomtom_navkit_NavKit_navKitCreate(JNIEnv* aEnv, jobject thiz, jstring aLibNavKitPath)
{
  // This should never happen! Creating a new instance of NavKit when the previous instance is still up and running
  // leads to undefined behavior (usually crash). This is prevented from happening on upper level (in NavKit.java).
  // Therefore if for some reason this happened it's better to kill the hosted process explicitly with clear error
  // message rather than have a crash in random location with confusing crash log.
  if (sLibNavKit != NULL)
  {
    __android_log_print(ANDROID_LOG_FATAL, TAG, " ");
    __android_log_print(ANDROID_LOG_FATAL, TAG, " ");
    __android_log_print(ANDROID_LOG_FATAL, TAG, " ");
    __android_log_print(ANDROID_LOG_FATAL, TAG, "================================================================================");
    __android_log_print(ANDROID_LOG_FATAL, TAG, " ");
    __android_log_print(ANDROID_LOG_FATAL, TAG, "FATAL ERROR: NAVKIT IS ALREADY CREATED WHEN CREATING A NEW INSTANCE OF NAVKIT!!!");
    __android_log_print(ANDROID_LOG_FATAL, TAG, "             KILLING NAVKIT HOSTED PROCESS BY CALLING raise(SIGKILL)...");
    __android_log_print(ANDROID_LOG_FATAL, TAG, " ");
    __android_log_print(ANDROID_LOG_FATAL, TAG, "================================================================================");
    __android_log_print(ANDROID_LOG_FATAL, TAG, " ");
    __android_log_print(ANDROID_LOG_FATAL, TAG, " ");
    __android_log_print(ANDROID_LOG_FATAL, TAG, " ");

    raise(SIGKILL);
  }

  aEnv->GetJavaVM(&sJavaVM);
  __android_log_print(ANDROID_LOG_VERBOSE, TAG, "sJavaVM: %ld", (long) sJavaVM);

  NNavKit::IProductContext* productContext = NNavKit::CreateProductContext();
  CNavKitRunnerAndroid* navKitRunner =  new CNavKitRunnerAndroid(productContext);
  assert(navKitRunner != NULL);

  bool ok = true;

  // Redirect stdio/stderr to logcat, install crash handler
  navKitRunner->Setup();

  // Load libNavKit.so, which initializes all static variables
  const char* libNavKitSoPath = aEnv->GetStringUTFChars(aLibNavKitPath, NULL);
  ok = OpenNavKit(libNavKitSoPath);
  aEnv->ReleaseStringUTFChars(aLibNavKitPath, libNavKitSoPath);
  libNavKitSoPath = NULL;

  if (!ok)
  {
    CloseNavKit();

    if (navKitRunner != NULL)
    {
      // Stops stdio/stderr redirection
      navKitRunner->Teardown();
      delete navKitRunner;
      navKitRunner = NULL;
    }
  }

  return (jlong) navKitRunner;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_tomtom_navkit_NavKit_navKitStart(JNIEnv* aEnv, jobject aThis,
                                                                                  jlong aNavKitRunnerPtr)
{
  bool result = false;

  if (aNavKitRunnerPtr != 0)
  {
    // Allow RW permissions for user and group. This is required for multi user installations,
    // on Italia devices for example the case where a restricted user runs NavKit and the owner
    // performs map updates.
    umask(0007);

    CNavKitRunnerAndroid* navKitRunner = reinterpret_cast<CNavKitRunnerAndroid*>(aNavKitRunnerPtr);

    if (navKitRunner->StartNavKit())
    {
      result = true;
    }
  }

  return (jboolean) (result ? JNI_TRUE : JNI_FALSE);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_tomtom_navkit_NavKit_navKitIsRunning(JNIEnv* env, jobject thiz,
                                                                                    jlong navKitRunnerPtr)
{
  bool result = false;
  
  if (navKitRunnerPtr != 0)
  {
    CNavKitRunnerAndroid* navKitRunner = reinterpret_cast<CNavKitRunnerAndroid*>(navKitRunnerPtr);
    result = navKitRunner->IsRunning();
  }

  return (jboolean) (result ? JNI_TRUE : JNI_FALSE);
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_tomtom_navkit_NavKit_navKitStop(JNIEnv* env, jobject thiz,
                                                                                           jlong navKitRunnerPtr)
{
  if (navKitRunnerPtr != 0)
  {
    CNavKitRunnerAndroid* navKitRunner = reinterpret_cast<CNavKitRunnerAndroid*>(navKitRunnerPtr);
    return navKitRunner->StopNavKit();
  }
  return true;
}

extern "C" JNIEXPORT void JNICALL Java_com_tomtom_navkit_NavKit_navKitDestroy(JNIEnv* env, jobject thiz,
                                                                              jlong navKitRunnerPtr)
{
  CloseNavKit();

  if (navKitRunnerPtr != 0)
  {
    CNavKitRunnerAndroid* navKitRunner = reinterpret_cast<CNavKitRunnerAndroid*>(navKitRunnerPtr);
    // Stops stdio/stderr redirection
    navKitRunner->Teardown();
    delete navKitRunner;
    navKitRunner = NULL;
  }
}

