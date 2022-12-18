package dalcoms.pub.naturesound;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NatureSound extends Game {
    final String tag = "NatureSound";
    private SpriteBatch batch;
    private GameConfiguration gameConfiguration;
    final private IActivityRequestHandler androidLauncherHandler;
    final private AssetManager assetManager;
    final private TextureLoader.TextureParameter para;
    final private FPSLogger fpsLogger;

    final boolean FPS_LOGGING = false;
    final boolean REMOVE_PREFERENCES = false;

    private int retryCountAdmobInterstitial = 0;
    private int retryCountAdmobReward = 0;

    final int ADMOB_SCREEN_ADS_RETRY_CNT = 5;

    private Array<String> assetPathTextureHome;
//    private Array<String> assetPathSoundHome;

    public NatureSound(IActivityRequestHandler handler) {
        androidLauncherHandler = handler;
        assetManager = new AssetManager();

        para = new TextureLoader.TextureParameter();
        para.minFilter = Texture.TextureFilter.Linear; // 축소필터
        para.magFilter = Texture.TextureFilter.Linear; // 확대필터

        fpsLogger = new FPSLogger();
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        gameConfiguration = GameConfiguration.getInstance();
        gameConfiguration.setViewportSize(1080f, 1920f, true);
        logViewPortSize();

        if (REMOVE_PREFERENCES) {
            gameConfiguration.clearAllGamePreferences();
        }

        loadAdmobAds();

        loadAssets();

        gotoSplashScreen("img/splashImg.png", Texture.class);
    }

    @Override
    public void render() {
        super.render();
        if (!assetManager.update()) {
            Gdx.app.log(tag, "asset : " + assetManager.getProgress() * 100 + "%");
        }
        if (FPS_LOGGING) {
            this.fpsLogger.log();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }

    private void gotoSplashScreen(String assetTobeLoading, Class classType) {
        int i = 0;
        do {
            Gdx.app.log(tag, "wait to gotoSplash : count = " + i);
            i++;
        } while (!assetManager.isLoaded(assetTobeLoading, classType));

        Gdx.app.log(tag, "gotoSplash ->");
        setScreen(new SplashScreen(this));
    }

    private void logViewPortSize() {
        Gdx.app.log(tag, "Graphic width = " + (float) Gdx.graphics.getWidth() + ", Height = " +
                         (float) Gdx.graphics.getHeight());
        Gdx.app.log(tag,
                    "Viewport width = " + getViewportWidth() + ", Height = " + getViewportHeight());
    }

    private void loadAssets() {
        initAssetPathTextureHome();
//        initAssetPathSoundHome();

        loadAssetsSplashScreen();
        loadAssetHomeScreen();
    }

    private void initAssetPathTextureHome() {
        this.assetPathTextureHome = new Array<>();
    }

//    private void initAssetPathSoundHome() {
//        this.assetPathSoundHome = new Array<>();
//        assetPathSoundHome.add("sound/bgSoundBell.mp3");
//        assetPathSoundHome.add("sound/bgSoundBirds.mp3");
//        assetPathSoundHome.add("sound/bgSoundFire.mp3");
//        assetPathSoundHome.add("sound/bgSoundOwl.mp3");
//        assetPathSoundHome.add("sound/bgSoundRain.mp3");
//        assetPathSoundHome.add("sound/bgSoundStream.mp3");
//        assetPathSoundHome.add("sound/bgSoundWaves.mp3");
//        assetPathSoundHome.add("sound/bgSoundWind.mp3");
//    }

    public Array<String> getAssetPathTextureHome() {
        return assetPathTextureHome;
    }

//    public Array<String> getAssetPathSoundHome() {
//        return assetPathSoundHome;
//    }

    private void loadAssetsSplashScreen() {
        assetManager.load("img/title_splash.png", Texture.class, para);
        assetManager.finishLoadingAsset("img/title_splash.png");
        assetManager.load("img/splashImg.png", Texture.class, para);
        assetManager.finishLoadingAsset("img/splashImg.png");
    }

    private void loadAssetHomeScreen() {
        assetManager.load("img/homeBg.png", Texture.class, para);
        assetManager.load("img/ico_leaf.png", Texture.class, para);
        assetManager.load("img/homeTitle.png", Texture.class, para);

        assetManager.load("img/btn_music.png", Texture.class, para);
        assetManager.load("img/btn_bell.png", Texture.class, para);
        assetManager.load("img/btn_bird.png", Texture.class, para);
        assetManager.load("img/btn_fire.png", Texture.class, para);
        assetManager.load("img/btn_frog.png", Texture.class, para);
        assetManager.load("img/btn_meditation.png", Texture.class, para);
        assetManager.load("img/btn_owl.png", Texture.class, para);
        assetManager.load("img/btn_rain.png", Texture.class, para);
        assetManager.load("img/btn_stream.png", Texture.class, para);
        assetManager.load("img/btn_wave.png", Texture.class, para);

        assetManager.load("img/btn_study.png", Texture.class, para);
        assetManager.load("img/btn_sleep.png", Texture.class, para);
        assetManager.load("img/btn_whitenoise.png", Texture.class, para);

        assetManager.load("img/rect4x4.png", Texture.class, para);

        assetManager.load("img/btn_loop.png", Texture.class, para);
        assetManager.load("img/btn_timer.png", Texture.class, para);
        assetManager.load("img/btn_volume.png", Texture.class, para);
        assetManager.load("img/btn_moreapps.png", Texture.class, para);

        assetManager.load("img/bgVolume.png", Texture.class, para);

        //==Timer numbers
        assetManager.load("img/timerNum0.png", Texture.class, para);
        assetManager.load("img/timerNum1.png", Texture.class, para);
        assetManager.load("img/timerNum2.png", Texture.class, para);
        assetManager.load("img/timerNum3.png", Texture.class, para);
        assetManager.load("img/timerNum4.png", Texture.class, para);
        assetManager.load("img/timerNum5.png", Texture.class, para);
        assetManager.load("img/timerNum6.png", Texture.class, para);
        assetManager.load("img/timerNum7.png", Texture.class, para);
        assetManager.load("img/timerNum8.png", Texture.class, para);
        assetManager.load("img/timerNum9.png", Texture.class, para);
        assetManager.load("img/timerNumColon.png", Texture.class, para);

        assetManager.load("img/compassSmall.png", Texture.class, para);

        assetManager.load("img/circle222.png", Texture.class, para);
        assetManager.load("img/circle82.png", Texture.class, para);
        assetManager.load("img/circle60.png", Texture.class, para);
        assetManager.load("img/rect580x16.png", Texture.class, para);

        assetManager.load("img/textVolumeSetting.png", Texture.class, para);
        assetManager.load("img/icoWhiteNoise.png", Texture.class, para);
        assetManager.load("img/icoSleep.png", Texture.class, para);
        assetManager.load("img/icoStudy.png", Texture.class, para);
        assetManager.load("img/icoMeditation.png", Texture.class, para);
        assetManager.load("img/icoFrog.png", Texture.class, para);
        assetManager.load("img/icoRain.png", Texture.class, para);
        assetManager.load("img/icoBirds.png", Texture.class, para);
        assetManager.load("img/icoStream.png", Texture.class, para);
        assetManager.load("img/icoBell.png", Texture.class, para);
        assetManager.load("img/icoFire.png", Texture.class, para);
        assetManager.load("img/icoOwl.png", Texture.class, para);
        assetManager.load("img/icoWaves.png", Texture.class, para);
        assetManager.load("img/icoMusicBox.png", Texture.class, para);
        assetManager.load("img/rect1080x220.png", Texture.class, para);
        assetManager.load("img/btn_cancle.png", Texture.class, para);
        assetManager.load("img/icoVolume.png", Texture.class, para);

        assetManager.load("img/soundAdIcon.png", Texture.class, para);

        loadAssetMyAds();

//        loadHomeScreenSound();
    }

    public Array<String> getMyAdsBannerTexturePath() {
        Array<String> banners = new Array<>();
        banners.add("img/bannerPindot.png");
        banners.add("img/bannerDotsup.png");
        banners.add("img/bannerBrainwave.png");
        banners.add("img/bannerSkinVibor.png");
        banners.add("img/bannerTeethRoulette.png");
        return banners;
    }

    public enum ADS {
        ADMOB, PINDOT, DOTSUP, BRAINWAVE, SKINVIBOR, MEGABRICKS, TEETHROULETTE, FARTBUTTON,
    }

    public String getMyBannerTexturePath(ADS ads) {
        String texturePath;
        switch (ads) {
            case PINDOT:
                texturePath = "img/bannerPindot.png";
                break;
            case DOTSUP:
                texturePath = "img/bannerDotsup.png";
                break;
            case BRAINWAVE:
                texturePath = "img/bannerBrainwave.png";
                break;
            case SKINVIBOR:
                texturePath = "img/bannerSkinVibor.png";
                break;
            case TEETHROULETTE:
                texturePath = "img/bannerTeethRoulette.png";
                break;
            default:
                Gdx.app.log(tag, "getMyBannerTexturePath() : " + ads.name() +
                                 " is not defined. so return default");
                texturePath = "img/bannerPindot.png";
                break;
        }
        return texturePath;
    }

    public String getMyScreenAdsTexturePath(ADS ads) {
        String texturePath;
        switch (ads) {
            case PINDOT:
                texturePath = "img/screenAdsPindot.png";
                break;
            case DOTSUP:
                texturePath = "img/screenAdsDotsup.png";
                break;
            case BRAINWAVE:
                texturePath = "img/screenAdsBrain.png";
                break;
            case SKINVIBOR:
                texturePath = "img/screenAdsSkinVibor.png";
                break;
            case MEGABRICKS:
                texturePath = "img/screenAdsMegaBricks.png";
                break;
            case TEETHROULETTE:
                texturePath = "img/screenAdsTeethRoulette.png";
                break;
            case FARTBUTTON:
                texturePath = "img/screenAdsFartButton.png";
                break;
            default:
                Gdx.app.log(tag, "getMyBannerTexturePath() : " + ads.name() +
                                 " is not defined. so return default");
                texturePath = "img/screenAdsDotsup.png";
                break;
        }
        return texturePath;
    }

    public String getMyAdsUri(ADS ads) {
        String texturePath;
        switch (ads) {
            case PINDOT:
                texturePath = "dalcoms.game.pindot";
                break;
            case DOTSUP:
                texturePath = "dalcoms.game.dotsup";
                break;
            case BRAINWAVE:
                texturePath = "dalcoms.pub.brainwavestudio";
                break;
            case SKINVIBOR:
                texturePath = "hs.app.skinvibrator";
                break;
            case MEGABRICKS:
                texturePath = "dalcoms.game.magabricksbreaker";
                break;
            case TEETHROULETTE:
                texturePath = "dalcoms.fun.teethroulette";
                break;
            case FARTBUTTON:
                texturePath = "dalcoms.fun.fartsoundbutton";
                break;
            default:
                Gdx.app.log(tag, "getMyAdsUri() : " + ads.name() +
                                 " is not defined. so return default");
                texturePath = "dalcoms.game.pindot";
                break;
        }
        return texturePath;
    }

    public ADS getRandomAds() {
        float rand = (float) Math.random();
        ADS randomAds;

        if (rand < 0.15f) {
            randomAds = ADS.DOTSUP;
        } else if (rand < 0.25f) {
            randomAds = ADS.PINDOT;
        } else if (rand < 0.4f) {
            randomAds = ADS.BRAINWAVE;
        } else if (rand < 0.6f) {
            randomAds = ADS.SKINVIBOR;
        } else if (rand < 0.8f) {
            randomAds = ADS.MEGABRICKS;
        } else if (rand < 0.95f) {
            randomAds = ADS.FARTBUTTON;
        } else {
            randomAds = ADS.TEETHROULETTE;
        }
        return randomAds;
    }

    private int seqAdsIndex = 0;

    public ADS getSequentialAds() {
        final ADS AdsList[] =
                {
                        ADS.MEGABRICKS, ADS.PINDOT, ADS.DOTSUP, ADS.SKINVIBOR, ADS.BRAINWAVE,
                        ADS.TEETHROULETTE, ADS.FARTBUTTON};
        ADS ret = AdsList[seqAdsIndex % AdsList.length];
        seqAdsIndex++;
        return ret;
    }

    private void loadAssetMyAds() {
        for (String path : getMyAdsBannerTexturePath()) {
            assetManager.load(path, Texture.class, para);
        }
        assetManager.load("img/adsCloseButton.png", Texture.class, para);
        assetManager.load("img/screenAdsDotsup.png", Texture.class, para);
        assetManager.load("img/screenAdsPindot.png", Texture.class, para);
        assetManager.load("img/screenAdsBrain.png", Texture.class, para);
        assetManager.load("img/screenAdsSkinVibor.png", Texture.class, para);
        assetManager.load("img/screenAdsMegaBricks.png", Texture.class, para);
        assetManager.load("img/screenAdsTeethRoulette.png", Texture.class, para);
        assetManager.load("img/screenAdsFartButton.png", Texture.class, para);
    }

    public void loadAssetGameScreen() {


        loadGameScreenSound();
    }

    private void loadHomeScreenSound() {
//        for (String path : getAssetPathSoundHome()) {
//            assetManager.load(path, Sound.class);
//            Gdx.app.log(tag, "Load sound " + path);
//        }
    }

    private void loadGameScreenSound() {

    }


    private void loadAssetTextureNum(String pathPrefix, int fromNum, int toNum) {
        for (int i = fromNum; i < toNum + 1; i++) {
            assetManager.load(pathPrefix + String.valueOf(i) + ".png", Texture.class, para);
        }
    }

    /**
     * @param pathPrefix path + prefix of the image file. e.g, "img/fontNameFontSize"
     * @return
     */
    public Array<Texture> getNumTextureArray(String pathPrefix) {
        Array<Texture> numTextureArray = new Array<>();
        for (int i = 0; i < 10; i++) {
            try {
                numTextureArray.add(getAssetManager().get(pathPrefix + String.valueOf(i) + ".png",
                                                          Texture.class));
            } catch (NullPointerException e) {
                numTextureArray.add(getAssetManager()
                                            .get("img/centuryGothic42Num" + String.valueOf(i) +
                                                 ".png",
                                                 Texture.class));
                Gdx.app.log(tag, "getNumTextureArray : " + e.getMessage());
            }
        }
        return numTextureArray;
    }


    public SpriteBatch getSpriteBatch() {
        return this.batch;
    }

    public IActivityRequestHandler getLauncherHandler() {
        return androidLauncherHandler;
    }

    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public float getLocationYFromTop(float lengthFromTop) {
        return getGameConfiguration().getViewportHeight() - lengthFromTop;
    }

    public float getCenterX() {
        return getViewportWidth() / 2f;
    }

    public float getCenterY() {
        return getViewportHeight() / 2f;
    }

    public float getViewportWidth() {
        return getGameConfiguration().getViewportWidth();
    }

    public float getViewportHeight() {
        return getGameConfiguration().getViewportHeight();
    }

    public boolean isAdmobInterAdLoaded() {
        return getLauncherHandler().isAdmobInterstitialLoaded();
    }

    public boolean isAdmobRewardAdLoaded() {
        return getLauncherHandler().isAdmobRewardedLoaded();
    }

    public void loadAdmobInterstitial() {

        final String strLog = "AdmobInterstitial : ";
        getLauncherHandler().loadAdmobInterstitial(new AdmobFullScreenCallback() {
            @Override
            public void onAdLoaded() {
                Gdx.app.log(tag, strLog + "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(String errLoadAd) {
                Gdx.app.log(tag, strLog + "onAdFailedToLoad : " + errLoadAd + ",retry=" +
                                 getRetryCountAdmobInterstitial());
                if (getRetryCountAdmobInterstitial() < ADMOB_SCREEN_ADS_RETRY_CNT) {
                    loadAdmobInterstitial();
                    setRetryCountAdmobInterstitial(getRetryCountAdmobInterstitial() + 1);
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(String errAd) {
                Gdx.app.log(tag, strLog + "onAdFailedToShowFullScreenContent : " + errAd);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                loadAdmobInterstitial();
                Gdx.app.log(tag, strLog + "onAdShowedFullScreenContent");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Gdx.app.log(tag, strLog + "onAdDismissedFullScreenContent");
            }

            @Override
            public void onAdImpression() {
                Gdx.app.log(tag, strLog + "onAdImpression");
            }

            @Override
            public void onAdClicked() {
                Gdx.app.log(tag, strLog + "onAdClicked");
            }
        });
    }

    public void loadAdmobReward() {
        final String strLog = "AdmobRewarded : ";

        getLauncherHandler().loadAdmobReward(new AdmobFullScreenCallback() {
            @Override
            public void onAdLoaded() {
                Gdx.app.log(tag, strLog + "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(String errLoadAd) {
                Gdx.app.log(tag, strLog + "onAdFailedToLoad : " + errLoadAd + "," +
                                 getRetryCountAdmobReward());
                if (getRetryCountAdmobReward() < ADMOB_SCREEN_ADS_RETRY_CNT) {
                    loadAdmobReward();
                    setRetryCountAdmobReward(getRetryCountAdmobReward() + 1);
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(String errAd) {
                Gdx.app.log(tag, strLog + "onAdFailedToShowFullScreenContent : " + errAd);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                loadAdmobReward();
                Gdx.app.log(tag, strLog + "onAdShowedFullScreenContent");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Gdx.app.log(tag, strLog + "onAdDismissedFullScreenContent");
            }

            @Override
            public void onAdImpression() {
                Gdx.app.log(tag, strLog + "onAdImpression");
            }

            @Override
            public void onAdClicked() {
                Gdx.app.log(tag, strLog + "onAdClicked");
            }
        });
    }

    /**
     * Load interstitial and rewarded.
     */
    private void loadAdmobAds() {
        if (getLauncherHandler().isMobileAdsInitializationCompleted()) {
            loadAdmobInterstitial();
            loadAdmobReward();
        }
    }

    public int getRetryCountAdmobInterstitial() {
        return retryCountAdmobInterstitial;
    }

    public void setRetryCountAdmobInterstitial(int retryCountAdmobInterstitial) {
        this.retryCountAdmobInterstitial = retryCountAdmobInterstitial;
    }

    public int getRetryCountAdmobReward() {
        return retryCountAdmobReward;
    }

    public void setRetryCountAdmobReward(int retryCountAdmobReward) {
        this.retryCountAdmobReward = retryCountAdmobReward;
    }
}