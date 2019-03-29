package br.ufs.dcomp.ChatRabbitMQ;
import java.util.HashMap;

public class Usuario{
    
    Chat chat;
    private String nome;
    
    public Usuario(Chat chat){
        this.chat = chat;
        this.nome = null;
    }
    public void povoar(HashMap comandos){
        comandos.put("@", new AddUser());
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public String getNome(){
        return (this.nome);
    }
    class AddUser implements iAcao{
        public void execute() throws Exception{
            String destinatario = chat.getMensagem();
            chat.setDestinatario(destinatario);
        }
    }
}