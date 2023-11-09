package gov.cabinetoffice.gap.applybackend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetContentfulAdvertExistsDto {

    @Builder.Default
    @JsonProperty("isAdvertInContentful")
    private boolean isAdvertInContentful = false;
}
