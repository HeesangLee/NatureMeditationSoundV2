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
import dalcoms.lib.libgdx.GameTimer;
import dalcoms.lib.libgdx.IGestureInput;
import dalcoms.lib.libgdx.Renderable;
import dalcoms.lib.libgdx.SpriteGameObject;
import dalcoms.lib.libgdx.SpriteSimpleButton;
import dalcoms.lib.libgdx.SpriteSimpleToggleButton;
import dalcoms.lib.libgdx.Var4TimePair;
import dalcoms.lib.libgdx.VariationPerTime;
import dalcoms.lib.libgdx.easingfunctions.EaseBounceOut;
import dalcoms.lib.libgdx.easingfunctions.EaseCircIn;
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

    private boolean flagFirstGame = true;
    private boolean checkAdmobAdsLoaded = false;
    private boolean isShowMyScreenAdsOn = false;

    private float myScreenAdsOnTime = 0;
    SpriteSimpleButton ssbMyScreenAdsDisplay, ssbMyScreenAdsCancelBtn;
    SpriteGameObject sgoMyScreenAdsCancelBg;
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
        initSettingPanel();
        initSoundButtons();
    }

    private float initSettingPanelBg() {
        SpriteGameObject sgoSettingPanelBg =
                new SpriteGameObject(
                        game.getAssetManager().get("img/setting_bg.png", Texture.class), 0,
                        game.getLocationYFromTop(386f))
                        .setSpriteBatch(game.getSpriteBatch());
        sgoSettingPanelBg.setCenterLocationX(game.getCenterX());
        sgoSettingPanelBg.setScaleX(0.4f);
        sgoSettingPanelBg.setScaleY(0.1f);
        sgoSettingPanelBg.scale(1f, 1f, 0.8f);
        renderables.add(sgoSettingPanelBg);

        return sgoSettingPanelBg.getCenterLocationY();
    }

    SpriteSimpleButton ssbSettingTimer;

    private void initSettingTimer(float centerY) {
        this.ssbSettingTimer = new SpriteSimpleButton(
                game.getAssetManager().get("img/btn_loop.png", Texture.class), viewport,
                game.getSpriteBatch(), 150f, 0);
        ssbSettingTimer.setCenterLocationY(centerY);
        ssbSettingTimer.setOnTouchEffect(SpriteSimpleButton.OnTouchEffect.HOLO);
        ssbSettingTimer.setSgoTouchHolo(new SpriteGameObject(
                game.getAssetManager().get("img/holo200.png", Texture.class), 0,
                game.getLocationYFromTop(386f)).setSpriteBatch(game.getSpriteBatch()));
        ssbSettingTimer.setTouchScale(1.6f);

        ssbSettingTimer.setScale(0.1f);
        ssbSettingTimer.scale(1f,1f,1.4f,EaseCubicIn.getInstance());

        renderables.add(ssbSettingTimer);
        gestureDetectables.add(ssbSettingTimer);
    }

    private void initSettingVolume(float centerY) {
        SpriteSimpleButton ssbSettingVolume = new SpriteSimpleButton(
                game.getAssetManager().get("img/btn_volume.png", Texture.class), viewport,
                game.getSpriteBatch(), 0, 0);

        ssbSettingVolume.setCenterLocationX(game.getCenterX());
        ssbSettingVolume.setCenterLocationY(centerY);
        ssbSettingVolume.setOnTouchEffect(SpriteSimpleButton.OnTouchEffect.HOLO);
        ssbSettingVolume.setSgoTouchHolo(new SpriteGameObject(
                game.getAssetManager().get("img/holo200.png", Texture.class), 0,
                game.getLocationYFromTop(386f)).setSpriteBatch(game.getSpriteBatch()));
        ssbSettingVolume.setTouchScale(1.6f);

        ssbSettingVolume.setScale(0.1f);
        ssbSettingVolume.scale(1f,1f,1.4f,EaseCubicIn.getInstance());

        renderables.add(ssbSettingVolume);
        gestureDetectables.add(ssbSettingVolume);

    }

    private void initSettingHide(float centerY) {
        SpriteSimpleButton ssbSettingHide = new SpriteSimpleButton(
                game.getAssetManager().get("img/btn_hide.png", Texture.class), viewport,
                game.getSpriteBatch(), 850f, 0);

        ssbSettingHide.setCenterLocationY(centerY);
        ssbSettingHide.setOnTouchEffect(SpriteSimpleButton.OnTouchEffect.HOLO);
        ssbSettingHide.setSgoTouchHolo(new SpriteGameObject(
                game.getAssetManager().get("img/holo200.png", Texture.class), 0,
                game.getLocationYFromTop(386f)).setSpriteBatch(game.getSpriteBatch()));
        ssbSettingHide.setTouchScale(1.6f);

        ssbSettingHide.setScale(0.1f);
        ssbSettingHide.scale(1f,1f,1.4f,EaseCubicIn.getInstance());

        renderables.add(ssbSettingHide);
        gestureDetectables.add(ssbSettingHide);

    }

    private void initSettingPanel() {
        float centY = initSettingPanelBg();
        initSettingTimer(centY);
        initSettingVolume(centY);
        initSettingHide(centY);
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
                                game.getLocationYFromTop(726f)) {
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
        renderables.add(sgoTitle);
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
