import java.util.ArrayList;
import java.util.List;

public class Group {

	private List<String> members;
	private String groupName;
	

		
	/**
	 * Creates a new Instance of a group
	 * @param name  the name of the group
	 * @return instance of the new created group
	 * @author github.com/oliverrascheja
	 */
	public Group(String name) {
		groupName = name;
		members = new ArrayList<String>();
	}
	
	
	
	/**
	 * Adds a new Member to the group
	 * @param na  the user name to join the group
	 */
	public void addNewMember(String na) {
		members.add(na);
	}
	
	
	
	/**
	 * Removed a Member from the group
	 * @param na  the user name
	 */
	public void removeMember(String na) {
		members.remove(na);
	}
	
	
	
	/**
	 * Returns the name of the group
	 * @return name of the group
	 */
	public String getGroupName() {
		return groupName;
	}
	
	
	
	/**
	 * Returning a List of the members of the group
	 * @return member list of the group
	 */
	public List<String> getMemberList() {
		return members;
	}
	
	
	
	/**
	 * Cleaing and removing the group from the server
	 */
	public void dissolve() {
		members = null;
		groupName = null;
	}
	
}
