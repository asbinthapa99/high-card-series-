import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Deck {
    private final Deque<Card> cards = new ArrayDeque<>();

    // Build a full 52-card deck and shuffle it.
    public Deck() {
        List<Card> freshDeck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                freshDeck.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(freshDeck);
        cards.addAll(freshDeck);
    }

    // Draw card from the top, or null if empty.
    public Card drawTopCard() {
        return cards.pollFirst();
    }

    // Put a card at the bottom of the deck.
    public void returnToBottom(Card card) {
        cards.offerLast(card);
    }

    // Number of cards currently left in deck.
    public int remainingCards() {
        return cards.size();
    }
}
