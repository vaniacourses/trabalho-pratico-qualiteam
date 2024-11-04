package br.uff.ic.poo.restaurante.Main;

import br.uff.ic.poo.restaurante.Item.Item;
import br.uff.ic.poo.restaurante.Mesa.Mesa;
import br.uff.ic.poo.restaurante.Pedido.Pedido;
import br.uff.ic.poo.restaurante.Restaurante.Restaurante;
import br.uff.ic.poo.restaurante.Cardapio.Cardapio;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;


public class Main {

    public static void main(String[] args){

        Scanner teclado = new Scanner(System.in);
        
        System.out.println("\n........... Restaurant System Mega Super Pro Version 1.0.28.45 Fall 2021 ...........");

        int escolha_menu_principal = -1;
        Cardapio novoCardapio = new Cardapio();
        novoCardapio.lerArquivo();
        while(escolha_menu_principal != 0){

            System.out.println("\nMenu principal: ");
            System.out.println("1. Administrar cardápio.");
            System.out.println("2. Iniciar funcionamento.");
            System.out.println("0. Encerrar programa.\n");

            try{
                escolha_menu_principal = teclado.nextInt();
            }catch(InputMismatchException ex){
                teclado.next();
                escolha_menu_principal = -1;
                System.out.println("\nEntrada inválida. Escolha uma opção válida.");
            }
            
            switch(escolha_menu_principal){
                case -1:
                    break;
                case 1:
                    int escolha_menu_secundario1 = -1;

                    while(escolha_menu_secundario1 != 0){

                        System.out.println("\nAdministrar Cardápio: ");
                        System.out.println("1. Imprimir cardápio.");
                        System.out.println("2. Adicionar prato.");
                        System.out.println("3. Remover prato.");
                        System.out.println("0. Voltar ao menu anterior.\n");
                        
                        try{
                            escolha_menu_secundario1 = teclado.nextInt();
                        }catch(InputMismatchException ex){
                            teclado.next();
                            escolha_menu_secundario1 = -1;
                            System.out.println("\nEntrada inválida. Escolha uma opção válida.");
                        }

                        switch(escolha_menu_secundario1){
                            case -1:
                                break;
                            case 1:
                                // chamar método para imprimir o cardápio
                                novoCardapio.imprimeCardapio();
                                break;
                            case 2:
                                // chamar método para adicionar prato ao cardápio
                                novoCardapio.adicionarItem();
                                break;
                            case 3:
                                // chamar método para remover prato do cardápio
                                novoCardapio.removerItem();
                                break;
                            case 0:
                                escolha_menu_secundario1 = 0;
                                break;
                            default:
                                System.out.println("\nOpção inválida. Escolha uma opção válida.");
                                break;
                        }
                    }
                    break;
                case 2:
                    Restaurante restaurante = new Restaurante(10);
                    System.out.println("\nRestaurante em funcionamento.");
                    
                    int escolha_menu_secundario2 = -1;

                    while(escolha_menu_secundario2 != 0){

                        System.out.println("\nAdministrar salão: ");
                        System.out.println("1. Abrir conta para mesa.");
                        System.out.println("2. Fazer pedido para mesa.");
                        System.out.println("3. Fechar conta para mesa.");
                        // System.out.println("4. Fazer pagamento de uma conta.");
                        System.out.println("4. Chama proximo da fila");
                        System.out.println("0. Voltar ao menu anterior.\n");

                        try{
                            escolha_menu_secundario2 = teclado.nextInt();
                        }catch(InputMismatchException ex){
                            teclado.next();
                            escolha_menu_secundario2 = -1;
                            System.out.println("\nEntrada inválida. Escolha uma opção válida.");
                        }

                        switch(escolha_menu_secundario2){
                            case -1:
                                break;
                            case 1:
                                // instanciar uma mesa, adicioná-la às mesas existentes e abrir lista de pedidos para ela
                                ArrayList<Integer> disponiveis = restaurante.encontraMesaDisponivel();
                                if(disponiveis.size()>0){
                                    System.out.println("\nPossuem as seguintes mesas livres:\n"+disponiveis);
                                    int k = -1;
                                    try{
                                        while(!disponiveis.contains(k)){
                                            System.out.println("\nQual deseja?");
                                            k = teclado.nextInt();
                                            if(!disponiveis.contains(k)){
                                                System.out.println("\nNúmero inválido. Tente novamente.");
                                            }
                                        }
                                        restaurante.ocupar(k);
                                    }catch(InputMismatchException ex){
                                        teclado.next();
                                        System.out.println("\nEntrada inválida. Escolha uma opção válida. Operação interrompida.");
                                        break;
                                    }
                                }
                                else{
                                    teclado.nextLine();
                                    System.out.println("\nNão possui mesa disponível, favor aguardar.\nInsira seu nome para entrar na lista de espera (n para sair):");
                                    restaurante.entrarNaFila(teclado.nextLine());
                                }
                                break;
                            case 2:
                                // chamar método para fazer pedido para uma mesa
                                System.out.print("\nInsira o numero da mesa: ");
                                int num;
                                try{
                                    num = teclado.nextInt();
                                    try{
                                        if (restaurante.getMesa(num).isDisponivel()) {
                                            System.out.println("\nA mesa "+num+" não está ocupada");
                                            break;
                                        }
                                    }catch(IndexOutOfBoundsException ex){
                                        System.out.println("\nMesa não existe. Operação interrompida.");
                                        break;
                                    }
                                }catch(InputMismatchException ex){
                                    teclado.next();
                                    System.out.println("\nEntrada inválida. Operação interrompida.");
                                    break;
                                }
                                teclado.nextLine();
                                Pedido pedido = new Pedido();
                                while(true){
                                    novoCardapio.imprimeCardapio();
                                    System.out.println("\nInsira o nome do item e a quantidade: (0 para sair)");
                                    String info = teclado.nextLine();
                                    if(info.equals("0")){
                                        break;
                                    }
                                    String [] infos = info.split(" ");
                                    //validar nome do item
                                    
                                    ArrayList<Item> validate = novoCardapio.getItems();
                                    boolean validation = false;
                                    Item a = validate.get(0);
                                    for(Item it:validate){
                                        if(it.getNome().equalsIgnoreCase(infos[0])){
                                            a = it;
                                            validation = true;
                                        }
                                    }
                                    if(validation){
                                        pedido.adicionaItem(a,Integer.parseInt(infos[1]));
                                    }
                                    else{
                                        System.out.println("\nO item inserido nao existe no cardápio");
                                    }
                                }
                                
                                System.out.println("\nValor do pedido: "+ pedido.calculaSubTotal());
                                
                                restaurante.getMesa(num).fazerPedido(pedido);

                                break;
                            case 3:
                                // fechar conta para uma mesa e apresentar valor total
                                System.out.println("\nInsira o numero da mesa: ");
                                try {
                                    num = teclado.nextInt();
                                    try{
                                        System.out.println("\nA conta da mesa "+num+" deu R$"+restaurante.getMesa(num).fechaMesa());
                                    }catch(IndexOutOfBoundsException ex){
                                        System.out.println("\nMesa não existe. Operação interrompida.");
                                    }  
                                } catch (InputMismatchException e) {
                                    System.out.println("\nInsira uma mesa válida. Operação cancelada.");
                                }
                                break;
                            // case 4:
                            //     // apresentar formas de pagamento e receber pagamento (adicionar ao caixa do dia)
                            //     break;
                            case 4:
                                // administrar a fila
                                System.out.println("\nChamando próximo da fila.");
                                if(restaurante.chamaProx()){
                                    disponiveis = restaurante.encontraMesaDisponivel();
                                    if(disponiveis.size()>0){
                                        System.out.println("\nPossuem as seguintes mesas livres:\n"+disponiveis + "\nQual deseja?");
                                        int mesa_livre;
                                        try{
                                            mesa_livre = teclado.nextInt();
                                            System.out.printf("\nO cliente foi encaminhado para a mesa %d.\n", mesa_livre);
                                            restaurante.ocupar(mesa_livre);
                                        }catch(InputMismatchException ex){
                                            teclado.next();
                                            System.out.println("\nEntrada inválida. Escolha uma opção válida. Operação interrompida.");
                                        }
                                    }
                                }
                                break;
                            case 0:
                                
                                int voltar_para_menu_principal = -1;
                    
                                while(voltar_para_menu_principal != 0 && voltar_para_menu_principal != 1){
                                    
                                    System.out.println("\nVocê deseja voltar para o menu principal?");
                                    System.out.println("Isso irá resetar todo o gerenciamento pedidos.");
                                    System.out.println("1. Sim.");
                                    System.out.println("0. Não.\n");
                                    
                                    voltar_para_menu_principal = teclado.nextInt();
                                    
                                    if(voltar_para_menu_principal != 0 && voltar_para_menu_principal != 1){
                                        System.out.println("\nOpção inválida. Escolha uma opção válida.");
                                    }
                                }
                                if(voltar_para_menu_principal == 0){
                                    escolha_menu_secundario2 = -1;
                                }
                                break;
                            default:
                                System.out.println("\nOpção inválida. Escolha uma opção válida.");
                                break;
                        }
                    }
                    break;
                case 0:
                    
                    int sair_do_programa = -1;
                    
                    while(sair_do_programa != 0 && sair_do_programa != 1){
                        
                        System.out.println("\nVocê deseja encerrar o programa?");
                        System.out.println("1. Sim.");
                        System.out.println("0. Não.\n");
                        
                        try{
                            sair_do_programa = teclado.nextInt();
                            if(sair_do_programa != 0 && sair_do_programa != 1){
                                System.out.println("\nOpção inválida. Escolha uma opção válida.");
                            }
                        }catch(InputMismatchException ex){
                            teclado.next();
                            sair_do_programa = -1;
                            System.out.println("\nEntrada inválida. Escolha uma opção válida.");
                        }
                    }
                    if(sair_do_programa == 0){
                        escolha_menu_principal = -1;
                    }else{
                        System.out.print("\nEncerrando programa");
                        try{
                            Thread.sleep(800);
                        }
                        catch(InterruptedException ex){
                            Thread.currentThread().interrupt();
                        }
                        System.out.print(".");
                        try{
                            Thread.sleep(800);
                        }
                        catch(InterruptedException ex){
                            Thread.currentThread().interrupt();
                        }
                        System.out.print(".");
                        try{
                            Thread.sleep(800);
                        }
                        catch(InterruptedException ex){
                            Thread.currentThread().interrupt();
                        }
                        System.out.println(".");
                        try{
                            Thread.sleep(400);
                        }
                        catch(InterruptedException ex){
                            Thread.currentThread().interrupt();
                        }
                    }
                    break;
                default:
                    System.out.println("\nOpção inválida. Escolha uma opção válida.");
                    break;
            }
            
        }
        
        System.out.println("\nPrograma encerrado.");
    }
    
}
