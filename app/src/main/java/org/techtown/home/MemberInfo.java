package org.techtown.home;

public class MemberInfo {
    private String name;
    private String age;
    private String email;
    private String gender;
    private Integer goal;

    public MemberInfo(String name, String age, String email, String gender,Integer goal ){
        this.name = name;
        this.age = age;
        this.email = email;
        this.gender = gender;
        this.goal=goal;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAge() { return age; }

    public void setAge(String age) { this.age = age; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public Integer getGoal() { return goal; }

    public void setGoal(Integer goal) { this.goal = goal; }
}

