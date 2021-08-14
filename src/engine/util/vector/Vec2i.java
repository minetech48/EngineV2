package engine.util.vector;

import java.io.Serializable;

public class Vec2i implements Serializable {
	
	public int x, y;
	
	public Vec2i() {}
	public Vec2i(Vec2i copy) {
		this(copy.x, copy.y);
	}
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Vec2i(float x, float y) {
		this.x = (int) x;
		this.y = (int) y;
	}
	
	
	public Vec2i translate(Vec2i vect) {return translate(vect.x, vect.y);}
	public Vec2i translate(Vec2f vect) {return translate((int) vect.x, (int) vect.y);}
	
	public Vec2i translate(int x, int y) {
		translateTo(this.x + x, this.y + y);
		return this;
	}
	
	public void translateTo(Vec2i vect) {translateTo(vect.x, vect.y);}
	public void translateTo(Vec2f vect) {translateTo((int) vect.x, (int) vect.y);}
	
	public void translateTo(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "Vec2i: (" + x + ", " + y + ")";
	}
	
	public void add(Vec2i vec) {
		x+=vec.x;
		y+=vec.y;
	}
	
	public static Vec2i add(Vec2i vec1, Vec2i vec2) {
		return new Vec2i(
				vec1.x + vec2.x,
				vec1.y + vec2.y);
	}
}
