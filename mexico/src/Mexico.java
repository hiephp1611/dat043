
import java.util.Random;
import java.util.Scanner;

import static java.lang.System.*;

/*
 *  The Mexico dice game
 *  See https://en.wikipedia.org/wiki/Mexico_(game)
 *
 */
public class Mexico {

    public static void main(String[] args) {
        new Mexico().program();
    }

    final Random rand = new Random();
    final Scanner sc = new Scanner(in);
    final int maxRolls = 3;      // No player may exceed this
    final int startAmount = 3;   // Money for a player. Select any
    final int mexico = 1000;     // A value greater than any other

    void program() {
        //test();            // <----------------- UNCOMMENT to test

        int pot = 0;         // What the winner will get
        Player[] players;    // The players (array of Player objects)
        Player current;      // Current player for round
        Player leader;       // Player starting the round
        Player loser;

        players = getPlayers();
        current = getRandomPlayer(players);
        leader = current;
        int count = players.length;
        int roundRolls;


        out.println("Mexico Game Started");
        statusMsg(players);

       while (players.length > 1) {   // Game over when only one player left

           if (current.name .equals(leader.name)){
               roundRolls = maxRolls;
               leader = current;
           }else {
               roundRolls = leader.nRolls;}
            // ----- In ----------
            String cmd = getPlayerChoice(current);
            if ("r".equals(cmd)) {

                    // --- Process ------

                if (current.nRolls< roundRolls){
                    rollDice(current);
                    current.nRolls ++;
                    // ---- Out --------
                    roundMsg(current);
                } else {
                    //players[indexOf(players,current)] = current;
                    current = next(players, current);
                    count--;
                }
            } else if ("n".equals(cmd)) {
                 // Process
                current = next(players, current);
                count--;
            } else {
                out.println("?");
            }

            if ( allRolled(count)) {
                // --- Process -----

                loser = getLoser (players, leader);
                loser.amount --;
                pot++;
                leader = next(players, loser);
                clearRoundResults(players);
                // ----- Out --------------------

                out.println("Round done "+ loser.name + " lost!");
                current = leader;
                out.println("Next to roll is " + current.name);
                if (loser.amount == 0){
                    players = removeLoser(players, loser);
                    out.println(loser.name + " has no resources will leave game.");
                }
                count = players.length;
                statusMsg(players);

            }
        }
       out.println("Game Over, winner is " + players[0].name + ". Will get " + pot + " from pot");
    }


    // ---- Game logic methods --------------

    // TODO implement and test methods (one at the time)

    void rollDice(Player player) {
        player.fstDice= rand.nextInt(6) + 1;
        player.secDice= rand.nextInt(6) + 1;
    }

    Player next(Player[] players, Player current) {
        int index = (indexOf(players, current)+1)% players.length;

        return players[index];
    }

    boolean allRolled(int count) {
        return count == 0;
    }

    Player getLoser(Player[] players, Player leader){
        Player loser = players[indexOf(players,leader)];

        for(Player current1 : players){
            if (getScore(current1) < getScore(loser)) {
                loser = current1;
            }
        }
        return loser;
    }

    int getScore(Player player){
        int fst = player.fstDice;
        int sec = player.secDice;
        int score;
        if (fst > sec)      {score = fst*10 +sec; }
        else if (fst < sec) {score = sec*10 + fst;}
        else                {score = fst*100;}
        if (score == 21){ score = mexico;}
        return score;
    }

    Player[] removeLoser(Player[] players, Player loser){
        Player[] update = new Player[players.length -1];
        int k=0;
        for (int i=0; i < players.length; i++){
            if (players[i]!= loser){
                update[k]=players[i];
                k++;
            }
        }
        return update;
    }

    void clearRoundResults(Player[] players){
        for(int i= 0; i< players.length; i++){
            players[i].fstDice = players[i].secDice = players[i].nRolls = 0;
        }
    }

    int indexOf(Player[] players, Player player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return i;
            }
        }
        return -1;
    }

    Player getRandomPlayer(Player[] players) {
        return players[rand.nextInt(players.length)];
    }


    // ---------- IO methods (nothing to do here) -----------------------

    Player[] getPlayers() {
        // Ugly for now. If using a constructor this may
        // be cleaned up.
        Player[] players = new Player[3];
        String[] names = {"Olle","Fia","Lisa"};
        for (int i=0; i < players.length; i++){
            players[i] = new Player(names[i],startAmount);
        }
        return players;
    }

    void statusMsg(Player[] players) {
        out.print("Status: ");
        for (int i = 0; i < players.length; i++) {
            out.print(players[i].name + " " + players[i].amount + " ");
        }
        out.println();
    }

    void roundMsg(Player current) {
        out.println(current.name + " got " +
                current.fstDice + " and " + current.secDice);
    }

    String getPlayerChoice(Player player) {
        out.print("Player is " + player.name + " > ");
        return sc.nextLine();
    }

    // Possibly useful utility during development
    String toString(Player p){
        return p.name + ", " + p.amount + ", " + p.fstDice + ", "
                + p.secDice + ", " + p.nRolls;
    }

    // Class for a player
    class Player {
        String name;
        int amount;   // Start amount (money)
        int fstDice;  // Result of first dice
        int secDice;  // Result of second dice
        int nRolls;   // Current number of rolls
        public Player(String n, int a){
            name = n;
            amount = a;
        }
    }

    /**************************************************
     *  Testing
     *
     *  Test are logical expressions that should
     *  evaluate to true (and then be written out)
     *  No testing of IO methods
     *  Uncomment in program() to run test (only)
     ***************************************************/
    /*void test() {
        // A few hard coded player to use for test
        // NOTE: Possible to debug tests from here, very efficient!
        Player[] ps = {new Player(), new Player(), new Player()};
        ps[0].fstDice = 2;
        ps[0].secDice = 6;
        ps[1].fstDice = 6;
        ps[1].secDice = 5;
        ps[2].fstDice = 1;
        ps[2].secDice = 1;

        out.println(getScore(ps[0]) == 62);
        out.println(getScore(ps[1]) == 65);
        out.println(next(ps, ps[0]) == ps[1]);
        out.println(getLoser(ps,ps[0]) == ps[0]);

        exit(0);
    }*/


}
