package com.tartagliaeg.grmjava.domain.utils;

import android.os.Looper;

public class PlatformData {

  public boolean isMainThread () {
    return Looper.myLooper() == Looper.getMainLooper();
  }

}
