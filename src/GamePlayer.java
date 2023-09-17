package src;

import java.util.ArrayList;

public class GamePlayer
{
    public static void main(String[] args)
    {
        int numPlayers = 15;
        System.out.printf("\n%44s\n\n", "Playing Texas Hold'em");
        Game game = new Game(numPlayers);
        game.playGame();
    }
}
