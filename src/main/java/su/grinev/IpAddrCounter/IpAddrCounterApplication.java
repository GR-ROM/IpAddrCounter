package su.grinev.IpAddrCounter;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IpAddrCounterApplication {

    private static String filename;
    private static int threads = 4;

    public static void main(String[] args) {
        validateArguments(args);

        System.out.printf("File: %s, threads: %d%n", filename, threads);
        System.out.println("Counting in progress, depends on the file size this can take a while...");
        IpAddrCounter ipAddrCounter = new IpAddrCounter(filename, threads, IpAddrCounterApplication::printProgress);

        long time = System.currentTimeMillis();
        Long uniqueIpAddrCounter = ipAddrCounter.countUniqueIpAddresses();
        time = (System.currentTimeMillis() - time) / 1000;
        System.out.printf("%nUnique IP addresses: %d%n", uniqueIpAddrCounter);
        System.out.printf("File processed in %02dm%02ds.", time / 60, time % 60);
        System.exit(0);
    }

    private static void validateArguments(String[] args) {
        if (args.length < 1) {
            System.err.println("Missing required argument.");
            System.exit(1);
        }

        if ((filename = args[0]).isEmpty()) {
            System.err.println("Filename must be specified.");
            System.exit(1);
        }

        if (args.length > 1 && (threads = Integer.parseInt(args[1])) < 1) {
            System.err.println("The number of threads must be greater than 0.");
            System.exit(1);
        }
    }

    public static void printProgress(float progress) {
        int width = 50;
        int filled = Math.round(width * progress);
        String bar = "=".repeat(filled) + " ".repeat(width - filled);
        System.out.printf("\r[%s]  %.1f%%", bar, progress * 100);
    }
}
