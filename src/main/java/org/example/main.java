package org.example;

import java.util.Properties;

public class main {
    public static void main(String args[]) throws Exception {
        SQLManager.createDatabase();
        SQLManager.fillDatabase();
    }
}
