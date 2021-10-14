package engine.ux;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;

import engine.util.FileIO;
import engine.util.StringReader;

public class GraphicsWrapper {
	
	protected Graphics2D graphics;
	AlphaComposite aComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
	
	public Graphics2D getGraphics() {return graphics;}
	
	public void setColor(String colorName) {
		setColor(GUI.getColor(colorName));
	}
	public void setColor(Color color) {
		if (color != null)
			graphics.setColor(color);
	}
	public void setHSVColor(float h, float s, float v) {
		graphics.setColor(Color.getHSBColor(h, s, v));
	}
	public void setHSVAColor(float h, float s, float v, float a) {
		Color tempColor = Color.getHSBColor(h, s, v);
		
		graphics.setColor(new Color(
				tempColor.getRed(),
				tempColor.getGreen(),
				tempColor.getBlue(),
				(int) (a*255)));
	}
	
	public void setSize(float size) {
		graphics.setStroke(new BasicStroke(size));
	}
	
	public void translate(int x, int y) {
		graphics.translate(x, y);
	}
	
	public void setAlpha(float a) {
		graphics.setComposite(aComposite.derive(a));
	}
	
	
	public void drawRect(int x, int y, int width, int height) {
		graphics.drawRect(x, y, width, height);
	}
	public void fillRect(int x, int y, int width, int height) {
		graphics.fillRect(x, y, width, height);
	}
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		graphics.drawLine(x1, y1, x2, y2);
	}
	
	
	public void drawPolygon(int... points) {
		drawLine(points[0], 
				 points[1],
				 points[2],
				 points[3]);
		
		for (int i = 2; i < points.length/2; i+=2) {
			drawLine(points[i-2], 
					 points[i-1], 
					 points[i  ], 
					 points[i+1]);
		}
	}
	
	public void drawImageS(String imgName, int x, int y, int width, int height) {
		drawImageI(FileIO.loadImage(imgName), x, y, width, height);
	}
	public void drawImageI(Image img, int x, int y, int width, int height) {
		graphics.drawImage(img, x, y, width, height, null);
	}
	
	public void setFont(engine.ux.primitives.Font font) {
		graphics.setFont(font.getAwtFont());
	}
	public void setFont(Font font) {
		graphics.setFont(font);
	}
	
	public void drawString(String text, int x, int y) {
		drawStringRaw(GUI.reconstructString(text), x, y);
	}
	public void drawStringCentered(String text, int x, int y, int width, int height) {
		drawStringCenteredRaw(GUI.reconstructString(text), x, y, width, height);
	}
	public void drawStringCenteredRaw(String text, int x, int y, int width, int height) {
		Dimension size = getTextSizeRaw(text);
		
		drawStringRaw(text, x+(width-size.width)/2, y+(height-size.height)/2-size.height*2);
	}
	public void drawStringRight(String text, int x, int y, int width) {
		text = GUI.reconstructString(text);
		
		Dimension size = getTextSizeRaw(text);

		drawStringRaw(text, x+(width-size.width)/2, y);
	}
	public void drawStringRight(String text, int x, int y, int width, int height) {
		text = GUI.reconstructString(text);
		
		Dimension size = getTextSizeRaw(text);

		drawStringRaw(text, x+(width-size.width), y+(height-size.height)/2-size.height*2);
	}
	public void drawStringLeft(String text, int x, int y, int height) {
		text = GUI.reconstructString(text);
		
		Dimension size = getTextSizeRaw(text);

		drawStringRaw(text, x, y+(height-size.height)/2-size.height*2);
	}
	
	public void drawStringRaw(String text, int x, int y) {
		char c;
		StringReader reader = new StringReader(text);
		y+=graphics.getFontMetrics().getAscent();
		
		int xBegin = x;
		
		Color oldColor = null;
		
		while (reader.hasNext()) {
			c = reader.next();
			
			if (c == '\\') {
				switch (reader.next()) {
//				case '$':
//					break;
//				case '\\':
//					break;
				case 'n':
					y+=getTextLineHeight(graphics.getFontMetrics());
					x = xBegin;
					break;
				case 'c':
					String newColor = reader.advanceTo('\\').substring(1);
					
					if (newColor.equals("revert"))
						graphics.setColor(oldColor);
					else {
						oldColor = graphics.getColor();
						graphics.setColor(GUI.getColor(newColor));
					}
					
					if (reader.hasNext() && reader.next() != ' ')
						reader.previous();
					break;
//				case 'f':
//					g.setFont(GUI.getFont(reader.advanceTo('\\').substring(1), graphics.getFont().getSize()));
//					if (reader.next() != ' ')
//						reader.previous();
//					break;
				default:
					reader.previous();
					break;
				}
				continue;
			}else if (c == '\n') {
				y+=getTextLineHeight(graphics.getFontMetrics());
				x = xBegin;
			}
			
			drawChar(c, x, y);
			x+=graphics.getFontMetrics().charWidth(c)+GUI.getFontScale();
		}
	}
	private void drawChar(char c, int x, int y) {
		graphics.drawString(String.valueOf(c), x, y);
	}
	
	public int getTextLineHeight(FontMetrics font) {
		return font.getAscent() + font.getDescent();
	}
	
	public Dimension getTextSize(String text) {
		return getTextSizeRaw(GUI.reconstructString(text));
	}
	public Dimension getTextSizeRaw(String text) {
		StringReader reader = new StringReader(text);
		FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
		
		int width = 0;
		int tempWidth = 0;
		int height = - metrics.getAscent() + metrics.getHeight();
		
		while (reader.hasNext()) {
			if (reader.next() == '\\') {
				if (reader.next() != '\\') {
					if (reader.current() == 'n') {
						height+=  - metrics.getAscent();
						width = Math.max(width, tempWidth);
						tempWidth = 0;
						continue;
					}
					
					reader.advanceTo('\\');
					if (reader.next() != ' ')
						reader.previous();
					continue;
				}
			}
			
			tempWidth+= graphics.getFontMetrics().charWidth(reader.current())+GUI.getFontScale();
		}
		
		width = Math.max(width, tempWidth);
		
		return new Dimension(width, height);
	}
	
	//getters
	public Color getRGBColor(float r, float g, float b) {
		return new Color(r, g, b);
	}
}
