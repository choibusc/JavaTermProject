package movie.model;

public class Distributor {
    private String id;
    private String name;
    private String phone;
    private String address;
    private String manager;

    public Distributor(String id, String name, String phone, String address, String manager) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.manager = manager;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getManager() { return manager; }
}
