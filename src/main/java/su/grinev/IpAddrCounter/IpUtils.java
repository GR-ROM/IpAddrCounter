package su.grinev.IpAddrCounter;

public class IpUtils {

    public static long ipv4ToInt(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result |= Long.parseLong(octets[i]) << (24 - (i * 8));
        }
        return result;
    }

}
