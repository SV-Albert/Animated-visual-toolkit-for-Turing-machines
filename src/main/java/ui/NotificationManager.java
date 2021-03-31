package ui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationManager {
    public static final Image errorIcon = new Image(NotificationManager.class.getResource("icons/error_icon.png").toExternalForm());
    public static final ImageView errorGraphic = new ImageView(errorIcon);
    public static final Image confirmIcon = new Image(NotificationManager.class.getResource("icons/confirm_icon.png").toExternalForm());
    public static final ImageView confirmGraphic = new ImageView(confirmIcon);

    public static void confirmationNotification(String title, String message, Stage owner){
        Notifications.create()
                .graphic(confirmGraphic)
                .position(Pos.BOTTOM_RIGHT)
                .title(title)
                .text(message)
                .owner(owner)
                .hideAfter(Duration.seconds(3))
                .show();
    }

    public static void errorNotification(String title, String message, Stage owner){
        System.out.println(NotificationManager.class.getResource("icons/error_icon.png").toExternalForm());
        Notifications.create()
                .graphic(errorGraphic)
                .position(Pos.BOTTOM_RIGHT)
                .title(title)
                .text(message)
                .owner(owner)
                .hideAfter(Duration.seconds(3))
                .show();
    }
}
