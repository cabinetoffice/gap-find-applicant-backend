package gov.cabinetoffice.gap.applybackend.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GapIdGenerator {

    public static String generateGapId(final Long userId, final String env, final long recordNumber, final boolean isMandatoryQuestion) {
        final LocalDate currentDate = LocalDate.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final String date = currentDate.format(formatter);

        return "GAP" +
                "-" +
                env +
                (isMandatoryQuestion ?
                        "-MQ-" : "-") +
                date +
                "-" +
                recordNumber +
                "-" +
                userId
                ;
    }
}
