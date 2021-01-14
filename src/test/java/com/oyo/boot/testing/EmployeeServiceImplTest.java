package com.oyo.boot.testing;

import com.oyo.factory.EmployeeFactory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Spy
    private RestTemplate restTemplate = new RestTemplate();
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockServer = MockRestServiceServer.createServer(restTemplate);
        Employee john = EmployeeFactory.make("John Doe");
        Mockito.when(employeeRepository.findByName("John Doe")).thenReturn(john);
        Mockito.when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(john));
    }

    @Test
    void testEmployeeByNameWhenUserNotPresentReturnNull() {
        Mockito.when(employeeRepository.findByName("foo")).thenReturn(null);
        Employee employee = employeeService.getEmployeeByName("foo");
        Assertions.assertNull(employee);
    }

    @Test
    void testGeEmployeeByNameWhenUserIsPresent() {
        Employee employee = employeeService.getEmployeeByName("John Doe");
        Assertions.assertNotNull(employee);
        Assertions.assertEquals("John Doe", employee.getName());
    }

    @Test
    void testGetEmployeeByIdWhenIdIsNotPresentReturnsNull() {
        Mockito.when(employeeRepository.findById(2L)).thenReturn(Optional.empty());
        Employee employee = employeeService.getEmployeeById(2L);
        Assertions.assertNull(employee);
    }

    @Test
    void testGetEmployeeByIdWhenIdIsPresent() {
        Employee employee = employeeService.getEmployeeById(1L);
        Assertions.assertNotNull(employee);
        Assertions.assertEquals("John Doe", employee.getName());
    }

    @ParameterizedTest
    @CsvSource({"true,John Doe", "false,Foo Bar"})
    void exists(boolean expected, String input) {
        boolean actual = employeeService.exists(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testSave() {
        Mockito.when(employeeRepository.save(ArgumentMatchers.any(Employee.class))).thenAnswer(
                invocationOnMock -> {
                    Employee employee = (Employee) invocationOnMock.getArgument(0);
                    employee.setId(10L);
                    return employee;
                }
        );
        Employee save = employeeService.save(EmployeeFactory.make());
        Assertions.assertEquals(10L, save.getId(), "Id is generated");
        Mockito.verify(employeeRepository, Mockito.times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void testGetAllEmployees() {
        Mockito.when(employeeRepository.findAll()).thenReturn(Arrays.asList(EmployeeFactory.make("a", 1L)
                , EmployeeFactory.make("b", 2L)));
        List<Employee> allEmployees = employeeService.getAllEmployees();
        Assertions.assertEquals(2, allEmployees.size());
    }

    @Test
    void testGetProjectsForEmployee() {
        Matcher<String> matcher = Matchers.equalTo("http://demo0167306.mockable.io/project");
        mockServer
                .expect(ExpectedCount.times(1), requestTo(matcher))
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        MockRestResponseCreators.withSuccess(getResponseForProject(), MediaType.APPLICATION_JSON));
        List<String> projectForEmployee = employeeService.getProjectForEmployee(1);
        Assertions.assertEquals(2, projectForEmployee.size());
    }

    String getResponseForProject() {
        return "{\n" +
                " \"projects\":[ \n" +
                "     \"Hack World\",\n" +
                "     \"Down Under\"\n" +
                " ]\n" +
                "}";
    }
}