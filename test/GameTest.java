import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    @Test
    void checkIsValidName() throws Exception {
        // Arrange
        Game game = new Game(new Scanner(""));
        Method method = Game.class.getDeclaredMethod("isValidName", String.class);
        method.setAccessible(true);

        // Act
        boolean name1 = (boolean) method.invoke(game, "Alice");
        boolean name2 = (boolean) method.invoke(game, "Computer");
        boolean name3 = (boolean) method.invoke(game, "");
        boolean name4 = (boolean) method.invoke(game, "Bob123");

        // Assert
        assertTrue(name1);
        assertTrue(name2);
        assertFalse(name3);
        assertFalse(name4);
    }

    @Test
    void checkLongestConsecutiveRankLength() throws Exception {
        // Arrange
        Game game = new Game(new Scanner(""));
        Method method = Game.class.getDeclaredMethod("longestConsecutiveRankLength", List.class);
        method.setAccessible(true);

        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Card.Suit.CLUBS, Card.Rank.TWO));
        cards.add(new Card(Card.Suit.DIAMONDS, Card.Rank.THREE));
        cards.add(new Card(Card.Suit.HEARTS, Card.Rank.FOUR));
        cards.add(new Card(Card.Suit.SPADES, Card.Rank.SEVEN));

        // Act
        int result = (int) method.invoke(game, cards);

        // Assert
        assertEquals(3, result);
    }

    @Test
    void checkReadYesNoWithInvalidThenValidInput() throws Exception {
        // Arrange
        Game game = new Game(new Scanner("maybe\nyes\n"));
        Method method = Game.class.getDeclaredMethod("readYesNo", String.class);
        method.setAccessible(true);

        // Act
        boolean result = (boolean) method.invoke(game, "Enter yes/no: ");

        // Assert
        assertTrue(result);
    }

    @Test
    void checkReadIntInRangeWithRetry() throws Exception {
        // Arrange
        Game game = new Game(new Scanner("abc\n11\n7\n"));
        Method method = Game.class.getDeclaredMethod("readIntInRange", String.class, int.class, int.class);
        method.setAccessible(true);

        // Act
        int value = (int) method.invoke(game, "Enter number: ", 5, 10);

        // Assert
        assertEquals(7, value);
    }

    @Test
    void checkSequenceBonusSingleWinnerGetsFivePoints() throws Exception {
        // Arrange
        Game game = new Game(new Scanner(""));
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Player> players = (List<Player>) playersField.get(game);

        Player p1 = new Player("Alice");
        Player p2 = new Player("Bob");

        p1.collectCard(new Card(Card.Suit.CLUBS, Card.Rank.TWO));
        p1.collectCard(new Card(Card.Suit.DIAMONDS, Card.Rank.THREE));
        p1.collectCard(new Card(Card.Suit.HEARTS, Card.Rank.FOUR));
        p2.collectCard(new Card(Card.Suit.SPADES, Card.Rank.ACE));

        players.add(p1);
        players.add(p2);

        // Act
        Method method = Game.class.getDeclaredMethod("applyLongestConsecutiveSequenceBonus");
        method.setAccessible(true);
        method.invoke(game);

        // Assert
        assertEquals(5, p1.getSequenceBonus());
        assertEquals(0, p2.getSequenceBonus());
    }
}
