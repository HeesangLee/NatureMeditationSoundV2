package dalcoms.pub.naturesound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import dalcoms.lib.libgdx.Renderable;
import dalcoms.lib.libgdx.SpriteGameObject;
import dalcoms.lib.libgdx.SpriteNumber;

public class SpriteTimeHMS implements Renderable {
    final String tag = "SpriteTimeHMS";
    Array<Texture> textureArrayOfNumbers;
    Texture textureColon;
    int timeSec = -1; //88:88:88
    float locationX, locationY;
    private SpriteNumber snHour10, snHour1, snMinute10, snMinute1, snSecond10, snSecond1;
    private SpriteGameObject sgoColon1, sgoColon2;
    private Array<Renderable> childRenderables;
    final private SpriteBatch spriteBatch;
    final private int COLOR_DISABLED = 0x4c596aff;
    final private int COLOR_MAX = 0xe43935ff;
    final private int COLOR_MIN = 0x2d889fff;
    private int maxNumSec = 100;
    private int minNumSec = 0;
    private boolean flagColorByRange = false;

    public SpriteTimeHMS(Array<Texture> textureArrayOfNumbers,
            Texture textureColon,
            int timeSec,
            float locationX,
            float locationY,
            int maxNumSec,
            int minNumSec,
            SpriteBatch spriteBatch) {
        this.textureArrayOfNumbers = textureArrayOfNumbers;
        this.textureColon = textureColon;
        this.timeSec = timeSec;
        this.locationX = locationX;
        this.locationY = locationY;
        this.spriteBatch = spriteBatch;

        this.maxNumSec = maxNumSec;
        this.minNumSec = minNumSec;

        childRenderables = new Array<>();
        initTime();
    }

    private void initTime() {
        float gap = 36f;
        float locXh10 = getLocationX();
        float locXh1 = locXh10 + gap;
        float locXc1 = locXh1 + gap;
        float locXm10 = locXc1 + gap;
        float locXm1 = locXm10 + gap;
        float locXc2 = locXm1 + gap;
        float locXs10 = locXc2 + gap;
        float locXs1 = locXs10 + gap;

//        Array<VarTimePair> rotPath = new Array<>();
//        rotPath.add(new VarTimePair(360, 0));
//        rotPath.add(new VarTimePair(0, 0.5f));

        snHour10 = new SpriteNumber(this.textureArrayOfNumbers, getTimeHourDig10(),
                                    locXh10, getLocationY())
                .setSpriteBatch(this.spriteBatch);
        snHour1 = new SpriteNumber(this.textureArrayOfNumbers, getTimeHourDig1(),
                                   locXh1, getLocationY())
                .setSpriteBatch(this.spriteBatch);

        sgoColon1 =
                new SpriteGameObject(this.textureColon,
                                     locXc1, getLocationY())
                        .setSpriteBatch(this.spriteBatch);

        snMinute10 = new SpriteNumber(this.textureArrayOfNumbers, getTimeMinuteDig10(),
                                      locXm10, getLocationY()).setSpriteBatch(this.spriteBatch);
        snMinute1 = new SpriteNumber(this.textureArrayOfNumbers, getTimeMinuteDig1(),
                                     locXm1, getLocationY()).setSpriteBatch(this.spriteBatch);

        sgoColon2 = new SpriteGameObject(this.textureColon,
                                         locXc2, getLocationY())
                .setSpriteBatch(this.spriteBatch);

        snSecond10 = new SpriteNumber(this.textureArrayOfNumbers, getTimeSecondDig10(),
                                      locXs10, getLocationY()).setSpriteBatch(this.spriteBatch);
        snSecond1 = new SpriteNumber(this.textureArrayOfNumbers, getTimeSecondDig1(),
                                     locXs1, getLocationY()).setSpriteBatch(this.spriteBatch);
//        snHour10.rotate(rotPath);
//        snHour1.rotate(rotPath);
//        sgoColon1.rotate(rotPath);
//        snMinute10.rotate(rotPath);
//        snMinute1.rotate(rotPath);
//        sgoColon2.rotate(rotPath);
//        snSecond10.rotate(rotPath);
//        snSecond1.rotate(rotPath);


        childRenderables.add(snHour10, snHour1, sgoColon1);
        childRenderables.add(snMinute10, snMinute1, sgoColon2);
        childRenderables.add(snSecond10, snSecond1);

        setColorByRange();
    }

    public boolean isColorByRange() {
        return flagColorByRange;
    }

    public void setColorByRange(boolean colorByRange) {
        flagColorByRange = colorByRange;
    }

    private void setColorByRange() {
        if (isColorByRange()) {
            if (getTimeSec() < 0) {
                setColors(COLOR_DISABLED);
            } else {
                float ratio = getTimeSec() > getMaxNumSec() ? 1f :
                        (float) getTimeSec() / (float) (getMaxNumSec() - getMinNumSec());

                int r = getColorRatio(getRed(COLOR_MAX), getRed(COLOR_MIN), ratio);
                int g = getColorRatio(getGreen(COLOR_MAX), getGreen(COLOR_MIN), ratio);
                int b = getColorRatio(getBlue(COLOR_MAX), getBlue(COLOR_MIN), ratio);

                Gdx.app.log(tag, "ratio:" + ratio + " >> r:" + r + "  g:" + g + "  b:" + b);
                setColors(getColorNum(r, g, b));

            }
        }
    }

    private int getColorRatio(int max, int min, float ratio) {
        Gdx.app.log(tag, "max:" + max + ", min:" + min + ", ratio:" + ratio);
        int ret;
        ret = (int) (((float) (max - min)) * ratio) + min;

        return ret;
    }

    public void setColors(int colorNum) {
        snHour10.setColor(new Color(colorNum));
        snHour1.setColor(new Color(colorNum));
        sgoColon1.setColor(new Color(colorNum));
        snMinute10.setColor(new Color(colorNum));
        snMinute1.setColor(new Color(colorNum));
        sgoColon2.setColor(new Color(colorNum));
        snSecond10.setColor(new Color(colorNum));
        snSecond1.setColor(new Color(colorNum));
    }

    private int getRed(int color) {
        return (color * 0xff0000) >>> (8 * 3);
    }

    private int getGreen(int color) {
        return (color & 0x00ff0000) >>> (8 * 2);
    }

    private int getBlue(int color) {
        return (color & 0x0000ff00) >>> (8);
    }

    private int getColorNum(int r, int g, int b) {
        return (r << (8 * 3)) | (g << (8 * 2)) | b << 8 | 0xff;
    }


    private int getTimeHourDig10() {
        return (int) getTimeHour() / 10;
    }

    private int getTimeHourDig1() {
        return (int) getTimeHour() % 10;
    }

    private int getTimeMinuteDig10() {
        return (int) getTimeMinute() / 10;
    }

    private int getTimeMinuteDig1() {
        return (int) getTimeMinute() % 10;
    }

    private int getTimeSecondDig10() {
        return (int) getSecond() / 10;
    }

    private int getTimeSecondDig1() {
        return (int) getSecond() % 10;
    }

    private int getTimeHour() {
        return getTimeSec() < 0 ? 88 : (int) getTimeSec() / 3600;
    }

    private int getTimeMinute() {
        return getTimeSec() < 0 ? 88 : (int) (getTimeSec() % 3600) / 60;
    }

    private int getSecond() {
        return getTimeSec() < 0 ? 88 : (int) getTimeSec() % 60;
    }

    public int getTimeSec() {
        return timeSec;
    }

    public void setTimeSec(int timeSec) {
        this.timeSec = timeSec;

        snHour10.setNumber(getTimeHourDig10());
        snHour1.setNumber(getTimeHourDig1());
        snMinute10.setNumber(getTimeMinuteDig10());
        snMinute1.setNumber(getTimeMinuteDig1());
        snSecond10.setNumber(getTimeSecondDig10());
        snSecond1.setNumber(getTimeSecondDig1());

        setColorByRange();
    }

    public float getLocationX() {
        return locationX;
    }

    public void setLocationX(float locationX) {
        this.locationX = locationX;
    }

    public float getLocationY() {
        return locationY;
    }

    public void setLocationY(float locationY) {
        this.locationY = locationY;
    }

    public int getMaxNumSec() {
        return maxNumSec;
    }

    public void setMaxNumSec(int maxNumSec) {
        this.maxNumSec = maxNumSec;
    }

    public int getMinNumSec() {
        return minNumSec;
    }

    public void setMinNumSec(int minNumSec) {
        this.minNumSec = minNumSec;
    }

    @Override
    public void render(float delta) {
        for (Renderable renderable : childRenderables) {
            renderable.render(delta);
        }
    }
}
