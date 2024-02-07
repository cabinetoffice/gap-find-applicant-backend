package gov.cabinetoffice.gap.applybackend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import gov.cabinetoffice.gap.applybackend.config.properties.S3ConfigProperties;
import gov.cabinetoffice.gap.applybackend.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZipServiceTest {

    @InjectMocks
    private ZipService serviceUnderTest;

    @Mock
    private S3ConfigProperties mockS3ConfigProperties;

    @BeforeEach
    void beforeEach() {
        Mockito.lenient().when(mockS3ConfigProperties.getBucket()).thenReturn("mockedValue");
        AmazonS3 s3Client = mock(AmazonS3.class);
        S3Object s3Obj = mock(S3Object.class);
        Mockito.lenient().when(s3Obj.getKey()).thenReturn("mockKey");
        Mockito.lenient().when(s3Obj.getObjectContent()).thenReturn(mock(S3ObjectInputStream.class));
        Mockito.lenient().when(s3Client.getObject(any())).thenReturn(s3Obj);
        ReflectionTestUtils.setField(serviceUnderTest, "client", s3Client);

        final ListObjectsV2Result res = Mockito.mock(ListObjectsV2Result.class);
        final List<S3ObjectSummary> objectSummaryList = new ArrayList<>();
        final S3ObjectSummary s3ObjectSummary1 = new S3ObjectSummary();
        s3ObjectSummary1.setKey("some/random/prefix/hello-world.txt");
        s3ObjectSummary1.setLastModified(new Date());
        objectSummaryList.add(s3ObjectSummary1);

        Mockito.lenient().when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(res);
        Mockito.lenient().when(res.getObjectSummaries())
                .thenReturn(objectSummaryList);
    }

    @Test
    void shouldReturnsByteArrayOutputStream() throws Exception {
        Submission testSubmission = Submission.builder().definition(SubmissionDefinition.builder().sections(
                List.of(SubmissionSection.builder().questions(
                                List.of(SubmissionQuestion.builder().questionId("APPLICANT_ORG_NAME").response("e").build())
                        ).sectionId("APPLICANT_ORG_NAME").sectionId("ESSENTIAL").build()))
                        .build()).id(UUID.randomUUID()).scheme(GrantScheme.builder().version(1).build())
                .application(GrantApplication.builder().id(2).build()).build();

        OdfTextDocument odt = OdfTextDocument.newTextDocument();
        ByteArrayOutputStream res = serviceUnderTest.createSubmissionZip(testSubmission, odt);

        assertTrue(res.size() > 0);
    }

    @Test
    void getSubmissionAttachmentFileNames() {
        final List<String> result = serviceUnderTest.getSubmissionAttachmentFileNames(
                "testApplicationId", "testSubmissionId");

        assertEquals(1, result.size());
        assertEquals("some/random/prefix/hello-world.txt", result.get(0));
    }

    @Test
    void shouldHandleMultiplePeriodsInFilename() {
        String result = serviceUnderTest.parseFileName("330/submission/folder/file.odt.w..pdf",
                1, "330","submission");
        assertEquals("file.odt.w._1.pdf", result);
    }
    @Test
    void shouldHandleFileNameThatAreNotInTheRegex() {
        String result = serviceUnderTest.parseFileName("330/submission/folder/file.word",
                1, "330","submission");

        assertEquals("file_1.word", result);
    }

    @Test
    void shouldHandleFileNameThatAreNotInTheRegexWithMoreDots() {
        String result = serviceUnderTest
                .parseFileName("330/submission/folder/file.a.b.c.d.word",
                        1, "330","submission");

        assertEquals("file.a.b.c.d_1.word", result);
    }

    @Test
    void shouldReturnFileNameWithSuffix() {
        String result = serviceUnderTest.parseFileName("330/submission/folder/file.pdf", 1, "330","submission");

        assertEquals("file_1.pdf", result);
    }

    @Test
    void shouldReturnFileNameWithSuffixWhenFileNameHasLoadsOfSpecialCharacter() {
        String result = serviceUnderTest.parseFileName("330/submission/folder//test... /File {{{}}} Name.???FLL. odt.<>\"/\\|?*"+ "odt.xls", 1, "330","submission");

        assertEquals("_test... _File {{{}}} Name.___FLL. odt.________odt_1.xls", result);
    }

    @Test
    void shouldTruncateLongFileNames() {
        final String folder = "330/submission/folder/";
        final String longFileName = "202303 [DRAFT] - Open Networks Ecosystem competition - Grant Funding Agreement.docx-EmbeddedFile.xlsx";
        final String truncatedLongFileName = longFileName
                .substring(0, ZipService.LONG_FILE_NAME_LENGTH).trim();

        final String result = serviceUnderTest
                .parseFileName(folder + longFileName, 1, "330","submission");

        assertEquals(truncatedLongFileName + "_1.xlsx", result);
    }
}
