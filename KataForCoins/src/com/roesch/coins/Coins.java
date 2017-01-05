package com.roesch.coins;

import java.util.Arrays;

public class Coins {
	int quarters = 0;
	int dimes = 1;
	int nickles = 2;
	int cents = 3;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Coins().run(1.06f);

	}

	/**
	 * 
	 * @param amount
	 */
	public void run(final float amount) {
		assert amount >= 0;
		System.out.println("Coin Kata for $" + amount);

		int pennies = this.convertToPennies(amount);
		System.out.println(pennies);

		int[] counts = this.convertToCounts(pennies);
		String countsAsString = Arrays.toString(counts);
		System.out.println(countsAsString);

	}

	/**
	 * 
	 * @param amount
	 * @return
	 */
	int convertToPennies(final float amount) {
		assert amount >= 0;

		float product = amount * 1000f / 10f;
		return (int) (product);
	}

	/**
	 * 
	 * @param pennies
	 * @return
	 */
	int[] convertToCounts(final int pennies) {
		assert pennies > -1;

		int remaining = pennies;

		int[] counts = new int[4]; 

		while (true) {
			if (remaining >= 25) {
				counts[quarters]++;
				remaining -= 25;
				continue;
			} else if (remaining >= 10) {
				counts[dimes]++;
				remaining -= 10;
				continue;
			} else if (remaining >= 5) {
				counts[nickles]++;
				remaining -= 5;
				continue;
			} else
				counts[cents] = remaining;
			break;
		}

		return counts;
	}

}
