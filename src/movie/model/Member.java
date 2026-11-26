package movie.model;

public class Member {
    private String id;
    private String password;
    private String name;
    private Integer age; // 선택 입력(NULL 가능)
    private String grade;
    private int point;

    public Member(String id, String password, String name, Integer age, String grade, int point) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.point = point;
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public Integer getAge() { return age; }
    public String getGrade() { return grade; }
    public int getPoint() { return point; }
}
