package org.hbrs.se2.project.entities;

import javax.persistence.*;

@Entity
@Table(name="company", schema = "mid9db")
public class Company {

    private int companyid;
    private int userid;
    private String name;
    private String industry;
    private boolean banned;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "companyid")
    public int getCompanyid() {return companyid;}

    public void setCompanyid(int id) {this.companyid = id;}

    @Basic
    @Column(name="userid")
    public int getUserid() {return userid;}

    public void setUserid(int id) {this.userid = id;}

    @Basic
    @Column(name = "name")
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    @Basic
    @Column(name = "industry")
    public String getIndustry() {return industry;}

    public void setIndustry(String industry) {this.industry = industry;}

    @Basic
    @Column(name = "banned")
    public boolean isBanned() {return banned;}

    public void setBanned(boolean b) {this.banned = b;}
}
