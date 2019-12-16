package CombinePDF;

import java.util.List;

public class History {
    private int id;
    private String paths;
    private String date;

    public History(int id, String date, String paths) {
        this.id = id;
        this.paths = paths;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", paths=" + paths +
                ", date='" + date + '\'' +
                '}';
    }
}
