package sample1_basics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.LocalResponseCache;
import org.jxmapviewer.viewer.TileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import sample1_basics.SelectionAdapter;
import sample1_basics.SelectionPainter;

/**
 * A simple sample application that shows
 * a OSM map of Europe
 * @author Martin Steiger
 */
public class MainFunc
{
	/**
	 * @param args the program args (ignored)
	 */
	final private static JXMapViewer mapViewer = new JXMapViewer();
	private static JPanel panel = new JPanel();
	private static JButton buttonOp = new JButton("Operation");
	private static JButton buttonClean = new JButton("Clean");
	private static JFrame frame = new JFrame("Pokemon Go Map");
	private static double startLocX = 0;
	private static double startLocY = 0;
	private static AccountDB accDB = new AccountDB();
	private static WaypointPainter<PokemonPoint> waypointPainter = new WaypointPainter<PokemonPoint>();
	private static Set<PokemonPoint> Pokepoints = new HashSet<PokemonPoint>();
	private static PokemonPoint startPoint = new PokemonPoint(0,0,new GeoPosition(startLocX,startLocY));
	private static FindPokemon [] findTeam = new FindPokemon[6];
	private static ExecutorService pool = Executors.newFixedThreadPool(7);  
	private static Future [] list;
	private static NewGeoPos newP [] = new NewGeoPos[100];
	private static JLabel labelThreadCount = new JLabel("Progress: ");
	private static double cnt = -1;
	private static Timer t = new Timer(500, new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (cnt == -1)
				labelThreadCount.setText("Progress: ");
			else
				labelThreadCount.setText("Progress: "+Double.toString(cnt/7*100)+"%");
		}
	}); 
	
	private static void MapSetup()
	{
		//Setup the Map
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		// Setup JXMapViewer				
		mapViewer.setTileFactory(tileFactory);
		GeoPosition firstPos = new GeoPosition(22.3358582, 114.2633857);
		// Set the focus
		mapViewer.setZoom(2);
		mapViewer.setAddressLocation(firstPos);
		// Add interactions
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);

		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
		
		
	}
	
	public static void main(String[] args)
	{
		//Setup the Map
		MapSetup();
		//Add function based on the action on the map, no need to care about it
		mapViewer.addMouseListener(new MouseAdapter(){
			
			@Override
	        public void mouseEntered(MouseEvent e) {
	        //hover
	        //should do something here...        
	        }

			
		    @Override
		    public void mousePressed(MouseEvent e){
		        if(e.getClickCount()==2){
		        	double ox = mapViewer.getCenter().getX();
		        	double oy = mapViewer.getCenter().getY();
		        	Rectangle bounds = mapViewer.getViewportBounds();
		    		double x = bounds.getX() + e.getX();
		    		double y = bounds.getY() + e.getY();
		    		mapViewer.setCenter(new Point2D.Double(x, y));
		    		startLocX = mapViewer.getCenterPosition().getLatitude();
		    		startLocY = mapViewer.getCenterPosition().getLongitude();
		    		Pokepoints.remove(startPoint);
		    		startPoint.setPosition(new GeoPosition(startLocX,startLocY));
		    		Pokepoints.add(startPoint);
		    		mapViewer.setCenter(new Point2D.Double(ox, oy));
		    		updateMapviewer();
		        }
		    }
		});
		
		//The operation when the button is pressed
		buttonOp.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub	
				/* updateStartLoc(): Update the start location based on mouse double click
				 * 
				 * DoFindPokemon(lantitude,longitude,AccountIndex): Using the account to find pokemon at the location
				 * 
				 * getGeoPos(lantitude,longitude, angle, dis, PositionIndex):
				 * Generate a new Position, based on the current position and distance, angle
				 * 
				 * getLatitude(PositionIndex), getLongitude(PositionIndex): Return the position information of the potision
				 * 
				 * getPokemons(AccountIndex): Save pokemons from accountIndex into a list
				 * 
				 * updateMapviewer(): Update the map to show wild pokemons 
				*/
				updateStartLoc();
				DoFindPokemon(startLocX, startLocY, 0);
				
				for (int i = 1; i <= 6; i++)
				{	
					getGeoPos(startLocX,startLocY,i*60,0.1, i);
					double newX = getLatitude(i);
					double newY = getLongitude(i);
					DoFindPokemon(newX, newY, i);
					
				}
				
				for (int i = 0; i < 7; i++) getPokemons(i);
			
				updateMapviewer();
				
			}
			
		});
		
		//Finish the setup of map
		finishMapDraw();
		
	}
	private static double getLatitude(int i)
	{
		return newP[i].getLat();
	}
	
	private static double getLongitude(int i)
	{
		return newP[i].getLon();
	}
	private static void getGeoPos(double x, double y, double angle, double dis, int i)
	{
		newP[i] = new NewGeoPos(x,y,angle,dis);
	}
	
	private static void DoFindPokemon(double lat, double lon, int i)
	{
		Callable t1 = new FindPokemon(lat,lon,accDB.getAcc(i).getUsername(),accDB.getAcc(i).getPassword());
		Future f = pool.submit(t1);
		list[i] = f;
	}
	
	private static void getPokemons(int i)
	{
		pool.shutdown();
		List<CatchablePokemon> list1;
		if (cnt == -1) cnt = 0;
		cnt = cnt+1;
		try {
			list1 = (List<CatchablePokemon>) list[i].get();
			if (list1 == null) return;
			for (CatchablePokemon cp : list1) {
				Pokepoints.add(new PokemonPoint(cp.getPokemonIdValue(), cp.getExpirationTimestampMs(), new GeoPosition(cp.getLatitude(),cp.getLongitude())));
		}
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
		}
			
	}
	
	private static void updateStartLoc()
	{
		Pokepoints.remove(startPoint);
		cnt = -1;
		startPoint.setPosition(new GeoPosition(startLocX,startLocY));
		Pokepoints.add(startPoint);
		pool = Executors.newFixedThreadPool(7);  
		list = new Future[7];
	}
	
	private static void updateMapviewer()
	{
		waypointPainter.setWaypoints(Pokepoints);
		waypointPainter.setRenderer(new FancyWaypointRenderer());
		mapViewer.setOverlayPainter(waypointPainter);
	}
	
	private static void finishMapDraw()
	{
		panel.setLayout(new FlowLayout());
		panel.add(buttonOp);
		panel.add(buttonClean);
		// Display the viewer in a JFrame
		
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.NORTH);
		frame.add(labelThreadCount, BorderLayout.SOUTH);
		frame.add(mapViewer);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
		
		t.start();
	}
}
