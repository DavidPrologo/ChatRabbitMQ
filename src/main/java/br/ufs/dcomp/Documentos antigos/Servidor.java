package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;

public class Servidor extends Thread{
    
    public Servidor(String ip, String login, String senha)throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        
        System.out.println("Servidor");
        
        factory.setHost(ip); // Alterar
        factory.setUsername(login); // Alterar
        factory.setPassword(senha); // Alterar
        factory.setVirtualHost("/");
        
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        String QUEUE_NAME = "minha-fila";
                          //(queue-name, durable, exclusive, auto-delete, params); 
        channel.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
        
        Consumer consumer = new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)           throws IOException {
            
                String message = new String(body, "UTF-8");
                System.out.println(message);
            
            }
        };
                          //(queue-name, autoAck, consumer);    
        channel.basicConsume(QUEUE_NAME, true,    consumer);
    }
    public static void main(String[] argv) throws Exception {
        new Servidor(
            "3.95.21.168",
            "marcio",
            "1234"
        );
    }
    
}