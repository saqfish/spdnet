
/*
 * SPDnet
 * Copyright (C) 2025 saqfish
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.net;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Events;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Send;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.NetWindow;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.WndNetOptions;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.WndServerInfo;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.watabou.utils.DeviceCompat;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.json.JSONObject;

import static java.util.Collections.singletonMap;

public class Net {
    public static String DEFAULT_HOST = "saqfish.com";
    public static String DEFAULT_SCHEME = "http";
    public static String DEFAULT_KEY = "debug";
    public static long DEFAULT_ASSET_VERSION = 0;
    public static int DEFAULT_PORT = 5800;

    private Socket socket;
    private Receiver receiver;
    private Sender sender;
    private ObjectMapper mapper;
    private Dispatcher dispatcher;
    private ConnectionPool connectionPool;
    private Loader loader;
    private long seed;

    private NetWindow w;

    public Net(String address, String key){
        URI url = URI.create(address);
        Settings.scheme(url.getScheme());
        Settings.address(url.getHost());
        Settings.port(url.getPort());
        Settings.auth_key(key);
        session();
    }

    public Net(String address){
        this(address, DEFAULT_KEY);
    }

    public Net(){
        session();
    }

    public void reset() {
        session();
    }

    public void session(){
        URI url = Settings.uri();
        String key = Settings.auth_key();
        log(url.toString());
        IO.Options options = IO.Options.builder()
                .setAuth(singletonMap("token", key))
                .setQuery("version="+Game.versionCode)
                .build();
        socket = IO.socket(url, options);
        mapper = new ObjectMapper();
        connectionPool = new ConnectionPool();
        dispatcher = new Dispatcher();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .readTimeout(1, TimeUnit.MINUTES)
                .connectionPool(connectionPool)
                .build();
        loader = new Loader();
        receiver = new Receiver(this, mapper);
        sender = new Sender(this, mapper);
        setupEvents();
    }

    public void setupEvents(){
        Emitter.Listener onConnected = args -> {
            log("connected");

            if(w != null) {
                Game.runOnRenderThread( () -> w.destroy());
            }
            if(Game.scene() instanceof GameScene)
                ShatteredPixelDungeon.net().sender().sendAction(Send.INTERLEVEL, Dungeon.hero.heroClass.ordinal(), Dungeon.depth, Dungeon.hero.pos);
        };

        Emitter.Listener onDisconnected = args -> {
            log("disconnected");
        };

        // TODO: Clean this up or handle errors better
        Emitter.Listener onConnectionError = args -> {
            try {
                JSONObject data = (JSONObject)args[0];
                String json = data.getString("message");
                Events.Error e = mapper().readValue(json, Events.Error.class);
                if(e.type == 1){
                    NetWindow.message(Icons.get(Icons.CHANGES), "Update required", e.data);
                }else NetWindow.error(e.data);
            }catch(ClassCastException ce){
                try {
                    EngineIOException err = (EngineIOException) args[0];
                    NetWindow.error(err.getMessage());
                    log(err.getLocalizedMessage());
                }catch (Exception eignored) {
                    NetWindow.error("Connection could not be established!");
                }
            }catch(Exception ignored) {
                ignored.printStackTrace();
                NetWindow.error("Connection could not be established!");
            }
            receiver.cancelAll();
            disconnect();
        };

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);
        socket.on(Socket.EVENT_CONNECT, onConnected);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnected);
    }

    public void endEvents(){
        socket.off();
    }

    public void connect() {
        receiver.startAll();
        socket.connect();
    }
    public void disconnect(){
        receiver.cancelAll();
        socket.close();
    }
    public void finish() {
        log("gracefully finishing");
        if (socket.connected()) {
            log("disconnecting");
            socket.disconnect();
        }
        socket.off();
        dispatcher.cancelAll();
        connectionPool.evictAll();
        dispatcher.executorService().shutdown();
        socket = null;
    }

    public void toggle(WndServerInfo w) {
        this.w = w;
        if(!socket.connected() && !socket.io().isReconnecting())
            connect();
        else
            disconnect();
    }

    public void die(){
        if (socket != null) {
            disconnect();
            endEvents();
            socket = null;
        }
        receiver = null;
        sender = null;
    }


    public void seed(long seed) { this.seed = seed; }
    public long seed() { return this.seed; }

    public Boolean connected() { return socket != null && socket.connected(); }
    public Socket socket(){ return this.socket; }
    public ObjectMapper mapper() { return this.mapper;}
    public Sender sender() { return sender; }
    public Receiver reciever() { return receiver; }
    public Loader loader() { return loader; }
    public URI uri(){ return Settings.uri(); }
    public void log(String msg){ DeviceCompat.log("NET", msg);}
}
