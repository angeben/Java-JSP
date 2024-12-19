package app.car;
import org.springframework.stereotype.Component;

import app.interfaces.Car;

@Component("swift")
public class Swift implements Car {

	@Override
	public void specs() {
		System.out.println("Suzuki Swift");

	}

}
