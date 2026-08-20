package com.hospital.sistema.utils;

import java.time.LocalDate;

public class NumberGenerator {

	private NumberGenerator() {
	}

	public static String generate(String prefix, long total) {
		int ano = LocalDate.now().getYear();
		return String.format("%s-%d-%05d", prefix, ano, total + 1);
	}

}
