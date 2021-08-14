package engine.util.vector;

public class Vec2f {
	
	public float x, y;
	
	public Vec2f() {}
	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void translate(Vec2i vect) {translate(vect.x, vect.y);}
	public void translate(Vec2f vect) {translate(vect.x, vect.y);}
	
	public void translate(float x, float y) {
		translateTo(this.x + x, this.y + y);
	}
	
	public void translateTo(Vec2i vect) {translateTo(vect.x, vect.y);}
	public void translateTo(Vec2f vect) {translateTo(vect.x, vect.y);}
	
	public void translateTo(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void add(Vec2f vec) {
		x+=vec.x;
		y+=vec.y;
	}
	
	public static Vec2f add(Vec2f vec1, Vec2f vec2) {
		return new Vec2f(
				vec1.x + vec2.x,
				vec1.y + vec2.y);
	}
	
	public static double dist(Vec2f vec1, Vec2f vec2) {
		return Math.sqrt(Math.pow(vec1.x-vec2.x, 2) + Math.pow(vec1.y-vec2.y, 2));
	}
}
