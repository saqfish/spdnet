package com.shatteredpixel.shatteredpixeldungeon.net.windows;

import com.shatteredpixel.shatteredpixeldungeon.net.Settings;
import com.shatteredpixel.shatteredpixeldungeon.net.ui.NetIcons;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;

import static com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon.net;

public class WndServerInfo extends NetWindow {
    private static final int WIDTH_P	    = 122;
    private static final int WIDTH_L	    = 223;
    private static final int BTN_HEIGHT	    = 18;

    private static final float GAP          = 2;

    IconTitle title;
    RenderedTextBlock host;
    IconButton keyBtn;
    RedButton connectBtn;

    WndServerInfo self = this;

    int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

    public WndServerInfo() {
        super();

        int height, y = 0;

        int maxWidth = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        title = new IconTitle(NetIcons.get(NetIcons.GLOBE), "Server Connection");
        title.setRect(0, 0, maxWidth, 20);
        add(title);

        float bottom = y;
        bottom = title.bottom();

        host = PixelScene.renderTextBlock("Host: " +Settings.uri().toString(), 9);
        host.maxWidth(maxWidth);
        host.setPos(0, bottom + GAP);
        add(host);

        bottom = host.bottom() + GAP+3;

        Image keyIcon = NetIcons.get(NetIcons.KEY);
        keyIcon.scale.set(0.8f);

        keyBtn = new IconButton(keyIcon) {
            @Override
            protected void onClick() {
                NetWindow.showKeyInput();
            }
        };

        keyBtn.icon(NetIcons.get(NetIcons.KEY));
        keyBtn.icon().scale.set(PixelScene.align(0.8f));
        add(keyBtn);

        keyBtn.setSize(16, BTN_HEIGHT);
        keyBtn.setPos(width - 16, bottom);

        float finalBottom = bottom;
        connectBtn = new RedButton("Connect") {
            @Override
            public synchronized void update() {
                super.update();
                text.text(net().connected() ? "Disconnect" : "Connect");
                connectBtn.setRect(keyBtn.left() - connectBtn.width(), finalBottom, maxWidth/2 , BTN_HEIGHT);
            }

            @Override
            protected void onClick() {
                super.onClick();
                net().toggle(self);
            }
        };
        add(connectBtn);
        connectBtn.setSize(maxWidth/2, BTN_HEIGHT);
        connectBtn.setPos(keyBtn.left() - connectBtn.width(), bottom);

        height = (int) (connectBtn.bottom() + GAP/2);

        resize(maxWidth, height);
    }
}