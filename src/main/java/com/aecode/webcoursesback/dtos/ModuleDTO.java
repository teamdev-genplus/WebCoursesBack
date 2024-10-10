package com.aecode.webcoursesback.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public class ModuleDTO {
    private int moduleId;
    private int courseId;
    private String title;
    private int orderNumber;
    private Set<ClassDTO> classes;
    private TestDTO test;

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Set<ClassDTO> getClasses() {
        return classes;
    }

    public void setClasses(Set<ClassDTO> classes) {
        this.classes = classes;
    }

    public TestDTO getTest() {
        return test;
    }

    public void setTest(TestDTO test) {
        this.test = test;
    }
}

