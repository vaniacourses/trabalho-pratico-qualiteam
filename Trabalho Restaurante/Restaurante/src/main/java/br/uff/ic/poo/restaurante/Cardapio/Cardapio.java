package br.uff.ic.poo.restaurante.Cardapio;

import br.uff.ic.poo.restaurante.Item.Item;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Cardapio {   
    
    Scanner Teclado = new Scanner(System.in);
    ArrayList<Item> itens = new ArrayList();
        
    //ler o arquivo e armazenar em memoria principal(array itens)
    public void lerArquivo(){        
        File arquivo = new File("cardapio.txt");
        
        if (arquivo.exists()){
            FileReader fr = null;
            try {
                fr = new FileReader(arquivo);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cardapio.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader br = new BufferedReader(fr);
            //lê a linha                        
            String linha = null;            
            try {
                while (br.ready()){
                    linha = br.readLine();
                    String palavras[] = linha.split(";");
                    Item novo = new Item(Integer.parseInt(palavras[0]),palavras[1],Float.parseFloat(palavras[2]));
                    itens.add(novo);                    
                }
            } catch (IOException ex) {
                Logger.getLogger(Cardapio.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Cardapio.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Cardapio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("Não há cardapio salvo no disco");
        } 
        System.out.println();
    }
    
    public void adicionarItem(){
        if (itens.isEmpty()){
            this.lerArquivo(); 
        }    
        boolean continua = true;
        while (continua){
            System.out.println("\nInsira o nome do item a ser inserido");
            String nome = Teclado.nextLine();
            boolean existe = false;
            int i = 0;
            while (i < itens.size()){
                if (itens.get(i).getNome().equals(nome)){ 
                    existe = true;
                    i+= itens.size();
                }                
                i++;
            }     
            if (!existe){               
                System.out.println("\nInsira o id do item");
                String aux = Teclado.nextLine();
                int id;
                //Tratamento de entrada inválida
                try{
                    id = Integer.parseInt(aux); 
                    for (Item auxiliar: itens){
                        if (auxiliar.getId()== id){
                            throw new IllegalArgumentException();                        
                        }                   
                    }
                }catch (IllegalArgumentException e){
                    System.out.println("Entrada de ID inválida\nOperação Interrompida");
                    break;                       
                }
                

                System.out.println("Insira o preço do item");
                aux = Teclado.nextLine();
                float preco;
                //Tratamento de entrada inválida
                try{
                    preco = Float.parseFloat(aux);
                }catch (IllegalArgumentException e){
                    System.out.println("Entrada de preço inválida\nOperação Interrompida");
                    break;                       
                } 
                
                Item novo = new Item(id, nome, preco);                
                itens.add(novo); 
                System.out.println("Item salvo");
            }else{
                System.out.println("Item já se encontra no cardapio");
            }            
            System.out.println("\nDeseja salvar mais um item?");
            System.out.println("1. Sim\n0. Não");
            String op = Teclado.nextLine();
            int escolha = Integer.parseInt(op);
            if (escolha == 0){
                continua = false;
            }           
        }
        //atualizando cardapio.txt
        File arquivo = new File("cardapio.txt");
        try (FileWriter arq = new FileWriter(arquivo)) {
            PrintWriter gravarArq = new PrintWriter(arq);
            for (int i = 0 ; i < itens.size() ; i++){
                gravarArq.println(itens.get(i).getId() + ";" + itens.get(i).getNome() + ";" + itens.get(i).getPreco());
            }
        } catch (IOException ex) {
            Logger.getLogger(Cardapio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    public void removerItem() {
        if (itens.isEmpty()){
            this.lerArquivo();
        }    
        boolean continua = true;
        while (continua){
            System.out.println("\nInsira o ID do item a ser removido");
            String nome = Teclado.nextLine();
            int id = Integer.parseInt(nome);
            boolean existe = false;
            int i = 0;
            int remover = -1;
            while (i < itens.size()){
                if (itens.get(i).getId() == id){
                    existe = true;
                    remover = i;
                    i+= itens.size();
                }                
                i++;
            }     
            if (existe){                
                itens.remove(remover);
                System.out.println("Item removido");
            }else{
                System.out.println("Item não se encontra no cardapio");                
            }            
            System.out.println("\nDeseja remover mais um item?");
            System.out.println("1. Sim\n0. Não");
            String op = Teclado.nextLine();
            int escolha = Integer.parseInt(op);
            if (escolha == 0){
                continua = false;
            }           
        }
        //atualizando cardapio.txt
        File arquivo = new File("cardapio.txt");
        try (FileWriter arq = new FileWriter(arquivo,false)) {
            PrintWriter gravarArq = new PrintWriter(arq);
            for (int i = 0 ; i < itens.size() ; i++){
                gravarArq.println(itens.get(i).getId() + ";" + itens.get(i).getNome() + ";" + itens.get(i).getPreco());
            }
        } catch (IOException ex) {
            Logger.getLogger(Cardapio.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
   
    public void imprimeCardapio(){
        File arquivo = new File("cardapio.txt");
        if (arquivo.exists()){
            try{
                FileReader fr = new FileReader(arquivo);
                BufferedReader br = new BufferedReader(fr);
                System.out.println("\n\t\t\tCARDAPIO");
                while (br.ready()) {
                    //lê a linha
                    String linha = br.readLine();
                    //verifica se item já encontra no arquivo
                    String palavras[] = linha.split(";");                
                    // System.out.println("ID:"+palavras[0]+ " Item: " + palavras[1]+ " R$" + palavras[2]);
                    float preco = Float.parseFloat(palavras[2]);
                    System.out.printf("ID: %-3s Item: %-25s R$ %.2f %n", palavras[0], palavras[1], preco);                                                                                       
                }            
            }catch (IOException ex) {
            }
            System.out.println();
        }else{
            System.out.println("Não há cardapio salvo no disco");
        }     
        
    }
    
    public ArrayList<Item> getItems(){
        return this.itens;
    }
}
