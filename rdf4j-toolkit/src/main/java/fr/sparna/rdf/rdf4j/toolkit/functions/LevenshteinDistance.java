package fr.sparna.rdf.rdf4j.toolkit.functions;

/**
 * Computes Levenshtein distance between 2 Strings, with an additionnal parameter to make the comparison
 * case-insensitive if needed.
 * 
 * @author Thomas Francart
 *
 */
public class LevenshteinDistance {

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
		return computeLevenshteinDistance(str1, str2, true);
	}

	public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2, boolean caseSensitive) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 0; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++) {
			for (int j = 1; j <= str2.length(); j++) {
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ (((caseSensitive)?(str1.charAt(i - 1) == str2.charAt(j - 1)):(Character.toLowerCase(str1.charAt(i - 1)) == Character.toLowerCase(str2.charAt(j - 1)))) ? 0 : 1));
			}
		}

		return distance[str1.length()][str2.length()];
	}
}
