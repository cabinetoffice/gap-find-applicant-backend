package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.dto.api.CreateGrantBeneficiaryDto;
import gov.cabinetoffice.gap.applybackend.exception.NotFoundException;
import gov.cabinetoffice.gap.applybackend.model.GrantBeneficiary;
import gov.cabinetoffice.gap.applybackend.repository.GrantBeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GrantBeneficiaryService {

    private final GrantBeneficiaryRepository grantBeneficiaryRepository;
    private final ModelMapper modelMapper;

    public UUID addResponse(final CreateGrantBeneficiaryDto createGrantBeneficiaryDto,
                            final UUID grantBeneficiaryId) {
        final GrantBeneficiary grantBeneficiary = grantBeneficiaryRepository.findById(grantBeneficiaryId)
                .orElseThrow(() -> new NotFoundException("Could not find a grant beneficiary with the id: " + grantBeneficiaryId));

        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(createGrantBeneficiaryDto, grantBeneficiary);
        grantBeneficiary.setGrantBeneficiaryId(grantBeneficiaryId);

        return grantBeneficiaryRepository.save(grantBeneficiary).getGrantBeneficiaryId();
    }

    public GrantBeneficiary getGrantBeneficiary(final UUID submissionId) {
        return grantBeneficiaryRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new NotFoundException("Could not find a grant beneficiary with the submission id: " + submissionId));
    }
}
