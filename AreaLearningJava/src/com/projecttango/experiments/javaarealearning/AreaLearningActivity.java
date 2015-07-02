/*
 * Copyright 2014 Google Inc. All Rights Reserved.
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

package com.projecttango.experiments.javaarealearning;

import com.aawhere.jts.map.MapService;
import com.aawhere.tango.jts.TangoJtsUtil;
import com.aawhere.jts.place.Place;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.Tango.OnTangoUpdateListener;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoInvalidException;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.projecttango.experiments.javaarealearning.SetADFNameDialog.SetNameCommunicator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Main Activity class for the Area Learning API Sample. Handles the connection to the Tango service
 * and propagation of Tango pose data to OpenGL and Layout views. OpenGL rendering logic is
 * delegated to the {@link ALRenderer} class.
 */
public class AreaLearningActivity extends Activity implements View.OnClickListener,
        SetNameCommunicator, WaypointNameDialog.WaypiontNameCommunicator {

    private static final String TAG = AreaLearningActivity.class.getSimpleName();
    private static final int SECONDS_TO_MILLI = 1000;
    private Tango mTango;
    private TangoConfig mConfig;
    private TextView mTangoEventTextView;
    private TextView mStart2DeviceTranslationTextView;
    private TextView mAdf2DeviceTranslationTextView;
    private TextView mAdf2StartTranslationTextView;
    private TextView mStart2DeviceQuatTextView;
    private TextView mAdf2DeviceQuatTextView;
    private TextView mAdf2StartQuatTextView;
    private TextView mTangoServiceVersionTextView;
    /**
     * general description of what the device knows
     */
    private TextView mAwarenessTextView;
    private TextView mApplicationVersionTextView;
    private TextView mUUIDTextView;
    private TextView mStart2DevicePoseStatusTextView;
    private TextView mAdf2DevicePoseStatusTextView;
    private TextView mAdf2StartPoseStatusTextView;
    private TextView mStart2DevicePoseCountTextView;
    private TextView mAdf2DevicePoseCountTextView;
    private TextView mAdf2StartPoseCountTextView;
    private TextView mStart2DevicePoseDeltaTextView;
    private TextView mAdf2DevicePoseDeltaTextView;
    private TextView mAdf2StartPoseDeltaTextView;

    private Button mSaveAdf;
    private Button mMarkWaypoint;
    private Button mFirstPersonButton;
    private Button mThirdPersonButton;
    private Button mTopDownButton;

    private int mStart2DevicePoseCount;
    private int mAdf2DevicePoseCount;
    private int mAdf2StartPoseCount;
    private int mStart2DevicePreviousPoseStatus;
    private int mAdf2DevicePreviousPoseStatus;
    private int mAdf2StartPreviousPoseStatus;

    private double mStart2DevicePoseDelta;
    private double mAdf2DevicePoseDelta;
    private double mAdf2StartPoseDelta;
    private double mStart2DevicePreviousPoseTimeStamp;
    private double mAdf2DevicePreviousPoseTimeStamp;
    private double mAdf2StartPreviousPoseTimeStamp;

    private boolean mIsRelocalized;
    private boolean mIsLearningMode;
    private boolean mIsAutoMode;
    private boolean mIsConstantSpaceRelocalize;
    private String mCurrentUUID;
    /**
     * Describes the adf file loaded for localization.
     */
    private TangoAreaDescriptionMetaData mAdfMetadata;

    private ALRenderer mRenderer;
    private GLSurfaceView mGLView;

    private TangoPoseData[] mPoses;
    private static final int UPDATE_INTERVAL_MS = 100;
    private static final DecimalFormat threeDec = new DecimalFormat("00.000");
    public static Object sharedLock = new Object();

    /**
     * The repository of places recorded during this session.
     */
    private MapService mapService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_learning);

        mTangoEventTextView = (TextView) findViewById(R.id.tangoevent);

        mAdf2DeviceTranslationTextView = (TextView) findViewById(R.id.adf2devicePose);
        mStart2DeviceTranslationTextView = (TextView) findViewById(R.id.start2devicePose);
        mAdf2StartTranslationTextView = (TextView) findViewById(R.id.adf2startPose);
        mAdf2DeviceQuatTextView = (TextView) findViewById(R.id.adf2deviceQuat);
        mStart2DeviceQuatTextView = (TextView) findViewById(R.id.start2deviceQuat);
        mAdf2StartQuatTextView = (TextView) findViewById(R.id.adf2startQuat);

        mAdf2DevicePoseStatusTextView = (TextView) findViewById(R.id.adf2deviceStatus);
        mStart2DevicePoseStatusTextView = (TextView) findViewById(R.id.start2deviceStatus);
        mAdf2StartPoseStatusTextView = (TextView) findViewById(R.id.adf2startStatus);

        mAdf2DevicePoseCountTextView = (TextView) findViewById(R.id.adf2devicePosecount);
        mStart2DevicePoseCountTextView = (TextView) findViewById(R.id.start2devicePosecount);
        mAdf2StartPoseCountTextView = (TextView) findViewById(R.id.adf2startPosecount);

        mAdf2DevicePoseDeltaTextView = (TextView) findViewById(R.id.adf2deviceDeltatime);
        mStart2DevicePoseDeltaTextView = (TextView) findViewById(R.id.start2deviceDeltatime);
        mAdf2StartPoseDeltaTextView = (TextView) findViewById(R.id.adf2startDeltatime);

        mFirstPersonButton = (Button) findViewById(R.id.first_person_button);
        mThirdPersonButton = (Button) findViewById(R.id.third_person_button);
        mTopDownButton = (Button) findViewById(R.id.top_down_button);

        mTangoServiceVersionTextView = (TextView) findViewById(R.id.version);
        mApplicationVersionTextView = (TextView) findViewById(R.id.appversion);
        mAwarenessTextView = (TextView) findViewById(R.id.awareness);

        mGLView = (GLSurfaceView) findViewById(R.id.gl_surface_view);

        mSaveAdf = (Button) findViewById(R.id.saveAdf);
        mMarkWaypoint = (Button) findViewById(R.id.markWaypoint);
        mUUIDTextView = (TextView) findViewById(R.id.uuid);

        mSaveAdf.setVisibility(View.GONE);
        // Set up button click listeners
        mFirstPersonButton.setOnClickListener(this);
        mThirdPersonButton.setOnClickListener(this);
        mTopDownButton.setOnClickListener(this);

        PackageInfo packageInfo;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            mApplicationVersionTextView.setText(packageInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        // Configure OpenGL renderer
        mRenderer = new ALRenderer();
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(mRenderer);
        initializeNewTangoService();


        startUIThread();
    }

    /**
     * provides a new tango service session allowing for multiple attempts to localize against
     * different ADF.
     */
    private void initializeNewTangoService() {
        // Instantiate the Tango service
        mTango = new Tango(this);
        mIsRelocalized = false;

        Intent intent = getIntent();
        mIsLearningMode = intent.getBooleanExtra(ALStartActivity.USE_AREA_LEARNING, false);
        mIsAutoMode = intent.getBooleanExtra(ALStartActivity.USE_AUTO, false);
        mIsConstantSpaceRelocalize = intent.getBooleanExtra(ALStartActivity.LOAD_ADF, false);
        setTangoConfig();
        mPoses = new TangoPoseData[3];
    }

    private void setTangoConfig() {
        mConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        // Check if learning mode
        if (mIsLearningMode) {
            // Set learning mode to config.
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_LEARNINGMODE, true);
            // Set the ADF save button visible.
            mSaveAdf.setVisibility(View.VISIBLE);
            mSaveAdf.setOnClickListener(this);
        }
        //don't show until coordinates are available once localized
        mMarkWaypoint.setVisibility(View.INVISIBLE);
        mMarkWaypoint.setOnClickListener(this);

        if (mIsAutoMode) {
            // Set learning mode to config.
            mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_LEARNINGMODE, true);
            //the save button is not necessary since saving will be automatic
            mSaveAdf.setOnClickListener(this);
        }
        // Check for Load ADF/Constant Space relocalization mode
        if (mIsConstantSpaceRelocalize) {
            ArrayList<String> fullUUIDList = new ArrayList<String>();
            // Returns a list of ADFs with their UUIDs
            fullUUIDList = mTango.listAreaDescriptions();
            if (fullUUIDList.size() == 0) {
                mUUIDTextView.setText(R.string.no_uuid);
            }

            // Load the latest ADF if ADFs are found.
            if (fullUUIDList.size() > 0) {
                String adfId = fullUUIDList.get(fullUUIDList.size() - 1);
                mAdfMetadata = mTango.loadAreaDescriptionMetaData(adfId);
                mConfig.putString(TangoConfig.KEY_STRING_AREADESCRIPTION,
                        adfId);
                mUUIDTextView.setText(getString(R.string.number_of_adfs) + fullUUIDList.size()
                        + getString(R.string.latest_adf_is)
                        + adfId);

            }
        }

        //update awareness
        if (!mIsLearningMode && !mIsConstantSpaceRelocalize) {
            mAwarenessTextView.setText(getString(R.string.awareness_looking_only));
        } else if (mIsLearningMode && !mIsConstantSpaceRelocalize) {
            mAwarenessTextView.setText(getString(R.string.awareness_learning));
        } else if (!mIsLearningMode && mIsConstantSpaceRelocalize) {
            mAwarenessTextView.setText(getString(R.string.awareness_memory));
        } else if (mIsLearningMode && mIsConstantSpaceRelocalize) {
            mAwarenessTextView.setText(getString(R.string.awareness_memory_learning));
        } else {
            mAwarenessTextView.setText(getString(R.string.awareness_unknown));
        }
        // Set the number of loop closures to zero at start.
        mStart2DevicePoseCount = 0;
        mAdf2DevicePoseCount = 0;
        mAdf2StartPoseCount = 0;
        mTangoServiceVersionTextView.setText(mConfig.getString("tango_service_library_version"));
    }

    private void setUpTangoListeners() {

        // Set Tango Listeners for Poses Device wrt Start of Service, Device wrt
        // ADF and Start of Service wrt ADF
        ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                TangoPoseData.COORDINATE_FRAME_DEVICE));
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION,
                TangoPoseData.COORDINATE_FRAME_DEVICE));
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION,
                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE));

        mTango.connectListener(framePairs, new OnTangoUpdateListener() {
            @Override
            public void onXyzIjAvailable(TangoXyzIjData xyzij) {
                // Not using XyzIj data for this sample
            }

            // Listen to Tango Events
            @Override
            public void onTangoEvent(final TangoEvent event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTangoEventTextView.setText(event.eventKey + ": " + event.eventValue);
                    }
                });
            }

            @Override
            public void onPoseAvailable(TangoPoseData pose) {
                // Make sure to have atomic access to Tango Data so that
                // render loop doesn't interfere while Pose call back is updating
                // the data.
                synchronized (sharedLock) {
                    float[] translation = pose.getTranslationAsFloats();
                    boolean updateRenderer = false;

                    // Check for Device wrt ADF pose, Device wrt Start of Service pose,
                    // Start of Service wrt ADF pose(This pose determines if device
                    // the is relocalized or not).
                    //from the perspective of the loaded ADF file that can be matched
                    if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION
                            && pose.targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {
                        mPoses[0] = pose;
                        if (mAdf2DevicePreviousPoseStatus != pose.statusCode) {
                            // Set the count to zero when status code changes.
                            mAdf2DevicePoseCount = 0;
                        }
                        mAdf2DevicePreviousPoseStatus = pose.statusCode;
                        mAdf2DevicePoseCount++;
                        // Calculate time difference between current and last available Device wrt
                        // ADF pose.
                        mAdf2DevicePoseDelta = (pose.timestamp - mAdf2DevicePreviousPoseTimeStamp)
                                * SECONDS_TO_MILLI;
                        mAdf2DevicePreviousPoseTimeStamp = pose.timestamp;
                        if (mIsRelocalized && mRenderer.isValid()) {
                            updateRenderer = true;
                            mRenderer.getGreenTrajectory().updateTrajectory(translation);
                        }

                    }
                    //from the persective of the device this session
                    else if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE
                            && pose.targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {
                        mPoses[1] = pose;
                        if (mStart2DevicePreviousPoseStatus != pose.statusCode) {
                            // Set the count to zero when status code changes.
                            mStart2DevicePoseCount = 0;
                        }
                        mStart2DevicePreviousPoseStatus = pose.statusCode;
                        mStart2DevicePoseCount++;
                        // Calculate time difference between current and last available Device wrt
                        // SS pose.
                        mStart2DevicePoseDelta = (pose.timestamp - mStart2DevicePreviousPoseTimeStamp)
                                * SECONDS_TO_MILLI;
                        mStart2DevicePreviousPoseTimeStamp = pose.timestamp;
                        if (!mIsRelocalized && mRenderer.isValid()) {
                            updateRenderer = true;

                            synchronized (mRenderer.getBlueTrajectory()) {
                                mRenderer.getBlueTrajectory().updateTrajectory(translation);
                            }
                        }
                    }
                    //the position of the device when it started up in the coordinates of the ADF file, if matched
                    else if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION
                            && pose.targetFrame == TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE) {
                        mPoses[2] = pose;
                        if (mAdf2StartPreviousPoseStatus != pose.statusCode) {
                            // Set the count to zero when status code changes.
                            mAdf2StartPoseCount = 0;
                        }
                        mAdf2StartPreviousPoseStatus = pose.statusCode;
                        mAdf2StartPoseCount++;
                        // Calculate time difference between current and last available SS wrt ADF
                        // pose.
                        mAdf2StartPoseDelta = (pose.timestamp - mAdf2StartPreviousPoseTimeStamp)
                                * SECONDS_TO_MILLI;
                        mAdf2StartPreviousPoseTimeStamp = pose.timestamp;
                        if (pose.statusCode == TangoPoseData.POSE_VALID) {
                            mIsRelocalized = true;
                            // Set the color to green
                        } else {
                            mIsRelocalized = false;
                            // Set the color blue
                        }
                    }


                    // Update the trajectory, model matrix, and view matrix, then
                    // render the scene again
                    if (updateRenderer && mRenderer.isValid()) {
                        mRenderer.getModelMatCalculator().updateModelMatrix(translation,
                                pose.getRotationAsFloats());
                        mRenderer.updateViewMatrix();
                    }
                }
            }

            @Override
            public void onFrameAvailable(int cameraId) {
                // We are not using onFrameAvailable for this application.
            }
        });
    }

    /**
     * shows a dialog to record the current coordinates by a name given. This should only be called
     * if localization has occurred.
     */
    private void markWaypoint() {
        Bundle bundle = new Bundle();
        TangoPoseData poseData = devicePoseFromMemory();
        Coordinate coordinate = TangoJtsUtil.coordinate(poseData);
        bundle.putSerializable(WaypointNameDialog.COORDINATE_KEY, coordinate);
        FragmentManager manager = getFragmentManager();
        WaypointNameDialog setADFNameDialog = new WaypointNameDialog();
        setADFNameDialog.setArguments(bundle);
        setADFNameDialog.show(manager, "WaypointNameDialog");
    }

    @Override
    public void onWaypointName(String name, Coordinate coordinate) {
        Log.i(TAG,
                "Waypiont named " + name + " at " + coordinate);
        mapService.place(name, coordinate);
    }

    private void saveAdf() {
        showSetNameDialog();
    }

    private void showSetNameDialog() {
        Bundle bundle = new Bundle();
        if (mCurrentUUID != null) {
            try {
                TangoAreaDescriptionMetaData metaData = mTango
                        .loadAreaDescriptionMetaData(mCurrentUUID);
                byte[] adfNameBytes = metaData.get(TangoAreaDescriptionMetaData.KEY_NAME);
                if (adfNameBytes != null) {
                    String fillDialogName = new String(adfNameBytes);
                    bundle.putString(TangoAreaDescriptionMetaData.KEY_NAME, fillDialogName);
                }
            } catch (TangoErrorException e) {
            }
            bundle.putString(TangoAreaDescriptionMetaData.KEY_UUID, mCurrentUUID);
        }
        FragmentManager manager = getFragmentManager();
        SetADFNameDialog setADFNameDialog = new SetADFNameDialog();
        setADFNameDialog.setArguments(bundle);
        setADFNameDialog.show(manager, "ADFNameDialog");
    }

    @Override
    public void onSetName(String name, String uuids) {

        TangoAreaDescriptionMetaData metadata = new TangoAreaDescriptionMetaData();
        try {
            mCurrentUUID = mTango.saveAreaDescription();
            metadata = mTango.loadAreaDescriptionMetaData(mCurrentUUID);
            metadata.set(TangoAreaDescriptionMetaData.KEY_NAME, name.getBytes());
            mTango.saveAreaDescriptionMetadata(mCurrentUUID, metadata);
        } catch (TangoErrorException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.tango_error),
                    Toast.LENGTH_SHORT).show();
            return;
        } catch (TangoInvalidException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.tango_invalid),
                    Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.tango_error),
                    Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), getString(R.string.adf_save) + mCurrentUUID,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the text view in UI screen with the Pose. Each pose is associated with Target and
     * Base Frame. We need to check for that pair and update our views accordingly.
     *
     * @param pose
     */
    private void updateTextViews() {
        if (devicePoseFromMemory() != null
                && devicePoseFromMemory().baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION
                && devicePoseFromMemory().targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {
            mAdf2DeviceTranslationTextView.setText(getTranslationString(devicePoseFromMemory()));
            mAdf2DeviceQuatTextView.setText(getQuaternionString(devicePoseFromMemory()));
            mAdf2DevicePoseStatusTextView.setText(getPoseStatus(devicePoseFromMemory()));
            mAdf2DevicePoseCountTextView.setText(Integer.toString(mAdf2DevicePoseCount));
            mAdf2DevicePoseDeltaTextView.setText(threeDec.format(mAdf2DevicePoseDelta));

            TangoPoseData pose = devicePoseFromMemory();
            //update the awareness text.  this caused problems in the listener thread.
            if (pose.statusCode == TangoPoseData.POSE_INITIALIZING) {
                mAwarenessTextView.setText(getString(R.string.awareness_memory_initializing));
            } else if (pose.statusCode == TangoPoseData.POSE_VALID) {
                if (mMarkWaypoint.getVisibility() != View.VISIBLE) {
                    mMarkWaypoint.setVisibility(View.VISIBLE);
                }

                if (mapService == null) {
                    //load the map service using the uuid of the localized ADF
                    final String uuid = new String(
                            mAdfMetadata.get(TangoAreaDescriptionMetaData.KEY_UUID));
                    final File filesDir = getApplicationContext().getFilesDir();
                    mapService = new MapService(uuid,filesDir);
                }

                final Place place = mapService.nearestPlace(TangoJtsUtil.coordinate(pose));
                String locationName;
                if (place == null) {
                    final String adfFile = new String(
                            mAdfMetadata.get(TangoAreaDescriptionMetaData.KEY_NAME));
                    locationName = adfFile;
                } else {
                    locationName = place.name();
                }
                mAwarenessTextView.setText(
                        getString(R.string.awareness_memory_valid, locationName));

            } else if (pose.statusCode == TangoPoseData.POSE_INVALID) {
                if (mIsConstantSpaceRelocalize) {
                    mAwarenessTextView.setText(getString(R.string.awareness_memory_invalid));
                } else {
                    mAwarenessTextView.setText(getString(R.string.awareness_invalid));
                }
            } else {
                mAwarenessTextView.setText(getString(R.string.awareness_memory_unknown));
            }
        }

        if (mPoses[1] != null
                && mPoses[1].baseFrame == TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE
                && mPoses[1].targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {
            mStart2DeviceTranslationTextView.setText(getTranslationString(mPoses[1]));
            mStart2DeviceQuatTextView.setText(getQuaternionString(mPoses[1]));
            mStart2DevicePoseStatusTextView.setText(getPoseStatus(mPoses[1]));
            mStart2DevicePoseCountTextView.setText(Integer.toString(mStart2DevicePoseCount));
            mStart2DevicePoseDeltaTextView.setText(threeDec.format(mStart2DevicePoseDelta));
        }

        if (mPoses[2] != null
                && mPoses[2].baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION
                && mPoses[2].targetFrame == TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE) {
            mAdf2StartTranslationTextView.setText(getTranslationString(mPoses[2]));
            mAdf2StartQuatTextView.setText(getQuaternionString(mPoses[2]));
            mAdf2StartPoseStatusTextView.setText(getPoseStatus(mPoses[2]));
            mAdf2StartPoseCountTextView.setText(Integer.toString(mAdf2StartPoseCount));
            mAdf2StartPoseDeltaTextView.setText(threeDec.format(mAdf2StartPoseDelta));
        }
    }

    /**
     * accesses the pose, if available, that describes the from from the device as it relates to the
     * area description file.
     *
     * @return the pose or null if not available
     */
    private TangoPoseData devicePoseFromMemory() {
        return mPoses[0];
    }

    private String getTranslationString(TangoPoseData pose) {
        return "[" + threeDec.format(pose.translation[0]) + ","
                + threeDec.format(pose.translation[1]) + "," + threeDec.format(pose.translation[2])
                + "] ";

    }

    private String getQuaternionString(TangoPoseData pose) {
        return "[" + threeDec.format(pose.rotation[0]) + "," + threeDec.format(pose.rotation[1])
                + "," + threeDec.format(pose.rotation[2]) + "," + threeDec.format(pose.rotation[3])
                + "] ";

    }

    private String getPoseStatus(TangoPoseData pose) {
        switch (pose.statusCode) {
            case TangoPoseData.POSE_INITIALIZING:
                return getString(R.string.pose_initializing);
            case TangoPoseData.POSE_INVALID:
                return getString(R.string.pose_invalid);
            case TangoPoseData.POSE_VALID:
                return getString(R.string.pose_valid);
            default:
                return getString(R.string.pose_unknown);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mTango.disconnect();
        } catch (TangoErrorException e) {
            Toast.makeText(getApplicationContext(), R.string.tango_error, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setUpTangoListeners();
        } catch (TangoErrorException e) {
            Toast.makeText(getApplicationContext(), R.string.tango_error, Toast.LENGTH_SHORT)
                    .show();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), R.string.no_permissions, Toast.LENGTH_SHORT)
                    .show();
        }
        try {
            mTango.connect(mConfig);
        } catch (TangoOutOfDateException e) {
            Toast.makeText(getApplicationContext(), R.string.tango_out_of_date_exception,
                    Toast.LENGTH_SHORT).show();
        } catch (TangoErrorException e) {
            Toast.makeText(getApplicationContext(), R.string.tango_error, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTango.disconnect();
    }

    // OnClick Button Listener for all the buttons
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_person_button:
                mRenderer.setFirstPersonView();
                break;
            case R.id.top_down_button:
                mRenderer.setTopDownView();
                break;
            case R.id.third_person_button:
                mRenderer.setThirdPersonView();
                break;
            case R.id.saveAdf:
                saveAdf();
                break;
            case R.id.markWaypoint:
                markWaypoint();
                break;
            default:
                Log.w(TAG, "Unknown button click");
                return;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mRenderer.onTouchEvent(event);
    }

    /**
     * Create a separate thread to update Log information on UI at the specified interval of
     * UPDATE_INTERVAL_MS. This function also makes sure to have access to the mPoses atomically.
     */
    private void startUIThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(UPDATE_INTERVAL_MS);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    synchronized (sharedLock) {

                                        if (mPoses == null) {
                                            return;
                                        } else {
                                            updateTextViews();
                                        }
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


}
