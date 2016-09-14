package sample1_basics;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class PokemonPoint extends DefaultWaypoint{

	private final int ID;
	private final long timeMs;
	//private final double x,y;

	/**
	 * @param ID:  the index of pokemon
	 * @param x,y: the location of pokemon
	 */
	
	public PokemonPoint(int i, long timeMs, GeoPosition coord)
	{
		super(coord);
		this.ID = i;
		this.timeMs = timeMs;
	}

	/**
	 * @return the ID
	 */
	public int getID()
	{
		return ID;
	}
	
	public long getTime()
	{
		return timeMs;
	}

	public boolean equals(PokemonPoint a)
	{
		return true;
	}
	/*
	 * @return the lantitude or longitude
	 */
	/*
	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}*/
	
}
