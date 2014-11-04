package br.unifor.ads.redes.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class Server extends Thread {

	public static Vector<PrintStream> Clientes;
	private static ArrayList<String> listaNomes = new ArrayList<String>();
	private Socket conexao;
	private String nomeCliente;

	public Server(Socket socket) {
		this.conexao = socket;
	}

	public boolean armazena(String newName) {
		for (int i = 0; i < listaNomes.size(); i++) {
			if (listaNomes.get(i).equals(newName))
				return true;
		}
		listaNomes.add(newName);
		return false;
	}

	public void remove(String oldName) {
		for (int i = 0; i < listaNomes.size(); i++) {
			if (listaNomes.get(i).equals(oldName))
				listaNomes.remove(oldName);
		}
	}

	public static void main(String[] args) {
		Clientes = new Vector<PrintStream>();
		try {
			ServerSocket server = new ServerSocket(5555);
			System.out.println("Servidor rodando na porta 5555");
			while (true) {
				Socket conexao = server.accept();
				Thread t = new Server(conexao);
				t.start();
			}
		} catch (Exception e) {
			System.out.println("IOException: " + e);
		}
	}

	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(
					this.conexao.getInputStream()));
			PrintStream saida = new PrintStream(this.conexao.getOutputStream());
			this.nomeCliente = entrada.readLine();
			if (armazena(this.nomeCliente)) {
				saida.println("Este nome já existe! Conecte novamente com outro nome.");
				Clientes.add(saida);
				this.conexao.close();
				return;
			} else {
				System.out.println(this.nomeCliente
						+ " : Conectado ao Servidor!");
			}

			if (this.nomeCliente == null) {
				return;
			}

			Clientes.add(saida);
			String msg = entrada.readLine();
			while (msg != null && !(msg.trim().equals(""))) {
				sendToAll(saida, " escreveu: ", msg);
				msg = entrada.readLine();
			}
			System.out.println(this.nomeCliente + " saiu do bate-papo!");
			sendToAll(saida, " saiu", " do bate-papo!");
			remove(this.nomeCliente);
			Clientes.remove(saida);
			this.conexao.close();
		} catch (Exception e) {
			System.out.println("Falha na Conexão... .. ." + " IOException: "
					+ e);
		}
	}

	private void sendToAll(PrintStream saida, String acao, String msg)
			throws IOException {
		Enumeration<PrintStream> e = Clientes.elements();
		while (e.hasMoreElements()) {
			PrintStream chat = (PrintStream) e.nextElement();
			if (chat != saida) {
				chat.println(this.nomeCliente + acao + msg);
			}
		}
	}
}
