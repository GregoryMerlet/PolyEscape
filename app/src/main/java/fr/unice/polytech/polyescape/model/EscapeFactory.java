package fr.unice.polytech.polyescape.model;

import fr.unice.polytech.polyescape.R;

/**
 * Initializes the Escape Game used to test the app.
 */
public abstract class EscapeFactory {
    public static EscapeGame makeEscapeGame() {
        EscapeGame eg = new EscapeGame(30, 10);
        EscapeRoom math = new EscapeRoom("Salle de Maths", R.drawable.math_room);
        EscapeRoom chemical = new EscapeRoom("Salle de Chimie", R.drawable.chemistry_room);

        math.addPuzzle(new Puzzle("4 énigmes sont au tableau. Chaque énigme dévoile une lettre du code.\n" +
                "Découvrez le mot de passe caché en résolvant toutes les énigmes.\n\n" +
                "1 - ABC est un triangle équilatéral. D est le symétrique de C par rapport à A. BCD est un triangle particulier.\n" +
                "La troisième lettre de son type est une lettre du code (exemple : éq'U'ilatéral).\n\n" +
                "2 - ABCD est un carré. CDE un triangle équilatéral tracé à l'extérieur du carré. Quel est l'angle AEB ?\n" +
                "Ecrit en toutes lettres, la 5ème lettre de l’angle est une lettre du mot de passe (exemple : cinq'U'ante).\n\n" +
                "3 - Par quel point passe la fonction -2 cos(x) ?\n" +
                "La lettre correspondante est une lettre du code.\n\n" +
                "4 - Par quel point passe la fonction 2x² - 3x + 4 ?\n" +
                "La lettre correspondante est une lettre du code.",
                "CHAT", AnswerType.STRING));
        math.addPuzzle(new Puzzle("Calculez l'aire du sol de la pièce arrondi à l'entier supérieur en m².", "42", AnswerType.NUMBERS));
        chemical.addPuzzle(new Puzzle("Trouvez les codes QR cachés dans la pièce. Scannez les et stabilisez les équations chimiques.\n" +
                "Chaque énigme résolue vous donnera un chiffre du code.",
                "1612", AnswerType.FOUR_NUMBERS));

        eg.addInformation("chimie1", "https://image.ibb.co/kHaXTb/formule_Chimie1.png");
        eg.addInformation("chimie2", "https://image.ibb.co/d4iK8b/formule_Chimie2.png");
        eg.addInformation("chimie3", "https://image.ibb.co/eV8VEG/formule_Chimie3.png");
        eg.addInformation("chimie4", "https://image.ibb.co/knGHuG/formule_Chimie4.png");

        eg.addEscapeRoom("FF:11:8D:6C:50:9A", math);
        eg.addEscapeRoom("DC:0B:39:F4:7A:12", chemical);
        return eg;
    }
}
