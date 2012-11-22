package org.openmrs.module.mirebalais.integration;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.paperrecord.PaperRecordService;
import org.openmrs.module.emr.paperrecord.PaperRecordServiceImpl;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.NotTransactional;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.intThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SkipBaseSetup
public class PaperRecordServiceIT extends BaseModuleContextSensitiveTest {

    @Autowired
    private IdentifierSourceService identifierSourceService;

    private PatientService patientService;

    private AdministrationService administrationService;

    private PaperRecordService paperRecordService;

    @Before
    public void setUp(){
        paperRecordService = new PaperRecordServiceImpl();
        patientService = mock(PatientService.class);
        administrationService = mock(AdministrationService.class);
        ((PaperRecordServiceImpl) paperRecordService).setAdministrationService(administrationService);
        ((PaperRecordServiceImpl) paperRecordService).setIdentifierSourceService(identifierSourceService);
        ((PaperRecordServiceImpl) paperRecordService).setPatientService(patientService);
    }

    @Override
    public Boolean useInMemoryDatabase() {
        return false;
    }

    @Override
    public String getWebappName() {
        return "mirebalais";
    }


    @Test
    @DirtiesContext
    @NotTransactional
    public void shouldCreateTwoDifferentDossierNumbers() throws Exception {
        authenticate();
        when(administrationService.getGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE)).thenReturn("e66645eb-03a8-4991-b4ce-e87318e37566");

        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
        patientIdentifierType.setPatientIdentifierTypeId(3);
        when(patientService.getPatientIdentifierTypeByUuid("e66645eb-03a8-4991-b4ce-e87318e37566")).thenReturn(patientIdentifierType);

        String paperMedicalRecordNumber = paperRecordService.createPaperMedicalRecordNumberFor(new Patient(), new Location(15));
        assertTrue(paperMedicalRecordNumber.matches("A\\d{6}"));
        assertThat(paperRecordService.createPaperMedicalRecordNumberFor(new Patient(), new Location(15)), Matchers.not(eq(paperMedicalRecordNumber)));
     }

}
