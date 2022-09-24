package dalcoms.pub.naturesound;

interface AdmobFullScreenCallback {
    void onAdLoaded();
    void onAdFailedToLoad(String errLoadAd);
    void onAdFailedToShowFullScreenContent(String errAd);
    void onAdShowedFullScreenContent();
    void onAdDismissedFullScreenContent();
    void onAdImpression();
    void onAdClicked();
}

