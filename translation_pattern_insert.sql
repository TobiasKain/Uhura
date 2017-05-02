INSERT INTO TranslationPattern(nlSentence, regex, translation) VALUES ('A and B are C.','.* and .* are .* \.$','A is a C.
B is a C.');
INSERT INTO TranslationPattern(nlSentence, regex, translation) VALUES ('Just A are B.','Just .* are .* \.$','All A are B.');
INSERT INTO TranslationPattern(nlSentence, regex, translation) VALUES ('All A are not B.','All .* are not .* \.$','No A are B.');
