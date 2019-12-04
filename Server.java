package pr4;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


import java.io.IOException;  

public class Server {

	private ArrayList<Task> masTask;
	private ArrayList<User> masUser;
	

	public Info changeTaskState(String nameTask, String stateTask, String nameUser) {
		for (int i = 0; i < masTask.size(); i++) {
			if (masTask.get(i).getNameUser() == nameUser && 
					masTask.get(i).getNameTask()==nameTask) {
				masTask.get(i).setState(stateTask);
				return new Info(true, "Статус задачи изменен!");
			}
		}
		return new Info(false, "Невозможно выполнить операцию: задача не найдена.");
	}
	
	public Info changeTaskWorker(String nameTask, String loginSelf, String loginTarget) {
		for (int i = 0; i < masTask.size(); i++) {
			if (masTask.get(i).getNameUser() == loginSelf && 
					masTask.get(i).getNameTask()==nameTask) {
				masTask.get(i).setNameUser(loginTarget);;
				return new Info(true, "Статус задачи изменен!");
			}
		}
		return new Info(false, "Невозможно выполнить операцию: задача не найдена.");
	}
	
	public Info getTasks(String nameUser) {
		ArrayList<Task> masUserTask = new ArrayList<Task>(0);
		for (int i = 0; i < masTask.size(); i++) {
			if (masTask.get(i).getNameUser() == nameUser) {
				masUserTask.add(masTask.get(i));
			}

		}
		return new Info(true, masUserTask);
	}

	
	public Info getAllUsers() {
		return new Info(true, masUser);
	}

	public Info addTask(Task task) {
		String nameTask = task.getNameTask();
		for (int i = 0; i < masTask.size(); i++) {
			if (masTask.get(i).getNameTask() == nameTask)
				return new Info(false, "Задача не может быть добавлена, потому что задача с таким именем уже имеется!");
		}
		masTask.add(task);
		return new Info(true, "Задача успешно добавлена!");
	}

	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Сервер запущен!");
		try {
			ServerSocket ss = new ServerSocket(6666);
			
			String str = "";
			while (!str.equals("end")) {
				Socket s = ss.accept();// establishes connection
				DataInputStream dis = new DataInputStream(s.getInputStream());
				str = (String) dis.readUTF();
				System.out.println("message = " + str);
			}
			System.out.println("Сервер остановлен!");
			ss.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
