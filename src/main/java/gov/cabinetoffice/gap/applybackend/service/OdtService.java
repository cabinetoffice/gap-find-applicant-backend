package gov.cabinetoffice.gap.applybackend.service;

import gov.cabinetoffice.gap.applybackend.model.Submission;
import gov.cabinetoffice.gap.applybackend.model.SubmissionQuestion;
import gov.cabinetoffice.gap.applybackend.model.SubmissionSection;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.style.*;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.OdfStylePropertySet;
import org.odftoolkit.odfdom.dom.style.props.OdfPageLayoutProperties;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OdtService {

    private static final Logger logger = LoggerFactory.getLogger(OdtService.class);
    private static final String ELIGIBILITY_SECTION_ID = "ELIGIBILITY";
    private static final String ESSENTIAL_SECTION_ID = "ESSENTIAL";
    private static final String ORGANISATION_DETAILS_SECTION_ID = "ORGANISATION_DETAILS";
    private static final String FUNDING_DETAILS_SECTION_ID = "FUNDING_DETAILS";
    private static final String APPLICANT_TYPE = "APPLICANT_TYPE";
    private static final String APPLICANT_ORG_NAME = "APPLICANT_ORG_NAME";
    private static final String APPLICANT_ORG_ADDRESS = "APPLICANT_ORG_ADDRESS";
    private static final String APPLICANT_ORG_CHARITY_NUMBER = "APPLICANT_ORG_CHARITY_NUMBER";
    private static final String APPLICANT_ORG_COMPANIES_HOUSE = "APPLICANT_ORG_COMPANIES_HOUSE";
    private static final String APPLICANT_AMOUNT = "APPLICANT_AMOUNT";
    private static final String BENEFITIARY_LOCATION = "BENEFITIARY_LOCATION";
    private static final String APPLICANT_ORG_TYPE_INDIVIDUAL = "I am applying as an individual";
    private static final String Heading_20_1 = "Heading_20_1";
    private static final String Heading_20_2 = "Heading_20_2";
    private static final String Heading_20_3 = "Heading_20_3";
    private static final String Text_20_1 = "Text_20_1";
    private static final String Text_20_2 = "Text_20_2";
    private static final String Text_20_3 = "Text_20_3";

    public OdfTextDocument generateSingleOdt(final Submission submission, final String email) {
        try {
            OdfStyleProcessor styleProcessor = new OdfStyleProcessor();
            int schemeVersion = submission.getScheme().getVersion();
            OdfTextDocument odt = OdfTextDocument.newTextDocument();
            OdfOfficeStyles stylesOfficeStyles = odt.getOrCreateDocumentStyles();
            OdfContentDom contentDom = odt.getContentDom();
            OfficeTextElement documentText = odt.getContentRoot();
            final String fundingSectionName = schemeVersion == 1 ?
                    ESSENTIAL_SECTION_ID : FUNDING_DETAILS_SECTION_ID;
            final String requiredCheckSectionName = schemeVersion == 1 ?
                    ESSENTIAL_SECTION_ID : ORGANISATION_DETAILS_SECTION_ID;
            final SubmissionSection requiredCheckSection = submission.getSection(requiredCheckSectionName);
            final String orgType = requiredCheckSection.getQuestionById(APPLICANT_TYPE).getResponse();
            final boolean isIndividual = Objects.equals(orgType, APPLICANT_ORG_TYPE_INDIVIDUAL);

            setOfficeStyles(odt, styleProcessor, stylesOfficeStyles);

            populateHeadingSection(submission, documentText, contentDom,
                    isIndividual, odt);

            odt.getContentRoot().setTextUseSoftPageBreaksAttribute(true);

            addPageBreak(contentDom, odt);

            OdfTextParagraph sectionBreak = new OdfTextParagraph(contentDom);

            populateEligibilitySection(submission, documentText, contentDom, sectionBreak);

            addPageBreak(contentDom, odt);

            populateRequiredChecksSection(submission, documentText, contentDom,
                    requiredCheckSection, email, fundingSectionName, odt);

            addPageBreak(contentDom, odt);

            AtomicInteger count = new AtomicInteger(3); //2 sections already added
            documentText.appendChild(new OdfTextParagraph(contentDom)
                    .addStyledContentWhitespace(Heading_20_2, "Custom sections"));
            submission.getSections().forEach(section -> {
                if (!Objects.equals(section.getSectionId(), ELIGIBILITY_SECTION_ID) &&
                        !Objects.equals(section.getSectionId(), ESSENTIAL_SECTION_ID) &&
                        !Objects.equals(section.getSectionId(), ORGANISATION_DETAILS_SECTION_ID) &&
                        !Objects.equals(section.getSectionId(), FUNDING_DETAILS_SECTION_ID)) {
                    populateQuestionResponseTable(count, section, documentText, contentDom, odt);
                }
            });
            logger.info("ODT file generated successfully");
            return odt;
        } catch (Exception e) {
            logger.error("Could not generate ODT for given submission", e);
            throw new RuntimeException(e);
        }
    }

    private void addPageBreak(OdfContentDom contentDocument, OdfTextDocument doc) throws Exception {
        final OdfOfficeAutomaticStyles styles = contentDocument.getAutomaticStyles();
        final OdfStyle style = styles.newStyle(OdfStyleFamily.Paragraph);
        style.newStyleParagraphPropertiesElement().setFoBreakBeforeAttribute("page");
        final TextPElement page = doc.getContentRoot().newTextPElement();
        page.setStyleName(style.getStyleNameAttribute());
    }

    private static void populateHeadingSection(final Submission submission,
                                               final OfficeTextElement documentText,
                                               final OdfContentDom contentDom,
                                               final boolean isIndividual,
                                               final OdfTextDocument odt){

        OdfTextHeading h1 = new OdfTextHeading(contentDom);
        OdfTextHeading h2 = new OdfTextHeading(contentDom);
        OdfTextParagraph p = new OdfTextParagraph(contentDom);

        h1.addStyledContentWhitespace(Heading_20_1, submission.getLegalName());
        p.addStyledContentWhitespace(Text_20_1, "Application for " + submission.getScheme().getName() + "\n");
        h2.addStyledContentWhitespace(Heading_20_2, "Application details");
        OdfTable table;

        if(isIndividual){
           table = OdfTable.newTable(odt, 3, 2);
            table.getRowByIndex(0).getCellByIndex(0).setStringValue("Lead Applicant");
            table.getRowByIndex(0).getCellByIndex(1).setStringValue("test.super-admin@gov.uk");
            table.getRowByIndex(1).getCellByIndex(0).setStringValue("Applying for");
            table.getRowByIndex(1).getCellByIndex(1).setStringValue(submission.getScheme().getName());
            table.getRowByIndex(2).getCellByIndex(0).setStringValue("Submitted on");
            table.getRowByIndex(2).getCellByIndex(1).setStringValue(Objects.equals(null,
                    submission.getSubmittedDate())
                    ? "Not yet submitted" : String.valueOf(submission.getSubmittedDate()));
        } else {
            table = OdfTable.newTable(odt, 4, 2);
            table.getRowByIndex(0).getCellByIndex(0).setStringValue("Organisation");
            table.getRowByIndex(0).getCellByIndex(1).setStringValue(submission.getLegalName());
            table.getRowByIndex(1).getCellByIndex(0).setStringValue("Lead Applicant");
            table.getRowByIndex(1).getCellByIndex(1).setStringValue("test.super-admin@gov.uk");
            table.getRowByIndex(2).getCellByIndex(0).setStringValue("Applying for");
            table.getRowByIndex(2).getCellByIndex(1).setStringValue(submission.getScheme().getName());
            table.getRowByIndex(3).getCellByIndex(0).setStringValue("Submitted on");
            table.getRowByIndex(3).getCellByIndex(1).setStringValue(Objects.equals(null,
                    String.valueOf(submission.getSubmittedDate()))
                    ? "Not yet submitted" : String.valueOf(submission.getSubmittedDate()));

        }
        documentText.appendChild(h1);
        documentText.appendChild(p);
        documentText.appendChild(h2);
        documentText.appendChild(table.getOdfElement());
    }

    private static void populateRequiredChecksSection(final Submission submission,
                                                   final OfficeTextElement documentText,
                                                   final OdfContentDom contentDom,
                                                   final SubmissionSection requiredCheckSection,
                                                   final String email,
                                                   final String fundingSectionName,
                                                      final OdfTextDocument odt) {
        OdfTextHeading requiredCheckHeading = new OdfTextHeading(contentDom);
        OdfTextHeading requiredCheckSubHeading = new OdfTextHeading(contentDom);
        OdfTextParagraph locationQuestion = new OdfTextParagraph(contentDom);
        final String orgType = requiredCheckSection.getQuestionById(APPLICANT_TYPE).getResponse();
        final boolean isIndividual = Objects.equals(orgType, APPLICANT_ORG_TYPE_INDIVIDUAL);
        final String orgNameHeading = isIndividual ? "Applicant details" : "Organisation details";

        requiredCheckHeading.addStyledContentWhitespace(Heading_20_2, "Due diligence information");
        requiredCheckSubHeading.addStyledContentWhitespace(Heading_20_3, orgNameHeading);

        documentText.appendChild(requiredCheckHeading);
        documentText.appendChild(requiredCheckSubHeading);
        documentText.appendChild(generateEssentialTable(requiredCheckSection, odt));
        documentText.appendChild(new OdfTextParagraph(contentDom).addContentWhitespace(""));
        locationQuestion.addStyledContent(Heading_20_3, "Funding");
        OdfTable table = OdfTable.newTable(odt, 2, 2);

        table.getRowByIndex(0).getCellByIndex(0).setStringValue("Amount applied for");
        table.getRowByIndex(0).getCellByIndex(1).setStringValue("£" + submission.getQuestion(fundingSectionName, APPLICANT_AMOUNT).getResponse());
        table.getRowByIndex(1).getCellByIndex(0).setStringValue("Where funding will be spent");
        table.getRowByIndex(1).getCellByIndex(1).setStringValue(String.join(",\n",
                submission.getQuestion(fundingSectionName, BENEFITIARY_LOCATION).getMultiResponse()));

        documentText.appendChild(locationQuestion);
        documentText.appendChild(table.getOdfElement());
    }

    private static void populateEligibilitySection(final Submission submission,
                                                          final OfficeTextElement documentText,
                                                          final OdfContentDom contentDom,
                                                          final OdfTextParagraph sectionBreak
    ) {
        final SubmissionSection eligibilitySection = submission.getSection(ELIGIBILITY_SECTION_ID);
        OdfTextHeading eligibilityHeading = new OdfTextHeading(contentDom);
        OdfTextParagraph eligibilityStatement = new OdfTextParagraph(contentDom);
        OdfTextParagraph eligibilityResponse = new OdfTextParagraph(contentDom);

        documentText.appendChild(sectionBreak);
        eligibilityHeading.addStyledContent(Heading_20_2, "Eligibility");
        documentText.appendChild(eligibilityHeading);

        OdfTextHeading eligibilitySubHeading = new OdfTextHeading(contentDom);
        eligibilitySubHeading.addStyledContent(Heading_20_3, "Eligibility Statement");
        documentText.appendChild(eligibilitySubHeading);

        eligibilityResponse.addStyledContentWhitespace(Text_20_3, eligibilitySection
                .getQuestionById(ELIGIBILITY_SECTION_ID).getDisplayText() + "\n\n");

        documentText.appendChild(eligibilityResponse);

        eligibilityResponse.addContentWhitespace("Applicant" + (Objects.equals(eligibilitySection
                .getQuestionById(ELIGIBILITY_SECTION_ID).getResponse(), "Yes") ?
                " agreed to" : " did not agree to") + " the eligibility statement.");
        documentText.appendChild(new OdfTextHeading(contentDom).addContentWhitespace("\n\n"));
        documentText.appendChild(eligibilityStatement);
    }

    private static void populateQuestionResponseTable(AtomicInteger count,
                                              SubmissionSection section,
                                              OfficeTextElement documentText,
                                              OdfContentDom contentDom, OdfTextDocument odt) {
                OdfTextHeading sectionHeading = new OdfTextHeading(contentDom);
                sectionHeading.addStyledContentWhitespace(Heading_20_3, section.getSectionTitle());
                documentText.appendChild(sectionHeading);

                AtomicInteger questionIndex = new AtomicInteger(0);
                OdfTable table = OdfTable.newTable(odt, section.getQuestions().size(), 2);
                section.getQuestions().forEach(question -> {
                    populateDocumentFromQuestionResponse(question, documentText, contentDom, questionIndex, odt,
                             table);
                    questionIndex.incrementAndGet();
                });
                documentText.appendChild(new OdfTextParagraph(contentDom).addContentWhitespace(""));
                count.getAndIncrement();
        };

    private static void populateDocumentFromQuestionResponse(SubmissionQuestion question,
                                                             OfficeTextElement documentText,
                                                             OdfContentDom contentDom, AtomicInteger questionIndex,
                                                             OdfTextDocument odt, OdfTable table) {
            switch (question.getResponseType()) {
                case AddressInput, MultipleSelection -> {
                    table.getRowByIndex(questionIndex.get()).getCellByIndex(0).setStringValue(question.getFieldTitle());
                    if (question.getMultiResponse() != null) {
                        table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue(String.join(",\n",
                                question.getMultiResponse()) + "\n");
                    } else {
                        table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue("Not provided");
                    }
                }
                case SingleFileUpload -> {
                    table.getRowByIndex(questionIndex.get()).getCellByIndex(0).setStringValue(question.getFieldTitle());
                    if (question.getResponse() != null) {
                        int index = question.getResponse().lastIndexOf(".");
                        String fileInfo = "File name: " + question.getResponse().substring(0, index) + "\n" +
                                "File extension: " + question.getResponse().substring(index + 1);

                        table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue(fileInfo);

                    } else {
                        table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue("Not provided");
                    }
                }
                case Date -> {
                    table.getRowByIndex(questionIndex.get()).getCellByIndex(0).setStringValue(question.getFieldTitle());
                    if (question.getMultiResponse() != null) {
                        final String date = String.join("-", question.getMultiResponse());
                        table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue(date);
                    } else {
                        table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue("Not provided");
                    }
                }
                case YesNo, Dropdown, ShortAnswer, LongAnswer, Numeric -> {
                    table.getRowByIndex(questionIndex.get()).getCellByIndex(0).setStringValue(question.getFieldTitle());
                    if (question.getResponse() == null || question.getResponse().isEmpty()) {
                        table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue("Not provided");
                    } else {
                        table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue(question.getResponse());
                    }
                }
                default ->  {
                    table.getRowByIndex(questionIndex.get()).getCellByIndex(0).setStringValue(question.getFieldTitle());
                    table.getRowByIndex(questionIndex.get()).getCellByIndex(1).setStringValue(question.getResponse());
                }
            }
            documentText.appendChild(table.getOdfElement());
    }

    private static TableTableElement generateEssentialTable(
                                                            final SubmissionSection section,
                                                             OdfTextDocument doc) {
        final String orgType = section.getQuestionById(APPLICANT_TYPE).getResponse();
        final boolean isIndividual = Objects.equals(orgType, APPLICANT_ORG_TYPE_INDIVIDUAL);
        final String orgNameHeading = isIndividual ? "Applicant name" : "Organisation Name";
        OdfTable odfTable;

        if(isIndividual)  {
            odfTable = OdfTable.newTable(doc, 7, 2);
        } else {
            odfTable = OdfTable.newTable(doc, 9, 2);
        }

        odfTable.getRowByIndex(0).getCellByIndex(0).setStringValue(orgNameHeading);
        odfTable.getRowByIndex(0).getCellByIndex(1).setStringValue(section.getQuestionById(APPLICANT_ORG_NAME).getResponse());
        odfTable.getRowByIndex(1).getCellByIndex(0).setStringValue("Organisation type");
        odfTable.getRowByIndex(1).getCellByIndex(1).setStringValue(orgType);

        String[] applicantOrgAddress = section.getQuestionById(APPLICANT_ORG_ADDRESS).getMultiResponse();
        odfTable.getRowByIndex(2).getCellByIndex(0).setStringValue("Address line 1");
        odfTable.getRowByIndex(2).getCellByIndex(1).setStringValue(applicantOrgAddress[0]);
        odfTable.getRowByIndex(3).getCellByIndex(0).setStringValue("Address line 2");
        odfTable.getRowByIndex(3).getCellByIndex(1).setStringValue(applicantOrgAddress[1]);
        odfTable.getRowByIndex(4).getCellByIndex(0).setStringValue("Address city");
        odfTable.getRowByIndex(4).getCellByIndex(1).setStringValue(applicantOrgAddress[2]);
        odfTable.getRowByIndex(5).getCellByIndex(0).setStringValue("Address county");
        odfTable.getRowByIndex(5).getCellByIndex(1).setStringValue(applicantOrgAddress[3]);
        odfTable.getRowByIndex(6).getCellByIndex(0)
                .setStringValue("Address postcode");
        odfTable.getRowByIndex(6).getCellByIndex(1)
                .setStringValue(applicantOrgAddress[4]);

        if(isIndividual){
            return odfTable.getOdfElement();
        }
            int index = 7;
            final boolean hasCharityCommissionNumber = section
                    .optionalGetQuestionById(APPLICANT_ORG_CHARITY_NUMBER)
                    .isPresent();
            if (hasCharityCommissionNumber) {
                odfTable.getRowByIndex(index)
                        .getCellByIndex(0)
                        .setStringValue("Charities Commission number if the organisation has one (if blank, number has not been entered)");
                odfTable.getRowByIndex(index)
                        .getCellByIndex(1)
                        .setStringValue(section
                                .optionalGetQuestionById(APPLICANT_ORG_CHARITY_NUMBER)
                                .map(SubmissionQuestion::getResponse)
                                .orElse("")
                        );
                index++;
            }
            final boolean hasCompaniesHouseNumber = section
                    .optionalGetQuestionById(APPLICANT_ORG_COMPANIES_HOUSE)
                    .isPresent();
            if (hasCompaniesHouseNumber) {
                odfTable.getRowByIndex(index)
                        .getCellByIndex(0)
                        .setStringValue("Companies House number if the organisation has one (if blank, number has not been entered)");
                odfTable.getRowByIndex(index)
                        .getCellByIndex(1)
                        .setStringValue(section
                                .optionalGetQuestionById(APPLICANT_ORG_COMPANIES_HOUSE)
                                .map(SubmissionQuestion::getResponse)
                                .orElse("")
                        );
            }

        return odfTable.getOdfElement();
    }

    private static void setOfficeStyles(OdfTextDocument outputDocument, OdfStyleProcessor styleProcessor, OdfOfficeStyles stylesOfficeStyles) {
        // Set landscape layout
        StyleMasterPageElement defaultPage = outputDocument.getOfficeMasterStyles().getMasterPage("Standard");
        String pageLayoutName = defaultPage.getStylePageLayoutNameAttribute();
        OdfStylePageLayout pageLayoutStyle = defaultPage.getAutomaticStyles().getPageLayout(pageLayoutName);
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.PrintOrientation, "Portrait");

        styleProcessor.setStyle(stylesOfficeStyles.getDefaultStyle(OdfStyleFamily.Paragraph))
                .margins("0cm", "0cm", "0cm", "0cm")
                .fontFamilly("Arial")
                .fontSize("11pt")
                .textAlign("normal");


        // Title 1
        styleProcessor.setStyle(stylesOfficeStyles.getStyle(Heading_20_1, OdfStyleFamily.Paragraph))
                .margins("0cm", "0cm", "0cm", "0cm").
                color("#000000")
                .fontWeight("normal")
                .fontSize("26pt");

        // Title 2
        styleProcessor.setStyle(stylesOfficeStyles.getStyle(Heading_20_2, OdfStyleFamily.Paragraph))
                .fontStyle("normal")
                .margins("0cm", "0cm", "0.2cm", "0cm")
                .fontWeight("normal")
                .fontSize("20pt")
                .color("#000000");

        // Title 3
        styleProcessor.setStyle(stylesOfficeStyles.getStyle(Heading_20_3, OdfStyleFamily.Paragraph))
                .margins("0cm", "0cm", "0.2cm", "0cm")
                .fontWeight("normal")
                .fontSize("16pt");

        //test
        styleProcessor.setStyle(stylesOfficeStyles.newStyle(Text_20_1, OdfStyleFamily.Text))
                .margins("0cm", "0cm", "1cm", "0cm")
                .fontFamilly("Arial")
                .fontSize("15pt")
                .color("#000000");

        styleProcessor.setStyle(stylesOfficeStyles.newStyle(Text_20_2, OdfStyleFamily.Text))
                .margins("0cm", "0cm", "0cm", "0cm")
                .fontFamilly("Arial")
                .fontSize("11pt")
                .color("#000000");

        styleProcessor.setStyle(stylesOfficeStyles.newStyle(Text_20_3, OdfStyleFamily.Text))
                .margins("0cm", "0cm", "0cm", "0cm")
                .fontFamilly("Arial")
                .fontSize("11pt")
                .color("#000000")
                .fontStyle("italic");
    }

    private class OdfStyleProcessor {

        private OdfStylePropertySet style;

        public OdfStyleProcessor() {

        }

        public OdfStyleProcessor setStyle(OdfStylePropertySet style) {
            this.style = style;
            return this;
        }

        public OdfStyleProcessor fontFamilly(String value) {
            this.style.setProperty(StyleTextPropertiesElement.FontFamily, value);
            this.style.setProperty(StyleTextPropertiesElement.FontName, value);
            return this;
        }

        public OdfStyleProcessor fontWeight(String value) {
            this.style.setProperty(StyleTextPropertiesElement.FontWeight, value);
            this.style.setProperty(StyleTextPropertiesElement.FontWeightAsian, value);
            this.style.setProperty(StyleTextPropertiesElement.FontWeightComplex, value);
            return this;
        }

        public OdfStyleProcessor fontStyle(String value) {
            this.style.setProperty(StyleTextPropertiesElement.FontStyle, value);
            this.style.setProperty(StyleTextPropertiesElement.FontStyleAsian, value);
            this.style.setProperty(StyleTextPropertiesElement.FontStyleComplex, value);
            return this;
        }

        public OdfStyleProcessor fontSize(String value) {
            this.style.setProperty(StyleTextPropertiesElement.FontSize, value);
            this.style.setProperty(StyleTextPropertiesElement.FontSizeAsian, value);
            this.style.setProperty(StyleTextPropertiesElement.FontSizeComplex, value);
            return this;
        }

        public OdfStyleProcessor margins(String top, String right, String bottom, String left) {
            this.style.setProperty(StyleParagraphPropertiesElement.MarginTop, top);
            this.style.setProperty(StyleParagraphPropertiesElement.MarginRight, right);
            this.style.setProperty(StyleParagraphPropertiesElement.MarginBottom, bottom);
            this.style.setProperty(StyleParagraphPropertiesElement.MarginLeft, left);
            return this;
        }

        public OdfStyleProcessor color(String value) {
            this.style.setProperty(StyleTextPropertiesElement.Color, value);
            return this;
        }

        public OdfStyleProcessor textAlign(String value) {
            this.style.setProperty(StyleParagraphPropertiesElement.TextAlign, value);
            return this;
        }

    }
}
