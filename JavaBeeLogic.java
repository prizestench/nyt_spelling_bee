import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.io.*;

//Handles the logic for the JavaBee game
public class JavaBeeLogic {

   // Name of dictionary file (containing English words to validate guesses
   // against)
   private static final String DICTIONARY_FILENAME = "dictionary.txt";

   // Total number of hives in the game
   public static final int HIVE_COUNT = 7;

   // Required Min/Max length for a valid player guess
   public static final int MIN_WORD_LENGTH = 4;
   public static final int MAX_WORD_LENGTH = 19;

   // Required Min/Max number of formable words for a randomized hive
   public static final int MIN_FORMABLE = 30;
   public static final int MAX_FORMABLE = 110;

   // Collection of various letters (vowels only, consonants only, all letters)
   public static final String VOWEL_CHARS = "AEIOU";
   public static final String CONSONANT_CHARS = "BCDFGHJKLMNPQRSTVWXYZ";
   public static final String ALL_CHARS = VOWEL_CHARS + CONSONANT_CHARS;

   // The various score title thresholds and their respective titles
   public static final double[] TITLE_PERCENTS = { 0, 0.02, 0.05, 0.08, 0.15, 0.25, 0.4, 0.5, 0.7 };
   public static final String[] TITLE_NAMES = { "Beginner", "Good Start", "Moving Up",
         "Good", "Solid", "Nice", "Great",
         "Amazing", "Genius" };

   // Text for different error messages that occur for various invalid inputs
   private static final String ERROR_TOO_LONG = "Too long...";
   private static final String ERROR_TOO_SHORT = "Too short...";
   private static final String ERROR_MISSING_CENTER = "Missing yellow letter...";
   private static final String ERROR_INVALID_LETTER = "Contains non-hive letter...";
   private static final String ERROR_ALREADY_FOUND = "Already in word bank...";
   private static final String ERROR_NOT_WORD = "Not in dictionary...";

   // Character codes for the enter and backspace key press
   public static final char ENTER_KEY = KeyEvent.VK_ENTER;
   public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE;

   // Use me for generating random numbers (see
   // https://docs.oracle.com/javase/8/docs/api/java/util/Random.html)!
   private static final Random rand = new Random();

   // ...Feel free to add more **FINAL** variables of your own!

   // ****************** NON-FINAL GLOBAL VARIABLES ******************
   // ******** YOU CANNOT ADD ANY ADDITIONAL NON-FINAL GLOBALS! ********
   // ******** YOU WILL ONLY NEED THESE FOR MILESTONE #2 ********

   // An array storing all of the formable words given the chosen hive letters
   public static String[] formableWords = new String[MAX_FORMABLE];

   // The maximum number of points possible given the game's chosen hive letters
   public static int possiblePoints = 0;

   // *******************************************************************

   // This function gets called ONCE when the game is very first launched
   // before the user has the opportunity to do anything.
   //
   // Should perform any initialization that needs to happen at the start of the
   // game,
   // and return the randomly chosen hive letters as a char array. Whichever letter
   // is at index 0 of the array will be the center (yellow) hive letter, the
   // remainder
   // will be the outer (gray) hive letters.
   //
   // The returned char array:
   // -must be seven letters long
   // -cannot have duplicate letters
   // -cannot have an 'S' as one of its letters
   // -must contain AT LEAST one vowel character (AEIOU)
   // (additionally: if the array only contains one vowel, it should be
   // possible for the vowel to be in any hive, including the center)
   public static char[] initGame() {

      char[] hives = hiveRandomization();
      JavaBeeGUI.setTitle("Beginner");
      // { 'C', 'O', 'L', 'G', 'A', 'T', 'E' };

      return hives; // placeholder...
   }

   // Complete your warmup step 2 tasks (Section 3.2.2 step 2) here by calling
   // the requisite functions out of JavaBeeGUI.
   // This function gets called ONCE after the graphics window has been
   // initialized and initGame has been called.
   public static void warmupStep2() {

      // JavaBeeGUI.setGuess("MICHAEL");

      // All of your 3.2.2 step 2 warmup code will go here!
      // Where will the code for step 3 go...?

      // JavaBeeGUI.setGuess(JavaBeeGUI.getGuessStr());

   }

   // this function validates the guess
   public static boolean validateGuess(String word, char[] hives) {

      boolean validGuess = true;
      // this loops checks whether all letter in the guess are in the hive
      for (int i = 0; i < word.length(); i++) {
         validGuess = false;
         for (char c : hives) {
            if (word.charAt(i) == c) {
               validGuess = true;
            }
         }
         if (!validGuess) {
            JavaBeeGUI.displayError(ERROR_INVALID_LETTER);
            return validGuess;
         }
      }

      // this loop checks for the centre hive letter
      for (int i = 0; i < word.length(); i++) {
         validGuess = false;
         if (word.charAt(i) == hives[0]) {
            validGuess = true;
            break;
         }
      }
      if (!validGuess) {
         JavaBeeGUI.displayError(ERROR_MISSING_CENTER);
         return validGuess;
      }

      // this conditional checks for length of word
      if (word.length() < MIN_WORD_LENGTH) {
         JavaBeeGUI.displayError(ERROR_TOO_SHORT);
         return !validGuess;
      }

      String[] bank = JavaBeeGUI.getGuessBank();

      for (int i = 0; i < bank.length; i++) {
         if (bank[i].equalsIgnoreCase(word)) {
            JavaBeeGUI.displayError(ERROR_ALREADY_FOUND);
            return !validGuess;
         } else {
            validGuess = true;
         }
      }

      return validGuess;
   }

   // This function gets called EVERY TIME the user types a valid key on the
   // keyboard (letter, enter, or backspace) or clicks one of the hives/buttons
   // in the game window. Invalid key presses (ex: =, /, ;, etc) are ignored.
   //
   // The key pressed is passed in as a char value.
   // public static String guess = new String("");

   public static int playerScore(String guess, char[] hives) {
      /* This function takes in the player's correct guess and displays the score */

      int score = 0;
      if (guess.length() == MIN_WORD_LENGTH) {
         score = 1;
      } else if (guess.length() > MIN_WORD_LENGTH && allHiveletters(guess, hives)) {
         score = guess.length() + HIVE_COUNT;
      } else if (guess.length() > MIN_WORD_LENGTH) {
         score = guess.length();
      }
      return score;
   }

   public static boolean allHiveletters(String guess, char[] hives) {
      /**
       * this function takes in the guess string and the hives array and returns
       * boolean indicating whether the guess contains ALL HIVE LETTERS
       */
      boolean allhives = false;
      int count = 0;
      for (char c : hives) {
         for (int i = 0; i < guess.length(); i++) {
            if (c == guess.charAt(i)) {
               count++;
               break;
            }
         }
      }
      if (count >= HIVE_COUNT) {
         allhives = true;
      }
      return allhives;
   }

   public static void playerTitle(double score) {
      /*
       * this function takes in the players score and
       * calculates the percentage of words formed and displays the players title
       */
      int total = totalMaxpoints();
      // System.out.println(total);
      double percentageScore = (score) / total;
      String title = null;
      for (int i = JavaBeeLogic.TITLE_PERCENTS.length - 1; i >= 0; i--) {
         if (percentageScore >= TITLE_PERCENTS[i]) {
            title = JavaBeeLogic.TITLE_NAMES[i];
            break;
         }
      }
      JavaBeeGUI.setTitle(title);
   }

   public static char[] hiveRandomization() {
      /* this function creates a new hive array with random elements */
      char[] hives = new char[HIVE_COUNT];

      while (true) {
         for (int i = 0; i < HIVE_COUNT; i++) {
            int random = rand.nextInt(ALL_CHARS.length() - 1);
            hives[i] = ALL_CHARS.replace("S", "").charAt(random);
         }
         if (hiveValid(hives)) {
            return hives;
         }
      }
   }

   public static boolean hiveValid(char[] hives) {
      /*
       * this function takes in a char array and returns a boolean
       * indicating whether the generated hive array is valid or not
       */
      if (repeatedHiveLetters(hives) && vowelLetter(hives) && hiveDicValid(hives)) {
         addtoformableWords(hives);
         return true;
      } else {
         return false;
      }

   }

   public static boolean repeatedHiveLetters(char[] hives) {
      /**
       * Hive VALIDATION: this function checks for repeated elements elements in the
       * hive
       */
      boolean valid = true;

      for (int i = 0; i < hives.length; i++) {
         char temp = hives[i];
         for (int j = i + 1; j < hives.length; j++) {
            if (temp == hives[j])
               return !valid;
         }
      }
      return valid;
   }

   public static boolean vowelLetter(char[] hives) {
      /*
       * this function takes in the hive array and returns a boolean indicating
       * if it contains at least one vowel letter
       */
      boolean valid = true;
      for (int i = 0; i < VOWEL_CHARS.length(); i++) {
         for (char c : hives) {
            if (c == VOWEL_CHARS.charAt(i))
               return valid;
         }
      }
      return !valid;

   }

   public static boolean hiveDicValid(char[] hives) {
      /*
       * this function takes in a hive array and checks whether
       * the hive is valid based on the dictionary specifications
       */
      boolean valid = true;
      int wordCount = 0;
      int pangramCount = 0;

      try {
         File dicFile = new File(DICTIONARY_FILENAME);
         Scanner file = new Scanner(dicFile);
         while (file.hasNextLine()) {
            String word = file.nextLine();
            if (formableWord(word, hives)) {
               wordCount++;
               if (allHiveletters(word, hives)) {
                  pangramCount++;
               }
            }
         }
         file.close();

         if (wordCount < MIN_FORMABLE || wordCount > MAX_FORMABLE || pangramCount <= 0) {
            return !valid;
         } else {
            return valid;
         }

      } catch (FileNotFoundException e) {
         System.out.println("File not found exception");
         System.exit(1);
         return false;
      }
   }

   public static boolean formableWord(String word, char[] hives) {
      /*
       * this funtion takes in a word from the dictionary file
       * and checks whether the words can be formed from the letters in the hive
       */
      boolean validGuess = true;
      // this loops checks whether all letter in the word are in the hive
      for (int i = 0; i < word.length(); i++) {
         validGuess = false;
         for (char c : hives) {
            if (word.charAt(i) == c) {
               validGuess = true;
            }
         }
         if (!validGuess) {
            return validGuess;
         }
      }
      // this loop checks for the centre hive letter
      for (int i = 0; i < word.length(); i++) {
         validGuess = false;
         if (word.charAt(i) == hives[0]) {
            validGuess = true;
            break;
         }
      }
      if (!validGuess) {
         return validGuess;
      }
      return validGuess;
   }

   public static void addtoformableWords(char[] hives) {
      /*
       * this function adds all formable words from the hive array in the
       * String [] formableWords
       */
      try {
         File dicFile = new File(DICTIONARY_FILENAME);
         Scanner file = new Scanner(dicFile);
         int i = 0;
         while (file.hasNextLine()) {
            String word = file.nextLine();
            if (formableWord(word, hives)) {
               formableWords[i] = word;
               i++;
            }
         }
         // System.out.println(Arrays.toString(formableWords));
         file.close();
      }

      catch (FileNotFoundException e) {
         System.out.println("File not Found");
         System.exit(1);
      }
   }

   public static boolean isWord(String guess) {
      /*
       * this function takes in the players guess and checks whether
       * the guess is an English word(i.e in the dictionary)
       */
      for (String word : formableWords) {
         if (word != null) {
            if ((word.equals(guess)))
               return true;
         }
      }
      JavaBeeGUI.displayError(ERROR_NOT_WORD);
      JavaBeeGUI.wiggleGuess();
      return false;

   }

   public static int totalMaxpoints() {
      /*
       * this functions calculates the total maximum points for all
       * formable words in the hives
       */
      int total = 0;
      for (String word : formableWords) {
         if (word != null)
            total += playerScore(word, JavaBeeGUI.getAllHiveChars());
      }
      return total;
   }

   public static void reactToKey(char key) {

      String guess = JavaBeeGUI.getGuessStr();
      if (key != BACKSPACE_KEY && key != ENTER_KEY) {
         if (guess.length() < MAX_WORD_LENGTH) {
            guess += key;
         } else {
            JavaBeeGUI.displayError(ERROR_TOO_LONG);
            JavaBeeGUI.wiggleGuess();
         }
      } else if ((key == BACKSPACE_KEY) && guess.length() > 0) {
         guess = guess.substring(0, guess.length() - 1);

      } else if (key == ENTER_KEY) {
         boolean valid = validateGuess(guess, JavaBeeGUI.getAllHiveChars());
         if ((valid) && (isWord(guess))) {
            int score = playerScore(guess, JavaBeeGUI.getAllHiveChars());
            JavaBeeGUI.addToGuessBank(guess, score);
            playerTitle(JavaBeeGUI.getScore());
            guess = "";
         }
      }

      JavaBeeGUI.setGuess(guess);
      //System.out.println("reactToKey(...) called! key (int value) = '" + ((int) key) + "'");

      /*
       * if (key == 'E'){
       * JavaBeeGUI.wiggleGuess();
       * JavaBeeGUI.displayError("WARMUP!");
       * }
       */

   }

}
