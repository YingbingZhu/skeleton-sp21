package deque;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
        ArrayDeque<String> lld1 = new ArrayDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }
    @Test
    public void forEachTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();
        ad.addLast("Anna");
        ad.addLast("Elsa");
        ad.addFirst("Snowman");
        List<String> listOfItems = new ArrayList<>();
        for (String x : ad) {
            listOfItems.add(x);
        }
        String result = "{" + String.join(", ", listOfItems) + "}";
        assertEquals("{Snowman, Anna, Elsa}", result);
    }
}
