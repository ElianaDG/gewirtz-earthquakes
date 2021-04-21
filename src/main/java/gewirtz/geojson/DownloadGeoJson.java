package gewirtz.geojson;

import com.google.gson.Gson;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DownloadGeoJson {

    public static void main(String[] args) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://earthquake.usgs.gov")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        GeoJsonService service = retrofit.create(GeoJsonService.class);

        GeoJsonFeed feed = service.getSignificantEarthquakes()
                .blockingGet();


        Feature largest = feed.features.get(0);
        for(Feature feature : feed.features){
            if (feature.properties.mag > largest.properties.mag){
                largest = feature;
            }
        }
        System.out.printf("%s, %f, %d",
                largest.properties.place,
                largest.properties.mag,
                largest.properties.time);

        System.exit(0);
    }
}
