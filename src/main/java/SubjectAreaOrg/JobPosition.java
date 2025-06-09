package SubjectAreaOrg;

import java.io.Serializable;

public class JobPosition implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;

    public JobPosition() {
        this.id = 0;
        this.name = "";
        this.description = "";
    }

    public JobPosition(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "JobPosition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}