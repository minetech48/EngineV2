package engine.util.vector;

public class Vec4i {
	public int x, y, z, w;
	
	public Vec4i() {}
	
	public Vec4i(int x, int y, int z, int w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4i(Vec4i vect) {
		x = vect.x;
		y = vect.y;
		z = vect.z;
		w = vect.w;
	}
	
	
	public Vec4i add(Vec4i other) {
		x+= other.x;
		y+= other.y;
		z+= other.z;
		w+= other.w;
		
		return this;
	}
	public static Vec4i add(Vec4i vec1, Vec4i vec2) {
		return new Vec4i(
				vec1.x + vec2.x,
				vec1.y + vec2.y,
				vec1.z + vec2.z,
				vec1.w + vec2.w);
	}
}
