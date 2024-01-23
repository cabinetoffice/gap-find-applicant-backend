package gov.cabinetoffice.gap.applybackend.provider;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.stereotype.Service;

@Service
public class AmazonProvider {
    public static AmazonS3 initializeS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_2)
                .build();
    }
}
