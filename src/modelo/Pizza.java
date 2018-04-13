/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author Kokekui
 */
public class Pizza {
    
    private String masa;
    private String tipo;
    private String tamanyo;
    private List conjIngredientes;
    private Precio precioPizza = new Precio();
    
    public Pizza() {
    }
    
    public Pizza(String masa, String tipo, String tamanyo, List conjIngredientes) {
        this.masa = masa;
        this.tipo = tipo;
        this.tamanyo = tamanyo;
        this.conjIngredientes = conjIngredientes;
    }
    
    private double calcularPrecio() {
        double precioFinal, precioParcial, precioMasa = 0, precioTipo = 0, precioIngredientes;
        if (masa != null) {
            precioMasa = precioPizza.getPrecioMasa().get(masa);
        }
        if (tipo != null) {
            precioTipo = precioPizza.getPrecioTiposPizzas().get(tipo);
        }
        precioIngredientes = calcularIngredientes();
        precioParcial = precioMasa + precioTipo + precioIngredientes;
        precioFinal = precioParcial;
        if (tamanyo != null) {
            if (tamanyo.equalsIgnoreCase("Pequeña")) {
                precioFinal = precioParcial;
            } else if (tamanyo.equalsIgnoreCase("Mediana")) {
                precioFinal = precioParcial;
                precioFinal *= 1.15;
            } else if (tamanyo.equalsIgnoreCase("Familiar")) {
                precioFinal = precioParcial;
                precioFinal *= 1.30;
            }
        }
        return precioFinal;
    }
    
    public String composicion() {
        String mensaje = "";
        mensaje += "Precio Final: " + calcularPrecio() + "\n";
        if (masa != null) {
            mensaje += "Masa: " + precioPizza.getPrecioMasa().get(masa) + " " + masa + "\n";
        }
        if (tipo != null) {
            mensaje += "Tipo: " + precioPizza.getPrecioTiposPizzas().get(tipo) + " " + tipo + "\n";
        }
        if (conjIngredientes != null) {
            mensaje += "Ingredientes: " + String.valueOf(calcularIngredientes()) + " " + conjIngredientes + "\n";
        }
        if (tamanyo != null) {
            mensaje += "Tamaño: " + tamanyo + "\n";
        }
        
        return mensaje;
    }
    
    private double calcularIngredientes() {
        double precioIngredientes = 0;
        if (precioPizza.getPrecioIngrediente().keySet() != null && conjIngredientes != null) {
            for (String ingrediente : precioPizza.getPrecioIngrediente().keySet()) {
                if (conjIngredientes.contains(ingrediente)) {
                    precioIngredientes += precioPizza.getPrecioIngrediente().get(ingrediente);
                }
            }
        }
        return precioIngredientes;
    }
    
    public boolean generarTicket() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Window ventana = null;
        String direccion = "";
        File ticketGener = directoryChooser.showDialog(ventana);
        if (ticketGener != null) {
            direccion = ticketGener.getAbsolutePath();
        }
        boolean ok = false;
        DateTimeFormatter formateado = DateTimeFormatter.ofPattern("yyyy_mm_dd hh-mm-ss");
        String fechaFormat = LocalDateTime.now().format(formateado);
        Path ticketSel = Paths.get(direccion + "\\Tickets" + fechaFormat + ".txt");
        try (BufferedWriter out = Files.newBufferedWriter(ticketSel, StandardOpenOption.CREATE)) {
            out.write(composicion());
            out.newLine();
            out.write(String.valueOf(calcularPrecio()));
            ok = true;
        } catch (IOException e) {
            System.out.println("Error al abrir el fichero");
        }
        return ok;
    }
    
    public void cargarPrecios() {
//        precioPizza.getPrecioIngrediente().clear(); AL HACER = AL SET, SE MACHACA EL MAP QUE ESTABA
        FileChooser fileChooser = new FileChooser();
        Map<String, Double> mapMasa = new HashMap<>();// mover los filechooser y los directory en el controller, y despues mandarlo aqui
        Map<String, Double> mapTipo = new HashMap<>();// mover los filechooser y los directory en el controller, y despues mandarlo aqui
        Map<String, Double> mapIngrediente = new HashMap<>();// mover los filechooser y los directory en el controller, y despues mandarlo aqui
        Window ventana = null;
        String direccion = "";
        File ticketGener = fileChooser.showOpenDialog(ventana);
        if (ticketGener != null) {
            direccion = ticketGener.getAbsolutePath();
        }
        Path archivo = Paths.get(direccion);
        try (Stream<String> datos = Files.lines(archivo);) {
            Iterator<String> it = datos.iterator();
            while (it.hasNext()) {
                String mensaje = it.next();
                StringTokenizer s = new StringTokenizer(mensaje, ":");
                String tipoMap = s.nextToken();
                if (tipoMap.equalsIgnoreCase("Masa")) {
                    String nombre = s.nextToken();
                    Double precio = Double.parseDouble(s.nextToken());
                    mapMasa.put(nombre, precio);
                    System.out.println(nombre + precio);
                }
                if (tipoMap.equalsIgnoreCase("Tipo")) {
                    String nombre = s.nextToken();
                    Double precio = Double.parseDouble(s.nextToken());
                    mapTipo.put(nombre, precio);
                    System.out.println(nombre + precio);
                }
                if (tipoMap.endsWith("Ingrediente")) {
                    String nombre = s.nextToken();
                    Double precio = Double.parseDouble(s.nextToken());
                    mapIngrediente.put(nombre, precio);
                    System.out.println(nombre + precio);
                }
                // while (s.hasMoreTokens() == true) { //si hay mas trozos
//                    listarPrecios.put(nombre, precio);
//                    precioPizza.setPrecioMasa(nombre,precio); HACERLO CUANDO TENGA TODOS LOS PRECIOS METIDOS
// Meterle el hashmap, uno a uno, con las diferentes cargas, diferenciandolo con los nombres de cada Map
                //System.out.println(s.nextToken()); //siguiente trozo
                // }
            }
            precioPizza.setPrecioMasa(mapMasa);
            precioPizza.setPrecioTiposPizzas(mapTipo);
            precioPizza.setPrecioIngrediente(mapIngrediente);
            // al acabar de rellenar cada map, hago los sets
        } catch (IOException ex) {
            System.out.println("Error en la lectura del archivo");
        }
    }

    //GETS Y SETS
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public List getConjIngredientes() {
        return conjIngredientes;
    }
    
    public void setConjIngredientes(List conjIngredientes) {
        this.conjIngredientes = conjIngredientes;
    }
    
    public String getMasa() {
        return masa;
    }
    
    public void setMasa(String masa) {
        this.masa = masa;
    }
    
    public String getTamanyo() {
        return tamanyo;
    }
    
    public void setTamanyo(String tamanyo) {
        this.tamanyo = tamanyo;
    }
    
    public Precio getPrecioPizza() {
        return precioPizza;
    }
    
    public void setPrecioPizza(Precio precioPizza) {
        this.precioPizza = precioPizza;
    }
    
}
