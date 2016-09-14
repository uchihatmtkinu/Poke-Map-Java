package sample1_basics;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.NearbyPokemon;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.ServerRequest;
import com.pokegoapi.util.Log;

import POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import POGOProtos.Networking.Requests.Messages.LevelUpRewardsMessageOuterClass.LevelUpRewardsMessage;
import POGOProtos.Networking.Responses.LevelUpRewardsResponseOuterClass.LevelUpRewardsResponse;
import okhttp3.OkHttpClient;

public class test1 {
	

	

	/**
	* PTC is much simpler, but less secure.
	* You will need the username and password for each user log in
	* This account does not currently support a refresh_token. 
	* Example log in :
	 * @throws RemoteServerException 
	 * @throws LoginFailedException 
	*/
	public static void main(String[] args) throws LoginFailedException, RemoteServerException {
		OkHttpClient httpClient = new OkHttpClient();
		PokemonGo go = new PokemonGo(httpClient);
		JFrame test = new JFrame("Google Maps");
		try {
			go.login(new PtcCredentialProvider(httpClient,"uchiha_8" , "test1234"));
			// After this you can access the api from the PokemonGo instance :
			TimeUnit.MILLISECONDS.sleep(500);
			go.setLocation(22.3358582, 114.2633857, 1); // set your position to get stuff around (altitude is not needed, you can use 1 for example)
			//go.getMap().getCatchablePokemon(); // get all currently Catchable Pokemon around you
			TimeUnit.MILLISECONDS.sleep(500);
			//List<NearbyPokemon> nearbyPokemon = go.getMap().getNearbyPokemon();
			//System.out.println("Pokemon in area:" + nearbyPokemon.size());
			List<CatchablePokemon> catchablePokemon = go.getMap().getCatchablePokemon();
			System.out.println("Pokemon in catch:" + catchablePokemon.size());
			for (CatchablePokemon cp : catchablePokemon) {
				//tmpdata = cp.getPokemonData();
				long timedis = cp.getExpirationTimestampMs() - System.currentTimeMillis();
				System.out.println("Pokemon: " + cp.getPokemonId() + ' ' + timedis/1000);
			}
			
			

		} catch (LoginFailedException | RemoteServerException e) {
			// failed to login, invalid credentials, auth issue or server issue.
			Log.e("Main", "Failed to login or server issue: ", e);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
