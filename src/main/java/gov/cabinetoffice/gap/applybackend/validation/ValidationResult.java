package gov.cabinetoffice.gap.applybackend.validation;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class ValidationResult {
    @Builder.Default
    private boolean isValid = false;

    @Builder.Default
    private Map<String, String> fieldErrors = new HashMap<>();

    public void addError(String key, String value) {
        this.fieldErrors.put(key, value);
    }
}
