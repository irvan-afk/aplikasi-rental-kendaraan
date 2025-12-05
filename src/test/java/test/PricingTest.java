package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import pricing.DailyPricing;
import pricing.HourlyPricing;
import pricing.MonthlyPricing;
import pricing.PricingStrategy;
import pricing.WeeklyPricing;

public class PricingTest {

    @Test
    void testHourlyPricing() {
        PricingStrategy strategy = new HourlyPricing();
        // Harga dasar 240.000/hari. Per jam = 10.000. Sewa 5 jam = 50.000
        double price = strategy.calculatePrice(240000, 5); 
        assertEquals(50000, price, "Perhitungan per jam salah");
    }

    @Test
    void testDailyPricing() {
        PricingStrategy strategy = new DailyPricing();
        // Harga 100.000/hari. Sewa 3 hari = 300.000
        double price = strategy.calculatePrice(100000, 3);
        assertEquals(300000, price, "Perhitungan harian salah");
    }

    @Test
    void testWeeklyPricing() {
        PricingStrategy strategy = new WeeklyPricing();
        // Harga 100.000/hari. 
        // 1 Minggu (7 hari) = 100.000 * 5 (diskon bayar 5 hari) = 500.000
        double price = strategy.calculatePrice(100000, 1);
        assertEquals(500000, price, "Perhitungan mingguan salah");
    }
    
    @Test
    void testMonthlyPricing() {
        PricingStrategy strategy = new MonthlyPricing();
        // Harga 100.000/hari. 
        // 1 Bulan = 100.000 * 30 = 3.000.000
        // Diskon 20% = 3.000.000 * 0.8 = 2.400.000
        double price = strategy.calculatePrice(100000, 1);
        assertEquals(2400000, price, "Perhitungan bulanan (diskon) salah");
    }


}