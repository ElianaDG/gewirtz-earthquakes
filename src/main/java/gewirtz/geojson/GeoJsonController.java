package gewirtz.geojson;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoJsonController {

    @FXML
    TableColumn magnitudeColumn;
    @FXML
    TableColumn timeColumn;
    @FXML
    TableColumn locationColumn;
    @FXML
    TableView tableView;

    @FXML
    public void initialize(){

        locationColumn.setCellValueFactory(new PropertyValueFactory<GeoJsonFeed.Feature, String>("place"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<GeoJsonFeed.Feature, String>("time"));
        magnitudeColumn.setCellValueFactory(new PropertyValueFactory<GeoJsonFeed.Feature, String>("magnitude"));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://earthquake.usgs.gov")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        GeoJsonService service = retrofit.create(GeoJsonService.class);

        Disposable disposable = service.getSignificantEarthquakes()
                //request the data in the background
                .subscribeOn(Schedulers.io())
                //work w the data in the foreground
                .observeOn(Schedulers.trampoline())
                //work w the feed whenever it gets downloaded
                .subscribe(this::ongeoJsonFeed,this::onError);
    }

    public void ongeoJsonFeed(GeoJsonFeed feed){
        tableView.getItems().setAll(feed.features);
        tableView.refresh();
    }

    public void onError(Throwable throwable){
        //this is not the right way to handle errors
        System.out.println("error occured");
    }
}
