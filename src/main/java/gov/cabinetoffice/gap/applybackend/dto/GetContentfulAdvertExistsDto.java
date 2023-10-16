package gov.cabinetoffice.gap.applybackend.dto;


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
    private boolean isAdvertInContentful = false;
}
