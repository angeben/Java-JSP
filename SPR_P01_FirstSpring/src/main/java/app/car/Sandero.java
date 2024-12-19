package app.car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import app.interfaces.Car;

@Component("sandero")
public class Sandero implements Car {

	// With required = false (default), Spring does not create the object if it is not necessary
	@Autowired(required = false)
	Engine engine;
	
	/*  
	 public Sandero(Engine engine){
	 	engine.type = "V6";
	 	this.engine = engine;
	 }
	 
	 public void setEngine(Engine engine){
	 	engine.type = "V10";
	 	this.engine = engine;
	 }	 
	*/
	
	@Override
	public void specs() {
		System.out.println("Dacia Sandero with engine as " + engine.type);

	}

}
