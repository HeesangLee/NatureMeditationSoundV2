package dalcoms.pub.naturesound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

import dalcoms.lib.libgdx.GameGestureListener;
import dalcoms.lib.libgdx.GameObject;
import dalcoms.lib.libgdx.GameTimer;
import dalcoms.lib.libgdx.IGestureInput;
import dalcoms.lib.libgdx.Point2DFloat;
import dalcoms.lib.libgdx.Renderable;
import dalcoms.lib.libgdx.SpriteGameObject;
import dalcoms.lib.libgdx.SpriteSimpleButton;
import dalcoms.lib.libgdx.SpriteSimpleToggleButton;
import dalcoms.lib.libgdx.Var4TimePair;
import dalcoms.lib.libgdx.VariationPerTime;
import dalcoms.lib.libgdx.easingfunctions.EaseBounceOut;
import dalcoms.lib.libgdx.easingfunctions.EaseCircIn;
import dalcoms.lib.libgdx.easingfunctions.EaseCircOut;
import dalcoms.lib.libgdx.easingfunctions.EaseCubicIn;
import dalcoms.lib.libgdx.easingfunctions.EaseCubicInOut;
import dalcoms.lib.libgdx.easingfunctions.EaseElasticInOut;
import dalcoms.lib.libgdx.easingfunctions.EaseElasticOut;
import dalcoms.lib.libgdx.easingfunctions.EaseLinear;
import dalcoms.lib.libgdx.easingfunctions.IEasingFunction;

class HomeScreen implements Screen, GameTimer.EventListener {
    final String tag = "Home Screen";
    final NatureSound game;
    OrthographicCamera camera;
    Viewport viewport;
    GameTimer gameTimer;
    private Array<Renderable> renderables, renderablesTop;
    private Array<IGestureInput> gestureDetectables;
    private Array<IGestureInput> gestureDetectablesTop;
    private boolean gestureDetectTop = false;

    Array<Renderable> renderablesVolumePanel;
    Array<IGestureInput> gesturesVolumePanel;

    private boolean flagFirstGame = true;
    private boolean checkAdmobAdsLoaded = false;
    private boolean isShowMyScreenAdsOn = false;
    private boolean compassAvailable = false;

    private float myScreenAdsOnTime = 0;
    SpriteSimpleButton ssbMyScreenAdsDisplay, ssbMyScreenAdsCancelBtn;
    SpriteSimpleButton ssbMoreMyApps;
    SpriteGameObject sgoMyScreenAdsCancelBg;
    SpriteTimeHMS sthmsTimer;
    SliderX playerTimerSlider;
    SpriteGameObject sgoCompassSmall;
    Array<SoundButton> soundButtons;

    private int exeCount = 0;

    private boolean isScreenAdsOnTime = false;

    private float bgColorR = 0f, bgColorG = 0f, bgColorB = 0f, bgColorA = 1f;

    IAdmobOnUserEarnedReward admobOnUserEarnedReward;

    public HomeScreen(final NatureSound game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, game.getGameConfiguration().getViewportWidth(),
                          game.getGameConfiguration().getViewportHeight());
        this.viewport = new FitViewport(game.getGameConfiguration().getViewportWidth(),
                                        game.getGameConfiguration().getViewportHeight(),
                                        camera);
        Gdx.app.log(tag,
                    "camera:Orthographic,viewport width=" + viewport.getWorldWidth() + ",height=" +
                    viewport.getWorldHeight());
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        loadBgColor();
        loadExeCount();
    }

    @Override
    public void show() {
        renderables = new Array<>();
        renderablesTop = new Array<>();
        gestureDetectables = new Array<>();
        gestureDetectablesTop = new Array<>();
        initAdmobEarnedReward();
        setGameTimer();
        initSound();
        initGameObjects();
        setInputProcessor();
        showAdmobBanner();

    }

    @Override
    public void render(float delta) {
        draw(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        Gdx.input.cancelVibrate();
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

    private void loadExeCount() {
        setExeCount(game.getGameConfiguration().getExeCount());
        game.getGameConfiguration().putExeCount(getExeCount() + 1, true);
        if (getExeCount() > 0) {
            launchReviewMyApp();
        }
    }

    private float getFloatColor255(float colorInt) {
        return colorInt / 255f;
    }

    private void loadBgColor() {
        //load color from config or set color to default.
        bgColorR = getFloatColor255(247f);
        bgColorG = getFloatColor255(245f);
        bgColorB = getFloatColor255(229f);
        bgColorA = getFloatColor255(255f);
    }

    private void showAdmobBanner() {
        if (!game.getGameConfiguration().isInitialPublishing()) {
            game.getLauncherHandler()
                .showAdmobBanner(game.getGameConfiguration().isSHOW_BANNER_AD());
        }
    }

    private void draw(float delta) {
        Gdx.gl.glClearColor(bgColorR, bgColorG, bgColorB, bgColorA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();

        for (Renderable renderable : renderables) {
            renderable.render(delta);
        }
        for (Renderable renderable : renderablesTop) {
            renderable.render(delta);
        }

        game.getSpriteBatch().end();
    }

    private void initSound() {
//        soundGameStart = game.getAssetManager().get("sound/gameStart.ogg", Sound.class);
//
//        soundGameStart.play();
//
//        this.fartSoundsList = game.getFartSoundsPath();
    }

    private void initGameObjects() {
        initBg();
        initTitle();
        initVolumePanel();
        initSettingPanel();
        initSoundButtons();
    }

    private void initSettingTimer(Point2DFloat loc) {
        final SpriteSimpleToggleButton sstbTimer = new SpriteSimpleToggleButton(
                game.getAssetManager().get("img/btn_loop.png", Texture.class),
                game.getAssetManager().get("img/btn_timer.png", Texture.class),
                viewport, this.game.getSpriteBatch(),
                loc.getX(), loc.getY());

        sstbTimer.setScale(0.1f);
        sstbTimer.scale(1f, 1f, 1.4f, EaseCubicIn.getInstance());


        sstbTimer.setEventListenerTab(new GameGestureListener.TabEventListener() {
            @Override
            public void onEvent(float v, float v1, int i, int i1) {
                if (sstbTimer.isToggleOnTap()) {
                    Gdx.app.log(tag, "sstbTimer is toggled to " + sstbTimer.getBtnToggleState());
                }
                if (sstbTimer.getBtnToggleState() == SpriteSimpleToggleButton.ButtonState.TOGGLED) {
                    showTimerTimeSlider();
                    hideMoreMyApps();
                    hideCompassSmall();
                } else {
                    hideTimerTimeSlider();
                    showMoreMyApps();
                    showCompassSmall();
                }
            }
        });

        renderables.add(sstbTimer);
        gestureDetectables.add(sstbTimer);
    }


    private void showVolumePanel() {
        for (Renderable renderable : renderablesVolumePanel) {
            renderablesTop.add(renderable);
        }
        for (IGestureInput gestureInput : gesturesVolumePanel) {
            gestureDetectablesTop.add(gestureInput);
        }

        setGestureDetectTop(true);
    }

    private void hideVolmePanel() {
        for (Renderable renderable : renderablesVolumePanel) {
            safeRemoveRenderableTop(renderable);
        }
        for (IGestureInput gestureInput : gesturesVolumePanel) {
            gestureDetectablesTop.removeValue(gestureInput, true);
        }

        setGestureDetectTop(false);
    }

    private void initVolumePanelBg() {
        SpriteGameObject sgoVolumePanelBg = new SpriteGameObject(
                game.getAssetManager().get("img/bgVolume.png", Texture.class), 0, 0)
                .setSpriteBatch(game.getSpriteBatch());
        SpriteGameObject sgoTitleBg = new SpriteGameObject(
                game.getAssetManager().get("img/rect1080x220.png", Texture.class),
                0, game.getLocationYFromTop(220f))
                .setSpriteBatch(game.getSpriteBatch());

        sgoTitleBg.setColor(new Color(0x00000012));

        renderablesVolumePanel.add(sgoVolumePanelBg, sgoTitleBg);
    }

    private void initVolumePanelHead() {
        SpriteGameObject sgoIcon = new SpriteGameObject(
                game.getAssetManager().get("img/icoVolume.png", Texture.class), 40f, 0)
                .setSpriteBatch(game.getSpriteBatch());

        SpriteGameObject sgoTitle = new SpriteGameObject(
                game.getAssetManager().get("img/textVolumeSetting.png", Texture.class), 0, 0)
                .setSpriteBatch(game.getSpriteBatch());

        SpriteSimpleButton ssbCancel = new SpriteSimpleButton(
                game.getAssetManager().get("img/btn_cancle.png", Texture.class), viewport,
                game.getSpriteBatch(), 920f, 0);

        ssbCancel.setEventListenerTab(new GameGestureListener.TabEventListener() {
            @Override
            public void onEvent(float v, float v1, int i, int i1) {
                hideVolmePanel();
            }
        });

        sgoIcon.setCenterLocationY(game.getLocationYFromTop(220f / 2f));
        sgoTitle.setCenterLocationX(game.getCenterX());
        sgoTitle.setCenterLocationY(game.getLocationYFromTop(220f / 2f));
        ssbCancel.setCenterLocationY(game.getLocationYFromTop(220f / 2f));

        renderablesVolumePanel.add(sgoIcon, sgoTitle, ssbCancel);
        gestureDetectablesTop.add(ssbCancel);
    }

    private void initVolumeIconSliders() {
        final float header = 220f;
        final float gap = 12f + 120f;
        String[] iconPath = {
                "icoMusicBox.png", "icoWaves.png", "icoOwl.png", "icoFire.png", "icoBell.png",
                "icoStream.png", "icoBirds.png", "icoRain.png", "icoFrog.png", "icoMeditation.png",
                "icoStudy.png", "icoSleep.png", "icoWhiteNoise.png"};

        for (int i = 0; i < iconPath.length; i++) {
            SpriteGameObject sgoIcon = new SpriteGameObject(
                    game.getAssetManager().get("img/" + iconPath[i], Texture.class),
                    40f,
                    game.getLocationYFromTop(header + gap * (i + 1)))
                    .setSpriteBatch(game.getSpriteBatch());

            SliderX vSlider = initVolumeSlider(new Point2DFloat(750f + 32f,
                                                                sgoIcon.getCenterLocationY()),
                                               new Point2DFloat(32f, 32f));
            renderablesVolumePanel.add(sgoIcon, vSlider);
            gesturesVolumePanel.add(vSlider);
        }
    }

    private SliderX initVolumeSlider(Point2DFloat centLoc, Point2DFloat touchMargin) {
        final float width =
                game.getAssetManager().get("img/rect580x16.png", Texture.class).getWidth() +
                touchMargin.getX() * 2f;
        final float height =
                game.getAssetManager().get("img/rect580x16.png", Texture.class).getHeight() +
                touchMargin.getY() * 2f;

        final float locX = centLoc.getX() - width / 2f;
        final float locY = centLoc.getY() - height / 2f;
        final float minX = locX ;//+ touchMargin.getX();
        final float maxX = minX+width-touchMargin.getX() * 2f;

        SliderX vSlider = new SliderX(locX, locY,
                                      width, height,
                                      minX, maxX,
                                      game.getSpriteBatch(), this.viewport);

        vSlider
                .setSlideBase(game.getAssetManager()
                                  .get("img/rect580x16.png", Texture.class),
                              0,
                              0,
                              true);
        vSlider
                .setSlideProgress(game.getAssetManager()
                                      .get("img/rect580x16.png", Texture.class),
                                  0,
                                  0,
                                  true);
        vSlider
                .setSlideKnob(game.getAssetManager()
                                  .get("img/circle60.png", Texture.class),
                              0,
                              0,
                              true);

        vSlider
                .setTouchHolo(game.getAssetManager()
                                  .get("img/circle222.png", Texture.class),
                              new Color(0xffffff4c));


        vSlider.setColor_Base(new Color(0x09372fff));
        vSlider.setColor_progressBar(new Color(0xff0000ff));
        vSlider.setColor_knob(new Color(0x041d18ff));

        vSlider.setEventListenerTouchDown(
                new GameGestureListener.TouchDownEventListener() {
                    @Override
                    public void onEvent(float v, float v1, int i, int i1) {
                        sthmsTimer.setTimeSec(getTimeSecFromSlider());
                        Gdx.app.log(tag, "slide time = " + sthmsTimer.getTimerLog());
                    }
                });
        vSlider.setEventListenerTouchDragged(
                new GameGestureListener.TouchDraggedEventListener() {
                    @Override
                    public void onEvent(int i, int i1, int i2) {
                        sthmsTimer.setTimeSec(getTimeSecFromSlider());
                        Gdx.app.log(tag, "slide time = " + sthmsTimer.getTimerLog());
                    }
                });

        return vSlider;

    }

    private void initVolumePanel() {
        renderablesVolumePanel = new Array<>();
        gesturesVolumePanel = new Array<>();

        initVolumePanelBg();
        initVolumePanelHead();
        initVolumeIconSliders();
    }

    private void initSettingVolume(Point2DFloat loc) {
        SpriteSimpleButton ssbSettingVolume = new SpriteSimpleButton(
                game.getAssetManager().get("img/btn_volume.png", Texture.class), viewport,
                game.getSpriteBatch(), loc.getX(), loc.getY());

        ssbSettingVolume.setScale(0.1f);
        ssbSettingVolume.scale(1f, 1f, 1.4f, EaseCubicIn.getInstance());

        ssbSettingVolume.setEventListenerTab(new GameGestureListener.TabEventListener() {
            @Override
            public void onEvent(float v, float v1, int i, int i1) {
                showVolumePanel();
            }
        });

        renderables.add(ssbSettingVolume);
        gestureDetectables.add(ssbSettingVolume);

    }

    private void initMoreMyApps(Point2DFloat loc) {
        this.ssbMoreMyApps = new SpriteSimpleButton(
                game.getAssetManager().get("img/btn_moreapps.png", Texture.class), viewport,
                game.getSpriteBatch(), loc.getX(), loc.getY());

        ssbMoreMyApps.setScale(0.1f);
        ssbMoreMyApps.scale(1f, 1f, 1.4f, EaseCubicIn.getInstance());

        ssbMoreMyApps.setEventListenerTab(new GameGestureListener.TabEventListener() {
            @Override
            public void onEvent(float v, float v1, int i, int i1) {
                game.getLauncherHandler().actionMoreMyApp();
            }
        });

        renderables.add(ssbMoreMyApps);
        gestureDetectables.add(ssbMoreMyApps);
    }

    /**
     * Hide MoreMyApps button from display
     *
     * @return return false when this button is not initialized.
     */
    private boolean hideMoreMyApps() {
        final float movingT = 0.3f;
        if (this.ssbMoreMyApps == null) {
            Gdx.app.log(tag, "Can't hide moreMyApps button, this button is not initialized");
            return false;
        }
        ssbMoreMyApps.paintA(0f, movingT * 2f);
        ssbMoreMyApps.moveX(game.getViewportWidth() * 1.2f, movingT);

        return true;
    }

    /**
     * Display MoreMyApps button that is hidden
     *
     * @return return false when this button is not initialized.
     */
    private boolean showMoreMyApps() {
        final float movingT = 0.3f;
        if (this.ssbMoreMyApps == null) {
            Gdx.app.log(tag, "Can't display moreMyApps button, this button is not initialized");
            return false;
        }

        ssbMoreMyApps.paintA(1f, movingT);
        ssbMoreMyApps.moveX(640f, movingT);

        return true;
    }

    private void initTimerTime(Point2DFloat loc) {
        Array<Texture> nums = new Array<>();
        nums.add(game.getAssetManager().get("img/timerNum0.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum1.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum2.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum3.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum4.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum5.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum6.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum7.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum8.png", Texture.class));
        nums.add(game.getAssetManager().get("img/timerNum9.png", Texture.class));

        this.sthmsTimer = new SpriteTimeHMS(nums,
                                            game.getAssetManager()
                                                .get("img/timerNumColon.png", Texture.class),
                                            -1,
                                            loc.getX(), loc.getY(),
                                            2 * 60 * 60, 0,
                                            game.getSpriteBatch());
        this.sthmsTimer.setTimeSec(sthmsTimer.getMaxNumSec() / 2);
        this.sthmsTimer.setColors(0xc1ccc3ff);

//        renderables.add(sthmsTimer);
    }

    private void initTimerSlider(Point2DFloat loc) {
        final float sliderRange =
                game.getAssetManager().get("img/rect580x16.png", Texture.class).getWidth();
        final float touchMargin = 32f;
        final float touchHeight = 60f + touchMargin;
        final float minX = loc.getX();
        final float maxX = minX + sliderRange;

        playerTimerSlider
                = new SliderX(loc.getX(),
                              loc.getY() - touchMargin / 2f,
                              sliderRange + touchMargin * 2f,
                              touchHeight,
                              minX, maxX,
                              game.getSpriteBatch(), this.viewport);

        playerTimerSlider
                .setSlideBase(game.getAssetManager()
                                  .get("img/rect580x16.png", Texture.class),
                              0,
                              0,
                              true);
        playerTimerSlider
                .setSlideProgress(game.getAssetManager()
                                      .get("img/rect580x16.png", Texture.class),
                                  0,
                                  0,
                                  true);
        playerTimerSlider
                .setSlideKnob(game.getAssetManager()
                                  .get("img/circle60.png", Texture.class),
                              0,
                              0,
                              true);

        playerTimerSlider
                .setTouchHolo(game.getAssetManager()
                                  .get("img/circle222.png", Texture.class),
                              new Color(0xffffff4c));


        playerTimerSlider.setColor_Base(new Color(0x09372fff));
        playerTimerSlider.setColor_progressBar(new Color(0x041d18ff));
        playerTimerSlider.setColor_knob(new Color(0x041d18ff));

        playerTimerSlider.setEventListenerTouchDown(
                new GameGestureListener.TouchDownEventListener() {
                    @Override
                    public void onEvent(float v, float v1, int i, int i1) {
                        sthmsTimer.setTimeSec(getTimeSecFromSlider());
                        Gdx.app.log(tag, "slide time = " + sthmsTimer.getTimerLog());
                    }
                });
        playerTimerSlider.setEventListenerTouchDragged(
                new GameGestureListener.TouchDraggedEventListener() {
                    @Override
                    public void onEvent(int i, int i1, int i2) {
                        sthmsTimer.setTimeSec(getTimeSecFromSlider());
                        Gdx.app.log(tag, "slide time = " + sthmsTimer.getTimerLog());
                    }
                });

        setSliderPositionByTimerSec();

//        renderables.add(playerTimerSlider);
//        gestureDetectables.add(playerTimerSlider);

    }

    private void initTimerTimeSlider(Point2DFloat loc) {
        initTimerTime(new Point2DFloat(420f, loc.getY() + 80f));
        initTimerSlider(new Point2DFloat(420f, loc.getY()));
    }

    private boolean showTimerTimeSlider() {
        if (this.sthmsTimer == null) {
            Gdx.app.log(tag, "sthmsTimer is null");
            return false;
        }
        if (this.playerTimerSlider == null) {
            Gdx.app.log(tag, "playerTimerSlider is null");
            return false;
        }
//        sthmsTimer.setColors(0xc1ccc3ff);
//        playerTimerSlider.setColorAComponents(0);
        renderables.add(sthmsTimer, playerTimerSlider);
        gestureDetectables.add(playerTimerSlider);

        return true;
    }

    private boolean hideTimerTimeSlider() {
        if (this.sthmsTimer == null) {
            Gdx.app.log(tag, "sthmsTimer is null");
            return false;
        }
//        sthmsTimer.setColors(0xc1ccc300);
        safeRemoveRenderable(sthmsTimer);
        safeRemoveRenderable(playerTimerSlider);
        gestureDetectables.removeValue(playerTimerSlider, true);
        return true;
    }

    private int getTimeSecFromSlider() {
        int ret = (int) ((float) (sthmsTimer.getMaxNumSec() -
                                  sthmsTimer.getMinNumSec()) *
                         playerTimerSlider.getPositionXRatio()) +
                  sthmsTimer.getMinNumSec();
        return ret;
    }

    private void setSliderPositionByTimerSec() {
        if (playerTimerSlider == null) {
            Gdx.app.log(tag, "playerTimerSlider is null");
            return;
        }
        playerTimerSlider.setPositionXRatio((float) sthmsTimer.getTimeSec() /
                                            (float) (sthmsTimer.getMaxNumSec() -
                                                     sthmsTimer.getMinNumSec()));
        playerTimerSlider.updatePosion();
    }

    private void initCompassSmall(Point2DFloat loc) {
        sgoCompassSmall =
                new SpriteGameObject(
                        game.getAssetManager().get("img/compassSmall.png", Texture.class),
                        loc.getX(), loc.getY())
                        .setSpriteBatch(game.getSpriteBatch());
        sgoCompassSmall.setColorA(0.1f);
        sgoCompassSmall.paintA(1f, 2f);
        renderables.add(sgoCompassSmall);
    }

    private boolean showCompassSmall() {
        if (sgoCompassSmall == null) {
            Gdx.app.log(tag, "compass small is null");
            return false;
        }
        sgoCompassSmall.moveX(400f, 0.15f);
        return true;
    }

    private boolean hideCompassSmall() {
        if (sgoCompassSmall == null) {
            Gdx.app.log(tag, "compass small is null");
            return false;
        }
        sgoCompassSmall.moveX(game.getViewportWidth(), 0.15f);
        return true;
    }

    private void initSettingPanel() {
        final float locY = game.getLocationYFromTop(348f);

        initSettingVolume(new Point2DFloat(80f, locY));
        initSettingTimer(new Point2DFloat(240f, locY));
        initMoreMyApps(new Point2DFloat(640f, locY));
        initTimerTimeSlider(new Point2DFloat(420f, locY));

        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Compass)) {//compass is available
            Gdx.app.log(tag, "Compass available");
            setCompassAvailable(true);
            initCompassSmall(new Point2DFloat(400f, locY));
        } else {
            Gdx.app.log(tag, "Compass is not available");
            setCompassAvailable(false);
        }
    }

    public boolean isCompassAvailable() {
        return compassAvailable;
    }

    public void setCompassAvailable(boolean compassAvailable) {
        this.compassAvailable = compassAvailable;
    }

    private void initSoundButtons() {
        final float hMargin = (game.getViewportWidth() - 920f) / 2f; // left/right margin
        final float offset = (920f - 280f * 3f) / 2f;

        String[] soundButtonPath = {
                "img/btn_wave.png", "img/btn_owl.png", "img/btn_fire.png",
                "img/btn_bell.png", "img/btn_stream.png", "img/btn_bird.png",
                "img/btn_rain.png", "img/btn_frog.png", "img/btn_meditation.png",
                "img/btn_study.png", "img/btn_sleep.png", "img/btn_whitenoise.png"};

        this.soundButtons = new Array<>();
        SoundButton sndMusicBtn =
                new SoundButton(game.getAssetManager().get("img/btn_music.png", Texture.class),
                                game.getAssetManager().get("img/rect4x4.png", Texture.class),
                                this.viewport, game.getSpriteBatch(), hMargin,
                                game.getLocationYFromTop(668f)) {
                    @Override
                    public void onTab(int index, boolean buttonState) {
                        super.onTab(index, buttonState);
                        game.getLauncherHandler().playMusic(index, buttonState, 1f);
                        Gdx.app.log(tag, "SoundButton " + index + " : " + buttonState);
                    }
                };
        sndMusicBtn.setIndex(0);

        soundButtons.add(sndMusicBtn);

        final float orgLocY = sndMusicBtn.getLocationY() -
                              game.getAssetManager().get(soundButtonPath[0], Texture.class)
                                  .getHeight() -
                              offset;
        final float btnWidth =
                game.getAssetManager().get(soundButtonPath[0], Texture.class).getWidth();
        final float btnHeight = btnWidth;

        for (int i = 0; i < soundButtonPath.length; i++) {
            SoundButton sndBtn =
                    new SoundButton(game.getAssetManager().get(soundButtonPath[i], Texture.class),
                                    game.getAssetManager().get("img/rect4x4.png", Texture.class),
                                    this.viewport, game.getSpriteBatch(),
                                    hMargin + (btnWidth + offset) * (i % 3),
                                    orgLocY - (btnHeight + offset) * (float) (i / 3)) {
                        @Override
                        public void onTab(int index, boolean buttonState) {
                            super.onTab(index, buttonState);
                            game.getLauncherHandler().playMusic(index, buttonState, 1f);
                            //get volume
                            Gdx.app.log(tag, "SoundButton " + index + " : " + buttonState);
                        }
                    };
            sndBtn.setIndex(i + 1);

            soundButtons.add(sndBtn);
        }

        for (SoundButton soundButton : soundButtons) {
            soundButton.setColorA(0f);
            soundButton.paintA(1f, 3f);
            soundButton.setOnTouchScale(1.06f);
            renderables.add(soundButton);
            gestureDetectables.add(soundButton);
        }
    }

    private void initTitle() {
        SpriteGameObject sgoTitle =
                new SpriteGameObject(
                        game.getAssetManager().get("img/homeTitle.png", Texture.class), 0,
                        game.getLocationYFromTop(210f))
                        .setSpriteBatch(game.getSpriteBatch());
        sgoTitle.setCenterLocationX(game.getCenterX());
        sgoTitle.setColorA(0f);
        sgoTitle.paintA(1f, 1f);
        sgoTitle.setScale(0.5f);
        sgoTitle.scale(1f, 1f, 1.5f, EaseCubicIn.getInstance());

        SpriteGameObject sgoLogo =
                new SpriteGameObject(
                        game.getAssetManager().get("img/ico_leaf.png", Texture.class),
                        game.getViewportWidth(),
                        sgoTitle.getLocationY() + sgoTitle.getHeight() -
                        game.getAssetManager().get("img/ico_leaf.png", Texture.class).getHeight())
                        .setSpriteBatch(game.getSpriteBatch());

        sgoLogo.moveX(sgoTitle.getLocationX(), 1f);
        sgoLogo.rotate(360f, 0.8f);
        renderables.add(sgoLogo, sgoTitle);
    }

    private void initBg() {
        SpriteGameObject sgoBg =
                new SpriteGameObject(
                        game.getAssetManager().get("img/homeBg.png", Texture.class), 0, 0)
                        .setSpriteBatch(game.getSpriteBatch());
        sgoBg.setColorA(0f);
        sgoBg.paintA(1f, 3f, EaseCubicIn.getInstance());
        sgoBg.setScale(1.5f);
        sgoBg.scale(1f, 1f, 1f);
        renderables.add(sgoBg);
    }

    private void launchScreenAds() {
//        Gdx.app.log(tag, "getExeCount = " + getExeCount());
        final int exeCount = getExeCount();

        if (game.getGameConfiguration().isInitialPublishing()) {
            showMyScreenAds();
        } else {
            if (exeCount == 0) {
                showMyScreenAds();
            } else if (exeCount < 3) {
                if (Math.random() < 0.5f) {
                    showMyScreenAds();
                } else {
                    showAdmobScreenAds();
                }
            } else {
                if (Math.random() < 0.1f) {
                    showMyScreenAds();
                } else {
                    showAdmobScreenAds();
                }
            }
        }
    }

    private void showAdmobScreenAds() {
        if (Math.random() < 0.7f) {
            if (game.isAdmobInterAdLoaded()) {
                Gdx.app.log(tag, "show interstitial Ads");
                game.getLauncherHandler().showAdmobInterstitial();
            } else if (game.isAdmobRewardAdLoaded()) {
                Gdx.app.log(tag, "show rewarded Ads");
                game.getLauncherHandler().showAdmobReward(admobOnUserEarnedReward);
            } else {
                Gdx.app.log(tag, "Admob screen ads are not loaded..");
                showMyScreenAds();
            }
        } else {
            if (game.isAdmobRewardAdLoaded()) {
                Gdx.app.log(tag, "show rewarded Ads");
                game.getLauncherHandler().showAdmobReward(admobOnUserEarnedReward);
            } else if (game.isAdmobInterAdLoaded()) {
                Gdx.app.log(tag, "show interstitial Ads");
                game.getLauncherHandler().showAdmobInterstitial();
            } else {
                Gdx.app.log(tag, "Admob screen ads are not loaded..");
                showMyScreenAds();
            }
        }
    }


    private String getMyScreenAdsUri() {
        String uri = null;
        if (ssbMyScreenAdsDisplay == null) {
            Gdx.app.log(tag, "ssbMyScreenAdsDisplay is null");
        } else {
            uri = (String) ssbMyScreenAdsDisplay.getUserData();
        }

        return uri;
    }

    private void hideMyScreenAds() {
        isShowMyScreenAdsOn = false;
        myScreenAdsOnTime = 0f;
        setGestureDetectTop(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        renderablesTop.removeValue(ssbMyScreenAdsDisplay, false);
                        ssbMyScreenAdsDisplay = null;
                    }
                });
            }
        }).start();

        safeRemoveRenderableTop(sgoMyScreenAdsCancelBg);
        safeRemoveRenderableTop(ssbMyScreenAdsCancelBtn);
        gestureDetectablesTop.removeValue(ssbMyScreenAdsCancelBtn, false);
        gestureDetectablesTop.removeValue(ssbMyScreenAdsDisplay, false);
    }

    private void showMyScreenAds() {
        //cancel -> goto game(loading)
        //select ad -> ad ->return? -> timer set to goto game in this situation.
        isShowMyScreenAdsOn = true;
        myScreenAdsOnTime = 15f;

        NatureSound.ADS myAds = game.getRandomAds();

        ssbMyScreenAdsDisplay = new SpriteSimpleButton(
                game.getAssetManager().get(game.getMyScreenAdsTexturePath(myAds), Texture.class),
                viewport, game.getSpriteBatch(),
                0, 0);

        ssbMyScreenAdsDisplay
                .setLocationY(game.getLocationYFromTop(ssbMyScreenAdsDisplay.getHeight() + 250f));

        ssbMyScreenAdsDisplay.setSpriteOriginCenter();

        ssbMyScreenAdsDisplay.setOnTouchScale(1.05f);

        ssbMyScreenAdsDisplay.setUserData(game.getMyAdsUri(myAds));//save ad uri

        ssbMyScreenAdsDisplay.setEventListenerTab(new GameGestureListener.TabEventListener() {
            @Override
            public void onEvent(float v, float v1, int i, int i1) {
                myScreenAdsOnTime = 0f;
                game.getLauncherHandler()
                    .actionVisitPlayStore(getMyScreenAdsUri());
            }
        });

        if (sgoMyScreenAdsCancelBg == null) {
            sgoMyScreenAdsCancelBg = new SpriteGameObject(
                    game.getAssetManager().get("img/rect_18x18.png", Texture.class),
                    0, (game.getLocationYFromTop(250f)))
                    .setSpriteBatch(game.getSpriteBatch());
            sgoMyScreenAdsCancelBg.setSize(game.getViewportWidth(), 250f);
            sgoMyScreenAdsCancelBg.setSpriteOriginCenter();
            sgoMyScreenAdsCancelBg.setColor(0f, 0f, 0f, 0f);
            sgoMyScreenAdsCancelBg.paintA(1f, 1f);
        }

        if (ssbMyScreenAdsCancelBtn == null) {
            ssbMyScreenAdsCancelBtn = new SpriteSimpleButton(
                    game.getAssetManager().get("img/adsCloseButton.png", Texture.class),
                    viewport, game.getSpriteBatch(),
                    0, (game.getLocationYFromTop(175f)));
            ssbMyScreenAdsCancelBtn.setSpriteOriginCenter();


            ssbMyScreenAdsCancelBtn.setEventListenerTab(new GameGestureListener.TabEventListener() {
                @Override
                public void onEvent(float v, float v1, int i, int i1) {
                    myScreenAdsOnTime = 0f;
                }
            });
        } else {
            ssbMyScreenAdsCancelBtn
                    .setLocation(0, (game.getLocationYFromTop(175f)));
        }
        ssbMyScreenAdsCancelBtn.rotate(360, 2f);
        ssbMyScreenAdsCancelBtn.moveX((910f), 2f, EaseElasticInOut.getInstance());

        renderablesTop.add(ssbMyScreenAdsDisplay, sgoMyScreenAdsCancelBg, ssbMyScreenAdsCancelBtn);
        setGestureDetectTop(true);
        gestureDetectablesTop.add(ssbMyScreenAdsDisplay, ssbMyScreenAdsCancelBtn);
    }

    private Sound getSound(String soundPath) {
        return game.getAssetManager().get(soundPath, Sound.class);
    }

    @Override
    public void onTimer1sec(float v, int i) {
        Gdx.app.log(tag, "onTimer1sec");
        if (isCompassAvailable()) {
            checkCompass(1f);
        }
    }

    @Override
    public void onTimer500msec(float v, int i) {
        Gdx.app.log(tag, "onTimer500msec");

    }

    @Override
    public void onTimer250msec(float v, int i) {
        Gdx.app.log(tag, "onTimer250msec");
        if (isShowMyScreenAdsOn) {
            if (myScreenAdsOnTime < 0) {
                hideMyScreenAds();

            } else {
                myScreenAdsOnTime = myScreenAdsOnTime - 0.25f;
            }
        }
    }

    private void checkCompass(float rotateT) {
        if (sgoCompassSmall != null) {
            float azimuth = Gdx.input.getAzimuth();
            sgoCompassSmall.rotate(azimuth, rotateT);
        } else {
            Gdx.app.log(tag, "sgoCompass is null");
        }
    }

    private void setGameTimer() {
        gameTimer = new GameTimer().start();
        gameTimer.setEventListener(this);
        renderables.add(gameTimer);
    }

    Vector2 getNewTouchPoint(float x, float y) {
        return viewport.unproject(new Vector2(x, y));
    }

    public boolean isGestureDetectTop() {
        return gestureDetectTop;
    }

    public void setGestureDetectTop(boolean gestureDetectTop) {
        this.gestureDetectTop = gestureDetectTop;
    }

    private void setInputProcessor() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(new GestureDetector(new GameGestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                Vector2 newTouchPoint = getNewTouchPoint(x, y);
                for (IGestureInput iGestureInput : isGestureDetectTop() ? gestureDetectablesTop :
                        gestureDetectables) {
                    iGestureInput.touchDown(newTouchPoint.x, newTouchPoint.y, pointer, button);
                }
                return super.touchDown(x, y, pointer, button);
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                Vector2 newTouchPoint = getNewTouchPoint(x, y);
                for (IGestureInput iGestureInput : isGestureDetectTop() ? gestureDetectablesTop :
                        gestureDetectables) {
                    iGestureInput.tap(newTouchPoint.x, newTouchPoint.y, count, button);
                }
//                if (isGestureDetectTop()) {
//                    if (isOutsideOfSettingMenu(newTouchPoint)) {
//                        switch (getGestureTop()) {
//                            case ESK_EXIT:
//                                closeEskExitAppDlg();
//                                break;
//                            default:
//                                Gdx.app.log(tag, getGestureTop().toString());
//                                break;
//                        }
//                    }
//                }

                return super.tap(x, y, count, button);
            }

            @Override
            public boolean longPress(float x, float y) {
                Vector2 newTouchPoint = getNewTouchPoint(x, y);
                for (IGestureInput iGestureInput : isGestureDetectTop() ? gestureDetectablesTop :
                        gestureDetectables) {
                    iGestureInput.longPress(newTouchPoint.x, newTouchPoint.y);
                }
                return super.longPress(x, y);
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                return super.fling(velocityX, velocityY, button);
            }
        }));

        inputMultiplexer.addProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    Gdx.app.log(tag, "Input.Keys.BACK : Key down");
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    Gdx.app.log(tag, "Input.Keys.BACK : Key up");
//                    if (isGestureDetectTop()) {
//                        if (getGestureTop() == GestureTop.ESK_EXIT) {
//                            closeEskExitAppDlg();
//                        } else {
//                            Gdx.app.log(tag, "Unknown gestureTop" + getGestureTop().toString());
//                        }
//                    } else {
//                        showAskExitGame();
//                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                Gdx.app.log(tag, String.valueOf(character));
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                Vector2 newTouchPoint = getNewTouchPoint(screenX, screenY);
                for (IGestureInput iGestureInput : isGestureDetectTop() ? gestureDetectablesTop :
                        gestureDetectables) {
                    iGestureInput
                            .touchUp((int) newTouchPoint.x, (int) newTouchPoint.y, pointer, button);
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector2 newTouchPoint = getNewTouchPoint(screenX, screenY);
                for (IGestureInput iGestureInput : isGestureDetectTop() ? gestureDetectablesTop :
                        gestureDetectables) {
                    iGestureInput
                            .touchDragged((int) newTouchPoint.x, (int) newTouchPoint.y, pointer);
                }
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }

        });

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void safeRemoveRenderable(final Renderable renderable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        renderables.removeValue(renderable, false);
                    }
                });
            }
        }).start();
    }

    private void safeRemoveRenderableTop(final Renderable renderable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        renderablesTop.removeValue(renderable, false);
                    }
                });
            }
        }).start();
    }

    private void safeAddRenderable(final Renderable renderable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        renderables.add(renderable);
                    }
                });
            }
        }).start();
    }

    private void safeAddGestureDetectable(final IGestureInput gestureInput) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        gestureDetectables.add(gestureInput);
                    }
                });
            }
        }).start();
    }

    public boolean isFirstGame() {
        return flagFirstGame;
    }

    public void setFlagFirstGame(boolean flagFirstGame) {
        this.flagFirstGame = flagFirstGame;
    }

    private void playMusic(Music music, boolean isLooping, float volume) {
        music.setLooping(isLooping);
        music.setVolume(volume);
        music.play();
    }

    private void stopAllMusicSound() {
        stopAllMusic();
        stopAllSound();
    }

    private void stopAllMusic() {
    }

    private void stopAllSound() {
//        soundGameStart.stop();
    }

    private void initAdmobEarnedReward() {
        this.admobOnUserEarnedReward = new IAdmobOnUserEarnedReward() {
            @Override
            public void onUserEarnedReward(int rewardAmount, String rewardType) {
                Gdx.app.log(tag, "Reward ad : amount=" + rewardAmount + ",type=" + rewardType);
            }
        };
    }

    public boolean isCheckAdmobAdsLoaded() {
        return checkAdmobAdsLoaded;
    }

    public void setCheckAdmobAdsLoaded(boolean checkAdmobAdsLoaded) {
        this.checkAdmobAdsLoaded = checkAdmobAdsLoaded;
    }

    private void launchReviewMyApp() {
//        Gdx.app.log(tag, "time:" + (float) System.nanoTime() % 1000 / 10);
        game.getLauncherHandler().requestAppReview();
        Gdx.app.log(tag, "App review triggered");
    }

    public int getExeCount() {
        return exeCount;
    }

    public void setExeCount(int exeCount) {
        this.exeCount = exeCount;
    }
}
