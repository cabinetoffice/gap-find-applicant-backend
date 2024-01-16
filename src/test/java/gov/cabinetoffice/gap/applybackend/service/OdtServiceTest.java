package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.model.Submission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

import static gov.cabinetoffice.gap.applybackend.testData.TestData.*;


class OdtServiceTest {
    @Test
    void compareTestGenerateSingleOdtForSchemeVersion1() throws Exception {
        OdfDocument generatedDoc = OdtService.generateSingleOdt(V1_SUBMISSION, "testEmail");
        final String generatedContent = docToString(generatedDoc.getContentDom());

        assertThat(generatedContent).contains("Eligibility");
        assertThat(generatedContent).contains("Required checks");
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
        final OdfDocument generatedDoc = OdtService.generateSingleOdt(V2_SUBMISSION_LIMITED_COMPANY_WITH_CC_AND_CH, "testEmail");
        final String generatedContent = docToString(generatedDoc.getContentDom());

        assertThat(generatedContent).contains("Organisation name: V2_Company name");
        assertThat(generatedContent).contains("Eligibility");
        assertThat(generatedContent).contains("Required checks");
        assertThat(generatedContent).contains("Legal name of organisation");
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
        final OdfDocument generatedDoc = OdtService.generateSingleOdt(
                V2_SUBMISSION_LIMITED_COMPANY_WITHOUT_CC_AND_CH, "testEmail");
        final String generatedContent = docToString(generatedDoc.getContentDom());

        assertThat(generatedContent).contains("Organisation name: V2_Company name");
        assertThat(generatedContent).contains("Eligibility");
        assertThat(generatedContent).contains("Required checks");
        assertThat(generatedContent).contains("Legal name of organisation");
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
        final OdfDocument generatedDoc = OdtService.generateSingleOdt(V2_SUBMISSION_NON_LIMITED_COMPANY, "testEmail");
        final String generatedContent = docToString(generatedDoc.getContentDom());

        assertThat(generatedContent).contains("Organisation name: V2_Company name");
        assertThat(generatedContent).contains("Eligibility");
        assertThat(generatedContent).contains("Required checks");
        assertThat(generatedContent).contains("Legal name of organisation");
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
        final OdfDocument generatedDoc = OdtService.generateSingleOdt(V2_SUBMISSION_INDIVIDUAL, "testFileName5");
        final String generatedContent = docToString(generatedDoc.getContentDom());

        assertThat(generatedContent).contains("Applicant name: V2_Company name");
        assertThat(generatedContent).contains("Eligibility");
        assertThat(generatedContent).contains("Required checks");
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

    private String docToString(Document document) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
}