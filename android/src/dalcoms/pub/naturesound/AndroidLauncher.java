package dalcoms.pub.naturesound;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
    final String tag = "AndroidLauncher";
    final String myPackage = "dalcoms.pub.naturesound";
    final boolean testAdmobAds = false;
    //    final boolean testAdmobAdsInHomePc = true;
    final boolean useFakeReview = false;

    protected AdView adView;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private AdmobFullScreenCallback admobInterstitialCallback;
    private AdmobFullScreenCallback admobRewardedCallback;
    private IAdmobOnUserEarnedReward admobOnUserEarnedReward;
    private boolean mobileAdsInitialCompleted = false;

    ReviewInfo reviewInfo;
    ReviewManager manager;

    private final int SHOW_AD_BANNER = 0;
    private final int HIDE_AD_BANNER = 1;
    private final int LOAD_AD_INTERSTITIAL = 3;
    private final int SHOW_AD_INTERSTITIAL = 4;
    private final int LOAD_AD_REWARD = 5;
    private final int SHOW_AD_REWARD = 6;
    private final int TOAST_MSG = 7;
    private final int MORE_MYAPP = 8;
    private final int SHARE_MYAPP = 9;
    private final int REVIEW_MYAPP = 10;
    private final int VISIT_PLAYSTORE = 11;
    private final int REQ_APP_REVIEW = 12;

    private String stringToast = "";
    private String stringPackageToVisit = "dalcoms.pub.naturesound";

    MyService myService;
    boolean isService = false;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder localBinder = (MyService.LocalBinder) service;
            myService = localBinder.getService();
            isService = true;

            myService.attachNotification();

            Log.d(tag, "onServiceConnected");
//            myService.saySomething("Wow, it's easy");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
            Log.d(tag, "onServiceDisconnected");
        }
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getAction(),
                           Toast.LENGTH_LONG).show();
//            Toast.makeText(context, intent.getAction() + ":" + intent.getExtras().toString(),
//                           Toast.LENGTH_LONG).show();
        }
    };

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_AD_BANNER:
                    setShowAdmobBanner(true);
                    break;
                case HIDE_AD_BANNER:
                    setShowAdmobBanner(false);
                    break;
                case LOAD_AD_INTERSTITIAL:
                    loadAdmobInterstitialAd();
                    break;
                case SHOW_AD_INTERSTITIAL:
                    showAdmobInterstitialAd();
                    break;
                case LOAD_AD_REWARD:
                    loadAdmobRewardedAd();
                    break;
                case SHOW_AD_REWARD:
                    showRewardedAd();
                    break;
                case TOAST_MSG:
                    doToastMsg();
                    break;
                case MORE_MYAPP:
                    doMoreMyApp();
                    break;
                case SHARE_MYAPP:
                    doShareMayApp();
                    break;
                case REVIEW_MYAPP:
                    doReviewMyApp();
                    break;
                case VISIT_PLAYSTORE:
                    visitPlayStore();
                    break;
                case REQ_APP_REVIEW:
                    requestMyAppReview();
                    break;
                default:
                    Log.d(tag, "Wrong msg.what=" + msg.what);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout layout = getLayout();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View gameView = createGameView();
//        setViewToFullScreen();

        adView = createAdView(getResources().getString(R.string.admob_unit_id_banner));

        layout.addView(gameView, ViewGroup.LayoutParams.WRAP_CONTENT,
                       ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(adView);

        setContentView(layout);

        if (testAdmobAds) {
            List<String> testDeviceIds
                    = Arrays.asList("9B86AC393DDD244C7CE325826EF04268");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }

        MobileAds.initialize(this,
                             new OnInitializationCompleteListener() {
                                 @Override
                                 public void onInitializationComplete(
                                         InitializationStatus initializationStatus) {
                                     Log.d(tag,
                                           "onInitializationComplete : " +
                                           initializationStatus.toString());
                                     mobileAdsInitialCompleted = true;
                                     loadAdmobBanner(adView);
                                 }

                             });


        initReviewObjects();
        initMyService();
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (adView != null) {
            adView.resume();
        }
        setViewToFullScreen();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        if (myService != null) {
            myService.stopAllMusic();
        }
        unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    private void initMyService() {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        intent.putExtra("command","command");
//        intent.putExtra("name","Who are you, ");
//        startService(myServiceIntent);
        registerMyBroadcast();
    }

    private void registerMyBroadcast() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(CommandActions.NOTI_START_PAUSE);
        registerReceiver(broadcastReceiver, iFilter);
    }

    private void initReviewObjects() {
        if (useFakeReview) {
            manager = new FakeReviewManager(this);
        } else {
            manager = ReviewManagerFactory.create(this);
        }

    }

    private RelativeLayout getLayout() {
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        return layout;
    }

    private AdSize getAdmobBannerSize() {
        //Large, Medium 은 생각보다 너무 크다. ಥ_ಥ
        AdSize bannerSize;
        switch (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
//            case 12:
//            case 18:
//            case 8:
//                bannerSize = AdSize.LARGE_BANNER;
//                break;
//            case 20:
//                bannerSize = AdSize.MEDIUM_RECTANGLE;
//                break;
            case 7:
            case 23:
            case 3:
                bannerSize = AdSize.BANNER;
                break;
            default:
                bannerSize = getFullWidthAdaptiveSize();
                break;
        }


        return bannerSize;
    }

    private AdSize getFullWidthAdaptiveSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private AdView createAdView(String unitId) {
        adView = new AdView(this);

        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);

        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        adView.setAdSize(getAdmobBannerSize());
        adView.setAdUnitId(unitId);
        adView.setBackgroundColor(Color.TRANSPARENT);

        adView.setVisibility(View.GONE);

        adView.setLayoutParams(adParams);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.v(tag, "Admob banner : onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.v(tag, "Admob banner : onAdFailedToLoad : " + loadAdError.toString());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.v(tag, "Admob banner : onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.v(tag, "Admob banner : onAdLoaded");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.v(tag, "Admob banner : onAdClicked");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.v(tag, "Admob banner : onAdImpression");
            }
        });

        return adView;
    }

    private void showRewardedAd() {
        if (isAdmobRewardedLoaded()) {
            mRewardedAd.show(AndroidLauncher.this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    Log.d(tag, "The user earned the reward.");
                    if (admobOnUserEarnedReward != null) {
                        admobOnUserEarnedReward
                                .onUserEarnedReward(rewardItem.getAmount(), rewardItem.getType());
                    }
                }
            });
        } else {
            Log.d(tag, "The rewarded ad wasn't ready yet.");
        }
    }

    private void showAdmobInterstitialAd() {
        if (isAdmobInterstitialLoaded()) {
            mInterstitialAd.show(AndroidLauncher.this);
        } else {
            Log.d(tag, "The interstitial ad wasn't ready yet.");
        }
    }

    private void loadAdmobRewardedAd() {
        RewardedAd.load(AndroidLauncher.this,
                        getContext().getResources().getString(
                                R.string.admob_unit_id_reward),
                        new AdRequest.Builder().build(),
                        new RewardedAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                mRewardedAd = rewardedAd;
                                Log.d(tag, "Ad was loaded.");
                                if (admobRewardedCallback != null) {
                                    admobRewardedCallback.onAdLoaded();
                                }
                                mRewardedAd.setFullScreenContentCallback(
                                        new FullScreenContentCallback() {
                                            @Override
                                            public void onAdFailedToShowFullScreenContent(
                                                    @NonNull AdError adError) {
                                                if (admobRewardedCallback != null) {
                                                    admobRewardedCallback
                                                            .onAdFailedToShowFullScreenContent(
                                                                    adError.getMessage());
                                                }
                                            }

                                            @Override
                                            public void onAdShowedFullScreenContent() {
                                                if (admobRewardedCallback != null) {
                                                    mRewardedAd = null;
                                                    admobRewardedCallback
                                                            .onAdShowedFullScreenContent();
                                                }
                                            }

                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                if (admobRewardedCallback != null) {
                                                    admobRewardedCallback
                                                            .onAdDismissedFullScreenContent();
                                                }
                                            }

                                            @Override
                                            public void onAdImpression() {
                                                if (admobRewardedCallback != null) {
                                                    admobRewardedCallback.onAdImpression();
                                                }
                                            }

                                            @Override
                                            public void onAdClicked() {
                                                if (admobRewardedCallback != null) {
                                                    admobRewardedCallback.onAdClicked();
                                                }
                                            }
                                        });
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.d(tag, loadAdError.getMessage());
                                mRewardedAd = null;
                                if (admobRewardedCallback != null) {
                                    admobRewardedCallback
                                            .onAdFailedToLoad(loadAdError.getMessage());
                                }
                            }
                        }
        );
    }

    private void loadAdmobInterstitialAd() {

        InterstitialAd
                .load(AndroidLauncher.this,
                      getContext().getResources().getString(
                              R.string.admob_unit_id_interstitial),
                      new AdRequest.Builder().build(),
                      new InterstitialAdLoadCallback() {
                          @Override
                          public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                              super.onAdLoaded(interstitialAd);
                              mInterstitialAd = interstitialAd;
                              mInterstitialAd.setFullScreenContentCallback(
                                      new FullScreenContentCallback() {
                                          @Override
                                          public void onAdFailedToShowFullScreenContent(
                                                  @NonNull AdError adError) {
                                              if (admobInterstitialCallback != null) {
                                                  admobInterstitialCallback
                                                          .onAdFailedToShowFullScreenContent(
                                                                  adError.getMessage());
                                              }
                                          }

                                          @Override
                                          public void onAdShowedFullScreenContent() {
                                              if (admobInterstitialCallback != null) {
                                                  mInterstitialAd = null;
                                                  admobInterstitialCallback
                                                          .onAdShowedFullScreenContent();
                                              }
                                          }

                                          @Override
                                          public void onAdDismissedFullScreenContent() {
                                              if (admobInterstitialCallback != null) {
                                                  admobInterstitialCallback
                                                          .onAdDismissedFullScreenContent();
                                              }
                                          }

                                          @Override
                                          public void onAdImpression() {
                                              if (admobInterstitialCallback != null) {
                                                  admobInterstitialCallback.onAdImpression();
                                              }
                                          }

                                          @Override
                                          public void onAdClicked() {
                                              if (admobInterstitialCallback != null) {
                                                  admobInterstitialCallback.onAdClicked();
                                              }
                                          }
                                      });
                              if (admobInterstitialCallback != null) {
                                  admobInterstitialCallback.onAdLoaded();
                              }
                          }

                          @Override
                          public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                              mInterstitialAd = null;
                              if (admobInterstitialCallback != null) {
                                  admobInterstitialCallback
                                          .onAdFailedToLoad(loadAdError.getMessage());
                              }
                          }
                      }
                );

    }

    private void setShowAdmobBanner(boolean isShow) {

        try {
            adView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View createGameView() {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = true;
        config.useCompass = true;
        config.useGyroscope = false;
        config.useWakelock = true;

        return initializeForView(new NatureSound(this), config);
    }

    private void setViewToFullScreen() {
        View decorView = getWindow().getDecorView();
//        int uiOption = getWindow().getDecorView().getSystemUiVisibility();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        uiOption |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//        decorView.setSystemUiVisibility(uiOption);

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void loadAdmobBanner(final AdView adView) {
        adView.loadAd(new AdRequest.Builder().build());
    }


    @Override
    public void showAdmobBanner(boolean show) {
        handler
                .sendEmptyMessage(show ? SHOW_AD_BANNER : HIDE_AD_BANNER);
    }

    @Override
    public void loadAdmobInterstitial(AdmobFullScreenCallback adCallback) {
        this.admobInterstitialCallback = adCallback;
        handler.sendEmptyMessage(LOAD_AD_INTERSTITIAL);
    }

    @Override
    public void showAdmobInterstitial() {
        handler.sendEmptyMessage(SHOW_AD_INTERSTITIAL);
    }

    @Override
    public void loadAdmobReward(AdmobFullScreenCallback adCallback) {
        this.admobRewardedCallback = adCallback;
        handler.sendEmptyMessage(LOAD_AD_REWARD);
    }

    @Override
    public void showAdmobReward(IAdmobOnUserEarnedReward earnedReward) {
        this.admobOnUserEarnedReward = earnedReward;
        handler.sendEmptyMessage(SHOW_AD_REWARD);
    }

    @Override
    public void toastMessage(String message) {
        this.stringToast = message;
        handler.sendEmptyMessage(TOAST_MSG);
    }

    @Override
    public void actionMoreMyApp() {
        handler.sendEmptyMessage(MORE_MYAPP);
    }

    @Override
    public void actionShareMyApp() {
        handler.sendEmptyMessage(SHARE_MYAPP);
    }

    @Override
    public void actionReviewMyApp() {
        handler.sendEmptyMessage(REVIEW_MYAPP);
    }

    @Override
    public void actionVisitPlayStore(String pkg) {
        stringPackageToVisit = pkg;
        handler.sendEmptyMessage(VISIT_PLAYSTORE);
    }

    @Override
    public void requestAppReview() {
        handler.sendEmptyMessage(REQ_APP_REVIEW);
    }

    @Override
    public boolean isAdmobInterstitialLoaded() {
        return mInterstitialAd != null;
    }

    @Override
    public boolean isAdmobRewardedLoaded() {
        return mRewardedAd != null;
    }

    @Override
    public boolean isMobileAdsInitializationCompleted() {
        return mobileAdsInitialCompleted;
    }

    @Override
    public void playMusic(int index, boolean isPlay, float volume) {
        myService.playMusic(index, isPlay, volume);
    }

    @Override
    public int getTimerTimeSec() {
        return myService.getTimerTimeSec();
    }

    @Override
    public void setTimerTimeSec(int timeSec) {
        myService.setTimerTimeSec(timeSec);
    }

    @Override
    public void setTimerTimeSec(int timeSec, boolean timerMode) {
        myService.setTimerTimeSec(timeSec,timerMode);
    }

    @Override
    public void setTimerMode(boolean timerMode) {
        myService.setTimerMode(timerMode);
    }

    @Override
    public boolean isTimerMode() {
        return myService.isTimerMode();
    }

    @Override
    public void setVolume(int soundIndex, float volume) {
        myService.setVolume(soundIndex, volume);
    }

    private void doMoreMyApp() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://play.google.com/store/apps/developer?id=Dalcoms"));
        intent.setPackage("com.android.vending");
        startActivity(intent);
    }

    private void doShareMayApp() {
        final String shareText = "Nature Meditation Sounds";
        final String appUri = "https://play.google.com/store/apps/details?id=" + myPackage;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                            shareText + " ♡ ♥ " + appUri);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Nature Meditation Sounds");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, "");
        sendIntent.putExtra(Intent.EXTRA_CC, "");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share Nature Meditation Sounds"));
    }

    private void doReviewMyApp() {
        final String appUri = "https://play.google.com/store/apps/details?id=" + myPackage;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(appUri));
        intent.setPackage("com.android.vending");
        startActivity(intent);
    }

    private void visitPlayStore() {
        final String appUri =
                "https://play.google.com/store/apps/details?id=" + this.stringPackageToVisit;

        Intent launchIntent =
                getPackageManager().getLaunchIntentForPackage(this.stringPackageToVisit);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(appUri));
            intent.setPackage("com.android.vending");
            startActivity(intent);
        }
    }

    private void requestMyAppReview() {
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    reviewInfo = task.getResult();
                    launchRevieFlow(reviewInfo);

                } else {
                    // There was some problem, continue regardless of the result.
                    Gdx.app.log(tag, "Review fail" + task.getResult());
                }
            }
        });
    }

    private void launchRevieFlow(ReviewInfo reviewInfo) {
//        Gdx.app.log(tag, "RevieInfo" + reviewInfo.toString());
        manager.launchReviewFlow(this, reviewInfo).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Gdx.app.log(tag, "Review : launchReviewFlow : onFailure" + e);
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                Gdx.app.log(tag, "Review : launchReviewFlow : onComplete : " +
                                 task.getResult());
            }
        });
    }


    private void doToastMsg() {
        Toast.makeText(this, this.stringToast, Toast.LENGTH_SHORT).show();
    }


}
