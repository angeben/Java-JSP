import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import app.interfaces.Car;

public class App {

	public static void main(String[] args) {
		// Inversion of control -> Spring manages the creation of the object
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		Car myCar = context.getBean("sandero", Car.class);
		myCar.specs();
		context.close();
	}

}
