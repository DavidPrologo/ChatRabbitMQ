package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.amq.*;
import com.rabbitmq.client.*;
import java.util.Scanner;
import java.io.IOException;
import java.util.HashMap;
import com.google.protobuf.ByteString;

public class Chat{

    private String destinatario;
    private Usuario usuario;
    private Grupo grupo;
    private String mensagem;
    private Channel channel;
    private Consumer consumer;
    private Connection connection;
    private ConnectionFactory factory;
    private HashMap<String, iAcao>comandos;
    
    private Scanner entrada;
    
    
    public static void main(String[] argv) throws Exception {
        //porta 15672
        new Chat("david", "123", "3.84.134.71");
    }
    
    public Chat(String usuario, String senha, String proxy)throws Exception{
        this.factory = new ConnectionFactory();
        this.factory.setUri("amqp://"+usuario+":"+senha+"@"+proxy);
        this.connection = this.factory.newConnection();
        this.channel = this.connection.createChannel();//seria por aqui acho eu pra fazer a mudanca do exchange
        
        this.mensagem = "";
        
        this.comandos = new HashMap<String, iAcao>();
        this.povoar(this.comandos);
        this.grupo = new Grupo(this);
        this.grupo.povoar(this.comandos);
        this.usuario = new Usuario(this);
        this.usuario.povoar(this.comandos);
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
            System.out.print(this.getDestinatario()+">> ");
            this.mensagem = entrada.nextLine();
            String chave = Utilitario.getComando(mensagem);
            iAcao acao = comandos.get(chave);
            if(acao != null)
                acao.execute();
            else{
                System.out.println("comando inválido, por faver tente novamente:");
            }
        }
        
    }
    public void pedirUsuario()throws Exception{
        //Inserir usuário e criar fila no Rabbitmq
        System.out.println("Digite um nome de usuário");
        System.out.print("User: ");
        //acrescenta o @ na frente do usuário
        this.usuario.setNome(entrada.nextLine());
          
        System.out.println("Criando fila para acesso ....");
        this.channel.queueDeclare("@"+usuario.getNome()/*QUEUE_NAME*/, false,   false,     false,       null);
        this.consumer = new Consumidor(this);
        this.channel.basicConsume("@"+usuario.getNome(), true, this.consumer);
        
    }
    public void pedirDestinatario(){
        System.out.println("Selecione um usuário para conversar, digite @ antes do nome:" );
        System.out.print(">> ");
        
        this.mensagem = entrada.nextLine() ;
        String chave = Utilitario.getComando(mensagem);
        while(!chave.equals("@") && !chave.equals("#")){
            System.out.println("Usuário Incorreto!");
            System.out.println("Selecione um usuário para conversar, digite @ antes do nome:" );
            System.out.print(">>");
            this.mensagem = entrada.nextLine();
        }
        this.destinatario = this.mensagem;
        try{  this.comandos.get(chave).execute(); }
        catch(Exception e){ System.out.println(e); }
    }
    public void setDestinatario(String destinatario){
        this.destinatario = destinatario;
    }
    public String getDestinatario(){ return this.destinatario; }
    public String getMensagem(){ return this.mensagem; }
    public Channel getChannel(){ return this.channel; }
    public Usuario getUsuario(){return this.usuario; }
    
    private void povoar(HashMap comandos){
        comandos.put("enviar", new iAcao(){
            public void execute()throws Exception{
                HashMap<String,Object> message = new HashMap<String,Object>();
                message.put("receptor", getDestinatario().substring(1));
                message.put("emissor", usuario.getNome());
                message.put("data", Utilitario.getData());
                message.put("hora", Utilitario.getHora());
                message.put("grupo", grupo.getNome());
                message.put("conteudo", ByteString.copyFromUtf8(getMensagem()));
                
                char sinal = destinatario.charAt(0);
                String grupo_destino = (sinal == '#')?destinatario:"";
                String usuario_destino = (sinal == '@')?destinatario:"";
                
             //   channel.basicPublish("", destinatario, null, Utilitario.serializar(message)); //esse é seu codigo
                channel.basicPublish(grupo_destino, usuario_destino, null,  Utilitario.serializar(message)); /*testei e funcionou mas esta replicando 
acho que o problema pode estar na chamada da concatenaçao ou em outro metodo, as vezes nao vai para todos acho que a pode esta havendo fila
a menos ou algo no canal */ 
                
                //channel.basicPublish("", destinatario, null, texto.getBytes("UTF-8"));
            }
        });
    }
    public void imprimir(byte[] body){
        HashMap<String,String> message = Utilitario.desserializar(body);
        //Exemplo de saida
        //(26/03/2019 às 01:12:57) NomeDoEmissor diz para NomeDoReceptor: oi
        System.out.println(
            "\n("+message.get("data")+" às "
            +message.get("hora")+") "
            +message.get("emissor")+" diz para "
            +message.get("receptor")+": "
            +message.get("corpoconteudo")
        );
        if(getDestinatario() != null)
            System.out.print(getDestinatario());
        else
            System.out.println("destinatario nulo");
        System.out.print(">>");
        
    }
}





