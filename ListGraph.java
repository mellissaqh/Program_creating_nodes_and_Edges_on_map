import java.util.*;

//PROG2 VT2024, Inlämningsuppgift, del 2
//Grupp 025
//Olle Lindkvist olli5947
//Mellissa Qholizadeh meqho4654
public class ListGraph<N> implements Graph<N>{
	private Map<N, ArrayList<Edge<N>>> kopplingsLista = new HashMap<>();
	
	//För att lagra nodernas x och y koordinationer skapas dessa
	private Map<N, Double> nodeXCoordinates; 
    private Map<N, Double> nodeYCoordinates;  
    
    // För att se markerade platser
    private List<N> markedPlaces;
	
	public ListGraph() {
	   this.nodeXCoordinates = new HashMap<>();
       this.nodeYCoordinates = new HashMap<>();
       this.markedPlaces = new ArrayList<>();
	}

	
	@Override
	public void add(N node) {
	kopplingsLista.putIfAbsent(node,new ArrayList<>());
	}

	@Override
	public void connect(N node1, N node2, String name, int weight) {		    
		//Kontrollera först om båda noderna finns i grafen 
		if(!kopplingsLista.containsKey(node1) || !kopplingsLista.containsKey(node2)) {
			throw new NoSuchElementException("En eller båda noderna finns inte i grafen");
		}
		//Kontrollera om vikten är negativ
		if(weight<0) {
			throw new IllegalArgumentException("Vikten kan ej vara negativ!");
		}

		if(!hasEdge(node1, node2, name)) {
			
			//Här skapas två kanter mellan noderna
			 Edge<N> edge1 = new Edge<N>(node1, node2 , name, weight);
			 Edge<N> edge2 = new Edge<N>(node2, node1, name, weight);
			
			 //Här lägger vi till kanterna i grafen
			 kopplingsLista.get(node1).add(edge1);
			 kopplingsLista.get(node2).add(edge2);
		}

	}
	
	//Skapar en metod för att kunna setta koordinater
	 public void setNodeCoordinates(N node, double x, double y) {
			try {
		        nodeXCoordinates.put(node, x);
		        nodeYCoordinates.put(node, y);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		 

	    }
		   
	 //För att undvika dubbletter av kanter
	 public boolean hasEdge(N node1, N node2, String edgeName) {
		    
		    for (Edge<N> edge : kopplingsLista.getOrDefault(node1, new ArrayList<>())) {
		        if (edge.getDestination().equals(node2) && edge.getName().equals(edgeName)) {
		            return true;
		        }
		    }
		    return false;
		}
	 
	 //Skapar en metod för att få nodenamn
	 public N getNodeByName(String name) {
		    for (N node : kopplingsLista.keySet()) {
		        if (node instanceof Location) {
		            Location location = (Location) node;
		            if (location.getName().equals(name)) {
		                return node;
		            }
		        }
		    }
		    return null; 
		}

	@Override
	public void setConnectionWeight(N node1, N node2, int weight) {
	//Kontrollera först om båda noderna finns i grafen 
	if(!kopplingsLista.containsKey(node1) || !kopplingsLista.containsKey(node2)) {
		throw new NoSuchElementException("En eller båda noderna finns inte i grafen");
	}
	//Här kontrolleras det om det finns en kant mellan noderna
	Edge<N> edge1 = getEdgeBetween(node1, node2);
	Edge<N> edge2 = getEdgeBetween(node2, node1);
	if(edge1 == null||edge2 == null) {
		throw new IllegalStateException("Det finns ingen kant mellan noderna");
	}
	//Kontrollera om vikten är negativ
	if(weight<0) {
		throw new IllegalArgumentException("Vikten kan ej vara negativ!");
	}
	edge1.setWeight(weight);
    edge2.setWeight(weight);
		
	}

	@Override
	public Set<N> getNodes() {   
        return new HashSet<>(kopplingsLista.keySet());    
    }
	

	@Override
	public Collection<Edge<N>> getEdgesFrom(N node) {
		 // Kontrollera om noden finns i grafen
	    if (!kopplingsLista.containsKey(node)) {
	        throw new NoSuchElementException("Noden finns inte i grafen!");
	    }
	    //Returnera en kopia av samlingen av edges från den angivna noden
	    List<Edge<N>> edges = kopplingsLista.get(node);
	    
	    //Vet inte riktigt vilken datastruktur som ska returna, testar med ArrayList
		return new ArrayList<>(edges);
	}
	
    // Metod för att hämta x-koordinaten för en nod
    public double getNodeX(N node) {
        if (!nodeXCoordinates.containsKey(node)) {
            throw new NoSuchElementException("No coordinates set for node: " + node);
        }
        return nodeXCoordinates.getOrDefault(node, 0.0);
    }

    // Metod för att hämta y-koordinaten för en nod
    public double getNodeY(N node) {
        if (!nodeYCoordinates.containsKey(node)) {
            throw new NoSuchElementException("No coordinates set for node: " + node);
        }
        return nodeYCoordinates.getOrDefault(node, 0.0);
    }
    
    public N getNodeByCoordinates(double x, double y) {
    	//Hade lite problem med att hämta koordinaterna för nod, gjord med hjälp av chatGPT
        for (N node : kopplingsLista.keySet()) {
            double nodeX = getNodeX(node);
            double nodeY = getNodeY(node);
            double tolerance = 0.0001; 

            // Jämför x- och y-koordinaterna med tolerans
            if (Math.abs(nodeX - x) < tolerance && Math.abs(nodeY - y) < tolerance) {
                return node;
            }
        }
        return null;
    }

	@Override
	public Edge<N> getEdgeBetween(N node1, N node2) {
	//Kontrollera först om båda noderna finns i grafen 
	if(!kopplingsLista.containsKey(node1) || !kopplingsLista.containsKey(node2)) {
		throw new NoSuchElementException("En eller båda noderna finns inte i grafen");
	}	
    // Kontrollera kanter från node1 till node2
    for (Edge<N> edge : kopplingsLista.get(node1)) {
        if (edge.getDestination().equals(node2)) {
            return edge;
        }
    }

    // Kontrollera kanter från node2 till node1 (om grafen är oretad)
    for (Edge<N> edge : kopplingsLista.get(node2)) {
        if (edge.getDestination().equals(node1)) {
            return edge;
        }
    }
	//Om det inte hittas någon kant returnera null. 
		return null;
	}

	@Override
	public void disconnect(N node1, N node2) {
	//Kontrollera först om båda noderna finns i grafen 
	if(!kopplingsLista.containsKey(node1) || !kopplingsLista.containsKey(node2)) {
		throw new NoSuchElementException("En eller båda noderna finns inte i grafen");
	}
	//Här kontrolleras det om det finns en kant mellan noderna
	Edge<N> edge1 = getEdgeBetween(node1, node2);
	Edge<N> edge2 = getEdgeBetween(node2, node1);
	if(edge1 == null||edge2==null) {
		throw new IllegalStateException("Det finns ingen kant mellan noderna");
	}
	//Här tas kanten bort från noderna
	 kopplingsLista.get(node1).remove(edge1);
	 kopplingsLista.get(node2).remove(edge2);	
	}

	@Override
	public void remove(N node) {
		 // Kontrollera om noden finns i grafen
        if (!kopplingsLista.containsKey(node)) {
            throw new NoSuchElementException("Noden finns inte i grafen!");
        }
        
        kopplingsLista.remove(node);
        nodeXCoordinates.remove(node);
        nodeYCoordinates.remove(node);
        
        //För att ta bort kanterna som är kopplade till noden
        for(N otherNode:kopplingsLista.keySet()) {
        	//För varje nod hämtar vi edges från kopplingslistan, det ger oss alla kanter som är kopplade till den akuella noden(otherNode)
        	//Set eller List här
        	List<Edge<N>> edges = kopplingsLista.get(otherNode);
        	//Här tar vi bort alla kanter från listan edges där destinationen är samma som den node som ska tas bort
        	//Här använder vi även ett lambda uttryck, i det här fallet definerar vi ett villkor som specificerar vilka elemnt som ska bort. 
        	edges.removeIf(edge -> edge.getDestination().equals(node));
        }
		
	}

	//Hämta förbindelsens namn
    public String getConnectionName(N from, N to) {
        Edge<N> edge = getEdgeBetween(from, to);
        if (edge != null) {
            return edge.getName();
        }
        return null;
    }

    //Hämta förbindelsens tid/ vikt
    public int getConnectionTime(N from, N to) {
        Edge<N> edge = getEdgeBetween(from, to);
        if (edge != null) {
            return edge.getWeight();
        }
        return -1;
    }
    
	
	@Override
	public boolean pathExists(N from, N to) {	
		//Kontrollera först om båda noderna finns i grafen 
		if(!kopplingsLista.containsKey(from) || !kopplingsLista.containsKey(to)) {
			return false;
		}
		
		//Om det båda noderna finns, anropa depthFirstSearch för att koontrollera om det finns en väg
		return depthFirstSearch(from, to, new HashSet<>());
	}
	
	private boolean depthFirstSearch(N current, N target, Set<N> visited) {	
		//Här börjar vi med att kontrollera om current är lika med målnoden(target)
		if(current.equals(target)) {
			return true; 
		}
		//Här markeras den aktuella noden som visited om de inte är lika, då läggs den aktuella noden till i visited mängden
		visited.add(current);
	    
		//Gå igenom alla gränsande noder
	    for (Edge<N> edge : kopplingsLista.get(current)) {
	        N neighbor = edge.getDestination().equals(current) ? edge.getSource() : edge.getDestination();
	        if (!visited.contains(neighbor)) {
	            if (depthFirstSearch(neighbor, target, visited)) {
	                return true;
	            }
	        }
	    }
		//Om ingen väg hittas returneras false
		return false;
	}
	
	//Här skapas en till depthFirstSearch metod, men vi lägger till path för 
	private boolean depthFirstSearchPath(N current, N target, Set<N> visited, List<Edge<N>> path) {	
		//Här börjar vi med att kontrollera om current är lika med målnoden(target)
		//Har vi nått målnoden så returneras true
		if(current.equals(target)) {
			return true; 
		}
		//Här markeras den aktuella noden som visited om de inte är lika, då läggs den aktuella noden till i visited mängden
		visited.add(current);
		//Gå igenom alla gränsande noder
		for(Edge<N> edge : kopplingsLista.get(current)) {
			N neighbor = edge.getDestination();
			if(!visited.contains(neighbor)) {
				//Här läggs kanten till i vägen
				path.add(edge);
				//Här utförs en djupet först sökning  på varje gränsande nod som inte redan besökts
				if(depthFirstSearchPath(neighbor, target, visited, path)) {
					return true;
				}
				//Här tas kanten bort från vägen om det inte leder till målet
				path.remove(path.size()-1);
			}
		}
		//Om ingen väg hittas returneras false
		return false;
	}

	@Override
	public List<Edge<N>> getPath(N from, N to) {
	//Kontrollera först om båda noderna finns i grafen 
		if(!kopplingsLista.containsKey(from) || !kopplingsLista.containsKey(to)) {
			//Returnera en tom lista eller null
			return new ArrayList<>();
		}
		//Här skapas en lista för att hålla reda på vägen
		//Här skapas även en HashSet för att hålla reda på visited noder
		List<Edge<N>> path = new ArrayList<>();
		Set<N> visited = new HashSet<>();
		//Starta en djupet först sökning och returnera vägen om den hittas
		if(depthFirstSearchPath(from, to, visited, path)) {
			return path;
		} else { 
			//Returnerar en tom list eller null
			return new ArrayList<>();
		}
	
	}
	
	@Override
	public String toString() {
		//I denna implementation använder vi oss av en Stringbuilder
		StringBuilder sb = new StringBuilder();
		//Loopa igenom alla noderna i grafen med en foreach loop
		for(Map.Entry<N, ArrayList<Edge<N>>> entry: kopplingsLista.entrySet()) {
			N node = entry.getKey();
			List<Edge<N>> edges = entry.getValue();
			
			//Lägg till varje nod till strängen, System.lineseparator fungerar istället för att hårdkoda radbrytningar
			sb.append(node.toString()).append(System.lineSeparator());
			
			//Här loopas nodens kanter och läggs till i strängen 
			  for (Edge<N> edge : edges) {
		            sb.append("  ").append(edge.toString()).append(System.lineSeparator());
		      }
		}
		return sb.toString();
	}	
	
	//Nedan finns metoder som är kopplade till att ändra färg på noderna när man klickar på de och liknande.	
	// Metod för att markera en plats
    public void markPlace(N place) {
        markedPlaces.add(place);
    }

    // Metod för att avmarkera en plats
    public void unmarkPlace(N place) {
        markedPlaces.remove(place);
    }

    // Metod för att kontrollera om en plats är markerad
    public List<N> getMarkedPlace() {
        return new ArrayList<>(markedPlaces);
    }
    
    

  
	
	}


