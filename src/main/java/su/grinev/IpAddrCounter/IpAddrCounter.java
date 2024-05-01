package su.grinev.IpAddrCounter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static su.grinev.IpAddrCounter.IpUtils.ipv4ToInt;

public class IpAddrCounter {
    private static final int BUFFER_SIZE = 256 * 1024;
    private static final long IPV4_BIT_MAP_ALLOCATION_SIZE = 1L << 32;
    private final BitMap bitMap;
    private final int threads;
    private final AtomicLong uniqueIp = new AtomicLong();
    private final AtomicLong nonUniqueIp = new AtomicLong();
    private final ExecutorService executorService;
    private final String filename;
    private final Consumer<Float> progressConsumer;
    private final AtomicLong bytesProcessed;

    public IpAddrCounter(String filename, int threads, Consumer<Float> progressConsumer) {
        bytesProcessed = new AtomicLong();
        this.progressConsumer = progressConsumer;
        this.threads = threads;
        executorService = Executors.newFixedThreadPool(threads);
        bitMap = new BitMap(IPV4_BIT_MAP_ALLOCATION_SIZE);
        this.filename = filename;
    }

    public long countUniqueIpAddresses() {
        long[] offsets = new long[threads];
        long[] limits = new long[threads];

        long fileSize = splitFile(threads, offsets, limits);

        CountDownLatch countDownLatch = new CountDownLatch(threads);
        bytesProcessed.set(0);
        for (int i = 0; i < threads; i++) {
            int finalI = i;
            executorService.submit(() -> {
                countAddr(offsets[finalI], limits[finalI]);
                countDownLatch.countDown();
            });
        }

        try {
            while (!countDownLatch.await(1, TimeUnit.SECONDS)) {
                progressConsumer.accept((float) bytesProcessed.get() / fileSize);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return uniqueIp.get();
    }

    private long splitFile(int chunks, long[] offsets, long[] limits) {
        try (SeekableByteChannel channel = Files.newByteChannel(Path.of(filename))) {
            limits[0] = channel.size();
            ByteBuffer buffer;
            long chunkSize = channel.size() / chunks;
            long currentPosition = chunkSize;

            for (int i = 1; i < chunks; i++) {
                buffer = ByteBuffer.allocate(BUFFER_SIZE);
                channel.position(currentPosition);
                channel.read(buffer);
                buffer.flip();

                currentPosition += computePadding(buffer.array());
                offsets[i] = currentPosition;
                limits[i - 1] = currentPosition - 1;
                currentPosition += chunkSize;
            }
            limits[chunks - 1] = channel.size();

            return channel.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void countAddr(long offset, long limit) {
        long totalBytesRead = offset;
        int bytesRead;
        StringBuilder lineBuffer = new StringBuilder();
        ByteBuffer buffer;
        try (SeekableByteChannel channel = Files.newByteChannel(Path.of(filename))) {
            channel.position(offset);

            while (totalBytesRead < limit) {
                if (limit - totalBytesRead < BUFFER_SIZE) {
                    buffer = ByteBuffer.allocate((int) (limit - totalBytesRead));
                } else {
                    buffer = ByteBuffer.allocate(BUFFER_SIZE);
                }

                bytesRead = channel.read(buffer);

                if (bytesRead == -1) {
                    throw new RuntimeException("Unable to read the file.");
                }
                buffer.flip();

                while (buffer.hasRemaining()) {
                    byte currentByte = buffer.get();
                    if (currentByte == '\n') {
                        long ip = ipv4ToInt(lineBuffer.toString());
                        lineBuffer.setLength(0);
                        if (!bitMap.getAndSet(ip, true)) {
                            uniqueIp.getAndIncrement();
                        }
                        nonUniqueIp.getAndIncrement();
                    } else {
                        lineBuffer.append((char) currentByte);
                    }
                }

                buffer.clear();
                totalBytesRead += bytesRead;
                bytesProcessed.addAndGet(bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int computePadding(byte[] b) {
        int bufPos = 0;
        while (b[bufPos++] != '\n') {}
        return bufPos;
    }
}
