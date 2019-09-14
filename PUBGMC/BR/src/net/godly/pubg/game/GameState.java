package net.godly.pubg.game;

public enum GameState
{
    RUNNING("RUNNING", 0), 
    PREPARING("PREPARING", 1), 
    WAITING("WAITING", 2), 
    RUNNING_GRACEPERIOD("RUNNING_GRACEPERIOD", 3, GameState.RUNNING), 
    RUNNING_DEATHMATCH("RUNNING_DEATHMATCH", 4, GameState.RUNNING), 
    CLEANUP("CLEANUP", 5), 
    FAILED("FAILED", 6), 
    MISC("MISC", 7, GameState.RUNNING);
    
    private GameState root;
    
    private GameState(final String s, final int n) {
    }
    
    private GameState(final String s, final int n, final GameState root) {
        this.root = root;
    }
    
    public GameState getRoot() {
        if (this.root == null) {
            return this;
        }
        return this.root;
    }
}
