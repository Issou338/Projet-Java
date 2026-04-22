/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MoteurDeJeu;

/**
 *
 * @author cano28
 */
 // Exception lancée quand un scénario est invalide ou impossible à charger.
public class ChargementJeuException extends Exception {

    public ChargementJeuException(String message) {

        super(message);

    }

}