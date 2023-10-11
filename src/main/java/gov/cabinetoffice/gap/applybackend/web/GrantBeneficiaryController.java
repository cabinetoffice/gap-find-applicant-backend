package gov.cabinetoffice.gap.applybackend.web;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateGrantBeneficiaryDto;
import gov.cabinetoffice.gap.applybackend.dto.api.GetGrantBeneficiaryDto;
import gov.cabinetoffice.gap.applybackend.model.GrantBeneficiary;
import gov.cabinetoffice.gap.applybackend.service.GrantBeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/equality-and-diversity")
@RestController
public class GrantBeneficiaryController {

    private final GrantBeneficiaryService grantBeneficiaryService;
    private final ModelMapper modelMapper;

    @PatchMapping
    public ResponseEntity<UUID> submitResponse(final @RequestBody @Valid CreateGrantBeneficiaryDto createGrantBeneficiaryDto,
                                               final @RequestParam UUID grantBeneficiaryId) {
        final UUID newGrantBeneficiaryId = grantBeneficiaryService.addResponse(createGrantBeneficiaryDto, grantBeneficiaryId);
        return ResponseEntity.ok(newGrantBeneficiaryId);
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<GetGrantBeneficiaryDto> getGrantBeneficiary(@PathVariable UUID submissionId) {
        GrantBeneficiary grantBeneficiary = grantBeneficiaryService.getGrantBeneficiary(submissionId);
        return ResponseEntity.ok(modelMapper.map(grantBeneficiary, GetGrantBeneficiaryDto.class));
    }
}
