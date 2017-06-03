import java.util.*;
import java.io.*;
import java.net.*;

public class Group implements Serializable {
	private InetAddress address;
	private String name;
	private List<MulticastSocket> memberList;
	
	public Group(InetAddress address, String name) {
		this.address = address;
		this.name = name;
		memberList = new LinkedList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address.getHostAddress();
	}
	
	public void addToGroup(MulticastSocket s) {
		memberList.add(s);
	}
	
	public void removeFromGroup(MulticastSocket s) {
		memberList.remove(s);
	}
	
	public void deleteGroup() {
		for(MulticastSocket s : memberList) {
			removeFromGroup(s);
		}
	}
}