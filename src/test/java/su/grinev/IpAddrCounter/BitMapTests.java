package su.grinev.IpAddrCounter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BitMapTests {

    @Test
    public void bitMapSetTest() {
        BitMap bitMap = new BitMap(10);

        for (int i = 0; i < 10; i++) {
            bitMap.set(i, true);
            assertTrue(bitMap.get(i));
        }
    }

    @Test
    public void bitMapResetTest() {
        BitMap bitMap = new BitMap(64);

        for (int i = 0; i < 64; i++) {
            bitMap.set(i, true);
        }

        for (int i = 0; i < 64; i++) {
            assertTrue(bitMap.get(i));
        }

        bitMap.reset();

        for (int i = 0; i < 64; i++) {
            assertFalse(bitMap.get(i));
        }
    }

    @Test
    public void bitMapSetGetTest() {
        BitMap bitMap = new BitMap(64);

        for (int i = 0; i < 64; i++) {
            bitMap.set(i, i % 2 == 0);
        }

        for (int i = 0; i < 64; i++) {
            if (i % 2 == 0) {
                assertTrue(bitMap.get(i));
            } else {
                assertFalse(bitMap.get(i));
            }
        }
    }

    @Test
    public void bitMapSizeTest() {
        BitMap bitMap = new BitMap(128);
        assertEquals(128, bitMap.size());
    }

    @Test
    public void bitMapDefaultValuesTest() {
        BitMap bitMap = new BitMap(10);

        for (int i = 0; i < 10; i++) {
            assertFalse(bitMap.get(i));
        }
    }

    @Test
    public void bitMapSetOutOfRangeTest() {
        BitMap bitMap = new BitMap(64);

        assertThrows(IndexOutOfBoundsException.class, () -> bitMap.set(64, true));
    }

    @Test
    public void bitMapGetOutOfRangeTest() {
        BitMap bitMap = new BitMap(64);

        assertThrows(IndexOutOfBoundsException.class, () -> bitMap.get(64));
    }
}
