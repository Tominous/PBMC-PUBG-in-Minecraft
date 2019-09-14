package net.aluix.pubg.game.event.update;

import net.aluix.pubg.utils.UtilTime;

public enum UpdateType
{
    MIN_64("MIN_64", 0, "MIN_64", 0, 3840000L), 
    MIN_32("MIN_32", 1, "MIN_32", 1, 1920000L), 
    MIN_16("MIN_16", 2, "MIN_16", 2, 960000L), 
    MIN_08("MIN_08", 3, "MIN_08", 3, 480000L), 
    MIN_04("MIN_04", 4, "MIN_04", 4, 240000L), 
    MIN_02("MIN_02", 5, "MIN_02", 5, 120000L), 
    MIN_01("MIN_01", 6, "MIN_01", 6, 60000L), 
    SLOWEST("SLOWEST", 7, "SLOWEST", 7, 32000L), 
    SLOWER("SLOWER", 8, "SLOWER", 8, 16000L), 
    SLOW("SLOW", 9, "SLOW", 9, 4000L), 
    SEC("SEC", 10, "SEC", 10, 1000L), 
    FAST("FAST", 11, "FAST", 11, 500L), 
    FASTER("FASTER", 12, "FASTER", 12, 250L), 
    FASTEST("FASTEST", 13, "FASTEST", 13, 125L), 
    TICK("TICK", 14, "TICK", 14, 49L);
    
    private long _time;
    private long _last;
    private long _timeSpent;
    private long _timeCount;
    
    private UpdateType(final String s2, final int n2, final String s, final int n, final long time) {
        this._time = time;
        this._last = System.currentTimeMillis();
    }
    
    public boolean Elapsed() {
        if (UtilTime.elapsed(this._last, this._time)) {
            this._last = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    
    public void StartTime() {
        this._timeCount = System.currentTimeMillis();
    }
    
    public void StopTime() {
        this._timeSpent += System.currentTimeMillis() - this._timeCount;
    }
    
    public void PrintAndResetTime() {
        System.out.println(String.valueOf(String.valueOf(this.name())) + " in a second: " + this._timeSpent);
        this._timeSpent = 0L;
    }
}
