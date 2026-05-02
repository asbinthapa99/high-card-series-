import java.util.Objects;

public class Card {
    // Suits used in a standard deck.
    public enum Suit {
        CLUBS("Clubs"), DIAMONDS("Diamonds"), HEARTS("Hearts"), SPADES("Spades");

        private final String displayName;

        Suit(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Ranks with both value (for comparison) and display text.
    public enum Rank {
        TWO(2, "2"), THREE(3, "3"), FOUR(4, "4"), FIVE(5, "5"), SIX(6, "6"),
        SEVEN(7, "7"), EIGHT(8, "8"), NINE(9, "9"), TEN(10, "10"),
        JACK(11, "Jack"), QUEEN(12, "Queen"), KING(13, "King"), ACE(14, "Ace");

        private final int value;
        private final String displayName;

        Rank(int value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        public int getValue() {
            return value;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final Suit suit;
    private final Rank rank;

    // Create one card with suit + rank.
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String toDisplayString() {
        // Example: "Queen of Hearts"
        return rank.getDisplayName() + " of " + suit.getDisplayName();
    }

    @Override
    public String toString() {
        return toDisplayString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}
