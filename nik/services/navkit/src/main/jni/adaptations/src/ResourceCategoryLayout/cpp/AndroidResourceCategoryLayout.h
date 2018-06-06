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

#ifndef ANDROID_RESOURCE_CATEGORY_LAYOUT_H
#define ANDROID_RESOURCE_CATEGORY_LAYOUT_H

#include "IResourceCategoryLayout.h"
#include "AndroidJniUtil.h"
#include "AndroidJniAdaptation.h"
#include "AdaptationsMutex.h"

#include <string>

namespace NProcessHost
{
  namespace NAdaptation
  {
    class CAndroidResourceCategoryLayout: public NNavKit::NAdaptation::IResourceCategoryLayout, public IJniAdaptation, private CAndroidJNIUtil
    {
    public:
      CAndroidResourceCategoryLayout(JavaVM *aVM);

      virtual ~CAndroidResourceCategoryLayout();

      NNavKit::NAdaptation::IResourceCategoryLayout* OnAcquire() override;
      void OnRelease() override;

      bool AcquireJniResources() override;
      void ReleaseJniResources() override;

      const char* GetDirectory(TResourceCategory aCategory) const override;

    private:
      bool SetDirectory(JNIEnv *aEnv, std::string& aDirectory, jmethodID aMethod);

      //! Ensures that directory "aPath" exists. Currently only one level deep is supported.
      void EnsureDirectoryExists(const std::string& aDirectory);

      enum ENumberOfDirectories { ENumberOfDirectoriesValue = 8 };

      jmethodID iMethodIDs[ENumberOfDirectoriesValue];
      std::string iDirectories[ENumberOfDirectoriesValue];
      
      // Guards OnAcquire/OnRelease and all pure virtuals from  ITimeZoneChangedObserver
      NNavKit::NUtils::CAdaptationsMutex iMutex;
    };
  }
}

#endif /* ANDROID_RESOURCE_CATEGORY_LAYOUT_H */
