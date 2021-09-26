package com.saqfish.spdnet.net.windows;

import static com.saqfish.spdnet.ShatteredPixelDungeon.net;

import com.saqfish.spdnet.net.ui.BlueButton;
import com.saqfish.spdnet.scenes.PixelScene;
import com.saqfish.spdnet.ui.ScrollPane;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundle;

import java.util.List;

public class WndLogList extends NetWindow{
    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 144;
    private static final int HEIGHT	= 120;

    private ScrollPane list;
    private Component content;
    private List<Bundle> bundles;

    WndLogList(){
        super(PixelScene.landscape() ? WIDTH_L : WIDTH_P, HEIGHT);

        ScrollPane list = new ScrollPane( new Component() );
        add( list );

        Component content = list.content();
        content.clear();

        list.scrollTo( 0, 0 );

        bundles = net().logger().getLogs();

        float ypos = 0;


        for(int index = 0; index< bundles.size(); index++){
            int finalIndex = index;
            BlueButton bBtn = new BlueButton(""+ (finalIndex)){
                @Override
                protected void onClick() {
                    super.onClick();
                    //net().logger().printLog(index);
                    Bundle snapshot = net().logger().getLog(finalIndex);
                    net().logger().loadSnapshot(snapshot);
                }
            };
            bBtn.setSize(width, 12);
            bBtn.setPos(0, ypos);

            content.add(bBtn);

            ypos = bBtn.bottom();
        }

        content.setRect(0, list.top(), width, ypos );
        list.setRect( 0, 0, width, HEIGHT);

        resize(width, (int)list.bottom());
    }
}
