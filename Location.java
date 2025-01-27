
import java.util.Objects;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
//PROG2 VT2024, Inlämningsuppgift, del 2
//Grupp 025
//Olle Lindkvist olli5947
//Mellissa Qholizadeh meqho4654
public class Location extends Circle{
    private String name;

    public Location(double x, double y, double radius, Paint color, String name) {
    	super(x, y, radius);
    	this.setFill(color);
        this.name = name;
        this.setId(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.setId(name); 
    }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        if(location.getName() == this.name)
        	return true;
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
