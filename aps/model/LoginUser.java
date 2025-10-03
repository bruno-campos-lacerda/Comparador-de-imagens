package projeto.aps.model;

public class LoginUser {
    
    private String Username;
    private String Password;
    

    public LoginUser(){}
    
    public LoginUser(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
    } 

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }
    
    
}
