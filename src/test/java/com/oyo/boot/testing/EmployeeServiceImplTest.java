package com.oyo.boot.testing;

import com.oyo.factory.EmployeeFactory;
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

import java.util.Arrays;
import java.util.List;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
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
        Mockito.when(employeeRepository.findById(2L)).thenReturn(null);
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

}