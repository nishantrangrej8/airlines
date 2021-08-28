package com.example.airlines;

import java.util.Random;

public class Utils {

    private static Random random = new Random();

    private static String[] firstNames = {"John", "Mark", "Kate", "Molly", "Shelby", "Aaron", "Curtis", "Om", "Mehdi", "Jake", "Caitlyn"};

    private static String[] lastNames = {"Wright", "Sterling", "Sanchez", "Smith", "Cohen", "Shirly", "Godwin", "Shah", "Hasan", "Ramsey", "McTominay"};

    public static String generateRandomName() {
        return firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
