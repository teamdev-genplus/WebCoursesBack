package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "relatedworks")
public class RelatedWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int workId;

    @OneToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false, length = 255)
    private String formUrl;

    @Column(nullable = false, length = 255)
    private String title;

    public RelatedWork() {
    }

    public RelatedWork(int workId, Module module, String formUrl, String title) {
        this.workId = workId;
        this.module = module;
        this.formUrl = formUrl;
        this.title = title;
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
}
