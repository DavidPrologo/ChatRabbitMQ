package br.ufs.dcomp.ChatRabbitMQ;
import java.util.HashMap;

public class Grupo{
    
    private Chat chat;
    private String nome;
    
    public Grupo(Chat chat){
        this.chat = chat;
        this.nome = "";
    }
    public void setNome(String nome){
        this.nome = nome;
        chat.setDestinatario(nome);
    }
    public String getNome(){
        return (this.nome);
    }
    public String getNomeEnv(){
        char sinal = this.nome.charAt(0);
        return (sinal+"Env"+this.nome.substring(1));
    }
    public void povoar(HashMap comandos){
        comandos.put("!newGroup", new NewGroup());
        comandos.put("!addUser", new AddUser());       
        comandos.put("!delFromGroup",  new DelFromGroup());
        comandos.put("!removeGroup", new RemoveGroup());
        comandos.put("#", new SelectGroup());
        
    }
    class NewGroup implements iAcao{
        public void execute() throws Exception{
        /*exemplo: !newGroup nome_do_grupo*/
            String[] s = chat.getMensagem().split(" ");
            setNome("#"+s[1]);
            
            chat.getChannel().exchangeDeclare(getNome(), "fanout");
            chat.getChannel().queueBind(chat.getUsuario().getNome(), Grupo.this.getNome(), "");
           
            chat.getChannelArq().exchangeDeclare(getNomeEnv(), "fanout");
            chat.getChannelArq().queueBind(chat.getUsuario().getNomeEnv(), Grupo.this.getNomeEnv(), "");
            
            System.out.println("CreateGroup");
        }
    }
    class AddUser implements iAcao{
        // exemplo: !addUser nome_usuario nome_grupo
        public void execute() throws Exception{
            String[] s = chat.getMensagem().split(" ");
            String usuario = s[1];
            String grupo = s[2];
            if(("@"+usuario).equals(chat.getUsuario().getNome())){
                Grupo.this.setNome("#"+grupo);
            }
            chat.getChannel().queueBind("@"+usuario, "#"+grupo, "");
            chat.getChannelArq().queueBind("@Env"+usuario, "#Env"+grupo, "");//Adcionar o usuário a um grupo
            
            System.out.println(chat.getDestinatarioEnv()+ " adcionando ao grupo " + Grupo.this.getNome());
            
        }
    }
    class DelFromGroup implements iAcao{
        public void execute() throws Exception{
            String[] s = chat.getMensagem().split(" ");
            String usuario = s[1];
            String grupo = s[2];
            if(("@"+usuario).equals(chat.getUsuario().getNome())){
                Grupo.this.setNome("");
            }
            chat.getChannel().queueUnbind("@"+usuario, "#"+grupo, "");
            chat.getChannelArq().queueUnbind("@Env"+usuario, "#Env"+grupo, "");
            
            System.out.println("usuario: "+usuario);
            System.out.print(">>");    
        }
    }
    class RemoveGroup implements iAcao{
        public void execute() throws Exception{
            String[] s = chat.getMensagem().split(" ");
            String grupo = s[1];
            if(("#"+grupo).equals(Grupo.this.getNome())){
                Grupo.this.setNome("");
            }
            
            chat.getChannel().exchangeDelete( "#"+grupo);
            chat.getChannelArq().exchangeDelete( "#Env"+grupo);
            
            System.out.println("grupo: "+grupo);
            System.out.println(">>");   
        }
    }
    class SelectGroup implements iAcao{
        public void execute() throws Exception{
            System.out.println(chat.getMensagem());
            setNome(chat.getMensagem());
            
            chat.getChannel().queueBind(chat.getUsuario().getNome(), Grupo.this.getNome(), "");
            chat.getChannelArq().queueBind(chat.getUsuario().getNomeEnv(), Grupo.this.getNomeEnv(), "");
        }
    }
    
}