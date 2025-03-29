package com.shatteredpixel.shatteredpixeldungeon.net.events;

public class Events {
    public static final String INIT = "init";
    public static final String ACTION = "action";
    public static final String PLAYERLISTREQUEST = "playerlistrequest";
    public static final String TRANSFER = "transfer";
    public static final String CHAT = "chat";
    public static final String RECORDS = "records";
    public static final String JOIN = "join";
    public static final String LEAVE = "leave";

    public static class Error {
        public int type;
        public String data;
    }
}
