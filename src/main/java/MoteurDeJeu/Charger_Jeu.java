/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MoteurDeJeu;

/**
 *
 * @author cano28
 */

import com.google.gson.Gson;
import java.io.FileReader;
import com.mycompany.projet_jeu.model.Gamedata;

public class Charger_Jeu {

    public static Gamedata chargerJeu() throws Exception {
        Gson gson = new Gson();
        FileReader reader = new FileReader("manifest.json");
        return gson.fromJson(reader, Gamedata.class);
    }
}
//test bra,nche clems