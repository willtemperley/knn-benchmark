package nat.tree;

import java.awt.geom.Point2D;

/**
 * M - combination of FastTrig, java.lang.Math and robocode.util.Utils
 * 
 * The trig section was created by Alexander Schultz, and improved by Julian Kent.
 * 
 * The inverse trig section was first created by Julian Kent,
 * and later improved by Nat Pavasant and Starrynte.
 * 
 * The angle normalization is originally robocode.util.Utils'
 * so it is Matthew's and Flemming's.
 * 
 * Other parts was created by Nat Pavasant to use in his robot.
 * 
 * @author Alexander Schultz (a.k.a. Rednaxela)
 * @author Julian Kent (a.k.a. Skilgannon)
 * @author Flemming N. Larsen
 * @author Mathew A. Nelson
 * @author Nat Pavasant
 */
public final class M {
	public static final double PI = 3.1415926535897932384626433832795D;
	public static final double TWO_PI = 6.2831853071795864769252867665590D;
	public static final double HALF_PI = 1.5707963267948966192313216916398D;
	public static final double QUARTER_PI = 0.7853981633974483096156608458199D;
	public static final double THREE_OVER_TWO_PI = 4.7123889803846898576939650749193D;
	
	/* Setting for trig */
	private static final int TRIG_DIVISIONS = 8192; /* Must be power of 2 */
	private static final int TRIG_HIGH_DIVISIONS = 131072; /* Must be power of 2 */
	private static final double K = TRIG_DIVISIONS / TWO_PI;
	private static final double ACOS_K = (TRIG_HIGH_DIVISIONS - 1) / 2;
	private static final double TAN_K = TRIG_HIGH_DIVISIONS / PI;
	
	/* Lookup tables */
	private static final double[] sineTable = new double[TRIG_DIVISIONS];
	private static final double[] tanTable = new double[TRIG_HIGH_DIVISIONS];
	private static final double[] acosTable = new double[TRIG_HIGH_DIVISIONS];
	
	/* Hide the constructor */
	private M() {}
	
	/**
	 * Initializing the lookup table
	 */
	public static final void init() {
		for (int i = 0; i < TRIG_DIVISIONS; i++) {
			sineTable[i] = Math.sin(i / K);
		}
		for (int i = 0; i < TRIG_HIGH_DIVISIONS; i++) {
			tanTable[i] = Math.tan(i / TAN_K);
			acosTable[i] = Math.acos(i / ACOS_K - 1);
		}
	}
	
	/* Fast and reasonable accurate trig functions */
	public static final double sin(double value) { return sineTable[(int) (((value * K + 0.5) % TRIG_DIVISIONS + TRIG_DIVISIONS)) & (TRIG_DIVISIONS - 1)]; }
	public static final double cos(double value) { return sineTable[(int) (((value * K + 0.5) % TRIG_DIVISIONS + 1.25 * TRIG_DIVISIONS)) & (TRIG_DIVISIONS - 1)]; }
	public static final double tan(double value) { return tanTable[(int) (((value * TAN_K + 0.5) % TRIG_HIGH_DIVISIONS + TRIG_HIGH_DIVISIONS)) & (TRIG_HIGH_DIVISIONS - 1)]; }
	public static final double asin(double value) { return HALF_PI - acos(value); }
	public static final double acos(double value) { return acosTable[(int) (value * ACOS_K + (ACOS_K + 0.5))]; }
	public static final double atan(double value) { return (value >= 0 ? acos(1 / sqrt(value * value + 1)) : -acos(1 / sqrt(value * value + 1))); }
	public static final double atan2(double x, double y) { return (x >= 0 ? acos(y / sqrt(x * x + y * y)) : -acos(y / sqrt(x * x + y * y))); }
	
	/* Redirect to Math class (faster version not available) */
	public static final double sqrt(double x) { return Math.sqrt(x); }
	public static final double cbrt(double x) { return Math.cbrt(x); }
	public static final double pow(double x, double a) { return Math.pow(x, a); }
	
	/* Other implementation */
	public static final double round(double x) { return (double) ( (int) (x + 0.5) ); }
	public static final double floor(double x) { return (double) ( (int) x); }
	public static final double ceil(double x) { return (double) ( (int) (x + 1d) ); }
	public static final double sign(double x) { return x > 0 ? 1 : -1; }
	public static final double signum(double x) { return x == 0 ? 0 : x > 0 ? 1 : -1; }
	public static final double abs(double x) { return x < 0 ? -x : x; }
	public static final double sqr(double x) { return x * x; }
	public static final double cbr(double x) { return x * x * x; }
	public static final double qur(double x) { return x * x * x * x; }
	public static final double max(double a, double b) { return a > b ? a : b; }
	public static final double min(double a, double b) { return a < b ? a : b; }
	public static final double max(double a, double b, double c) { return max(c, max(a, b)); }
	public static final double min(double a, double b, double c) { return min(c, min(a, b)); }
	public static final double max(double a, double b, double c, double d) { return max(max(c, d), max(a, b)); }
	public static final double min(double a, double b, double c, double d) { return min(min(c, d), min(a, b)); }
	
	public static final double limit(double a, double b, double c) { return max(a, min(b, c)); }
	public static final double round(double value, double nearest) { return Math.round(value / (double) nearest) * (double) nearest; }
	public static final double getAngle(Point2D.Double source, Point2D.Double target) { return atan2(target.getY() - source.getX(), target.getY() - source.getY()); }
	
	public static final boolean inRenge(double a, double b, double c) { return a <= b && b <= c; }
	public static final Point2D project(Point2D point, double angle, double distance) { return new Point2D.Double(point.getX() + distance * sin(angle), point.getY() + distance * cos(angle)); }
	
	/* from robocode.util.Utils */
	public static final double NEAR_DELTA = .00001;
	public static final double normalAbsoluteAngle(double angle) { return (angle %= TWO_PI) >= 0 ? angle : (angle + TWO_PI); }
	public static final double normalRelativeAngle(double angle) { return (angle %= TWO_PI) >= 0 ? (angle < PI) ? angle : angle - TWO_PI : (angle >= -PI) ? angle : angle + TWO_PI; }
	public static final boolean isNear(double value1, double value2) { return (abs(value1 - value2) < NEAR_DELTA); }
}
