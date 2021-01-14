package com.oyo.boot.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public Employee getEmployeeByName(String name) {
        return employeeRepository.findByName(name);
    }

    @Override
    public boolean exists(String name) {
        if (employeeRepository.findByName(name) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public List<String> getProjectForEmployee(Integer id) {
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity("http://demo0167306.mockable.io/project",
                Map.class);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode.equals(HttpStatus.OK)) {
            Map<String, List<String>> projects = (Map<String, List<String>>) responseEntity.getBody();
            return projects.get("projects");
        } else {
            return null;
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
