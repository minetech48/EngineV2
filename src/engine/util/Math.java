package engine.util;

import java.awt.Dimension;
import java.awt.Point;

import engine.util.vector.Vec2i;
import engine.util.vector.Vec4i;

public class Math {
	
	//bounds checking
	public static boolean inBounds(Vec4i bounds, Point p) {
		int	inX = p.x-bounds.x,
			inY = p.y-bounds.y;
		return	inX > 0 && inX < bounds.z &&
				inY > 0 && inY < bounds.w;
	}
	public static boolean inBounds(Vec4i bounds, Vec2i p) {
		int	inX = p.x-bounds.x,
			inY = p.y-bounds.y;
		return	inX > 0 && inX < bounds.z &&
				inY > 0 && inY < bounds.w;
	}
	public static boolean inBounds(Point rectPos, Dimension rectSize, Point p) {
		int	inX = p.x-rectPos.x,
			inY = p.y-rectPos.y;
		return	inX > 0 && inX < rectSize.width &&
				inY > 0 && inY < rectSize.height;
	}
}
