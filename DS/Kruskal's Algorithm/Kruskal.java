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

class EdgeNode {
	Edge e;
	EdgeNode next;
	
	EdgeNode() {
		e = null;
		next = null;
	}
	
	EdgeNode(Edge newedge) {
		e = newedge;
		next = null;
	}
}

class Graph {
	EdgeNode list;
	Graph next;
	NodePool nodepool;
	
	Graph(int maxnodes) {
		list = null;
		next = null;
		nodepool = new NodePool(maxnodes);
	}
	
	void display() {
		EdgeNode e = list;
		while(e!=null) {
			e.e.display();
			e = e.next;
		}
	}
	
	void add(Edge newedge) {
		Graph graphkey = this;
		EdgeNode e = new EdgeNode(newedge);
		EdgeNode edgekey = list;
		boolean addtonewgraph = true;
		do {
			//check every graph:
			if(graphkey.nodepool.hasBothNodes(newedge)) {
				//current graph itself has both edges, must be discarded;
				System.out.print("Discarded: ");
				newedge.display();
				addtonewgraph = false;
				break;
			}
			else if(graphkey.nodepool.hasOneNode(newedge)) {
				//check if two graphs have same node, if so, join the two graphs;
				//if only one graph has this single node, add the edge to that graph;
				Graph g = graphkey.next;
				Graph prev = graphkey;
				boolean found = false;
				while(g!=null) {
					if(g.nodepool.hasOneNode(newedge)) {
					 found = true;
					 break;
					}
					prev = g;
					g = g.next;
				}
				if(found) {
					graphkey.nodepool.addGraphNodes(g);
					//connect the graphs if the newedge nodes are connecting two graphs;
					edgekey = graphkey.list;
					while(edgekey.next!=null) {
						edgekey = edgekey.next;
					}
					edgekey.next = g.list;
					if(g.next!=null) {
						//delete g:
						prev.next = g.next;
					}
					else {
						prev.next = null;
					}
					graphkey.nodepool.add(newedge);
				}
				
				while(edgekey.next!=null) {
					edgekey = edgekey.next;
				}
				edgekey.next = e;
				graphkey.nodepool.add(newedge);
				addtonewgraph = false;
				break;
			}
			if(graphkey!=null) {
				graphkey = graphkey.next;
			}
		} while(graphkey!=null);
		if(addtonewgraph) {
			Graph g = new Graph(nodepool.node.length);
			g.list = e;
			graphkey = next;
			if(list==null) {
				list = e;
				nodepool.add(newedge);
			}
			else if(graphkey==null) {
				next = g;
				g.nodepool.add(newedge);
			}
			else {
				while(graphkey.next!=null) {
					graphkey = graphkey.next;
				}
				graphkey.next = g;
				g.nodepool.add(newedge);
			}
		}
	}
}

class MST {
	Graph list;
	NodePool nodepool;
	
	MST(int maxnodes) {
		list = null;
		nodepool = new NodePool(maxnodes);
	}
	
	MST makeMST(Edge e[], int maxnodes) {
		MST mst = new MST(maxnodes);
		Graph g = new Graph(maxnodes);
		mst.list = g;
		for(int i=0;i<e.length;i++) {
			g.add(e[i]);
		}
		return mst;
	}
	
	void display() {
		list.display();
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
	
	void addGraphNodes(Graph g) {
		EdgeNode key = g.list;
		int i;
		boolean added1 = false, added2 = false;
		while(key!=null) {
			add(key.e);
			key = key.next;
		}
	}
}

class Kruskal {
	public static void main(String args[]) {
		Scanner scan = new Scanner(System.in);
		int i,n;
		Edge e[];
		NodePool nodepool;
		MST mst;
		
		System.out.println("Enter number of nodes:");
		n = scan.nextInt();
		nodepool = new NodePool(n);
		mst = new MST(n);
		System.out.println("Enter the number of edges:");
		n = scan.nextInt();
		e = new Edge[n];
		
		for(i=0;i<n;i++) {
			System.out.println("Enter information of edge number "+(i+1)+":");
			e[i] = new Edge();
			nodepool.add(e[i]);
		}
		sort(e);
		mst = mst.makeMST(e, nodepool.node.length);
		System.out.println("Minimum Spanning Tree contains:");
		mst.display();
	}
	
	static void sort(Edge e[]) {
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