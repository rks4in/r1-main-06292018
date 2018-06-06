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

#ifndef __PALJNIRESOURCEMANAGEMENT_H__
#define __PALJNIRESOURCEMANAGEMENT_H__

#include "jni.h"
#include <stdio.h>
namespace NJniUtils
{
/**
 * Generic container for any JNI type
 *
 */
  template <typename taJniType>
    class CJniResource
  {
  protected:
    CJniResource(taJniType aObj):iJniItem(aObj){}
    virtual ~CJniResource(){}
  protected:
    taJniType GetResource() {return iJniItem;}
    void SetResource(taJniType aJniItem) { iJniItem= aJniItem;}
  private:
    CJniResource(){}
    CJniResource ( CJniResource const& );
    CJniResource& operator = ( CJniResource const& );

    taJniType iJniItem;
  };

  /**
   * Generic container for any JNI class
   *
   */
  template <class taJObjectType>
    class CJniJPtr: public CJniResource<taJObjectType*>
  {
  public:
    explicit CJniJPtr(JNIEnv *aEnv,taJObjectType* aObj):CJniResource<taJObjectType*>(aObj),iJniEnv(aEnv)
    {
    }
    operator taJObjectType*()         {return this->GetResource();}
    taJObjectType& operator*()        {return *(this->GetResource());}
    taJObjectType* operator->()       {return this->GetResource();}

    /**
     * SetPtr(taJObjectType* aPtr)
     * Release current instance and reset the current object pointer.
     * WARNING: Current instance will be released.
     */
    void SetPtr(taJObjectType* aPtr)
    {
      ReleaseRef();
      CJniResource<taJObjectType*>::SetResource(aPtr);
    }
    virtual ~CJniJPtr()
    {
      ReleaseRef();
    }
  protected:
    JNIEnv* Env()                     {return this->iJniEnv;}
    void ReleaseRef()
    {
      if(this->GetResource() == NULL)
        return;
      iJniEnv->DeleteLocalRef(this->GetResource());
      this->SetResource(NULL);
    }
  private:
    JNIEnv *iJniEnv;
  };
  /**
   * jstring wrapper
   *
   */
  class CJniStringPtr: public CJniJPtr<_jstring>
  {
  public:
    CJniStringPtr(JNIEnv *aEnv):
      CJniJPtr<_jstring>(aEnv,NULL),
      iUTFString(NULL)
    {

    }
    CJniStringPtr(JNIEnv *aEnv, jstring aStr):
      CJniJPtr<_jstring>(aEnv,aStr),
      iUTFString(NULL)
    {

    }

    const char* GetStringUTFChars()
    {
      if((iUTFString == NULL) && (GetResource() != NULL))
      {
          iUTFString = Env()->GetStringUTFChars(GetResource(), NULL);
      }
      return iUTFString;
    }

    size_t GetStringUTFLength()
    {
      return GetResource() != NULL
        ? static_cast<size_t>(Env()->GetStringUTFLength(GetResource()))
        : 0;
    }

    ~CJniStringPtr()
    {
      if(iUTFString)
      {
        Env()->ReleaseStringUTFChars(GetResource(),iUTFString);
        iUTFString = NULL;
      }
    }
  private:
    const char* iUTFString;
  };
  /**
   * jclass wrapper
   *
   */
  class CJniJClassPtr: public CJniJPtr<_jclass>
  {
  public:
    CJniJClassPtr(JNIEnv *aEnv):
      CJniJPtr<_jclass>(aEnv,NULL)
    {

    }
    CJniJClassPtr(JNIEnv *aEnv, jclass aClass):
      CJniJPtr<_jclass>(aEnv,aClass)
    {

    }
  };
  /**
   * jobjectArray wrapper
   *
   */
  class CJniArrayPtr: public CJniJPtr<_jobjectArray>
  {
  public:
    CJniArrayPtr(JNIEnv *aEnv):
      CJniJPtr<_jobjectArray>(aEnv,NULL)
    {

    }
    CJniArrayPtr(JNIEnv *aEnv, jobjectArray aArr):
      CJniJPtr<_jobjectArray>(aEnv,aArr)
    {

    }
    size_t GetArrayLength()
    {
      return GetResource() != nullptr ? Env()->GetArrayLength(GetResource()) : 0u;
    }
  };
  /**
   * jobject wrapper
   *
   */
  class CJniObjectPtr: public CJniJPtr<_jobject>
  {
  public:
    CJniObjectPtr(JNIEnv *aEnv):
      CJniJPtr<_jobject>(aEnv,NULL)
    {

    }
    CJniObjectPtr(JNIEnv *aEnv, jobject aArr):
      CJniJPtr<_jobject>(aEnv,aArr)
    {

    }
  };
  /**
   * Java exception wrapper
   *
   */
 class CJniException
 {
public:
    static bool Occured(JNIEnv *aEnv)
    {
      if (aEnv->ExceptionOccurred())
      {
        aEnv->ExceptionDescribe();
        aEnv->ExceptionClear();
        return true;
      }
      return false;
    }
 private:
    CJniException();
    CJniException ( CJniException const& );
    CJniException& operator = ( CJniException const& );
 };
}

#endif //__PALJNIRESOURCEMANAGEMENT_H__
