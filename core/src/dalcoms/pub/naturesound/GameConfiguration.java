package dalcoms.pub.naturesound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Date;

public class GameConfiguration {
    private static final String tag = "GameConfiguration";
    private static final GameConfiguration instance = new GameConfiguration();

    Preferences preferences = Gdx.app.getPreferences("dalcoms.pub.naturesound.preference");

    private final boolean initialPublishing = true;

    private final String prefKey_ExeCount = "exeCount";
    private final String prefKey_Date = "date";
    private final String prefKey_MusicOnOff = "musicOnOff";
    private final String prefKey_SoundOnOff = "soundOnOff";
    private final String prefKey_MyAdShowDate = "myAdShowDate";

    private static final boolean TEST_ = false;
    private static final boolean TEST_MAX_LEVEL = false;
    private static final int TEST_CLEARED_LEVEL = 64;
    private static final int MAX_GAME_LEVEL = 2000;

    private static final boolean SHOW_BANNER_AD = true;

    private float viewportWidth = 1080f;
    private float viewportHeight = 1920f;
    private float REF_HperW = 1.64f;
    private float HperW;

    private float physicsWorldWidth = 10.8f;
    private float physicsWorldHeight = 21.435f;
    private float physicScreenRatio = 0.01f;

    private int gamePlayCount = 0;
    private int sameLevelRetryCount = 0;
    private int afterInterstitialAdCount = 0; //Clear to 0 as InterAd popup
    private int homeScreenCount = 0;
    private int soundPlayScreenCount = 0;


    static GameConfiguration getInstance() {
        return instance;
    }

    private GameConfiguration() {
    }

    public void clearAllGamePreferences() {
        preferences.clear();
        flushingPreferences();
    }

    public boolean isTestMode() {
        return TEST_;
    }


    public int getExeCount() {
        if (isTestMode()) {
            return TEST_MAX_LEVEL ? MAX_GAME_LEVEL : TEST_CLEARED_LEVEL;
        } else {
            return preferences.getInteger(prefKey_ExeCount, 0);
        }
    }

    public boolean isFirstGame() {
        return preferences.getInteger(prefKey_ExeCount, 0) == 0;
    }

    public void putExeCount(int count) {
        preferences.putInteger(prefKey_ExeCount, count);
    }

    public void putExeCount(int count, boolean flushing) {
        preferences.putInteger(prefKey_ExeCount, count);
        if (flushing) {
            flushingPreferences();
        }
    }

    public Date getLevelClearedDate(int level) {
        long dateLong = preferences.getLong(prefKey_Date + level, 0);
        //milliseconds
        return new Date(dateLong);
    }

    public void putLevelClearedDate(int level, long dateMilliseconds) {
        preferences.putLong(prefKey_Date + level, dateMilliseconds);
    }

    public void putMyAdShowDateNow() {
        preferences.putLong(prefKey_MyAdShowDate, TimeUtils.millis());
    }

    public void putMyAdShowDateNow(boolean flushing) {
        preferences.putLong(prefKey_MyAdShowDate, TimeUtils.millis());
        if (flushing) {
            flushingPreferences();
        }
    }

    public Date getMyAdShowDate() {
        long dateLong = preferences.getLong(prefKey_MyAdShowDate, 0);
        //milliseconds
        return new Date(dateLong);
    }

    public long getMyAdShowDateMillis() {
        return preferences.getLong(prefKey_MyAdShowDate, 0);
    }

    public boolean getMusicOnOff() {
        return preferences.getBoolean(prefKey_MusicOnOff, true);
    }

    public void putMusicOnOff(boolean onoff) {
        preferences.putBoolean(prefKey_MusicOnOff, onoff);
    }

    public boolean getSoundOnOff() {
        return preferences.getBoolean(prefKey_SoundOnOff, true);
    }

    public void putSoundOnOff(boolean onoff) {
        preferences.putBoolean(prefKey_SoundOnOff, onoff);
    }

    public void flushingPreferences() {
        preferences.flush();
    }


    public int getGamePlayCount() {
        return gamePlayCount;
    }

    public void setGamePlayCount(int gamePlayCount) {
        this.gamePlayCount = gamePlayCount;
    }


    public int increaseGamePlayCount() {
        setGamePlayCount(getGamePlayCount() + 1);
        return getGamePlayCount();
    }

    public int getHomeScreenCount() {
        return homeScreenCount;
    }

    public void setHomeScreenCount(int homeScreenCount) {
        this.homeScreenCount = homeScreenCount;
    }
    public void incHomeScreenCount() {
        setHomeScreenCount(getHomeScreenCount() + 1);
    }

    public int getSameLevelRetryCount() {
        return sameLevelRetryCount;
    }

    public void setSameLevelRetryCount(int sameLevelRetryCount) {
        this.sameLevelRetryCount = sameLevelRetryCount;
    }

    public int getAfterInterstitialAdCount() {
        return afterInterstitialAdCount;
    }

    public void setAfterInterstitialAdCount(int afterInterstitialAdCount) {
        this.afterInterstitialAdCount = afterInterstitialAdCount;
    }

    public float getHperW() {
        return HperW;
    }

    public void setHperW(float hperW) {
        HperW = hperW;
    }


    public boolean isSHOW_BANNER_AD() {
        return SHOW_BANNER_AD;
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public void setViewportWidth(float viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public void setViewportHeight(float viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public void setViewportSize(float width, float height, boolean isResetH) {
        setViewportWidth(width);
        setViewportHeight(height);
        if (isResetH) {
            if (((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()) >=
                this.REF_HperW) {
                setViewportHeight(
                        Gdx.graphics.getHeight() / (Gdx.graphics.getWidth() / viewportWidth));
            }
        }
    }

    public void setPhysicsWorldSize(float worldWidth) {
        this.physicsWorldWidth = worldWidth;
        this.physicScreenRatio = this.physicsWorldWidth / getViewportWidth();
        this.physicsWorldHeight = this.physicScreenRatio * this.viewportHeight;

        Gdx.app.log(tag, "Physics world size : ratio=" + physicScreenRatio + ",width=" +
                         physicsWorldWidth + ",height=" + physicsWorldHeight);
    }

    public float getPhysicsWorldWidth() {
        return physicsWorldWidth;
    }

    public void setPhysicsWorldWidth(float physicsWorldWidth) {
        this.physicsWorldWidth = physicsWorldWidth;
    }

    public float getPhysicsWorldHeight() {
        return physicsWorldHeight;
    }

    public void setPhysicsWorldHeight(float physicsWorldHeight) {
        this.physicsWorldHeight = physicsWorldHeight;
    }

    public float getPhysicScreenRatio() {
        return physicScreenRatio;
    }

    public void setPhysicScreenRatio(float physicScreenRatio) {
        this.physicScreenRatio = physicScreenRatio;
    }

    public boolean isInitialPublishing() {
        return initialPublishing;
    }
}
