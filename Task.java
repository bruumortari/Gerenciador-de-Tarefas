import java.util.Date;

public class Task {
    private int id;
    private String title;
    private String description;
    private String status;
    private Date date;

    // Construtor vazio
    public Task() {
    }

    // Construtor com argumentos
    public Task(int id, String title, String description, String status, Date date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStatus() {
        return this.status;
    }

    public Date getDate() {
        return this.date;
    }

}