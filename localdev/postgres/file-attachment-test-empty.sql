PGDMP         %                z        
   gapapply13    14.2 (Debian 14.2-1.pgdg110+1)    14.4     t           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            u           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            v           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            w           1262    18669 
   gapapply13    DATABASE     ^   CREATE DATABASE gapapply13 WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.utf8';
    DROP DATABASE gapapply13;
                postgres    false            j          0    18768    diligence_check 
   TABLE DATA           �   COPY public.diligence_check (id, address_county, address_postcode, address_street, address_town, application_amount, application_number, charity_number, check_type, companies_house_number, created, organisation_name, submission_id) FROM stdin;
    public          postgres    false    224   �       [          0    18670    flyway_schema_history 
   TABLE DATA           �   COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
    public          postgres    false    209   �       e          0    18731    gap_user 
   TABLE DATA           9   COPY public.gap_user (gap_user_id, user_sub) FROM stdin;
    public          postgres    false    219   �!       g          0    18737    grant_funding_organisation 
   TABLE DATA           R   COPY public.grant_funding_organisation (funder_id, organisation_name) FROM stdin;
    public          postgres    false    221   �!       i          0    18743    grant_admin 
   TABLE DATA           I   COPY public.grant_admin (grant_admin_id, funder_id, user_id) FROM stdin;
    public          postgres    false    223   ("       l          0    18776    grant_applicant 
   TABLE DATA           6   COPY public.grant_applicant (id, user_id) FROM stdin;
    public          postgres    false    226   K"       n          0    18783 $   grant_applicant_organisation_profile 
   TABLE DATA           �   COPY public.grant_applicant_organisation_profile (id, address_line1, address_line2, charity_commission_number, companies_house_number, county, legal_name, postcode, town, type, applicant_id) FROM stdin;
    public          postgres    false    228   �"       a          0    18708    grant_scheme 
   TABLE DATA           �   COPY public.grant_scheme (grant_scheme_id, funder_id, version, ggis_identifier, created_date, last_updated, last_updated_by, scheme_name, scheme_contact, created_by) FROM stdin;
    public          postgres    false    215   �"       c          0    18717    grant_application 
   TABLE DATA           �   COPY public.grant_application (grant_application_id, grant_scheme_id, version, created, last_update_by, last_updated, application_name, status, definition, created_by, last_published) FROM stdin;
    public          postgres    false    217   #       o          0    18801    grant_submission 
   TABLE DATA           �   COPY public.grant_submission (id, application_name, created, definition, last_updated, status, submitted_date, version, applicant_id, application_id, created_by, last_updated_by, scheme_id, gap_id) FROM stdin;
    public          postgres    false    229   t(       q          0    18848    grant_attachment 
   TABLE DATA           �   COPY public.grant_attachment (grant_attachment_id, created, filename, last_updated, location, question_id, status, version, created_by, submission_id) FROM stdin;
    public          postgres    false    231   \.       p          0    18838    grant_beneficiary 
   TABLE DATA           C  COPY public.grant_beneficiary (grant_beneficiary_id, scheme_id, application_id, submission_id, version, created, created_by, last_updated, last_updated_by, location_ne_eng, location_nw_eng, location_se_eng, location_sw_eng, location_mid_eng, location_sco, location_wal, location_nir, has_provided_additional_answers, supports_specific_gender, supporting_gender_details, age_group1, age_group2, age_group3, age_group4, age_group5, age_group_all, ethnic_group1, ethnic_group2, ethnic_group3, ethnic_group4, ethnic_group5, ethnic_group_other, ethnic_other_details, ethnic_group_all, supporting_disabilities, sexual_orientation_group1, sexual_orientation_group2, sexual_orientation_group3, sexual_orientation_other, sexual_orientation_other_details, sexual_orientation_group_all, sex_group1, sex_group2, sex_group_all, gap_id) FROM stdin;
    public          postgres    false    230   y.       \          0    18679    spring_session 
   TABLE DATA           �   COPY public.spring_session (primary_id, session_id, creation_time, last_access_time, max_inactive_interval, expiry_time, principal_name) FROM stdin;
    public          postgres    false    210   �.       ]          0    18687    spring_session_attributes 
   TABLE DATA           h   COPY public.spring_session_attributes (session_primary_id, attribute_name, attribute_bytes) FROM stdin;
    public          postgres    false    211   t/       _          0    18700    template_grant_application 
   TABLE DATA           M   COPY public.template_grant_application (template_id, definition) FROM stdin;
    public          postgres    false    213   �2       x           0    0    gap_user_gap_user_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.gap_user_gap_user_id_seq', 1, true);
          public          postgres    false    218            y           0    0    grant_admin_grant_admin_id_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('public.grant_admin_grant_admin_id_seq', 1, true);
          public          postgres    false    222            z           0    0    grant_applicant_id_seq    SEQUENCE SET     D   SELECT pg_catalog.setval('public.grant_applicant_id_seq', 1, true);
          public          postgres    false    225            {           0    0 +   grant_applicant_organisation_profile_id_seq    SEQUENCE SET     Y   SELECT pg_catalog.setval('public.grant_applicant_organisation_profile_id_seq', 1, true);
          public          postgres    false    227            |           0    0 *   grant_application_grant_application_id_seq    SEQUENCE SET     X   SELECT pg_catalog.setval('public.grant_application_grant_application_id_seq', 1, true);
          public          postgres    false    216            }           0    0 (   grant_funding_organisation_funder_id_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('public.grant_funding_organisation_funder_id_seq', 1, true);
          public          postgres    false    220            ~           0    0     grant_scheme_grant_scheme_id_seq    SEQUENCE SET     N   SELECT pg_catalog.setval('public.grant_scheme_grant_scheme_id_seq', 1, true);
          public          postgres    false    214                       0    0 *   template_grant_application_template_id_seq    SEQUENCE SET     Y   SELECT pg_catalog.setval('public.template_grant_application_template_id_seq', 1, false);
          public          postgres    false    212            j      x������ � �      [   �  x��Vێ�0}v��H����}�*�����Z"@���~}�6��f��R4��̙C��	H=�>��<N�?Ws;�a�ɷ�_�wp�\��RwK�4��+���(2s<O�Le�H�A�"�U'�"	$r���7��Wѿv����6�\u���� ����	���	9r���b�Ǳ��x�*���C���;WP�ʭ�,�+͹�;�Jr�D!�@vA�c�����\g	s���\8W��[�� K��K�+�p��(B�� ��1�n��ꐏ:6�(KQ�Y&�;J�!8��D��y%�J�0��8��R,ha�`4*ѫ��\����8�wvԛ���+APθ���ps�`�n���4i�2k�F���C���M�q� ��tqp+�C�5�����*�[`Q��D�n���$�|�s ;�b h��Aٸ>��zK�r��3!-g{sG�!5$j S{��Ȼ�^BN���9N>�qS�	�"��n�"D(n���_�(��([	)
�8O�ޯm��u��ߪx~S���1�fً%��\Xn��
��$��x,�x�U�U���B�w·u����>���RM��;���t y.)+�1,/�.�w�mw��A`>��dւJtʎ�p��ASh�cjf/����$}���	��er�\�S��0���t�mR�ⱄ�ُUh6~�e\hV�M)�{4 T���4R@�Ť>F_����n���,N�˶.2$�]�����i����p�fF��      e   4   x�3�LN�L5OIM�MKJM�5�44�M225�MM162�0��052����� �
�      g      x�3�tNL��K-Q�OK�LN����� L�      i      x�3�4�4����� �X      l   4   x�3�LN�L5OIM�MKJM�5�44�M225�MM162�0��052����� �
�      n      x�3��É�b���� ��i      a   V   x�3�4AC##ccN###]K]#CCc+#K+c=KSsss3�?r��IUp,)IL��M�+QI-.Qp/J�+��4����� �f      c   K  x��W�n�8�n�����J����@���I5��U&q��������O�o�O���B�2�+��R5�����}��`����j�Z�f�޲�}�o����F�sa_u���E���e9J� NW�'R!'M������Ǯw;�}�$	������_���[�����vǮo�v3>U��IFWtIU��IOa��#k0���#S�r�|��V�AD	+�#��s�QHe���'�
�o�b/�"��CA$ڈ<C"[aN�94JQH��΃�*�Q�eD�(�7���ȩ���0��qa��[	��"���WYN���>Վp���w�q���ӌ�(�I����9���8S�a���0u&�>�D��h����!�lp�8!HDe�`IL�ڢ��T"��% (f��m W�@�,DK򼍊��N2�� �T�h ��a�"��mc F�AB�,��aB��$��^FV۸�Tz�Ȕ��d��Y�Ř𕊭~������|�d�B�?�����X8oY�J�|@�����6ͥ��H��J�'����w��j F�P�!c�s�a&�P���t�YE�Z����/,�
^g��ų(C���Y��`Ȭ�o�w���a5ϓ�Q��'�p���m��s�	���:]M�>���Y@�|��M�G��ӕl9g��d���E�m^$�y�("Q�#!\\&Kv$՛�����
/]�
�Fº�DC(�4�T�_Ǆ�a���Ā�*�\��R��L
]^�g���rZ�v�� �$�+�Q�����;8dEF�`I� Էj���..�ѷ0KcA��Թ�)�uRpk8Sw�=���W�P��2�j��G���4����p&����#x���/��c�o�%y���eʊ~a�C��!�[�S:ױ��3@�D��MY���ҍC���v����8��{��1���$�����tt�Bc�����Φǀ|�˅@g�X!�]� *����= 3��D�WjV\����t私�A��Qs�'�,��IM�5�Ғ�f���l�g�|�yl#�� �$�t闉x��2�2��)�2RlP���Bf�h��e�S�����`�yٲ<���P���\/j;�3"w�IƑ�3��ܪ7/�n^E�^�Woc{Y�]v[��	zA�m�nX���ϫ��	"o�����β���ʮ��˰�lv�:��Y��kw�.���t�o�@d$Ъ�A�sJ7��)�H�<A��nzuö�t՚\7�ØXC��%����T��5��TC�j̇7�T�oF
,~��Ə�܃x&�V���]1h�F(�+8�{�fo蛧�Պ>��⩧?�s`�I[��|����U�sٶϿ4������D      o   �  x��mo�6�?'��Зu�e؎���R(���e�R��I�e�HU/s�"�f�d�lG���mӭ��F̗�����9i��^���k���7[_o��5���q�:'�98�� #M�����!I��(b��)��Qk4�ZGo�Q��=jv������I�s�YK�+%Z�f���������Ss`:�Z�ǡ)#����RF�ź�Nq�� �9��cL�~z?f��j	�RT��3��#J֍b����)a���R�	�
)&z4�^8�S
�[jP����ڙ��O0�'H�"���g��D���("��1MIL�[m��8&JJ.{!�a���c[L��@�E$U�&�%`��QO��u?k!��E�Yi���r����QL�?�4P~%b/���}0L�� �к>f	Y.l�(K7fJ�l��j��11'�Ty)�hy���ZI�XJ'[�w��l�o9�1(�5!3�qK^�0c<�g��܏&��-c�/��m�l������C��_�e�\�5{���)E y.��iqUd���Д�WN�J!1��tk�i�3F08�Y"sm���f�$������ږ{02[n@+0n"N��I�U�7�0o�j����W��\��@oٳ��Y h1<����A������i����,<�@4��ž�bD�ސꬊ!r�s+R����"�Ĝ���x��w
۱�Y�u��aW��l��qY�N0��~�<�כ@d.�r�Y��� �y�f����m�i��QFޑ�竦�G�"y�g�Ү{�rxڟ�3P��Q&� �\l\<��k�,�0�(������=��3�Q>�I��,HQ�❼h�������Y��Pja4�1O�՛˂�������2�a�\�pӆp��O��͖�J��w��'/�z�e8�#��BȖ]��$®<ן���plXf߾�]�O\��Aϱ�����>��ퟢ��߀��.-g��B�Q���ʋ�8/�N�y�,�KW!*q��	�9A���~�h����!�Ie�/�c��W���_Ir�?[6pڷ��&�������p̑�"W��(?��Σ�!l$��0��U�d�]��8/XU+�j�G�Ut���*pB�&A��"c*Uu�ȹj�|{�j��-���c�V{�BI���wĵ�vJ7R�s(O1�&y-���(~�4@}g��(�*"�xE�m�G�;#�ԓ�ɔ�vE�l�$��Ȋյg�Tx���m�;��N㸣7q}�w���uB܎�k��߱���e����:�}˚ܚ�]��O�z�=��i�]�I�� SRov��}\x;y�P�F��sT(��1q��`ו!�ٚNs�t`��Y��DJS�K��C6��Ud�� �B�Lr�/��Fg������Pu�;�v�+}}���E�O����Z��n40(-�$�z����yn��;���̱v��/�w_��iݏ'�w�e���:�o��Z�����ÿh|�P      q      x������ � �      p      x������ � �      \   �   x�5��n�0�kx
�D���6RN��I��i���V�����7��ځ�Jx�^	F8$%�F�Q���K�	�ƛY`?�P������%��w ҍ}5��E��$��q����=�,Sy�{�d�̿)R�4�|�F�9�͊�}���m��XΦg����s�9P�І��b����gI�o�by��k���u�&�P�      ]     x��V�N1}&�B��x�~�P���J���vm�7��{fֻ	�J�DJ�z||Ι�3Řyn�z��|M��:O=\{�Z	=�t���������������?�?�{���x���k��9�D	��Ƌ���÷��=7N"�>�:'��4FHpG[K�7�B��B��W��b�
7q\��_���I�w��M����
N��i8�����/o�/;�.�g�u�O���O΀WIw ��H�a5�"�%���>��Y/�y9��2��+��W'�1{�R���Q�x�:(ΆRv���ax�DO&�����U��!���<iq�B����u�$�	������l[_V��H�3>�̢
Wx�5^�2׶
24Q���k$PN}�Za�wԾ��s���O�O�gl9nEy.�-!E?�^sǁ��"1�ƽ�f�X(��!����B�Ϯ��"��0�\o^�}<��K��������[�*�Ksѐ�9�U<�<ںƋ8ڿ�]>d:��%]ńS����Qo�/�I�u4]��v�q3MuN)�da
Q�B���`�9!|�Ti.KY.̖�h:�8Ջ�7��~]��q/��H���#N��ykE�\�|n�k-`�^6�Z9m�#ۭ�l��zY����4�꫈I�Jg/n���;��F�������Đ�I�f'U���0�n�1;u{�do�܁`1��{T��p��N+����I���{���.��.ڕ��J�&7�Yb�W��r�
��:4K!�TK���(4�gCs/:ps�c�m�D'�I����
<<}:��=!V      _   g  x��X�n�6��<�/� �X�eP�`ˆ� Ȗ"���E�"U��#y��ɞlGʉmY��4�0`A+���!�ǻ��~��V��&��'���|�>�� Aa���`D7�~1���8[��̔�������zA8���#?�wP�
еX�����pK<i
��( aj	�9�e�ƪԤ�~��r��-�\�&�,�jE���I`�I����X1��_jV�Q��8o@�����r�8ۈ��fT�L�eF�+~������zx��z>��(�F-�����)HH�B���'��M��`���d��.��~m�r%��ÅͽU8�qC`j\�̀��aRk�)�&Q�4�_�/�1����	�e����O�Rc�(X1��ம�I�Pf9�"�.e��d�0��'���0D�d��e�Q��GQ�M�t��RO�5�o!e�.gbbeR��t ��[�������n�ϡKl�.�TJL���7vӎe��x�W
6���$��B,������F@��`5 �eXq�p:�?t$�\�<�뺷���};Ղk'�IF�q��l�8�������dB���M07�����β�;?�a1}M��"�kB�S�;Ȳ��
uZx*yQҕ^���Ko���.����glm���O9P<���s^���r��Qd��R6䂜�$�YL�� \"abU��*5�p��vIX��
[���)���+]@�������Z�f�6kϵ�c����ǣA���ʛ��w՝Wh0K\��;��wu�s\1�<O).���TH��z�Kg��0��w����sZT�F'�k3��l�ْ���7��'Wa�J�K�&Y�$i�8��BA�~�{���˷����u*9�\"5���	�B��З�n�'M+�*�\����A������W
��*���
J�NG=�C�"����n4zQ0	[�w��ॄ��(?�E�C{����F����aW�0�2�d�yx4: 7Xb*��9�N	_H}[���7-��:��>�������������*���lCn����s�\���b}J�O��G�Ӷ�\Cd.�.+��ʘ%��>ԍ�iԸ��.`P�
�ڱ������燓�����_���!     