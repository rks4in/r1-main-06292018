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

#ifndef JNI_ADPATATION_H
#define JNI_ADPATATION_H

#include "jni.h"
namespace NProcessHost
{
  namespace NAdaptation
  {
    class IJniAdaptation
    {
    public:
      virtual ~IJniAdaptation() { }
      virtual bool AcquireJniResources() = 0;
      virtual void ReleaseJniResources() = 0;
    };
  }
}

#endif /* JNI_ADPATATION_H */
