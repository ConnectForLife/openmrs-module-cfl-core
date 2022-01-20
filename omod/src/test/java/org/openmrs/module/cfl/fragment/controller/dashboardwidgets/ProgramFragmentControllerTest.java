package org.openmrs.module.cfl.fragment.controller.dashboardwidgets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.api.program.PatientProgramDetails;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class ProgramFragmentControllerTest {

  private static final String PROGRAM_MAP_LIST_ATTR_NAME = "programMapList";

  private static final String PATIENT_PROGRAMS_LIST_ATTR_NAME = "patientProgramsDetailsList";

  private final ProgramFragmentController controller = new ProgramFragmentController();

  @Mock private Patient patient;

  @Mock private FragmentModel model;

  @Mock private FragmentConfiguration configuration;

  @Mock private ProgramWorkflowService programService;

  @Before
  public void setUp() {
    mockStatic(Context.class);
    when(Context.getProgramWorkflowService()).thenReturn(programService);
  }

  @Test
  public void shouldProperlyAddAttributesToModel() {
    // when
    when(configuration.get(PROGRAM_MAP_LIST_ATTR_NAME)).thenReturn(new ArrayList<>());

    // given
    controller.controller(model, configuration, patient);

    // then
    verify(programService).getAllPrograms(false);
    verify(model)
        .addAttribute(
            eq(PATIENT_PROGRAMS_LIST_ATTR_NAME), anyCollectionOf(PatientProgramDetails.class));
  }
}
