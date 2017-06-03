import java.util.*;

class Edge {
	String node1, node2;
	int weight;
	
	Edge() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter node 1 and node 2:");
		node1 = scan.nextLine().toUpperCase();
		node2 = scan.nextLine().toUpperCase();
		scan = new Scanner(System.in);
		System.out.println("Enter weight of the edge:");
		weight = scan.nextInt();
	}
	
	void display() {
		System.out.println(node1+"-"+node2+": Weight = "+weight);
	}
}

class NodePool {
	String node[];
	int numnodes;
	
	NodePool(int n) {
		node = new String[n];
		for(int i=0;i<n;i++) {
			node[i] = new String();
		}
		numnodes=0;
	}
	
	void add(Edge e) {
		boolean node1found=false, node2found=false;
		for(int i=0;i<numnodes;i++) {
			if(node[i].equals(e.node1)) {
				node1found = true;
			}
			if(node[i].equals(e.node2)) {
				node2found = true;
			}
		}
		if(!node1found) {
			node[numnodes++] = e.node1;
		}
		if(!node2found) {
			node[numnodes++] = e.node2;
		}
	}
	
	void showNodePool() {
		for(int i=0;i<numnodes;i++) {
			System.out.print(node[i]+" ");
		}
		System.out.println();
	}
	
	boolean hasBothNodes(Edge e) {
		boolean node1found=false, node2found=false;
		for(int i=0;i<numnodes;i++) {
			if(node[i].equals(e.node1)) {
				node1found = true;
			}
			if(node[i].equals(e.node2)) {
				node2found = true;
			}
		}
		if(node1found&&node2found) {
			return true;
		}
		return false;
	}
	
	boolean hasOneNode(Edge e) {
		boolean node1found=false, node2found=false;
		for(int i=0;i<numnodes;i++) {
			if(node[i].equals(e.node1)) {
				node1found = true;
			}
			if(node[i].equals(e.node2)) {
				node2found = true;
			}
		}
		if(node1found||node2found) {
			return true;
		}
		return false;
	}
	
	boolean fullPool() {
		if(numnodes==node.length) {
			return true;
		}
		return false;
	}
}

class MST {
	Edge e[];
	int numedges;
	NodePool nodepool;
	
	MST(int m, int n) {
		int i;
		numedges = n;
		e = new Edge[m];
		nodepool = new NodePool(n);
		for(i=0;i<m;i++) {
			System.out.println("Enter the information of edge "+(i+1)+":");
			e[i] = new Edge();
			nodepool.add(e[i]);
		}
		sort();
		solve();
		display();
	}
	
	void solve() {
		int i, j=0;
		NodePool soln = new NodePool(nodepool.node.length);
		Edge temp[] = new Edge[e.length];
		numedges = 0;
		temp[j++] = e[0];
		numedges++;
		soln.add(e[0]);
		while(!soln.fullPool()) {
			for(i=0;i<nodepool.node.length;i++) {
				if(soln.hasOneNode(e[i])&&!soln.hasBothNodes(e[i])) {
					soln.add(e[i]);
					temp[j++] = e[i];
					numedges++;
					break;
				}
			}
		}
		e = temp;
	}
	
	void display() {
		int i;
		System.out.println("MST contains:");
		for(i=0;i<numedges;i++) {
			e[i].display();
		}
	}
	
	void sort() {
		int i,j,index;
		Edge temp = e[0];
		for(i=0;i<e.length;i++) {
			index = i;
			temp = e[i];
			for(j=i+1;j<e.length;j++) {
				if(e[j].weight<temp.weight) {
					index = j;
					temp = e[j];
				}
			}
			if(index!=i) {
				e[index]=e[i];
				e[i]=temp;
			}
		}
	}
}

class Prim {
	public static void main(String args[]) {
		Scanner scan = new Scanner(System.in);
		int m,n;
		MST mst;
		
		System.out.println("Enter the number of edges:");
		m = scan.nextInt();
		System.out.println("Enter the number of nodes:");
		n = scan.nextInt();
		mst = new MST(m,n);
	}
}