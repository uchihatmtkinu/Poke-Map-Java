package sample1_basics;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.Spring;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import okhttp3.OkHttpClient;

public class FindPokemon implements Callable<List<CatchablePokemon>>{

	private double lantitude,longitude;
	private String username;
	private String password;
	private List<CatchablePokemon> catchablePokemon;
	private PokemonGo go;
	private OkHttpClient httpClient;
	private static int cnt = 0;

	
	public FindPokemon(double la, double lo, String u, String p)
	{
		this.lantitude = la;
		this.longitude = lo;
		this.username = u;
		this.password = p;
		
	}
	
	private void startFindPokemon()
	{
		httpClient = new OkHttpClient();
		go = new PokemonGo(httpClient);
		updatePokemon();
	}
	
	private void updatePokemon()
	{
		try {
			go.login(new PtcCredentialProvider(httpClient, username , password));
			TimeUnit.MILLISECONDS.sleep(500);
			go.setLocation(lantitude, longitude, 1);
			TimeUnit.MILLISECONDS.sleep(500);
			catchablePokemon = go.getMap().getCatchablePokemon();
		} catch (LoginFailedException | RemoteServerException | InterruptedException e) {
			// TODO Auto-generated catch block
			if (cnt < 5)
			{
				System.out.println("failed");
				updatePokemon();
				cnt = cnt + 1;
				
			}
			
			if (cnt>5) e.printStackTrace();
		}
	}

	@Override
	public List<CatchablePokemon> call() throws Exception {
		// TODO Auto-generated method stub
		startFindPokemon();
		return catchablePokemon;
	}


}
