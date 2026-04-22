/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_jeu.model;

/**
 *
 * @author cano28
 */
import java.util.List;
import java.util.Map;

public class Puzzle {
    private String type;
    private String prompt;
    private String image;
    private List<String> choices;
    private Map<String, String> routes;

    public String getType() {
    return type;
   }

   public String getPrompt() {
    return prompt;
   }

   public String getImage() {
    return image;
   }

   public List<String> getChoices() {
    return choices;
   }
   
   public Map<String, String> getRoutes() {
        return routes;
    }
   
   public String getNextPuzzleId(String input) {
    if (routes == null || routes.isEmpty()) {
        return null;
    }

    if (input != null && routes.containsKey(input)) {
        return routes.get(input);
    }

    return routes.get("*");
    }

}
