package com.example.nisch100.call_a_bus;

import android.content.Context;
import android.content.Intent;

public class Utilities {

    // Switch to another screen
    static void switchScreen(Context current, String next){
        try{
            Intent intent = new Intent(current, Class.forName("com.example.nisch100.call_a_bus." + next));
            current.startActivity(intent);
        }
        catch (ClassNotFoundException e) {
            return;
        }
    }

    // Switch to Main Menu
    static void switchMainMenu(Context current){
        Intent intent = new Intent(current, MainMenu.class);
        current.startActivity(intent);
    }

}
