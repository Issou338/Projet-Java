package ui;

import javax.swing.*;
import java.awt.*;

import MoteurDeJeu.MoteurDeJeu;
import com.mycompany.projet_jeu.model.Puzzle;

public class FenetreJeu extends JFrame {

    private MoteurDeJeu moteur;
    private JTextArea zoneTexte = new JTextArea(6, 40);
    private JLabel labelImage = new JLabel("", SwingConstants.CENTER);
    private JPanel panelChoix = new JPanel();

    public FenetreJeu(MoteurDeJeu moteur) {
        this.moteur = moteur;

        // 1. Paramètres de la fenêtre
        setTitle("L'aventure de l'ile aux pirates");
        // CORRECTION 1 : Fenêtre un poil plus large (850) pour que les 5 boutons rentrent
        setSize(850, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- AMBIANCE VIEUX PARCHEMIN POUR LE TEXTE ---
        zoneTexte.setEditable(false);
        zoneTexte.setLineWrap(true);
        zoneTexte.setWrapStyleWord(true);
        zoneTexte.setFont(new Font("Papyrus", Font.BOLD, 22)); 
        zoneTexte.setBackground(new Color(240, 228, 198)); 
        zoneTexte.setForeground(new Color(60, 40, 20)); 
        zoneTexte.setMargin(new Insets(20, 20, 20, 20));

        // 2. Assemblage de l'écran avec un fond "Cale de navire"
        Color fondNavire = new Color(30, 15, 5); 

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(fondNavire);
        
        JScrollPane scrollPane = new JScrollPane(zoneTexte);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        
        panelCentral.add(scrollPane, BorderLayout.NORTH);
        panelCentral.add(labelImage, BorderLayout.CENTER);

        panelChoix.setBackground(fondNavire);

        add(panelCentral, BorderLayout.CENTER);
        add(panelChoix, BorderLayout.SOUTH);

        // 3. Démarrage
        mettreAJourAffichage();
        setVisible(true);
    }

    private void mettreAJourAffichage() {
        Puzzle puzzle = moteur.getPuzzleActuel();

        // --- HAUT DE L'ÉCRAN : TEXTE ET IMAGE ---
        zoneTexte.setText(puzzle.getPrompt());

        if (puzzle.getImage() != null && !puzzle.getImage().isEmpty()) {
            ImageIcon iconeOriginale = new ImageIcon(puzzle.getImage());
            Image imageRedimensionnee = iconeOriginale.getImage().getScaledInstance(700, 350, Image.SCALE_SMOOTH);
            labelImage.setIcon(new ImageIcon(imageRedimensionnee));
        } else {
            labelImage.setIcon(null);
        }

        // --- BAS DE L'ÉCRAN : LES CHOIX ---
        panelChoix.removeAll();

        // 1. D'ABORD : On affiche les actions du jeu (Texte ou QCM)
        if ("texte".equals(puzzle.getType())) {
            JTextField champ = new JTextField(10);
            champ.setFont(new Font("Papyrus", Font.BOLD, 18));
            champ.setBackground(new Color(240, 228, 198));
            champ.setForeground(new Color(60, 40, 20));
            champ.addActionListener(e -> envoyerReponse(champ.getText())); 
            
            panelChoix.add(champ);
            creerBouton("Valider", e -> envoyerReponse(champ.getText()));
        } 
        // CORRECTION 2 : On rajoute le bloc pour l'énigme finale (Forêt/Mer)
        else if ("boolean".equals(puzzle.getType())) {
            creerBouton("La forêt", e -> envoyerReponse("false"));
            creerBouton("La mer", e -> envoyerReponse("true"));
        }
        else if (puzzle.getChoices() != null) {
            for (String choix : puzzle.getChoices()) {
                creerBouton(choix, e -> envoyerReponse(choix));
            }
        }

        // 2. ENSUITE : On affiche toujours le bouton de sortie (Abandonner ou Quitter)
        if (!moteur.estTermine()) {
            creerBouton("Abandonner", e -> System.exit(0));
        } else {
            creerBouton("Quitter le jeu", e -> System.exit(0));
        }

        panelChoix.revalidate();
        panelChoix.repaint();
    }

    // ==========================================
    // MES OUTILS 
    // ==========================================

    private void envoyerReponse(String reponse) {
        moteur.passerAuPuzzleSuivant(reponse.trim());
        mettreAJourAffichage();
    }

    private void creerBouton(String texte, java.awt.event.ActionListener action) {
        JButton bouton = new JButton(texte);
        bouton.addActionListener(action);
        
        // Design Pirate
        bouton.setBackground(new Color(101, 67, 33)); 
        bouton.setForeground(new Color(255, 215, 0)); 
        bouton.setFont(new Font("Papyrus", Font.BOLD, 18)); 
        bouton.setFocusPainted(false); 
        
        // CORRECTION 3 : Marges intérieures réduites (5 et 10) pour gagner de la place
        bouton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 20, 10), 3), 
            BorderFactory.createEmptyBorder(5, 10, 5, 10)            
        ));
        
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR)); 

        panelChoix.add(bouton);
    }
}