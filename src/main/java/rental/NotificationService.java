package rental;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationService {
    
    // FIX 2: Gunakan Logger pengganti System.out
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    
    private final List<Notification> notifications = new ArrayList<>();
    
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
            "Halo %s! Booking kendaraan %s berhasil. Total: Rp %,.2f",
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
        
        // FIX 2: Log menggunakan Logger dengan Level yang sesuai
        Level level = Level.INFO;
        if (type == NotificationType.ERROR) {
            level = Level.SEVERE;
        } else if (type == NotificationType.WARNING) {
            level = Level.WARNING;
        }
        
        LOGGER.log(level, "[{0}] {1}", new Object[]{type.getDisplayName(), message});
    }
    
    public List<Notification> getAllNotifications() {
        // Return unmodifiable list agar data internal aman
        return Collections.unmodifiableList(new ArrayList<>(notifications));
    }
    
    public void clearNotifications() {
        notifications.clear();
    }

    public static class Notification {
        // FIX 1: Gunakan DateTimeFormatter (Thread-Safe)
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
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
            // FIX 1: Konversi timestamp (long) ke LocalDateTime untuk diformat
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                    .format(FORMATTER);
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