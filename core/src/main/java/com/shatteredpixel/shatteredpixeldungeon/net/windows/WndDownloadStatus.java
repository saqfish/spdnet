package com.shatteredpixel.shatteredpixeldungeon.net.windows;

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;

public class WndDownloadStatus extends NetWindow {

    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 144;
    private static final int HEIGHT	= 120;

    private static int SUCCESS =  0x00FF00;
    private static int FAIL = 0xFF0000;

    private ScrollPane pane;
    private Component list;
    private StatusItem success;
    private StatusItem fail;
    private RedButton reloadBtn;

    private float lastY;

    private WndDownloadStatus w = this;

    public WndDownloadStatus() {
        super(PixelScene.landscape() ? WIDTH_L : WIDTH_P, HEIGHT);

        float y;

        pane = new ScrollPane(new Component());
        add(pane);

        list = pane.content();
        list.clear();

        pane.scrollTo( 0, 0 );

        lastY = 0;

        y = height - 20;

        reloadBtn = new RedButton("Reload"){
            @Override
            protected void onClick() {
                super.onClick();
                TextureCache.clear();
                if (GamesInProgress.checkAll().size() == 0){
                    GamesInProgress.selectedClass = null;
                    GamesInProgress.curSlot = 1;
                    if(!(ShatteredPixelDungeon.scene() instanceof HeroSelectScene))
                        ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
                    else ShatteredPixelDungeon.switchNoFade(HeroSelectScene.class);

                } else {
                    ShatteredPixelDungeon.switchNoFade( StartScene.class );
                }
            }
        };
        reloadBtn.enable(false);
        add(reloadBtn);
        reloadBtn.setRect(width-40, y, 40, 20);

        list.setRect(0, pane.top(), width, y);
        pane.setRect( 0, 0, width, y);

        success = new StatusItem("Updated: ");
        success.setRect(0, y+2, width, 12);
        add(success);

        y += success.height();

        fail = new StatusItem("Default: ");
        fail.setRect(0, y, width, 12);
        add(fail);

    }

    public void addFile(String str, boolean succeeded, int count){
        String[] split = str.split("/");
        String filename = split[split.length-1];

        int color;

        if(succeeded) {
            color = SUCCESS;
            success.setCount(count);
        }else{
            color = FAIL;
            fail.setCount(count);
        }

        RenderedTextBlock entry = PixelScene.renderTextBlock(filename, 9);
        entry.hardlight(color);
        entry.setSize(width, 24);
        entry.setPos(0, lastY);

        lastY += entry.height()+1;

        list.add(entry);
        list.setSize(width,lastY);

        if (list.bottom() > pane.height())
            pane.scrollTo(0, list.bottom() - pane.height() + 2);

    }

    public void complete(boolean status){
        reloadBtn.enable(status);
    }

    public static class StatusItem extends Component {
        private RenderedTextBlock label;
        private RenderedTextBlock count;

        public StatusItem(String name){
            label = PixelScene.renderTextBlock(name, 9);
            add(label);

            count = PixelScene.renderTextBlock("0", 9);
            add(count);

        }

        @Override
        protected void layout() {
            super.layout();

            label.setRect(x, y, label.width(), label.height());
            count.setPos(label.width(), y);
        }

        public void setCount(int cnt){
            count.text(Integer.toString(cnt));
        }
    }
}
