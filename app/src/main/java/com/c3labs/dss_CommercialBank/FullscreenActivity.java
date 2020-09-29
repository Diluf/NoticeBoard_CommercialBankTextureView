package com.c3labs.dss_CommercialBank;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.c3labs.dss_CommercialBank.Clz.AutoScrollTextView;
import com.c3labs.dss_CommercialBank.Clz.MyExceptionHandler;
import com.c3labs.dss_CommercialBank.Constants.MyConstants;
import com.c3labs.dss_CommercialBank.Constants.MyValues;
import com.c3labs.dss_CommercialBank.Controls.Methods;
import com.c3labs.dss_CommercialBank.Recievers.MyBroadCastReciever;
import com.c3labs.dss_CommercialBank.WebService.Asyncs.AsyncWebService;
import com.c3labs.dss_CommercialBank.WebService.Refferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = "+++++++++++++++++++++++";
    LayoutInflater inflater;
//    LayoutControls layoutControls;

    Handler handlerTemplateSequence;
    Runnable runnableTemplateSequence;
    Handler handlerSchedules;
    Runnable runnableSchedules;
    Handler handlerCatchFullPlayed;
    Runnable runnableCatchFullPlayed;
    Handler handlerSingleSecond;
    Runnable runnableSingleSecond;
    Handler handlerChangeBoolean;
    Runnable runnableChangeBoolean;

//    Handler handlerLayoutOne;
//    Runnable runnableLayoutOne;

    //    SimpleDateFormat simpleDateFormatTime;
    private JSONArray templateSeqJsonArray, normalSchedulesJsonArray0, normalSchedulesJsonArray1, adFullScheduleJsonArray;
    private JSONObject jsonObjectInsideRunnableSchedules, jsonObjectInsideRunnableSchedules_1;
    private ArrayList<Date[]> adHocTimeArrayList;
    private int adHocStatus;

    private int selectedLayout, selectedSchedule, selectedScheduleNormal_0, selectedScheduleNormal_1;
    private int currentSecond = 0;

    //    private int networkSeconds = 0;
    private boolean changeAdFull, isDownloadStarted, isFirstTime = true;
    private final String[] imageFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};
    private SimpleDateFormat simpleDateFormatFull, simpleDateFormatTime;
    private Drawable.ConstantState downloadConstantStateIcon;
    //    private ZidooHdmiDisPlay mRealtekeHdmi = null;
//    private ViewGroup hdmiInRelative;
    private String[] currentLayoutDetails;
    private File file, file_1;
    private Date[] downloadStartTime;
    private Date date;

    //    Components
    public TextView progCount;
    //    public ImageView statusIcon;
    public ProgressBar progressBar;
    public RelativeLayout dynamic, header;
    //            , currencyMediaWrapper;
//    private TextView branchName, dateTime;
//    public AutoScrollTextView news;
    private View adFullView, currencyView;
    private VideoView videoViewNormal;
    private ImageView imageViewNormal_0, imageViewNormal_1, imageViewAdFull;
    private Bitmap bitmap, bitmap_1;

    //    MediaPlayer mpNormal, mpAdFull;
    private TextureView textureView;
    private MediaPlayer mMediaPlayer;
    Surface surface;

    @Override
    public void onBackPressed() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View v = layoutInflater.inflate(R.layout.dialog_signin, null);
        ImageView asterikL = v.findViewById(R.id.imgV_asterikLeftActivitySplash);
        ImageView asterik = v.findViewById(R.id.imgV_asterikActivitySplash);
        ImageView asterikR = v.findViewById(R.id.imgV_asterikRightActivitySplash);
        final EditText userName = v.findViewById(R.id.et_dialog_signin_UserName);
        final EditText password = v.findViewById(R.id.et_dialog_signin_Password);
//        Button exit = v.findViewById(R.id.btn_dialog_signin_Exit);
        Button signIn = v.findViewById(R.id.btn_dialog_signin_SignIn);

        asterik.startAnimation(AnimationUtils.loadAnimation(this, R.anim.load_asterik));
        asterikL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_asterik));
        asterikR.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_asterik));
        signIn.setText("OK");
        userName.setVisibility(View.GONE);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(v).create();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().trim().equalsIgnoreCase(Methods.getSharedPref(FullscreenActivity.this).getString("ExitPassword", Math.random() + ""))) {
                    handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
                    handlerSchedules.removeCallbacks(runnableSchedules);
                    handlerSingleSecond.removeCallbacks(runnableSingleSecond);
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    alertDialog.dismiss();
                }
            }
        });


        alertDialog.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
        initialize();


//        initializeHandlers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormatFull = new SimpleDateFormat("dd-MMM-yyyy   hh:mm a");

        dynamic = findViewById(R.id.rl_DynamicLayoutActivityFullScreen);
//        branchName = findViewById(R.id.tv_branchNameActivityFullScreen);
//        dateTime = findViewById(R.id.tv_DateTimeActivityFullScreen);
//        news = findViewById(R.id.tv_newsActivityFullScreenNews);
//        statusIcon = findViewById(R.id.imgV_NetworkStatusActivityFullScreen);
        progressBar = findViewById(R.id.prog_DownloadedActivitySplash);
        progCount = findViewById(R.id.tv_progressCountActivityFullScreen);
        header = findViewById(R.id.header);
//        news.setSelected(true);

//        statusIcon.setImageResource(new Methods().getNetworkStatusIcon(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            downloadConstantStateIcon = FullscreenActivity.this.getResources().getDrawable(getResources().getIdentifier("@drawable/download",
                    null, FullscreenActivity.this.getPackageName()), FullscreenActivity.this.getTheme()).getConstantState();
        } else {
            downloadConstantStateIcon = FullscreenActivity.this.getResources().getDrawable(getResources().getIdentifier("@drawable/download",
                    null, FullscreenActivity.this.getPackageName())).getConstantState();
        }


        findViewById(R.id.btn_TestC3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyExceptionHandler.restartApp(FullscreenActivity.this, Splash.class, new Exception("Restarted On C3 press"));
            }
        });

        findViewById(R.id.btn_TestC3).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new Methods().restartDevice();

                return false;
            }
        });
//
//        findViewById(R.id.btn_test2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                    Toast.makeText(FullscreenActivity.this, pInfo.versionName, Toast.LENGTH_SHORT).show();
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        statusIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });

        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, FullscreenActivity.class));


    }

    private void hideAllLayouts() {
//        hdmiInRelative.setVisibility(View.GONE);
//        currencyMediaWrapper.setVisibility(View.INVISIBLE);
//        videoViewNormal.setVisibility(View.GONE);
//        mRealtekeHdmi.setAudio(false);

    }


//    -------------------------
//    -------------------------
//    -------------------------
//    -------------------------
//    -------------------------

    private void initializeHandlers() {
        handlerChangeBoolean = new Handler();
        runnableChangeBoolean = new Runnable() {
            @Override
            public void run() {
                changeAdFull = true;
//                Toast.makeText(FullscreenActivity.this, "Change Boolean", Toast.LENGTH_SHORT).show();
                handlerChangeBoolean.removeCallbacks(this);
            }
        };

        handlerTemplateSequence = new Handler();
        runnableTemplateSequence = new Runnable() {
            @Override
            public void run() {
                methodTemplateSeq();

            }
        };

        handlerTemplateSequence.postDelayed(runnableTemplateSequence, 1000);

        handlerSchedules = new Handler();
        runnableSchedules = new Runnable() {
            @Override
            public void run() {
                methodRunnableSchedules();

            }
        };
        handlerSchedules.postDelayed(runnableSchedules, 1500);

        handlerSingleSecond = new Handler();
        runnableSingleSecond = new Runnable() {
            @Override
            public void run() {
                methodHandlerSingleSecond();
            }
        };
        handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);

        handlerCatchFullPlayed = new Handler();
        runnableCatchFullPlayed = new Runnable() {
            @Override
            public void run() {
                ++selectedScheduleNormal_0;
                Log.d(TAG, "Current Sche: " + selectedScheduleNormal_0);
            }
        };


//        handlerLayoutOne = new Handler();
//        runnableLayoutOne = new Runnable() {
//            @Override
//            public void run() {
//                try {
////                    isLayout_One_Calling = true;
//                    if (currentLayoutDetails[0].equalsIgnoreCase("Split")) {
//                        selectedScheduleNormal_1 = getSelectedLayoutPosition(selectedScheduleNormal_1, MyConstants.SELECTED_SCHEDULE_NORMAL_1);
//                        jsonObjectInsideRunnableSchedules_1 = normalSchedulesJsonArray1.getJSONObject(selectedScheduleNormal_1);
//
////                    String fileName = jsonObjectInsideRunnableSchedules_1.getString("Name");
//                        ++selectedScheduleNormal_1;
//
//                        file_1 = new File(Methods.createOrGetDirectory().toString() + "/" + jsonObjectInsideRunnableSchedules_1.getString("Name"));
//                        if (file_1.exists()) {
//                            if (imageVideoConfigsWithIsImage(file_1)) {
//                                bitmap_1 = BitmapFactory.decodeFile(file_1.getAbsolutePath());
//                                setImageWithAnim();
//
//
//                                handlerLayoutOne.postDelayed(runnableLayoutOne, getMillisFromTime(jsonObjectInsideRunnableSchedules_1.getString("Duration")));
//                            } else {
//                                handlerLayoutOne.postDelayed(runnableLayoutOne, 1);
//                            }
//
//                        } else {
//                            handlerLayoutOne.postDelayed(runnableLayoutOne, 1);
//                        }
//
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        };

    }


//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    Handlers Methods
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================
//    =====================================================

    private synchronized void methodTemplateSeq() {
        Log.d(TAG, "m: " + "");
        handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
        currentLayoutDetails = loadLayout();

        if (handlerCatchFullPlayed != null && runnableCatchFullPlayed != null) {
            handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);
        }
        if (templateSeqJsonArray.length() > 1) {

            if (currentLayoutDetails != null && currentLayoutDetails.length != 0) {
                if (currentLayoutDetails[0].equalsIgnoreCase("AdFull")) {
                    handlerChangeBoolean.postDelayed(runnableChangeBoolean, getMillisFromTime(currentLayoutDetails[1]));
                    handlerSchedules.postDelayed(runnableSchedules, 10);
//                    handlerLayoutOne.removeCallbacks(runnableLayoutOne);
//                    isLayout_One_Calling = false;
                    return;

                } else {
                    loadLayoutSchedules();
                }

                handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
            }


            handlerSchedules.postDelayed(runnableSchedules, 10);
        }
    }


    private synchronized void methodRunnableSchedules() {
        try {
            handlerSchedules.removeCallbacks(runnableSchedules);
            if (dynamic.getChildCount() != 0) {
                Log.d(TAG, "run: *************" + currentLayoutDetails[0]);
                if (currentLayoutDetails[0].equalsIgnoreCase("AdFull")) {
                    if (changeAdFull) {
                        currentLayoutDetails = loadLayout();
                        loadLayoutSchedules();
                        handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
                        handlerSchedules.postDelayed(runnableSchedules, 1);
                        changeAdFull = false;
                    } else {
//                                configAdFullSchedules(null);
                        selectedSchedule = getSelectedLayoutPosition(selectedSchedule, MyConstants.SELECTED_SCHEDULE);
                        jsonObjectInsideRunnableSchedules = adFullScheduleJsonArray.getJSONObject(selectedSchedule);
                        String fileName = jsonObjectInsideRunnableSchedules.getString("Name");


                        file = new File(Methods.createOrGetDirectory().toString() + "/" + jsonObjectInsideRunnableSchedules.getString("Name"));
                        Log.d(TAG, "run: " + fileName);
                        if (file.exists()) {
                            if (imageVideoConfigsWithIsImage(file)) {
                                stopVideoView();
                                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                textureView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
                                imageViewAdFull.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                                imageViewAdFull.setImageBitmap(bitmap);
                            } else {
//                                        videoView.setVisibility(View.VISIBLE);
                                imageViewAdFull.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
                                textureView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                                try {
                                    mMediaPlayer.reset();
                                    mMediaPlayer.setDataSource(this, Uri.parse(file.toString()));
                                    mMediaPlayer.setSurface(surface);

                                    // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
                                    // creating MediaPlayer
                                    mMediaPlayer.prepareAsync();
                                    // Play video when the media source is ready for playback.
                                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {
                                            mMediaPlayer.start();
                                        }
                                    });

                                } catch (IllegalArgumentException e) {
                                    Log.d(TAG, e.getMessage());
                                } catch (SecurityException e) {
                                    Log.d(TAG, e.getMessage());
                                } catch (IllegalStateException e) {
                                    Log.d(TAG, e.getMessage());
                                } catch (IOException e) {
                                    Log.d(TAG, e.getMessage());
                                }

                            }


                            Log.d(TAG, "run: ===========" + getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                            handlerSchedules.postDelayed(runnableSchedules, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
                        } else {
                            handlerSchedules.postDelayed(runnableSchedules, 10);
                        }
                        ++selectedSchedule;
//                                if (jsonObject.getString("Name"))
                    }
//                            asdasda
                } else if (currentLayoutDetails[0].equalsIgnoreCase("Split")) {
//                            configNormalSchedules();


//                    if (!isLayout_One_Calling) {
//                        handlerLayoutOne.postDelayed(runnableLayoutOne, 0);
//                    }
//
//
//                    selectedScheduleNormal_0 = getSelectedLayoutPosition(selectedScheduleNormal_0, MyConstants.SELECTED_SCHEDULE_NORMAL_0);
//                    jsonObjectInsideRunnableSchedules = normalSchedulesJsonArray0.getJSONObject(selectedScheduleNormal_0);
//                    String fileName = jsonObjectInsideRunnableSchedules.getString("Name");
//
//
//                    file = new File(Methods.createOrGetDirectory().toString() + "/" + jsonObjectInsideRunnableSchedules.getString("Name"));
//                    Log.d(TAG, "run:---------- " + fileName);
//                    if (file.exists()) {
//                        if (imageVideoConfigsWithIsImage(file)) {
//                            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                            videoViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
//                            imageViewNormal_0.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
//                            imageViewNormal_0.setImageBitmap(bitmap);
////                                    imageViewNormal_0.setVisibility(View.VISIBLE);
////                                    videoViewNormal.setVisibility(View.GONE);
//                        } else {
//                            imageViewNormal_0.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
//                            videoViewNormal.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
//                            videoViewNormal.setVideoURI(Uri.parse(file.toString()));
////                                    videoViewNormal.setVisibility(View.VISIBLE);
////                                    imageViewNormal_0.setVisibility(View.GONE);
////                                    videoViewNormal.get
//                            videoViewNormal.start();
//
//                        }
//
////asd
//                        handlerCatchFullPlayed.postDelayed(runnableCatchFullPlayed, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
//                        handlerSchedules.postDelayed(runnableSchedules, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")));
////                                Toast.makeText(FullscreenActivity.this, getMillisFromTime(jsonObjectInsideRunnableSchedules.getString("Duration")) + "", Toast.LENGTH_SHORT).show();
//                    } else {
//                        handlerSchedules.postDelayed(runnableSchedules, 1);
//                    }
////                            ++selectedScheduleNormal;
                }

//                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                        videoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//
//
            } else {
                handlerSchedules.postDelayed(runnableSchedules, 1000);
                Log.d(TAG, "run:  else----------");
            }
//                    handlerSchedules.postDelayed(runnableSchedules, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private synchronized void methodHandlerSingleSecond() {
        setTimePlusCheckDownloadingTimePlusAdHoc();
        currentSecond++;
        Log.d(TAG, "run: single=========================================" + currentSecond);
//        setNetStatusImage();
        setHeartBeatPacket();
//                checkNetworkIssue();

        handlerSingleSecond.removeCallbacks(runnableSingleSecond);
        handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);
    }


//    ===============================================
//    ===============================================
//    ===============================================
//    ===============================================
//    ===============================================
//    ===============================================


    private void hideNormalLayouts() {
        videoViewNormal.stopPlayback();
        videoViewNormal.seekTo(0);
        imageViewNormal_0.setImageResource(0);
        imageViewNormal_1.setImageResource(0);
    }

    private void setImageWithAnim() {
        imageViewNormal_1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
        imageViewNormal_1.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageViewNormal_1.setImageBitmap(bitmap_1);
                imageViewNormal_1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


//    private void checkNetworkIssue() {
//        if (networkSeconds >= 120) {
//            new Methods().restartDevice();
//        }
//        if (!MyBroadCastReciever.isNetworkConnected(this)) {
//            networkSeconds++;
//        } else {
//            networkSeconds = 1;
//        }
//    }

    private void setTimePlusCheckDownloadingTimePlusAdHoc() {
        try {
//            dateTime.setText(simpleDateFormatFull.format(date = new Date()));
            date = new Date();
            checkDownloadTime(date = simpleDateFormatTime.parse(simpleDateFormatTime.format(date)));


            for (Date adHDate[] :
                    adHocTimeArrayList) {
//                Log.d(TAG, "setTimePlusCheckDownloadingTimePlusAdHoc: b4 adHDate" + adHDate[0] + " - " + adHDate[1] + "srry");
//                Log.d(TAG, "setTimePlusCheckDownloadingTimePlusAdHoc: b4 " + date.after(adHDate[0]) + " " + date.before(adHDate[1]));

                if (date.equals(adHDate[0]) ||
                        (date.after(adHDate[0]) && date.before(adHDate[1]))) {
                    getMyRequiredSS("OnStart");
//                    Log.d(TAG, "setTimePlusCheckDownloadingTimePlusAdHoc: Insode" + adHocStatus);
                    if (adHocStatus == MyConstants.ADH_NOT_LOADED) {
                        String url = Refferences.SchedulePlayStatus.methodName + Methods.getNodeId(FullscreenActivity.this) + File.separator +
                                configAdFullSchedules(getDateFromString(simpleDateFormatTime.format(date)));
                        Log.d(TAG, "startTime: " + url);
                        new AsyncWebService(FullscreenActivity.this, MyConstants.SCHEDULE_PLAY_STATUS).execute(url);


                        handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
                        handlerChangeBoolean.removeCallbacks(runnableChangeBoolean);
                        handlerSchedules.removeCallbacks(runnableSchedules);
//                        handlerLayoutOne.removeCallbacks(runnableLayoutOne);
                        selectedSchedule = -1;
//                        isLayout_One_Calling = false;
                        changeAdFull = false;

                        setView("AdFull");
                        loadLayoutSchedules();
                        currentLayoutDetails = new String[]{"AdFull", ""};
                        //                handlerTemplateSequence.postDelayed(runnableTemplateSequence, getMillisFromTime(currentLayoutDetails[1]));
                        handlerSchedules.postDelayed(runnableSchedules, 1);
                        adHocStatus = MyConstants.ADH_PLAYING;
                    }
                    break;
                    //                return;
                    //                changeAdFull = false;
//                    =======================================AdHoc End Function
                } else if (date.equals(adHDate[2]) ||
                        (date.after(adHDate[2]) && date.before(adHDate[3]))) {
                    getMyRequiredSS("OnEnd");
                    if (adHocStatus == MyConstants.ADH_PLAYING) {
                        MyValues.setResetApp(true);
                        adHocStatus = MyConstants.ADH_STOPPED;
                        initialize();
                        //                return;
                        //                changeAdFull = false;
                    }
                    break;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void checkDownloadTime(Date date) {
        Log.d(TAG, "checkDownloadTime: " + date.after(downloadStartTime[0]) + "  " + date.before(downloadStartTime[1]));
        if (date.equals(downloadStartTime[0]) || (date.after(downloadStartTime[0]) && date.before(downloadStartTime[1]))) {
            if (!isDownloadStarted) {
                MyValues.setResetApp(true);
                beginDownload();
                isDownloadStarted = true;
            }

        }
    }


    private void loadLayoutSchedules() {
        videoViewNormal.stopPlayback();
        videoViewNormal.seekTo(0);
//        videoViewAdFull.stopPlayback();
//        videoViewAdFull.seekTo(0);
        stopVideoView();
//        videoViewNormal.setVisibility(View.GONE);
//        if (mpNormal != null) {
//            mpNormal.release();
//        }
//        if (currentLayoutDetails != null) {
//            Log.d(TAG, "curreny000000000000--" + currentLayoutDetails[0]);
//            if (currentLayoutDetails[0].equalsIgnoreCase("CurrencyHDMIV")) {
//                hdmiInRelative.setVisibility(View.VISIBLE);
//                currencyMediaWrapper.setVisibility(View.INVISIBLE);
//                videoViewNormal.setVisibility(View.GONE);
//                mRealtekeHdmi.setAudio(true);
//
//            } else if (currentLayoutDetails[0].equalsIgnoreCase("CurrencySplitV")) {
//                hdmiInRelative.setVisibility(View.GONE);
//                currencyMediaWrapper.setVisibility(View.VISIBLE);
//                videoViewNormal.setVisibility(View.VISIBLE);
//                mRealtekeHdmi.setAudio(false);
//            }
//        }
    }

    private void setHeartBeatPacket() {
        if (currentSecond >= 120) {
            currentSecond = 0;
            new AsyncWebService(this, MyConstants.HEART_BEAT).execute(Refferences.UpdateNodeStatus.methodName + Methods.getNodeId(FullscreenActivity.this));
            System.gc();
        }
    }


//    private void setNetStatusImage() {
//        if (statusIcon.getDrawable().getConstantState() != downloadConstantStateIcon) {
//            if (MyBroadCastReciever.isNetworkConnected(FullscreenActivity.this)) {
//                statusIcon.setImageResource(R.drawable.online);
//            } else {
//                statusIcon.setImageResource(R.drawable.offline);
//            }
//        }
//
//    }

    private boolean imageVideoConfigsWithIsImage(File file) {
        for (String extension :
                imageFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private String[] loadLayout() {
        try {
            String[] viewMetas = getViewName(selectedLayout);
            if (viewMetas != null && viewMetas.length != 0) {
                setView(viewMetas[0]);
            }
            ++selectedLayout;
            return viewMetas;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] getViewName(int selectedLayoutInner) throws JSONException {
        Log.d(TAG, "getViewName: " + templateSeqJsonArray);
        selectedLayout = getSelectedLayoutPosition(selectedLayoutInner, MyConstants.SELECTED_TEMPLATE_SEQ);
        if (templateSeqJsonArray != null && templateSeqJsonArray.length() != 0) {
            return new String[]{templateSeqJsonArray.getJSONObject(selectedLayout).getString("TemplateName"),
                    templateSeqJsonArray.getJSONObject(selectedLayout).getString("Duration")};

        }

        return null;
    }

    private int getSelectedLayoutPosition(int selectedLayoutInner, int typeArray) {
        if (typeArray == MyConstants.SELECTED_TEMPLATE_SEQ) {
            if (selectedLayoutInner == -1 || (templateSeqJsonArray.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        } else if (typeArray == MyConstants.SELECTED_SCHEDULE) {
            if (selectedLayoutInner == -1 || (adFullScheduleJsonArray.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        } else if (typeArray == MyConstants.SELECTED_SCHEDULE_NORMAL_0) {
            if (selectedLayoutInner == -1 || (normalSchedulesJsonArray0.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        } else if (typeArray == MyConstants.SELECTED_SCHEDULE_NORMAL_1) {
            if (selectedLayoutInner == -1 || (normalSchedulesJsonArray1.length() - 1) < selectedLayoutInner) {
                selectedLayoutInner = 0;
            }
        }

        return selectedLayoutInner;
    }

    public void initialize() {
        try {

//            branchName.setText(Methods.getBranchName(this));
            templateSeqJsonArray = new JSONArray(Methods.getSharedPref(this).getString("sequence", ""));
            adFullScheduleJsonArray = new JSONArray();
            adHocStatus = MyConstants.ADH_NOT_LOADED;
//            isDownloadStarted = false;
//            isLayout_One_Calling = false;

            configNormalSchedules();
            configAdFullSchedules(null);
//            if (dynamic.getChildCount() == 0) {
            loadLayouts(MyValues.isResetApp());
//            }
            hideAllLayouts();
//            setNewsLines();
//            loadWebView();
            resetHandlersAndReInit();

            selectedLayout = -1;
            selectedSchedule = -1;
            selectedScheduleNormal_0 = -1;
            selectedScheduleNormal_1 = -1;
            Date startDate = simpleDateFormatTime.parse(Methods.getSharedPref(this).getString("DownloadTime", ""));
//            Toast.makeText(this, startDate + "", Toast.LENGTH_SHORT).show();
            downloadStartTime = new Date[]{startDate, add10Sec(startDate)};
            if (textureView != null) {
                stopVideoView();
            }


            if (!MyValues.isResetApp()) {
                initializeHandlers();
                checkDownloadOnStart(Methods.getSharedPref(this).getString("DownloadStop", ""));
                Log.d(TAG, "initialize: -------111222333" + MyValues.isResetApp());
            }

            MyValues.setResetApp(false);
            Log.d(TAG, "initialize: =============================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDownloadOnStart(String downloadStop) {
        try {
            Date date = new Date();
            if (simpleDateFormatTime.parse(simpleDateFormatTime.format(date)).before(simpleDateFormatTime.parse(downloadStop))) {
                beginDownload();

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void resetHandlersAndReInit() {
        if (handlerChangeBoolean != null && runnableChangeBoolean != null) {
            handlerChangeBoolean.removeCallbacks(runnableChangeBoolean);
        }
        if (handlerSingleSecond != null && runnableSingleSecond != null) {
            handlerSingleSecond.removeCallbacks(runnableSingleSecond);
            handlerSingleSecond.postDelayed(runnableSingleSecond, 1000);

        }
        if (handlerSchedules != null && runnableSingleSecond != null) {
            handlerSchedules.removeCallbacks(runnableSchedules);
            handlerSchedules.postDelayed(runnableSchedules, 1500);

        }
        if (handlerTemplateSequence != null && runnableTemplateSequence != null) {
            handlerTemplateSequence.removeCallbacks(runnableTemplateSequence);
            handlerTemplateSequence.postDelayed(runnableTemplateSequence, 1000);

        }
        if (handlerCatchFullPlayed != null && runnableCatchFullPlayed != null) {
            handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);

        }
//        if (handlerLayoutOne != null && runnableLayoutOne != null) {
//            handlerLayoutOne.removeCallbacks(runnableLayoutOne);
//
//        }
    }


    public void beginDownload() {
        String url = Refferences.GetDownloadMedia.methodName + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + Methods.getNodeId(FullscreenActivity.this);
        new AsyncWebService(this, MyConstants.GET_REQUIRED_FILES).execute(url);
    }

    private void loadLayouts(boolean resetApp) {
//        try {
        if (adFullView == null) {
//                for (int i = 0; i < templateSeqJsonArray.length(); i++) {
//                    if (templateSeqJsonArray.getJSONObject(i).getString("TemplateName").equalsIgnoreCase("AdFullV")) {
            dynamic.addView(adFullView = inflater.inflate(R.layout.layout_ad_full, null));

            imageViewAdFull = adFullView.findViewById(R.id.img_MyFullLayoutAdFull);
            textureView = adFullView.findViewById(R.id.vid_MyFullLayoutAdFull);
            textureView.setSurfaceTextureListener(this);
//            videoViewAdFull.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                    handlerSchedules.postDelayed(runnableSchedules, 10);
//                    Log.d(TAG, "onError: VidAdFull++++++++++++++");
//                    return true;
//                }
//            });
//            videoViewAdFull.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mpAdFull = mediaPlayer;
//                }
//            });               14:27:07.311
//                        break;
//                    }
//                }
        }
        if (!resetApp && currencyView == null) {
            dynamic.addView(currencyView = inflater.inflate(R.layout.layout_currency_v, null));

            imageViewNormal_0 = currencyView.findViewById(R.id.imgV_ImageLayoutCurrencyV);
            imageViewNormal_1 = currencyView.findViewById(R.id.imgV_1_ImageLayoutCurrencyV);
            videoViewNormal = currencyView.findViewById(R.id.vidV_VideoLayoutCurrencyV);
            videoViewNormal.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    ++selectedScheduleNormal_0;
                    handlerCatchFullPlayed.removeCallbacks(runnableCatchFullPlayed);
                    handlerSchedules.postDelayed(runnableSchedules, 1);
//                    Uri mUri = null;
//                    try {
//                        Field mUriField = VideoView.class.getDeclaredField("mUri");
//                        mUriField.setAccessible(true);
//                        mUri = (Uri) mUriField.get(videoViewNormal);
//                        Log.d(TAG, "onError: VidNormal++++++++++++++ " + mUri.toString());
//                        Log.d(TAG, "onError: VidNormal++++++++++++++ " + mUri.toString());
//                    } catch (Exception e) {
//                    }

                    return true;
                }
            });
//            videoViewNormal.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mpNormal = mediaPlayer;
//                }
//            });


//            currencyMediaWrapper = currencyView.findViewById(R.id.rl_mediaWrapperLayoutCurrencyV);
//            hdmiInRelative = currencyView.findViewById(R.id.home_ac_hdmi);
//            mRealtekeHdmi = new ZidooHdmiDisPlay(FullscreenActivity.this, hdmiInRelative, null, ZidooHdmiDisPlay.TYPE_SURFACEVIEW);
//            mRealtekeHdmi.startDisPlay();
        }

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
//
//    public void loadWebView() {
//        if (currencyView != null) {
//            WebView webView = currencyView.findViewById(R.id.webV_ProductLayoutCurrencyV);
//            webView.getSettings().setJavaScriptEnabled(true);
//            webView.getSettings().setAppCacheEnabled(false);
//            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//            webView.loadUrl(Refferences.GetProduct.method);
//
//            webView = currencyView.findViewById(R.id.webV_CurrencyLayoutCurrencyV);
//            webView.getSettings().setJavaScriptEnabled(true);
//            webView.getSettings().setAppCacheEnabled(false);
//            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//            webView.loadUrl(Refferences.GetCurrency.method + Methods.getNodeId(FullscreenActivity.this));
//        }
//    }

    private void stopVideoView() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(-1);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }

        imageViewAdFull.setImageResource(0);
    }

    public void setNewsLines() {
        new AsyncWebService(this, MyConstants.GET_NEWS).execute(Refferences.GetNews.methodName + Methods.getNodeId(FullscreenActivity.this));
    }


    private void configNormalSchedules() {
        try {
            normalSchedulesJsonArray0 = new JSONArray();
            normalSchedulesJsonArray1 = new JSONArray();
            JSONArray schedulesJsonArray = new JSONArray(Methods.getSharedPref(this).getString("schedules", ""));
            JSONArray jsonArray;

            for (int i = 0; i < schedulesJsonArray.length(); i++) {
                if (schedulesJsonArray.getJSONObject(i).getInt("Panel") == 0) {
                    jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Default");
                    for (int l = 0; l < jsonArray.length(); l++) {
                        normalSchedulesJsonArray0.put(jsonArray.getJSONObject(l));
                    }
                }
                if (schedulesJsonArray.getJSONObject(i).getInt("Panel") == 1) {
                    jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Default");
                    for (int l = 0; l < jsonArray.length(); l++) {
                        normalSchedulesJsonArray1.put(jsonArray.getJSONObject(l));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private synchronized String configAdFullSchedules(Date now) {
        String playId = "";
        try {
            boolean isDefault = true;
//            if (adFullScheduleJsonArray.length() != 0) {
            adFullScheduleJsonArray = new JSONArray();
//            adHocStopTimeArrayList = new ArrayList<>();
            adHocTimeArrayList = new ArrayList<>();
            Log.d(TAG, "configAdFullSchedules: " + 1);
            JSONArray schedulesJsonArray = new JSONArray(Methods.getSharedPref(this).getString("schedules", ""));
            JSONArray jsonArray;
            for (int i = 0; i < schedulesJsonArray.length(); i++) {
                Log.d(TAG, "configAdFullSchedules: " + 2);

                if (schedulesJsonArray.getJSONObject(i).getInt("Panel") == 2) {
                    jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Schedule");
                    Log.d(TAG, "configAdFullSchedules: " + jsonArray.length() + "jLength");
//                    if (jsonArray.length() != 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObjectInner = jsonArray.getJSONObject(j);

                        if (now == null) {
                            now = getDateFromString(simpleDateFormatTime.format(new Date()));
                        }

                        Date start = getDateFromString(jsonObjectInner.getString("Start"));
                        Date end = getDateFromString(jsonObjectInner.getString("End"));
                        Log.d(TAG, "configAdFullSchedules: start" + start);

                        if (now.equals(start) || (now.after(start) && now.before(add10Sec(start)))) {
                            Methods methods = new Methods();
                            for (int k = 0; k < jsonObjectInner.getJSONArray("PlayItems").length(); k++) {
                                adFullScheduleJsonArray.put(jsonObjectInner.getJSONArray("PlayItems").getJSONObject(k));
                                methods.saveToTextFile(now + "" + jsonObjectInner.getJSONArray("PlayItems").getJSONObject(k), "/ad-hoc-status.txt");
                                File file = new File(Methods.createOrGetDirectory().toString() + "/" +
                                        jsonObjectInner.getJSONArray("PlayItems").getJSONObject(k).getString("Name"));
                                if (file.exists()) {
                                    methods.saveToTextFile("exists", "/ad-hoc-status.txt");
                                } else {
                                    methods.saveToTextFile("not exists", "/ad-hoc-status.txt");
                                }
                            }
                            isDefault = false;
//                            if (now.equals(start)) {
                            playId = jsonObjectInner.getString("PlaylistScheduleId");
//                            }
                        }

//                        adHocStartTimeArrayList.add(getDateArray(start));
//                        adHocStopTimeArrayList.add(getDateArray(end));
                        adHocTimeArrayList.add(getDateArray(start, end));
                        Log.d(TAG, "configAdFullSchedules: " + start);
                        Log.d(TAG, "configAdFullSchedules: " + end);
                    }
//                    }
                    if (isDefault) {
                        jsonArray = schedulesJsonArray.getJSONObject(i).getJSONArray("Default");
                        for (int l = 0; l < jsonArray.length(); l++) {
                            adFullScheduleJsonArray.put(jsonArray.getJSONObject(l));
                        }
//                        scheduleAdFullConfigs(MyConstants.DEFAULT_ARRAY, "");
                    }

                    break;
                }

            }
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return playId;
    }

    private Date[] getDateArray(Date start, Date end) {
        return new Date[]{start, add10Sec(start), end, add10Sec(end)};
    }

    private Date add10Sec(Date date) {
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, 10);
        return calendar.getTime();
    }

    //
//    private void scheduleAdFullConfigs(String typeArray, String playlistScheduleId) {
//        if (adFullArray != null) {
//            if (!adFullArray[0].equalsIgnoreCase(typeArray)) {
//                adFullArray[0] = typeArray;
//                adFullArray[1] = playlistScheduleId;
//                selectedSchedule = -1;
//
//            } else if (adFullArray[0].equalsIgnoreCase(MyConstants.SCHEDULE_ARRAY) && !adFullArray[1].equalsIgnoreCase(playlistScheduleId)) {
//                adFullArray[1] = playlistScheduleId;
//                selectedSchedule = -1;
//            }
//        } else {
//            adFullArray = new String[]{typeArray, playlistScheduleId};
//            selectedSchedule = -1;
//        }
//    }

    private Date getDateFromString(String format) throws ParseException {
        return simpleDateFormatTime.parse(format);
    }


    private void setView(String viewName) {
        if (viewName.equalsIgnoreCase("AdFull")) {
//            adFullView.setAlpha(1);
            adFullView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
            currencyView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
            currencyView.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    hideNormalLayouts();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else {
            if (currentLayoutDetails != null && currentLayoutDetails[0].equalsIgnoreCase("AdFull")) {
                stopVideoView();
//                if (mpAdFull != null) {
//                    mpAdFull.release();
//                }
                if (currencyView != null) {
                    currencyView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_in));
                } else {
                    return;
                }
                adFullView.startAnimation(AnimationUtils.loadAnimation(FullscreenActivity.this, R.anim.fade_out));
            } else {
//                adFullView.setAlpha(0);
                if (currencyView.getAnimation() != null) {
                    currencyView.getAnimation().reset();
                }

            }

        }
        initIfFirstTime();
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------
//    -------------------------------------------------------------------------

    public long getMillisFromTime(String dateString) {
        String[] splitStrings = dateString.split(":");
        return ((Long.parseLong(splitStrings[0]) * 60 * 60) + (Long.parseLong(splitStrings[1]) * 60) + (Long.parseLong(splitStrings[2]))) * 1000;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        surface = new Surface(surfaceTexture);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                handlerSchedules.postDelayed(runnableSchedules, 10);
                Log.d(TAG, "onError: VidAdFull++++++++++++++");
                return true;
            }
        });
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            // Make sure we stop video and release resources when activity is destroyed.
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    //
    //
    //
    //
    //

    private void takeScreenshot(String value, Bitmap bitmap) {
        Date now = new Date();
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + now + "_" + value + ".jpg";

            // create bitmap screen capture
            if (bitmap == null) {
                View v1 = getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(true);
            }

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }


    private void getMyRequiredSS(final String value) {
        if (mMediaPlayer.isPlaying()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    takeScreenshot(value, textureView.getBitmap());
                }
            };
            thread.start();
        } else {
            takeScreenshot(value, null);
        }
    }

    private void initIfFirstTime() {
        if (isFirstTime) {
            isFirstTime = false;
            final ImageView thumb = findViewById(R.id.thumb);
            thumb.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
            thumb.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((RelativeLayout) thumb.getParent()).removeView(thumb);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}
