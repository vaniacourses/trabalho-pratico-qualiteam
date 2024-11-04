package br.uff.ic.poo.restaurante.Restaurante;

import br.uff.ic.poo.restaurante.Mesa.Mesa;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Restaurante {
    
    Queue<String> fila = new LinkedList();
    ArrayList<Mesa> mesas = new ArrayList();
    
    public Restaurante(int n){
        for(int i=0;i<n;i++){
            Mesa m = new Mesa();
            this.mesas.add(m);
        }
    }
    
    public void entrarNaFila(String nome){
        if (nome.equals("n")) {
            return;
        }
        fila.add(nome);
    }
    
    public void esvaziaFila(){
        fila.clear();
    }
    
    public ArrayList<Integer> encontraMesaDisponivel(){
        ArrayList<Integer> numeros = new ArrayList<>();
        
        for(int i = 0; i< mesas.size() ; i++){
            if(mesas.get(i).isDisponivel()){
                numeros.add(i);
            }
        }
        return numeros;
            
    }    
    
    public boolean chamaProx(){
        if(this.fila.size()>0){
            System.out.println(fila.remove()+" foi chamado");
            return true;
        }
        return false;
    }
    
    public void ocupar(int n){
        mesas.get(n).ocupar();
    }
    
    public ArrayList<Mesa> getMesas() {
        return mesas;
    }
    
    public Mesa getMesa(int n){
        return mesas.get(n);
    }
    
}
