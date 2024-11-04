package net.originmobi.pdv.enumerado.cartao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CartaoSituacaoTest {

    @Test
    public void testGetDescricao() {
        assertEquals("A Processar", CartaoSituacao.APROCESSAR.getDescricao());
        assertEquals("Processado", CartaoSituacao.PROCESSADO.getDescricao());
        assertEquals("Antecipado", CartaoSituacao.ANTECIPADO.getDescricao());
    }

    @Test
    public void testEnumValues() {
        CartaoSituacao[] expectedValues = { CartaoSituacao.APROCESSAR, CartaoSituacao.PROCESSADO, CartaoSituacao.ANTECIPADO };
        assertEquals(expectedValues.length, CartaoSituacao.values().length);
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], CartaoSituacao.values()[i]);
        }
    }

    @Test
    public void testEnumValueOf() {
        assertEquals(CartaoSituacao.APROCESSAR, CartaoSituacao.valueOf("APROCESSAR"));
        assertEquals(CartaoSituacao.PROCESSADO, CartaoSituacao.valueOf("PROCESSADO"));
        assertEquals(CartaoSituacao.ANTECIPADO, CartaoSituacao.valueOf("ANTECIPADO"));
    }
}
