//PROG2 VT2024, Inlämningsuppgift, del 2
//Grupp 025
//Olle Lindkvist olli5947
//Mellissa Qholizadeh meqho4654
import java.util.Objects;

public class Edge<N> {
	private final N source;
	private final N destination;
	private final String name;
	private int weight;
	
	public Edge(N source, N destination, String name, int weight) {
		if(weight<0) {
			throw new IllegalArgumentException("Vikten kan ej vara negativ");
		}
		this.source = source;
        this.destination = destination;
        this.name = name;
        this.weight = weight;
	}
	
	//Returnerar den nod som kanten pekar från
		public N getSource() {
			return source;
		}
	
	//Returnerar den nod som kanten pekar till
	public N getDestination() {
		return destination;
	}
	
	//Returnerar den aktuella kantens vikt
	public int getWeight() {
		return weight;
	}
	
	//Sätter kantens vikt till ett angivet heltal
	public void setWeight(int weight) {
		if(weight<0) {
			throw new IllegalArgumentException("Vikten kan ej vara negativ");
		}
		this.weight = weight;
	}
	
	//Returnerar kantens namn
	public String getName() {
		return name;
	}

	//Returnera en sträng med information om kanten
	@Override
	public String toString() {
		return name + ": " + source + " -> " + destination + " time: " + weight;
	}

	//Edge måste ha equals för att grafens kanter är bidirektionella, hantera kanter i båda riktningar
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    Edge<?> edge = (Edge<?>) o;
	    // Kontrollera att source och destination, samt namn och vikt är lika
	    return (Objects.equals(source, edge.source) && Objects.equals(destination, edge.destination) ||
	            Objects.equals(source, edge.destination) && Objects.equals(destination, edge.source)) &&
	           Objects.equals(name, edge.name) &&
	           weight == edge.weight;
	}
	
	@Override
	public int hashCode() {
	    // Se till att hash-koden tar hänsyn till båda riktningarna av kanten
	    return Objects.hash(
	        Math.min(source.hashCode(), destination.hashCode()), 
	        Math.max(source.hashCode(), destination.hashCode()), 
	        name, 
	        weight
	    );
	}
	
}
