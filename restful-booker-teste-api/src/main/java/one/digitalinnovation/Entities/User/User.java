package one.digitalinnovation.Entities.User;

public class User {
    private String username;
    private String firsname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    
    public User(String username, String firsname, String lastname, String email, String password, String phone) {
        this.username = username;
        this.firsname = firsname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFirsname() {
        return firsname;
    }
    public void setFirsname(String firsname) {
        this.firsname = firsname;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
