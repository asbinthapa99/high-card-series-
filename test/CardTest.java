import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CardTest {

    @Test
    void checkCardDisplayText() {
        // Arrange
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.QUEEN);

        // Act + Assert
        assertEquals("Queen of Hearts", card.toDisplayString());
        assertEquals(card.toDisplayString(), card.toString());
    }

    @Test
    void checkCardEqualsAndHashCode() {
        // Arrange
        Card card1 = new Card(Card.Suit.SPADES, Card.Rank.ACE);
        Card card2 = new Card(Card.Suit.SPADES, Card.Rank.ACE);
        Card card3 = new Card(Card.Suit.CLUBS, Card.Rank.ACE);

        // Assert
        assertEquals(card1, card2);
        assertEquals(card1.hashCode(), card2.hashCode());
        assertNotEquals(card1, card3);
    }
}
