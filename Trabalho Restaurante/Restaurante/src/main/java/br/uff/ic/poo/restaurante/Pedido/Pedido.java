package br.uff.ic.poo.restaurante.Pedido;

import br.uff.ic.poo.restaurante.Item.Item;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Pedido {
    
    Map<Item,Integer> lista = new HashMap<>();
    
    public void adicionaItem(Item i, int n){
        lista.put(i, n);
    }
    
    public void removeItem(Item i, int n){
        if(lista.get(i)-n >= 0){
            lista.replace(i, lista.get(i), lista.get(i)- n);
        }
    }
    
    public void removeItem(Item i){
        lista.remove(i);
    }
       
    public float calculaSubTotal(){
        float total = 0;
        Set<Item> elem = lista.keySet();
        Item[] l = new Item[elem.size()];
        elem.toArray(l);
        for(int i=0;i<elem.size();i++)
            total += l[i].getPreco() * lista.get(l[i]);
        return total;
    }
}