package com.shatteredpixel.shatteredpixeldungeon.net.windows;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.net.Settings;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Receive;
import com.shatteredpixel.shatteredpixeldungeon.net.ui.NetIcons;
import com.shatteredpixel.shatteredpixeldungeon.net.ui.UI;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

import org.json.JSONObject;

import static com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon.net;

public class NetWindow extends Window {
    public NetWindow(int width, int height){
        super(width, height, UI.get(UI.Type.WINDOW));
    }
    public NetWindow(){
        super(0, 0, UI.get(UI.Type.WINDOW));
    }

    public static void message(Image i, String title, String message){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndMessage(i, title, message)));
    }

    public static void message(String title, String message){
        message(NetIcons.get(NetIcons.GLOBE), title, message);
    }

    public static void message(String message){
        message(NetIcons.get(NetIcons.GLOBE), "Server Message", message);
    }

    public static void error(String message){
        message(NetIcons.get(NetIcons.ALERT), "Connection Error", message);
    }

    public static void error(String title, String message){
        message(NetIcons.get(NetIcons.ALERT), title, message);
    }

    public static void runWindow(Window w){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(w));
    }

    public static void showSettings(){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndNetSettings()));
    }

    /*public static void showChat(){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndChat()));
    }*/

    public static void showServerInfo(){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndServerInfo()));
    }

    public static void showKeyInput(){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndTextInput("Enter key",null, Settings.auth_key(), 30, false, "Set", "Cancel"){
            @Override
            public void onSelect(boolean positive, String text) {
                if(positive){
                    Settings.auth_key(text);
                    net().reset();
                }
            }
        }));
    }

    public static void init(String motd, long seed){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndMotd(motd,seed)));
    }

    public static void showPlayerList(Receive.PlayerList p){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndPlayerList(p)));
    }

    public static void showRanking(JSONObject recordsData){
        //Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(new WndNetRanking(recordsData)));
    }

    public static void show(Window w){
        Game.runOnRenderThread(() -> ShatteredPixelDungeon.scene().add(w));
    }

}
