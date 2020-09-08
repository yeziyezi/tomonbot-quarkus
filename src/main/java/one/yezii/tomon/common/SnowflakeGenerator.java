package one.yezii.tomon.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SnowflakeGenerator {
    private final static long startTimestamp =
            LocalDateTime.of(2020, 8, 11, 0, 0)
                    .toInstant(ZoneOffset.ofHours(8))
                    .toEpochMilli();
    private final static String first = "0";
    private final static String endpointId = "1100101011";
    private static String lastTimestampId = "";
    private static int serialId = 0;

    private static String getTimestamp() {
        String timestamp = Long.toBinaryString(System.currentTimeMillis() - startTimestamp);
        return "0".repeat(41 - timestamp.length()) + timestamp;
    }

    public static synchronized long next() {
        String timestampId = getTimestamp();
        if (serialId > 1024) {
            serialId = 0;
            while (timestampId.equals(lastTimestampId)) {
                timestampId = getTimestamp();
            }
        }
        lastTimestampId = timestampId;
        return Long.parseLong(first + timestampId + endpointId + serialId, 2);
    }

    public static String nextString() {
        return String.valueOf(next());
    }
}
