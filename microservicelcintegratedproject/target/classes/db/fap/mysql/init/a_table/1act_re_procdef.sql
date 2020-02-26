
CREATE TABLE lc_testpro (
  ID_ varchar(64) COLLATE utf8_bin NOT NULL,
  REV_ decimal(8,0) DEFAULT NULL,
  CATEGORY_ varchar(255) COLLATE utf8_bin DEFAULT NULL,
  NAME_ varchar(255) COLLATE utf8_bin DEFAULT NULL,
  KEY_ varchar(255) COLLATE utf8_bin NOT NULL,
  VERSION_ decimal(8,0) NOT NULL,
  DEPLOYMENT_ID_ varchar(64) COLLATE utf8_bin DEFAULT NULL,
  RESOURCE_NAME_ varchar(2000) COLLATE utf8_bin DEFAULT NULL,
  DGRM_RESOURCE_NAME_ varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  DESCRIPTION_ varchar(2000) COLLATE utf8_bin DEFAULT NULL,
  HAS_START_FORM_KEY_ decimal(1,0) DEFAULT NULL,
  SUSPENSION_STATE_ decimal(8,0) DEFAULT NULL,
  PRIMARY KEY (ID_),
  KEY ACT_UNIQ_PROCDEF (KEY_,VERSION_) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;