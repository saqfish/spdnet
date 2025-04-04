package com.shatteredpixel.shatteredpixeldungeon.net.events;


import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.watabou.noosa.Game;

import com.fasterxml.jackson.core.JsonProcessingException;

import static com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon.net;

public class Send {
    public static final int INTERLEVEL = 0;
    public static final int MOVE = 1;
    public static final int ITEM = 2;
    public static final int DEATH = 3;
    public static final int BOSSKILL = 4;
    public static final int WIN = 5;
    public static final int RECORDSREQUEST = 6;

    public static class Message {
        public String data;
    }

    public static class Death {
        public String cause;

        public Death(Object cause) {
            if (cause instanceof Mob) this.cause = "was killed by a " + ((Mob) cause).name();
            else if (cause instanceof Chasm) this.cause = "fell to death";
            else if (cause instanceof Bleeding) this.cause = "bled to death";
            else if (cause instanceof Burning) this.cause = "burned to death";
            else this.cause = "died";
        }

        public Death(String whom) {
            this.cause = " killed" + whom;
        }
    }


    public static class Transfer {
        public String id;
        public String className;
        public int level;
        public boolean cursed;
        public boolean identified;

        public Transfer(Item item, String to) {
            this.id = to;
            this.className = clean(item.getClass().getName());
            this.level = item.level();
            this.cursed = item.cursed;
            this.identified = item.isIdentified();
        }
    }

    public static class Interlevel {
        public int playerClass;
        public int depth;
        public int pos;

        public Interlevel(int playerClass, int depth, int pos) {
            this.playerClass = playerClass;
            this.depth = depth;
            this.pos = pos;
        }
    }

    public static class Move {
        public int pos;

        public Move(int pos) {
            this.pos = pos;
        }
    }

    public static class Types {
        public static final int ALL = 0;
        public static final int WEAPON = 1;
        public static final int ARMOR = 2;
        public static final int ARTIFACT = 3;
        public static final int MISC = 4;
        public static final int RING = 5;
    }

    public static class NetItem {
        public int type;
        public String className;
        public int level;

        public NetItem(int t, Object i) {
            this.type = t;
            try {
                this.className = clean(i.getClass().getName());
                this.level = ((Item) i).level();
            } catch (Exception ignored) {
            }
        }

    }

    public static String clean(String name) {
        return name.replace(Game.pkgName + ".items.", "");
    }

    public static class NetItems {
        public int type;
        public NetItem weapon;
        public NetItem armor;
        public NetItem artifact;
        public NetItem misc;
        public NetItem ring;
    }

    public static class Items {
        public int type;
        public NetItem weapon;
        public NetItem armor;
        public NetItem artifact;
        public NetItem misc;
        public NetItem ring;

        public Items() {
            this.type = Types.ALL;
            this.weapon = new NetItem(Types.WEAPON, Dungeon.hero.belongings.weapon);
            this.armor = new NetItem(Types.ARMOR, Dungeon.hero.belongings.armor);
            this.artifact = new NetItem(Types.ARTIFACT, Dungeon.hero.belongings.artifact);
            this.misc = new NetItem(Types.MISC, Dungeon.hero.belongings.misc);
            this.ring = new NetItem(Types.RING, Dungeon.hero.belongings.ring);
        }
    }

    public static void sendItems() {
        Items items = new Items();
        try {
            String json = net().mapper().writeValueAsString(items);
            net().sender().sendAction(ITEM, json);
        } catch (JsonProcessingException ignored) {
        }
    }

    public static void sendSingleItem(Object o) {
        int type = 0;
        NetItem n = null;

        if (o instanceof KindOfWeapon) type = Types.WEAPON;
        if (o instanceof Armor) type = Types.ARMOR;
        if (o instanceof Artifact) type = Types.ARTIFACT;
        if (o instanceof KindofMisc) type = Types.MISC;
        if (o instanceof Ring) type = Types.RING;

        switch (type) {
            case Types.WEAPON:
                n = new NetItem(Types.WEAPON, Dungeon.hero.belongings.weapon);
                break;
            case Types.ARMOR:
                n = new NetItem(Types.ARMOR, Dungeon.hero.belongings.armor);
                break;
            case Types.ARTIFACT:
                n = new NetItem(Types.ARTIFACT, Dungeon.hero.belongings.artifact);
                break;
            case Types.MISC:
                n = new NetItem(Types.MISC, Dungeon.hero.belongings.misc);
                break;
            case Types.RING:
                n = new NetItem(Types.RING, Dungeon.hero.belongings.ring);
                break;
        }

        try {
            String json = net().mapper().writeValueAsString(n);
            net().sender().sendAction(ITEM, json);
        } catch (JsonProcessingException ignored) {
        }
    }
}
