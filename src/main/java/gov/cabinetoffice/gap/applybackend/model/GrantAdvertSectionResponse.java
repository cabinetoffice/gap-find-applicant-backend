package gov.cabinetoffice.gap.applybackend.model;

import gov.cabinetoffice.gap.applybackend.enums.GrantAdvertSectionResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantAdvertSectionResponse {

    @Builder.Default
    private List<GrantAdvertPageResponse> pages = new ArrayList<>();

    private String id;

    @Builder.Default
    private GrantAdvertSectionResponseStatus status = GrantAdvertSectionResponseStatus.NOT_STARTED;

    public Optional<GrantAdvertPageResponse> getPageById(String pageId) {
        return this.pages.stream().filter(page -> Objects.equals(page.getId(), pageId)).findFirst();

    }

}
