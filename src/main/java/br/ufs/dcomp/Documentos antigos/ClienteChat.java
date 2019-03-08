//package br.ufs.dcomp.ChatRabbitMQ;
import java.util.Scanner;

public class ClienteChat{
    
    public static void main(String []args){
        Scanner e = new Scanner(System.in);
        String c = "oit";
        while(!c.equals("")){
            System.out.println("digite");
            c = e.nextLine();
        }
    }
}