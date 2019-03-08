package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.util.Scanner;
import java.io.IOException;
//import br.ufs.dcomp.ChatRabbitMQ.Utilitario;

public class Chat{

    private String destinatario = "";
    private String usuario;
    private Channel channel;
    private Consumer consumer;
    private Connection connection;
    
    private Scanner entrada;
    
    
    public static void main(String[] argv) throws Exception {
        new Chat();
    }
    
    public Chat()throws Exception{
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://david:123@3.84.134.71");
        
        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();
        //Entrada de dados
        this.entrada = new Scanner(System.in); 
        //Garantir a quebra de linha no Mac, Windowns e Linux
        
        this.pedirUsuario();
        this.pedirDestinatario();
        this.rodar();
        
    }
    public void rodar() throws Exception{
        while (true) {          
            //imprimir na tela ex: usuárioDestino >>   
            System.out.print(destinatario+">> ");
            String mensagem = entrada.nextLine();
            if(mensagem.charAt(0) == '@'){
                this.setDestinatario(mensagem);
            }else{
                String texto = Utilitario.data_e_hora()+" "+usuario+" diz: "+mensagem;
                this.channel.basicPublish("", this.destinatario, null, texto.getBytes("UTF-8"));
            }
        }
        
    }
    public void pedirUsuario()throws Exception{
        //Inserir usuário e criar fila no Rabbitmq
        System.out.println("Digite um nome de usuário");
        System.out.print("User: ");
        //acrescenta o @ na frente do usuário
        this.usuario = entrada.nextLine();
        this.usuario = "@"+usuario;
          
        System.out.println("Criando fila para acesso ....");
        this.channel.queueDeclare(usuario/*QUEUE_NAME*/, false,   false,     false,       null);
        this.consumer = new Consumidor(this.channel, this);
        this.channel.basicConsume(usuario, true, this.consumer);
        
    }
    public void pedirDestinatario(){
        System.out.println("Selecione um usuário para conversar, digite @ antes do nome:" );
        System.out.print(">> ");
        
        this.destinatario = entrada.nextLine();
        while(destinatario.charAt(0) != '@' ){
            System.out.println("Usuário Incorreto!");
            System.out.println("Selecione um usuário para conversar, digite @ antes do nome:" );
            System.out.print(">>");
            destinatario = entrada.nextLine();
        }
        
    }
    public String getDestinatario(){
        return this.destinatario;
    }
    public void setDestinatario(String destinatario){
        this.destinatario = destinatario;
    }
}