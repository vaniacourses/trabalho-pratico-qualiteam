package br.uff.ic.poo.restaurante.Item;

public class Item {
        
    private String nome;
    private int id;
    private float preco;
    
    public Item(int idRecebido, String nomeRecebido , float precoRecebido){
        this.nome = nomeRecebido;
        this.id = idRecebido;
        this.preco = precoRecebido;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    public float getPreco() {
        return preco;
    }  

}
