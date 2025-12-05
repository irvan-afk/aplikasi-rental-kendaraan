package rental;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationService {
    
    private final List<Notification> notifications = new ArrayList<>();
    private static final SimpleDateFormat DATE_FORMAT = 
        new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public enum NotificationType {
        SUCCESS("Success"),
        WARNING("Peringatan"),
        ERROR("Error"),
        INFO("Informasi");
        
        private final String displayName;
        
        NotificationType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public void sendBookingConfirmation(
            String customerName, 
            String vehiclePlate,
            double totalPrice
    ) {
        String message = String.format(
            "Halo %s! Booking kendaraan %s berhasil. Total: Rp %.2f",
            customerName, vehiclePlate, totalPrice
        );
        sendNotification(NotificationType.SUCCESS, message);
    }
    
    public void sendPaymentConfirmation(
            String customerName, 
            String transactionId
    ) {
        String message = String.format(
            "Pembayaran berhasil! Transaction ID: %s. Terima kasih %s!",
            transactionId, customerName
        );
        sendNotification(NotificationType.SUCCESS, message);
    }

    public void sendErrorNotification(String errorMessage) {
        sendNotification(NotificationType.ERROR, errorMessage);
    }
    
    public void sendInfoNotification(String message) {
        sendNotification(NotificationType.INFO, message);
    }
    
    private void sendNotification(NotificationType type, String message) {
        Notification notification = new Notification(
            type,
            message,
            System.currentTimeMillis()
        );
        notifications.add(notification);
        
        System.out.println("[" + type.getDisplayName() + "] " + message);
    }
    
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }
    
    public void clearNotifications() {
        notifications.clear();
    }

    public static class Notification {
        private final NotificationType type;
        private final String message;
        private final long timestamp;
        
        public Notification(NotificationType type, String message, long timestamp) {
            this.type = type;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public NotificationType getType() {
            return type;
        }
        
        public String getMessage() {
            return message;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public String getFormattedTimestamp() {
            return DATE_FORMAT.format(new Date(timestamp));
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s - %s", 
                type.getDisplayName(), 
                getFormattedTimestamp(), 
                message
            );
        }
    }
}
