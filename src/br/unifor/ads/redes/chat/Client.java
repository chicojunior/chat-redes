package br.unifor.ads.redes.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client extends Thread {

	private Socket conexao;

	public Client(Socket socket) {
		this.conexao = socket;
	}

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("127.0.0.1", 5555);
			PrintStream saida = new PrintStream(socket.getOutputStream());
			BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Digite seu nome: ");
			String meuNome = teclado.readLine();
			saida.println(meuNome);

			Thread thread = new Client(socket);
			thread.start();
			String msg;
			while (true) {
				System.out.print("Mensagem > ");
				msg = teclado.readLine();
				saida.println(msg);
			}
		} catch (IOException e) {
			System.out.println("Falha na conexão... .. ." + " IOException: " + e);
		}
	}

	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(
					this.conexao.getInputStream()));
			String msg;
			while (true) {
				msg = entrada.readLine();
				if (msg == null) {
					System.out.println("Conexão encerrada!");
					System.exit(0);
				}
				System.out.println();
				System.out.println(msg);
				System.out.println("Responder >");
			}
		} catch (Exception e) {
			System.out.println("Ocorreu uma falha... .. ." + " IOException: " + e);
		}
	}
}
