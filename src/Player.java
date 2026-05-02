import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    // Player named "Computer" is treated as computer-controlled.
    private final boolean computerControlled;
    private final List<Card> collectedCards = new ArrayList<>();
    private int score;
    private int roundPoints;
    private int sequenceBonus;
    private int suitBonus;
    private int roundsWon;
    private int roundsTied;
    public Player(String name) {
        this.name = name;
        this.computerControlled = "Computer".equalsIgnoreCase(name);
    }
    public String getName() {
        return name;
    }
    public boolean isComputerControlled() {
        return computerControlled;
    }
    public int getScore() {
        return score;
    }
    public int getRoundPoints() {
        return roundPoints;
    }
    public int getSequenceBonus() {
        return sequenceBonus;
    }
    public int getSuitBonus() {
        return suitBonus;
    }
    public int getRoundsWon() {
        return roundsWon;
    }
    public int getRoundsTied() {
        return roundsTied;
    }
    public void addRoundPoints(int points) {
        // Round points also contribute to total score.
        roundPoints += points;
        score += points;
    }

    public void recordRoundWin() {
        roundsWon++;
    }
    public void recordRoundTie() {
        roundsTied++;
    }
    public void addSequenceBonus(int points) {
        // Bonus points also contribute to total score.
        sequenceBonus += points;
        score += points;
    }
    public void addSuitBonus(int points) {
        suitBonus += points;
        score += points;
    }
    public List<Card> getCollectedCards() {
        return collectedCards;
    }
    public void collectCard(Card card) {
        collectedCards.add(card);
    }
    public List<Card> discardByOneBasedIndexes(List<Integer> indexes) {
        // Sort descending so removing one index does not shift later removals.
        indexes.sort((a, b) -> Integer.compare(b, a));
        List<Card> discarded = new ArrayList<>();

        for (int oneBasedIndex : indexes) {
            int zeroBased = oneBasedIndex - 1;
            if (zeroBased >= 0 && zeroBased < collectedCards.size()) {
                discarded.add(collectedCards.remove(zeroBased));
            }
        }
        return discarded;
    }
}
