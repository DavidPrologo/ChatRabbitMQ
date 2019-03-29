package br.ufs.dcomp.ChatRabbitMQ;
import java.util.HashMap;

public class Grupo{
    
    private Chat chat;
    private String nome;
    
    public Grupo(Chat chat){
        this.chat = chat;
        this.nome = "sem_grupo";
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public String getNome(){
        return (this.nome);
    }
    public void povoar(HashMap comandos){
        comandos.put("!newGroup", new NewGroup());
        comandos.put("!addUser", new AddUser());       
        comandos.put("!delFromGroup",  new RemoveUserGroup());
        comandos.put("!removeGroup", new DeleteGroup());
        comandos.put("#", new SelectGroup());
        
    }
    class NewGroup implements iAcao{
        public void execute() throws Exception{
        /*exemplo: !createGroup nome_do_grupo*/
            String[] s = chat.getMensagem().split(" ");
            setNome(s[1]);
            chat.getChannel().exchangeDeclare("#"+getNome(), "fanout");
            chat.getChannel().queueBind("@"+chat.getUsuario().getNome(), "#" + Grupo.this.getNome(), "");
            System.out.println("CreateGroup");
        }
    }
    class AddUser implements iAcao{
        public void execute() throws Exception{
            String[] s = chat.getMensagem().split(" ");
            
            chat.setDestinatario("@"+s[1]);
            setNome(s[2]);
            chat.getChannel().queueBind(chat.getDestinatario(), "#" + Grupo.this.getNome(), "");//Adcionar o usuário a um grupo
            System.out.println(chat.getDestinatario()+ " adcionando ao grupo " + Grupo.this.getNome());
            
        }
    }
    class RemoveUserGroup implements iAcao{
        public void execute() throws Exception{
            String[] s = chat.getMensagem().split(" ");
            String destinatario = s[1];
            String nome_do_grupo = s[2];
            chat.getChannel().queueUnbind("@"+destinatario, "#"+nome_do_grupo, "");
            // chat.getChannel().queueUnbind("@"+chat.getUsuario().getNome(), "#"+getNome(), "");
            System.out.println("usuario: "+destinatario);
            System.out.print(">>");    
        }
    }
    class DeleteGroup implements iAcao{
        public void execute() throws Exception{
            String[] s = chat.getMensagem().split(" ");
            String nome_do_grupo = s[1];
            chat.getChannel().exchangeDelete( "#"+nome_do_grupo);
            // chat.getChannel().queueUnbind("@"+chat.getUsuario().getNome(), "#"+getNome(), "");
            System.out.println("grupo: "+nome_do_grupo);
            System.out.print(">>");   
        }
    }
    class SelectGroup implements iAcao{
        public void execute() throws Exception{
            chat.setDestinatario(chat.getMensagem());
            setNome(chat.getDestinatario().substring(1));
            chat.getChannel().queueBind("@"+chat.getUsuario().getNome(), "#" + Grupo.this.getNome(), "");
        }
    // class EnviarGrupo implemnts iAcao{
    //     public void execute() throws Exception{
    //         String nome_grupo1 = chat.getDestinatario();
    //         setNome(nome_do_grupo1.substring(1));
    //         System.out.println("usuario: "+"@"+chat.getUsuario().getNome());
    //         System.out.println("grupo: "+nome_do_grupo);
    //         chat.getChannel().queueBind("@"+chat.getUsuario().getNome(), nome_do_grupo1, "");
    //     }
    // } Acho que nao é aqui ;-)
    }
}