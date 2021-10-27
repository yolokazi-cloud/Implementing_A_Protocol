package cssc2b.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * Class implements start method
 * @author Nogantsho Y 201602466
 * @version P06
 */
public class BUKAClient extends Application
{
    public static void main(String[] args)
    {
    	//launch the JavaFX Application
    	launch(args);
    }
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		//create the ClientPane, set up the Scene and Stage
		    BUKAClientPane BukaPane = new BUKAClientPane(primaryStage);
			primaryStage.setTitle("BUKA Protocol"); 
			Scene scene = new Scene(BukaPane, 600,600);
			primaryStage.setScene(scene);
			primaryStage.show();
	}
}
