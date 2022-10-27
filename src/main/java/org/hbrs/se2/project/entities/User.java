package org.hbrs.se2.project.entities;

import javax.persistence.*;

/**
 * This entity reflects the exact same database table "user"
 * so JPA Repository 'understands' how to save or read data from/to this table
 *
 * This is why you for instance pass a user entity 'user' to the method
 * repository.save(user)
 */
@Entity
@Table(name="user", schema = "mid9db")
public class User {
    private int userid;
    private String username;
    private String password;
    private String email;
    private String role;

    public User() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userid")
    public int getUserid() {
        return userid;
    }
    public void setUserid(int id) {
        this.userid = id;
    }
    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @Basic
    @Column(name="password")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Basic
    @Column(name="email")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Basic
    @Column(name = "role")
    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}
}


