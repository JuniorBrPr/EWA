package teamx.app.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import teamx.app.backend.controllers.EmailController;
import teamx.app.backend.models.Project;
import teamx.app.backend.services.EmailService;
import teamx.app.backend.services.ProjectService;
import teamx.app.backend.services.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailControllerTests {

    private MockMvc mockMvc;

    @Mock
    private EmailService emailService;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
    }


    @Test
    void sendProjectEmail_WithInProgressProjects() throws Exception {
        // Creating sample projects
        Project project1 = new Project();
        project1.setName("Energy Infrastructure");
        project1.setStatus(Project.Status.IN_PROGRESS);
        project1.setStartDate(java.sql.Date.valueOf("2000-01-01"));
        project1.setEndDate(java.sql.Date.valueOf("2000-02-01"));


        List<Project> projects = Arrays.asList(project1); // Add the created sample project(s) here

        // Mocking the scenario with projects in progress
        when(projectService.findProjectsByStatusAndDateBetween(
                Project.Status.IN_PROGRESS, null, java.sql.Date.valueOf("1970-01-01"), java.sql.Date.valueOf(LocalDate.now().plusWeeks(1))))
                .thenReturn(projects);

        // Perform the POST request to send project reminder emails
        mockMvc.perform(post("/mail/sendProjectEmail"))
                .andExpect(status().isOk());
    }


    @Test
    void sendProjectEmail_NoInProgressProjects() throws Exception {
        // Mocking the scenario with no projects in progress
        List<Project> projects = new ArrayList<>();
        when(projectService.findProjectsByStatusAndDateBetween(
                Project.Status.IN_PROGRESS, null, java.sql.Date.valueOf("1970-01-01"), java.sql.Date.valueOf(LocalDate.now().plusWeeks(1))))
                .thenReturn(projects);

        // Perform the POST request to send project reminder emails
        mockMvc.perform(post("/mail/sendProjectEmail"))
                .andExpect(status().isOk())
                .andExpect(content().string("No projects are still in progress."));
    }
}
