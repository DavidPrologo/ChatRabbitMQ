package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.util.Scanner;

public class Send extends Thread{

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        (new Send()).conectar();
    }
    public void run(){
        try{this.conectar();}catch(Exception e){System.out.println(e);}
    }
    
    public void conectar()throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        
        factory.setHost("3.84.134.71"); // Alterar
        factory.setUsername("david"); // Alterar
        factory.setPassword("123"); // Alterar
        factory.setVirtualHost("/");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel(); 
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "";
            while(true){
                System.out.println("Digite uma mensagem a ser enviada:");
                Scanner leitor = new Scanner(System.in);
                message = leitor.nextLine();
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
}