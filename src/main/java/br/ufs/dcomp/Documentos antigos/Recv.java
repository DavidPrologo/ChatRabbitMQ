package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;

public class Recv extends Thread{

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        (new Recv()).conectar();
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
        
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        
        Consumer consumer = new DefaultConsumer(channel) {
            public void handleDelivery(
            String consumerTag, 
            Envelope envelope, 
            AMQP.BasicProperties properties, 
            byte[] body
                )
            throws IOException {
        
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Mensagem recebida: '" + message + "'");
            }
        };
        //(queue-name, autoAck, consumer);    
        channel.basicConsume(QUEUE_NAME, true,    consumer);
    }
}