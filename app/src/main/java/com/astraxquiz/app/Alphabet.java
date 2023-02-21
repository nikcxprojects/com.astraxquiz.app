package com.astraxquiz.app;

public class Alphabet {

    int pos;
    String letter;
    boolean active;

    public Alphabet(){}

    public Alphabet(int pos, String letter, boolean active) {
        this.pos = pos ;
        this.letter = letter ;
        this.active = active ;

    }

    public int getPos(){ return pos; }
    public boolean getActive(){ return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getLetter(){ return letter; }
}
