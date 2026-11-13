package client;

import ui.REPL;

public class ClientMain {
    public static void main(String[] args) {
        REPL repl = new REPL(8080);
        System.out.println("Starting ♕ CS 240 Chess Client...");
        repl.run();
    }
}
