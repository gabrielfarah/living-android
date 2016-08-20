package co.ar_smart.www.interfaces;

import java.util.ArrayList;

import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Hub;
import co.ar_smart.www.pojos.Mode;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * This interface implements a Retrofit interface for the Home Activity
 */
public interface IHomeClient {
    /**
     * This function get all the endpoints inside a hub given a hub id.
     *
     * @param hub_id The ID of the hub from which to get the endpoints
     * @return A list containing all the endpoints
     */
    @GET("hubs/{hub_id}/endpoints/")
    Call<ArrayList<Endpoint>> endpoints(
            @Path("hub_id") String hub_id
    );

    /**
     * This function obtains all the hubs for the current user
     *
     * @return A list of all the hubs the user is available to query
     */
    @GET("hubs/")
    Call<ArrayList<Hub>> hubs();

    /**
     * This function obtains a particular hub given a valid hub ID
     *
     * @param hub_id The ID of the hub to get
     * @return The hub matching the hub ID
     */
    @GET("hubs/{hub_id}/")
    Call<Hub> hub(
            @Path("hub_id") String hub_id
    );

    /**
     * This function get all the modes inside a hub given a hub id.
     *
     * @param hub_id The ID of the hub from which to get the endpoints
     * @return A list containing all the endpoints
     */
    @GET("hubs/{hub_id}/modes/")
    Call<ArrayList<Mode>> modes(
            @Path("hub_id") String hub_id
    );
}
