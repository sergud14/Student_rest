import lombok.*;
import java.util.ArrayList;
import java.util.List;

public class Student {
    Integer id;
    private String name;
    private List marks = new ArrayList<>();
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public List getMarks() {
        return new ArrayList<>(marks);
    }
    public void setMarks(List marks) {
        this.marks = marks;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", marks=" + marks +
                '}';
    }
}