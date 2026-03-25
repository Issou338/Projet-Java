/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_jeu.model;

/**
 *
 * @author cano28
 */
import java.util.Map;

public class Gamedata {
    private String title;
    private String author;
    private String start;
    private Map<String, Puzzle> puzzles;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getStart() {
        return start;
    }

    public Puzzle getPuzzle(String id) {
        return puzzles.get(id);
    }

    public boolean hasPuzzle(String id) {
        return puzzles.containsKey(id);
    }
    
}
