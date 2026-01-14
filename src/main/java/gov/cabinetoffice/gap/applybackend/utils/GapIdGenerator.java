package gov.cabinetoffice.gap.applybackend.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GapIdGenerator {

    public static String generateGapId(final Long userId, final String env, final long recordNumber, final int version) {
        final LocalDateTime currentDateTime = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        final String dateTime = currentDateTime.format(formatter);

        return "GAP" +
                "-" +
                env +
                "-" +
                dateTime +
                "-" +
                version +
                recordNumber +
                "-" +
                userId
                ;
    }
}
