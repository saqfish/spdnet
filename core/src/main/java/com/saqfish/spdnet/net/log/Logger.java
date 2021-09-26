package com.saqfish.spdnet.net.log;

import com.saqfish.spdnet.Badges;
import com.saqfish.spdnet.Dungeon;
import com.saqfish.spdnet.ShatteredPixelDungeon;
import com.saqfish.spdnet.Statistics;
import com.saqfish.spdnet.actors.Actor;
import com.saqfish.spdnet.actors.hero.Hero;
import com.saqfish.spdnet.actors.mobs.npcs.Blacksmith;
import com.saqfish.spdnet.actors.mobs.npcs.Ghost;
import com.saqfish.spdnet.actors.mobs.npcs.Imp;
import com.saqfish.spdnet.actors.mobs.npcs.Wandmaker;
import com.saqfish.spdnet.items.Generator;
import com.saqfish.spdnet.items.Item;
import com.saqfish.spdnet.items.potions.Potion;
import com.saqfish.spdnet.items.rings.Ring;
import com.saqfish.spdnet.items.scrolls.Scroll;
import com.saqfish.spdnet.journal.Notes;
import com.saqfish.spdnet.levels.Level;
import com.saqfish.spdnet.levels.rooms.secret.SecretRoom;
import com.saqfish.spdnet.levels.rooms.special.SpecialRoom;
import com.saqfish.spdnet.messages.Messages;
import com.saqfish.spdnet.scenes.InterlevelScene;
import com.saqfish.spdnet.ui.QuickSlotButton;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Logger {
    private List<Bundle> log;

    private static final String VERSION		= "version";
    private static final String SEED		= "seed";
    private static final String CHALLENGES	= "challenges";
    private static final String MOBS_TO_CHAMPION	= "mobs_to_champion";
    private static final String HERO		= "hero";
    private static final String GOLD		= "gold";
    private static final String DEPTH		= "depth";
    private static final String DROPPED     = "dropped%d";
    private static final String PORTED      = "ported%d";
    private static final String LEVEL		= "level";
    private static final String LIMDROPS    = "limited_drops";
    private static final String CHAPTERS	= "chapters";
    private static final String QUESTS		= "quests";
    private static final String BADGES		= "badges";

    public Logger(){
        this.log = new ArrayList<>();
    }

    public void snapshot (){
        Bundle bundle = new Bundle();

        bundle.put( VERSION, Dungeon.version );
        bundle.put( SEED, Dungeon.seed );
        bundle.put( CHALLENGES, Dungeon.challenges );
        bundle.put( MOBS_TO_CHAMPION, Dungeon.mobsToChampion );
        bundle.put( HERO, Dungeon.hero );
        bundle.put( GOLD, Dungeon.gold );
        bundle.put( DEPTH, Dungeon.depth );
        bundle.put( LEVEL, Dungeon.level );

        for (int d : Dungeon.droppedItems.keyArray()) {
            bundle.put(Messages.format(DROPPED, d), Dungeon.droppedItems.get(d));
        }

        for (int p : Dungeon.portedItems.keyArray()){
            bundle.put(Messages.format(PORTED, p), Dungeon.portedItems.get(p));
        }

        Dungeon.quickslot.storePlaceholders( bundle );

        Bundle limDrops = new Bundle();
        Dungeon.LimitedDrops.store( limDrops );
        bundle.put ( LIMDROPS, limDrops );

        int count = 0;
        int ids[] = new int[Dungeon.chapters.size()];
        for (Integer id : Dungeon.chapters) {
            ids[count++] = id;
        }
        bundle.put( CHAPTERS, ids );

        Bundle quests = new Bundle();
        Ghost.Quest.storeInBundle( quests );
        Wandmaker.Quest.storeInBundle( quests );
        Blacksmith.Quest.storeInBundle( quests );
        Imp.Quest.storeInBundle( quests );
        bundle.put( QUESTS, quests );

        SpecialRoom.storeRoomsInBundle( bundle );
        SecretRoom.storeRoomsInBundle( bundle );

        Statistics.storeInBundle( bundle );
        Notes.storeInBundle( bundle );
        Generator.storeInBundle( bundle );

        Scroll.save( bundle );
        Potion.save( bundle );
        Ring.save( bundle );

        Actor.storeNextID( bundle );

        Bundle badges = new Bundle();
        Badges.saveLocal( badges );
        bundle.put( BADGES, badges );

        log.add(bundle);
    }

    public Bundle getLog(int pos) {
        if(log.size() > 0 )
            return log.get(pos);
        else return null;
    }

    public List<Bundle> getLogs() {
        return log;
    }

    public void printLog(int pos){
        //String dump = ShatteredPixelDungeon.net().sender().map(log.get(pos));
        String dump = log.get(pos).toString();
        System.out.println(dump);
    }

    public void printLastLog(){
        printLog(log.size()-1);
    }

    public void loadSnapshot(Bundle bundle){
        Dungeon.version = bundle.getInt( VERSION );

        Dungeon.seed = bundle.getLong( SEED );

        Actor.clear();
        Actor.restoreNextID( bundle );

        Dungeon.quickslot.reset();
        QuickSlotButton.reset();

        Dungeon.challenges = bundle.getInt( CHALLENGES );
        Dungeon.mobsToChampion = bundle.getInt( MOBS_TO_CHAMPION );

        Dungeon.level = null;
        Dungeon.depth = -1;

        Scroll.restore( bundle );
        Potion.restore( bundle );
        Ring.restore( bundle );

        Dungeon.quickslot.restorePlaceholders( bundle );


        Dungeon.chapters = new HashSet<>();
        int ids[] = bundle.getIntArray( CHAPTERS );
        if (ids != null) {
            for (int id : ids) {
                Dungeon.chapters.add( id );
            }
        }

        Bundle quests = bundle.getBundle( QUESTS );
        if (!quests.isNull()) {
            Ghost.Quest.restoreFromBundle( quests );
            Wandmaker.Quest.restoreFromBundle( quests );
            Blacksmith.Quest.restoreFromBundle( quests );
            Imp.Quest.restoreFromBundle( quests );
        } else {
            Ghost.Quest.reset();
            Wandmaker.Quest.reset();
            Blacksmith.Quest.reset();
            Imp.Quest.reset();
        }

        SpecialRoom.restoreRoomsFromBundle(bundle);
        SecretRoom.restoreRoomsFromBundle(bundle);

        Bundle badges = bundle.getBundle(BADGES);
        if (!badges.isNull()) {
            Badges.loadLocal( badges );
        } else {
            Badges.reset();
        }

        Notes.restoreFromBundle( bundle );

        Dungeon.hero = null;
        Dungeon.hero = (Hero)bundle.get( HERO );

        Dungeon.gold = bundle.getInt( GOLD );
        Dungeon.depth = bundle.getInt( DEPTH );

        Statistics.restoreFromBundle( bundle );
        Generator.restoreFromBundle( bundle );

        Dungeon.droppedItems = new SparseArray<>();
        Dungeon.portedItems = new SparseArray<>();
        for (int i=1; i <= 26; i++) {

            //dropped items
            ArrayList<Item> items = new ArrayList<>();
            if (bundle.contains(Messages.format( DROPPED, i )))
                for (Bundlable b : bundle.getCollection( Messages.format( DROPPED, i ) ) ) {
                    items.add( (Item)b );
                }
            if (!items.isEmpty()) {
                Dungeon.droppedItems.put( i, items );
            }

            //ported items
            items = new ArrayList<>();
            if (bundle.contains(Messages.format( PORTED, i )))
                for (Bundlable b : bundle.getCollection( Messages.format( PORTED, i ) ) ) {
                    items.add( (Item)b );
                }
            if (!items.isEmpty()) {
                Dungeon.portedItems.put( i, items );
            }
        }

        Dungeon.level = null;
        Actor.clear();

        Dungeon.level = (Level)bundle.get( LEVEL );

        InterlevelScene.mode = InterlevelScene.Mode.LOG;
        InterlevelScene.returnDepth = Dungeon.depth;
        InterlevelScene.returnPos = Dungeon.hero.pos;
        ShatteredPixelDungeon.switchNoFade( InterlevelScene.class );
    }
}
