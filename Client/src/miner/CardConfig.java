package miner;

import miner.MinerApplet;

/**
 *
 * @author Petr Svenda
 */
public class CardConfig {
    int targetReaderIndex = 0;
    public Class appletToSimulate = MinerApplet.class;
    
    public enum CARD_TYPE {
        PHYSICAL, JCOPSIM, JCARDSIMLOCAL, JCARDSIMREMOTE
    }
    public CARD_TYPE testCardType = CARD_TYPE.PHYSICAL;
    
    public static CardConfig getDefaultConfig() {
        CardConfig runCfg = new CardConfig();
        runCfg.targetReaderIndex = 0;
        runCfg.testCardType = CARD_TYPE.PHYSICAL;
        runCfg.appletToSimulate = MinerApplet.class;
        
        return runCfg;
    }
}
