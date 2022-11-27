package dalcoms.pub.naturesound;

public interface IActivityRequestHandler {
    void showAdmobBanner(boolean show);

    void loadAdmobInterstitial(AdmobFullScreenCallback adCallback);

    void showAdmobInterstitial();

    void loadAdmobReward(AdmobFullScreenCallback adCallback);

    void showAdmobReward(IAdmobOnUserEarnedReward earnedReward);

    void toastMessage(String message);

    void actionMoreMyApp();

    void actionShareMyApp();

    void actionReviewMyApp();

    void actionVisitPlayStore(String pkg);

    void requestAppReview();

    boolean isAdmobInterstitialLoaded();

    boolean isAdmobRewardedLoaded();

    boolean isMobileAdsInitializationCompleted();

    void playMusic(int index, boolean isPlay, float volume);

    int getTimerTimeSec();

    void setTimerTimeSec(int timeSec);

    void setTimerTimeSec(int timeSec, boolean timerMode);

    void setTimerMode(boolean timerMode);//false : loop, true : timer mode

    boolean isTimerMode();

    void setVolume(int soundIndex, float volume);

}