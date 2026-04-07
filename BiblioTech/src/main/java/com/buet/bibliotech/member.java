package com.buet.bibliotech;

public class member
{
    private String id;
    private String name;
    private String department;
    private String batch;
    private String email;

    public member(String id, String name, String department, String batch, String email) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.batch = batch;
        this.email = email;
    }
    public String getId() {
        return id;
    }
    public String getName() {

        return name;
    }
    public String getDepartment() {

        return department;
    }
    public String getBatch() {
        return batch; }
    public String getEmail() {
        return email;
    }
}
