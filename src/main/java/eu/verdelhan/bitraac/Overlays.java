package eu.verdelhan.bitraac;

import com.xeiam.xchange.dto.marketdata.Trade;
import eu.verdelhan.bitraac.data.ExchangeMarket;
import eu.verdelhan.bitraac.data.Period;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import org.joda.money.BigMoney;

/**
 * See:
 *   - http://bitcoincharts.com/charts/
 *   - http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators
 */
public class Overlays {

	/**
	 * @param periods the list of periods
	 * @param lastPeriods the number of periods to use (i.e. the n last periods)
	 * @return the moving average price of trades
	 */
    public static BigDecimal getSimpleMovingAverage(ArrayList<Period> periods, int lastPeriods) {
        int nbPeriods = periods.size();
        if (lastPeriods >= nbPeriods) {
            throw new IllegalArgumentException("Not enough periods");
        }

        BigDecimal average = new BigDecimal(0);
        int firstValueIndex = (nbPeriods - lastPeriods) > 0 ? nbPeriods - lastPeriods : 0;
        for (int i = firstValueIndex; i < nbPeriods; i++) {
			BigMoney periodLastPrice = periods.get(i).getLast();
			if (periodLastPrice == null) {
				lastPeriods--;
			} else {
				average = average.add(periods.get(i).getLast().getAmount());
			}
        }
        return average.divide(new BigDecimal(lastPeriods), RoundingMode.HALF_UP);
    }

	/**
	 * @param period the period for which we want the trade volume
	 * @return the trade volume during the period
	 */
	public static BigDecimal getVolume(Period period) {
		BigDecimal volume = BigDecimal.ZERO;
		for (Trade trade : period.getTrades()) {
			volume = volume.add(trade.getPrice().getAmount());
		}
		return volume;
	}

	/**
	 * @param timestamp the date for which we want the trade volume
	 * @return the trade volume at timestamp
	 */
	public static BigDecimal getVolume(Date timestamp) {
		BigDecimal volume = BigDecimal.ZERO;
		for (Trade trade : ExchangeMarket.getAllTrades()) {
			if (timestamp.equals(trade.getTimestamp())) {
				volume = volume.add(trade.getPrice().getAmount());
			}
		}
		return volume;
	}
}