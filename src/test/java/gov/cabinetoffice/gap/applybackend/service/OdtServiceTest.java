package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.enums.SubmissionQuestionResponseType;
import gov.cabinetoffice.gap.applybackend.enums.SubmissionSectionStatus;
import gov.cabinetoffice.gap.applybackend.model.GrantScheme;
import gov.cabinetoffice.gap.applybackend.model.Submission;
import gov.cabinetoffice.gap.applybackend.model.SubmissionDefinition;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestion;
import gov.cabinetoffice.gap.applybackend.model.SubmissionSection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static gov.cabinetoffice.gap.applybackend.testData.TestData.*;

@ExtendWith(MockitoExtension.class)

class OdtServiceTest {
    @InjectMocks
    OdtService odtService;


   @Test
   void compareTestGenerateSingleOdtForSchemeVersion1() throws Exception {
       OdfDocument generatedDoc = odtService.generateSingleOdt(V1_SUBMISSION, "testEmail");
       final String generatedContent = docToString(generatedDoc.getContentDom());

       assertThat(generatedContent).contains("Eligibility");
       assertThat(generatedContent).contains("Due diligence information");
       assertThat(generatedContent).contains("V1_Company name");
       assertThat(generatedContent).contains("V1_test address");
       assertThat(generatedContent).contains("V1_Edinburgh");
       assertThat(generatedContent).contains("V1_TEST_POSTCODE");
       assertThat(generatedContent).contains("V1_Limited company");
       assertThat(generatedContent).contains("V1_CHN");
       assertThat(generatedContent).contains("V1_CMSN_NO");
       assertThat(generatedContent).contains("V1_Scotland", "V1_North East England");
       assertThat(generatedContent).contains("My Custom Section");
       assertThat(generatedContent).doesNotContain("V2_Company name");
       assertThat(generatedContent).doesNotContain("V2_test address");
       assertThat(generatedContent).doesNotContain("V2_Edinburgh");
       assertThat(generatedContent).doesNotContain("V2_POSTCODE");
       assertThat(generatedContent).doesNotContain("V2_Limited company");
       assertThat(generatedContent).doesNotContain("V2_CHN");
       assertThat(generatedContent).doesNotContain("V2_CMSN_NO");
       assertThat(generatedContent).doesNotContain("V2_Scotland", "V2_North East England");
   }

   @Test
   void compareTestGenerateSingleOdtForLimitedCompanyWithCCAndCHForSchemeVersion2() throws Exception {
       final OdfDocument generatedDoc = odtService.generateSingleOdt(V2_SUBMISSION_LIMITED_COMPANY_WITH_CC_AND_CH, "testEmail");
       final String generatedContent = docToString(generatedDoc.getContentDom());

       assertThat(generatedContent).contains("Organisation Name");
       assertThat(generatedContent).contains("V2_Company name");
       assertThat(generatedContent).contains("Eligibility");
       assertThat(generatedContent).contains("Due diligence information");
       assertThat(generatedContent).contains("Organisation type");
       assertThat(generatedContent).contains("v2 limited Scheme Name");
       assertThat(generatedContent).contains("V2_test address");
       assertThat(generatedContent).contains("V2_Edinburgh");
       assertThat(generatedContent).contains("V2_POSTCODE");
       assertThat(generatedContent).contains("V2_Limited company");
       assertThat(generatedContent).contains("Companies House number if the organisation has one (if blank, number has not been entered)");
       assertThat(generatedContent).contains("V2_CHN");
       assertThat(generatedContent).contains("Charities Commission number if the organisation has one (if blank, number has not been entered)");
       assertThat(generatedContent).contains("V2_CMSN_NO");
       assertThat(generatedContent).contains("V2_Scotland", "V2_North East England");
       assertThat(generatedContent).doesNotContain("V1_test address");
       assertThat(generatedContent).doesNotContain("V1_Edinburgh");
       assertThat(generatedContent).doesNotContain("V1_TEST_POSTCODE");
       assertThat(generatedContent).doesNotContain("V1_Limited company");
       assertThat(generatedContent).doesNotContain("V1_CHN");
       assertThat(generatedContent).doesNotContain("V1_CMSN_NO");
       assertThat(generatedContent).doesNotContain("V1_Scotland", "V1_North East England");
       assertThat(generatedContent).doesNotContain("My Custom Section");
   }

   @Test
   void compareTestGenerateSingleOdtForLimitedCompanyWithoutCCAndCHForSchemeVersion2() throws Exception {
       final OdfDocument generatedDoc = odtService.generateSingleOdt(
               V2_SUBMISSION_LIMITED_COMPANY_WITHOUT_CC_AND_CH, "testEmail");
       final String generatedContent = docToString(generatedDoc.getContentDom());

       assertThat(generatedContent).contains("V2_Company name");
       assertThat(generatedContent).contains("Eligibility");
       assertThat(generatedContent).contains("Due diligence information");
       assertThat(generatedContent).contains("Organisation type");
       assertThat(generatedContent).contains("Test Org Name v2");
       assertThat(generatedContent).contains("V2_test address");
       assertThat(generatedContent).contains("V2_Edinburgh");
       assertThat(generatedContent).contains("V2_POSTCODE");
       assertThat(generatedContent).contains("V2_Limited company");
       assertThat(generatedContent).contains(
               "Companies House number if the organisation has one (if blank, number has not been entered)");
       assertThat(generatedContent).doesNotContain("V2_CHN");
       assertThat(generatedContent).contains(
               "Charities Commission number if the organisation has one (if blank, number has not been entered)");
       assertThat(generatedContent).doesNotContain("V2_CMSN_NO");
       assertThat(generatedContent).contains("V2_Scotland", "V2_North East England");
       assertThat(generatedContent).doesNotContain("V1_test address");
       assertThat(generatedContent).doesNotContain("V1_Edinburgh");
       assertThat(generatedContent).doesNotContain("V1_TEST_POSTCODE");
       assertThat(generatedContent).doesNotContain("V1_Limited company");
       assertThat(generatedContent).doesNotContain("V1_CHN");
       assertThat(generatedContent).doesNotContain("V1_CMSN_NO");
       assertThat(generatedContent).doesNotContain("V1_Scotland", "V1_North East England");
       assertThat(generatedContent).doesNotContain("My Custom Section");
   }

   @Test
   void compareTestGenerateSingleOdtForNonLimitedCompanyForSchemeVersion2() throws Exception {
       final OdfDocument generatedDoc = odtService.generateSingleOdt(V2_SUBMISSION_NON_LIMITED_COMPANY, "testEmail");
       final String generatedContent = docToString(generatedDoc.getContentDom());
       assertThat(generatedContent).contains("Organisation Name");
       assertThat(generatedContent).contains("V2_Company name");
       assertThat(generatedContent).contains("Eligibility");
       assertThat(generatedContent).contains("Due diligence information");
       assertThat(generatedContent).contains("Organisation details");
       assertThat(generatedContent).contains("Test Org Name v2");
       assertThat(generatedContent).contains("V2_test address");
       assertThat(generatedContent).contains("V2_Edinburgh");
       assertThat(generatedContent).contains("V2_POSTCODE");
       assertThat(generatedContent).contains("Non-limited company");
       assertThat(generatedContent).doesNotContain(
               "Companies House number if the organisation has one (if blank, number has not been entered)");
       assertThat(generatedContent).doesNotContain("V2_CHN");
       assertThat(generatedContent).doesNotContain(
               "Charities Commission number if the organisation has one (if blank, number has not been entered)");
       assertThat(generatedContent).doesNotContain("V2_CMSN_NO");
       assertThat(generatedContent).contains("V2_Scotland", "V2_North East England");
       assertThat(generatedContent).doesNotContain("V1_test address");
       assertThat(generatedContent).doesNotContain("V1_Edinburgh");
       assertThat(generatedContent).doesNotContain("V1_TEST_POSTCODE");
       assertThat(generatedContent).doesNotContain("V1_Limited company");
       assertThat(generatedContent).doesNotContain("V1_CHN");
       assertThat(generatedContent).doesNotContain("V1_CMSN_NO");
       assertThat(generatedContent).doesNotContain("V1_Scotland", "V1_North East England");
       assertThat(generatedContent).doesNotContain("My Custom Section");
   }

   @Test
   void compareTestGenerateSingleOdtForIndividualForSchemeVersion2() throws Exception {
       final OdfDocument generatedDoc = odtService.generateSingleOdt(V2_SUBMISSION_INDIVIDUAL, "testFileName5");
       final String generatedContent = docToString(generatedDoc.getContentDom());


       assertThat(generatedContent).contains("Applicant name");
       assertThat(generatedContent).contains("V2_Company name");
       assertThat(generatedContent).contains("Eligibility");
       assertThat(generatedContent).contains("Due diligence information");
       assertThat(generatedContent).contains("Applicant name");
       assertThat(generatedContent).contains("Test Org Name v2");
       assertThat(generatedContent).contains("V2_test address");
       assertThat(generatedContent).contains("V2_Edinburgh");
       assertThat(generatedContent).contains("V2_POSTCODE");
       assertThat(generatedContent).contains("I am applying as an individual");
       assertThat(generatedContent).doesNotContain(
               "Companies House number if the organisation has one (if blank, number has not been entered)");
       assertThat(generatedContent).doesNotContain("V2_CHN");
       assertThat(generatedContent).doesNotContain(
               "Charities Commission number if the organisation has one (if blank, number has not been entered)");
       assertThat(generatedContent).doesNotContain("V2_CMSN_NO");
       assertThat(generatedContent).contains("V2_Scotland", "V2_North East England");
       assertThat(generatedContent).doesNotContain("V1_test address");
       assertThat(generatedContent).doesNotContain("V1_Edinburgh");
       assertThat(generatedContent).doesNotContain("V1_TEST_POSTCODE");
       assertThat(generatedContent).doesNotContain("V1_Limited company");
       assertThat(generatedContent).doesNotContain("V1_CHN");
       assertThat(generatedContent).doesNotContain("V1_CMSN_NO");
       assertThat(generatedContent).doesNotContain("V1_Scotland", "V1_North East England");
       assertThat(generatedContent).doesNotContain("My Custom Section");
   }

    @Test
    void compareTestGenerateSingleOdtForLocalAuthorityForSchemeVersion2() throws Exception {
        final OdfDocument generatedDoc = odtService.generateSingleOdt(V2_SUBMISSION_LOCAL_AUTHORITY, "testFileName5");
        final String generatedContent = docToString(generatedDoc.getContentDom());


        assertThat(generatedContent).contains("Organisation Name");
        assertThat(generatedContent).contains("V2_Company name");
        assertThat(generatedContent).contains("Eligibility");
        assertThat(generatedContent).contains("Due diligence information");
        assertThat(generatedContent).contains("Organisation details");
        assertThat(generatedContent).contains("Test Org Name v2");
        assertThat(generatedContent).contains("V2_test address");
        assertThat(generatedContent).contains("V2_Edinburgh");
        assertThat(generatedContent).contains("V2_POSTCODE");
        assertThat(generatedContent).contains("Local authority");
        assertThat(generatedContent).doesNotContain(
                "Companies House number if the organisation has one (if blank, number has not been entered)");
        assertThat(generatedContent).doesNotContain("V2_CHN");
        assertThat(generatedContent).doesNotContain(
                "Charities Commission number if the organisation has one (if blank, number has not been entered)");
        assertThat(generatedContent).doesNotContain("V2_CMSN_NO");
        assertThat(generatedContent).contains("V2_Scotland", "V2_North East England");
        assertThat(generatedContent).doesNotContain("V1_test address");
        assertThat(generatedContent).doesNotContain("V1_Edinburgh");
        assertThat(generatedContent).doesNotContain("V1_TEST_POSTCODE");
        assertThat(generatedContent).doesNotContain("V1_Limited company");
        assertThat(generatedContent).doesNotContain("V1_CHN");
        assertThat(generatedContent).doesNotContain("V1_CMSN_NO");
        assertThat(generatedContent).doesNotContain("V1_Scotland", "V1_North East England");
        assertThat(generatedContent).doesNotContain("My Custom Section");
    }

    @Test
    void generateSingleOdt_stripsUnsupportedCharactersFromSavedDocument() throws Exception {
        final SubmissionSection customSection = SubmissionSection.builder()
                .sectionId("26215de2-2211-48e4-9c82-835cd227daad")
                .sectionTitle("Project details \uD83D\uDE80")
                .sectionStatus(SubmissionSectionStatus.COMPLETED)
                .questions(List.of(SubmissionQuestion.builder()
                        .questionId("PROJECT_SUMMARY")
                        .fieldTitle("Describe your project")
                        .responseType(SubmissionQuestionResponseType.LongAnswer)
                        .response("Our \uD83C\uDF08 project costs £1,000 – café")
                        .build()))
                .build();

        final Submission submission = Submission.builder()
                .scheme(GrantScheme.builder().version(2).name("Emoji Scheme").build())
                .gapId("GAP-3")
                .submittedDate(ZonedDateTime.parse("2022-02-15T18:35:24.00Z"))
                .definition(SubmissionDefinition.builder().sections(List.of(
                        ELIGIBILITY_SECTION_SUBMISSION,
                        ORGANISATION_DETAILS_SECTION_SUBMISSION_NON_LIMITED,
                        FUNDING_DETAILS_SECTION_SUBMISSION,
                        customSection)).build())
                .version(2)
                .build();

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        odtService.generateSingleOdt(submission, "testEmail").save(outputStream);
        final String savedContent = readContentXml(outputStream.toByteArray());

        assertThat(savedContent).doesNotContain("&#55356;", "&#55357;", "\uD83C\uDF08", "\uD83D\uDE80");
        assertThat(savedContent).contains("Project details ", "Our  project costs £1,000 – café");
    }

    private String readContentXml(byte[] odt) throws Exception {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(odt))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals("content.xml")) {
                    return new String(zip.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        }
        throw new IllegalStateException("content.xml not found in ODT");
    }

   private String docToString(Document document) throws Exception {
       TransformerFactory tf = TransformerFactory.newInstance();
       Transformer transformer = tf.newTransformer();
       StringWriter writer = new StringWriter();
       transformer.transform(new DOMSource(document), new StreamResult(writer));
       return writer.getBuffer().toString();
   }
}