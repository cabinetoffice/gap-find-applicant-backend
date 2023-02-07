DELETE FROM public.grant_submission;


DELETE FROM public.grant_application;


DELETE FROM public.grant_scheme;


DELETE FROM public.grant_applicant_organisation_profile;


DELETE FROM public.grant_applicant;
DELETE FROM public.grant_admin;
DELETE FROM public.grant_funding_organisation;
DELETE FROM public.gap_user;
-- TODO delete from grant beneficiary and diligence checks too?


-- Create ADMIN data

INSERT INTO public.grant_funding_organisation(funder_id, organisation_name)
  VALUES(1,'Company ABC');
INSERT INTO grant_funding_organisation VALUES (2, 'Evil Org');

INSERT INTO gap_user values (1, '6a6131f2-239e-11ed-861d-0242ac120002');
INSERT INTO grant_admin values (1, 1, 1);

INSERT INTO gap_user values (2, '55a0b020-239f-11ed-861d-0242ac120002');
INSERT INTO grant_admin values (2, 1, 2);

INSERT INTO gap_user VALUES (3, '281b3363-6eab-44fc-a6af-d87cc4b94131');
INSERT INTO grant_admin VALUES (3, 2, 3);

-- Create APPLICANTS

INSERT INTO public.grant_applicant(id, user_id) VALUES (1, '75ab5fbd-0682-4d3d-a467-01c7a447f07c');
INSERT INTO public.grant_applicant(id, user_id) VALUES (2, '7373c0a2-3d08-4ec7-b454-1682dc16b036');


-- Create ORGANISATIONS / SCHEMES / FUNDING BODIES

INSERT INTO public.grant_applicant_organisation_profile(id, address_line1, address_line2, charity_commission_number, companies_house_number, county, legal_name, postcode, town, TYPE, applicant_id)
VALUES (1, '9 George Square', 'City Centre', '55667788', '11223344', 'Glasgow', 'AND Digital', 'G2 1QQ', 'GLASGOW', 'LIMITED_COMPANY', 2);

INSERT INTO public.grant_scheme(grant_scheme_id, created_by, funder_id, ggis_identifier, created_date, last_updated, last_updated_by, scheme_contact, scheme_name, VERSION)
VALUES (1, 1, 1, 'SCH-000003589','2022-08-02 20:10:20-00', '2022-08-02 20:10:20-00', 1, 'grantadmin@and.digital', 'AND Test Grant Scheme', 1);


--  Create APPLICATIONS

-- Draft
INSERT INTO public.grant_application(grant_application_id, created_by, application_name, created, definition, last_updated, last_update_by, status, VERSION, grant_scheme_id)
VALUES (1, 1, 'AND Test Grant Application', '2022-08-02 20:10:20-00', '{}', '2022-08-02 20:10:20-00', 1, 'DRAFT', 1, 1);


-- Published
INSERT INTO public.grant_application(grant_application_id, created_by, application_name, created, definition, last_updated, last_update_by, status, VERSION, grant_scheme_id)
VALUES (2, 1, 'AND Test Grant Application', '2022-08-02 20:10:20-00', '{}', '2022-08-02 20:10:20-00', 1, 'PUBLISHED', 1, 1);

-- Published and for starting a submission
INSERT INTO grant_application VALUES (3, 1, 1, '2022-09-09 14:58:24.862874', 12345, '2022-09-09 14:58:24.862875', 'Woodland Partnership Application', 'PUBLISHED', '{"sections":[{"sectionId":"ELIGIBILITY","sectionTitle":"Eligibility","sectionStatus":"NOT_STARTED","questions":[{"questionId":"ELIGIBILITY","fieldTitle":"Eligibility Statement","displayText":"","questionSuffix":"Does your organisation meet the eligibility criteria?","responseType":"YesNo","validation":{"mandatory":true}}]},{"sectionId":"ESSENTIAL","sectionTitle":"Required checks","sectionStatus":"CANNOT_START_YET","questions":[{"questionId":"APPLICANT_ORG_NAME","profileField":"ORG_NAME","fieldTitle":"Enter the name of your organisation","hintText":"This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission","adminSummary":"organisation legal name","responseType":"ShortAnswer","validation":{"mandatory":true,"minLength":2,"maxLength":250}},{"questionId":"APPLICANT_TYPE","profileField":"ORG_TYPE","fieldTitle":"Choose your organisation type","hintText":"Choose the option that best describes your organisation","adminSummary":"organisation type (e.g. limited company)","responseType":"Dropdown","validation":{"mandatory":true},"options":["Limited company","Non-limited company","Registered charity","Unregistered charity","Other"]},{"questionId":"APPLICANT_ORG_ADDRESS","profileField":"ORG_ADDRESS","fieldTitle":"Enter your organisation''s address","adminSummary":"registered address","responseType":"AddressInput","validation":{"mandatory":true}},{"questionId":"APPLICANT_ORG_CHARITY_NUMBER","profileField":"ORG_CHARITY_NUMBER","fieldTitle":"Please supply the Charity Commission number for your organisation - if applicable","hintText":"Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.","adminSummary":"Charity Commission number (if applicable)","responseType":"ShortAnswer","validation":{"mandatory":false,"minLength":2,"maxLength":15,"validInput":"alphanumeric-nospace"}},{"questionId":"APPLICANT_ORG_COMPANIES_HOUSE","profileField":"ORG_COMPANIES_HOUSE","fieldTitle":"Please supply the Companies House number for your organisation - if applicable","hintText":"Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.","adminSummary":"Companies House number (if applicable)","responseType":"ShortAnswer","validation":{"mandatory":false,"minLength":2,"maxLength":8,"validInput":"alphanumeric-nospace"}},{"questionId":"APPLICANT_AMOUNT","fieldPrefix":"£","fieldTitle":"How much does your organisation require as a grant?","hintText":"Please enter whole pounds only","adminSummary":"amount of funding required","responseType":"Numeric","validation":{"mandatory":true,"greaterThanZero":true}},{"questionId":"BENEFITIARY_LOCATION","fieldTitle":"Where will this funding be spent?","hintText":"Select the location where the grant funding will be spent. You can choose more than one, if it is being spent in more than one location.\\n\\nSelect all that apply:","adminSummary":"where the funding will be spent","responseType":"MultipleSelection","validation":{"mandatory":true},"options":["North East England","North West England","South East England","South West England","Midlands","Scotland","Wales","Northern Ireland"]}]}]}', 1, NULL);

-- Create SUBMISSIONS

-- Fails submission on application being in draft
INSERT INTO public.grant_submission(id, application_name, created, last_updated, status, submitted_date, VERSION, applicant_id, application_id, created_by, last_updated_by, scheme_id, definition)
VALUES ('3a6cfe2d-bf58-440d-9e07-3579c7dcf205', 'Test Grant Application', '2022-08-02 20:10:20-00', '2022-08-02 20:10:20-00', 'IN_PROGRESS', NULL, 1, 2, 1, 1, 1, 1, '{
  "sections": [
    {
      "sectionId": "ELIGIBILITY",
      "sectionTitle": "Eligibility",
      "sectionStatus": "COMPLETED",
      "questions": [
        {
          "questionId": "ELIGIBILITY",
          "fieldTitle": "Eligibility Statement",
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
          "questionId": "CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": true,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          },
          "response": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi sed cumque ad harum natus repudiandae, iure eos, quae fugit iusto sit! Asperiores itaque, voluptatem nam doloribus sunt excepturi quidem temporibus facere cupiditate nostrum similique placeat aspernatur culpa quasi doloremque nemo iure. Praesentium aut quidem corporis repudiandae quas? Recusandae, distinctio accusamus?"
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
          "responseType": "YesNo",
          "validation": { "mandatory": true },
          "response": "Yes"
        },
        {
          "questionId": "APPLICANT_ORG_CHARITY_NUMBER",
          "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
          "fieldTitle": "What type is your company",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ],
          "multiResponse": ["Registered charity", "Unregistered charity"]
        },
        {
          "questionId": "CUSTOM_QUESTION_4",
          "fieldTitle": "Please provide the date of your last awarded grant",
          "responseType": "Date",
          "validation": { "mandatory": true },
          "multiResponse": ["10", "12", "2020"]
        }
      ]
    },
    {
      "sectionId": "CUSTOM_SECTION_1",
      "sectionTitle": "Project Information",
      "sectionStatus": "COMPLETED",
      "questions": [
        {
          "questionId": "CUSTOM_APPLICANT_TYPE",
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
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": true,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_NAME",
          "profileField": "ORG_NAME",
          "fieldTitle": "Enter the name of your organisation",
          "hintText": "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission",
          "responseType": "ShortAnswer",
          "validation": { "mandatory": true, "minLength": 5, "maxLength": 100 }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_ADDRESS",
          "profileField": "ORG_ADDRESS",
          "fieldTitle": "Enter your organisation''s address",
          "responseType": "AddressInput",
          "validation": { "mandatory": true }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_COMPANIES_HOUSE",
          "profileField": "ORG_COMPANIES_HOUSE",
          "fieldTitle": "Does your organisation have a Companies House number?",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "YesNo",
          "validation": { "mandatory": true }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_CHARITY_NUMBER",
          "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
          "fieldTitle": "What type is your company",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_4",
          "fieldTitle": "Please provide the date of your last awarded grant",
          "responseType": "Date",
          "validation": { "mandatory": true }
        }
      ]
    }
  ]
}
');

-- This one runs all the section tests
INSERT INTO public.grant_submission(id, application_name, created, last_updated, status, submitted_date, VERSION, applicant_id, application_id, created_by, last_updated_by, scheme_id, definition)
VALUES ('3a6cfe2d-bf58-440d-9e07-3579c7dcf206', 'Test Grant Application 2', '2022-08-02 20:10:20-00', '2022-08-02 20:10:20-00', 'IN_PROGRESS', NULL, 1, 2, 2, 1, 1, 1, '{
  "sections": [
    {
      "sectionId": "ELIGIBILITY",
      "sectionTitle": "Eligibility",
      "sectionStatus": "NOT_STARTED",
      "questions": [
        {
          "questionId": "ELIGIBILITY",
          "fieldTitle": "Eligibility Statement",
          "displayText": "Some admin supplied text describing what it means to be eligible to apply for this grant",
          "questionSuffix": "Does your organisation meet the eligibility criteria?",
          "responseType": "YesNo",
          "validation": { "mandatory": true },
          "response": ""
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
          "questionId": "CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": true,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          },
          "response": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi sed cumque ad harum natus repudiandae, iure eos, quae fugit iusto sit! Asperiores itaque, voluptatem nam doloribus sunt excepturi quidem temporibus facere cupiditate nostrum similique placeat aspernatur culpa quasi doloremque nemo iure. Praesentium aut quidem corporis repudiandae quas? Recusandae, distinctio accusamus?"
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
          "responseType": "YesNo",
          "validation": { "mandatory": true },
          "response": "Yes"
        },
        {
          "questionId": "APPLICANT_ORG_CHARITY_NUMBER",
          "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
          "fieldTitle": "What type is your company",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ],
          "multiResponse": ["Registered charity", "Unregistered charity"]
        },
        {
          "questionId": "CUSTOM_QUESTION_4",
          "fieldTitle": "Please provide the date of your last awarded grant",
          "responseType": "Date",
          "validation": { "mandatory": true },
          "multiResponse": ["10", "12", "2020"]
        }
      ]
    },
    {
      "sectionId": "CUSTOM_SECTION_1",
      "sectionTitle": "Project Information",
      "sectionStatus": "NOT_STARTED",
      "questions": [
        {
          "questionId": "CUSTOM_APPLICANT_TYPE",
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
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": true,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_NAME",
          "profileField": "ORG_NAME",
          "fieldTitle": "Enter the name of your organisation",
          "hintText": "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission",
          "responseType": "ShortAnswer",
          "validation": { "mandatory": true, "minLength": 5, "maxLength": 100 }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_ADDRESS",
          "profileField": "ORG_ADDRESS",
          "fieldTitle": "Enter your organisation''s address",
          "responseType": "AddressInput",
          "validation": { "mandatory": true }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_COMPANIES_HOUSE",
          "profileField": "ORG_COMPANIES_HOUSE",
          "fieldTitle": "Does your organisation have a Companies House number?",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "YesNo",
          "validation": { "mandatory": true }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_CHARITY_NUMBER",
          "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
          "fieldTitle": "What type is your company",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_4",
          "fieldTitle": "Please provide the date of your last awarded grant",
          "responseType": "Date",
          "validation": { "mandatory": true }
        }
      ]
    }
  ]
}
');

-- Fails submission on mandatory responses being blank
INSERT INTO public.grant_submission(id, application_name, created, last_updated, status, submitted_date, VERSION, applicant_id, application_id, created_by, last_updated_by, scheme_id, definition)
VALUES ('3a6cfe2d-bf58-440d-9e07-3579c7dcf208', 'Test Grant Application 4', '2022-08-02 20:10:20-00', '2022-08-02 20:10:20-00', 'IN_PROGRESS', NULL, 1, 2, 2, 1, 1, 1, '{
  "sections": [
    {
      "sectionId": "ELIGIBILITY",
      "sectionTitle": "Eligibility",
      "sectionStatus": "NOT_STARTED",
      "questions": [
        {
          "questionId": "ELIGIBILITY",
          "fieldTitle": "Eligibility Statement",
          "displayText": "Some admin supplied text describing what it means to be eligible to apply for this grant",
          "questionSuffix": "Does your organisation meet the eligibility criteria?",
          "responseType": "YesNo",
          "validation": { "mandatory": true },
          "response": ""
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
          "questionId": "CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": true,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          },
          "response": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi sed cumque ad harum natus repudiandae, iure eos, quae fugit iusto sit! Asperiores itaque, voluptatem nam doloribus sunt excepturi quidem temporibus facere cupiditate nostrum similique placeat aspernatur culpa quasi doloremque nemo iure. Praesentium aut quidem corporis repudiandae quas? Recusandae, distinctio accusamus?"
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
          "responseType": "YesNo",
          "validation": { "mandatory": true },
          "response": "Yes"
        },
        {
          "questionId": "APPLICANT_ORG_CHARITY_NUMBER",
          "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
          "fieldTitle": "What type is your company",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ],
          "multiResponse": ["Registered charity", "Unregistered charity"]
        },
        {
          "questionId": "CUSTOM_QUESTION_4",
          "fieldTitle": "Please provide the date of your last awarded grant",
          "responseType": "Date",
          "validation": { "mandatory": true },
          "multiResponse": ["10", "12", "2020"]
        }
      ]
    },
    {
      "sectionId": "CUSTOM_SECTION_1",
      "sectionTitle": "Project Information",
      "sectionStatus": "NOT_STARTED",
      "questions": [
        {
          "questionId": "CUSTOM_APPLICANT_TYPE",
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
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": true,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_NAME",
          "profileField": "ORG_NAME",
          "fieldTitle": "Enter the name of your organisation",
          "hintText": "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission",
          "responseType": "ShortAnswer",
          "validation": { "mandatory": true, "minLength": 5, "maxLength": 100 }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_ADDRESS",
          "profileField": "ORG_ADDRESS",
          "fieldTitle": "Enter your organisation''s address",
          "responseType": "AddressInput",
          "validation": { "mandatory": true }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_COMPANIES_HOUSE",
          "profileField": "ORG_COMPANIES_HOUSE",
          "fieldTitle": "Does your organisation have a Companies House number?",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "YesNo",
          "validation": { "mandatory": true }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_CHARITY_NUMBER",
          "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
          "fieldTitle": "What type is your company",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_4",
          "fieldTitle": "Please provide the date of your last awarded grant",
          "responseType": "Date",
          "validation": { "mandatory": true }
        }
      ]
    }
  ]
}
');

-- Should submit - All mando fields should have response
INSERT INTO public.grant_submission(id, application_name, created, last_updated, status, submitted_date, VERSION, applicant_id, application_id, created_by, last_updated_by, scheme_id, definition)
VALUES ('3a6cfe2d-bf58-440d-9e07-3579c7dcf207', 'Test Grant Application 3', '2022-08-02 20:10:20-00', '2022-08-02 20:10:20-00', 'IN_PROGRESS', NULL, 1, 2, 2, 1, 1, 1, '{
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
          "questionId": "BENEFITIARY_LOCATION",
          "profileField": "ORG_TYPE",
          "fieldTitle": "Beneficiary Location",
          "hintText": "Choose the options that best describes your organisation location",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "North East England",
            "North West England",
            "Scotland",
            "Wales"
          ],
          "multiResponse": ["Scotland", "Wales"]
        },
        {
          "questionId": "CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": true,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          },
          "response": "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Animi sed cumque ad harum natus repudiandae, iure eos, quae fugit iusto sit! Asperiores itaque, voluptatem nam doloribus sunt excepturi quidem temporibus facere cupiditate nostrum similique placeat aspernatur culpa quasi doloremque nemo iure. Praesentium aut quidem corporis repudiandae quas? Recusandae, distinctio accusamus?"
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
          "questionId": "APPLICANT_AMOUNT",
          "profileField": "ORG_AMOUNT",
          "fieldTitle": "Enter the money you would wish to receive",
          "hintText": "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission",
          "responseType": "ShortAnswer",
          "validation": { "mandatory": true, "minLength": 5, "maxLength": 100 },
          "response": "500"
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
          "responseType": "YesNo",
          "validation": { "mandatory": true },
          "response": "Yes"
        },
        {
          "questionId": "APPLICANT_ORG_CHARITY_NUMBER",
          "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
          "fieldTitle": "What type is your company",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ],
          "multiResponse": ["Registered charity", "Unregistered charity"]
        },
        {
          "questionId": "CUSTOM_QUESTION_4",
          "fieldTitle": "Please provide the date of your last awarded grant",
          "responseType": "Date",
          "validation": { "mandatory": true },
          "multiResponse": ["10", "12", "2020"]
        }
      ]
    },
    {
      "sectionId": "CUSTOM_SECTION_1",
      "sectionTitle": "Project Information",
      "sectionStatus": "NOT_STARTED",
      "questions": [
        {
          "questionId": "CUSTOM_APPLICANT_TYPE",
          "profileField": "ORG_TYPE",
          "fieldTitle": "Choose your organisation type",
          "hintText": "Choose the option that best describes your organisation",
          "responseType": "Dropdown",
          "validation": { "mandatory": false },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": false,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_NAME",
          "profileField": "ORG_NAME",
          "fieldTitle": "Enter the name of your organisation",
          "hintText": "This is the official name of your organisation. It could be the name that is registered with Companies House or the Charities Commission",
          "responseType": "ShortAnswer",
          "validation": { "mandatory": false, "minLength": 5, "maxLength": 100 }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_ADDRESS",
          "profileField": "ORG_ADDRESS",
          "fieldTitle": "Enter your organisation''s address",
          "responseType": "AddressInput",
          "validation": { "mandatory": false }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_COMPANIES_HOUSE",
          "profileField": "ORG_COMPANIES_HOUSE",
          "fieldTitle": "Does your organisation have a Companies House number?",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "YesNo",
          "validation": { "mandatory": false }
        },
        {
          "questionId": "CUSTOM_APPLICANT_ORG_CHARITY_NUMBER",
          "profileField": "ORG_CHARITY_COMMISSION_NUMBER",
          "fieldTitle": "What type is your company",
          "hintText": "Funding organisation might use this to identify your organisation when you apply for a grant. It might also be used to check your organisation is legitimate.",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": false },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_4",
          "fieldTitle": "Please provide the date of your last awarded grant",
          "responseType": "Date",
          "validation": { "mandatory": false }
        }
      ]
    }
  ]
}
');


-- Should show section scenarios
INSERT INTO public.grant_submission(id, application_name, created, last_updated, status, submitted_date, VERSION, applicant_id, application_id, created_by, last_updated_by, scheme_id, definition)
VALUES ('3a6cfe2d-bf58-440d-9e07-3579c7dcf209', 'Test Grant Application 5', '2022-08-02 20:10:20-00', '2022-08-02 20:10:20-00', 'IN_PROGRESS', NULL, 1, 2, 2, 1, 1, 1, '{
  "sections": [
    {
      "sectionId": "ELIGIBILITY",
      "sectionTitle": "Eligibility",
      "sectionStatus": "NOT_STARTED",
      "questions": [
        {
          "questionId": "ELIGIBILITY",
          "fieldTitle": "Eligibility Statement",
          "displayText": "Some admin supplied text describing what it means to be eligible to apply for this grant",
          "questionSuffix": "Does your organisation meet the eligibility criteria?",
          "responseType": "YesNo",
          "validation": { "mandatory": true },
          "response": null
        }
      ]
    },
    {
      "sectionId": "ESSENTIAL",
      "sectionTitle": "Essential Information",
      "sectionStatus": "CANNOT_START_YET",
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
          "response": null
        },
        {
          "questionId": "BENEFITIARY_LOCATION",
          "profileField": "ORG_TYPE",
          "fieldTitle": "Beneficiary Location",
          "hintText": "Choose the options that best describes your organisation location",
          "responseType": "MultipleSelection",
          "validation": { "mandatory": true },
          "options": [
            "North East England",
            "North West England",
            "Scotland",
            "Wales"
          ],
          "multiResponse": null
        }
      ]
    },
    {
      "sectionId": "CUSTOM_SECTION_1",
      "sectionTitle": "Project Information",
      "sectionStatus": "CANNOT_START_YET",
      "questions": [
        {
          "questionId": "CUSTOM_APPLICANT_TYPE",
          "profileField": "ORG_TYPE",
          "fieldTitle": "Choose your organisation type",
          "hintText": "Choose the option that best describes your organisation",
          "responseType": "Dropdown",
          "validation": { "mandatory": false },
          "options": [
            "Limited company",
            "Non-limited company",
            "Registered charity",
            "Unregistered charity",
            "Other"
          ]
        },
        {
          "questionId": "CUSTOM_CUSTOM_QUESTION_1",
          "fieldTitle": "Description of the project, please include information regarding public accessibility (see GOV.UK guidance for a definition of public access) to the newly planted trees",
          "responseType": "LongAnswer",
          "validation": {
            "mandatory": false,
            "minLength": 100,
            "maxLength": 2000,
            "minWords": 20,
            "maxWords": 500
          }
        }
      ]
    }
  ]
}
');



