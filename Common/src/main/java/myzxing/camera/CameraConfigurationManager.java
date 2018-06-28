/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package myzxing.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;

/**
 * A class which deals with reading, parsing, and setting the camera parameters
 * which are used to configure the camera hardware.
 */
final class CameraConfigurationManager {

	private static final String TAG = "ZXing";

	// This is bigger than the size of a small screen, which is still supported.
	// The routine
	// below will still select the default (presumably 320x240) size for these.
	// This prevents
	// accidental selection of very low resolution on some devices.
//	private static final int MIN_PREVIEW_PIXELS = 470 * 320; // normal screen
//	private static final int MAX_PREVIEW_PIXELS = 1280 * 800;

	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;
	private SurfaceView surfaceView;

	CameraConfigurationManager(Context context, SurfaceView surfaceView) {
		this.context = context;
		this.surfaceView = surfaceView;
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 */
	@SuppressWarnings("deprecation")
	void initFromCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		int width = surfaceView.getWidth();
		int height = surfaceView.getHeight();
		if (width <= 0 || height <= 0) {
			width = metric.widthPixels;
			height = metric.heightPixels;
		}
		Log.i(TAG, "widthPixels:" + metric.widthPixels + " heightPixels:" + metric.heightPixels);
		Log.i(TAG, "viewWidth:" + width + " viewHeight:" + height);
		screenResolution = new Point(width, height);
		Log.i(TAG, "Screen resolution: " + screenResolution);

		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;
		if (screenResolution.x < screenResolution.y) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}

		cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolutionForCamera);

		Log.i(TAG, "Camera resolution: " + cameraResolution);
	}

	void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		Camera.Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

		if (safeMode) {
			Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
		}

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		initializeTorch(parameters, prefs, safeMode);

		CameraConfigurationUtils.setFocus(parameters, true, true, safeMode);

		if (!safeMode) {
			CameraConfigurationUtils.setVideoStabilization(parameters);
			CameraConfigurationUtils.setFocusArea(parameters);
			CameraConfigurationUtils.setMetering(parameters);
		}

		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);

		camera.setDisplayOrientation(90);

		camera.setParameters(parameters);

		Log.i(TAG, "Final camera parameters: " + parameters.flatten());
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	boolean getTorchState(Camera camera) {
		if (camera != null) {
			Camera.Parameters parameters = camera.getParameters();
			if (parameters != null) {
				String flashMode = camera.getParameters().getFlashMode();
				return flashMode != null && (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)
						|| Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode));
			}
		}
		return false;
	}

	void setTorch(Camera camera, boolean newSetting) {
		Camera.Parameters parameters = camera.getParameters();
		doSetTorch(parameters, newSetting, false);
		camera.setParameters(parameters);
	}

	private void initializeTorch(Camera.Parameters parameters, SharedPreferences prefs, boolean safeMode) {
		boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
		doSetTorch(parameters, currentSetting, safeMode);
	}

	private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
		CameraConfigurationUtils.setTorch(parameters, newSetting);
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    if (!safeMode && !prefs.getBoolean("preferences_disable_exposure", true)) {
	      CameraConfigurationUtils.setBestExposure(parameters, newSetting);
	    }
	}

}
