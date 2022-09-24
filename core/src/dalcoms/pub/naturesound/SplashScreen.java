package dalcoms.pub.naturesound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import dalcoms.lib.libgdx.GameObject;
import dalcoms.lib.libgdx.GameTimer;
import dalcoms.lib.libgdx.Renderable;
import dalcoms.lib.libgdx.SpriteGameObject;
import dalcoms.lib.libgdx.Var4TimePair;
import dalcoms.lib.libgdx.VariationPerTime;
import dalcoms.lib.libgdx.easingfunctions.EaseCircOut;
import dalcoms.lib.libgdx.easingfunctions.EaseCubicInOut;

class SplashScreen implements Screen, GameTimer.EventListener {
    final String tag = "SplashScreen";
    final NatureSound game;
    OrthographicCamera camera;
    Viewport viewport;
    GameTimer gameTimer;
    private Array<Renderable> renderables;

    private float bgColorR = 0f, bgColorG = 0f, bgColorB = 0f, bgColorA = 1f;
    private boolean screenIsDone = false, nextScreen = false;
    int timeOfScreenDone250msec = 0;
    private boolean isAdmobInterstitialLoaded = false;
    private boolean isAdmobRewardedLoaded = false;
    GameObject goBgColor;


    public SplashScreen(final NatureSound game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, game.getGameConfiguration().getViewportWidth(),
                          game.getGameConfiguration().getViewportHeight());
        this.viewport = new FitViewport(game.getGameConfiguration().getViewportWidth(),
                                        game.getGameConfiguration().getViewportHeight(),
                                        camera);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setCatchKey(Input.Keys.HOME, true);
    }

    @Override
    public void show() {
        renderables = new Array<>();
        setGameTimer();
        initGameObjects();
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

    private float getFloatColor255(float colorInt) {
        return colorInt / 255f;
    }

    private void loadBgColor() {
//        bgColorR = getFloatColor255(0f);
//        bgColorG = getFloatColor255(0f);
//        bgColorB = getFloatColor255(0f);
//        bgColorA = getFloatColor255(255f);

        goBgColor = new GameObject();
        Array<Var4TimePair> bgColorPath = new Array<>();
        bgColorPath.add(new Var4TimePair(0f / 255f, 0f / 255f, 0f / 255f, 1f, 0f));
        bgColorPath.add(new Var4TimePair(33f / 255f, 63f / 255f, 56f / 255f, 1f, 2f));
        bgColorPath.add(new Var4TimePair(247f / 255f, 245f / 255f, 229f / 255f, 1f, 3f));
        goBgColor.paint(bgColorPath);
        renderables.add(goBgColor);
    }

    private void draw(float delta) {
        if (goBgColor == null) {
            ScreenUtils.clear(bgColorR, bgColorG, bgColorB, bgColorA);
        } else {
            ScreenUtils.clear(goBgColor.getColorR(), goBgColor.getColorG(), goBgColor.getColorB(),
                              goBgColor.getColorA());
        }


        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();

        for (Renderable renderable : renderables) {
            renderable.render(delta);
        }

        game.getSpriteBatch().end();
    }


    private void initGameObjects() {
        loadBgColor();
        final SpriteGameObject sgoTitleText =
                new SpriteGameObject(game.getAssetManager().get("img/title_splash.png",
                                                                Texture.class), 0,
                                     game.getLocationYFromTop(1330f))
                        .setSpriteBatch(game.getSpriteBatch());
        sgoTitleText.setCenterLocationX(game.getGameConfiguration().getViewportWidth() / 2f);
        sgoTitleText.setColorA(0.2f);
        sgoTitleText.paintA(1f, 3f);

        final SpriteGameObject sgoImg =
                new SpriteGameObject(game.getAssetManager().get("img/splashImg.png",
                                                                Texture.class), 0, 0)
                        .setSpriteBatch(game.getSpriteBatch());
        sgoImg.setCenterLocation(sgoTitleText.getCenterLocationX(),
                                 sgoTitleText.getCenterLocationY());
        sgoImg.setScale(0.2f);
        sgoImg.scale(1f, 1f, 3f, EaseCircOut.getInstance());
        sgoImg.setColorA(0);
        sgoImg.paintA(1f, 2f);
        renderables.add(sgoTitleText, sgoImg);
    }

    Color getProgressRectColor(int num) {
        final int[] colors = {
                0xeb2822ff, 0xed3f35ff, 0xee5143ff, 0xf06755ff, 0xf27e67ff,
                0xf49177ff, 0xf6a688ff, 0xf8bb98ff, 0xf9cda7ff, 0xfcebbfff};
        return new Color(colors[num]);
    }

    @Override
    public void onTimer1sec(float v, int i) {

    }

    @Override
    public void onTimer500msec(float v, int i) {

    }

    @Override
    public void onTimer250msec(float v, int i) {
        final int countDone = 12;
        Gdx.app.log(tag, "asset : " + gameTimer.getCurTimeSec() + "sec : " +
                         game.getAssetManager().getProgress() * 100 + "%");
        checkAdmobAdLoaded();

        if (++timeOfScreenDone250msec == countDone) {
            screenIsDone = true;
            timeOfScreenDone250msec = countDone + 1;

        }
        if (screenIsDone && !nextScreen) {
            if (game.getAssetManager().isFinished()) {
                game.loadAssetGameScreen();
                nextScreen = true;
                gameTimer.pause();
                Gdx.app.log(tag, "goto home : " + gameTimer.getCurTimeSec() + "sec");
                gotoNextScreen();
            } else {
                Gdx.app.log(tag, "Home screen asset loading progress : " +
                                 gameTimer.getCurTimeSec() + "sec : " +
                                 game.getAssetManager().getProgress() * 100 + "%");
            }
        }

    }

    private void checkAdmobAdLoaded() {
        if (!isAdmobInterstitialLoaded && game.isAdmobInterAdLoaded()) {
            isAdmobInterstitialLoaded = true;
            Gdx.app.log(tag,
                        "Admob interstitial Ad is loaded@" + gameTimer.getCurTimeSec() + "sec");
        }
        if (!isAdmobRewardedLoaded && game.isAdmobRewardAdLoaded()) {
            isAdmobRewardedLoaded = true;
            Gdx.app.log(tag,
                        "Admob Reward Ad is loaded@" + gameTimer.getCurTimeSec() + "sec");
        }
    }

    private void gotoNextScreen() {
        game.setScreen(new HomeScreen(game));
    }

    private void setGameTimer() {
        gameTimer = new GameTimer().start();
        gameTimer.setEventListener(this);
        renderables.add(gameTimer);
    }


}
