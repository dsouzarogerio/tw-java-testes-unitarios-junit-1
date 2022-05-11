package br.com.treinaweb.twbiblioteca.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class AutorTest {
    
    /*
    * Teste de cenário com retorno TRUE
    */
    @Test
    void quandoMetodoEstaVivoForChamadoComDataDeFalecimentoNulaDeveRetornarTrue(){
        
        //cenario
        Autor autor = new Autor();

        //execução
        boolean estaVivo = autor.estaVivo();

        //verificação
        //assertEquals(true, estaVivo);
        assertTrue(estaVivo);
    }

    /*
    * Teste do método com retorno FALSE 
    */
    @Test
    void quandoMetodoEstaVivoForChamadoComDataDeFalecimentoPreenchidaDeveRetornarFalse(){
        
        //cenario
        Autor autor = new Autor();
        autor.setDataFalecimento(LocalDate.now());

        //execução
        boolean estaVivo = autor.estaVivo();

        //verificação
        //assertEquals(false, estaVivo);
        assertFalse(estaVivo);

    }
}
