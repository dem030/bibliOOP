package exceptions;

import java.sql.Date;

public class PrestitoNonValido extends BibliotecaException{
    public PrestitoNonValido(int prest_id){
        super("Prestito non valido con id: " + prest_id, "PRESTITO_NON_VALIDO");
    }
    public PrestitoNonValido(Date data_restituzione){
        super("Prestito già restituito in data: " + data_restituzione.toString(), "PRESTITO_GIA_RESTITUITO");
    }

}
