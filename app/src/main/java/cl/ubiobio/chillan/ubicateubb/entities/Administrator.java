package cl.ubiobio.chillan.ubicateubb.entities;



/** This class is created for be ocupated in BDD's motor SQLite**/
public class Administrator {


    private String username;
    private String password;


    /** initializing Constructor and creating methods getters and setters for posterior use **/
    public Administrator(String username, String password) {
        this.username = username;
        this.password = password;


    }
    public Administrator(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
