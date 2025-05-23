package com.shatteredpixel.shatteredpixeldungeon.net.events;

public class Receive {

    public static final int MOVE = 0;
    public static final int JOIN = 1;
    public static final int JOIN_LIST = 2;
    public static final int LEAVE = 3;
    public static final int ITEM = 4;
    public static final int GLOG = 5;

    public static class Init {
        public String motd;
        public long seed;
        public long assetVersion;
    }

    public static class Transfer {
        public String id;
        public String className;
        public int level;
        public int count;
        public boolean cursed;
        public boolean identified;
    }


    public static class Record {
        public String nick;
        public Integer playerClass;
        public Integer depth;
        public NetItems items;
        public int wins;
    }

    public static class Records {
        public Record[] records;
    }

    public static class Player {
        public String nick;
        public Integer playerClass;
        public Integer depth;
        public NetItems items;
        public Integer role;
        public String id;
    }

    public static class PlayerList {
        public Player[]list;
    }


    public static class Action {
        public int type;
        public String data;
    }


    public static class Glog {
        public String msg;
    }

    public static class Join {
        public String id;
        public int playerClass;
        public String nick;
        public int depth;
        public int pos;
        public NetItems items;
    }

    public static class NetItem {
        public int type;
        public String className;
        public int level;
    }

    public static class NetItems {
        public int type;
        public NetItem weapon;
        public NetItem armor;
        public NetItem artifact;
        public NetItem misc;
        public NetItem ring;
    }

    public static class JoinList {
        public Join[]players;
    }

    public static class Leave {
        public String id;
        public int playerClass;
        public String nick;
        public int depth;
        public int pos;
    }

    public static class Move {
        public String id;
        public int playerClass;
        public String nick;
        public int depth;
        public int pos;
    }
}
