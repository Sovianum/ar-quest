/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.core.examples.java.helloar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/** Helper to ask permission. */
public final class PermissionHelper {
  private static final int MY_PERMISSIONS_CODE = 0;

  static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
  static final String FINE_LOC_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
  static final String COARSE_LOC_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;

  public static boolean hasPermissions(Context context) {
    return hasPermission(context, CAMERA_PERMISSION) &&
            hasPermission(context, FINE_LOC_PERMISSION) &&
            hasPermission(context, COARSE_LOC_PERMISSION);
  }

  static private boolean hasPermission(Context context, String permission) {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
  }

  public static void requestPermissions(Activity activity) {
    ActivityCompat.requestPermissions(
            activity, new String[] {
                    CAMERA_PERMISSION,
                    FINE_LOC_PERMISSION,
                    COARSE_LOC_PERMISSION
            }, MY_PERMISSIONS_CODE);
  }

  static void launchPermissionSettings(Activity activity) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
    activity.startActivity(intent);
  }
}
