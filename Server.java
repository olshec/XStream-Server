package pr4;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

public class Server {

	private static ArrayList<Task> masTask = new ArrayList<Task>(0);
	private static ArrayList<User> masUser = new ArrayList<User>(0);

	public static void addUsers() {
		for (int i = 0; i < 10; i++) {
			masUser.add(new User("log" + i, "pas" + i));
		}
	}

	public static void addTasks() {
		for (int i = 0; i < 10; i++) {
			masTask.add(new Task("task" + i, "desc" + i, "log" + i, "" + (45 + i)));
		}
	}

	public static boolean checkUserPermission(String nameUser, String password, String nameTask) {

		for (int j = 0; j < masUser.size(); j++) {
			if (masUser.get(j).getLogin().equals(nameUser) && masUser.get(j).getPassword().equals(password)) {
				for (int i = 0; i < masTask.size(); i++) {
					if (masTask.get(i).getNameUser().contentEquals(nameUser)
							&& masTask.get(i).getNameTask().equals(nameTask))
						return true;
				}
			}
		}
		return false;

	}

	public static boolean existsUser(String nameUser) {
		for (int j = 0; j < masUser.size(); j++)
			if (masUser.get(j).getLogin().equals(nameUser))
				return true;
		return false;
	}

	public static boolean checkUserPermission(String nameUser, String password) {

		for (int j = 0; j < masUser.size(); j++)
			if (masUser.get(j).getLogin().equals(nameUser) && masUser.get(j).getPassword().equals(password))
				return true;
		return false;
	}

	public static Info authentication(Info inf) {
		if (checkUserPermission(inf.getLogin(), inf.getPassword()))
			return new Info(true, "authentication: ok");
		return new Info(false, "authentication: failed");
	}
	
	public static Info changeTaskState(Info inf) {

		Task t1 = (Task) inf.getResultObject();
		String nameTask = t1.getNameTask();
		String stateTask = t1.getState();
		// String nameUser = inf.getLogin();

		for (int i = 0; i < masTask.size(); i++) {
			if (checkUserPermission(inf.getLogin(), inf.getPassword(), nameTask)) {

				masTask.get(i).setState(stateTask);
				return new Info(true, "result: state changed");
			}
		}

		return new Info(false, "result: operation failed");
	}

	public static Info changeTaskOwner(Info inf) {

		Task t1 = (Task) inf.getResultObject();
		String nameTask = t1.getNameTask();
		// String loginSelf = inf.getLogin();
		String loginTarget = t1.getNameUser();
		if (existsUser(loginTarget)) {
			if (checkUserPermission(inf.getLogin(), inf.getPassword(), nameTask)) {
				for (int i = 0; i < masTask.size(); i++) {
					if (masTask.get(i).getNameUser().equals(inf.getLogin())
							&& masTask.get(i).getNameTask().equals(nameTask)) {
						masTask.get(i).setNameUser(loginTarget);
						return new Info(true, "result: owner changed");
					}
				}
			}
		} else
			return new Info(false, "result: operation failed. User not exist.");

		return new Info(false, "result: operation failed");

	}

	public static Info getTasks(Info inf) {
		ArrayList<Task> masUserTask = new ArrayList<Task>(0);
		for (int i = 0; i < masTask.size(); i++) {
			if (checkUserPermission(inf.getLogin(), inf.getPassword())
					&& masTask.get(i).getNameUser().equals(inf.getLogin())) {
				masUserTask.add(masTask.get(i));
			}
		}
		return new Info(true, "result: all tasks", masUserTask);
	}

	public static Info getListUsers(Info inf) {
		if (checkUserPermission(inf.getLogin(), inf.getPassword()))
			return new Info(true, "result: all users", masUser);
		return new Info(false, "result: operarion failed");
	}

	public static Info addTask(Info inf) {
		Task task = (Task) inf.getResultObject();
		// String nameTask = task.getNameTask();
		for (int i = 0; i < masTask.size(); i++) {
			if (checkUserPermission(inf.getLogin(), inf.getPassword())) {
				masTask.add(task);
				return new Info(true, "result: task added");
			}
		}
		return new Info(false, "result: operation failed");
	}

	/*
	 * 
	 */
	public static Info getStringFromXML(String str) {
		XStream xstream = new XStream();
		// Allow types for Info
		xstream.allowTypes(new String[] { "pr4.Info","pr4.User" });
		return (Info) xstream.fromXML(str);
	}

	public static Info actionMain(Info inf) {
		switch (inf.getMessage()) {
		case "get list tasks":
			return getTasks(inf);
		// break;
		case "add new task":
			return addTask(inf);
		case "change owner":
			return changeTaskOwner(inf);
		case "change state":
			return changeTaskState(inf);
		case "get list users":
			return getListUsers(inf);
		case "authentication":
			return authentication(inf);
//		case "get all users":
//			return getAllUsers();

		default:
			return new Info(false, "result: command not found");
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
		addUsers();
		addTasks();
		// TODO Auto-generated method stub
		System.out.println("Сервер запущен!");
		Info inf = new Info(true, "");
		try {
			ServerSocket ss = new ServerSocket(6666);
			String str = "";
			while (!inf.getMessage().equals("end")) {
				Socket s = ss.accept();// establishes connection
				DataInputStream dis = new DataInputStream(s.getInputStream());

				str = (String) dis.readUTF();
				inf = getStringFromXML(str);
				System.out.println("Сообщение клиента: " + inf.getMessage());

				Info infResponse = actionMain(inf);

				sendRequest(serializeInfoToXML(infResponse), s);
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
