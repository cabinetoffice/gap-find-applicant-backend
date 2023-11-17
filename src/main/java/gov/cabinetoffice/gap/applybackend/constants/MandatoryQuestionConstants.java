package gov.cabinetoffice.gap.applybackend.constants;

public class MandatoryQuestionConstants {

    public static final String ORGANISATION_DETAILS_SECTION_ID = "ORGANISATION_DETAILS";
    public static final String ORGANISATION_DETAILS_SECTION_TITLE = "Your organisation";
    public static final String ORGANISATION_INDIVIDUAL_DETAILS_SECTION_TITLE = "Your details";

    public static final String FUNDING_DETAILS_SECTION_ID = "FUNDING_DETAILS";
    public static final String FUNDING_DETAILS_SECTION_TITLE = "Funding";

    public static final String APPLICANT_ORG_NAME_TITLE = "Enter the name of your organisation";
    public static final String APPLICANT_SUBMISSION_ORG_NAME_TITLE = "Name";
    public static final String APPLICANT_ORG_NAME_PROFILE_FIELD = "ORG_NAME";
    public static final String APPLICANT_ORG_NAME_HINT = "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission";
    public static final String APPLICANT_ORG_NAME_ADMIN_SUMMARY = "organisation legal name";
    public static final int APPLICANT_ORG_NAME_MIN_LENGTH = 0;
    public static final int APPLICANT_ORG_NAME_MAX_LENGTH = 250;

    public static final String APPLICANT_TYPE_TITLE = "Choose your organisation type";
    public static final String APPLICANT_SUBMISSION_TYPE_TITLE = "Type of organisation";
    public static final String APPLICANT_INDIVIDUAL_SUBMISSION_TYPE_TITLE = "Type of application";
    public static final String APPLICANT_TYPE_PROFILE_FIELD = "ORG_TYPE";
    public static final String APPLICANT_TYPE_HINT_TEXT = "Choose the option that best describes your organisation";
    public static final String APPLICANT_TYPE_ADMIN_SUMMARY = "organisation type (e.g. limited company)";
    public static final String[] APPLICANT_TYPE_OPTIONS = new String[] {
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other",
            "Charity",
            "I am applying as an individual",
    };

    public static final String ORGANISATION_ADDRESS_TITLE = "Enter your organisations address";
    public static final String ORGANISATION_SUBMISSION_ADDRESS_TITLE = "Address";
    public static final String ORGANISATION_ADDRESS_PROFILE_FIELD = "ORG_ADDRESS";
    public static final String ORGANISATION_ADDRESS_ADMIN_SUMMARY = "Enter your organisations address";

    public static final String CHARITY_COMMISSION_NUMBER_TITLE = "Enter your Charity Commission number";
    public static final String CHARITY_COMMISSION_NUMBER_PROFILE_FIELD = "ORG_CHARITY_NUMBER";
    public static final String CHARITY_COMMISSION_NUMBER_HINT_TEXT = "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.";
    public static final String CHARITY_COMMISSION_NUMBER_ADMIN_SUMMARY = "Charity Commission number";
    public static final int CHARITY_COMMISSION_NUMBER_MIN_LENGTH = 2;
    public static final int CHARITY_COMMISSION_NUMBER_MAX_LENGTH = 15;
    public static final String CHARITY_COMMISSION_NUMBER_VALID_INPUT = "alphanumeric-nospace";

    public static final String COMPANIES_HOUSE_NUMBER_TITLE = "Enter your Companies House number";
    public static final String COMPANIES_HOUSE_NUMBER_PROFILE_FIELD = "ORG_COMPANIES_HOUSE";
    public static final String COMPANIES_HOUSE_NUMBER_HINT_TEXT = "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.";
    public static final String COMPANIES_HOUSE_NUMBER_ADMIN_SUMMARY = "Companies House number";
    public static final int COMPANIES_HOUSE_NUMBER_MIN_LENGTH = 2;
    public static final int COMPANIES_HOUSE_NUMBER_MAX_LENGTH = 8;
    public static final String COMPANIES_HOUSE_NUMBER_VALID_INPUT = "alphanumeric-nospace";

    public static final String APPLICANT_AMOUNT_TITLE = "How much does your organisation require as a grant?";
    public static final String APPLICANT_AMOUNT_HINT_TEXT = "Please enter whole pounds only";
    public static final String APPLICANT_AMOUNT_PREFIX = "£";
    public static final String APPLICANT_AMOUNT_ADMIN_SUMMARY = "amount of funding required";

    public static final String APPLICANT_FUNDING_LOCATION_TITLE = "Where will this funding be spent?";
    public static final String APPLICANT_FUNDING_LOCATION_HINT_TEXT = "Select the location where the grant funding will be spent. You can choose more than one, if it is being spent in more than one location.\n\nSelect all that apply:";
    public static final String APPLICANT_FUNDING_LOCATION_ADMIN_SUMMARY = "where the funding will be spent";
    public static final String[] APPLICANT_FUNDING_LOCATION_OPTIONS = new String[] {
            "North East England",
            "North West England",
            "South East England",
            "South West England",
            "Midlands",
            "Scotland",
            "Wales",
            "Northern Ireland"
    };

    public enum SUBMISSION_QUESTION_IDS {
        APPLICANT_ORG_NAME,
        APPLICANT_TYPE,
        APPLICANT_ORG_ADDRESS,
        APPLICANT_ORG_CHARITY_NUMBER,
        APPLICANT_ORG_COMPANIES_HOUSE,
        APPLICANT_AMOUNT,
        BENEFITIARY_LOCATION
    }
}
