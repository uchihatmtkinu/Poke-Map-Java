/*
 * WaypointRenderer.java
 *
 * Created on March 30, 2006, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sample1_basics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;


/**
 * A fancy waypoint painter
 * @author Martin Steiger
 */
public class FancyWaypointRenderer implements WaypointRenderer<PokemonPoint>
{
	private static final Log log = LogFactory.getLog(FancyWaypointRenderer.class);
	
	private final Map<Color, BufferedImage> map = new HashMap<Color, BufferedImage>();
	
//	private final Font font = new Font("Lucida Sans", Font.BOLD, 10);
	
	private BufferedImage origImage;
	private BufferedImage backImage;
	/**
	 * Uses a default waypoint image
	 */
	public FancyWaypointRenderer()
	{
		URL resource = getClass().getResource("waypoint_white.png");

		try
		{
			origImage = ImageIO.read(resource);
		}
		catch (Exception ex)
		{
			log.warn("couldn't read the picture", ex);
		}
	}


	private BufferedImage convert(BufferedImage loadImg, Color newColor)
	{
		int w = loadImg.getWidth();
		int h = loadImg.getHeight();
		BufferedImage imgOut = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		BufferedImage imgColor = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = imgColor.createGraphics();
		g.setColor(newColor);
		g.fillRect(0, 0, w+1, h+1);
		g.dispose();

		Graphics2D graphics = imgOut.createGraphics();
		graphics.drawImage(loadImg, 0, 0, null);
		graphics.setComposite(MultiplyComposite.Default);
		graphics.drawImage(imgColor, 0, 0, null);
		graphics.dispose();
		
		return imgOut;
	}

	@Override
	public void paintWaypoint(Graphics2D g, JXMapViewer viewer, PokemonPoint w)
	{
		g = (Graphics2D)g.create();
		
		boolean pokemon = true;
		if (w.getTime() == 0) pokemon = false;
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL resource = loader.getResource(Integer.toString(w.getID())+".png");
		URL back = loader.getResource(Integer.toString(w.getID())+".png");
		int status = 4;
		if (pokemon)
		{
			long timedis = (w.getTime() - System.currentTimeMillis())/1000;
			if (timedis < 0) status = 4;
			else if (timedis < 60) status = 0;
			else if (timedis < 300) status = 1;
			else if (timedis < 600) status = 2;
			else status = 3;
			back = loader.getResource("hsl-"+Integer.toString(status)+".png");
		}
		try
		{
			origImage = ImageIO.read(resource);
			if (pokemon) backImage = ImageIO.read(back);
		}
		catch (Exception ex)
		{
			log.warn("couldn't read waypoint_white.png", ex);
		}
		
		if (origImage == null)
			return;
		
		Point2D point = viewer.getTileFactory().geoToPixel(w.getPosition(), viewer.getZoom());
		
		int x = (int)point.getX();
		int y = (int)point.getY();
		if (pokemon) g.drawImage(backImage, x -origImage.getWidth() / 2, y -origImage.getHeight(), null);
		if (status < 4 || !pokemon) g.drawImage(origImage, x -origImage.getWidth() / 2, y -origImage.getHeight(), null);
		
	
//		g.setFont(font);

		FontMetrics metrics = g.getFontMetrics();

		g.dispose();
	}
}


