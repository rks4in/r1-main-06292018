/*!
 * \file
 * \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
 * <br>
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

#ifndef ANDROIDJNIUTIL_H
#define ANDROIDJNIUTIL_H

#include "jni.h"

//! \brief Utility class for using JNI in Android context.
//!
//! Used as base class for Android Adaptations.
class CAndroidJNIUtil
{
protected:
  //! \brief Construct utility class by passing Java VM pointer.
  //!
  //! The Java Virtual Machine should remain in existence as long
  //! as the created utility class is alive.
  //!
  //! \param[in] aVm Pointer to the Java Virtual Machine.
  explicit CAndroidJNIUtil(JavaVM* aVm);

  //! \brief Destruct utility class.
  virtual ~CAndroidJNIUtil() {};

  //! \brief Retrieve the Java VM pointer.
  //! \returns Pointer to the Java Virtual Machine.
  JavaVM* RetrieveVM() const;

  //! \brief Retrieve the JNI Environment.
  //!
  //! This method should only be called if the caller's thread is a Java thread or
  //! is a native thread that already has been attached to the Java VM.
  //! If that condition is not met, NULL is returned.
  //!
  //! \returns Pointer to the JNI Environment.
  JNIEnv* RetrieveEnv() const;

  //! \brief Create instance of the named Java class, and keep global reference to it.
  //!
  //! The class must have a constructor that doesn't take any arguments.
  //! This method, or CreateProxyWithContext(), should only be called once.
  //! It should be paired with a call to ReleaseProxy().
  //!
  //! \param[in] aClassName The name of the Java class of which an object should be created
  //!                       in JNI notation.
  //! \retval true  The object has successfully been created.
  //! \retval false The object was not created, or was already created.
  bool CreateProxy(const char* aClassName);

  //! \brief Create instance of the named Java class by passing it an Android Context,
  //!        and keep global reference to it.
  //!
  //! The class must have a constructor that takes a single argument of type
  //! android.content.Context.
  //! This method, or CreateProxy(), should only be called once.
  //! It should be paired with call to ReleaseProxy().
  //!
  //! \param[in] aClassName The name of the Java class of which an object should be created
  //!                       in JNI notation.
  //! \retval true  The object has successfully been created.
  //! \retval false The object was not created, or was already created.
  bool CreateProxyWithContext(const char* aClassName);

  //! \brief Check whether a Java proxy object has been created and not yet been released.
  //! \returns Whether a Java project is present.
  bool HasProxy() const;

  //! \brief Get the Java proxy object created by CreateProxy() or CreateProxyWithContext().
  //!
  //! \returns The Java proxy object. If no Java proxy is present (i.e., HasProxy() returns false),
  //!          then NULL is returned.
  jobject GetProxy();

  //! \brief Release the object created with either CreateProxy() or CreateProxyWithContext().
  //!
  //! This method may be called safely multiple times.
  void ReleaseProxy();

  // TODO: move to private, and rename
  jobject mProxy; // global reference to the Java (proxy) object.

private:
  JavaVM* iVm;
};

#endif /* ANDROIDJNIUTIL_H */
