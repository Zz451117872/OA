package com.example.OA.model.activiti;

public class ActGroup {
    private String id;

    private Integer rev;

    private String name;

    private String type;

    public ActGroup(String id, Integer rev, String name, String type) {
        this.id = id;
        this.rev = rev;
        this.name = name;
        this.type = type;
    }

    public ActGroup() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
}