import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Game {
    private final Scanner scanner;
    private final List<Player> players = new ArrayList<>();
    private Deck deck;

    public Game(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        // Main game flow.
        System.out.println("=== High Card Series ===");
        setupPlayers();
        int rounds = readIntInRange("Enter number of rounds (5-10): ", 5, 10);

        deck = new Deck();
        playRounds(rounds);

        System.out.println();
        showRoundSummary();
        runAdjustmentStage();

        System.out.println();
        applyLongestConsecutiveSequenceBonus();
        applyHighestSuitCountBonus();

        System.out.println();
        showFinalResults();
    }

    private void setupPlayers() {
        // Read player count and player names.
        int playerCount = readIntInRange("Enter number of players (2-5): ", 2, 5);

        for (int i = 1; i <= playerCount; i++) {
            String name;
            do {
                System.out.print("Enter name for player " + i + ": ");
                name = scanner.nextLine().trim();
                if (!isValidName(name)) {
                    System.out.println("Invalid input.");
                }
            } while (!isValidName(name));

            Player player = new Player(name);
            players.add(player);
            if (player.isComputerControlled()) {
                System.out.println("Player " + i + " is computer-controlled.");
            }
        }
    }

    private void playRounds(int rounds) {
        // Run each round: deal cards, resolve winner, show score.
        for (int round = 1; round <= rounds; round++) {
            printRoundHeader(round);

            Map<Player, Card> roundCards = new HashMap<>();
            for (Player player : players) {
                Card drawnCard = deck.drawTopCard();
                if (drawnCard == null) {
                    System.out.println("Deck ran out of cards.");
                    return;
                }

                roundCards.put(player, drawnCard);
                System.out.println(player.getName() + " was dealt: " + drawnCard.toDisplayString());
            }

            resolveRound(roundCards);
            System.out.println();
            printScores();
            System.out.println();
            printRoundFooter(round, rounds);
        }
    }

    private void printRoundHeader(int round) {
        System.out.println();
        System.out.println("==============================");
        System.out.println("Round " + round);
        System.out.println("==============================");
    }

    private void printRoundFooter(int round, int rounds) {
        System.out.println("End of Round " + round + ".");
        System.out.println();
        if (round < rounds) {
            System.out.print("Press Enter to continue to Round " + (round + 1) + "...");
            scanner.nextLine();
        }
    }

    private void resolveRound(Map<Player, Card> roundCards) {
        // Highest rank wins this round.
        int highestRank = roundCards.values()
                .stream()
                .mapToInt(card -> card.getRank().getValue())
                .max()
                .orElse(-1);

        List<Player> winners = new ArrayList<>();
        for (Player player : players) {
            Card card = roundCards.get(player);
            if (card != null && card.getRank().getValue() == highestRank) {
                winners.add(player);
            }
        }

        if (winners.size() == 1) {
            Player winner = winners.get(0);
            winner.addRoundPoints(3);
            winner.recordRoundWin();
            winner.collectCard(roundCards.get(winner));
            System.out.println("Round winner: " + winner.getName() + " keeps "
                    + roundCards.get(winner).toDisplayString() + " and gets 3 points.");
        } else {
            System.out.print("Round winners: ");
            for (int i = 0; i < winners.size(); i++) {
                Player winner = winners.get(i);
                winner.addRoundPoints(1);
                winner.recordRoundTie();
                winner.collectCard(roundCards.get(winner));
                System.out.print(winner.getName() + " keeps "
                        + roundCards.get(winner).toDisplayString());
                if (i < winners.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println(" and get 1 point each.");
        }

        Set<Player> winnerSet = new HashSet<>(winners);
        // Non-winning cards go back to bottom of deck.
        for (Player player : players) {
            if (!winnerSet.contains(player)) {
                deck.returnToBottom(roundCards.get(player));
            }
        }
    }

    private void printScores() {
        System.out.println("Current scores:");
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getScore());
        }
    }

    private void showRoundSummary() {
        System.out.println("=== End of Rounds Summary ===");
        for (Player player : players) {
            System.out.println(player.getName() + " round score: " + player.getRoundPoints());
            System.out.println(player.getName() + "'s collected cards:");
            if (player.getCollectedCards().isEmpty()) {
                System.out.println("none");
            } else {
                for (int i = 0; i < player.getCollectedCards().size(); i++) {
                    System.out.println((i + 1) + ". " + player.getCollectedCards().get(i).toDisplayString());
                }
            }
            System.out.println();
        }
    }

    private void runAdjustmentStage() {
        // Optional step to replace up to 2 collected cards.
        System.out.println("=== Optional Adjustment Stage ===");

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            System.out.println();
            System.out.println(player.getName() + "'s turn.");

            if (player.getCollectedCards().isEmpty()) {
                System.out.println(player.getName() + " has no collected cards and skips the adjustment stage.");
                continue;
            }

            if (player.isComputerControlled()) {
                printCollectedCards(player);
                System.out.println(player.getName() + " skipped replacement.");
                continue;
            }

            printCollectedCards(player);
            if (!readYesNo("Do you want to replace cards? (yes/no): ")) {
                System.out.println(player.getName() + " skipped replacement.");
                continue;
            }

            int maxDiscard = Math.min(2, player.getCollectedCards().size());
            int discardCount = readIntInRange("How many cards do you want to replace? (1-" + maxDiscard + "): ",
                    1, maxDiscard);

            for (int discardIndex = 0; discardIndex < discardCount; discardIndex++) {
                printCollectedCards(player);
                int cardIndex = readIntInRange("Choose card number to discard: ", 1, player.getCollectedCards().size());
                List<Integer> indexes = new ArrayList<>();
                indexes.add(cardIndex);
                List<Card> discarded = player.discardByOneBasedIndexes(indexes);
                Card replacement = deck.drawTopCard();
                if (replacement != null) {
                    player.collectCard(replacement);
                    System.out.println("Replacement card drawn: " + replacement.toDisplayString());
                } else {
                    System.out.println("Replacement card drawn: no replacement available");
                }

                if (!discarded.isEmpty()) {
                    deck.returnToBottom(discarded.get(0));
                }
            }

            printCollectedCards(player);
        }
    }

    private void printCollectedCards(Player player) {
        System.out.println(player.getName() + "'s collected cards:");
        if (player.getCollectedCards().isEmpty()) {
            System.out.println("none");
            return;
        }
        for (int i = 0; i < player.getCollectedCards().size(); i++) {
            System.out.println((i + 1) + ". " + player.getCollectedCards().get(i).toDisplayString());
        }
    }

    private void applyLongestConsecutiveSequenceBonus() {
        // Bonus based on longest consecutive rank sequence.
        System.out.println("=== Bonus 1: Longest Consecutive Rank Sequence ===");
        Map<Player, Integer> sequenceLengths = new HashMap<>();
        int bestLength = 0;

        for (Player player : players) {
            int longest = longestConsecutiveRankLength(player.getCollectedCards());
            sequenceLengths.put(player, longest);
            bestLength = Math.max(bestLength, longest);
        }

        List<Player> bestPlayers = new ArrayList<>();
        for (Player player : players) {
            if (sequenceLengths.get(player) == bestLength) {
                bestPlayers.add(player);
            }
        }

        Map<Player, Integer> bonuses = new HashMap<>();
        for (Player player : players) {
            bonuses.put(player, 0);
        }
        if (bestLength > 0) {
            if (bestPlayers.size() == 1) {
                Player winner = bestPlayers.get(0);
                winner.addSequenceBonus(5);
                bonuses.put(winner, 5);
            } else {
                for (Player player : bestPlayers) {
                    player.addSequenceBonus(2);
                    bonuses.put(player, 2);
                }
            }
        }

        for (Player player : players) {
            System.out.println(player.getName() + " sequence length = " + sequenceLengths.get(player)
                    + ", bonus = " + bonuses.get(player));
        }
        System.out.println();
    }

    private void applyHighestSuitCountBonus() {
        // Bonus based on most cards of the same suit.
        System.out.println("=== Bonus 2: Highest Suit Count ===");
        Map<Player, Integer> playerHighestSuitCounts = new HashMap<>();
        Map<Player, Map<Card.Suit, Integer>> allSuitCounts = new HashMap<>();
        int bestSuitCount = 0;

        for (Player player : players) {
            Map<Card.Suit, Integer> suitCounts = new EnumMap<>(Card.Suit.class);
            for (Card.Suit suit : Card.Suit.values()) {
                suitCounts.put(suit, 0);
            }
            for (Card card : player.getCollectedCards()) {
                suitCounts.put(card.getSuit(), suitCounts.get(card.getSuit()) + 1);
            }

            int highestForPlayer = suitCounts.values().stream().max(Integer::compareTo).orElse(0);
            playerHighestSuitCounts.put(player, highestForPlayer);
            allSuitCounts.put(player, suitCounts);
            bestSuitCount = Math.max(bestSuitCount, highestForPlayer);
        }

        List<Player> bestPlayers = new ArrayList<>();
        for (Player player : players) {
            if (playerHighestSuitCounts.get(player) == bestSuitCount) {
                bestPlayers.add(player);
            }
        }

        Map<Player, Integer> bonuses = new HashMap<>();
        for (Player player : players) {
            bonuses.put(player, 0);
        }
        if (bestSuitCount > 0) {
            if (bestPlayers.size() == 1) {
                Player winner = bestPlayers.get(0);
                winner.addSuitBonus(5);
                bonuses.put(winner, 5);
            } else {
                for (Player player : bestPlayers) {
                    player.addSuitBonus(2);
                    bonuses.put(player, 2);
                }
            }
        }

        for (Player player : players) {
            Map<Card.Suit, Integer> suitCounts = allSuitCounts.get(player);
            System.out.println(player.getName() + " suit counts -> Clubs: " + suitCounts.get(Card.Suit.CLUBS)
                    + ", Diamonds: " + suitCounts.get(Card.Suit.DIAMONDS)
                    + ", Hearts: " + suitCounts.get(Card.Suit.HEARTS)
                    + ", Spades: " + suitCounts.get(Card.Suit.SPADES));
            System.out.println(player.getName() + " highest suit count = " + playerHighestSuitCounts.get(player)
                    + ", bonus = " + bonuses.get(player));
        }
        System.out.println();
    }

    private void showFinalResults() {
        // Sort by score and print winner/draw.
        System.out.println("=== Final Results ===");
        List<Player> sorted = new ArrayList<>(players);
        sorted.sort(Comparator.comparingInt(Player::getScore).reversed());

        for (int i = 0; i < sorted.size(); i++) {
            Player player = sorted.get(i);
            System.out.println((i + 1) + ". " + player.getName() + " - " + player.getScore() + " points");
        }

        int topScore = sorted.get(0).getScore();
        List<Player> topPlayers = sorted.stream().filter(p -> p.getScore() == topScore).toList();

        if (topPlayers.size() == 1) {
            System.out.println("Winner: " + topPlayers.get(0).getName());
        } else {
            System.out.print("Draw: ");
            for (int i = 0; i < topPlayers.size(); i++) {
                System.out.print(topPlayers.get(i).getName());
                if (i < topPlayers.size() - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println(" with " + topScore + " points each.");
        }
    }

    private int readIntInRange(String prompt, int min, int max) {
        // Keep asking until user enters valid number in range.
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Invalid input.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private boolean readYesNo(String prompt) {
        // Accept y/yes or n/no only.
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(input) || "yes".equals(input)) {
                return true;
            }
            if ("n".equals(input) || "no".equals(input)) {
                return false;
            }

            System.out.println("Invalid input.");
        }
    }

    private boolean isValidName(String name) {
        // Name cannot be empty and cannot contain digits.
        if (name.isEmpty()) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            if (Character.isDigit(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private int longestConsecutiveRankLength(List<Card> cards) {
        // Find longest consecutive run using unique rank values.
        if (cards.isEmpty()) {
            return 0;
        }

        Set<Integer> uniqueRanks = new HashSet<>();
        for (Card card : cards) {
            uniqueRanks.add(card.getRank().getValue());
        }

        int best = 0;
        for (int rank : uniqueRanks) {
            if (!uniqueRanks.contains(rank - 1)) {
                int current = rank;
                int length = 1;
                while (uniqueRanks.contains(current + 1)) {
                    current++;
                    length++;
                }
                best = Math.max(best, length);
            }
        }
        return best;
    }

}
