package dalcoms.pub.naturesound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import dalcoms.lib.libgdx.Renderable;
import dalcoms.lib.libgdx.SpriteGameObject;
import dalcoms.lib.libgdx.SpriteSimpleButton;
import dalcoms.lib.libgdx.Var2TimePair;
import dalcoms.lib.libgdx.VariationPerTime;
import dalcoms.lib.libgdx.easingfunctions.EaseQuadInOut;

public class SoundButton extends SpriteSimpleButton {
    private boolean buttonState = false;
    Array<SpriteGameObject> sgoPlayEffects;
    Texture textureEffect;

    private Array<Renderable> childRenderables;

    public SoundButton(Texture texture, Texture textureEffect, Viewport viewport, float locationX,
            float locationY) {
        super(texture, viewport, locationX, locationY);
        this.textureEffect = textureEffect;
        childRenderables = new Array<>();
        initPlayEffect();
    }

    public SoundButton(Texture texture, Texture textureEffect, Viewport viewport,
            SpriteBatch spriteBatch,
            float locationX, float locationY) {
        super(texture, viewport, locationX, locationY);
        this.textureEffect = textureEffect;
        setSpriteBatch(spriteBatch);
        childRenderables = new Array<>();
        initPlayEffect();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        renderChild(delta);
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (isInTouchArea(x, y)) {
            toggleButtonState();
            onTab(getIndex(), isPlay());
//            Gdx.app.log("SoundButton", "state : " + getButtonState());
        }
        return super.tap(x, y, count, button);
    }

    private boolean getButtonState() {
        return buttonState;
    }

    private void setButtonState(boolean buttonState) {
        this.buttonState = buttonState;
    }

    public boolean isPlay() {
        return getButtonState();
    }

    private void toggleButtonState() {
        setButtonState(!getButtonState());
        effectPlay(isPlay());
    }

    public void onTab(int index, boolean buttonState) {

    }

    private void initPlayEffect() {
        final int count = 7;
        sgoPlayEffects = new Array<>();
        for (int i = 0; i < count; i++) {
            sgoPlayEffects.add(new SpriteGameObject(textureEffect, 0, 0)
                                       .setSpriteBatch(getSpriteBatch()));
        }
        for (SpriteGameObject sgo : sgoPlayEffects) {
            childRenderables.add(sgo);
        }
        updateEffectLocation();
    }

    public void updateEffectLocation() {
        final float gap = 28f;
        final float wh = textureEffect.getWidth();
        final float y = getCenterLocationY() - wh / 2f;
        float locX = getCenterLocationX() - wh / 2f - gap * (sgoPlayEffects.size - 1) / 2f;
        for (SpriteGameObject sgo : sgoPlayEffects) {
            sgo.setLocation(locX, y);
            locX = locX + gap;
        }
    }

    private Array<Var2TimePair> getEffectPath() {
        Array<Var2TimePair> path = new Array<>();
        float midT = (new Random().nextFloat()) * 1.5f + 0.5f;
        path.add(new Var2TimePair(1f, 1f, 0f), new Var2TimePair(1f, 20f, midT),
                 new Var2TimePair(1f, 1f, midT * 2f));
        return path;
    }

    private void effectPlay(boolean isOn) {

        if (isOn) {
            for (final SpriteGameObject sgo : sgoPlayEffects) {

                sgo.scale(getEffectPath());
                sgo.setEventListenerScaleY(new VariationPerTime.EventListener() {
                    @Override
                    public void onUpdate(float v, float v1) {

                    }

                    @Override
                    public void onStart(float v) {

                    }

                    @Override
                    public void onFinish(float v, float v1) {
                        sgo.scale(getEffectPath());
                    }
                });
            }
        } else {
            for (SpriteGameObject sgo : sgoPlayEffects) {
                sgo.scale(1f, 1f, 0.46f);
                sgo.setEventListenerScaleY(null);
            }
        }
    }

    private void renderChild(float delta) {
        for (Renderable renderable : childRenderables) {
            renderable.render(delta);
        }
    }
}
