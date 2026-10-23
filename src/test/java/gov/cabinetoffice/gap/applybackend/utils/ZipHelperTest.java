package gov.cabinetoffice.gap.applybackend.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ZipHelperTest {

    private static Stream<Arguments> provideLegalNames() {
        return Stream.of(
                Arguments.of("My Organisation", "My_Organisation"),
                Arguments.of("Org: \"Name\" <Ltd>?", "Org___Name___Ltd__"),
                Arguments.of("Rainbow \uD83C\uDF08 Charity", "Rainbow_Charity"),
                Arguments.of("Ticks \u2705 and hearts \u2764\uFE0F Ltd", "Ticks_and_hearts_\u2764_Ltd"),
                Arguments.of("Café £ Société", "Café_£_Société"),
                Arguments.of("\uD83C\uDF08\uD83D\uDE80", "submission"),
                Arguments.of("An organisation name that is much longer than fifty characters", "An_organisation_name_that_is_much_longer_than_fift"),
                Arguments.of("\uD83D\uDE80An organisation name that is much longer than fifty", "An_organisation_name_that_is_much_longer_than_fift")
        );
    }

    @ParameterizedTest
    @MethodSource("provideLegalNames")
    void generateFilename_cleansLegalName(String legalName, String expectedFilename) {
        assertThat(ZipHelper.generateFilename(legalName)).isEqualTo(expectedFilename);
    }

    @Test
    void generateFilename_throwsIfLegalNameIsNull() {
        assertThrows(RuntimeException.class, () -> ZipHelper.generateFilename(null));
    }
}
