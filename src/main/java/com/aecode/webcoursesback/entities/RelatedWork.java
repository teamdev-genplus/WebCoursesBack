package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relatedworks")
@SequenceGenerator(name = "work_seq", sequenceName = "work_sequence", allocationSize = 1)

public class RelatedWork {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_seq")
    private int workId;

    @OneToOne
    @JoinColumn(name = "module_id", referencedColumnName = "moduleId",nullable = false)
    private Module module;

    @Column(nullable = false, length = 255)
    private String formUrl;

    @Column(nullable = false, length = 255)
    private String title;

    @OneToMany(mappedBy = "rw", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgressRW> userprogressrw = new ArrayList<>();

    public RelatedWork() {
    }

    public RelatedWork(int workId, Module module, String formUrl, String title, List<UserProgressRW> userprogressrw) {
        this.workId = workId;
        this.module = module;
        this.formUrl = formUrl;
        this.title = title;
        this.userprogressrw = userprogressrw;
    }

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<UserProgressRW> getUserprogressrw() {
        return userprogressrw;
    }

    public void setUserprogressrw(List<UserProgressRW> userprogressrw) {
        this.userprogressrw = userprogressrw;
    }

}
