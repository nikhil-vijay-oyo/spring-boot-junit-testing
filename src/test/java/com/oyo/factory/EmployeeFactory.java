package com.oyo.factory;


import com.oyo.boot.testing.Employee;

import java.time.LocalDate;

public class EmployeeFactory {

    public static Employee make() {
        return make("John Doe", 1L);
    }

    public static Employee make(String name) {
        return make(name, 1L);
    }

    public static Employee make(String name, Long id) {
        return Employee.builder()
                .name(name)
                .id(id)
                .birthday(LocalDate.of(1970, 1, 1))
                .build();
    }


}
