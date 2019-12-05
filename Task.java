package pr4;

public class Task {
	private String nameTask;
	private String description;
	private String nameUser;
	private String state;
	
	
	public Task(String nameTask, String description, 
			String nameUser, String state) {
		setNameTask(nameTask);
		setDescription(description);
		setNameUser(nameUser);
		setState(state);
	}
	
	public String getNameTask() {
		return nameTask;
	}
	public void setNameTask(String nameTask) {
		this.nameTask = nameTask;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNameUser() {
		return nameUser;
	}
	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
}
