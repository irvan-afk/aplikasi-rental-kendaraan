package rental.exception;

public class RentalException extends Exception {
    
    private final ErrorCode errorCode;
    
    public enum ErrorCode {
        VEHICLE_NOT_AVAILABLE("Kendaraan tidak tersedia"),
        INVALID_DURATION("Durasi rental tidak valid"),
        PAYMENT_FAILED("Pembayaran gagal"),
        VEHICLE_NOT_FOUND("Kendaraan tidak ditemukan"),
        USER_NOT_AUTHENTICATED("User belum login"),
        INSUFFICIENT_BALANCE("Saldo tidak mencukupi"),
        DATABASE_ERROR("Kesalahan database");
        
        private final String message;
        
        ErrorCode(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    public RentalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public RentalException(ErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + ": " + additionalMessage);
        this.errorCode = errorCode;
    }
    
    public RentalException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
