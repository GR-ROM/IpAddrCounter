package su.grinev.IpAddrCounter;

import java.util.Arrays;

public class BitMap {

    private long[] data;
    private Object[] mutexes;

    public BitMap(long size) {
        if (size == 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        int longSize = Math.max(1, (int) (size >> 6));
        data = new long[longSize];
        mutexes = new Object[longSize];
        for (int i = 0; i != longSize; i++) {
            mutexes[i] = new Object();
        }
        Arrays.fill(data, 0);
    }

    public void set(long bitPos, boolean value) {
        int longPos = (int) (bitPos >> 6);
        if (longPos >= data.length) {
            throw new IndexOutOfBoundsException("Bit pos: " + bitPos + " is out of range of: " + (data.length << 6));
        }
        bitPos = bitPos % 64;
        synchronized (mutexes[longPos]) {
            if (value) {
                data[longPos] = data[longPos] | (1L << bitPos);
            } else {
                data[longPos] = data[longPos] & ~(1L << bitPos);
            }
        }
    }

    public boolean get(long bitPos) {
        int longPos = (int) (bitPos >> 6);
        if (longPos >= data.length) {
            throw new IndexOutOfBoundsException("Bit pos: " + bitPos + " is out of range of: " + (data.length << 6));
        }
        bitPos = bitPos % 64;
        synchronized (mutexes[longPos]) {
            return (data[longPos] & (1L << bitPos)) != 0;
        }
    }

    public boolean getAndSet(long bitPos, boolean value) {
        boolean result;
        int longPos = (int) (bitPos >> 6);
        if (longPos >= data.length) {
            throw new IndexOutOfBoundsException("Bit pos: " + bitPos + " is out of range of: " + (data.length << 6));
        }
        bitPos = bitPos % 64;
        synchronized (mutexes[longPos]) {
            result = (data[longPos] & (1L << bitPos)) != 0;
            data[longPos] = value ? data[longPos] | (1L << bitPos) : data[longPos] & ~(1L << bitPos);
        }
        return result;
    }

    public void reset() {
        Arrays.fill(data, 0);
    }

    public long size() {
        return (long) data.length << 6;
    }
}
