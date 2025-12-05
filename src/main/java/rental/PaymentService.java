package rental;

import rental.exception.RentalException;

public class PaymentService {
    
    public enum PaymentMethod {
        CASH("Cash"),
        CREDIT_CARD("Kartu Kredit"),
        DEBIT_CARD("Kartu Debit"),
        E_WALLET("E-Wallet");
        
        private final String displayName;
        
        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public PaymentReceipt processPayment(
            double amount, 
            PaymentMethod method, 
            String customerName
    ) throws RentalException {
        
        if (amount <= 0) {
            throw new RentalException(
                RentalException.ErrorCode.PAYMENT_FAILED,
                "Jumlah pembayaran tidak valid"
            );
        }

        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new PaymentReceipt(
            generateTransactionId(),
            amount,
            method,
            customerName,
            System.currentTimeMillis()
        );
    }
    
    public boolean validatePaymentMethod(PaymentMethod method) {
        return method != null;
    }
    
    private String generateTransactionId() {
        return "TRX-" + System.currentTimeMillis() + "-" + 
               (int)(Math.random() * 1000);
    }

    public static class PaymentReceipt {
        private final String transactionId;
        private final double amount;
        private final PaymentMethod method;
        private final String customerName;
        private final long timestamp;
        
        public PaymentReceipt(
                String transactionId, 
                double amount, 
                PaymentMethod method,
                String customerName, 
                long timestamp
        ) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.method = method;
            this.customerName = customerName;
            this.timestamp = timestamp;
        }
        
        public String getTransactionId() {
            return transactionId;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public PaymentMethod getMethod() {
            return method;
        }
        
        public String getCustomerName() {
            return customerName;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Transaction ID: %s\nAmount: Rp %.2f\nMethod: %s\nCustomer: %s",
                transactionId, amount, method.getDisplayName(), customerName
            );
        }
    }
}
