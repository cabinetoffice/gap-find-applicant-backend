package gov.cabinetoffice.gap.applybackend.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    public static AmazonS3 initializeS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_2)
                .build();
    }
}
