package com.shatteredpixel.shatteredpixeldungeon.net;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.net.ui.NetIcons;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.WndDownloadStatus;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.WndMotd;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptionsCondensed;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Loader {
    private String root;
    //TODO: load asset root from server
    private static String defaultRoot = "https://saqfish.com/assets/";

    private List<String> assets;

    private Window confirmWindow;
    private WndDownloadStatus statusWindow;

    private int count;
    private int failures;
    private int successes;
    private long assetVersion;

    Loader(String root){
        this.root = root;
        this.assets = new ArrayList<String>();
    }

    Loader(){
        this(defaultRoot);
    }

    public void download(String from, String to){
        Game.runOnRenderThread(()->
                Pixmap.downloadFromUrl(from, new Pixmap.DownloadPixmapResponseListener() {
                    @Override
                    public void downloadComplete(Pixmap pixmap) {
                        DeviceCompat.log("Assets <-", from);
                        PixmapIO.writePNG(Gdx.files.external(FileUtils.getDefaultPath() + to), pixmap);
                        DeviceCompat.log("Assets ->", to);
                        statusWindow.addFile(from, true, ++successes);
                        if((successes+failures) == count){
                            Settings.asset_version(assetVersion);
                            statusWindow.complete((successes + failures) == count);
                        }
                        pixmap.dispose();
                    }

                    @Override
                    public void downloadFailed(Throwable t) {
                        DeviceCompat.log("Assets !- ", from);
                        statusWindow.addFile(from, false, ++failures);
                        statusWindow.complete((successes+failures) == count);
                    }

                })
        );
    }

    public void addToAssets(List<String> list, Class c){
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            try {
                list.add( (String) field.get(c));
                count++;
            } catch (IllegalAccessException ignored) { }
        }
    }

    public void downloadAllAssets(long a){
        assetVersion = a;

        Game.runOnRenderThread(() -> {
            ShatteredPixelDungeon.scene().add(new WndOptionsCondensed(NetIcons.get(NetIcons.ALERT), "Missing Assets", "Some assets are either missing or outdated, download?", "Download", "Cancel"){
                @Override
                protected void onSelect(int index) {
                    super.onSelect(index);
                    if(index == 0){

                        addToAssets(assets, Assets.Environment.class);
                        addToAssets(assets, Assets.Sprites.class);
                        addToAssets(assets, Assets.Interfaces.class);

                        Game.runOnRenderThread(() -> {
                            for (String file : assets) {
                                if(statusWindow == null) {
                                    statusWindow = new WndDownloadStatus();
                                    ShatteredPixelDungeon.scene().add(statusWindow);
                                }
                                download(root+file, file);
                            }
                        });

                    } else {
                        this.destroy();
                    }

                }
            });
        });
    }

    public void clear(){
        assets.clear();
    }
}
