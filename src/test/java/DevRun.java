import com.tfc.GLSLGameEngine;
import com.tfc.utils.Arguments;

public class DevRun {
	public static void main(String[] args) {
		Arguments.isDevEnvro = true;
		GLSLGameEngine.main(args);
	}
}
