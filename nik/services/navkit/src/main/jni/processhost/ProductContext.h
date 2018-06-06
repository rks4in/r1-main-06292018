//! \file
//! \copyright Copyright (c) 1992-2018 TomTom N.V. All rights reserved.
//! This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
//! used for internal evaluation purposes or commercial use strictly subject to separate
//! licensee agreement between you and TomTom. If you are the licensee, you are only permitted
//! to use this Software in accordance with the terms of your license agreement. If you are
//! not the licensee then you are not authorised to use this software in any manner and should
//! immediately return it to TomTom N.V.
#ifndef PRODUCT_CONTEXT_H
#define PRODUCT_CONTEXT_H

#include "IProductContext.h"
#include <stddef.h>

namespace NNavKit
{
  namespace NAdaptation
  {
    class IAdaptationProvider;
    class CBaseAdaptationProvider;
  }

  /*!
  * \brief Product Context for the NavKit Runners of the various platforms
  */
  class CProductContext : public IProductContext
  {
  public:
    virtual ~CProductContext();

    NAdaptation::IAdaptationProvider* GetAdaptationProvider() { return iAdaptationProvider; }

    static IProductContext* CreateProductContext()
    {
      CProductContext* productContext = PDCreateProductContext();
      if (productContext != NULL)
      {
        productContext->Initialize();
      }
      return productContext;
    }

  protected:
    CProductContext() : iAdaptationProvider(NULL) { }
    virtual void Initialize() = 0;
    void SetAdaptationProvider(NNavKit::NAdaptation::CBaseAdaptationProvider* aAdaptationProvider);

    static CProductContext* PDCreateProductContext();

  private:
    NAdaptation::IAdaptationProvider* iAdaptationProvider;
  };

  inline IProductContext* CreateProductContext()
  {
    return CProductContext::CreateProductContext();
  }
}

#endif
