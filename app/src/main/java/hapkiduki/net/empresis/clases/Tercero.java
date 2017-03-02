package hapkiduki.net.empresis.clases;

/**
 * Created by Programa-PC on 01/03/2017.
 */

public class Tercero {

    private String tercero, direccion, telefono, dni;

   /* public Tercero(int dni, String tercero, String direccion, String telefono) {
        this.dni = dni;
        this.tercero = tercero;
        this.direccion = direccion;
        this.telefono = telefono;
    }*/

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
