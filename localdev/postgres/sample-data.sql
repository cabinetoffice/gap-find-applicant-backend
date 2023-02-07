DELETE FROM public.grant_submission;


DELETE FROM public.grant_application;


DELETE FROM public.grant_scheme;


DELETE FROM public.grant_applicant_organisation_profile;


DELETE FROM public.grant_applicant;
DELETE FROM public.grant_funding_organisation;
DELETE FROM public.grant_attachment;

INSERT INTO public.grant_applicant(
	id, user_id)
	VALUES (1, '75ab5fbd-0682-4d3d-a467-01c7a447f07c');

INSERT INTO public.grant_applicant_organisation_profile(
	id, address_line1, address_line2, charity_commission_number, companies_house_number, county, legal_name, postcode, town, type, applicant_id)
	VALUES (1, '9 George Square', 'City Centre', '55667788', '11223344', 'Glasgow', 'AND Digital', 'G2 1QQ', 'GLASGOW', 'LIMITED_COMPANY', 1);


INSERT INTO public.grant_scheme(grant_scheme_id, funder_id, ggis_identifier, created_date, last_updated, last_updated_by, scheme_contact, scheme_name, VERSION)
VALUES (1, 1, 'SCH-000003589', '2022-08-02 20:10:20-00', '2022-08-02 20:10:20-00', 1, 'grantadmin@and.digital', 'AND Test Grant Scheme', 1);


INSERT INTO public.grant_application(grant_application_id, application_name, created, definition, last_updated, last_update_by, status, VERSION, grant_scheme_id)
VALUES (1, 'AND Test Grant Application', '2022-08-02 20:10:20-00', '{}', '2022-08-02 20:10:20-00', 1, 'DRAFT', 1, 1);

INSERT INTO public.grant_submission(
	id, application_name, created, last_updated, status, submitted_date, version, applicant_id, application_id, created_by, last_updated_by, scheme_id, definition)
	VALUES ('3a6cfe2d-bf58-440d-9e07-3579c7dcf205', 'Test Grant Application', '2022-08-02 20:10:20-00', '2022-08-02 20:10:20-00', 'IN_PROGRESS', null, 1, 1, 1, 1, 1, 1,
	'{
       "sections": [
         {
           "sectionId": "ELIGIBILITY",
           "sectionTitle": "Eligibility",
           "sectionStatus": "COMPLETED",
           "questions": [
             {
               "questionId": "ELIGIBILITY",
               "fieldTitle": "Eligitiblity Statement",
               "displayText": "Some admin supplied text describing what it means to be eligible to apply for this grant",
               "questionSuffix": "Does your organisation meet the eligibility criteria?",
               "responseType": "YesNo",
               "validation": { "mandatory": true },
               "response": "Yes"
             }
           ]
         },
         {
           "sectionId": "ESSENTIAL",
           "sectionTitle": "Essential Information",
           "sectionStatus": "COMPLETED",
           "questions": [
             {
               "questionId": "APPLICANT_TYPE",
               "profileField": "ORG_TYPE",
               "fieldTitle": "Choose your organisation type",
               "hintText": "Choose the option that best describes your organisation",
               "responseType": "Dropdown",
               "validation": { "mandatory": true },
               "options": [
                 "Limited company",
                 "Non-limited company",
                 "Registered charity",
                 "Unregistered charity",
                 "Other"
               ],
               "response": "Limited company"
             },
             {
               "questionId": "APPLICANT_ORG_NAME",
               "profileField": "ORG_NAME",
               "fieldTitle": "Enter the name of your organisation",
               "hintText": "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission",
               "responseType": "ShortAnswer",
               "validation": { "mandatory": true, "minLength": 5, "maxLength": 100 },
               "response": "Some company name"
             },
             {
               "questionId": "APPLICANT_ORG_ADDRESS",
               "profileField": "ORG_ADDRESS",
               "fieldTitle": "Enter your organisation''s address",
               "responseType": "AddressInput",
               "validation": { "mandatory": true },
               "multiResponse": [
                 "9-10 St Andrew Square",
                 "",
                 "Edinburgh",
                 "",
                 "EH2 2AF"
               ]
             },
             {
               "questionId": "APPLICANT_ORG_COMPANIES_HOUSE",
               "profileField": "ORG_COMPANIES_HOUSE",
               "fieldTitle": "Does your organisation have a Companies House number?",
               "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
               "responseType": "YesNoPlusValue",
               "validation": { "minLength": 5, "maxLength": 100 },
               "multiResponse": ["Yes", "SC1234567"]
             },
             {
               "questionId": "APPLICANT_ORG_CHARITY_NUMBER",
               "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
               "fieldTitle": "Does your organisation have a Charity Commission Number?",
               "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
               "responseType": "YesNoPlusValue",
               "validation": { "minLength": 5, "maxLength": 100 },
               "multiResponse": ["No", ""]
             },
             {
               "questionId": "APPLICANT_AMOUNT",
               "fieldTitle": "How much does your organisation require as a grant?",
               "hintText": "Please enter whole pounds only",
               "fieldPrefix": "£",
               "responseType": "Numeric",
               "validation": { "mandatory": true, "greaterThanZero": true },
               "response": 1000
             }
           ]
         },
         {
           "sectionId": "CUSTOM_SECTION_1",
           "sectionTitle": "Project Information",
           "sectionStatus": "NOT_STARTED",
           "questions": [
             {
               "questionId": "CUSTOM_QUESTION_4",
               "fieldTitle": "Supporting Documentation",
               "responseType": "SingleFileUpload",
               "validation": {
                 "mandatory": true,
                 "allowedTypes": ["txt"]
               }
             }
           ]
         }
       ]
     }'
	);


INSERT INTO grant_funding_organisation(funder_id, organisation_name)
    VALUES(1,'Company ABC');