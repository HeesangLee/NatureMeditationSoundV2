package dalcoms.pub.naturesound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import dalcoms.lib.libgdx.GameObject;
import dalcoms.lib.libgdx.IGestureInput;
import dalcoms.lib.libgdx.Renderable;
import dalcoms.lib.libgdx.SpriteGameObject;
import dalcoms.lib.libgdx.easingfunctions.EaseLinear;
import dalcoms.lib.libgdx.easingfunctions.IEasingFunction;

/*Todo
 * Touch dragged -> Touch down event 가 선행되었을 경우에만 반영
 * Touch down -> check touched in rendoer method -> view Holo effect
 */
public class SliderX extends GameObject {
    private Array<Renderable> childRenderables;
    private Array<IGestureInput> childGestureDetectables;
    private SpriteGameObject sgoSlideBase, sgoSlideKnob, sgoSlideProgress, sgoTouchHolo;
    private SpriteBatch spriteBatch;
    private Viewport viewport;
    private float minX, maxX;
    private float positionXRatio = 0.5f;
    private boolean onValidTouch = false;

    public SliderX(float locationX, float locationY, float width, float height,
            float minX, float maxX,
            SpriteBatch spriteBatch,
            Viewport viewport) {
        super(locationX, locationY, width, height);
        this.spriteBatch = spriteBatch;
        this.viewport = viewport;
        this.minX = minX;
        this.maxX = maxX;
        childRenderables = new Array<>();
        childGestureDetectables = new Array<>();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        renderChild(delta);
        if (isOnValidTouch()) {
            checkTouchValid(delta);
        }
    }

    private void checkIsTouched() {
        Vector2 touchPos = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        setOnValidTouch(Gdx.input.isTouched() & isInTouchArea(touchPos.x, touchPos.y));
    }

    private void checkTouchValid(float delta) {
        checkIsTouched();
        if (!isOnValidTouch()) {
            sgoTouchHolo.setVisible(false);
        }
    }

    private void renderChild(float delta) {
        for (Renderable renderable : childRenderables) {
            renderable.render(delta);
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        if (isInTouchArea(x, y)) {
            updateTouchPosition(x);
        }

        for (IGestureInput gestureInput : childGestureDetectables) {
            gestureInput.touchDown(x, y, pointer, button);
        }
        return super.touchDown(x, y, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (IGestureInput gestureInput : childGestureDetectables) {
            gestureInput.touchUp(screenX, screenY, pointer, button);
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        for (IGestureInput gestureInput : childGestureDetectables) {
            gestureInput.tap(x, y, count, button);
        }
        return super.tap(x, y, count, button);
    }

    @Override
    public boolean longPress(float x, float y) {
        for (IGestureInput gestureInput : childGestureDetectables) {
            gestureInput.longPress(x, y);
        }
        return super.longPress(x, y);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isOnValidTouch() && isInTouchArea((float) screenX, (float) screenY)) {
            updateTouchPosition((float) screenX);
        }
        for (IGestureInput gestureInput : childGestureDetectables) {
            gestureInput.touchDragged(screenX, screenY, pointer);
        }
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public void enterTouchDown() {
        setOnValidTouch(true);
        if (sgoTouchHolo != null) {
            sgoTouchHolo.setVisible(true);
            sgoTouchHolo.setScale(0.6f);
            sgoTouchHolo.scale(1f, 1f, 0.1f);
        }
        super.enterTouchDown();
    }

    public SliderX setSlideBase(Texture texture, float locationX, float locationY,
            boolean isCenterY) {
        sgoSlideBase = new SpriteGameObject(
                texture,
                getLocationX() + locationX,
                getLocationY() + locationY).setSpriteBatch(this.spriteBatch);
        if (isCenterY) {
            sgoSlideBase.setCenterLocationY(getCenterLocationY());
        }
        childRenderables.add(sgoSlideBase);

        return this;
    }

    public SliderX setSlideKnob(Texture texture, float locationX, float locationY,
            boolean isCenterY) {
        sgoSlideKnob = new SpriteGameObject(
                texture,
                0,
                getLocationY() + locationY).setSpriteBatch(this.spriteBatch);
        if (isCenterY) {
            sgoSlideKnob.setCenterLocationY(getCenterLocationY());
        }
        setKnobPositionX();
        childRenderables.add(sgoSlideKnob);
        return this;
    }

    public SliderX setSlideProgress(Texture texture, float locationX, float locationY,
            boolean isCenterY) {
        sgoSlideProgress = new SpriteGameObject(
                texture,
                0,
                getLocationY() + locationY).setSpriteBatch(this.spriteBatch);
        if (isCenterY) {
            sgoSlideProgress.setCenterLocationY(getCenterLocationY());
        }
        setProgress();
        childRenderables.add(sgoSlideProgress);
        return this;
    }

    private void setProgress() {
        sgoSlideProgress.setScaleX(getPositionXRatio());
        sgoSlideProgress.setCenterLocationX(minX + (maxX - minX) * getPositionXRatio() / 2f);
    }

    private void setKnobPositionX() {
        sgoSlideKnob.setCenterLocationX(minX + (maxX - minX) * getPositionXRatio());
        setHoloCenterPosition(sgoSlideKnob.getCenterLocationX(), sgoSlideKnob.getCenterLocationY());
    }

    private void setHoloCenterPosition(float centerX, float centerY) {
        if (sgoTouchHolo != null) {
            sgoTouchHolo.setCenterLocation(centerX, centerY);
        }
    }

    public SliderX setTouchHolo(Texture texture) {
        sgoTouchHolo = new SpriteGameObject(
                texture,
                0,
                0).setSpriteBatch(this.spriteBatch);
        sgoTouchHolo.setCenterLocationY(getCenterLocationY());
        sgoTouchHolo.setVisible(false);

        childRenderables.add(sgoTouchHolo);
        return this;
    }

    public SliderX setTouchHolo(Texture texture, Color color) {
        setTouchHolo(texture);
        sgoTouchHolo.setColor(color);
        return this;
    }

    public void setColor_Base(Color color) {
        sgoSlideBase.setColor(color);
    }

    public void setColor_progressBar(Color color) {
        sgoSlideProgress.setColor(color);
    }

    public void setColor_knob(Color color) {
        sgoSlideKnob.setColor(color);
    }

    public float getMinX() {
        return minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public boolean isOnValidTouch() {
        return onValidTouch;
    }

    public void setOnValidTouch(boolean onValidTouch) {
        this.onValidTouch = onValidTouch;
    }

    public float getPositionXRatio() {
        return positionXRatio;
    }

    public void setPositionXRatio(float positionXRatio) {
        this.positionXRatio = positionXRatio;
    }

    public void setPositionXRatioByTouchPosition(float x) {
        if (x <= minX) {
            setPositionXRatio(0f);
        } else if (x >= maxX) {
            setPositionXRatio(1f);
        } else {
            setPositionXRatio((x - minX) / (maxX - minX));
        }
    }

    public void updateTouchPosition(float touchPosition) {
        setPositionXRatioByTouchPosition(touchPosition);
        setKnobPositionX();
        setProgress();
    }

    public void updatePosion(){
        setKnobPositionX();
        setProgress();
    }

    public void paintAComponents(float toColorA, float varT) {
        paintAComponents(toColorA, varT, EaseLinear.getInstance());
    }

    public void paintAComponents(float toColorA, float varT, IEasingFunction easeFun) {
        sgoSlideBase.paintA(toColorA, varT, easeFun);
        sgoSlideKnob.paintA(toColorA, varT, easeFun);
        sgoSlideProgress.paintA(toColorA, varT, easeFun);
    }

    public void setColorAComponents(float colorA) {
        sgoSlideBase.setColorA(colorA);
        sgoSlideKnob.setColorA(colorA);
        sgoSlideProgress.setColorA(colorA);
    }


}
