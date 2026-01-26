package exceptions;

public class LibroNonDisponibile extends BibliotecaException {
    public LibroNonDisponibile(String titolo){
        super("Il libro '" + titolo + "' non è disponibile per il prestito.");
    }
}
