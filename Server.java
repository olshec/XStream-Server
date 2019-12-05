package pr4;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;


public class Server {

	private static ArrayList<Task> masTask=new ArrayList<Task>(0);
	private static ArrayList<User> masUser;

	public static Info changeTaskState(String nameTask, String stateTask, String nameUser) {
		for (int i = 0; i < masTask.size(); i++) {
			if (masTask.get(i).getNameUser() == nameUser && masTask.get(i).getNameTask() == nameTask) {
				masTask.get(i).setState(stateTask);
				return new Info(true, "Статус задачи изменен!");
			}
		}
		return new Info(false, "Невозможно выполнить операцию: задача не найдена.");
	}

	public static Info changeTaskWorker(String nameTask, String loginSelf, String loginTarget) {
		for (int i = 0; i < masTask.size(); i++) {
			if (masTask.get(i).getNameUser() == loginSelf && masTask.get(i).getNameTask() == nameTask) {
				masTask.get(i).setNameUser(loginTarget);
				;
				return new Info(true, "Статус задачи изменен!");
			}
		}
		return new Info(false, "Невозможно выполнить операцию: задача не найдена.");
	}

	public static Info getTasks(String nameUser) {
		ArrayList<Task> masUserTask = new ArrayList<Task>(0);
		for (int i = 0; i < masTask.size(); i++) {
			if (masTask.get(i).getNameUser() == nameUser) {
				masUserTask.add(masTask.get(i));
			}

		}
		return new Info(true, masUserTask);
	}

	public static Info getAllUsers() {
		return new Info(true, masUser);
	}

	public static Info addTask(Task task) {
		String nameTask = task.getNameTask();
		for (int i = 0; i < masTask.size(); i++) {
			if (masTask.get(i).getNameTask() == nameTask)
				return new Info(false, "Задача не может быть добавлена, потому что задача с таким именем уже имеется!");
		}
		masTask.add(task);
		return new Info(true, "Задача успешно добавлена!");
	}
	
	/*
	 * 
	 */
	public static Info getStringFromXML(String str) {
		XStream xstream = new XStream();
		// Allow types for Info
		xstream.allowTypes(new String[] {"pr4.Info"});
		return (Info)xstream.fromXML(str);
	}
	
	public static Info actionMain(Info inf) {
		switch (inf.getMessage()) {
		case "get list tasks":
			return getTasks(inf.getLogin());
			//break;
		case "get all users":
			return getAllUsers();
		default:
			return new Info(false,"Неверная комманда!");
		}
	}
	
	public static String serializeInfoToXML(Info inf) {
		XStream xstream = new XStream();
		String xml = xstream.toXML(inf);
		return xml;
	}
	
	public static void sendRequest(String str, Socket s) {

		try {
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			dout.writeUTF(str);
			dout.flush();
			dout.close();
			
		} catch (

		Exception e) {
			System.out.println(e);
		}
	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Сервер запущен!");
		Info inf = new Info(true,"");
		try {
			ServerSocket ss = new ServerSocket(6666);
			String str = "";
			while (!inf.getMessage().equals("end")) {
				Socket s = ss.accept();// establishes connection
				DataInputStream dis = new DataInputStream(s.getInputStream());
				
				str = (String) dis.readUTF();
				inf = getStringFromXML(str);
				System.out.println("Сообщение клиента: " + inf.getMessage());
				
				Info infResponse = actionMain(inf) ;
				infResponse.setMessage("result: all tasks");
				sendRequest(serializeInfoToXML(infResponse),s);
				s.close();
			}
			System.out.println("Сервер остановлен!");
			ss.close();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Сервер остановлен!");
		}
		
		

	}

}
