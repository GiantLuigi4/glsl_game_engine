public class test {
	public static void main(String[] args) {
		int lastVal = 0;
		for (int y = 0; y < 256; y++) {
			for (int x = 0; x < 256; x++) {
				if (x + y * 256 <= lastVal) {
					System.out.println(x + y * 256 + ", " + lastVal + ", x = " + x + ", y = " + y);
				}
				lastVal = x + y * 256;
			}
		}
	}
}
