package net.godly.pubg.game.achievment;

public class Achievemnt
{
    private String name;
    private String description;
    
    public Achievemnt(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
