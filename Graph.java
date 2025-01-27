import java.util.Collection;
import java.util.List;
import java.util.Set;
//PROG2 VT2024, Inlämningsuppgift, del 2
//Grupp 025
//Olle Lindkvist olli5947
//Mellissa Qholizadeh meqho4654
public interface Graph<T> {

    void add(T node);
    
    void connect(T node1, T node2, String name, int weight);
    
    void setConnectionWeight(T node1, T node2, int weight);
    
    Set<T> getNodes();
    
    Collection<Edge<T>> getEdgesFrom(T node);
    
    Edge<T> getEdgeBetween(T node1, T node2);
    
    void disconnect(T node1, T node2);
    
    void remove(T node);
    
    boolean pathExists(T from, T to);
    
    List<Edge<T>> getPath(T from, T to);
}
