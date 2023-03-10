package org.hbrs.se2.project.entities;

import javax.persistence.*;

@Entity
@Table(name="job", schema = "mid9db")
public class Job {
    private int jobid;
    private int companyid;
    private String title;
    private String description;
    private Integer salary;
    private String location;
    private String contactdetails;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jobid")
    public int getJobid() {return jobid;}

    public void setJobid(int id) {this.jobid = id;}

    @Basic
    @Column(name = "companyid")
    public int getCompanyid() {return companyid;}

    public void setCompanyid(int id) {this.companyid = id;}

    @Basic
    @Column(name = "title")
    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    @Basic
    @Column(name = "description")
    public String getDescription() {return description;}

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "salary")
    public Integer getSalary() {return salary;}

    public void setSalary(Integer salary) {this.salary = salary;}
    @Basic
    @Column(name = "location")
    public String getLocation(){return location;}
    public void setLocation(String location){this.location = location;}

    @Basic
    @Column(name = "contactdetails")
    public String getContactdetails(){return contactdetails;}
    public void setContactdetails(String contactdetails){this.contactdetails = contactdetails;}
}

