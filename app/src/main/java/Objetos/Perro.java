package Objetos;

public class Perro {

    String email;
    String genero;
    String nombre;
    String raza;


    public Perro() {
    }

    public Perro(int ID, String email, String genero, String nombre, String raza) {

        this.email = email;
        this.genero = genero;
        this.nombre = nombre;
        this.raza = raza;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }
}
