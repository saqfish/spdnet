package com.shatteredpixel.shatteredpixeldungeon.net;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Events;
import com.shatteredpixel.shatteredpixeldungeon.net.events.Send;
import com.shatteredpixel.shatteredpixeldungeon.net.windows.NetWindow;
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene;
import com.watabou.noosa.Game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.socket.client.Ack;

import static com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon.net;


public class Sender {
        private ObjectMapper mapper;
        private Net net;

        public Sender(Net net, ObjectMapper mapper){
                this.net = net;
                this.mapper = mapper;
        }

        public void sendPlayerListRequest(){ net.socket().emit(Events.PLAYERLISTREQUEST, 0); }
        public void sendRecordsRequest(){ net.socket().emit(Events.RECORDS, 0); }

        public void sendTransfer(Item i, String id, Heap h) {
                Send.Transfer item = new Send.Transfer(i, id);
                net.socket().emit(Events.TRANSFER, map(item), (Ack) args -> {
                    try {
                            Boolean enabled = (Boolean)args[0];
                            if(enabled) h.remove(i);
                    }catch(Exception e){ }
                });
        }

        public void sendAction(int type, String s) {
                if(net.socket().connected()) net.socket().emit(Events.ACTION,type, s);
        }

        public void sendChat(String message) {
                if(net.socket().connected()&& message != null) net.socket().emit(Events.CHAT, message);
        }

        public void sendAction(int type, int... data) {
                String json = "";
                switch (type) {
                        case Send.INTERLEVEL:
                                Send.Interlevel d;
                                d = new Send.Interlevel(data[0], data[1], data[2]);
                                json = map(d);
                                break;
                }
                if(net.socket().connected()&& json != null) net.socket().emit(Events.ACTION,type, json);
        }

        public void sendAction(int type, Object o) {
                String json = "";
                Object c = null;
                switch (type) {
                        case Send.DEATH:
                                c = new Send.Death(o);
                                break;
                        case Send.BOSSKILL:
                                c = new Send.Death((String)o);
                                break;
                        case Send.MOVE:
                                c = new Send.Move(((Integer)o));
                }
                json = map(c);
                if(net.socket().connected() && json != null) net.socket().emit(Events.ACTION,type, json);
        }

        public static void sendWin(){
                if(net().connected()) {
                        net().sender().sendAction(Send.WIN, 0);
                        Dungeon.win(Amulet.class);
                        Dungeon.deleteGame(GamesInProgress.curSlot, true);
                        Game.switchScene(RankingsScene.class);
                }else
                        NetWindow.error("You're not connected!\nTo record your win to the server, connect first.");
        }

        // Object -> String
        public String map(Object o){
                try {
                        return mapper.writeValueAsString(o);
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
                return null;
        }
}
