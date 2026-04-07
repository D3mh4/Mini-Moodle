package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

public class Assignment {
    private String id;
    private String courseId;
    private String title;
    private String description;
    private String dueDate;
    private String instructions;
    private String status;
    private Integer grade;
    private String comment;
    private int totalPoints;
    private String type;

    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getStatus() {
        return status;
    }

    public Integer getGrade() {
        return grade;
    }

    public String getComment() {
        return comment;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setType(String type) {
        this.type = type;
    }
}